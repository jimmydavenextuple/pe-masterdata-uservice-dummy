/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.domain.outbound;

import com.nextuple.common.pojo.AdditionalAttributes;
import com.nextuple.node.carrier.domain.constants.NodeCarrierConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class NodeCarrierSelectionResponse extends AdditionalAttributes implements Serializable {

  private static final long serialVersionUID = -8403080247082341161L;

  @Schema(description = NodeCarrierConstants.ORG_ID, example = NodeCarrierConstants.ORG_ID_EXAMPLE)
  private String orgId;

  @Schema(
      description = NodeCarrierConstants.SERVICE_OPTION,
      example = NodeCarrierConstants.SERVICE_OPTION_EXAMPLE)
  private String serviceOption;

  @Schema(
      description = NodeCarrierConstants.SRC_GEOZONE,
      example = NodeCarrierConstants.GEOZONE_EXAMPLE)
  private String sourceGeozone;

  @Schema(
      description = NodeCarrierConstants.DEST_GEOZONE,
      example = NodeCarrierConstants.GEOZONE_EXAMPLE)
  private String destinationGeozone;

  @Schema(description = "Priority", example = "0")
  private String priority;
}
