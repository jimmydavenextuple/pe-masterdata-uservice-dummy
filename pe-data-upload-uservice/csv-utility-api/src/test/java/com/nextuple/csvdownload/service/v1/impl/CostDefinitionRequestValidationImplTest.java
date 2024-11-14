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
public class CostDefinitionRequestValidationImplTest {
  @InjectMocks CostDefinitionRequestValidationImpl costDefinitionRequestValidation;
  @InjectMocks private TestUtil testUtil;

  @Test
  @DisplayName("Validate cost definition request: Happy Path")
  void validateCostDefinitionRequestHappyPathTest() {
    CostDefinitionRequest request = testUtil.getGridRequestWithoutSelector();
    CostTypeValidationResponse response = testUtil.getCostTypeValidationResponseWithoutSelector();
    assertDoesNotThrow(
        () -> costDefinitionRequestValidation.validateCostDefinitionRequest(request, response));
  }

  @Test
  @DisplayName("Validate cost definition request: Invalid row without selector")
  void validateCostDefinitionRequestExceptionWhenInvalidRowWithoutSelectorTest() {
    CostDefinitionRequest request = testUtil.getGridRequestWithoutSelector();
    CostTypeValidationResponse response = testUtil.getCostTypeValidationResponseWithoutSelector();
    request.setRow("invalidRowCF");
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              costDefinitionRequestValidation.validateCostDefinitionRequest(request, response);
            });
    assertTrue(ex.getMessage().contains("Invalid cost factor"));
    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
    assertEquals("invalidRowCF", ex.getFieldInfo().get("row").getRejectedValue());
  }

  @Test
  @DisplayName("Validate cost definition request: Invalid filter without selector")
  void validateCostDefinitionRequestExceptionWhenInvalidFilterWithoutSelectorTest() {
    CostTypeValidationResponse response = testUtil.getCostTypeValidationResponseWithoutSelector();
    CostDefinitionRequest request = testUtil.getGridRequestWithoutSelector();
    request.setFilters(
        List.of(
            FilterCostFactorInfoDto.builder()
                .costFactor("surge")
                .costFactorValue("WEEKDAY")
                .build(),
            FilterCostFactorInfoDto.builder()
                .costFactor("invalid-filter")
                .costFactorValue("invalid-filter-value")
                .build()));
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              costDefinitionRequestValidation.validateCostDefinitionRequest(request, response);
            });
    assertTrue(ex.getMessage().contains("Invalid filter or filter values"));
    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
  }

  @Test
  @DisplayName("Get Itinerary")
  void getCostItineraryTest() {
    assertEquals(
        "DEFAULT-ITN",
        costDefinitionRequestValidation
            .getCostItinerary(
                testUtil.getGridRequestWithoutSelector(),
                testUtil.getCostTypeValidationResponseWithoutSelector())
            .get());
  }
}
