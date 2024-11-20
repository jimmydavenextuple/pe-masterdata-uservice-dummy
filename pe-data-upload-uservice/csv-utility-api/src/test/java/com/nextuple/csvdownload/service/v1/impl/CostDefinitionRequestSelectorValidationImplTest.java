/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service.v1.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.csvdownload.common.TestUtil;
import com.nextuple.sourcing.cost.config.dto.FilterCostFactorInfoDto;
import com.nextuple.sourcing.cost.config.inbound.CostDefinitionRequest;
import com.nextuple.sourcing.cost.config.outbound.CostTypeValidationResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class CostDefinitionRequestSelectorValidationImplTest {
  @InjectMocks CostDefinitionRequestSelectorValidationImpl costDefinitionRequestSelectorValidation;
  @InjectMocks private TestUtil testUtil;

  @Test
  @DisplayName("Validate cost definition request: Happy Path")
  void validateCostDefinitionRequestHappyPathTest() {
    CostDefinitionRequest request = testUtil.getGridRequest();
    CostTypeValidationResponse response = testUtil.getCostTypeValidationResponse();
    assertDoesNotThrow(
        () ->
            costDefinitionRequestSelectorValidation.validateCostDefinitionRequest(
                request, response));
  }

  @Test
  @DisplayName("Validate cost definition request: Invalid selectors")
  void getCostDefinitionResponseExceptionInvalidSelectorTest() {

    CostTypeValidationResponse response = testUtil.getCostTypeValidationResponse();
    CostDefinitionRequest request1 = testUtil.getGridRequest();
    request1.getSelector().setSelectorCf("invalidSelector");
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              costDefinitionRequestSelectorValidation.validateCostDefinitionRequest(
                  request1, response);
            });
    assertTrue(ex.getMessage().contains("Invalid selectorCF for cost type"));
    assertEquals("invalidSelector", ex.getFieldInfo().get("selectorCf").getRejectedValue());
    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());

    CostDefinitionRequest request2 = testUtil.getGridRequest();
    request2.getSelector().setSelectorCfValue("invalid-selector-value");
    ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              costDefinitionRequestSelectorValidation.validateCostDefinitionRequest(
                  request2, response);
            });
    assertTrue(ex.getMessage().contains("Invalid selectorCFValue for selectorCf"));
    assertEquals(
        "invalid-selector-value", ex.getFieldInfo().get("selectorCfValue").getRejectedValue());
    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
  }

  @Test
  @DisplayName("Validate cost definition request: Invalid row when selector is present")
  void validateCostDefinitionRequestExceptionWhenInvalidRowWithSelectorTest() {
    CostDefinitionRequest request = testUtil.getGridRequest();
    CostTypeValidationResponse response = testUtil.getCostTypeValidationResponse();
    request.setRow("invalidRowCF");
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              costDefinitionRequestSelectorValidation.validateCostDefinitionRequest(
                  request, response);
            });
    assertTrue(ex.getMessage().contains("Invalid cost factor"));
    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
    assertEquals("invalidRowCF", ex.getFieldInfo().get("row").getRejectedValue());
  }

  @Test
  @DisplayName("Validate cost definition request: Invalid filter with selector")
  void validateCostDefinitionRequestExceptionWhenInvalidFilterWithSelectorTest() {
    CostDefinitionRequest request = testUtil.getGridRequest();
    CostTypeValidationResponse response = testUtil.getCostTypeValidationResponse();
    request.setFilters(
        List.of(
            FilterCostFactorInfoDto.builder()
                .costFactor("surge")
                .costFactorValue("NON-HOLIDAY")
                .build(),
            FilterCostFactorInfoDto.builder()
                .costFactor("invalid-filter")
                .costFactorValue("invalid-filter-value")
                .build()));
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              costDefinitionRequestSelectorValidation.validateCostDefinitionRequest(
                  request, response);
            });
    assertTrue(ex.getMessage().contains("Invalid filter or filter values"));
    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
  }

  @Test
  @DisplayName("Get Itinerary")
  void getCostItineraryTest() {
    assertEquals(
        "UPS-ITN",
        costDefinitionRequestSelectorValidation
            .getCostItinerary(testUtil.getGridRequest(), testUtil.getCostTypeValidationResponse())
            .get());
  }
}
