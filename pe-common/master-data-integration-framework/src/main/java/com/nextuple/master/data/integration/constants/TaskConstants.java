/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.master.data.integration.constants;

public class TaskConstants {
  public static final String CREATE = "CREATE";
  public static final String UPDATE = "UPDATE";
  public static final String DELETE = "DELETE";
  public static final String NODE = "NODE";
  public static final String CARRIER = "CARRIER";
  public static final String NODE_CARRIER = "NODE_CARRIER";
  public static final String NODE_SERVICE_OPTION_BUFFER = "NODE_SERVICE_OPTION_BUFFER";
  public static final String PROCESSING_LEAD_TIME = "PROCESSING_LEAD_TIME";
  public static final String CALENDAR = "CALENDAR";
  public static final String NODE_CALENDAR = "NODE_CALENDAR";
  public static final String PICKUP_CALENDAR = "PICKUP_CALENDAR";
  public static final String CARRIER_SERVICE_CALENDAR = "CARRIER_SERVICE_CALENDAR";
  public static final String TRANSIT = "TRANSIT";
  public static final String TRANSIT_BUFFER = "TRANSIT_BUFFER";
  public static final String TRANSFER_SCHEDULE = "TRANSFER_SCHEDULE";
  public static final String RETRY_METHOD_NAME = "handleRetry";

  private TaskConstants() {}
}
