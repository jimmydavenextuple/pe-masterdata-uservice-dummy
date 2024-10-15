/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.service.DomainPersistenceService;
import com.nextuple.transit.persistence.domain.TransitBufferV2DomainDto;
import com.nextuple.transit.persistence.domain.key.TransitBufferV2DomainKey;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TransitBufferV2PersistenceService
    extends DomainPersistenceService<TransitBufferV2DomainDto, TransitBufferV2DomainKey> {
  List<TransitBufferV2DomainDto> fetchTransitBuffersByOrgIdAndDestinationGeozone(
      String orgId,
      String destinationGeozone,
      LocalDate requestDate,
      LocalDate requestDatePlusHorizon)
      throws CommonServiceException;

  List<TransitBufferV2DomainDto>
      fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceId(
          String orgId, String destinationGeozone, String sourceGeozone, String carrierServiceId)
          throws CommonServiceException;

  Optional<TransitBufferV2DomainDto>
      fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceIdAndTransitBufferConfigRequestId(
          String orgId,
          String destinationGeozone,
          String sourceGeozone,
          String carrierServiceId,
          Long transitBufferConfigRequestId)
          throws CommonServiceException;

  TransitBufferV2DomainDto saveTransitBuffer(TransitBufferV2DomainDto transitBufferV2DomainDto)
      throws CommonServiceException;

  void deleteTransitBufferEntityByIdAndOrgId(Long id, String orgId) throws CommonServiceException;

  void deleteTransitBuffer(TransitBufferV2DomainDto transitBufferV2DomainDto)
      throws CommonServiceException;

  Optional<TransitBufferV2DomainDto> fetchTransitBufferByOrgIdAndId(String orgId, Long id)
      throws CommonServiceException;

  List<TransitBufferV2DomainDto> fetchTransitBufferById(Long transitBufferConfigRequestId)
      throws CommonServiceException;

  TransitBufferV2DomainDto updateTransitBuffer(TransitBufferV2DomainDto transitBufferV2DomainDto)
      throws CommonServiceException;

  Optional<TransitBufferV2DomainDto>
      fetchTransitBufferByOrgIdAndDestinationGeozoneAndSourceGeozoneAndCarrierServiceIdAndBufferStartDateAndBufferEndDate(
          String orgId,
          String destinationGeozone,
          String sourceGeozone,
          String carrierServiceId,
          Date bufferStartDays,
          Date bufferEndDays)
          throws CommonServiceException;
}
