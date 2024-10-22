/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.master.data.integration.service;

import static com.nextuple.platform.tasks.service.TaskUtil.TaskStatus.COMPLETED;
import static com.nextuple.platform.tasks.service.TaskUtil.TaskStatus.FAILED;
import static org.mockito.Mockito.*;

import com.nextuple.master.data.integration.TestUtil;
import com.nextuple.master.data.integration.enums.TaskInformation;
import com.nextuple.platform.tasks.api.TaskApi;
import com.nextuple.platform.tasks.pojo.CreateTaskReq;
import com.nextuple.platform.tasks.pojo.TaskCreateOrUpdateReq;
import com.nextuple.platform.tasks.service.TaskHelperService;
import com.nextuple.platform.tasks.service.TaskProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ErrorHandlingServiceTest {
  @Mock TaskApi taskApi;
  @Mock private TaskProcessor taskProcessor;
  @Mock private TaskHelperService taskHelperService;
  @InjectMocks private TestUtil testUtil;
  @InjectMocks private ErrorHandlingService exceptionHandlingService;

  @ParameterizedTest
  @ValueSource(ints = {400, 404, 412, 403})
  void handleExceptionsTestForNonRetryableErrors(int errorCode) {
    ReflectionTestUtils.setField(exceptionHandlingService, "kafkaRetryTopic", "nodeFeed");
    Object request = new Object();
    TaskInformation taskInfo = TaskInformation.NODE_FEED;
    TaskCreateOrUpdateReq taskCreateOrUpdateReq =
        testUtil.getTaskCreateOrUpdate(TestUtil.ERROR_MESSAGE, FAILED);
    when(taskProcessor.createTaskImpl(taskCreateOrUpdateReq, TestUtil.TENANT)).thenReturn(null);
    exceptionHandlingService.handleExceptions(
        errorCode, TestUtil.ERROR_MESSAGE, request, taskInfo, TestUtil.TENANT, TestUtil.CLASS_NAME);
    verify(taskProcessor, times(1)).createTaskImpl(any(), any());
  }

  @Test
  void handleRetryableExceptionsTest() {
    ReflectionTestUtils.setField(exceptionHandlingService, "kafkaRetryTopic", "nodeFeed");
    Object request = new Object();
    TaskInformation taskInfo = TaskInformation.NODE_FEED;
    CreateTaskReq createTaskReq = testUtil.getCreateTaskReqForRetry();
    exceptionHandlingService.handleExceptions(
        500, TestUtil.ERROR_MESSAGE, request, taskInfo, TestUtil.TENANT, TestUtil.CLASS_NAME);
    verify(taskProcessor, times(0)).createTaskImpl(any(), any());
    verify(taskApi, times(1)).addTaskForAsync(any());
  }

  @Test
  void handleSuccessResponseTest() {
    Object request = new Object();
    TaskInformation taskInfo = TaskInformation.NODE_FEED;
    TaskCreateOrUpdateReq taskCreateOrUpdateReq = testUtil.getTaskCreateOrUpdate(null, COMPLETED);
    when(taskProcessor.createTaskImpl(taskCreateOrUpdateReq, TestUtil.TENANT)).thenReturn(null);
    exceptionHandlingService.handleSuccessResponse(
        request, TestUtil.TENANT, taskInfo, TestUtil.CLASS_NAME);
    verify(taskProcessor, times(1)).createTaskImpl(any(), any());
  }
}
