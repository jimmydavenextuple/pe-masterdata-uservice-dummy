/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.configuration.outbound;

import com.nextuple.common.pojo.AdditionalAttributes;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigMetadataResponse extends AdditionalAttributes implements Serializable {
  private static final long serialVersionUID = -9189650528221359798L;

  @Schema(description = "Unique identifier for configMetadata", example = "1")
  private Long id;

  @Schema(description = "Name of the application or service", example = "PE")
  private String appName;

  @Schema(
      description = "Configuration key of the tenant-based configuration",
      example = "service-options")
  private String configKey;

  @Schema(description = "Default value for cache key", example = "SDND,EXPRESS,STANDARD")
  private String defaultConfigValue;
}
