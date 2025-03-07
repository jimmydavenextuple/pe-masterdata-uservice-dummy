/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.pe.userexit.domain.outbound;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nextuple.common.pojo.AdditionalAttributes;
import com.nextuple.common.userexit.domain.enums.UEImplTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ConfigDataResponse extends AdditionalAttributes implements Serializable {
  private static final long serialVersionUID = -1411471420533799037L;

  @Schema(description = "Unique id")
  private Long id;

  @NotBlank(message = "userExitName cannot be blank")
  @Schema(description = "Unique name for the user exit.", example = "GetItemDetails")
  private String userExitName;

  @NotBlank(message = "appName cannot be blank")
  @Schema(description = "Unique name for the app.", example = "PE")
  private String appName;

  @NotBlank(message = "orgId cannot be blank")
  @Schema(description = "Unique identifier for organisation.", example = "NEXTUPLE")
  private String orgId;

  @NotBlank(message = "serviceName cannot be blank")
  @Schema(description = "Unique name for the service.", example = "ItemService")
  private String serviceName;

  @Schema(
      description = "URL of the user exit logic.",
      example = "com.nextuple.promise.item.ItemMockProvider")
  private String url;

  @Schema(description = "Type of user exit implementation.", example = "REST/MOCK")
  private UEImplTypeEnum ueImplType;

  @Schema(
      description =
          "Comma-separated pairs of attributes required from the input and their corresponding JSON path expressions.",
      example =
          "serviceOption:$.serviceOption,orgId:$.orgId,uom:$.orderLines[0].item.unitOfMeasure,lineId:$.orderLines[0].lineId,sessionId:$.sessionId,pageName:$.pageName")
  private String attributeJsonPath;

  @Schema(
      description = "Whether the error in user exit response should be bubbled up",
      example = "true/false")
  private Boolean propagateError;
}
