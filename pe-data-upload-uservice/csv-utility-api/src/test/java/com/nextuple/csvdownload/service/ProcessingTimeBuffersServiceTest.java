/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.csvdownload.common.TestUtil;
import com.nextuple.dataupload.common.feign.DataUploadFeign;
import com.nextuple.dataupload.common.outbound.ProcessingTimeBufferResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ProcessingTimeBuffersServiceTest {

  @InjectMocks private ProcessingTimeBuffersService processingTimeBuffersService;

  @Mock private DataUploadFeign dataUploadFeign;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(processingTimeBuffersService, "pageSize", 1);
  }

  @Test
  void getProcessingTimeBuffersTest() {
    when(dataUploadFeign.getProcessingTimeBufferDetailsV1(any(), any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getBaseResponseOfProcessingTimeBuffers());

    List<ProcessingTimeBufferResponse> responseList =
        processingTimeBuffersService.getProcessingTimeBuffers(TestUtil.ORG_ID, TestUtil.NODE_IDS);

    assertEquals(4, responseList.size());
    assertEquals(TestUtil.ORG_ID, responseList.get(0).getOrgId());
    assertEquals(TestUtil.NODE_ID, responseList.get(0).getNodeId());
    verify(dataUploadFeign, times(2))
        .getProcessingTimeBufferDetailsV1(any(), any(), any(), any(), any(), any());
  }
}
