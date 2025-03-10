/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.service.impl;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.common.util.DateValidationUtil;
import com.nextuple.transit.domain.dto.TransitBufferDetailsDto;
import com.nextuple.transit.domain.inbound.TransitBufferDeletionRequest;
import com.nextuple.transit.domain.inbound.TransitBufferRequest;
import com.nextuple.transit.domain.inbound.TransitBufferV2UpdationRequest;
import com.nextuple.transit.domain.mapper.TransitBufferV2Mapper;
import com.nextuple.transit.domain.outbound.TransitBufferDetailsResponse;
import com.nextuple.transit.domain.outbound.TransitBufferV2Response;
import com.nextuple.transit.domain.pojo.TransitBufferValidationDto;
import com.nextuple.transit.domain.pojo.TransitDetailsValidationDto;
import com.nextuple.transit.persistence.domain.TransitBufferConfigRequestDomainDto;
import com.nextuple.transit.persistence.domain.TransitBufferV2DomainDto;
import com.nextuple.transit.persistence.domain.TransitDomainDto;
import com.nextuple.transit.persistence.exception.TransitDomainException;
import com.nextuple.transit.persistence.service.TransitBufferConfigRequestPersistenceService;
import com.nextuple.transit.persistence.service.TransitBufferV2PersistenceService;
import com.nextuple.transit.persistence.service.TransitPersistenceService;
import com.nextuple.transit.service.TransitBufferV2Service;
import com.nextuple.transit.utils.TransitUtils;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class TransitBufferV2ServiceImpl implements TransitBufferV2Service {

  private static final String TRANSIT_BUFFER_CONFIG_REQUEST_ID = "transitBufferConfigRequestId";

  private final DateValidationUtil dateValidationUtil;

  public static final TransitBufferV2Mapper INSTANCE =
      Mappers.getMapper(TransitBufferV2Mapper.class);
  private static final Logger logger = LoggerFactory.getLogger(TransitBufferV2ServiceImpl.class);

  private static final String TRANSIT_BUFFER_DETAILS_NOT_FOUND =
      "Transit buffer details not found with given details";
  private static final String INVALID_TRANSIT_BUFFER = "Invalid transit buffer";
  private static final String BUFFER_END_DATE_MUST_BE_IN_FUTURE =
      "Buffer end date must be in future";
  private static final String TRANSIT_BUFFER_NOT_FOUND =
      "Transit Buffer Not found for requested orgId and id";
  private static final String TRANSIT_BUFFER_WINDOW_ALREADY_EXISTS_OR_OVERLAPS =
      "Transit Buffer window already exists or overlaps";
  private static final String PIPE_SPLITTER = "|";
  private final TransitBufferV2PersistenceService transitBufferV2PersistenceService;
  private final TransitPersistenceService transitPersistenceService;
  private final TransitBufferConfigRequestPersistenceService
      transitBufferConfigRequestPersistenceService;
  private static final String ORG_ID = "orgId";
  private static final String ID = "id";
  private static final String BUFFER_START_DATE = "bufferStartDate";
  private static final String BUFFER_END_DATE = "bufferEndDate";
  private static final String CARRIER_SERVICE_ID = "carrierServiceId";
  private static final String SOURCE_GEOZONE = "sourceGeozone";
  private static final String DESTINATION_GEOZONE = "destinationGeozone";
  private static final String TRANSIT_NOT_FOUND = "Transit details not found";

  private static final String TRANSIT_BUFFER_NOT_FOUND_EXCEPTION =
      "Transit Buffer Not found for requested orgId, carrierServiceId, sourceGeozone, destinationGeozone, bufferStartDate and bufferEndDate";

  @Override
  public List<TransitBufferDetailsResponse>
      getTransitBuffersByOrgIdDestinationGeozoneRequestDateAndHorizonDays(
          String orgId, String destinationGeozone, LocalDate requestDate, Integer horizonDays)
          throws CommonServiceException {
    List<TransitBufferV2DomainDto> transitBufferEntities =
        transitBufferV2PersistenceService.fetchTransitBuffersByOrgIdAndDestinationGeozone(
            orgId, destinationGeozone, requestDate, requestDate.plusDays(horizonDays));
    validateEmptyTransitEntities(
        orgId, destinationGeozone, requestDate, horizonDays, transitBufferEntities);
    Map<String, List<TransitBufferDetailsDto>> transitBuffersMap =
        getTransitBuffersMap(transitBufferEntities);
    return (List<TransitBufferDetailsResponse>)
        transitBuffersMap.entrySet().stream()
            .map(
                entry -> {
                  String[] details = entry.getKey().split("\\|");
                  return TransitBufferDetailsResponse.builder()
                      .orgId(details[0])
                      .destinationGeozone(details[1])
                      .sourceGeozone(details[2])
                      .carrierServiceId(details[3])
                      .transitBuffers(entry.getValue())
                      .customAttributes(
                          JsonNodeFactory.instance
                              .objectNode()
                              .put("key1", "value1")
                              .put("key2", "value2"))
                      .build();
                })
            .toList();
  }

  @Transactional
  @Override
  public TransitBufferV2Response saveTransitBuffer(TransitBufferRequest transitBufferRequest)
      throws CommonServiceException, TransitDomainException {
    validateTransitDetails(transitBufferRequest);

    dateValidationUtil.validateBufferStartAndEndDate(
        transitBufferRequest.getBufferStartDate(), transitBufferRequest.getBufferEndDate());
    validateValidFutureDate(transitBufferRequest.getBufferEndDate());
    checkOverlapsWithExistingBuffers(
        TransitBufferValidationDto.builder()
            .orgId(transitBufferRequest.getOrgId())
            .destinationGeozone(transitBufferRequest.getDestinationGeozone())
            .sourceGeozone(transitBufferRequest.getSourceGeozone())
            .carrierServiceId(transitBufferRequest.getCarrierServiceId())
            .bufferStartDate(transitBufferRequest.getBufferStartDate())
            .bufferEndDate(transitBufferRequest.getBufferEndDate())
            .build(),
        null);
    var transitBufferV2Entity =
        transitBufferV2PersistenceService.saveTransitBuffer(
            INSTANCE.toTransitBufferV2DomainDto(transitBufferRequest));
    return INSTANCE.toTransitBufferV2Response(transitBufferV2Entity);
  }

  @Override
  public TransitBufferV2Response getTransitBufferByOrgIdAndId(String orgId, Long id)
      throws CommonServiceException {
    Optional<TransitBufferV2DomainDto> transitBufferEntity = getTransitBufferEntity(orgId, id);
    return INSTANCE.toTransitBufferV2Response(transitBufferEntity.get());
  }

  @Transactional
  @Override
  public TransitBufferV2Response updateTransitBufferByOrgIdAndId(
      String orgId, Long id, TransitBufferV2UpdationRequest transitBufferV2UpdationRequest)
      throws CommonServiceException, TransitDomainException {
    Optional<TransitBufferV2DomainDto> transitBufferEntity = getTransitBufferEntity(orgId, id);

    TransitBufferRequest updatedRequest =
        INSTANCE.toTransitBufferRequest(transitBufferEntity.get());
    updatedRequest.setBufferDays(transitBufferV2UpdationRequest.getBufferDays());

    validateTransitDetails(updatedRequest);
    dateValidationUtil.validateBufferStartAndEndDate(
        transitBufferV2UpdationRequest.getBufferStartDate(),
        transitBufferV2UpdationRequest.getBufferEndDate());
    validateValidFutureDate(transitBufferV2UpdationRequest.getBufferEndDate());
    if (Objects.nonNull(transitBufferV2UpdationRequest.getBufferStartDate())
        && Objects.nonNull(transitBufferV2UpdationRequest.getBufferEndDate())) {
      checkOverlapsWithExistingBuffers(
          TransitBufferValidationDto.builder()
              .orgId(transitBufferEntity.get().getOrgId())
              .destinationGeozone(transitBufferEntity.get().getDestinationGeozone())
              .sourceGeozone(transitBufferEntity.get().getSourceGeozone())
              .carrierServiceId(transitBufferEntity.get().getCarrierServiceId())
              .bufferStartDate(transitBufferV2UpdationRequest.getBufferStartDate())
              .bufferEndDate(transitBufferV2UpdationRequest.getBufferEndDate())
              .build(),
          id);
    }
    INSTANCE.updateTransitBufferV2DomainDtoFromUpdationRequest(
        transitBufferV2UpdationRequest, transitBufferEntity.get());
    return INSTANCE.toTransitBufferV2Response(
        transitBufferV2PersistenceService.updateTransitBuffer(transitBufferEntity.get()));
  }

  @Transactional
  @Override
  public TransitBufferV2Response updateTransitBuffer(TransitBufferRequest transitBufferRequest)
      throws CommonServiceException, TransitDomainException {
    validateTransitDetails(transitBufferRequest);
    Long parentBufferConfigRequestId =
        validateAndRetrieveBufferConfigRequestId(transitBufferRequest);
    dateValidationUtil.validateBufferStartAndEndDate(
        transitBufferRequest.getBufferStartDate(), transitBufferRequest.getBufferEndDate());
    validateValidFutureDate(transitBufferRequest.getBufferEndDate());
    List<TransitBufferV2DomainDto> transitBufferV2Entities =
        transitBufferV2PersistenceService
            .fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceId(
                transitBufferRequest.getOrgId(),
                transitBufferRequest.getDestinationGeozone(),
                transitBufferRequest.getSourceGeozone(),
                transitBufferRequest.getCarrierServiceId());
    Optional<TransitBufferV2DomainDto> entityToUpdate =
        transitBufferV2Entities.stream()
            .filter(
                e ->
                    Objects.equals(
                        e.getTransitBufferConfigRequestId(), parentBufferConfigRequestId))
            .findFirst();
    if (entityToUpdate.isEmpty()) {
      throwExceptionWhenInvalidConfigRequestId(transitBufferRequest);
    }
    checkOverlap(
        transitBufferV2Entities,
        entityToUpdate.get().getId(),
        transitBufferRequest.getBufferStartDate(),
        transitBufferRequest.getBufferEndDate());
    INSTANCE.updateTransitBufferV2DomainDtoFromRequest(transitBufferRequest, entityToUpdate.get());
    return INSTANCE.toTransitBufferV2Response(
        transitBufferV2PersistenceService.updateTransitBuffer(entityToUpdate.get()));
  }

  @Transactional
  @Override
  public TransitBufferV2Response deleteTransitBuffer(TransitBufferRequest transitBufferRequest)
      throws CommonServiceException {
    Long parentBufferConfigRequestId =
        validateAndRetrieveBufferConfigRequestId(transitBufferRequest);
    Optional<TransitBufferV2DomainDto> entityToDelete =
        transitBufferV2PersistenceService
            .fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceIdAndTransitBufferConfigRequestId(
                transitBufferRequest.getOrgId(),
                transitBufferRequest.getDestinationGeozone(),
                transitBufferRequest.getSourceGeozone(),
                transitBufferRequest.getCarrierServiceId(),
                parentBufferConfigRequestId);

    if (entityToDelete.isEmpty()) {
      throwExceptionWhenInvalidConfigRequestId(transitBufferRequest);
    }
    transitBufferV2PersistenceService.deleteTransitBufferEntityByIdAndOrgId(
        entityToDelete.get().getId(), entityToDelete.get().getOrgId());
    return INSTANCE.toTransitBufferV2Response(entityToDelete.get());
  }

  @Transactional
  @Override
  public TransitBufferV2Response deleteTransitBufferById(String orgId, Long id)
      throws CommonServiceException {
    Optional<TransitBufferV2DomainDto> transitBufferEntity =
        transitBufferV2PersistenceService.fetchTransitBufferByOrgIdAndId(orgId, id);
    if (transitBufferEntity.isEmpty()) {
      throwExceptionWhenBufferNotFound(orgId, id);
    }
    transitBufferV2PersistenceService.deleteTransitBufferEntityByIdAndOrgId(
        transitBufferEntity.get().getId(), transitBufferEntity.get().getOrgId());
    return INSTANCE.toTransitBufferV2Response(transitBufferEntity.get());
  }

  private void checkOverlapsWithExistingBuffers(
      TransitBufferValidationDto transitBufferValidationDto, Long existingBufferId)
      throws CommonServiceException {
    List<TransitBufferV2DomainDto> existingTransitBufferEntities =
        transitBufferV2PersistenceService
            .fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceId(
                transitBufferValidationDto.getOrgId(),
                transitBufferValidationDto.getDestinationGeozone(),
                transitBufferValidationDto.getSourceGeozone(),
                transitBufferValidationDto.getCarrierServiceId());

    checkOverlap(
        existingTransitBufferEntities,
        existingBufferId,
        transitBufferValidationDto.getBufferStartDate(),
        transitBufferValidationDto.getBufferEndDate());
  }

  private void checkOverlap(
      List<TransitBufferV2DomainDto> entities,
      Long existingEntityId,
      Date bufferStartDate,
      Date bufferEndDate)
      throws CommonServiceException {
    boolean hasOverlap =
        entities.stream()
            .filter(
                e ->
                    Objects.isNull(existingEntityId)
                        || !Objects.equals(existingEntityId, e.getId()))
            .anyMatch(entity -> isBufferWindowOverlap(entity, bufferStartDate, bufferEndDate));

    if (hasOverlap) {
      throwExceptionForOverlappingBuffers(bufferStartDate, bufferEndDate);
    }
  }

  private Long validateAndRetrieveBufferConfigRequestId(TransitBufferRequest transitBufferRequest)
      throws CommonServiceException {
    if (Objects.isNull(transitBufferRequest.getTransitBufferConfigRequestId())) {
      throwExceptionWhenInvalidConfigRequestId(transitBufferRequest);
    }

    Optional<TransitBufferConfigRequestDomainDto> transitBufferConfigRequestEntity =
        transitBufferConfigRequestPersistenceService.findById(
            transitBufferRequest.getTransitBufferConfigRequestId());
    if (transitBufferConfigRequestEntity.isEmpty()) {
      throwExceptionWhenInvalidConfigRequestId(transitBufferRequest);
    }

    Long parentBufferConfigRequestId = transitBufferConfigRequestEntity.get().getParentRequestId();
    if (Objects.isNull(parentBufferConfigRequestId)) {
      throwExceptionWhenInvalidConfigRequestId(transitBufferRequest);
    }
    return parentBufferConfigRequestId;
  }

  private Optional<TransitBufferV2DomainDto> getTransitBufferEntity(String orgId, Long id)
      throws CommonServiceException {
    Optional<TransitBufferV2DomainDto> transitBufferDomainDto =
        transitBufferV2PersistenceService.fetchTransitBufferByOrgIdAndId(orgId, id);
    if (transitBufferDomainDto.isEmpty()) {
      throwExceptionWhenBufferNotFound(orgId, id);
    }
    return transitBufferDomainDto;
  }

  private static void validateEmptyTransitEntities(
      String orgId,
      String destinationGeozone,
      LocalDate requestDate,
      Integer horizonDays,
      List<TransitBufferV2DomainDto> transitBufferEntities)
      throws CommonServiceException {
    if (CollectionUtils.isEmpty(transitBufferEntities)) {
      throwExceptionWhenBufferNotFound(orgId, destinationGeozone, requestDate, horizonDays);
    }
  }

  private Map<String, List<TransitBufferDetailsDto>> getTransitBuffersMap(
      List<TransitBufferV2DomainDto> transitBufferEntities) {
    Map<String, List<TransitBufferDetailsDto>> transitBuffersMap = new HashMap<>();
    for (TransitBufferV2DomainDto transitBufferV2DomainDto : transitBufferEntities) {
      String key = getBufferKey(transitBufferV2DomainDto);
      List<TransitBufferDetailsDto> transitBufferDetailsDtoList =
          transitBuffersMap.getOrDefault(key, new ArrayList<>());
      transitBufferDetailsDtoList.add(
          TransitBufferDetailsDto.builder()
              .bufferDays(transitBufferV2DomainDto.getBufferDays())
              .bufferStartDate(transitBufferV2DomainDto.getBufferStartDate())
              .bufferEndDate(transitBufferV2DomainDto.getBufferEndDate())
              .build());
      transitBuffersMap.put(key, transitBufferDetailsDtoList);
    }
    return transitBuffersMap;
  }

  private String getBufferKey(TransitBufferV2DomainDto transitBufferV2DomainDto) {
    return transitBufferV2DomainDto.getOrgId()
        + PIPE_SPLITTER
        + transitBufferV2DomainDto.getDestinationGeozone()
        + PIPE_SPLITTER
        + transitBufferV2DomainDto.getSourceGeozone()
        + PIPE_SPLITTER
        + transitBufferV2DomainDto.getCarrierServiceId();
  }

  public void validateValidFutureDate(Date endDate) throws CommonServiceException {
    DateTime today = new DateTime(new Date(), DateTimeZone.UTC);
    DateTime end = new DateTime(endDate, DateTimeZone.UTC);

    if (end.isBefore(today)) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(BUFFER_END_DATE, FieldError.builder().rejectedValue(endDate).build());
      throw new CommonServiceException(
          BUFFER_END_DATE_MUST_BE_IN_FUTURE, HttpStatus.BAD_REQUEST, 0x1772, errorMap);
    }
  }

  private boolean isBufferWindowOverlap(
      TransitBufferV2DomainDto existingBuffer, Date newBufferStartDate, Date newBufferEndDate) {

    DateTime newStart = new DateTime(newBufferStartDate, DateTimeZone.UTC);
    DateTime newEnd = new DateTime(newBufferEndDate, DateTimeZone.UTC);

    DateTime existingStart = new DateTime(existingBuffer.getBufferStartDate(), DateTimeZone.UTC);
    DateTime existingEnd = new DateTime(existingBuffer.getBufferEndDate(), DateTimeZone.UTC);

    boolean startOverlap = newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
    boolean endOverlap = newEnd.isAfter(existingStart) && newStart.isBefore(existingEnd);
    boolean containsNewBuffer = existingStart.isBefore(newStart) && existingEnd.isAfter(newEnd);
    boolean containedInNewBuffer = newStart.isBefore(existingStart) && newEnd.isAfter(existingEnd);

    boolean sameStartAsExistingEnd = newStart.isEqual(existingEnd);
    boolean sameEndAsExistingStart = newEnd.isEqual(existingStart);
    boolean sameWindow = newStart.isEqual(existingStart) && newEnd.isEqual(existingEnd);

    return startOverlap
        || endOverlap
        || containsNewBuffer
        || containedInNewBuffer
        || sameStartAsExistingEnd
        || sameEndAsExistingStart
        || sameWindow;
  }

  private void validateTransitDetails(TransitBufferRequest transitBufferRequest)
      throws CommonServiceException, TransitDomainException {
    var transitDays =
        getTransitDaysFromExistingTransitEntity(
            transitBufferRequest.getOrgId(),
            transitBufferRequest.getSourceGeozone(),
            transitBufferRequest.getDestinationGeozone(),
            transitBufferRequest.getCarrierServiceId());
    TransitUtils.validateTransitDetails(
        TransitDetailsValidationDto.builder()
            .transitDays(transitDays)
            .bufferDays(transitBufferRequest.getBufferDays())
            .orgId(transitBufferRequest.getOrgId())
            .sourceGeozone(transitBufferRequest.getSourceGeozone())
            .destinationGeozone(transitBufferRequest.getDestinationGeozone())
            .carrierServiceId(transitBufferRequest.getCarrierServiceId())
            .build());
  }

  private Float getTransitDaysFromExistingTransitEntity(
      String orgId, String sourceGeozone, String destinationGeozone, String carrierServiceId)
      throws CommonServiceException, TransitDomainException {
    Float transitDays;

    Optional<TransitDomainDto> existingTransitEntity =
        transitPersistenceService.findTransitDetails(
            orgId, sourceGeozone, destinationGeozone, carrierServiceId);

    if (existingTransitEntity.isPresent()) {
      transitDays = existingTransitEntity.get().getTransitDays();
    } else {
      Map<String, FieldError> errorMap = new HashMap<>();

      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          CARRIER_SERVICE_ID, FieldError.builder().rejectedValue(carrierServiceId).build());
      errorMap.put(SOURCE_GEOZONE, FieldError.builder().rejectedValue(sourceGeozone).build());
      errorMap.put(
          DESTINATION_GEOZONE, FieldError.builder().rejectedValue(destinationGeozone).build());

      throw new CommonServiceException(TRANSIT_NOT_FOUND, HttpStatus.BAD_REQUEST, 0x1773, errorMap);
    }
    return transitDays;
  }

  private static void throwExceptionWhenInvalidConfigRequestId(
      TransitBufferRequest transitBufferRequest) throws CommonServiceException {
    logger.error(INVALID_TRANSIT_BUFFER);
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(
        TRANSIT_BUFFER_CONFIG_REQUEST_ID,
        FieldError.builder()
            .rejectedValue(transitBufferRequest.getTransitBufferConfigRequestId())
            .build());
    throw new CommonServiceException(
        INVALID_TRANSIT_BUFFER, HttpStatus.NOT_FOUND, 0x1775, errorMap);
  }

  private static void throwExceptionForOverlappingBuffers(Date bufferStartDate, Date bufferEndDate)
      throws CommonServiceException {
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(BUFFER_START_DATE, FieldError.builder().rejectedValue(bufferStartDate).build());
    errorMap.put(BUFFER_END_DATE, FieldError.builder().rejectedValue(bufferEndDate).build());
    throw new CommonServiceException(
        TRANSIT_BUFFER_WINDOW_ALREADY_EXISTS_OR_OVERLAPS,
        HttpStatus.PRECONDITION_FAILED,
        0x1774,
        errorMap);
  }

  private static void throwExceptionWhenBufferNotFound(String orgId, Long id)
      throws CommonServiceException {
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
    errorMap.put(ID, FieldError.builder().rejectedValue(id).build());
    throw new CommonServiceException(
        TRANSIT_BUFFER_NOT_FOUND, HttpStatus.NOT_FOUND, 0x1776, errorMap);
  }

  private static void throwExceptionWhenBufferNotFound(
      String orgId, String destinationGeozone, LocalDate requestDate, Integer horizonDays)
      throws CommonServiceException {
    logger.error(TRANSIT_BUFFER_DETAILS_NOT_FOUND);
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
    errorMap.put(
        DESTINATION_GEOZONE, FieldError.builder().rejectedValue(destinationGeozone).build());
    errorMap.put("requestDate", FieldError.builder().rejectedValue(requestDate).build());
    errorMap.put("horizonDays", FieldError.builder().rejectedValue(horizonDays).build());
    throw new CommonServiceException(
        TRANSIT_BUFFER_DETAILS_NOT_FOUND, HttpStatus.NOT_FOUND, 0x1771, errorMap);
  }

  @Transactional
  @Override
  public TransitBufferV2Response deleteTransitBufferRecord(
      TransitBufferDeletionRequest transitBufferDeletionRequest) throws CommonServiceException {
    Optional<TransitBufferV2DomainDto> transitBufferEntity =
        transitBufferV2PersistenceService
            .fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceIdAndBufferStartDateAndBufferEndDate(
                transitBufferDeletionRequest.getOrgId(),
                transitBufferDeletionRequest.getDestinationGeozone(),
                transitBufferDeletionRequest.getSourceGeozone(),
                transitBufferDeletionRequest.getCarrierServiceId(),
                transitBufferDeletionRequest.getBufferStartDate(),
                transitBufferDeletionRequest.getBufferEndDate());
    if (transitBufferEntity.isEmpty()) {
      throwExceptionWhenTransitBufferNotFound(transitBufferDeletionRequest);
    }
    transitBufferV2PersistenceService.deleteTransitBuffer(transitBufferEntity.get());
    return INSTANCE.toTransitBufferV2Response(transitBufferEntity.get());
  }

  private static void throwExceptionWhenTransitBufferNotFound(
      TransitBufferDeletionRequest transitBufferDeletionRequest) throws CommonServiceException {
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(
        transitBufferDeletionRequest.getOrgId(),
        FieldError.builder().rejectedValue(transitBufferDeletionRequest.getOrgId()).build());
    errorMap.put(
        transitBufferDeletionRequest.getDestinationGeozone(),
        FieldError.builder()
            .rejectedValue(transitBufferDeletionRequest.getDestinationGeozone())
            .build());
    errorMap.put(
        transitBufferDeletionRequest.getSourceGeozone(),
        FieldError.builder()
            .rejectedValue(transitBufferDeletionRequest.getSourceGeozone())
            .build());
    errorMap.put(
        transitBufferDeletionRequest.getCarrierServiceId(),
        FieldError.builder()
            .rejectedValue(transitBufferDeletionRequest.getCarrierServiceId())
            .build());
    errorMap.put(
        String.valueOf(transitBufferDeletionRequest.getBufferStartDate()),
        FieldError.builder()
            .rejectedValue(transitBufferDeletionRequest.getBufferStartDate())
            .build());
    errorMap.put(
        String.valueOf(transitBufferDeletionRequest.getBufferEndDate()),
        FieldError.builder()
            .rejectedValue(transitBufferDeletionRequest.getBufferEndDate())
            .build());
    throw new CommonServiceException(
        TRANSIT_BUFFER_NOT_FOUND_EXCEPTION, HttpStatus.NOT_FOUND, 0x1776, errorMap);
  }
}
