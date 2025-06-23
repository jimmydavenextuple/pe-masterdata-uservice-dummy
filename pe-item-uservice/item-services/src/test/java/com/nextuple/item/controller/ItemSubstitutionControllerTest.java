/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.item.TestUtil;
import com.nextuple.item.domain.inbound.UpsertItemSubstitutionRequest;
import com.nextuple.item.domain.outbound.ItemSubstitutionResponse;
import com.nextuple.item.service.ItemSubstitutionService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ItemSubstitutionControllerTest {

  @InjectMocks private ItemSubstitutionController itemSubstitutionController;

  @InjectMocks private TestUtil testUtil;

  @Mock private ItemSubstitutionService itemSubstitutionService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Test upsert item substitution - Should successfully upsert item substitution")
  void testUpsertItemSubstitution() {
    // Arrange
    UpsertItemSubstitutionRequest request = new UpsertItemSubstitutionRequest();
    ItemSubstitutionResponse mockResponse = new ItemSubstitutionResponse();

    when(itemSubstitutionService.upsertItemSubstitution(any(UpsertItemSubstitutionRequest.class)))
        .thenReturn(mockResponse);

    // Act
    ResponseEntity<BaseResponse<ItemSubstitutionResponse>> responseEntity =
        itemSubstitutionController.upsertItemSubstitution(request);

    // Assert
    Assertions.assertNotNull(responseEntity);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        "Item substitution successfully upserted",
        Objects.requireNonNull(responseEntity.getBody()).getMessage());
    Assertions.assertEquals(mockResponse, responseEntity.getBody().getPayload());

    // Verify service call
    verify(itemSubstitutionService, times(1)).upsertItemSubstitution(request);
  }

  @Test
  @DisplayName("Test get item substitution - Should return list of item substitutions")
  void testGetItemSubstitution() {
    // Arrange
    String orgId = "org1";
    String primaryItemId = "item1";
    String primaryUom = "EACH";

    List<ItemSubstitutionResponse> mockResponses = new ArrayList<>();
    mockResponses.add(new ItemSubstitutionResponse());

    when(itemSubstitutionService.getItemSubstitution(orgId, primaryItemId, primaryUom))
        .thenReturn(mockResponses);

    // Act
    ResponseEntity<BaseResponse<List<ItemSubstitutionResponse>>> responseEntity =
        itemSubstitutionController.getItemSubstitution(orgId, primaryItemId, primaryUom);

    // Assert
    Assertions.assertNotNull(responseEntity);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(mockResponses, responseEntity.getBody().getPayload());

    // Verify service call
    verify(itemSubstitutionService, times(1)).getItemSubstitution(orgId, primaryItemId, primaryUom);
  }

  @Test
  @DisplayName("Test delete item substitution - Should successfully delete item substitution")
  void testDeleteItemSubstitution() {
    // Arrange
    var deleteRequest = testUtil.getDeleteItemSubstitutionRequest();

    // Act
    ResponseEntity<BaseResponse<Void>> responseEntity =
        itemSubstitutionController.deleteItemSubstitution(deleteRequest);

    // Assert
    Assertions.assertNotNull(responseEntity);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        "Item substitution deleted successfully",
        Objects.requireNonNull(responseEntity.getBody()).getMessage());

    // Verify service call
    verify(itemSubstitutionService, times(1)).deleteItemSubstitution(any());
  }
}
