/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.domain.inbound;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.nextuple.node.carrier.domain.constants.NodeCarrierConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeServiceOptionBufferUpdateRequest implements Serializable {
  private static final long serialVersionUID = -5812318709620272913L;

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
}
