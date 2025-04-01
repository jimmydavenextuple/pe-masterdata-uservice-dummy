/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.service.DomainPersistenceService;
import com.nextuple.transit.domain.inbound.FetchTransferScheduleRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleDeleteRequest;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import com.nextuple.transit.persistence.domain.TransferScheduleDomainDto;
import com.nextuple.transit.persistence.domain.TransferScheduleDomainRequest;
import com.nextuple.transit.persistence.domain.key.TransferScheduleDomainKey;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransferSchedulePersistenceService
    extends DomainPersistenceService<TransferScheduleDomainDto, TransferScheduleDomainKey> {
  TransferScheduleDomainDto saveTransferSchedule(
      TransferScheduleDomainDto transferScheduleDomainDto) throws PromiseEngineException;

  List<TransferScheduleDomainDto> saveTransferSchedules(
      List<TransferScheduleDomainDto> transferScheduleDomainDtos) throws PromiseEngineException;

  List<TransferScheduleDomainDto> fetchUpcomingTransferSchedules(
      String orgId, String dropOffNodeId);

  TransferScheduleDomainDto deleteTransferSchedule(
      String orgId, String sourceNodeId, String dropOffNodeId, Date startTime)
      throws PromiseEngineException, CommonServiceException;

  Page<TransferScheduleResponse> fetchTransferSchedulesList(
      String orgId, FetchTransferScheduleRequest request, Pageable pageable)
      throws PromiseEngineException;

  List<TransferScheduleDomainDto> fetchTransferSchedulesInRange(
      TransferScheduleDomainRequest request);

  List<TransferScheduleDomainDto> deleteTransferSchedules(
      List<TransferScheduleDeleteRequest> transferScheduleDeleteRequests);
}
