/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.domain.outbound;

import com.nextuple.node.carrier.domain.constants.NodeCarrierConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NodeServiceOptionResponse implements Serializable {

  private static final long serialVersionUID = 3329653563814817521L;

  @Schema(
      description = NodeCarrierConstants.NODE_ID,
      example = NodeCarrierConstants.NODE_ID_EXAMPLE)
  private String nodeId;

  @Schema(description = NodeCarrierConstants.ORG_ID, example = NodeCarrierConstants.ORG_ID_EXAMPLE)
  private String orgId;

  @Schema(
      description = NodeCarrierConstants.SERVICE_OPTION,
      example = NodeCarrierConstants.SERVICE_OPTION_EXAMPLE)
  private String serviceOption;

  @Schema(
      description = NodeCarrierConstants.PROCESSING_TIME,
      example = NodeCarrierConstants.PROCESSING_TIME_EXAMPLE)
  private double processingTime;
}
