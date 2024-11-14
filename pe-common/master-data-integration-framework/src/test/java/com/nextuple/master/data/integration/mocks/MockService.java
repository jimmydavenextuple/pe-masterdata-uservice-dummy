/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.master.data.integration.mocks;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.master.data.integration.enums.TaskInformation;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.service.BatchService;

public class MockService extends BatchService<MockDto> {
  @Override
  public TypeReference<BatchRequest<MockDto>> getTypeReference() {
    return new TypeReference<BatchRequest<MockDto>>() {};
  }

  @Override
  public String createRecordImpl(MockDto payload) throws CommonServiceException {
    return "Record created successfully";
  }

  @Override
  public String updateRecordImpl(MockDto payload) throws CommonServiceException {
    return "Record updated successfully";
  }

  @Override
  public String deleteRecordImpl(MockDto payload) {
    return "Record deleted successfully";
  }

  @Override
  public void checkForOutdatedRecord(BatchRequest<MockDto> batchRequest) {}

  @Override
  public TaskInformation getTaskInformation() {
    return TaskInformation.NODE_FEED;
  }
}
