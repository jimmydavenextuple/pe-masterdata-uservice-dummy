/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.weightage.configuration.api.domain.inbound;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateWeightageConfigurationRequest implements Serializable {
  private static final long serialVersionUID = -2330679446163300146L;

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
      description = "Combination of the source, geozone, line, order and priority.",
      example = "ONON")
  @NotBlank(message = "key can't be empty.")
  private String key;

  @Schema(
      description =
          "Weightage is the score assigned to the weightage type of the configuration. The node is selected based on this weightage score.",
      example = "500")
  @NotNull(message = "weightage can't be null.")
  private Float weightage;
}
