/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.carrier.persistence.service.impl;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;

import com.nextuple.carrier.persistence.domain.CarrierServiceDomainDto;
import com.nextuple.carrier.persistence.domain.key.CarrierServiceDomainKey;
import com.nextuple.carrier.persistence.entity.CarrierServiceEntity;
import com.nextuple.carrier.persistence.entity.key.CarrierServiceKey;
import com.nextuple.carrier.persistence.exception.CarrierServiceDomainException;
import com.nextuple.carrier.persistence.mapper.CarrierServiceEntityMapper;
import com.nextuple.carrier.persistence.repository.CarrierServiceRepository;
import com.nextuple.carrier.persistence.service.CarrierServicePersistenceService;
import com.nextuple.postgres.service.CommonPersistenceService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarrierServicePersistenceServiceImpl
    extends CommonPersistenceService<
        CarrierServiceDomainDto,
        CarrierServiceDomainKey,
        CarrierServiceEntity,
        CarrierServiceKey,
        CarrierServiceRepository,
        CarrierServiceEntityMapper>
    implements CarrierServicePersistenceService {

  private static final Logger logger =
      LoggerFactory.getLogger(CarrierServicePersistenceServiceImpl.class);

  @Override
  public CarrierServiceDomainDto saveCarrierService(CarrierServiceDomainDto carrierServiceDomainDto)
      throws CarrierServiceDomainException {
    try {
      return save(carrierServiceDomainDto);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to create carrier service");
      throw new CarrierServiceDomainException(
          "Error while saving the carrier service",
          carrierServiceDomainDto.getCarrierId(),
          carrierServiceDomainDto.getCarrierServiceId(),
          carrierServiceDomainDto.getOrgId());
    }
  }

  @Override
  public Optional<CarrierServiceDomainDto> findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
      String carrierId, String serviceId, String orgId) throws CarrierServiceDomainException {
    try {
      return findByKey(
          CarrierServiceDomainKey.builder()
              .carrierServiceId(serviceId)
              .carrierId(carrierId)
              .orgId(orgId)
              .build());
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find carrier service");
      throw new CarrierServiceDomainException(
          "Error while finding carrier service", carrierId, serviceId, orgId);
    }
  }

  @Override
  public void deleteCarrierService(CarrierServiceDomainDto carrierServiceDomainDto)
      throws CarrierServiceDomainException {
    try {
      delete(carrierServiceDomainDto);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to delete carrier service");
      throw new CarrierServiceDomainException(
          "Error while deleting carrier service",
          carrierServiceDomainDto.getCarrierId(),
          carrierServiceDomainDto.getCarrierServiceId(),
          carrierServiceDomainDto.getOrgId());
    }
  }

  @Override
  public Page<CarrierServiceDomainDto> findCarrierServiceListByOrgId(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder)
      throws CarrierServiceDomainException {
    try {
      Pageable pageable = null;
      if (sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)) {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).ascending());
      } else {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).descending());
      }
      return getRepository().findCarrierServicesByOrgId(orgId, pageable).map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find carrier service list");
      throw new CarrierServiceDomainException(
          "Error while finding carrier service list", null, null, orgId);
    }
  }

  @Override
  public List<CarrierServiceDomainDto> findCarrierServiceListByOrgIdWithoutPagination(String orgId)
      throws CarrierServiceDomainException {
    try {
      List<CarrierServiceEntity> carrierServiceEntity =
          getRepository().findCarrierServicesByOrgId(orgId);
      return getMapper().toDomain(carrierServiceEntity);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find carrier service list");
      throw new CarrierServiceDomainException(
          "Error while finding carrier service list", null, null, orgId);
    }
  }

  @Override
  public Optional<List<CarrierServiceDomainDto>> findCarrierServiceByServiceIdAndOrgId(
      String serviceId, String orgId) throws CarrierServiceDomainException {
    try {
      return getRepository()
          .findCarrierServiceByCarrierServiceIdAndOrgId(serviceId, orgId)
          .map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find carrier service");
      throw new CarrierServiceDomainException(
          "Error while finding carrier service", "", serviceId, orgId);
    }
  }

  @Override
  public List<CarrierServiceDomainDto> getAllCarrierServiceEntities(Integer limit)
      throws CarrierServiceDomainException {
    try {
      return getMapper().toDomain(getRepository().findAllCarriersByLimit(limit));
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to fetch the carrier entities");
      throw new CarrierServiceDomainException(
          "Error while fetching all carrier services", null, null, null);
    }
  }
}
