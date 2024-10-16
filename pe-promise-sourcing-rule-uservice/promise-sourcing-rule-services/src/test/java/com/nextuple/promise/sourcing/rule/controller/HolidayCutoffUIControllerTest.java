/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffUIRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PageResponseForHolidayCutoff;
import com.nextuple.promise.sourcing.rule.service.HolidayCutoffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class HolidayCutoffUIControllerTest {

  @InjectMocks private HolidayCutoffUIController holidayCutoffUIController;

  @Mock private HolidayCutoffService holidayCutoffService;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("When get Holiday Cutoff Details is successful")
  void getHolidayCutoffDetailsTest() throws PromiseEngineException, CommonServiceException {
    HolidayCutoffUIRequest holidayCutoffUIRequest =
        HolidayCutoffUIRequest.builder().sourcingAttributesDefinitionId(1L).build();
    when(holidayCutoffService.getHolidayCutoffDetailsBasedOnFilters(
            anyString(), anyBoolean(), any(PageParams.class), any(HolidayCutoffUIRequest.class)))
        .thenReturn(testUtil.getPageResponseForHolidayCutoff());
    ResponseEntity<BaseResponse<PageResponseForHolidayCutoff>> response =
        holidayCutoffUIController.getHolidayCutoffDetails(
            TestUtil.ORG_ID, true, 1, 10, "ruleName", "ASC", holidayCutoffUIRequest);
    assertEquals(HttpStatus.valueOf(200), response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Holiday cutoff details fetched successfully", response.getBody().getMessage());
    assertEquals(testUtil.getPageResponseForHolidayCutoff(), response.getBody().getPayload());
    verify(holidayCutoffService, times(1))
        .getHolidayCutoffDetailsBasedOnFilters(
            anyString(), anyBoolean(), any(PageParams.class), any(HolidayCutoffUIRequest.class));
  }

  @Test
  @DisplayName("When  get Holiday Cutoff Details is not successful")
  void getHolidayCutoffDetailsException() throws PromiseEngineException, CommonServiceException {
    HolidayCutoffUIRequest holidayCutoffUIRequest =
        HolidayCutoffUIRequest.builder().sourcingAttributesDefinitionId(1L).build();
    when(holidayCutoffService.getHolidayCutoffDetailsBasedOnFilters(
            anyString(), anyBoolean(), any(PageParams.class), any(HolidayCutoffUIRequest.class)))
        .thenThrow(new RuntimeException("error"));
    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              holidayCutoffUIController.getHolidayCutoffDetails(
                  TestUtil.ORG_ID, true, 1, 10, "ruleName", "ASC", holidayCutoffUIRequest);
            });
    assertEquals("error", ex.getMessage());

    verify(holidayCutoffService, times(1))
        .getHolidayCutoffDetailsBasedOnFilters(
            anyString(), anyBoolean(), any(PageParams.class), any(HolidayCutoffUIRequest.class));
  }
}
