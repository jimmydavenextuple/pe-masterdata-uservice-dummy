/*
 *  Copyright (c) 2025, Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 *  The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 *  The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.domain.constants;

public class ItemSubstitutionConstants {

  private ItemSubstitutionConstants() {} // NOSONAR

  public static final String ORG_ID = "Organisation ID";
  public static final String ORG_ID_EXAMPLE = "NEXTUPLE";

  public static final String PRIMARY_ITEM_ID = "Primary Item ID";
  public static final String PRIMARY_ITEM_ID_EXAMPLE = "ITEM12345";

  public static final String PRIMARY_UOM = "Primary Unit of Measure";
  public static final String PRIMARY_UOM_EXAMPLE = "EACH";

  public static final String ALTERNATE_ITEM_ID = "Alternate Item ID";
  public static final String ALTERNATE_ITEM_ID_EXAMPLE = "ALT-ITEM-456";

  public static final String ALTERNATE_UOM = "Alternate Unit of Measure";
  public static final String ALTERNATE_UOM_EXAMPLE = "CASE";

  public static final String CONVERSION_FACTOR =
      "Conversion Factor between Primary and Alternate items";
  public static final String CONVERSION_FACTOR_EXAMPLE = "12";

  public static final String PRIORITY = "Priority of the substitution";
  public static final String PRIORITY_EXAMPLE = "1";
  public static final String ITEM_SUBSTITUTION_RESPONSE =
      "Specifies the item substitution details.";
  public static final String ITEM_SUBSTITUTION_RESPONSE_EXAMPLE =
      "{\"item01\":true,\"item02\":\"false\",\"item03\":\"true\"}";
}
