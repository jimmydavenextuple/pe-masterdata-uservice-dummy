/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.domain.dto;

import com.nextuple.dataupload.domain.pojo.ProcessingTimeDetails;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NodeListDto implements Serializable {

  private static final long serialVersionUID = -9030089295880579769L;

  private String nodeId;
  private String orgId;
  private String nodeType;
  private String street;
  private String city;
  private String state;
  private String zipCode;
  private String latitude;
  private String longitude;
  private String timezone;
  private Boolean isActive;
  private NodeWorkingCalendarDto nodeWorkingCalendar;
  private List<ProcessingTimeDetails> processingTimeDetails;
  private List<String> carrierServices;
  private List<String> serviceOptions;
  private List<PickupTimeDto> pickupTime;
  private String startWorkingTime;
  private String lastWorkingTime;
}
