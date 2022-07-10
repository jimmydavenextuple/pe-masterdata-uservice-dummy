package com.hbc.carrier.service;

import com.hbc.carrier.domain.CarrierServiceDomain;
import com.hbc.carrier.domain.entity.CarrierServiceEntity;
import com.hbc.carrier.domain.inbound.CarrierServiceRequest;
import com.hbc.carrier.domain.inbound.CarrierServiceUpdateRequest;
import com.hbc.carrier.domain.mapper.CarrierServiceMapper;
import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import com.hbc.carrier.exception.CarrierServiceDomainException;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.error.FieldError;
import java.util.HashMap;
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
public class CarrierServiceService {
  private static final Logger logger = LoggerFactory.getLogger(CarrierServiceService.class);
  private static final String CARRIER_ID = "carrierId";

  private static final String ORG_ID = "orgId";

  private static final String CARRIER_SERVICE_EXCEPTION_MESSAGE =
      "Carrier service not found with given details";
  private static final String SERVICE_ID = "serviceId";

  private final CarrierServiceDomain carrierServiceDomain;

  public static final CarrierServiceMapper INSTANCE = Mappers.getMapper(CarrierServiceMapper.class);

  public CarrierServiceResponse createCarrierService(CarrierServiceRequest carrierServiceRequest)
      throws CarrierServiceDomainException {

    var carrierServiceEntity =
        INSTANCE.carrierServiceRequestToCarrierServiceEntity(carrierServiceRequest);

    return INSTANCE.toCarrierServiceResponse(
        carrierServiceDomain.saveCarrierServiceEntity(carrierServiceEntity));
  }

  public CarrierServiceResponse getCarrierServiceDetails(
      String carrierId, String serviceId, String orgId)
      throws CarrierServiceDomainException, CommonServiceException {

    Optional<CarrierServiceEntity> carrierServiceEntity =
        carrierServiceDomain.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
            carrierId, serviceId, orgId);

    if (carrierServiceEntity.isEmpty()) {
      logger.debug(CARRIER_SERVICE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(CARRIER_ID, FieldError.builder().rejectedValue(carrierId).build());
      errorMap.put(SERVICE_ID, FieldError.builder().rejectedValue(serviceId).build());
      throw new CommonServiceException(
          CARRIER_SERVICE_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return INSTANCE.toCarrierServiceResponse(carrierServiceEntity.get());
  }

  public CarrierServiceResponse deleteCarrierService(
      String carrierId, String serviceId, String orgId)
      throws CarrierServiceDomainException, CommonServiceException {

    Optional<CarrierServiceEntity> carrierServiceEntity =
        carrierServiceDomain.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
            carrierId, serviceId, orgId);

    if (carrierServiceEntity.isEmpty()) {
      logger.debug(CARRIER_SERVICE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(CARRIER_ID, FieldError.builder().rejectedValue(carrierId).build());
      errorMap.put(SERVICE_ID, FieldError.builder().rejectedValue(serviceId).build());
      throw new CommonServiceException(
          CARRIER_SERVICE_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    var carrierServiceResponse = INSTANCE.toCarrierServiceResponse(carrierServiceEntity.get());
    carrierServiceDomain.deleteCarrierService(carrierServiceEntity.get());
    return carrierServiceResponse;
  }

  public CarrierServiceResponse updateCarrierServiceDetails(
      String carrierId,
      String serviceId,
      String orgId,
      CarrierServiceUpdateRequest carrierServiceUpdateRequest)
      throws CarrierServiceDomainException, CommonServiceException {

    Optional<CarrierServiceEntity> carrierServiceEntity =
        carrierServiceDomain.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
            carrierId, serviceId, orgId);

    if (carrierServiceEntity.isEmpty()) {
      logger.debug(CARRIER_SERVICE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(CARRIER_ID, FieldError.builder().rejectedValue(carrierId).build());
      errorMap.put(SERVICE_ID, FieldError.builder().rejectedValue(serviceId).build());
      throw new CommonServiceException(
          CARRIER_SERVICE_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }

    INSTANCE.updateCarrierServiceEntity(carrierServiceUpdateRequest, carrierServiceEntity.get());
    return INSTANCE.toCarrierServiceResponse(
        carrierServiceDomain.saveCarrierServiceEntity(carrierServiceEntity.get()));
  }
}
