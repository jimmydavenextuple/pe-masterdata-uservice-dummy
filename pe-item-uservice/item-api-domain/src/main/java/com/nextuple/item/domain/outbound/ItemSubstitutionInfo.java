/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.domain.outbound;

import com.nextuple.item.domain.constants.ItemConstants;
import com.nextuple.item.domain.constants.ItemSubstitutionConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ItemSubstitutionInfo implements Serializable {
  private static final long serialVersionUID = 1905122041950253207L;

  @Schema(description = ItemConstants.ITEM_ID, example = ItemConstants.ITEM_ID_EXAMPLE)
  private String itemId;

  @Schema(description = ItemConstants.UOM, example = ItemConstants.UOM_EXAMPLE)
  private String uom;

  @Schema(
      description = ItemSubstitutionConstants.CONVERSION_FACTOR,
      example = ItemSubstitutionConstants.CONVERSION_FACTOR_EXAMPLE)
  private Integer conversionFactor;

  @Schema(
      description = ItemSubstitutionConstants.PRIORITY,
      example = ItemSubstitutionConstants.PRIORITY_EXAMPLE)
  private Integer priority;
}
