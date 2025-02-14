/*
 * Copyright (c) 2023., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import org.springframework.stereotype.Component;

@Component
public class GsonExclusionStrategy implements ExclusionStrategy {
  @Override
  public boolean shouldSkipField(FieldAttributes f) {
    return f.getAnnotation(GsonExclude.class) != null;
  }

  @Override
  public boolean shouldSkipClass(Class<?> clazz) {
    return false;
  }
}
