/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.controller;

import static com.nextuple.promise.sourcing.rule.TestUtil.HOLIDAY_CUTOFF_NAME;
import static com.nextuple.promise.sourcing.rule.TestUtil.HOLIDAY_CUTOFF_RULE;
import static com.nextuple.promise.sourcing.rule.TestUtil.ORG_ID_1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffRulesRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffUpdateRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.HolidayCutoffRulesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.HolidayCutoffInfo;
import com.nextuple.promise.sourcing.rule.service.HolidayCutoffService;
import java.text.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class HolidayCutoffControllerTest {
  @InjectMocks private HolidayCutoffController holidayCutoffController;
  @Mock private HolidayCutoffService holidayCutoffService;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("When creation of a holiday cutoff is successful")
  void createHolidayCutoffTest()
      throws PromiseEngineException, CommonServiceException, ParseException {
    HolidayCutoffRequest request = testUtil.createHolidayCutoffRequest();
    when(holidayCutoffService.createHolidayCutoff(any()))
        .thenReturn(testUtil.createHolidayCutoffInfo());
    ResponseEntity<BaseResponse<HolidayCutoffInfo>> response =
        holidayCutoffController.createHolidayCutoff(request);
    assertEquals(HttpStatus.valueOf(200), response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(
        "Holiday cutoff override data created successfully!", response.getBody().getMessage());
    assertEquals(testUtil.createHolidayCutoffInfo(), response.getBody().getPayload());
    verify(holidayCutoffService, times(1)).createHolidayCutoff(any(HolidayCutoffRequest.class));
  }

  @Test
  @DisplayName("When creation of holiday cutoff is not successful")
  void createHolidayCutoffWithException()
      throws PromiseEngineException, CommonServiceException, ParseException {
    when(holidayCutoffService.createHolidayCutoff(any())).thenThrow(new RuntimeException("error"));
    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              holidayCutoffController.createHolidayCutoff(testUtil.createHolidayCutoffRequest());
            });
    assertEquals("error", ex.getMessage());

    verify(holidayCutoffService, times(1)).createHolidayCutoff(any(HolidayCutoffRequest.class));
  }

  @Test
  @DisplayName("When fetching of holiday cutoff rules is successful")
  void fetchHolidayCutoffRules() throws PromiseEngineException, CommonServiceException {
    HolidayCutoffRulesRequest request = testUtil.getHolidayCutoffRulesRequest();
    when(holidayCutoffService.processFetchHolidayCutoffRules(any()))
        .thenReturn(testUtil.getHolidayCutoffRulesResponse());
    ResponseEntity<BaseResponse<HolidayCutoffRulesResponse>> response =
        holidayCutoffController.fetchHolidayCutoffRules(request);
    assertEquals(HttpStatus.valueOf(200), response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Holiday cutoff rules fetched successfully!", response.getBody().getMessage());
    assertEquals(
        testUtil.createHolidayCutoffInfo(),
        response.getBody().getPayload().getHolidayCutoffInfo().get(0));
    verify(holidayCutoffService, times(1))
        .processFetchHolidayCutoffRules(any(HolidayCutoffRulesRequest.class));
  }

  @Test
  @DisplayName("When updation of a holiday cutoff is successful")
  void updateHolidayCutoffTest()
      throws PromiseEngineException, CommonServiceException, ParseException {
    HolidayCutoffUpdateRequest request = testUtil.updateHolidayCutoffRequest();
    when(holidayCutoffService.updateHolidayCutoff(any(), any(), any(), any()))
        .thenReturn(testUtil.createHolidayCutoffInfo());
    ResponseEntity<BaseResponse<HolidayCutoffInfo>> response =
        holidayCutoffController.updateHolidayCutoff(
            ORG_ID_1, HOLIDAY_CUTOFF_NAME, HOLIDAY_CUTOFF_RULE, request);
    assertEquals(HttpStatus.valueOf(200), response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(
        "Holiday cutoff override data updated successfully!", response.getBody().getMessage());
    verify(holidayCutoffService, times(1))
        .updateHolidayCutoff(
            any(String.class),
            any(String.class),
            any(String.class),
            any(HolidayCutoffUpdateRequest.class));
  }

  @Test
  @DisplayName("When updation of holiday cutoff is not successful")
  void updateHolidayCutoffWithException()
      throws PromiseEngineException, CommonServiceException, ParseException {
    when(holidayCutoffService.updateHolidayCutoff(
            any(String.class),
            any(String.class),
            any(String.class),
            any(HolidayCutoffUpdateRequest.class)))
        .thenThrow(new RuntimeException("error"));
    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              holidayCutoffController.updateHolidayCutoff(
                  ORG_ID_1,
                  HOLIDAY_CUTOFF_NAME,
                  HOLIDAY_CUTOFF_RULE,
                  testUtil.updateHolidayCutoffRequest());
            });
    assertEquals("error", ex.getMessage());

    verify(holidayCutoffService, times(1))
        .updateHolidayCutoff(
            any(String.class),
            any(String.class),
            any(String.class),
            any(HolidayCutoffUpdateRequest.class));
  }

  @Test
  @DisplayName("When holiday cutoff is fetched successful")
  void fetchHolidayCutoffTest()
      throws PromiseEngineException, CommonServiceException, ParseException {
    when(holidayCutoffService.fetchHolidayCutoff(any(), any(), any()))
        .thenReturn(testUtil.createHolidayCutoffInfo());
    ResponseEntity<BaseResponse<HolidayCutoffInfo>> response =
        holidayCutoffController.fetchHolidayCutoff(
            ORG_ID_1, HOLIDAY_CUTOFF_NAME, HOLIDAY_CUTOFF_RULE);
    assertEquals(HttpStatus.valueOf(200), response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Holiday cutoff data fetched successfully!", response.getBody().getMessage());
    verify(holidayCutoffService, times(1))
        .fetchHolidayCutoff(any(String.class), any(String.class), any(String.class));
  }

  @Test
  @DisplayName("When fetch of holiday cutoff is not successful")
  void fetchHolidayCutoffWithException()
      throws PromiseEngineException, CommonServiceException, ParseException {
    when(holidayCutoffService.fetchHolidayCutoff(
            any(String.class), any(String.class), any(String.class)))
        .thenThrow(new RuntimeException("error"));
    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              holidayCutoffController.fetchHolidayCutoff(
                  ORG_ID_1, HOLIDAY_CUTOFF_NAME, HOLIDAY_CUTOFF_RULE);
            });
    assertEquals("error", ex.getMessage());

    verify(holidayCutoffService, times(1))
        .fetchHolidayCutoff(any(String.class), any(String.class), any(String.class));
  }

  @Test
  @DisplayName("When deletion of a holiday cutoff is successful")
  void deleteHolidayCutoffTest() throws CommonServiceException {
    when(holidayCutoffService.deleteHolidayCutoff(any(), any(), any()))
        .thenReturn(testUtil.createHolidayCutoffInfo());
    ResponseEntity<BaseResponse<HolidayCutoffInfo>> response =
        holidayCutoffController.deleteHolidayCutoff(
            ORG_ID_1, HOLIDAY_CUTOFF_NAME, HOLIDAY_CUTOFF_RULE);
    assertEquals(HttpStatus.valueOf(200), response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Holiday cutoff data deleted successfully!", response.getBody().getMessage());
    verify(holidayCutoffService, times(1))
        .deleteHolidayCutoff(any(String.class), any(String.class), any(String.class));
  }

  @Test
  @DisplayName("When deletion of holiday cutoff is not successful")
  void deleteHolidayCutoffWithException() throws CommonServiceException {
    when(holidayCutoffService.deleteHolidayCutoff(
            any(String.class), any(String.class), any(String.class)))
        .thenThrow(new RuntimeException("Holiday cutoff data for a given input not found"));
    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              holidayCutoffController.deleteHolidayCutoff(
                  ORG_ID_1, HOLIDAY_CUTOFF_NAME, HOLIDAY_CUTOFF_RULE);
            });
    assertEquals("Holiday cutoff data for a given input not found", ex.getMessage());

    verify(holidayCutoffService, times(1))
        .deleteHolidayCutoff(any(String.class), any(String.class), any(String.class));
  }
}
