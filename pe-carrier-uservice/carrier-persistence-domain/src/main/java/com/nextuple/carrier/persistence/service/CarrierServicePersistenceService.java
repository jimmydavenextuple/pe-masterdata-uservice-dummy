/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.carrier.persistence.service;

import com.nextuple.carrier.persistence.domain.CarrierServiceDomainDto;
import com.nextuple.carrier.persistence.exception.CarrierServiceDomainException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface CarrierServicePersistenceService {
  CarrierServiceDomainDto saveCarrierService(CarrierServiceDomainDto carrierServiceDomainDto)
      throws CarrierServiceDomainException;

  Optional<CarrierServiceDomainDto> findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
      String carrierId, String serviceId, String orgId) throws CarrierServiceDomainException;

  void deleteCarrierService(CarrierServiceDomainDto carrierServiceDomainDto)
      throws CarrierServiceDomainException;

  Page<CarrierServiceDomainDto> findCarrierServiceListByOrgId(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder)
      throws CarrierServiceDomainException;

  List<CarrierServiceDomainDto> findCarrierServiceListByOrgIdWithoutPagination(String orgId)
      throws CarrierServiceDomainException;

  Optional<List<CarrierServiceDomainDto>> findCarrierServiceByServiceIdAndOrgId(
      String serviceId, String orgId) throws CarrierServiceDomainException;

  List<CarrierServiceDomainDto> getAllCarrierServiceEntities(Integer limit)
      throws CarrierServiceDomainException;
}
