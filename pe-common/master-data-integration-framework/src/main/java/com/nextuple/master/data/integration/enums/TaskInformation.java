/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.master.data.integration.enums;

import com.nextuple.master.data.integration.constants.TaskConstants;

public enum TaskInformation {
  NODE_FEED(TaskConstants.NODE),
  VENDOR_FEED(TaskConstants.VENDOR),
  CARRIER_FEED(TaskConstants.CARRIER),
  NODE_CARRIER_FEED(TaskConstants.NODE_CARRIER),
  NODE_SERVICE_OPTION_BUFFER_FEED(TaskConstants.NODE_SERVICE_OPTION_BUFFER),
  PROCESSING_LEAD_TIME(TaskConstants.PROCESSING_LEAD_TIME),
  CALENDAR_FEED(TaskConstants.CALENDAR),
  NODE_CALENDAR_FEED(TaskConstants.NODE_CALENDAR),
  CARRIER_SERVICE_CALENDAR_FEED(TaskConstants.CARRIER_SERVICE_CALENDAR),
  PICKUP_CALENDAR_FEED(TaskConstants.PICKUP_CALENDAR),
  TRANSIT_FEED(TaskConstants.TRANSIT),
  TRANSIT_BUFFER_FEED(TaskConstants.TRANSIT_BUFFER),
  TRANSFER_SCHEDULE(TaskConstants.TRANSFER_SCHEDULE);
  private final String taskType;

  TaskInformation(String taskType) {
    this.taskType = taskType;
  }

  public String getTaskType() {
    return taskType;
  }
}
