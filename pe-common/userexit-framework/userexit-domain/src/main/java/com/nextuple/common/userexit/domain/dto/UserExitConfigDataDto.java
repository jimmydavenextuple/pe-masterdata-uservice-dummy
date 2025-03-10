/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.common.userexit.domain.dto;

import com.nextuple.common.pojo.AdditionalAttributes;
import com.nextuple.common.userexit.domain.enums.UEImplTypeEnum;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserExitConfigDataDto extends AdditionalAttributes implements Serializable {
  private static final long serialVersionUID = -1836300941660960473L;

  private Long id;

  private String userExitName;

  private String appName;

  private String orgId;

  private String serviceName;

  private String url;

  private UEImplTypeEnum ueImplType;

  private String attributeJsonPath;

  private Boolean propagateError;
}
