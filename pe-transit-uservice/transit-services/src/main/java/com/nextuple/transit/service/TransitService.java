/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.service;

import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.common.util.DateValidationUtil;
import com.nextuple.postal.code.timezone.api.domain.feign.PostalCodeFeign;
import com.nextuple.postal.code.timezone.api.domain.outbound.PostalCodeResponse;
import com.nextuple.postgres.config.ReaderDS;
import com.nextuple.transit.domain.dto.TransitTimeEntriesDto;
import com.nextuple.transit.domain.inbound.DistinctGeozonesResponse;
import com.nextuple.transit.domain.inbound.TransitBufferCreationRequest;
import com.nextuple.transit.domain.inbound.TransitDataCreationRequest;
import com.nextuple.transit.domain.inbound.TransitDataUpdationRequest;
import com.nextuple.transit.domain.mapper.TransitMapper;
import com.nextuple.transit.domain.outbound.TransitResponse;
import com.nextuple.transit.domain.pojo.TransitDetailsValidationDto;
import com.nextuple.transit.persistence.domain.TransitBufferDomainDto;
import com.nextuple.transit.persistence.domain.TransitDomainDto;
import com.nextuple.transit.persistence.exception.TransitDomainException;
import com.nextuple.transit.persistence.service.TransitBufferPersistenceService;
import com.nextuple.transit.persistence.service.impl.TransitPersistenceServiceImpl;
import com.nextuple.transit.utils.TransitUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
@Service
@Slf4j
public class TransitService {

  private static final Logger logger = LoggerFactory.getLogger(TransitService.class);
  private static final String SOURCE_GEOZONE = "sourceGeozone";
  private static final String DESTINATION_GEOZONE = "destinationGeozone";
  private static final String ORG_ID = "orgId";
  private static final String CARRIER_SERVICE_ID = "carrierServiceId";

  private final TransitPersistenceServiceImpl transitPersistenceService;

  private final CarrierFeign carrierFeign;

  public static final TransitMapper INSTANCE = Mappers.getMapper(TransitMapper.class);

  private static final String TRANSIT_EXCEPTION_MESSAGE =
      "Transit data not found with given details";

  private static final String INVALID_TRANSIT_DATA_EXCEPTION_MESSAGE =
      "Transit data cannot be created with given carrierServiceId and orgId";

  private final DateValidationUtil dateValidationUtil;

  private static final String INVALID_GEOZONE = "geoZone is not valid";

  @Autowired private final PostalCodeFeign postalCodeFeign;
  private final TransitBufferPersistenceService transitBufferPersistenceService;

  @Qualifier("threadPoolTaskExecutor")
  @Autowired
  ThreadPoolTaskExecutor threadPoolTaskExecutor;

  public TransitResponse addTransitInfo(TransitDataCreationRequest transitDataCreationRequest)
      throws TransitDomainException, CommonServiceException {
    dateValidationUtil.validateBufferStartAndEndDate(
        transitDataCreationRequest.getBufferStartDate(),
        transitDataCreationRequest.getBufferEndDate());
    Optional<TransitBufferDomainDto> existingTransitBufferDomainDto =
        transitBufferPersistenceService
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                transitDataCreationRequest.getOrgId(),
                transitDataCreationRequest.getCarrierServiceId(),
                transitDataCreationRequest.getSourceGeozone(),
                transitDataCreationRequest.getDestinationGeozone());
    TransitUtils.validateTransitDetails(
        TransitDetailsValidationDto.builder()
            .transitDays(transitDataCreationRequest.getTransitDays())
            .bufferDays(
                existingTransitBufferDomainDto.isPresent()
                    ? existingTransitBufferDomainDto.get().getBufferDays()
                    : transitDataCreationRequest.getBufferDays())
            .orgId(transitDataCreationRequest.getOrgId())
            .sourceGeozone(transitDataCreationRequest.getSourceGeozone())
            .destinationGeozone(transitDataCreationRequest.getDestinationGeozone())
            .carrierServiceId(transitDataCreationRequest.getCarrierServiceId())
            .build());
    if (Boolean.FALSE.equals(validateCarrierDetails(transitDataCreationRequest))) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          ORG_ID,
          FieldError.builder().rejectedValue(transitDataCreationRequest.getOrgId()).build());
      errorMap.put(
          CARRIER_SERVICE_ID,
          FieldError.builder()
              .rejectedValue(transitDataCreationRequest.getCarrierServiceId())
              .build());
      throw new CommonServiceException(
          INVALID_TRANSIT_DATA_EXCEPTION_MESSAGE, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
    validateSourceAndDestinationGeozone(
        transitDataCreationRequest.getOrgId(), transitDataCreationRequest.getSourceGeozone());
    validateSourceAndDestinationGeozone(
        transitDataCreationRequest.getOrgId(), transitDataCreationRequest.getDestinationGeozone());

    return INSTANCE.toTransitResponse(
        transitPersistenceService.saveTransitDomainDto(
            INSTANCE.toTransitDomainDtoFromRequest(transitDataCreationRequest)));
  }

  private void validateSourceAndDestinationGeozone(String orgId, String geozone)
      throws CommonServiceException {
    try {
      BaseResponse<List<PostalCodeResponse>> postalCodeResponseList =
          postalCodeFeign.getByPostalCodePrefix(orgId, geozone);
      if (CollectionUtils.isEmpty(postalCodeResponseList.getPayload())) {
        commonServiceExceptionInvalidGeoZone(INVALID_GEOZONE, orgId, geozone);
      }
    } catch (Exception e) {
      logger.error(
          "Error while fetching postal code timezone details for orgId:{} , geoZone:{}",
          orgId,
          geozone);
      commonServiceExceptionInvalidGeoZone(INVALID_GEOZONE, orgId, geozone);
    }
  }

  public void commonServiceExceptionInvalidGeoZone(
      String errorMessage, String orgId, String geoZone) throws CommonServiceException {
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
    errorMap.put("geoZone", FieldError.builder().rejectedValue(geoZone).build());
    throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
  }

  private Boolean validateCarrierDetails(TransitDataCreationRequest transitDataCreationRequest) {
    try {
      var carrierServiceResponse =
          carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(
              transitDataCreationRequest.getCarrierServiceId(),
              transitDataCreationRequest.getOrgId());
      if (Objects.nonNull(carrierServiceResponse)) {
        var payload = carrierServiceResponse.getPayload();
        if (Objects.nonNull(payload) && Boolean.FALSE.equals(payload.isEmpty())) {
          return true;
        }
      }
      return false;
    } catch (Exception e) {
      logger.error(
          "Error while fetching carrier details for orgId:{} , carrierServiceId:{}",
          transitDataCreationRequest.getOrgId(),
          transitDataCreationRequest.getCarrierServiceId());
      return false;
    }
  }

  public TransitResponse updateTransitBufferDetails(
      TransitBufferCreationRequest transitBufferCreationRequest)
      throws TransitDomainException, CommonServiceException {
    dateValidationUtil.validateBufferStartAndEndDate(
        transitBufferCreationRequest.getBufferStartDate(),
        transitBufferCreationRequest.getBufferEndDate());
    Optional<TransitDomainDto> existingTransitEntity =
        getTransitEntity(
            transitBufferCreationRequest.getOrgId(),
            transitBufferCreationRequest.getCarrierServiceId(),
            transitBufferCreationRequest.getSourceGeozone(),
            transitBufferCreationRequest.getDestinationGeozone());
    if (existingTransitEntity.isPresent()) {
      var transitDays = existingTransitEntity.get().getTransitDays();
      var bufferDays = transitBufferCreationRequest.getBufferDays();
      TransitUtils.validateTransitDetails(
          TransitDetailsValidationDto.builder()
              .transitDays(transitDays)
              .bufferDays(bufferDays)
              .orgId(transitBufferCreationRequest.getOrgId())
              .sourceGeozone(transitBufferCreationRequest.getSourceGeozone())
              .destinationGeozone(transitBufferCreationRequest.getDestinationGeozone())
              .carrierServiceId(transitBufferCreationRequest.getCarrierServiceId())
              .build());
      logger.info(
          "Response before updation of transit data :{}",
          INSTANCE.toTransitResponse(existingTransitEntity.get()));
      existingTransitEntity.get().setBufferDays(bufferDays);
      existingTransitEntity
          .get()
          .setBufferStartDate(transitBufferCreationRequest.getBufferStartDate());
      existingTransitEntity.get().setBufferEndDate(transitBufferCreationRequest.getBufferEndDate());
      return INSTANCE.toTransitResponse(
          transitPersistenceService.saveTransitDomainDto(existingTransitEntity.get()));
    } else {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          ORG_ID,
          FieldError.builder().rejectedValue(transitBufferCreationRequest.getOrgId()).build());
      errorMap.put(
          SOURCE_GEOZONE,
          FieldError.builder()
              .rejectedValue(transitBufferCreationRequest.getSourceGeozone())
              .build());
      errorMap.put(
          DESTINATION_GEOZONE,
          FieldError.builder()
              .rejectedValue(transitBufferCreationRequest.getDestinationGeozone())
              .build());
      errorMap.put(
          CARRIER_SERVICE_ID,
          FieldError.builder()
              .rejectedValue(transitBufferCreationRequest.getCarrierServiceId())
              .build());
      throw new CommonServiceException(
          TRANSIT_EXCEPTION_MESSAGE, HttpStatus.BAD_REQUEST, 0x1771, null);
    }
  }

  public TransitResponse updateTransitDetails(
      String orgId,
      String sourceGeozone,
      String destinationGeozone,
      String carrierServiceId,
      TransitDataUpdationRequest transitDataUpdationRequest)
      throws TransitDomainException, CommonServiceException {

    Optional<TransitDomainDto> existingTransitDomainDto =
        getTransitEntity(orgId, carrierServiceId, sourceGeozone, destinationGeozone);

    if (existingTransitDomainDto.isEmpty()) {
      logger.error(TRANSIT_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(SOURCE_GEOZONE, FieldError.builder().rejectedValue(sourceGeozone).build());
      errorMap.put(
          DESTINATION_GEOZONE, FieldError.builder().rejectedValue(destinationGeozone).build());
      errorMap.put(
          CARRIER_SERVICE_ID, FieldError.builder().rejectedValue(carrierServiceId).build());
      throw new CommonServiceException(
          TRANSIT_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    logger.info(
        "Response before updation of transit data :{}",
        INSTANCE.toTransitResponse(existingTransitDomainDto.get()));
    INSTANCE.updateTransitDomainDto(transitDataUpdationRequest, existingTransitDomainDto.get());
    return INSTANCE.toTransitResponse(
        transitPersistenceService.saveTransitDomainDto(existingTransitDomainDto.get()));
  }

  @ReaderDS
  public TransitResponse getTransitDetails(
      String orgId,
      String sourceGeozone,
      String destinationGeozone,
      String carrierServiceId,
      String serviceOption)
      throws TransitDomainException, CommonServiceException {

    String allServiceOption = "ALL-" + serviceOption;

    List<TransitDomainDto> transitEntities =
        transitPersistenceService.filterAndGetTransitDetails(
            orgId, sourceGeozone, destinationGeozone, carrierServiceId, allServiceOption);

    Optional<TransitDomainDto> transitEntity = Optional.empty();

    if (!"ALL".equals(carrierServiceId)) {
      transitEntity =
          transitEntities.stream()
              .filter(x -> carrierServiceId.equals(x.getCarrierServiceId()))
              .findFirst();
    }

    if (transitEntity.isEmpty()) {
      transitEntity =
          transitEntities.stream()
              .filter(x -> allServiceOption.equals(x.getCarrierServiceId()))
              .findAny();
    }
    if (transitEntity.isEmpty()) {
      transitEntity =
          transitEntities.stream().filter(x -> "ALL".equals(x.getCarrierServiceId())).findAny();
    }

    if (transitEntity.isEmpty()) {
      logger.error(TRANSIT_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(SOURCE_GEOZONE, FieldError.builder().rejectedValue(sourceGeozone).build());
      errorMap.put(
          DESTINATION_GEOZONE, FieldError.builder().rejectedValue(destinationGeozone).build());
      errorMap.put(
          CARRIER_SERVICE_ID, FieldError.builder().rejectedValue(carrierServiceId).build());
      throw new CommonServiceException(
          TRANSIT_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }

    return INSTANCE.toTransitResponse(transitEntity.get());
  }

  public TransitResponse deleteTransitDetails(
      String orgId, String sourceGeozone, String destinationGeozone, String carrierServiceId)
      throws CommonServiceException,
          com.nextuple.transit.persistence.exception.TransitDomainException {
    Optional<TransitDomainDto> transitDomainDto =
        getTransitEntity(orgId, carrierServiceId, sourceGeozone, destinationGeozone);

    if (transitDomainDto.isEmpty()) {
      logger.error(TRANSIT_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(SOURCE_GEOZONE, FieldError.builder().rejectedValue(sourceGeozone).build());
      errorMap.put(
          DESTINATION_GEOZONE, FieldError.builder().rejectedValue(destinationGeozone).build());
      errorMap.put(
          CARRIER_SERVICE_ID, FieldError.builder().rejectedValue(carrierServiceId).build());
      throw new CommonServiceException(
          TRANSIT_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    logger.info("Response before deletion of transit data :{}", transitDomainDto.get());
    var transitResponse = INSTANCE.toTransitResponse(transitDomainDto.get());
    transitPersistenceService.deleteTransitDetails(transitDomainDto.get());
    return transitResponse;
  }

  @ReaderDS
  public List<TransitResponse> getListOfTransitDetails(
      String orgId, String destinationGeozone, List<String> sourceGeozones)
      throws TransitDomainException {

    List<TransitDomainDto> transitEntities =
        transitPersistenceService.fetchTransitList(orgId, destinationGeozone, sourceGeozones);

    return INSTANCE.toTransitResponseList(transitEntities);
  }

  @ReaderDS
  public List<String> getDistinctDFSA(
      String orgId, String sourceGeozone, List<String> carrierServiceIds)
      throws TransitDomainException {
    return transitPersistenceService.fetchDestinationGeozones(
        orgId, sourceGeozone, carrierServiceIds);
  }

  @ReaderDS
  public TransitTimeEntriesDto getTransitTimeEntries(String orgId, String carrierServiceId)
      throws TransitDomainException {
    return TransitTimeEntriesDto.builder()
        .orgId(orgId)
        .carrierServiceId(carrierServiceId)
        .totalRecords(transitPersistenceService.fetchTransitEntitiesCount(orgId, carrierServiceId))
        .build();
  }

  @ReaderDS
  public List<TransitResponse> getListOfTransitDetailsForDestinationGeoZone(
      String orgId, String destinationGeozone) throws CommonServiceException {
    List<TransitDomainDto> transitDomainDtos = new ArrayList<>();
    List<TransitBufferDomainDto> transitBufferDomainDtos = new ArrayList<>();
    try {
      List<Callable<Object>> tasks = getTransitCallables(orgId, destinationGeozone);
      List<Future<Object>> results =
          threadPoolTaskExecutor.getThreadPoolExecutor().invokeAll(tasks);
      transitDomainDtos = (List<TransitDomainDto>) results.get(0).get();
      transitBufferDomainDtos = (List<TransitBufferDomainDto>) results.get(1).get();
    } catch (ExecutionException | InterruptedException e) {
      Thread.currentThread().interrupt();
      logger.error(
          String.valueOf(e),
          "Unable to fetch transit list for orgId: {} and destination geozone: {}",
          orgId,
          destinationGeozone);
    }
    if (transitDomainDtos.isEmpty()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          DESTINATION_GEOZONE, FieldError.builder().rejectedValue(destinationGeozone).build());
      throw new CommonServiceException(
          TRANSIT_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    } else {
      updateBufferDetailsToTransitEntities(transitDomainDtos, transitBufferDomainDtos);
    }
    return INSTANCE.toTransitResponseList(transitDomainDtos);
  }

  public List<TransitResponse> getTransitDetailsForDestinationGeozones(
      String orgId, String carrierServiceId, List<String> destinationGeozones)
      throws TransitDomainException {
    return INSTANCE.convertToTransitResponseList(
        transitPersistenceService.fetchTransitListForDestinationGeoZones(
            orgId, carrierServiceId, destinationGeozones));
  }

  public DistinctGeozonesResponse getDistinctSourceAndDestinationGeoZones(
      String orgId, String carrierServiceId) throws TransitDomainException {
    List<String> sourceGeoZones =
        transitPersistenceService.fetchDistinctSourceGeoZones(orgId, carrierServiceId);
    List<String> destinationGeoZones =
        transitPersistenceService.fetchDistinctDestinationGeoZones(orgId, carrierServiceId);
    var response = new DistinctGeozonesResponse();
    response.setSourceGeozones(sourceGeoZones);
    response.setDestinationGeozones(destinationGeoZones);

    return response;
  }

  public TransitResponse updateTransitBufferDays(
      String orgId, String carrierServiceId, String sourceGeozone, String destinationGeozone)
      throws TransitDomainException {
    Optional<TransitDomainDto> transitEntity =
        getTransitEntity(orgId, carrierServiceId, sourceGeozone, destinationGeozone);
    if (transitEntity.isPresent()) {
      var tempEntity = transitEntity.get();

      if ((tempEntity.getBufferDays() != null && tempEntity.getBufferDays() != 0)) {
        tempEntity.setBufferDays(0D);
        tempEntity = transitPersistenceService.saveTransitDomainDto(tempEntity);
      }

      return INSTANCE.toTransitResponse(tempEntity);
    }
    return null;
  }

  public List<TransitResponse> getTransitDetailsForDestinationGeoZone(
      String orgId, String destinationGeozone)
      throws CommonServiceException, TransitDomainException {
    List<TransitDomainDto> transitDomainDtos =
        transitPersistenceService.fetchTransitListForDestinationGeoZone(orgId, destinationGeozone);
    if (CollectionUtils.isEmpty(transitDomainDtos)) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          DESTINATION_GEOZONE, FieldError.builder().rejectedValue(destinationGeozone).build());
      throw new CommonServiceException(
          TRANSIT_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return INSTANCE.toTransitResponseList(transitDomainDtos);
  }

  private Optional<TransitDomainDto> getTransitEntity(
      String orgId, String carrierServiceId, String sourceGeozone, String destinationGeozone)
      throws TransitDomainException {
    return transitPersistenceService.findTransitDetails(
        orgId, sourceGeozone, destinationGeozone, carrierServiceId);
  }

  private void updateBufferDetailsToTransitEntities(
      List<TransitDomainDto> transitDomainDtos,
      List<TransitBufferDomainDto> transitBufferDomainDtos) {
    Map<String, TransitBufferDomainDto> transitBufferDetailMap = new HashMap<>();
    if (!transitBufferDomainDtos.isEmpty()) {
      for (TransitBufferDomainDto transitBufferDomainDto : transitBufferDomainDtos) {
        String transitBufferKey =
            getTransitKey(
                transitBufferDomainDto.getOrgId(),
                transitBufferDomainDto.getSourceGeozone(),
                transitBufferDomainDto.getDestinationGeozone(),
                transitBufferDomainDto.getCarrierServiceId());
        transitBufferDetailMap.put(transitBufferKey, transitBufferDomainDto);
      }
      for (TransitDomainDto transitEntity : transitDomainDtos) {
        String transitKey =
            getTransitKey(
                transitEntity.getOrgId(),
                transitEntity.getSourceGeozone(),
                transitEntity.getDestinationGeozone(),
                transitEntity.getCarrierServiceId());
        if (!transitBufferDetailMap.isEmpty() && transitBufferDetailMap.containsKey(transitKey)) {
          var transitBufferEntity = transitBufferDetailMap.get(transitKey);
          transitEntity.setBufferDays(transitBufferEntity.getBufferDays());
          transitEntity.setBufferStartDate(transitBufferEntity.getBufferStartDate());
          transitEntity.setBufferEndDate(transitBufferEntity.getBufferEndDate());
        }
      }
    }
  }

  public static String getTransitKey(
      String orgId, String sourceGeoZone, String destinationGeoZone, String carrierServiceId) {
    var sBuilder = new StringBuilder();
    return sBuilder
        .append(orgId)
        .append("-")
        .append(sourceGeoZone)
        .append("-")
        .append(destinationGeoZone)
        .append("-")
        .append(carrierServiceId)
        .toString();
  }

  private List<Callable<Object>> getTransitCallables(String orgId, String destinationGeozone) {
    List<Callable<Object>> tasks = new ArrayList<>();
    Callable<Object> transitCallable =
        () ->
            transitPersistenceService.fetchTransitListForDestinationGeoZone(
                orgId, destinationGeozone);
    Callable<Object> transitBufferCallable =
        () ->
            transitBufferPersistenceService.findByOrgIdAndDestinationGeozone(
                orgId, destinationGeozone);
    tasks.add(transitCallable);
    tasks.add(transitBufferCallable);
    return tasks;
  }
}
