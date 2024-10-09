/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.carrier.service;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;
import static com.nextuple.common.constants.CommonConstants.DESC_SORT_ORDER;

import com.nextuple.carrier.config.CarrierTenantBasedDBConfig;
import com.nextuple.carrier.domain.dto.CarrierCacheKeyDto;
import com.nextuple.carrier.domain.inbound.CarrierServiceRequest;
import com.nextuple.carrier.domain.inbound.CarrierServiceUpdateRequest;
import com.nextuple.carrier.domain.mapper.CarrierServiceMapper;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.carrier.persistence.domain.CarrierServiceDomainDto;
import com.nextuple.carrier.persistence.exception.CarrierServiceDomainException;
import com.nextuple.carrier.persistence.service.CarrierServicePersistenceService;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.node.carrier.domain.feign.INodeCarrierFeign;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.postgres.config.ReaderDS;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

  private static final String SERVICE_OPTIONS = "serviceOptions";

  @Autowired INodeCarrierFeign nodeCarrierFeign;

  private final CarrierServicePersistenceService carrierServicePersistenceService;

  private final CarrierTenantBasedDBConfig carrierTenantBasedDBConfig;

  public static final CarrierServiceMapper INSTANCE = Mappers.getMapper(CarrierServiceMapper.class);

  private static final String CARRIER_SERVICE_DELTE_EXCEPTION_MESSAGE =
      "Carrier cannot be deleted. Please delete the associated node-carrier and last Pickup time details before deleting this carrier";

  public CarrierServiceResponse createCarrierService(CarrierServiceRequest carrierServiceRequest)
      throws CarrierServiceDomainException, CommonServiceException {

    validateServiceOptions(
        carrierServiceRequest.getOrgId(), carrierServiceRequest.getServiceOptions());

    Optional<List<CarrierServiceDomainDto>> carrierServiceDomainDtos =
        carrierServicePersistenceService.findCarrierServiceByServiceIdAndOrgId(
            carrierServiceRequest.getCarrierServiceId(), carrierServiceRequest.getOrgId());

    if (carrierServiceDomainDtos.isPresent() && !carrierServiceDomainDtos.get().isEmpty()) {
      logger.error(
          "Carrier Service Id already exists for given OrgId and CarrierServiceId : {}",
          carrierServiceRequest.getCarrierServiceId());
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          ORG_ID, FieldError.builder().rejectedValue(carrierServiceRequest.getOrgId()).build());
      errorMap.put(
          SERVICE_ID,
          FieldError.builder().rejectedValue(carrierServiceRequest.getCarrierServiceId()).build());
      throw new CommonServiceException(
          "Carrier Service Id already exists for given OrgId and CarrierServiceId",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
    var carrierServiceDomainDto =
        INSTANCE.carrierServiceRequestToCarrierServiceDomainDto(carrierServiceRequest);

    return INSTANCE.toCarrierServiceResponse(
        carrierServicePersistenceService.saveCarrierService(carrierServiceDomainDto));
  }

  private void validateServiceOptions(String orgId, String serviceOptions)
      throws CommonServiceException {
    if (!carrierTenantBasedDBConfig.getServiceOptions(orgId).contains(serviceOptions)) {
      logger.error("Invalid service Option : {} for given orgId :{}", serviceOptions, orgId);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(SERVICE_OPTIONS, FieldError.builder().rejectedValue(serviceOptions).build());
      throw new CommonServiceException(
          "Invalid service option", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
  }

  @ReaderDS
  public CarrierServiceResponse getCarrierServiceDetails(
      String carrierId, String serviceId, String orgId)
      throws CarrierServiceDomainException, CommonServiceException {

    Optional<CarrierServiceDomainDto> carrierServiceEntity =
        carrierServicePersistenceService.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
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

    Optional<CarrierServiceDomainDto> carrierServiceEntity =
        carrierServicePersistenceService.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
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
    BaseResponse<List<NodeCarrierResponse>> response =
        nodeCarrierFeign.getAllNodeCarriersByOrgIdCarrierServiceId(orgId, serviceId);
    /*Check if node-carrier still in use for the carrierServiceId to be deleted*/
    if (!response.getPayload().isEmpty()) {
      logger.error(CARRIER_SERVICE_DELTE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(CARRIER_ID, FieldError.builder().rejectedValue(carrierId).build());
      errorMap.put(SERVICE_ID, FieldError.builder().rejectedValue(serviceId).build());
      throw new CommonServiceException(
          CARRIER_SERVICE_DELTE_EXCEPTION_MESSAGE, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
    var carrierServiceResponse = INSTANCE.toCarrierServiceResponse(carrierServiceEntity.get());
    carrierServicePersistenceService.deleteCarrierService(carrierServiceEntity.get());
    return carrierServiceResponse;
  }

  public CarrierServiceResponse updateCarrierServiceDetails(
      String carrierId,
      String serviceId,
      String orgId,
      CarrierServiceUpdateRequest carrierServiceUpdateRequest)
      throws CarrierServiceDomainException, CommonServiceException {

    validateServiceOptions(orgId, carrierServiceUpdateRequest.getServiceOptions());

    Optional<CarrierServiceDomainDto> carrierServiceEntity =
        carrierServicePersistenceService.findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
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
        carrierServicePersistenceService.saveCarrierService(carrierServiceEntity.get()));
  }

  @ReaderDS
  public Page<CarrierServiceResponse> getCarrierServiceList(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder)
      throws CommonServiceException, CarrierServiceDomainException {
    if (sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)
        || sortOrder.equalsIgnoreCase(DESC_SORT_ORDER)) {
      return carrierServicePersistenceService
          .findCarrierServiceListByOrgId(orgId, pageNo, pageSize, sortBy, sortOrder)
          .map(INSTANCE::toCarrierServiceResponse);
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
    return INSTANCE.toCarrierServiceResponseList(
        carrierServicePersistenceService.findCarrierServiceListByOrgIdWithoutPagination(orgId));
  }

  @ReaderDS
  public List<CarrierServiceResponse> getCarrierServiceDetailsByCarrierIdAndOrgId(
      String serviceId, String orgId) throws CarrierServiceDomainException, CommonServiceException {

    Optional<List<CarrierServiceDomainDto>> carrierServiceDomainDtoList =
        carrierServicePersistenceService.findCarrierServiceByServiceIdAndOrgId(serviceId, orgId);

    if (carrierServiceDomainDtoList.isEmpty()) {
      logger.error(CARRIER_SERVICE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(SERVICE_ID, FieldError.builder().rejectedValue(serviceId).build());
      throw new CommonServiceException(
          CARRIER_SERVICE_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return INSTANCE.toCarrierServiceResponseList(carrierServiceDomainDtoList.get());
  }

  public List<CarrierCacheKeyDto> getAllCarrierCacheKeys(Integer limit)
      throws CarrierServiceDomainException {
    var carrierServiceEntities =
        carrierServicePersistenceService.getAllCarrierServiceEntities(limit);

    return INSTANCE.toCarrierCacheKeyList(carrierServiceEntities);
  }
}
