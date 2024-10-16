/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.service.validation;

import java.util.List;
import lombok.Data;

@Data
public class BucketRange {
  private Double fromValue;
  private Double toValue;
  private Boolean isFromValueInclusive;
  private Boolean isToValueInclusive;

  public BucketRange(
      Double fromValue,
      Double toValue,
      Boolean isFromValueInclusive,
      Boolean isToValueInclusive,
      List<BucketRange> ranges) {
    this.fromValue = fromValue;
    this.toValue = toValue;
    this.isFromValueInclusive = isFromValueInclusive;
    this.isToValueInclusive = isToValueInclusive;
    ranges.add(this);
  }
}
