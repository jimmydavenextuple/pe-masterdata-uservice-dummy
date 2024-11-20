/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.common.outbound.GenericUploadResponse;
import com.nextuple.csvdownload.exception.JobSubmissionException;
import com.nextuple.csvdownload.service.ConfigDataUploadService;
import com.nextuple.csvdownload.util.TestUtil;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;

@ExtendWith(MockitoExtension.class)
class ConfigDataUploadControllerTest {

  @Mock private ConfigDataUploadService configDataUploadService;
  @InjectMocks private ConfigDataUploadController configDataUploadController;
  @InjectMocks private TestUtil testUtil;

  @Test
  void uploadConfigDataTest()
      throws CommonServiceException, JobSubmissionException, IOException, CsvException {
    when(configDataUploadService.processUploadConfigData(any(), any()))
        .thenReturn(testUtil.getGenericUploadResponse());
    ResponseEntity<BaseResponse<GenericUploadResponse>> res =
        configDataUploadController.uploadConfigData("node", testUtil.getGenericUploadRequest());
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res);
    Assertions.assertNotNull(res.getBody());
    Assertions.assertFalse(ObjectUtils.isEmpty(res.getBody().getMessage()));
    Assertions.assertEquals(TestUtil.ORG_ID, res.getBody().getPayload().getOrgId());
  }
}
