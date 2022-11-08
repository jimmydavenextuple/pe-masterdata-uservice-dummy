package com.nextuple.transit.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.transit.domain.entity.TransitBufferEntity;
import com.nextuple.transit.domain.entity.TransitEntity;
import com.nextuple.transit.domain.inbound.TransitBufferRequest;
import com.nextuple.transit.domain.mapper.TransitBufferMapper;
import com.nextuple.transit.domain.outbound.TransitBufferResponse;
import com.nextuple.transit.repository.TransitBufferRepository;
import com.nextuple.transit.repository.TransitRepository;
import com.nextuple.transit.utils.TransitUtils;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransitBufferService {
  private final TransitBufferRepository transitBufferRepository;

  public static final TransitBufferMapper INSTANCE = Mappers.getMapper(TransitBufferMapper.class);

  private static final Logger logger = LoggerFactory.getLogger(TransitBufferService.class);

  private static final String ORG_ID = "orgId";
  private static final String CARRIER_SERVICE_ID = "carrierServiceId";
  private static final String SOURCE_GEOZONE = "sourceGeozone";
  private static final String DESTINATION_GEOZONE = "destinationGeozone";
  private static final String TRANSIT_BUFFER_NOT_FOUND = "Transit buffer details not found";
  private final TransitRepository transitRepository;
  private static final String TRANSIT_NOT_FOUND = "Transit details not found";

  public TransitBufferResponse saveTransitBuffer(TransitBufferRequest transitBufferRequest)
      throws CommonServiceException {
    getTransitDaysAndValidateTransitDetails(transitBufferRequest);
    Optional<TransitBufferEntity> existingTransitBufferEntity =
        transitBufferRepository.findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
            transitBufferRequest.getOrgId(),
            transitBufferRequest.getCarrierServiceId(),
            transitBufferRequest.getSourceGeozone(),
            transitBufferRequest.getDestinationGeozone());
    if (existingTransitBufferEntity.isPresent()) {
      Map<String, FieldError> errorMap =
          getErrorMap(
              transitBufferRequest.getOrgId(),
              transitBufferRequest.getCarrierServiceId(),
              transitBufferRequest.getSourceGeozone(),
              transitBufferRequest.getDestinationGeozone());
      throw new CommonServiceException(
          "Transit Buffer details already present", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
    try {
      var transitBufferEntity =
          transitBufferRepository.save(INSTANCE.toTransitBufferEntity(transitBufferRequest));
      return INSTANCE.toTransitBufferResponse(transitBufferEntity);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to create transit buffer");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          ORG_ID, FieldError.builder().rejectedValue(transitBufferRequest.getOrgId()).build());
      errorMap.put(
          CARRIER_SERVICE_ID,
          FieldError.builder().rejectedValue(transitBufferRequest.getCarrierServiceId()).build());
      throw new CommonServiceException(
          "Unable to create transit buffer", HttpStatus.BAD_REQUEST, 0x1772, errorMap);
    }
  }

  public List<TransitBufferResponse> getTransitBuffersByOrgIdAndDestinationGeozone(
      String orgId, String destinationGeozone) throws CommonServiceException {
    try {
      List<TransitBufferEntity> transitBufferEntities =
          transitBufferRepository.findByOrgIdAndDestinationGeozone(orgId, destinationGeozone);

      return INSTANCE.toTransitBufferResponseList(transitBufferEntities);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Error in fetching transit buffer details");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          DESTINATION_GEOZONE, FieldError.builder().rejectedValue(destinationGeozone).build());
      throw new CommonServiceException(
          "Unable to fetch transit buffer details", HttpStatus.BAD_REQUEST, 0x1772, errorMap);
    }
  }

  public TransitBufferResponse updateTransitBuffer(TransitBufferRequest transitBufferRequest)
      throws CommonServiceException {
    getTransitDaysAndValidateTransitDetails(transitBufferRequest);
    Optional<TransitBufferEntity> existingTransitBufferEntity =
        transitBufferRepository.findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
            transitBufferRequest.getOrgId(),
            transitBufferRequest.getCarrierServiceId(),
            transitBufferRequest.getSourceGeozone(),
            transitBufferRequest.getDestinationGeozone());
    if (existingTransitBufferEntity.isPresent()) {
      existingTransitBufferEntity.get().setBufferDays(transitBufferRequest.getBufferDays());
      existingTransitBufferEntity
          .get()
          .setBufferStartDate(transitBufferRequest.getBufferStartDate());
      existingTransitBufferEntity.get().setBufferEndDate(transitBufferRequest.getBufferEndDate());
      existingTransitBufferEntity.get().setUpdatedBy(transitBufferRequest.getUpdatedBy());
      existingTransitBufferEntity.get().setLastModifiedDate(new Date());
      return INSTANCE.toTransitBufferResponse(
          transitBufferRepository.save(existingTransitBufferEntity.get()));
    } else {
      Map<String, FieldError> errorMap =
          getErrorMap(
              transitBufferRequest.getOrgId(),
              transitBufferRequest.getCarrierServiceId(),
              transitBufferRequest.getSourceGeozone(),
              transitBufferRequest.getDestinationGeozone());
      throw new CommonServiceException(
          TRANSIT_BUFFER_NOT_FOUND, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
  }

  public TransitBufferResponse deleteTransitBufferDetails(
      String orgId, String carrierServiceId, String sourceGeozone, String destinationGeozone)
      throws CommonServiceException {
    Optional<TransitBufferEntity> transitBufferEntity =
        transitBufferRepository.findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
            orgId, carrierServiceId, sourceGeozone, destinationGeozone);

    if (transitBufferEntity.isPresent()) {
      var transitBufferResponse = INSTANCE.toTransitBufferResponse(transitBufferEntity.get());
      transitBufferRepository.delete(transitBufferEntity.get());
      return transitBufferResponse;
    } else {
      Map<String, FieldError> errorMap =
          getErrorMap(orgId, carrierServiceId, sourceGeozone, destinationGeozone);
      throw new CommonServiceException(
          TRANSIT_BUFFER_NOT_FOUND, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
  }

  private Map<String, FieldError> getErrorMap(
      String orgId, String carrierServiceId, String sourceGeozone, String destinationGeozone) {
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
    errorMap.put(CARRIER_SERVICE_ID, FieldError.builder().rejectedValue(carrierServiceId).build());
    errorMap.put(SOURCE_GEOZONE, FieldError.builder().rejectedValue(sourceGeozone).build());
    errorMap.put(
        DESTINATION_GEOZONE, FieldError.builder().rejectedValue(destinationGeozone).build());
    return errorMap;
  }

  private void getTransitDaysAndValidateTransitDetails(TransitBufferRequest transitBufferRequest)
      throws CommonServiceException {
    var transitDays =
        getTransitDaysFromExistingTransitEntity(
            transitBufferRequest.getOrgId(),
            transitBufferRequest.getSourceGeozone(),
            transitBufferRequest.getDestinationGeozone(),
            transitBufferRequest.getCarrierServiceId());
    TransitUtils.validateTransitDetails(
        transitDays,
        transitBufferRequest.getBufferDays(),
        transitBufferRequest.getOrgId(),
        transitBufferRequest.getSourceGeozone(),
        transitBufferRequest.getDestinationGeozone(),
        transitBufferRequest.getCarrierServiceId());
  }

  private Float getTransitDaysFromExistingTransitEntity(
      String orgId, String sourceGeozone, String destinationGeozone, String carrierServiceId)
      throws CommonServiceException {
    Float transitDays;
    Optional<TransitEntity> existingTransitEntity =
        transitRepository.findByOrgIdAndSourceGeozoneAndDestinationGeozoneAndCarrierServiceId(
            orgId, sourceGeozone, destinationGeozone, carrierServiceId);
    if (existingTransitEntity.isPresent()) {
      transitDays = existingTransitEntity.get().getTransitDays();
    } else {
      Map<String, FieldError> errorMap =
          getErrorMap(orgId, carrierServiceId, sourceGeozone, destinationGeozone);
      throw new CommonServiceException(TRANSIT_NOT_FOUND, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return transitDays;
  }
}
