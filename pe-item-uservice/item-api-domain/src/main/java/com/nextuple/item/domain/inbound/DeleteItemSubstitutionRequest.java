/*
 *  Copyright (c) 2025, Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 *  The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 *  The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.domain.inbound;

import com.nextuple.item.domain.constants.ItemSubstitutionConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DeleteItemSubstitutionRequest implements Serializable {
  @Serial private static final long serialVersionUID = 1905122041950251208L;

  @NotBlank(message = "Organisation ID can't be blank")
  @Schema(
      description = ItemSubstitutionConstants.ORG_ID,
      example = ItemSubstitutionConstants.ORG_ID_EXAMPLE)
  private String orgId;

  @NotBlank(message = "Primary Item ID can't be blank")
  @Schema(
      description = ItemSubstitutionConstants.PRIMARY_ITEM_ID,
      example = ItemSubstitutionConstants.PRIMARY_ITEM_ID_EXAMPLE)
  private String primaryItemId;

  @NotBlank(message = "Primary UOM can't be blank")
  @Schema(
      description = ItemSubstitutionConstants.PRIMARY_UOM,
      example = ItemSubstitutionConstants.PRIMARY_UOM_EXAMPLE)
  private String primaryUom;

  @NotBlank(message = "Alternate Item ID can't be blank")
  @Schema(
      description = ItemSubstitutionConstants.ALTERNATE_ITEM_ID,
      example = ItemSubstitutionConstants.ALTERNATE_ITEM_ID_EXAMPLE)
  private String alternateItemId;

  @NotBlank(message = "Alternate UOM can't be blank")
  @Schema(
      description = ItemSubstitutionConstants.ALTERNATE_UOM,
      example = ItemSubstitutionConstants.ALTERNATE_UOM_EXAMPLE)
  private String alternateUom;
}
