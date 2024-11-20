/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.common.inbound.ConfigureShipChargeCappingRequest;
import com.nextuple.dataupload.common.outbound.TargetProfitMarginResponse;
import com.nextuple.dataupload.service.RecommendationRulesService;
import com.nextuple.dataupload.util.TestUtil;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class RecommendationRulesControllerTest {

  @Mock private RecommendationRulesService recommendationRulesService;

  @InjectMocks private TestUtil testUtil;

  @InjectMocks private RecommendationRulesController recommendationRulesController;

  @Test
  @DisplayName("Happy path: Configure target profit margin")
  void createTargetProfitMargin() throws CommonServiceException {
    when(recommendationRulesService.createTargetProfitMargin(any(), any()))
        .thenReturn(testUtil.getTargetProfitMarginResponse());
    ResponseEntity<BaseResponse<TargetProfitMarginResponse>> expectedResponse =
        ResponseEntity.ok(
            BaseResponse.builder()
                .message("Target profit margin configured successfully")
                .payload(testUtil.getTargetProfitMarginResponse())
                .build());
    ResponseEntity<BaseResponse<TargetProfitMarginResponse>> response =
        recommendationRulesController.createTargetProfitMargin(
            TestUtil.ORG_ID, testUtil.getTargetProfitMarginRequest());
    Objects.requireNonNull(expectedResponse.getBody()).getMessage();
    Objects.requireNonNull(response.getBody()).getMessage();
    verify(recommendationRulesService, times(1)).createTargetProfitMargin(any(), any());
  }

  @Test
  @DisplayName("Happy path: Update target profit margin")
  void updateTargetProfitMargin() throws CommonServiceException {
    when(recommendationRulesService.updateTargetProfitGrossMargin(any(), any()))
        .thenReturn(testUtil.getTargetProfitMarginResponse());
    ResponseEntity<BaseResponse<TargetProfitMarginResponse>> expectedResponse =
        ResponseEntity.ok(
            BaseResponse.builder()
                .message("Target profit margin updated successfully")
                .payload(testUtil.getTargetProfitMarginResponse())
                .build());
    ResponseEntity<BaseResponse<TargetProfitMarginResponse>> response =
        recommendationRulesController.updateTargetProfitMarginConfig(
            TestUtil.ORG_ID, testUtil.getTargetProfitMarginRequest());
    Assertions.assertEquals(expectedResponse.getStatusCode(), response.getStatusCode());
    Assertions.assertEquals(
        Objects.requireNonNull(expectedResponse.getBody()).getMessage(),
        Objects.requireNonNull(response.getBody()).getMessage());
    Assertions.assertEquals(
        expectedResponse.getBody().getPayload(), response.getBody().getPayload());
    verify(recommendationRulesService, times(1)).updateTargetProfitGrossMargin(any(), any());
  }

  @Test
  @DisplayName("Delete target profit margin")
  void deleteTargetProfitMargin() throws CommonServiceException {
    ResponseEntity<BaseResponse<String>> expectedResponse =
        ResponseEntity.ok(
            BaseResponse.builder().message("Target profit margin(s) deleted successfully").build());

    doNothing().when(recommendationRulesService).deleteTargetProfitMargin(any(), any());

    ResponseEntity<BaseResponse<String>> response =
        recommendationRulesController.deleteTargetProfitMargin(
            TestUtil.ORG_ID, testUtil.getDeleteTargetProfitMarginRequestSingle());

    assertEquals(expectedResponse.getStatusCode(), response.getStatusCode());
    assertEquals(
        Objects.requireNonNull(expectedResponse.getBody()).getMessage(),
        Objects.requireNonNull(response.getBody()).getMessage());
    verify(recommendationRulesService, times(1)).deleteTargetProfitMargin(any(), any());
  }

  @Test
  @DisplayName("Fetch target profit margin")
  void fetchTargetProfitMargin() throws CommonServiceException {
    when(recommendationRulesService.fetchTargetProfitMargin(anyString(), anyString()))
        .thenReturn(testUtil.getFetchTargetProfitMarginResponse());

    var response =
        recommendationRulesController.fetchTargetProfitMargins(
            TestUtil.ORG_ID, TestUtil.ATTRIBUTE_NAME_ITEM_CATEGORY);

    assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    assertEquals(
        "Target profit margin fetched successfully",
        Objects.requireNonNull(response.getBody()).getMessage());
    assertEquals(testUtil.getFetchTargetProfitMarginResponse(), response.getBody().getPayload());
    verify(recommendationRulesService, times(1)).fetchTargetProfitMargin(anyString(), anyString());
  }

  @Test
  @DisplayName("Fetch attribute details")
  void fetchAttributeDetails() throws CommonServiceException {
    when(recommendationRulesService.fetchAttributeDetails(anyString()))
        .thenReturn(testUtil.getAttributeAndValuesTGMResponse());

    var response = recommendationRulesController.fetchAttributeDetails(TestUtil.ORG_ID);

    assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    assertEquals(
        "Attribute fetched successfully", Objects.requireNonNull(response.getBody()).getMessage());
    assertEquals(testUtil.getAttributeAndValuesTGMResponse(), response.getBody().getPayload());
    verify(recommendationRulesService, times(1)).fetchAttributeDetails(anyString());
  }

  @Test
  @DisplayName("Fetch ship charge details")
  void fetchShipChargeDetailsDetails() throws CommonServiceException {
    when(recommendationRulesService.fetchShipChargeDetails(anyString()))
        .thenReturn(testUtil.getShipChargeDetailsTGMResponse());

    var response = recommendationRulesController.fetchShipChargeDetailsDetails(TestUtil.ORG_ID);

    assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    assertEquals(
        "Ship charge capping constants fetched successfully",
        Objects.requireNonNull(response.getBody()).getMessage());
    assertEquals(testUtil.getShipChargeDetailsTGMResponse(), response.getBody().getPayload());
    verify(recommendationRulesService, times(1)).fetchShipChargeDetails(anyString());
  }

  @Test
  @DisplayName("Configure ship charge capping")
  void configureShipChargeCapping() throws CommonServiceException {
    var request = testUtil.getConfigureShipChargeCappingRequest();
    when(recommendationRulesService.configureShipChargeCapping(
            anyString(), any(ConfigureShipChargeCappingRequest.class)))
        .thenReturn(testUtil.getConfigureShipChargeCappingResponse());

    var response =
        recommendationRulesController.configureShipChargeCapping(TestUtil.ORG_ID, request);

    assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    assertEquals(
        "Ship charge capping constants configured successfully",
        Objects.requireNonNull(response.getBody()).getMessage());
    assertEquals(testUtil.getConfigureShipChargeCappingResponse(), response.getBody().getPayload());
    verify(recommendationRulesService, times(1))
        .configureShipChargeCapping(anyString(), any(ConfigureShipChargeCappingRequest.class));
  }

  @Test
  @DisplayName("Update ship charge capping status")
  void updateShipChargeCappingStatus() throws CommonServiceException {
    ResponseEntity<BaseResponse<String>> expectedResponse =
        ResponseEntity.ok(
            BaseResponse.builder()
                .message("Ship charge capping logic status updated successfully")
                .build());

    doNothing().when(recommendationRulesService).updateShipChargeCappingStatus(any(), any());

    ResponseEntity<BaseResponse<String>> response =
        recommendationRulesController.updateShipChargeCappingStatus(TestUtil.ORG_ID, true);

    assertEquals(expectedResponse.getStatusCode(), response.getStatusCode());
    assertEquals(
        Objects.requireNonNull(expectedResponse.getBody()).getMessage(),
        Objects.requireNonNull(response.getBody()).getMessage());
    verify(recommendationRulesService, times(1)).updateShipChargeCappingStatus(any(), any());
  }
}
