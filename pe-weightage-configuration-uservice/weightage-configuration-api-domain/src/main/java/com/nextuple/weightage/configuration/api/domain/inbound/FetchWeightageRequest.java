/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.weightage.configuration.api.domain.inbound;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class FetchWeightageRequest implements Serializable {
  private static final long serialVersionUID = -3470182101752563409L;

  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  @NotBlank(message = "orgId can't be empty.")
  private String orgId;

  @Schema(
      description =
          "Weightage type of the configuration. Weightage configuration could be AVAILABILITY, PRIORITY or PROXIMITY.",
      example = "PROXIMITY")
  @NotBlank(message = "type can't be empty.")
  private String type;

  @Schema(
      description = "List of keys. Keys are based on the configuration type.",
      example = "[\"ONON\"]")
  @NotEmpty(message = "keys can't be empty.")
  private List<String> keys;
}
