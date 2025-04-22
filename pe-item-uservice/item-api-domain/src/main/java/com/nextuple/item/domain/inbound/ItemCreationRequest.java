/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.domain.inbound;

import com.nextuple.item.domain.constants.ItemConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.joda.time.DateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ItemCreationRequest extends ItemBaseRequest implements Serializable {

  private static final long serialVersionUID = 1905122041950251207L;

  @NotBlank(message = "itemId can't be blank")
  @Schema(description = ItemConstants.ITEM_ID, example = ItemConstants.ITEM_ID_EXAMPLE)
  private String itemId;

  @NotBlank(message = "orgId can't be blank")
  @Schema(description = ItemConstants.ORG_ID, example = ItemConstants.ORG_ID_EXAMPLE)
  private String orgId;

  @NotBlank(message = "uom can't be blank")
  @Schema(description = ItemConstants.UOM, example = ItemConstants.UOM_EXAMPLE)
  private String uom;

  @Schema(description = ItemConstants.IS_DSV_ELIGIBLE, example = ItemConstants.BOOL_EXAMPLE)
  private Boolean isDSVEligible;

  @Schema(description = "Date at which item was last modified")
  private DateTime lastModifiedDate;
}
