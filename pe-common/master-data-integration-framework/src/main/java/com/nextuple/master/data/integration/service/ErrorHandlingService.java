/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.master.data.integration.service;

import com.nextuple.master.data.integration.constants.TaskConstants;
import com.nextuple.master.data.integration.enums.TaskInformation;
import com.nextuple.platform.common.base.httpresponse.AppException;
import com.nextuple.platform.tasks.api.TaskApi;
import com.nextuple.platform.tasks.pojo.CreateTaskReq;
import com.nextuple.platform.tasks.pojo.TaskCreateOrUpdateReq;
import com.nextuple.platform.tasks.service.TaskHelperService;
import com.nextuple.platform.tasks.service.TaskProcessor;
import com.nextuple.platform.tasks.service.TaskUtil;
import com.nextuple.platform.tasks.service.TaskUtil.TaskStatus;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ErrorHandlingService {

  private final TaskApi taskApi;
  private final TaskProcessor taskProcessor;
  private final TaskHelperService taskHelperService;

  @Value("${kafka.input.taskTopics}")
  private String kafkaRetryTopic;

  public void handleExceptions(
      int statusCode,
      String errorMessage,
      Object request,
      TaskInformation taskInfo,
      String tenant,
      String className) {
    Instant executionTime = Instant.now().plusMillis(20000);
    if (statusCode == 404 || statusCode == 400 || statusCode == 403 || statusCode == 412)
      handleNonRetryableExceptions(
          request, errorMessage, tenant, taskInfo, className, executionTime);
    else
      handleRetryableExceptions(request, errorMessage, tenant, taskInfo, className, executionTime);
  }

  private void handleNonRetryableExceptions(
      Object request,
      String errorMessage,
      String tenant,
      TaskInformation taskInfo,
      String className,
      Instant executionTime) {
    TaskCreateOrUpdateReq taskCreateOrUpdateReq =
        getCreateTaskReqWithStatus(
            request, errorMessage, TaskStatus.FAILED, taskInfo, className, executionTime);
    taskProcessor.createTaskImpl(taskCreateOrUpdateReq, tenant);
  }

  public void handleSuccessResponse(
      Object request, String tenant, TaskInformation taskInfo, String className) {
    Instant executionTime = Instant.now().plusMillis(20000);
    TaskCreateOrUpdateReq taskCreateOrUpdateReq =
        getCreateTaskReqWithStatus(
            request, null, TaskStatus.COMPLETED, taskInfo, className, executionTime);
    taskProcessor.createTaskImpl(taskCreateOrUpdateReq, tenant);
  }

  private void handleRetryableExceptions(
      Object request,
      String errorMessage,
      String tenant,
      TaskInformation taskInfo,
      String className,
      Instant executionTime) {
    CreateTaskReq createTaskReq =
        getCreateTaskReqForRetry(request, errorMessage, tenant, taskInfo, className, executionTime);
    taskApi.addTaskForAsync(createTaskReq);
  }

  private CreateTaskReq getCreateTaskReqForRetry(
      Object request,
      String errorMessage,
      String tenant,
      TaskInformation taskInfo,
      String className,
      Instant executionTime) {
    return CreateTaskReq.builder()
        .taskType(taskInfo.getTaskType())
        .executionTime(executionTime)
        .methodName(TaskConstants.RETRY_METHOD_NAME)
        .param(request)
        .topicName(kafkaRetryTopic)
        .className(className)
        .lastException(new AppException(errorMessage))
        .tenant(tenant)
        .build();
  }

  private TaskCreateOrUpdateReq getCreateTaskReqWithStatus(
      Object request,
      String errorMessage,
      TaskUtil.TaskStatus taskStatus,
      TaskInformation taskInfo,
      String className,
      Instant executionTime) {
    TaskCreateOrUpdateReq taskCreateOrUpdateReq = new TaskCreateOrUpdateReq();
    taskCreateOrUpdateReq.setTaskType(taskInfo.getTaskType());
    taskCreateOrUpdateReq.setMethodName(TaskConstants.RETRY_METHOD_NAME);
    taskCreateOrUpdateReq.setMinExecutionTime(executionTime);
    taskCreateOrUpdateReq.setLastExecutionTime(executionTime);
    taskCreateOrUpdateReq.setClassName(className);
    taskCreateOrUpdateReq.setArgClassName(taskHelperService.getParamClassName(request));
    taskCreateOrUpdateReq.setMethodArg(taskHelperService.getParamString(request));
    taskCreateOrUpdateReq.setRetryCount(0);
    taskCreateOrUpdateReq.setTaskStatus(taskStatus);
    taskCreateOrUpdateReq.setFailureMsg(errorMessage);
    return taskCreateOrUpdateReq;
  }
}
