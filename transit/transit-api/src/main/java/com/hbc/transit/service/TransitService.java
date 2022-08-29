package com.hbc.transit.service;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.error.FieldError;
import com.hbc.postgres.config.ReaderDS;
import com.hbc.transit.domain.TransitDomain;
import com.hbc.transit.domain.dto.TransitTimeEntriesDto;
import com.hbc.transit.domain.entity.TransitEntity;
import com.hbc.transit.domain.inbound.TransitDataCreationRequest;
import com.hbc.transit.domain.inbound.TransitDataUpdationRequest;
import com.hbc.transit.domain.mapper.TransitMapper;
import com.hbc.transit.domain.outbound.TransitResponse;
import com.hbc.transit.exception.TransitDomainException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TransitService {

  private static final Logger logger = LoggerFactory.getLogger(TransitService.class);
  private static final String SOURCE_GEOZONE = "sourceGeozone";
  private static final String DESTINATION_GEOZONE = "destinationGeozone";
  private static final String ORG_ID = "orgId";
  private static final String CARRIER_SERVICE_ID = "carrierServiceId";

  private final TransitDomain transitDomain;

  public static final TransitMapper INSTANCE = Mappers.getMapper(TransitMapper.class);

  private static final String TRANSIT_EXCEPTION_MESSAGE =
      "Transit data not found with given details";

  public TransitResponse addTransitInfo(TransitDataCreationRequest transitDataCreationRequest)
      throws TransitDomainException {

    var transitEntity = INSTANCE.toTransitEntity(transitDataCreationRequest);

    return INSTANCE.toTransitResponse(transitDomain.saveTransitEntity(transitEntity));
  }

  public TransitResponse updateTransitDetails(
      String orgId,
      String sourceGeozone,
      String destinationGeozone,
      String carrierServiceId,
      TransitDataUpdationRequest transitDataUpdationRequest)
      throws TransitDomainException, CommonServiceException {

    Optional<TransitEntity> existingTransitEntity =
        transitDomain.findTransitDetails(
            orgId, sourceGeozone, destinationGeozone, carrierServiceId);

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
        transitDomain.findTransitDetails(
            orgId, sourceGeozone, destinationGeozone, carrierServiceId);

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
      String orgId, String destinationGeozone)
      throws TransitDomainException, CommonServiceException {

    List<TransitEntity> transitEntities =
        transitDomain.fetchTransitListForDestinationGeoZone(orgId, destinationGeozone);

    if (transitEntities.isEmpty()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          DESTINATION_GEOZONE, FieldError.builder().rejectedValue(destinationGeozone).build());
      throw new CommonServiceException(
          TRANSIT_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }

    return INSTANCE.toTransitResponseList(transitEntities);
  }
}
