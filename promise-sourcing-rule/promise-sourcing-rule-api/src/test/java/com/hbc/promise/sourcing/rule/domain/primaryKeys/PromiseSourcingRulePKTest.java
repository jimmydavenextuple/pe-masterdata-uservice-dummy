package com.hbc.promise.sourcing.rule.domain.primaryKeys;

import static org.junit.jupiter.api.Assertions.*;

import com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants;
import org.junit.jupiter.api.Test;

class PromiseSourcingRulePKTest {

  @Test
  void promiseSourcingRulePKNullTest() {
    PromiseSourcingRulePK promiseSourcingRulePK = new PromiseSourcingRulePK();
    assertNull(promiseSourcingRulePK.getServiceOption());
    assertNull(promiseSourcingRulePK.getOrgId());
    assertNull(promiseSourcingRulePK.getDestinationGeoZone());
    assertNull(promiseSourcingRulePK.getAllocationRuleId());
  }

  @Test
  void promiseSourcingRulePKTest() {
    PromiseSourcingRulePK promiseSourcingRulePK =
        new PromiseSourcingRulePK(
            PromiseSourcingRuleConstants.ORG_ID,
            PromiseSourcingRuleConstants.SERVICE_OPTION,
            PromiseSourcingRuleConstants.DESTINATION_GEO_ZONE,
            PromiseSourcingRuleConstants.ALLOCATION_RULE_ID,
            PromiseSourcingRuleConstants.PRIORITY);
    assertNotNull(promiseSourcingRulePK.getServiceOption());
    assertNotNull(promiseSourcingRulePK.getOrgId());
    assertNotNull(promiseSourcingRulePK.getDestinationGeoZone());
    assertNotNull(promiseSourcingRulePK.getAllocationRuleId());
  }
}
