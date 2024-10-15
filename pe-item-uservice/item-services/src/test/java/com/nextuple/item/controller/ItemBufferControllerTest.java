/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.common.response.error.ErrorType;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.item.TestUtil;
import com.nextuple.item.domain.inbound.ItemBufferRequest;
import com.nextuple.item.domain.inbound.ItemBufferUpdateRequest;
import com.nextuple.item.domain.outbound.ItemBufferResponse;
import com.nextuple.item.persistence.exception.ItemDomainException;
import com.nextuple.item.service.ItemBufferService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ItemBufferControllerTest {

  @InjectMocks private ItemBufferController itemBufferController;

  @InjectMocks private TestUtil testUtil;

  @Mock private ItemBufferService itemBufferService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Happy path - Create Item Buffer")
  void createItemBufferTest() throws CommonServiceException, ItemDomainException {
    ItemBufferRequest itemBufferRequest = testUtil.getItemBufferRequest();
    when(itemBufferService.createItemBuffer(any(ItemBufferRequest.class)))
        .thenReturn(testUtil.getItemBufferResponse());

    ResponseEntity<BaseResponse<ItemBufferResponse>> responseEntity =
        itemBufferController.createItemBuffer(itemBufferRequest);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody().getPayload());
    verify(itemBufferService, times(1)).createItemBuffer(any());
  }

  @Test
  @DisplayName("Exception - Create Item Buffer")
  void createItemBufferExceptionTest() throws CommonServiceException, ItemDomainException {
    ItemBufferRequest itemBufferRequest = testUtil.getItemBufferRequest();
    when(itemBufferService.createItemBuffer(any(ItemBufferRequest.class)))
        .thenThrow(new RuntimeException("Failed to create item buffer"));

    Exception exception =
        assertThrows(
            Exception.class, () -> itemBufferController.createItemBuffer(itemBufferRequest));
    assertEquals("Failed to create item buffer", exception.getMessage());

    verify(itemBufferService, times(1)).createItemBuffer(any());
  }

  @Test
  @DisplayName("Happy path - Delete Item Buffer")
  void deleteItemBufferTest() throws CommonServiceException {
    ItemBufferRequest itemBufferRequest = new ItemBufferRequest();
    when(itemBufferService.deleteItemBuffer(any(ItemBufferRequest.class)))
        .thenReturn(new ItemBufferResponse());
    ResponseEntity<BaseResponse<ItemBufferResponse>> responseEntity =
        itemBufferController.deleteItemBuffer(itemBufferRequest);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals("Item Buffer successfully deleted", responseEntity.getBody().getMessage());
    verify(itemBufferService, times(1)).deleteItemBuffer(any(ItemBufferRequest.class));
  }

  @Test
  @DisplayName("Exception - Delete Item Buffer")
  void deleteItemBufferExceptionTest() throws CommonServiceException {
    ItemBufferRequest itemBufferRequest = new ItemBufferRequest();

    when(itemBufferService.deleteItemBuffer(any(ItemBufferRequest.class)))
        .thenThrow(new RuntimeException("Failed to delete item buffer"));

    Exception exception =
        org.junit.jupiter.api.Assertions.assertThrows(
            Exception.class, () -> itemBufferController.deleteItemBuffer(itemBufferRequest));
    assertEquals("Failed to delete item buffer", exception.getMessage());

    verify(itemBufferService, times(1)).deleteItemBuffer(any(ItemBufferRequest.class));
  }

  @Test
  @DisplayName("Happy path - Get Item Buffer By OrgId And Id")
  void getItemBufferByOrgIdAndIdTest() throws CommonServiceException {
    ItemBufferResponse expectedResponse = testUtil.getItemBufferResponse();
    when(itemBufferService.fetchItemBuffer(TestUtil.ORG_ID, 2L)).thenReturn(expectedResponse);
    ResponseEntity<BaseResponse<ItemBufferResponse>> responseEntity =
        itemBufferController.getItemBufferByOrgIdAndId(TestUtil.ORG_ID, 2L);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals("Item Buffer successfully fetched", responseEntity.getBody().getMessage());
    assertEquals(expectedResponse, responseEntity.getBody().getPayload());
    verify(itemBufferService, times(1)).fetchItemBuffer(TestUtil.ORG_ID, 2L);
  }

  @Test
  @DisplayName("Happy path - Delete Item Buffer By OrgId And Id")
  void deleteItemBufferByOrgIdAndIdTest() throws CommonServiceException {
    ItemBufferResponse expectedResponse = new ItemBufferResponse();
    when(itemBufferService.deleteItemBufferByOrgIdAndId(TestUtil.ORG_ID, 2L))
        .thenReturn(expectedResponse);
    ResponseEntity<BaseResponse<ItemBufferResponse>> responseEntity =
        itemBufferController.deleteItemBufferByOrgIdAndId(TestUtil.ORG_ID, 2L);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals("Item Buffer successfully deleted", responseEntity.getBody().getMessage());
    assertEquals(expectedResponse, responseEntity.getBody().getPayload());
    verify(itemBufferService, times(1)).deleteItemBufferByOrgIdAndId(TestUtil.ORG_ID, 2L);
  }

  @Test
  @DisplayName("Happy path - Update Item Buffer")
  void updateItemBufferByOrgIdAndIdTest() throws CommonServiceException {
    ItemBufferUpdateRequest updateRequest = new ItemBufferUpdateRequest();
    when(itemBufferService.updateItemBuffer(TestUtil.ORG_ID, 2L, updateRequest))
        .thenReturn(testUtil.getItemBufferResponse());
    ResponseEntity<BaseResponse<ItemBufferResponse>> responseEntity =
        itemBufferController.updateItemBufferByOrgIdAndId(TestUtil.ORG_ID, 2L, updateRequest);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody().getPayload());
    assertEquals("Item Buffer successfully updated", responseEntity.getBody().getMessage());
    verify(itemBufferService, times(1)).updateItemBuffer(TestUtil.ORG_ID, 2L, updateRequest);
  }

  @Test
  @DisplayName("Exception - Update Item Buffer")
  void updateItemBufferByOrgIdAndIdExceptionTest() throws CommonServiceException {
    ItemBufferUpdateRequest updateRequest = new ItemBufferUpdateRequest();
    when(itemBufferService.updateItemBuffer(TestUtil.ORG_ID, 2L, updateRequest))
        .thenThrow(new RuntimeException("Failed to update item buffer"));
    Exception exception =
        assertThrows(
            Exception.class,
            () ->
                itemBufferController.updateItemBufferByOrgIdAndId(
                    TestUtil.ORG_ID, 2L, updateRequest));
    assertEquals("Failed to update item buffer", exception.getMessage());

    verify(itemBufferService, times(1)).updateItemBuffer(TestUtil.ORG_ID, 2L, updateRequest);
  }

  @Test
  @DisplayName("Exception - Handle InvalidFormatException with Invalid Date Format")
  void handleInvalidFormatExceptionWithInvalidDateFormat() {
    InvalidFormatException ex = mock(InvalidFormatException.class);
    JsonMappingException.Reference reference = mock(JsonMappingException.Reference.class);
    List<JsonMappingException.Reference> referenceList = new ArrayList<>();
    referenceList.add(reference);
    when(ex.getPath()).thenReturn(referenceList);
    when(ex.getOriginalMessage()).thenReturn("Failed to parse date");
    when(reference.getFieldName()).thenReturn("bufferStartDate");

    ResponseEntity<ErrorResponse> responseEntity =
        itemBufferController.handleInvalidFormatException(ex);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(ErrorType.ERROR, responseEntity.getBody().getPayload().getType());
    assertEquals("Failed to parse date", responseEntity.getBody().getMessage());
    Map<String, FieldError> errorFields = responseEntity.getBody().getPayload().getFields();
    assertTrue(errorFields.containsKey("bufferStartDate"));
  }
}
