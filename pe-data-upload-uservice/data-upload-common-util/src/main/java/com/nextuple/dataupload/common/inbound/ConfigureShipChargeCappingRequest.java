/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.common.inbound;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfigureShipChargeCappingRequest implements Serializable {
  @Serial private static final long serialVersionUID = 6041673174393321568L;

  @NotNull(message = "shipChargeCappingConstantOne can't be null")
  @Schema(description = "Capping for first ship charge constant.", example = "20")
  private Integer shipChargeCappingConstantOne;

  @NotNull(message = "shipChargeCappingConstantTwo can't be null")
  @Schema(description = "Capping for second ship charge constant.", example = "20")
  private Integer shipChargeCappingConstantTwo;
}
