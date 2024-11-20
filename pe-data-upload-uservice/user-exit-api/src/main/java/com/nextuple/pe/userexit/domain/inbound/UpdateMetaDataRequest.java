/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.pe.userexit.domain.inbound;

import com.nextuple.common.userexit.domain.enums.ExecutionFailureEnum;
import com.nextuple.common.userexit.domain.enums.UserExitTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMetaDataRequest implements Serializable {
  private static final long serialVersionUID = 3827542254836780787L;

  @Schema(description = "Short description of the user exit", example = "Store ranking user exit")
  private String description;

  @Schema(
      description = "Failure type specifying whether the request fails in case of exception",
      example = "HARD / SOFT")
  private ExecutionFailureEnum executionFailureType;

  @Schema(
      description = "User exit type specifying REGULAR or API user exit",
      example = "REGULAR / API")
  private UserExitTypeEnum type;

  @Schema(description = "Name of the pre user exit", example = "PreGetItemDetails")
  private String preUEName;

  @Schema(description = "Name of the post user exit", example = "PostGetItemDetails")
  private String postUEName;
}
