/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.sourcing.cost.config.cache.domain;

import com.nextuple.core.cache.domain.CacheValue;
import com.nextuple.sourcing.cost.config.enums.CostFactorTypeEnum;
import com.nextuple.sourcing.cost.config.enums.DataTypeEnum;
import com.nextuple.sourcing.cost.config.enums.ExpressionLibraryEnum;
import com.nextuple.sourcing.cost.config.enums.LevelAppliedEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CostFactorCacheValue implements CacheValue {

  private Long id;

  private String orgId;

  private String costFactor;

  private DataTypeEnum dataType;

  private String formula;

  private ExpressionLibraryEnum library;

  private CostFactorTypeEnum costFactorType;

  private String displayName;

  private String values;

  private String defaultValue;

  private LevelAppliedEnum levelApplied;

  private String uom;

  private Boolean isBucketed;
  private Boolean isRateCardLookUpRequired;

  @Override
  public boolean isUndefined() {
    return false;
  }
}
