/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.api.domain.outbound;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nextuple.common.pojo.AdditionalAttributes;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CustomRegionResponse extends AdditionalAttributes implements Serializable {

  private static final long serialVersionUID = -6839410089923170099L;

  @Schema(description = "Unique identifier of the region.", example = "CRID1")
  private String id;

  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE_GR")
  private String orgId;

  @Schema(
      description = "Array of the zip code prefixes in a custom region.",
      example = "['T2P','T3P']")
  private List<String> codes;

  @Schema(description = "Name of the custom region.", example = "CR1")
  private String customRegionName;

  @Schema(description = "Description of the custom region.", example = "Boston Metro Region")
  private String customRegionDescription;
}
