/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NodeCarrierCalendarDataUploadConstants {
  public static final String NODE_CARRIER_CALENDAR_DATA_UPLOAD_INVALID_FILE_TYPE =
      "Node Carrier Calendar data uploaded file is not a csv file.";
  public static final String NODE_CARRIER_CALENDAR_DATA_UPLOAD_FILE_EMPTY_RECORDS =
      "Node Carrier Calendar data uploaded file has no records.";
  public static final String NODE_CARRIER_CALENDAR_DATA_UPLOAD_LARGE_FILE_SIZE =
      "Node Carrier Calendar data uploaded file has size greater than 10240 kB.";
  public static final String NODE_CARRIER_CALENDAR_DATA_UPLOAD_LARGE_ROW_SIZE =
      "Node Carrier Calendar data uploaded file has exceeded maximum file size limit.";
  public static final String NODE_CARRIER_CALENDAR_DATA_UPLOAD_INVALID_FILE_HEADERS =
      "Node Carrier Calendar data uploaded file has invalid headers.";
}
