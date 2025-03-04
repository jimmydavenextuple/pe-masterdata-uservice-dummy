/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.domain.outbound;

import com.nextuple.common.pojo.AdditionalAttributes;
import com.nextuple.item.domain.constants.ItemConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ItemBufferResponse extends AdditionalAttributes implements Serializable {

  private static final long serialVersionUID = 4612221801177057344L;

  @Schema(description = ItemConstants.ID, example = ItemConstants.ID_EXAMPLE)
  private Long id;

  @Schema(description = ItemConstants.ORG_ID, example = ItemConstants.ORG_ID_EXAMPLE)
  private String orgId;

  @Schema(description = ItemConstants.ITEM_ID, example = ItemConstants.ITEM_ID_EXAMPLE)
  private String itemId;

  @Schema(description = ItemConstants.UOM, example = ItemConstants.UOM_EXAMPLE)
  private String uom;

  @Schema(description = ItemConstants.BUFFER_HOURS, example = ItemConstants.BUFFER_HOURS_EXAMPLE)
  private Double bufferHours;

  @Schema(
      description = ItemConstants.BUFFER_START_DATE,
      example = ItemConstants.BUFFER_START_DATE_EXAMPLE)
  private Date bufferStartDate;

  @Schema(
      description = ItemConstants.BUFFER_END_DATE,
      example = ItemConstants.BUFFER_END_DATE_EXAMPLE)
  private Date bufferEndDate;
}
