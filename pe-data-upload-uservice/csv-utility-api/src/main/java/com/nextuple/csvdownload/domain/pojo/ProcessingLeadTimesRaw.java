/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.domain.pojo;

import static com.nextuple.csvdownload.common.constants.CSVCommonConstants.ACTION_TYPE;
import static com.nextuple.csvdownload.common.constants.CSVCommonConstants.NODE_ID;
import static com.nextuple.csvdownload.common.constants.CSVCommonConstants.ORG_ID;
import static com.nextuple.csvdownload.common.constants.CSVCommonConstants.PROCESSING_TIME;
import static com.nextuple.csvdownload.common.constants.CSVCommonConstants.SERVICE_OPTIONS;

import com.opencsv.bean.CsvBindByName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessingLeadTimesRaw {
  @CsvBindByName(column = ACTION_TYPE)
  @NotEmpty
  @NotBlank(message = "action can not be empty")
  private String actionType;

  @CsvBindByName(column = NODE_ID)
  private String nodeId;

  @CsvBindByName(column = ORG_ID)
  private String orgId;

  @CsvBindByName(column = SERVICE_OPTIONS)
  private String serviceOption;

  @CsvBindByName(column = PROCESSING_TIME)
  private String processingTime;

  private String carrierServiceId = "";

  public static String[] columnHeadersArray() {
    return new String[] {ACTION_TYPE, NODE_ID, ORG_ID, SERVICE_OPTIONS, PROCESSING_TIME};
  }
}
