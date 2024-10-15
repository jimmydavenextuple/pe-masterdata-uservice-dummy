/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.service.DomainPersistenceService;
import com.nextuple.transit.persistence.domain.TransitBufferDomainDto;
import com.nextuple.transit.persistence.domain.key.TransitDomainKey;
import java.util.List;
import java.util.Optional;

public interface TransitBufferPersistenceService
    extends DomainPersistenceService<TransitBufferDomainDto, TransitDomainKey> {
  List<TransitBufferDomainDto> findByOrgIdAndDestinationGeozone(
      String orgId, String destinationGeozone) throws CommonServiceException;

  Optional<TransitBufferDomainDto>
      findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
          String orgId, String carrierServiceId, String sourceGeozone, String destinationGeozone)
          throws CommonServiceException;

  TransitBufferDomainDto deleteTransitBuffer(TransitBufferDomainDto transitBufferDomainDto)
      throws CommonServiceException;

  List<TransitBufferDomainDto> findByTransitBufferConfigRequestId(Long transitBufferConfigRequestId)
      throws CommonServiceException;

  TransitBufferDomainDto saveTransitBuffer(TransitBufferDomainDto transitBufferDomainDto)
      throws CommonServiceException;
}
