/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.master.data.integration;

import static org.mockito.ArgumentMatchers.any;

import com.nextuple.master.data.integration.constants.TaskConstants;
import com.nextuple.master.data.integration.enums.TaskInformation;
import com.nextuple.platform.tasks.pojo.CreateTaskReq;
import com.nextuple.platform.tasks.pojo.TaskCreateOrUpdateReq;
import com.nextuple.platform.tasks.service.TaskUtil;
import java.time.Instant;

public class TestUtil {

  public static final String ERROR_MESSAGE = "Error message";
  public static final String TENANT = "Nextuple_GR";
  public static final String TASK_TYPE = "NODE";
  public static final String METHOD_NAME = "makeNodeFeignCallBasedOnAction";
  public static final String CLASS_NAME = "NodeFeedService";
  public static final String EMPTY_STRING = "";

  public TaskCreateOrUpdateReq getTaskCreateOrUpdate(
      String errorMessage, TaskUtil.TaskStatus taskStatus) {
    TaskCreateOrUpdateReq taskCreateOrUpdateReq = new TaskCreateOrUpdateReq();
    taskCreateOrUpdateReq.setTaskType(TASK_TYPE);
    taskCreateOrUpdateReq.setMethodName(METHOD_NAME);
    taskCreateOrUpdateReq.setMinExecutionTime(Instant.now().plusMillis(20000));
    taskCreateOrUpdateReq.setLastExecutionTime(Instant.now().plusMillis(20000));
    taskCreateOrUpdateReq.setClassName(CLASS_NAME);
    taskCreateOrUpdateReq.setArgClassName(any());
    taskCreateOrUpdateReq.setMethodArg(any());
    taskCreateOrUpdateReq.setRetryCount(0);
    taskCreateOrUpdateReq.setTaskStatus(taskStatus);
    taskCreateOrUpdateReq.setFailureMsg(errorMessage);
    return taskCreateOrUpdateReq;
  }

  public CreateTaskReq getCreateTaskReqForRetry() {
    TaskInformation taskInfo = TaskInformation.NODE_FEED;
    return CreateTaskReq.builder()
        .taskType(taskInfo.getTaskType())
        .executionTime(Instant.now().plusMillis(20000))
        .methodName(TaskConstants.RETRY_METHOD_NAME)
        .param(EMPTY_STRING)
        .topicName(EMPTY_STRING)
        .className(CLASS_NAME)
        .lastException(new Throwable())
        .tenant(TENANT)
        .build();
  }
}
