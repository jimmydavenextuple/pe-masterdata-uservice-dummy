package com.nextuple.carrier.service;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;
import static com.nextuple.common.constants.CommonConstants.DESC_SORT_ORDER;

import com.nextuple.carrier.domain.CarrierServiceDomain;
import com.nextuple.carrier.domain.dto.CarrierCacheKeyDto;
import com.nextuple.carrier.domain.entity.CarrierServiceEntity;
import com.nextuple.carrier.domain.inbound.CarrierServiceRequest;
import com.nextuple.carrier.domain.inbound.CarrierServiceUpdateRequest;
import com.nextuple.carrier.domain.mapper.CarrierServiceMapper;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.carrier.exception.CarrierServiceDomainException;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.postgres.config.ReaderDS;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CarrierServiceService {
  private static final Logger logger = LoggerFactory.getLogger(CarrierServiceService.class);
  private static final String CARRIER_ID = "carrierId";

  private static final String ORG_ID = "orgId";

  private static final String SORT_ORDER = "sortOrder";

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

  @ReaderDS
  public CarrierServiceResponse getCarrierServiceDetails(
      String carrierId, String serviceId, String orgId)
      throws CarrierServiceDomainException, CommonServiceException {

    Optional<CarrierServiceEntity> carrierServiceEntity =
        carrierServiceDomain.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
            carrierId, serviceId, orgId);

    if (carrierServiceEntity.isEmpty()) {
      logger.error(CARRIER_SERVICE_EXCEPTION_MESSAGE);
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
      logger.error(CARRIER_SERVICE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(CARRIER_ID, FieldError.builder().rejectedValue(carrierId).build());
      errorMap.put(SERVICE_ID, FieldError.builder().rejectedValue(serviceId).build());
      throw new CommonServiceException(
          CARRIER_SERVICE_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    logger.info(
        "Response before deletion of carrier :{}",
        INSTANCE.toCarrierServiceResponse(carrierServiceEntity.get()));
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
      logger.error(CARRIER_SERVICE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(CARRIER_ID, FieldError.builder().rejectedValue(carrierId).build());
      errorMap.put(SERVICE_ID, FieldError.builder().rejectedValue(serviceId).build());
      throw new CommonServiceException(
          CARRIER_SERVICE_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    logger.info(
        "Response before updation of carrier :{}",
        INSTANCE.toCarrierServiceResponse(carrierServiceEntity.get()));
    INSTANCE.updateCarrierServiceEntity(carrierServiceUpdateRequest, carrierServiceEntity.get());
    return INSTANCE.toCarrierServiceResponse(
        carrierServiceDomain.saveCarrierServiceEntity(carrierServiceEntity.get()));
  }

  @ReaderDS
  public Page<CarrierServiceResponse> getCarrierServiceList(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder)
      throws CarrierServiceDomainException, CommonServiceException {
    if (sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)
        || sortOrder.equalsIgnoreCase(DESC_SORT_ORDER)) {
      return carrierServiceDomain.findCarrierServiceListByOrgId(
          orgId, pageNo, pageSize, sortBy, sortOrder);
    } else {
      logger.error("Invalid sort order");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(SORT_ORDER, FieldError.builder().rejectedValue(sortOrder).build());
      throw new CommonServiceException(
          "Invalid sort order, consider giving either ASC or DESC",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
  }

  public List<CarrierServiceResponse> getCarrierServiceListByOrgId(String orgId)
      throws CarrierServiceDomainException {
    return carrierServiceDomain.findCarrierServiceListByOrgIdWithoutPagination(orgId);
  }

  @ReaderDS
  public List<CarrierServiceResponse> getCarrierServiceDetailsByCarrierIdAndOrgId(
      String serviceId, String orgId) throws CarrierServiceDomainException, CommonServiceException {

    Optional<List<CarrierServiceEntity>> carrierServiceEntity =
        carrierServiceDomain.findCarrierServiceByServiceIdAndOrgId(serviceId, orgId);

    if (carrierServiceEntity.isEmpty()) {
      logger.error(CARRIER_SERVICE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(SERVICE_ID, FieldError.builder().rejectedValue(serviceId).build());
      throw new CommonServiceException(
          CARRIER_SERVICE_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return INSTANCE.toCarrierServiceResponseList(carrierServiceEntity.get());
  }

  public List<CarrierCacheKeyDto> getAllCarrierCacheKeys(Integer limit)
      throws CarrierServiceDomainException {
    var carrierServiceEntities = carrierServiceDomain.getAllCarrierServiceEntities(limit);

    return INSTANCE.toCarrierCacheKeyList(carrierServiceEntities);
  }
}
