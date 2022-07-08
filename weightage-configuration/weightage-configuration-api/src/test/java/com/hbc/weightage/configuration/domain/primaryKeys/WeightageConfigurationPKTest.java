package com.hbc.weightage.configuration.domain.primaryKeys;

import static com.hbc.weightage.configuration.utils.WeightageConfigurationConstants.KEY;
import static com.hbc.weightage.configuration.utils.WeightageConfigurationConstants.ORG_ID;
import static com.hbc.weightage.configuration.utils.WeightageConfigurationConstants.TYPE;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class WeightageConfigurationPKTest {

  @Test
  void weightageConfigurationPKNullTest() {
    WeightageConfigurationPK weightageConfigurationPK = new WeightageConfigurationPK();
    assertNull(weightageConfigurationPK.getOrgId());
    assertNull(weightageConfigurationPK.getType());
    assertNull(weightageConfigurationPK.getKey());
  }

  @Test
  void weightageConfigurationPKTest() {
    WeightageConfigurationPK weightageConfigurationPK =
        new WeightageConfigurationPK(ORG_ID, TYPE, KEY);
    assertNotNull(weightageConfigurationPK.getOrgId());
    assertNotNull(weightageConfigurationPK.getType());
    assertNotNull(weightageConfigurationPK.getKey());
  }
}
