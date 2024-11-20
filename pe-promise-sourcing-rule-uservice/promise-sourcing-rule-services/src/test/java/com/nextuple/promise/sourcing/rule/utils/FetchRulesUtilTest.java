/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.utils;

import static com.nextuple.promise.sourcing.rule.utils.FetchRulesUtil.REQUIRED_ATTRIBUTES_LENGTH_ERROR_MESSAGE;
import static com.nextuple.promise.sourcing.rule.utils.FetchRulesUtil.TOTAL_ATTRIBUTES_LENGTH_ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nextuple.common.exception.CommonServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

class FetchRulesUtilTest {

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Validation for attribute definition: Happy Path")
  void validateAttributesDefinitionIdForScopeTest() throws CommonServiceException {
    String reqAttrFromDefinition = "23,24";
    String optionalAttrFromDefinition = "25";
    String generatedRule = "ABC:DEF:GHI";

    FetchRulesUtil.validateAttributesDefinitionIdForScope(
        reqAttrFromDefinition, optionalAttrFromDefinition, generatedRule);

    assertDoesNotThrow(
        () ->
            FetchRulesUtil.validateAttributesDefinitionIdForScope(
                reqAttrFromDefinition, optionalAttrFromDefinition, generatedRule));
  }

  @Test
  @DisplayName("Validation for attribute definition: Exception Path - Extra required attributes")
  void validateAttributesDefinitionIdForScopeExceptionTest() {
    String reqAttrFromDefinition = "23,34";
    String optionalAttrFromDefinition = "25";
    String generatedRule = "ABC::";

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                FetchRulesUtil.validateAttributesDefinitionIdForScope(
                    reqAttrFromDefinition, optionalAttrFromDefinition, generatedRule));

    assertEquals(REQUIRED_ATTRIBUTES_LENGTH_ERROR_MESSAGE, ex.getMessage());
  }

  @Test
  @DisplayName("Validation for attribute definition: Exception Path - Extra total attributes")
  void validateAttributesDefinitionIdForScopeExceptionExtraAttributesTest() {
    String reqAttrFromDefinition = "23:24";
    String optionalAttrFromDefinition = "25:26";
    String generatedRule = "ABC:DEF:GHI";

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                FetchRulesUtil.validateAttributesDefinitionIdForScope(
                    reqAttrFromDefinition, optionalAttrFromDefinition, generatedRule));

    assertEquals(TOTAL_ATTRIBUTES_LENGTH_ERROR_MESSAGE, ex.getMessage());
  }
}
