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
import com.nextuple.sourcing.cost.config.dto.CostAttributeDetailsCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostAttributeDto;
import com.nextuple.sourcing.cost.config.inbound.CostAttributeRequest;
import com.nextuple.sourcing.cost.config.inbound.CostAttributeUpdateRequest;
import com.nextuple.sourcing.cost.config.service.CostAttributeService;
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

class CostAttributeControllerTest {
  @Mock private CostAttributeService costAttributeService;

  @InjectMocks private CostAttributeController costAttributeController;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Test for creating a cost attribute")
  void createCostAttributeTest() throws CommonServiceException {
    CostAttributeDto costAttributeDto = testUtil.getCostAttributeDto();

    when(costAttributeService.createCostAttribute(any(CostAttributeRequest.class)))
        .thenReturn(costAttributeDto);

    ResponseEntity<BaseResponse<CostAttributeDto>> responseEntity =
        costAttributeController.createCostAttribute(testUtil.getCostAttributeRequest());

    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(costAttributeDto.getId(), responseEntity.getBody().getPayload().getId());

    verify(costAttributeService, times(1)).createCostAttribute(any(CostAttributeRequest.class));
  }

  @Test
  @DisplayName("Test for fetching cost attribute details by id")
  void getCostAttributeDetailsByIdTest() throws CommonServiceException {
    CostAttributeDto costAttributeDto = testUtil.getCostAttributeDto();
    when(costAttributeService.findCostAttributeDetailsById(anyLong())).thenReturn(costAttributeDto);

    ResponseEntity<BaseResponse<CostAttributeDto>> responseEntity =
        costAttributeController.getCostAttributeDetailsById(TestUtil.ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(costAttributeDto.getId(), responseEntity.getBody().getPayload().getId());

    verify(costAttributeService, times(1)).findCostAttributeDetailsById(anyLong());
  }

  @Test
  @DisplayName("Test for updating cost attribute")
  void updateCostAttributeTest() throws CommonServiceException {
    CostAttributeDto costAttributeDto = testUtil.getCostAttributeDto();
    when(costAttributeService.updateCostAttribute(anyLong(), any(CostAttributeUpdateRequest.class)))
        .thenReturn(costAttributeDto);

    ResponseEntity<BaseResponse<CostAttributeDto>> responseEntity =
        costAttributeController.updateCostAttribute(
            TestUtil.ID, testUtil.getCostAttributeUpdateRequest());

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(costAttributeDto.getId(), responseEntity.getBody().getPayload().getId());

    verify(costAttributeService, times(1))
        .updateCostAttribute(anyLong(), any(CostAttributeUpdateRequest.class));
  }

  @Test
  @DisplayName("Test for fetching cost attribute details by attribute name")
  void getCostAttributeDetailsByAttributeNameTest() throws CommonServiceException {
    CostAttributeDto costAttributeDto = testUtil.getCostAttributeDto();
    when(costAttributeService.findCostAttributeDetailsByAttributeName(anyString()))
        .thenReturn(costAttributeDto);

    ResponseEntity<BaseResponse<CostAttributeDto>> responseEntity =
        costAttributeController.getCostAttributeDetailsByAttributeName(TestUtil.ATTRIBUTE_NAME);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(costAttributeDto.getId(), responseEntity.getBody().getPayload().getId());

    verify(costAttributeService, times(1)).findCostAttributeDetailsByAttributeName(anyString());
  }

  @Test
  @DisplayName("Test for fetching cost attribute details list")
  void getCostAttributeDetailsListTest() throws CommonServiceException {
    CostAttributeDto costAttributeDto = testUtil.getCostAttributeDto();
    when(costAttributeService.findCostAttributeDetailsList()).thenReturn(List.of(costAttributeDto));

    ResponseEntity<BaseResponse<List<CostAttributeDto>>> responseEntity =
        costAttributeController.getCostAttributeDetailsList();

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(1, responseEntity.getBody().getPayload().size());

    verify(costAttributeService, times(1)).findCostAttributeDetailsList();
  }

  @Test
  @DisplayName("Test for deleting cost attribute details")
  void deleteCostAttributeDetailsTest() {
    CostAttributeDto costAttributeDto = testUtil.getCostAttributeDto();
    when(costAttributeService.deleteCostAttributeDetails()).thenReturn(List.of(costAttributeDto));

    ResponseEntity<BaseResponse<List<CostAttributeDto>>> responseEntity =
        costAttributeController.deleteCostAttributeDetails();

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(1, responseEntity.getBody().getPayload().size());

    verify(costAttributeService, times(1)).deleteCostAttributeDetails();
  }

  @Test
  void getCostAttributeDetailsCacheKeysTest() {

    List<CostAttributeDetailsCacheKeyDto> costAttributeDetailsCacheKeyDtoList =
        testUtil.getCostAttributeDetailsCacheKeys();

    when(costAttributeService.getAllCostAttributeCacheKeys(any()))
        .thenReturn(costAttributeDetailsCacheKeyDtoList);

    ResponseEntity<BaseResponse<List<CostAttributeDetailsCacheKeyDto>>> response =
        costAttributeController.getCostAttributeDetailsCacheKeys(2);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(costAttributeDetailsCacheKeyDtoList, response.getBody().getPayload());

    verify(costAttributeService, times(1)).getAllCostAttributeCacheKeys(any());
  }
}
