/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.sourcing.cost.config.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.dto.SelectorAndCostItineraryMappingCacheKeyDto;
import com.nextuple.sourcing.cost.config.outbound.SelectorAndCostItineraryMappingResponse;
import com.nextuple.sourcing.cost.config.service.SelectorAndCostItineraryMappingService;
import com.nextuple.sourcing.cost.config.utils.TestUtil;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@DisplayName("SelectorAndCostItineraryMappingController Test Cases")
class SelectorAndCostItineraryMappingControllerTest {

  @Mock private SelectorAndCostItineraryMappingService selectorAndCostItineraryMappingService;

  @InjectMocks
  private SelectorAndCostItineraryMappingController selectorAndCostItineraryMappingController;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Test creating a Selector And Cost Itinerary Mapping")
  void createSelectorAndCostItineraryMapping() throws CommonServiceException {

    when(selectorAndCostItineraryMappingService.createSelectorAndCostItineraryMapping(
            anyString(), any()))
        .thenReturn(testUtil.getSelectorAndCostItineraryMappingResponse());
    ResponseEntity<BaseResponse<SelectorAndCostItineraryMappingResponse>> response =
        selectorAndCostItineraryMappingController.createSelectorAndCostItineraryMapping(
            TestUtil.ORG_ID, testUtil.getSelectorAndCostItineraryMappingRequest());
    assertEquals(201, response.getStatusCodeValue());
    assertEquals(TestUtil.ID, response.getBody().getPayload().getId());
    assertEquals(
        TestUtil.COST_ITINERARY_UPSLIKE, response.getBody().getPayload().getCostItinerary());
    assertEquals(TestUtil.COST_TYPE_SHIPPING_COST, response.getBody().getPayload().getCostType());
    assertEquals(TestUtil.SELECTOR_CF, response.getBody().getPayload().getSelectorCf());
    assertEquals(TestUtil.SELECTOR_CF_VALUE, response.getBody().getPayload().getSelectorCfValue());
    assertEquals(TestUtil.ORG_ID, response.getBody().getPayload().getOrgId());
    verify(selectorAndCostItineraryMappingService, times(1))
        .createSelectorAndCostItineraryMapping(anyString(), any());
  }

  @Test
  @DisplayName("Test retrieving a Selector And Cost Itinerary Mapping")
  void getSelectorAndCostItineraryMapping() throws CommonServiceException {
    when(selectorAndCostItineraryMappingService.getSelectorAndCostItineraryMapping(
            anyString(), anyLong()))
        .thenReturn(testUtil.getSelectorAndCostItineraryMappingResponse());
    ResponseEntity<BaseResponse<SelectorAndCostItineraryMappingResponse>> response =
        selectorAndCostItineraryMappingController.getSelectorAndCostItineraryMapping(
            TestUtil.ORG_ID, TestUtil.ID);
    assertEquals(
        TestUtil.COST_ITINERARY_UPSLIKE, response.getBody().getPayload().getCostItinerary());
    assertEquals(TestUtil.COST_TYPE_SHIPPING_COST, response.getBody().getPayload().getCostType());
    assertEquals(TestUtil.SELECTOR_CF, response.getBody().getPayload().getSelectorCf());
    assertEquals(TestUtil.SELECTOR_CF_VALUE, response.getBody().getPayload().getSelectorCfValue());
    assertEquals(TestUtil.ID, response.getBody().getPayload().getId());
    assertEquals(TestUtil.ORG_ID, response.getBody().getPayload().getOrgId());
    assertEquals(200, response.getStatusCodeValue());
    verify(selectorAndCostItineraryMappingService, times(1))
        .getSelectorAndCostItineraryMapping(any(), any());
  }

  @Test
  @DisplayName(
      "Should get selector and cost itinerary mapping by orgId and selectorCf and costType")
  void getSelectorAndCostItineraryMappingByOrgIdSelectorCfAndCostType()
      throws CommonServiceException {
    when(selectorAndCostItineraryMappingService
            .getSelectorAndCostItineraryMappingByOrgIdSelectorCfAndCostType(
                anyString(), anyString(), anyString()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingResponse()));
    ResponseEntity<BaseResponse<List<SelectorAndCostItineraryMappingResponse>>> response =
        selectorAndCostItineraryMappingController
            .getSelectorAndCostItineraryMappingByOrgIdSelectorCfAndCostType(
                TestUtil.ORG_ID, TestUtil.SELECTOR_CF, TestUtil.COST_TYPE_SHIPPING_COST);

    assertEquals(
        TestUtil.COST_ITINERARY_UPSLIKE, response.getBody().getPayload().get(0).getCostItinerary());
    assertEquals(
        TestUtil.COST_TYPE_SHIPPING_COST, response.getBody().getPayload().get(0).getCostType());
    assertEquals(TestUtil.SELECTOR_CF, response.getBody().getPayload().get(0).getSelectorCf());
    assertEquals(
        TestUtil.SELECTOR_CF_VALUE, response.getBody().getPayload().get(0).getSelectorCfValue());
    assertEquals(TestUtil.ID, response.getBody().getPayload().get(0).getId());
    assertEquals(TestUtil.ORG_ID, response.getBody().getPayload().get(0).getOrgId());
    assertEquals(200, response.getStatusCodeValue());

    verify(selectorAndCostItineraryMappingService, times(1))
        .getSelectorAndCostItineraryMappingByOrgIdSelectorCfAndCostType(any(), any(), any());
  }

  @Test
  @DisplayName("Test deleting a Selector And Cost Itinerary Mapping")
  void deleteSelectorAndCostItineraryMapping() throws CommonServiceException {
    when(selectorAndCostItineraryMappingService.deleteSelectorAndCostItineraryMapping(
            anyString(), anyLong()))
        .thenReturn(testUtil.getSelectorAndCostItineraryMappingResponse());
    ResponseEntity<BaseResponse<SelectorAndCostItineraryMappingResponse>> response =
        selectorAndCostItineraryMappingController.deleteSelectorAndCostItineraryMapping(
            TestUtil.ORG_ID, TestUtil.ID);
    assertEquals(
        TestUtil.COST_ITINERARY_UPSLIKE, response.getBody().getPayload().getCostItinerary());
    assertEquals(TestUtil.COST_TYPE_SHIPPING_COST, response.getBody().getPayload().getCostType());
    assertEquals(TestUtil.SELECTOR_CF, response.getBody().getPayload().getSelectorCf());
    assertEquals(TestUtil.SELECTOR_CF_VALUE, response.getBody().getPayload().getSelectorCfValue());
    assertEquals(TestUtil.ID, response.getBody().getPayload().getId());
    assertEquals(TestUtil.ORG_ID, response.getBody().getPayload().getOrgId());
    assertEquals(200, response.getStatusCodeValue());
    verify(selectorAndCostItineraryMappingService, times(1))
        .deleteSelectorAndCostItineraryMapping(any(), any());
  }

  @Test
  void getSelectorAndCostItineraryCacheKeysTest() {
    List<SelectorAndCostItineraryMappingCacheKeyDto> selectorAndCostItineraryCacheKeyDtoList =
        testUtil.getSelectorAndCostItineraryCacheKeys();
    when(selectorAndCostItineraryMappingService.getAllSelectorAndCostItineraryCacheKeys(any()))
        .thenReturn(selectorAndCostItineraryCacheKeyDtoList);
    ResponseEntity<BaseResponse<List<SelectorAndCostItineraryMappingCacheKeyDto>>> response =
        selectorAndCostItineraryMappingController.getSelectorAndCostItineraryCacheKeys(2);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(selectorAndCostItineraryCacheKeyDtoList, response.getBody().getPayload());
    verify(selectorAndCostItineraryMappingService, times(1))
        .getAllSelectorAndCostItineraryCacheKeys(any());
  }

  @Test
  @DisplayName("Update selector and cost itinerary mapping by id and org id")
  void updateSelectorAndCostItineraryMappingByIdAndOrgIdTest() throws CommonServiceException {
    when(selectorAndCostItineraryMappingService.updateSelectorAndCostItineraryMappingByIdAndOrgId(
            anyLong(), anyString(), any()))
        .thenReturn(testUtil.getSelectorAndCostItineraryMappingResponse());

    ResponseEntity<BaseResponse<SelectorAndCostItineraryMappingResponse>> response =
        selectorAndCostItineraryMappingController.updateSelectorAndCostItineraryMappingByIdAndOrgId(
            TestUtil.ORG_ID,
            TestUtil.ID,
            testUtil.getUpdateSelectorAndCostItineraryMappingRequest());
    assertEquals(
        TestUtil.COST_ITINERARY_UPSLIKE, response.getBody().getPayload().getCostItinerary());
    assertEquals(TestUtil.COST_TYPE_SHIPPING_COST, response.getBody().getPayload().getCostType());
    assertEquals(TestUtil.SELECTOR_CF, response.getBody().getPayload().getSelectorCf());
    assertEquals(TestUtil.SELECTOR_CF_VALUE, response.getBody().getPayload().getSelectorCfValue());
    assertEquals(TestUtil.ID, response.getBody().getPayload().getId());
    assertEquals(TestUtil.ORG_ID, response.getBody().getPayload().getOrgId());
    assertEquals(200, response.getStatusCodeValue());
    verify(selectorAndCostItineraryMappingService, times(1))
        .updateSelectorAndCostItineraryMappingByIdAndOrgId(any(), any(), any());
  }
}
