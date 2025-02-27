/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.configuration.outbound;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nextuple.common.pojo.AdditionalAttributes;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TenantConfigdataResponse extends AdditionalAttributes implements Serializable {
  private static final long serialVersionUID = 3195946904038241015L;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Schema(description = "Unique identifier for tenantConfigdata", example = "1")
  private Long id;

  @Schema(description = "Unique identification of an organization", example = "NEXTUPLE")
  private String orgId;

  @Schema(
      description = "Configuration key of the tenant-based configuration",
      example = "service-options")
  private String configKey;

  @Schema(
      description = "Configuration value of the tenant-based configuration",
      example = "SDND,EXPRESS,STANDARD")
  private String configValue;
}
