package com.nextuple.transit.service;

import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.common.util.DateValidationUtil;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.api.domain.feign.PostalCodeTimezoneFeign;
import com.nextuple.postgres.config.ReaderDS;
import com.nextuple.transit.domain.TransitDomain;
import com.nextuple.transit.domain.dto.TransitTimeEntriesDto;
import com.nextuple.transit.domain.entity.TransitBufferEntity;
import com.nextuple.transit.domain.entity.TransitEntity;
import com.nextuple.transit.domain.inbound.DistinctGeozonesResponse;
import com.nextuple.transit.domain.inbound.TransitBufferCreationRequest;
import com.nextuple.transit.domain.inbound.TransitDataCreationRequest;
import com.nextuple.transit.domain.inbound.TransitDataUpdationRequest;
import com.nextuple.transit.domain.mapper.TransitMapper;
import com.nextuple.transit.domain.outbound.TransitResponse;
import com.nextuple.transit.exception.TransitDomainException;
import com.nextuple.transit.repository.TransitBufferRepository;
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

@RequiredArgsConstructor
@Service
@Slf4j
public class TransitService {

  private static final Logger logger = LoggerFactory.getLogger(TransitService.class);
  private static final String SOURCE_GEOZONE = "sourceGeozone";
  private static final String DESTINATION_GEOZONE = "destinationGeozone";
  private static final String ORG_ID = "orgId";
  private static final String CARRIER_SERVICE_ID = "carrierServiceId";

  private final TransitDomain transitDomain;

  private final CarrierFeign carrierFeign;

  public static final TransitMapper INSTANCE = Mappers.getMapper(TransitMapper.class);

  private static final String TRANSIT_EXCEPTION_MESSAGE =
      "Transit data not found with given details";

  private static final String INVALID_TRANSIT_DATA_EXCEPTION_MESSAGE =
      "Transit data cannot be created with given carrierServiceId and orgId";

  private final DateValidationUtil dateValidationUtil;

  private static final String INVALID_GEOZONE = "geoZone is not valid";

  @Autowired private final PostalCodeTimezoneFeign postalCodeTimezoneFeign;
  private final TransitBufferRepository transitBufferRepository;

  @Qualifier("threadPoolTaskExecutor")
  @Autowired
  ThreadPoolTaskExecutor threadPoolTaskExecutor;

  public TransitResponse addTransitInfo(TransitDataCreationRequest transitDataCreationRequest)
      throws TransitDomainException, CommonServiceException {
    Optional<TransitBufferEntity> existingTransitBufferEntity =
        transitBufferRepository.findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
            transitDataCreationRequest.getOrgId(),
            transitDataCreationRequest.getCarrierServiceId(),
            transitDataCreationRequest.getSourceGeozone(),
            transitDataCreationRequest.getDestinationGeozone());
    TransitUtils.validateTransitDetails(
        transitDataCreationRequest.getTransitDays(),
        existingTransitBufferEntity.isPresent()
            ? existingTransitBufferEntity.get().getBufferDays()
            : transitDataCreationRequest.getBufferDays(),
        transitDataCreationRequest.getOrgId(),
        transitDataCreationRequest.getSourceGeozone(),
        transitDataCreationRequest.getDestinationGeozone(),
        transitDataCreationRequest.getCarrierServiceId());
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
    var transitEntity = INSTANCE.toTransitEntity(transitDataCreationRequest);

    return INSTANCE.toTransitResponse(transitDomain.saveTransitEntity(transitEntity));
  }

  private void validateSourceAndDestinationGeozone(String orgId, String geozone)
      throws CommonServiceException {
    try {
      BaseResponse<PostalCodeTimezoneDto> postalCodeTimezoneDtoBaseResponse =
          postalCodeTimezoneFeign.getPostalCodeTimezone(orgId, geozone);
      if (Objects.isNull(postalCodeTimezoneDtoBaseResponse.getPayload())) {
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
    Optional<TransitEntity> existingTransitEntity =
        getTransitEntity(
            transitBufferCreationRequest.getOrgId(),
            transitBufferCreationRequest.getCarrierServiceId(),
            transitBufferCreationRequest.getSourceGeozone(),
            transitBufferCreationRequest.getDestinationGeozone());
    if (existingTransitEntity.isPresent()) {
      var transitDays = existingTransitEntity.get().getTransitDays();
      var bufferDays = transitBufferCreationRequest.getBufferDays();
      TransitUtils.validateTransitDetails(
          transitDays,
          bufferDays,
          transitBufferCreationRequest.getOrgId(),
          transitBufferCreationRequest.getSourceGeozone(),
          transitBufferCreationRequest.getDestinationGeozone(),
          transitBufferCreationRequest.getCarrierServiceId());
      logger.info(
          "Response before updation of transit data :{}",
          INSTANCE.toTransitResponse(existingTransitEntity.get()));
      existingTransitEntity.get().setBufferDays(bufferDays);
      existingTransitEntity
          .get()
          .setBufferStartDate(transitBufferCreationRequest.getBufferStartDate());
      existingTransitEntity.get().setBufferEndDate(transitBufferCreationRequest.getBufferEndDate());
      return INSTANCE.toTransitResponse(
          transitDomain.saveTransitEntity(existingTransitEntity.get()));
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
          TRANSIT_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, null);
    }
  }

  public TransitResponse updateTransitDetails(
      String orgId,
      String sourceGeozone,
      String destinationGeozone,
      String carrierServiceId,
      TransitDataUpdationRequest transitDataUpdationRequest)
      throws TransitDomainException, CommonServiceException {

    Optional<TransitEntity> existingTransitEntity =
        getTransitEntity(orgId, carrierServiceId, sourceGeozone, destinationGeozone);

    if (existingTransitEntity.isEmpty()) {
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
        INSTANCE.toTransitResponse(existingTransitEntity.get()));
    INSTANCE.updateTransitEntity(transitDataUpdationRequest, existingTransitEntity.get());
    return INSTANCE.toTransitResponse(transitDomain.saveTransitEntity(existingTransitEntity.get()));
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

    List<TransitEntity> transitEntities =
        transitDomain.filterAndGetTransitDetails(
            orgId, sourceGeozone, destinationGeozone, carrierServiceId, allServiceOption);

    Optional<TransitEntity> transitEntity = Optional.empty();

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
      throws TransitDomainException, CommonServiceException {
    Optional<TransitEntity> transitEntity =
        getTransitEntity(orgId, carrierServiceId, sourceGeozone, destinationGeozone);

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
    logger.info("Response before deletion of transit data :{}", transitEntity.get());
    var transitResponse = INSTANCE.toTransitResponse(transitEntity.get());
    transitDomain.deleteTransitDetails(transitEntity.get());
    return transitResponse;
  }

  @ReaderDS
  public List<TransitResponse> getListOfTransitDetails(
      String orgId, String destinationGeozone, List<String> sourceGeozones)
      throws TransitDomainException {

    List<TransitEntity> transitEntities =
        transitDomain.fetchTransitList(orgId, destinationGeozone, sourceGeozones);

    return INSTANCE.toTransitResponseList(transitEntities);
  }

  @ReaderDS
  public List<String> getDistinctDFSA(
      String orgId, String sourceGeozone, List<String> carrierServiceIds)
      throws TransitDomainException {
    return transitDomain.fetchDestinationGeozones(orgId, sourceGeozone, carrierServiceIds);
  }

  @ReaderDS
  public TransitTimeEntriesDto getTransitTimeEntries(String orgId, String carrierServiceId)
      throws TransitDomainException {
    return TransitTimeEntriesDto.builder()
        .orgId(orgId)
        .carrierServiceId(carrierServiceId)
        .totalRecords(transitDomain.fetchTransitEntitiesCount(orgId, carrierServiceId))
        .build();
  }

  @ReaderDS
  public List<TransitResponse> getListOfTransitDetailsForDestinationGeoZone(
      String orgId, String destinationGeozone) throws CommonServiceException {
    List<TransitEntity> transitEntities = new ArrayList<>();
    List<TransitBufferEntity> transitBufferEntities = new ArrayList<>();
    try {
      List<Callable<Object>> tasks = getTransitCallables(orgId, destinationGeozone);
      List<Future<Object>> results =
          threadPoolTaskExecutor.getThreadPoolExecutor().invokeAll(tasks);
      transitEntities = (List<TransitEntity>) results.get(0).get();
      transitBufferEntities = (List<TransitBufferEntity>) results.get(1).get();
    } catch (ExecutionException | InterruptedException e) {
      Thread.currentThread().interrupt();
      logger.error(
          String.valueOf(e),
          "Unable to fetch transit list for orgId: {} and destination geozone: {}",
          orgId,
          destinationGeozone);
    }
    if (transitEntities.isEmpty()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          DESTINATION_GEOZONE, FieldError.builder().rejectedValue(destinationGeozone).build());
      throw new CommonServiceException(
          TRANSIT_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    } else {
      updateBufferDetailsToTransitEntities(transitEntities, transitBufferEntities);
    }
    return INSTANCE.toTransitResponseList(transitEntities);
  }

  public List<TransitResponse> getTransitDetailsForDestinationGeozones(
      String orgId, String carrierServiceId, List<String> destinationGeozones)
      throws TransitDomainException {
    return INSTANCE.convertToTransitResponseList(
        transitDomain.fetchTransitListForDestinationGeoZones(
            orgId, carrierServiceId, destinationGeozones));
  }

  public DistinctGeozonesResponse getDistinctSourceAndDestinationGeoZones(
      String orgId, String carrierServiceId) throws TransitDomainException {
    List<String> sourceGeoZones =
        transitDomain.fetchDistinctSourceGeoZones(orgId, carrierServiceId);
    List<String> destinationGeoZones =
        transitDomain.fetchDistinctDestinationGeoZones(orgId, carrierServiceId);
    var response = new DistinctGeozonesResponse();
    response.setSourceGeozones(sourceGeoZones);
    response.setDestinationGeozones(destinationGeoZones);

    return response;
  }

  public TransitResponse updateTransitBufferDays(
      String orgId, String carrierServiceId, String sourceGeozone, String destinationGeozone)
      throws TransitDomainException {
    Optional<TransitEntity> transitEntity =
        getTransitEntity(orgId, carrierServiceId, sourceGeozone, destinationGeozone);
    if (transitEntity.isPresent()) {
      var tempEntity = transitEntity.get();

      if ((tempEntity.getBufferDays() != null && tempEntity.getBufferDays() != 0)) {
        tempEntity.setBufferDays(0D);
        tempEntity = transitDomain.saveTransitEntity(tempEntity);
      }

      return INSTANCE.toTransitResponse(tempEntity);
    }
    return null;
  }

  private Optional<TransitEntity> getTransitEntity(
      String orgId, String carrierServiceId, String sourceGeozone, String destinationGeozone)
      throws TransitDomainException {
    return transitDomain.findTransitDetails(
        orgId, sourceGeozone, destinationGeozone, carrierServiceId);
  }

  private void updateBufferDetailsToTransitEntities(
      List<TransitEntity> transitEntities, List<TransitBufferEntity> transitBufferEntities) {
    Map<String, TransitBufferEntity> transitBufferDetailMap = new HashMap<>();
    if (!transitBufferEntities.isEmpty()) {
      for (TransitBufferEntity transitBufferEntity : transitBufferEntities) {
        String transitBufferKey =
            getTransitKey(
                transitBufferEntity.getOrgId(),
                transitBufferEntity.getSourceGeozone(),
                transitBufferEntity.getDestinationGeozone(),
                transitBufferEntity.getCarrierServiceId());
        transitBufferDetailMap.put(transitBufferKey, transitBufferEntity);
      }
      for (TransitEntity transitEntity : transitEntities) {
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
        () -> transitDomain.fetchTransitListForDestinationGeoZone(orgId, destinationGeozone);
    Callable<Object> transitBufferCallable =
        () -> transitBufferRepository.findByOrgIdAndDestinationGeozone(orgId, destinationGeozone);
    tasks.add(transitCallable);
    tasks.add(transitBufferCallable);
    return tasks;
  }
}
