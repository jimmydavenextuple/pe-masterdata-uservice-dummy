/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.weightage.configuration.domain.primaryKeys;

import static com.nextuple.weightage.configuration.utils.WeightageConfigurationConstants.KEY;
import static com.nextuple.weightage.configuration.utils.WeightageConfigurationConstants.ORG_ID;
import static com.nextuple.weightage.configuration.utils.WeightageConfigurationConstants.TYPE;
import static org.junit.jupiter.api.Assertions.*;

import com.nextuple.weightage.configuration.persistence.domain.key.WeightageConfigurationDomainKey;
import org.junit.jupiter.api.Test;

class WeightageConfigurationPKTest {

  @Test
  void weightageConfigurationPKNullTest() {
    WeightageConfigurationDomainKey weightageConfigurationPK =
        new WeightageConfigurationDomainKey();
    assertNull(weightageConfigurationPK.getOrgId());
    assertNull(weightageConfigurationPK.getType());
    assertNull(weightageConfigurationPK.getKey());
  }

  @Test
  void weightageConfigurationPKTest() {
    WeightageConfigurationDomainKey weightageConfigurationPK =
        new WeightageConfigurationDomainKey(ORG_ID, TYPE, KEY);
    assertNotNull(weightageConfigurationPK.getOrgId());
    assertNotNull(weightageConfigurationPK.getType());
    assertNotNull(weightageConfigurationPK.getKey());
  }
}
