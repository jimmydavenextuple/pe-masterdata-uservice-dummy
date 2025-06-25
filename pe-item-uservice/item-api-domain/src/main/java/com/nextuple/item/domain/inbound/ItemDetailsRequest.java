/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.domain.inbound;

import com.nextuple.item.domain.constants.ItemConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDetailsRequest implements Serializable {

  private static final long serialVersionUID = 1905122041950251297L;

  @NotEmpty
  @Schema(description = "List of the item IDs.", example = "ITEM-01,ITEM-02")
  private List<String> itemList;

  @NotBlank(message = "orgId can't be empty")
  @Schema(description = ItemConstants.ORG_ID, example = ItemConstants.ORG_ID_EXAMPLE)
  private String orgId;

  @Schema(description = "Flag indicating whether item buffer should be enabled.", example = "true")
  private Boolean isItemBufferEnabled;

  @Schema(
      description = "Date used for promising engine computation.",
      example = "2030-05-30T22:00:00Z")
  private Date promisingEngineDate;

  @Schema(description = "Map indicating whether item substitution flag for each item.")
  private Map<String, Boolean> itemSubstitutionMap;
}
