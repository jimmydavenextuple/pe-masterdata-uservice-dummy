/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.weightage.configuration.utils;

import java.util.Collections;
import java.util.List;

public class WeightageConfigurationConstants {
  public static final List<String> KEYS = Collections.singletonList("P1");
  public static final String ORG_ID = "ABC";
  public static final String TYPE = "PRIORITY";
  public static final String AVAILABILITY = "AVAILABILITY";
  public static final String STATUS_CODE = "Status code";
  public static final Float WEIGHTAGE = 100F;
  public static final String KEY = "KEY";
  public static final String WEIGHTAGE_CONFIGURATION_SUCCESSFULLY_CREATED =
      "Weightage Configuration successfully created!";
  public static final String WEIGHTAGE_CONFIGURATION_SUCCESSFULLY_FETCHED =
      "Weightage Configuration successfully fetched!";
  public static final String WEIGHTAGE_CONFIGURATION_SUCCESSFULLY_UPDATED =
      "Weightage Configuration successfully updated!";
  public static final String WEIGHTAGE_CONFIGURATION_SUCCESSFULLY_DELETED =
      "Weightage Configuration successfully deleted!";

  WeightageConfigurationConstants() {}
}
