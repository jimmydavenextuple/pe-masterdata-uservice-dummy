/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.service;

import com.nextuple.common.service.DomainPersistenceService;
import com.nextuple.transit.domain.pojo.ProjectedTransitEntity;
import com.nextuple.transit.persistence.domain.TransitDomainDto;
import com.nextuple.transit.persistence.domain.key.TransitDomainKey;
import com.nextuple.transit.persistence.exception.TransitDomainException;
import java.util.List;
import java.util.Optional;

public interface TransitPersistenceService
    extends DomainPersistenceService<TransitDomainDto, TransitDomainKey> {

  TransitDomainDto saveTransitDomainDto(TransitDomainDto transitDomainDto)
      throws TransitDomainException;

  List<TransitDomainDto> filterAndGetTransitDetails(
      String orgId,
      String sourceGeozone,
      String destinationGeozone,
      String carrierServiceId,
      String serviceOption)
      throws TransitDomainException;

  Optional<TransitDomainDto> findTransitDetails(
      String orgId, String sourceGeozone, String destinationGeozone, String carrierServiceId)
      throws TransitDomainException;

  void deleteTransitDetails(TransitDomainDto transitEntity) throws TransitDomainException;

  List<TransitDomainDto> fetchTransitList(
      String orgId, String destinationGeozone, List<String> sourceGeozones)
      throws TransitDomainException;

  List<String> fetchDestinationGeozones(
      String orgId, String sourceGeozone, List<String> carrierServiceIds)
      throws TransitDomainException;

  Integer fetchTransitEntitiesCount(String orgId, String carrierServiceId)
      throws TransitDomainException;

  List<TransitDomainDto> fetchTransitListForDestinationGeoZone(
      String orgId, String destinationGeozone) throws TransitDomainException;

  List<ProjectedTransitEntity> fetchTransitListForDestinationGeoZones(
      String orgId, String carrierServiceId, List<String> destinationGeozones)
      throws TransitDomainException;

  List<String> fetchDistinctSourceGeoZones(String orgId, String carrierServiceId)
      throws TransitDomainException;

  List<String> fetchDistinctDestinationGeoZones(String orgId, String carrierServiceId)
      throws TransitDomainException;
}
