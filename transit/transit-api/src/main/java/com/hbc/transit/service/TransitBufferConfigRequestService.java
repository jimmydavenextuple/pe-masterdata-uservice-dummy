package com.hbc.transit.service;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.error.FieldError;
import com.hbc.transit.domain.entity.TransitBufferConfigRequestEntity;
import com.hbc.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import com.hbc.transit.domain.inbound.TransitBufferConfigRequest;
import com.hbc.transit.domain.mapper.TransitBufferConfigRequestMapper;
import com.hbc.transit.domain.outbound.TransitBufferConfigResponse;
import com.hbc.transit.repository.TransitBufferConfigRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class TransitBufferConfigRequestService {

  private static final Logger logger =
      LoggerFactory.getLogger(TransitBufferConfigRequestService.class);

  private static final String TRANSIT_BUFFER_CONFIG_REQUEST_EXCEPTION_MESSAGE =
      "Transit buffer config request not found with given id";

  public static final TransitBufferConfigRequestMapper INSTANCE =
      Mappers.getMapper(TransitBufferConfigRequestMapper.class);

  private final TransitBufferConfigRepository transitBufferConfigRepository;

  private static final String ORG_ID = "orgId";

  private static final String CARRIER_SERVICE_ID = "carrierServiceId";

  private static final String TRANSIT_BUFFER_CONFIG_REQUEST_ID = "transitBufferConfigRequestId";

  public TransitBufferConfigResponse createTransitBufferRequest(
      TransitBufferConfigRequest transitBufferConfigRequest) throws CommonServiceException {
    try {

      var transitBufferConfigRequestEntity =
          INSTANCE.toTransitBufferConfigEntity(transitBufferConfigRequest);

      return INSTANCE.toTransitBufferConfigResponse(
          transitBufferConfigRepository.save(transitBufferConfigRequestEntity));
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to create transit buffer config request");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          ORG_ID,
          FieldError.builder().rejectedValue(transitBufferConfigRequest.getOrgId()).build());
      errorMap.put(
          CARRIER_SERVICE_ID,
          FieldError.builder()
              .rejectedValue(transitBufferConfigRequest.getCarrierServiceId())
              .build());
      throw new CommonServiceException(
          "Unable to create transit buffer config request",
          HttpStatus.BAD_REQUEST,
          0x1772,
          errorMap);
    }
  }

  public TransitBufferConfigResponse updateTransitBufferRequestStatus(
      Long id, TransitBufferConfigRequestStatusEnum status) throws CommonServiceException {

    Optional<TransitBufferConfigRequestEntity> existingTransitBufferConfigRequestEntity =
        transitBufferConfigRepository.findById(id);

    if (existingTransitBufferConfigRequestEntity.isEmpty()) {
      logger.error(TRANSIT_BUFFER_CONFIG_REQUEST_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          TRANSIT_BUFFER_CONFIG_REQUEST_ID, FieldError.builder().rejectedValue(id).build());
      throw new CommonServiceException(
          TRANSIT_BUFFER_CONFIG_REQUEST_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    existingTransitBufferConfigRequestEntity.get().setStatus(status);
    return INSTANCE.toTransitBufferConfigResponse(
        transitBufferConfigRepository.save(existingTransitBufferConfigRequestEntity.get()));
  }

  public List<TransitBufferConfigResponse> fetchTransitBufferRequests(
      String orgId, String carrierServiceId) throws CommonServiceException {
    try {
      List<String> filteredStatus =
          List.of(
              TransitBufferConfigRequestStatusEnum.INACTIVE.getStatus(),
              TransitBufferConfigRequestStatusEnum.DELETED.getStatus());
      List<TransitBufferConfigRequestEntity> transitBufferConfigRequestEntities =
          transitBufferConfigRepository.findByOrgIdAndCarrierServiceId(
              orgId, carrierServiceId, filteredStatus);

      return INSTANCE.toTransitBufferConfigResponseList(transitBufferConfigRequestEntities);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to fetch transit buffer requests");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          CARRIER_SERVICE_ID, FieldError.builder().rejectedValue(carrierServiceId).build());
      throw new CommonServiceException(
          "Unable to fetch transit buffer requests", HttpStatus.BAD_REQUEST, 0x1772, errorMap);
    }
  }

  public TransitBufferConfigRequestEntity getTransitBufferRequest(Long id)
      throws CommonServiceException {

    Optional<TransitBufferConfigRequestEntity> existingTransitBufferConfigRequestEntity =
        transitBufferConfigRepository.findById(id);

    if (existingTransitBufferConfigRequestEntity.isEmpty()) {
      logger.error(TRANSIT_BUFFER_CONFIG_REQUEST_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          TRANSIT_BUFFER_CONFIG_REQUEST_ID, FieldError.builder().rejectedValue(id).build());
      throw new CommonServiceException(
          TRANSIT_BUFFER_CONFIG_REQUEST_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return existingTransitBufferConfigRequestEntity.get();
  }
}
