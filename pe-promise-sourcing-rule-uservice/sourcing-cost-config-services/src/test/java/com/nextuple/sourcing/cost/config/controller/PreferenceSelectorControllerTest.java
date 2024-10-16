/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller;

import static com.nextuple.sourcing.cost.config.utils.TestUtil.COST_TYPE;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.ORG_ID;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.SELECTOR_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.dto.PreferenceSelectorCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.PreferenceSelectorDto;
import com.nextuple.sourcing.cost.config.inbound.CreatePreferenceSelectorRequest;
import com.nextuple.sourcing.cost.config.inbound.UpdatePreferenceSelectorRequest;
import com.nextuple.sourcing.cost.config.service.PreferenceSelectorService;
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

class PreferenceSelectorControllerTest {
  @Mock private PreferenceSelectorService preferenceSelectorService;

  @InjectMocks private PreferenceSelectorController preferenceSelectorController;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createPreferenceSelectorTest() throws CommonServiceException {
    PreferenceSelectorDto preferenceSelectorResponse = testUtil.getPreferenceSelectorResponse();
    when(preferenceSelectorService.createPreferenceSelector(
            anyString(), any(CreatePreferenceSelectorRequest.class)))
        .thenReturn(preferenceSelectorResponse);

    ResponseEntity<BaseResponse<PreferenceSelectorDto>> responseEntity =
        preferenceSelectorController.createPreferenceSelector(
            ORG_ID, testUtil.getUpsertPreferenceSelectorRequest());

    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(preferenceSelectorResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(preferenceSelectorService, times(1))
        .createPreferenceSelector(anyString(), any(CreatePreferenceSelectorRequest.class));
  }

  @Test
  void getPreferenceSelectorByOrgIdAndIdTest() throws CommonServiceException {
    PreferenceSelectorDto preferenceSelectorResponse = testUtil.getPreferenceSelectorResponse();
    when(preferenceSelectorService.findByOrgIdAndPreferenceSelectorId(anyString(), anyLong()))
        .thenReturn(preferenceSelectorResponse);

    ResponseEntity<BaseResponse<PreferenceSelectorDto>> responseEntity =
        preferenceSelectorController.getPreferenceSelectorByOrgIdAndSelectorId(ORG_ID, SELECTOR_ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(preferenceSelectorResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(preferenceSelectorService, times(1))
        .findByOrgIdAndPreferenceSelectorId(anyString(), anyLong());
  }

  @Test
  @DisplayName("Should get preference selector by org id and cost type")
  void getPreferenceSelectorByOrgIdAndCostTypeTest() throws CommonServiceException {
    PreferenceSelectorDto preferenceSelectorResponse = testUtil.getPreferenceSelectorResponse();
    when(preferenceSelectorService.findByOrgIdAndPreferenceCostType(anyString(), anyString()))
        .thenReturn(preferenceSelectorResponse);

    ResponseEntity<BaseResponse<PreferenceSelectorDto>> responseEntity =
        preferenceSelectorController.getPreferenceSelectorByOrgIdAndCostType(ORG_ID, COST_TYPE);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(preferenceSelectorResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(preferenceSelectorService, times(1))
        .findByOrgIdAndPreferenceCostType(anyString(), anyString());
  }

  @Test
  void updatePreferenceSelectorTest() throws CommonServiceException {
    PreferenceSelectorDto preferenceSelectorResponse = testUtil.getPreferenceSelectorResponse();
    when(preferenceSelectorService.updatePreferenceSelector(
            anyLong(), anyString(), any(UpdatePreferenceSelectorRequest.class)))
        .thenReturn(preferenceSelectorResponse);

    ResponseEntity<BaseResponse<PreferenceSelectorDto>> responseEntity =
        preferenceSelectorController.updatePreferenceSelector(
            ORG_ID, SELECTOR_ID, testUtil.getUpdatePreferenceSelectorRequest());

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(preferenceSelectorResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(preferenceSelectorService, times(1))
        .updatePreferenceSelector(
            anyLong(), anyString(), any(UpdatePreferenceSelectorRequest.class));
  }

  @Test
  void deletePreferenceSelectorTest() throws CommonServiceException {
    PreferenceSelectorDto preferenceSelectorResponse = testUtil.getPreferenceSelectorResponse();
    when(preferenceSelectorService.deletePreferenceSelector(anyLong(), anyString()))
        .thenReturn(preferenceSelectorResponse);
    ResponseEntity<BaseResponse<PreferenceSelectorDto>> responseEntity =
        preferenceSelectorController.deletePreferenceSelector(ORG_ID, SELECTOR_ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(preferenceSelectorResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(preferenceSelectorService, times(1)).deletePreferenceSelector(anyLong(), anyString());
  }

  @Test
  void getPreferenceSelectorCacheKeysTest() {
    List<PreferenceSelectorCacheKeyDto> preferenceSelectorCacheKeyDtoList =
        testUtil.getPreferenceSelectorCacheKeys();
    when(preferenceSelectorService.getAllPreferenceSelectorCacheKeys(any()))
        .thenReturn(preferenceSelectorCacheKeyDtoList);
    ResponseEntity<BaseResponse<List<PreferenceSelectorCacheKeyDto>>> response =
        preferenceSelectorController.getPreferenceSelectorCacheKeys(2);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(preferenceSelectorCacheKeyDtoList, response.getBody().getPayload());
    verify(preferenceSelectorService, times(1)).getAllPreferenceSelectorCacheKeys(any());
  }
}
