/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.transit.domain.inbound.TransitBufferDeletionRequest;
import com.nextuple.transit.domain.inbound.TransitBufferRequest;
import com.nextuple.transit.domain.inbound.TransitBufferV2UpdationRequest;
import com.nextuple.transit.domain.outbound.TransitBufferDetailsResponse;
import com.nextuple.transit.domain.outbound.TransitBufferV2Response;
import com.nextuple.transit.persistence.exception.TransitDomainException;
import java.time.LocalDate;
import java.util.List;

public interface TransitBufferV2Service {

  List<TransitBufferDetailsResponse>
      getTransitBuffersByOrgIdDestinationGeozoneRequestDateAndHorizonDays(
          String orgId, String destinationGeozone, LocalDate requestDate, Integer horizonDays)
          throws CommonServiceException;

  TransitBufferV2Response saveTransitBuffer(TransitBufferRequest transitBufferRequest)
      throws CommonServiceException, TransitDomainException;

  TransitBufferV2Response getTransitBufferByOrgIdAndId(String orgId, Long id)
      throws CommonServiceException;

  TransitBufferV2Response updateTransitBufferByOrgIdAndId(
      String orgId, Long id, TransitBufferV2UpdationRequest transitBufferV2UpdationRequest)
      throws CommonServiceException, TransitDomainException;

  TransitBufferV2Response updateTransitBuffer(TransitBufferRequest transitBufferRequest)
      throws CommonServiceException, TransitDomainException;

  TransitBufferV2Response deleteTransitBuffer(TransitBufferRequest transitBufferRequest)
      throws CommonServiceException;

  TransitBufferV2Response deleteTransitBufferById(String orgId, Long id)
      throws CommonServiceException;

  TransitBufferV2Response deleteTransitBufferRecord(
      TransitBufferDeletionRequest transitBufferDeletionRequest) throws CommonServiceException;
}
