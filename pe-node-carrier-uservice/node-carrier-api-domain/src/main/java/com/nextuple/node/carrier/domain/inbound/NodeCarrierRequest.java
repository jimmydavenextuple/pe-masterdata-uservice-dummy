/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.domain.inbound;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.nextuple.node.carrier.domain.constants.NodeCarrierConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeCarrierRequest implements Serializable {

  private static final long serialVersionUID = -5879215809620272913L;

  @Schema(
      description = NodeCarrierConstants.NODE_ID,
      example = NodeCarrierConstants.NODE_ID_EXAMPLE)
  @NotBlank(message = "nodeId cannot be empty")
  @Length(max = 50)
  private String nodeId;

  @Schema(description = NodeCarrierConstants.ORG_ID, example = NodeCarrierConstants.ORG_ID_EXAMPLE)
  @NotBlank(message = "orgId cannot be empty")
  @Length(max = 50)
  private String orgId;

  @Schema(
      description = NodeCarrierConstants.CARRIER_SERVICE_ID,
      example = NodeCarrierConstants.CARRIER_SERVICE_ID_EXAMPLE)
  @NotNull(message = "carrierServiceId cannot be null")
  @Length(max = 50)
  private String carrierServiceId;

  @Schema(
      description = NodeCarrierConstants.SERVICE_OPTION,
      example = NodeCarrierConstants.SERVICE_OPTION_EXAMPLE)
  @NotBlank(message = "serviceOption cannot be empty")
  @Length(max = 50)
  private String serviceOption;

  @Schema(
      description = NodeCarrierConstants.PROCESSING_TIME,
      example = NodeCarrierConstants.DOUBLE_EXAMPLE)
  private Double processingTime;

  @Schema(
      description = NodeCarrierConstants.PICKUP_TIME,
      example = NodeCarrierConstants.PICKUP_TIME_EX)
  @Length(max = 50)
  private String lastPickupTime;

  @Schema(
      description = NodeCarrierConstants.BUFFER_START_DATE,
      example = NodeCarrierConstants.DATE_EXAMPLE)
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", lenient = OptBoolean.FALSE)
  private Date bufferStartDate;

  @Schema(
      description = NodeCarrierConstants.BUFFER_END_DATE,
      example = NodeCarrierConstants.DATE_EXAMPLE)
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", lenient = OptBoolean.FALSE)
  private Date bufferEndDate;

  @Schema(
      description = NodeCarrierConstants.BUFFER_HOURS,
      example = NodeCarrierConstants.DOUBLE_EXAMPLE)
  private Double bufferHours;

  @Schema(
      description = NodeCarrierConstants.PICKUP_CALENDAR_ID,
      example = NodeCarrierConstants.PICKUP_CALENDAR_ID_EXAMPLE)
  private String pickupCalendarId;
}
