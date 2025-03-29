/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.transit.domain.inbound.*;
import com.nextuple.transit.domain.outbound.TransferScheduleBatchResponse;
import com.nextuple.transit.domain.outbound.TransferScheduleRangeResponse;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import java.util.List;
import org.springframework.data.domain.Page;

public interface TransferScheduleService {
  TransferScheduleResponse createTransferSchedule(TransferScheduleCreationRequest request)
      throws CommonServiceException, PromiseEngineException;

  TransferScheduleBatchResponse batchTransferSchedules(
      TransferScheduleBatchRequest transferScheduleCreationRequests, String orgId)
      throws PromiseEngineException;

  List<TransferScheduleResponse> fetchTransferSchedules(String orgId, String dropoffNodeId);

  TransferScheduleResponse deleteTransferSchedule(TransferScheduleRequest request)
      throws PromiseEngineException, CommonServiceException;

  Page<TransferScheduleResponse> fetchTransferScheduleList(
      String orgId,
      Boolean isPaginated,
      PageParams pageParams,
      FetchTransferScheduleRequest request)
      throws CommonServiceException, PromiseEngineException;

  List<TransferScheduleRangeResponse> fetchTransferSchedulesInRange(
      TransferScheduleRangeRequest transferScheduleRangeRequest);
}
