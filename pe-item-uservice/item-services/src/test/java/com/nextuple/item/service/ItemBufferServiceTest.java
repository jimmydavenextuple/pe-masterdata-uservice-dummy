/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.util.DateValidationUtil;
import com.nextuple.item.TestUtil;
import com.nextuple.item.domain.inbound.ItemBufferRequest;
import com.nextuple.item.domain.inbound.ItemBufferUpdateRequest;
import com.nextuple.item.domain.outbound.ItemBufferResponse;
import com.nextuple.item.domain.outbound.PageResponseForItemBuffer;
import com.nextuple.item.persistence.domain.ItemBufferDomainDto;
import com.nextuple.item.persistence.domain.ItemDomainDto;
import com.nextuple.item.persistence.exception.ItemDomainException;
import com.nextuple.item.persistence.service.ItemPersistenceService;
import com.nextuple.item.persistence.service.impl.ItemBufferPersistenceServiceImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

class ItemBufferServiceTest {
  @Mock private ItemPersistenceService itemPersistenceService;
  @Mock private ItemBufferPersistenceServiceImpl itemBufferPersistenceService;
  @Mock private DateValidationUtil dateValidationUtil;
  @InjectMocks private TestUtil testUtil;

  @InjectMocks private ItemBufferService itemBufferService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Create Item Buffer - Happy path")
  void createItemBufferHappyPath() throws Exception {
    ItemBufferRequest request = testUtil.getItemBufferRequest();
    ItemDomainDto itemDomainDto = testUtil.getItemDomainDto();
    when(itemPersistenceService.findItemByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(Optional.of(itemDomainDto));
    ItemBufferDomainDto bufferDomainDto = testUtil.getItemBufferDomainDto();
    when(itemBufferPersistenceService.saveItemBuffer(any())).thenReturn(bufferDomainDto);
    ItemBufferResponse response = itemBufferService.createItemBuffer(request);
    assertNotNull(response);
    verify(itemPersistenceService, times(1)).findItemByItemIdAndOrgIdAndUom(any(), any(), any());
    verify(itemBufferPersistenceService, times(1)).saveItemBuffer(any());
  }

  @Test
  @DisplayName("Create Item Buffer - Existing Buffer")
  void createItemBufferExistingBuffer() throws Exception {
    ItemBufferRequest request = new ItemBufferRequest();
    request.setOrgId(TestUtil.ORG_ID);
    request.setItemId("1L");
    request.setUom(TestUtil.UOM);
    request.setBufferHours(2.0);
    request.setBufferStartDate(new Date());
    request.setBufferEndDate(new Date());

    ItemBufferDomainDto existingBuffer = new ItemBufferDomainDto();
    existingBuffer.setOrgId(TestUtil.ORG_ID);
    existingBuffer.setId(1L);
    existingBuffer.setItemId("1L");
    existingBuffer.setUom(TestUtil.UOM);
    existingBuffer.setBufferHours(20.0);
    existingBuffer.setBufferStartDate(new Date());
    existingBuffer.setBufferEndDate(new Date());
    when(itemPersistenceService.findItemByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getItemDomainDto()));
    when(itemBufferPersistenceService.findItemBufferByOrgIdAndId(any(), any()))
        .thenReturn(Optional.of(testUtil.getItemBufferDomainDto()));
    when(itemBufferPersistenceService.findByItemIdAndOrgIdAndUomAndBufferStartDateAndBufferEndDate(
            any(), any(), any(), any(), any()))
        .thenReturn(Optional.of(existingBuffer));

    itemBufferService.createItemBuffer(request);
    verify(itemBufferPersistenceService, times(1)).findItemBufferByOrgIdAndId(any(), any());
    verify(itemBufferPersistenceService, times(1))
        .findByItemIdAndOrgIdAndUomAndBufferStartDateAndBufferEndDate(
            any(), any(), any(), any(), any());
    verify(itemPersistenceService, times(1)).findItemByItemIdAndOrgIdAndUom(any(), any(), any());
  }

  @Test
  @DisplayName("Create Item Buffer - Item Not Found")
  void createItemBufferItemNotFound() throws Exception {
    ItemBufferRequest request = testUtil.getItemBufferRequest();
    when(itemPersistenceService.findItemByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(Optional.empty());

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class, () -> itemBufferService.createItemBuffer(request));

    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    assertEquals("Item not found for given details", exception.getMessage());
    verify(itemPersistenceService, times(1)).findItemByItemIdAndOrgIdAndUom(any(), any(), any());
  }

  @Test
  @DisplayName("Validate Buffer Hours - Negative Value")
  void validateBufferHoursNegative() {
    Double negativeBufferHours = -5.0;
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> itemBufferService.validateBufferHours(negativeBufferHours));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    assertEquals("bufferHours cannot be negative", exception.getMessage());
  }

  @Test
  @DisplayName("Validate Buffer Hours - Non-Negative Value")
  void validateBufferHoursNonNegative() {
    Double nonNegativeBufferHours = 5.0;
    assertDoesNotThrow(() -> itemBufferService.validateBufferHours(nonNegativeBufferHours));
  }

  @Test
  @DisplayName("Check Overlapping Buffers - No Overlap")
  void checkOverlappingBuffersNoOverlap() throws CommonServiceException, ItemDomainException {
    ItemDomainDto itemDomainDto = testUtil.getItemDomainDto();
    when(itemPersistenceService.findItemByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(Optional.of(itemDomainDto));

    ItemBufferDomainDto existingBuffer1 = testUtil.getItemBufferDomainDto();
    existingBuffer1.setId(1L);
    existingBuffer1.setBufferStartDate(new Date(0));
    existingBuffer1.setBufferEndDate(new Date(3000));

    ItemBufferDomainDto existingBuffer2 = testUtil.getItemBufferDomainDto();
    existingBuffer2.setId(2L);
    existingBuffer2.setBufferStartDate(new Date(6000));
    existingBuffer2.setBufferEndDate(new Date(9000));

    List<ItemBufferDomainDto> existingBuffers = new ArrayList<>();
    existingBuffers.add(existingBuffer1);
    existingBuffers.add(existingBuffer2);

    ItemBufferRequest request = new ItemBufferRequest();
    request.setBufferStartDate(new Date(4000));
    request.setBufferEndDate(new Date(5000));

    when(itemBufferPersistenceService.findByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(existingBuffers);

    assertDoesNotThrow(() -> itemBufferService.createItemBuffer(request));

    verify(itemBufferPersistenceService, times(1)).findByItemIdAndOrgIdAndUom(any(), any(), any());
    verify(itemPersistenceService, times(1)).findItemByItemIdAndOrgIdAndUom(any(), any(), any());
  }

  @Test
  @DisplayName("Check Overlapping Buffers - Overlap Exists")
  void checkOverlappingBuffersOverlapExists() throws CommonServiceException, ItemDomainException {

    ItemDomainDto itemDomainDto = testUtil.getItemDomainDto();
    when(itemPersistenceService.findItemByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(Optional.of(itemDomainDto));

    ItemBufferDomainDto existingBuffer1 = testUtil.getItemBufferDomainDto();
    existingBuffer1.setId(1L);
    existingBuffer1.setBufferStartDate(new Date(0));
    existingBuffer1.setBufferEndDate(new Date(5000));

    ItemBufferDomainDto existingBuffer2 = testUtil.getItemBufferDomainDto();
    existingBuffer2.setId(2L);
    existingBuffer2.setBufferStartDate(new Date(3000));
    existingBuffer2.setBufferEndDate(new Date(6000));

    List<ItemBufferDomainDto> entities = new ArrayList<>();
    entities.add(existingBuffer1);
    entities.add(existingBuffer2);

    ItemBufferRequest request = new ItemBufferRequest();
    request.setBufferStartDate(new Date(4000));
    request.setBufferEndDate(new Date(7000));

    when(itemBufferPersistenceService.findByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(entities);

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class, () -> itemBufferService.createItemBuffer(request));

    assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getHttpStatus());
    assertEquals("Item Buffer window already exists or overlaps", exception.getMessage());
    verify(itemBufferPersistenceService, times(1)).findByItemIdAndOrgIdAndUom(any(), any(), any());
    verify(itemPersistenceService, times(1)).findItemByItemIdAndOrgIdAndUom(any(), any(), any());
  }

  @Test
  @DisplayName("Check Buffer Contains New Buffer")
  void checkBufferContainsNewBuffer() throws CommonServiceException, ItemDomainException {

    ItemDomainDto itemDomainDto = testUtil.getItemDomainDto();
    when(itemPersistenceService.findItemByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(Optional.of(itemDomainDto));

    ItemBufferDomainDto existingBuffer1 = testUtil.getItemBufferDomainDto();
    existingBuffer1.setId(1L);
    existingBuffer1.setBufferStartDate(new Date(1000));
    existingBuffer1.setBufferEndDate(new Date(6000));

    List<ItemBufferDomainDto> entities = new ArrayList<>();
    entities.add(existingBuffer1);

    ItemBufferRequest request = new ItemBufferRequest();
    request.setBufferStartDate(new Date(2000));
    request.setBufferEndDate(new Date(5000));

    when(itemBufferPersistenceService.findByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(entities);

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class, () -> itemBufferService.createItemBuffer(request));

    assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getHttpStatus());
    assertEquals("Item Buffer window already exists or overlaps", exception.getMessage());
    verify(itemBufferPersistenceService, times(1)).findByItemIdAndOrgIdAndUom(any(), any(), any());
    verify(itemPersistenceService, times(1)).findItemByItemIdAndOrgIdAndUom(any(), any(), any());
  }

  @Test
  @DisplayName("Check Buffer Contained in New Buffer")
  void checkBufferContainedInNewBuffer() throws CommonServiceException, ItemDomainException {

    ItemDomainDto itemDomainDto = testUtil.getItemDomainDto();
    when(itemPersistenceService.findItemByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(Optional.of(itemDomainDto));

    ItemBufferDomainDto existingBuffer1 = testUtil.getItemBufferDomainDto();
    existingBuffer1.setId(1L);
    existingBuffer1.setBufferStartDate(new Date(2000));
    existingBuffer1.setBufferEndDate(new Date(4000));

    List<ItemBufferDomainDto> entities = new ArrayList<>();
    entities.add(existingBuffer1);

    ItemBufferRequest request = new ItemBufferRequest();
    request.setBufferStartDate(new Date(1000));
    request.setBufferEndDate(new Date(5000));

    when(itemBufferPersistenceService.findByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(entities);

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class, () -> itemBufferService.createItemBuffer(request));

    assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getHttpStatus());
    assertEquals("Item Buffer window already exists or overlaps", exception.getMessage());
    verify(itemBufferPersistenceService, times(1)).findByItemIdAndOrgIdAndUom(any(), any(), any());
    verify(itemPersistenceService, times(1)).findItemByItemIdAndOrgIdAndUom(any(), any(), any());
  }

  @Test
  @DisplayName("Check Overlapping Buffers - Same window")
  void checkOverlappingBuffersContainsNewBuffer()
      throws CommonServiceException, ItemDomainException {

    ItemDomainDto itemDomainDto = testUtil.getItemDomainDto();
    when(itemPersistenceService.findItemByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(Optional.of(itemDomainDto));

    ItemBufferDomainDto existingBuffer = testUtil.getItemBufferDomainDto();
    existingBuffer.setId(1L);
    existingBuffer.setBufferStartDate(new Date(1000));
    existingBuffer.setBufferEndDate(new Date(3000));

    List<ItemBufferDomainDto> entities = Collections.singletonList(existingBuffer);

    ItemBufferRequest request = new ItemBufferRequest();
    request.setBufferStartDate(new Date(1000));
    request.setBufferEndDate(new Date(3000));

    when(itemBufferPersistenceService.findByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(entities);

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class, () -> itemBufferService.createItemBuffer(request));

    assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getHttpStatus());
    assertEquals("Item Buffer window already exists or overlaps", exception.getMessage());

    verify(itemBufferPersistenceService, times(1)).findByItemIdAndOrgIdAndUom(any(), any(), any());
    verify(itemPersistenceService, times(1)).findItemByItemIdAndOrgIdAndUom(any(), any(), any());
  }

  @Test
  @DisplayName("Delete Item Buffer - Happy path")
  void deleteItemBufferHappyPath() throws Exception {
    ItemBufferRequest request = testUtil.getItemBufferRequest();
    ItemBufferDomainDto bufferDomainDto = testUtil.getItemBufferDomainDto();
    when(itemBufferPersistenceService.findItemBuffer(any()))
        .thenReturn(Optional.of(bufferDomainDto));
    ItemBufferResponse response = itemBufferService.deleteItemBuffer(request);

    assertNotNull(response);
    verify(itemBufferPersistenceService, times(1)).findItemBuffer(any());
  }

  @Test
  @DisplayName("Delete Item Buffer - Exception : Buffer not found")
  void deleteItemBuffersBufferNotFound() throws Exception {
    ItemBufferRequest request = testUtil.getItemBufferRequest();
    when(itemBufferPersistenceService.findItemBuffer(any())).thenReturn(Optional.empty());

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class, () -> itemBufferService.deleteItemBuffer(request));

    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    assertEquals("Item buffer not found for given details", exception.getMessage());
    verify(itemBufferPersistenceService, times(1)).findItemBuffer(any());
  }

  @Test
  @DisplayName("Get Item Buffers By ItemId and OrgId and Uom - Happy path")
  void getItemBuffersByItemIdAndOrgIdAndUomHappyPath() throws CommonServiceException {

    List<ItemBufferDomainDto> bufferDomainDtos =
        Collections.singletonList(testUtil.getItemBufferDomainDto());

    when(itemBufferPersistenceService.findItemBuffersListByItemIdAndOrgIdAndUom(
            TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM))
        .thenReturn(bufferDomainDtos);

    List<ItemBufferResponse> responses =
        itemBufferService.getItemBuffersByItemIdAndOrgIdAndUom(
            TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM);

    verify(itemBufferPersistenceService, times(1))
        .findItemBuffersListByItemIdAndOrgIdAndUom(TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM);
    assertNotNull(responses);
    assertFalse(responses.isEmpty());
  }

  @Test
  @DisplayName("Get Item Buffers By ItemId and OrgId and Uom- Empty Result")
  void getItemBuffersByItemIdAndOrgIdAndUomEmptyResult() throws CommonServiceException {
    when(itemBufferPersistenceService.findItemBuffersListByItemIdAndOrgIdAndUom(
            TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM))
        .thenReturn(Collections.emptyList());

    List<ItemBufferResponse> responses =
        itemBufferService.getItemBuffersByItemIdAndOrgIdAndUom(
            TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM);

    verify(itemBufferPersistenceService, times(1))
        .findItemBuffersListByItemIdAndOrgIdAndUom(TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM);
    assertNotNull(responses);
    assertTrue(responses.isEmpty());
  }

  @Test
  @DisplayName("Get Items List based on OrgId with pagination - Happy path")
  void getItemsListWithConfiguredBuffers() throws CommonServiceException {
    PageParams pageParams = new PageParams();
    pageParams.setPageSize(Optional.of(10));
    pageParams.setPageNo(Optional.of(1));
    List<ItemBufferDomainDto> itemBufferDomainDtos = List.of(testUtil.getItemBufferDomainDto());

    Pageable pageable =
        PageRequest.of(pageParams.getPageNo().orElse(1) - 1, pageParams.getPageSize().orElse(10));
    Page<ItemBufferDomainDto> page =
        new PageImpl<>(itemBufferDomainDtos, pageable, itemBufferDomainDtos.size());

    when(itemBufferPersistenceService.findByOrgId(TestUtil.ORG_ID, pageable)).thenReturn(page);

    PageResponseForItemBuffer result =
        itemBufferService.getItemsListWithConfiguredBuffers(TestUtil.ORG_ID, null, pageParams);

    assertNotNull(result);
    assertEquals(pageParams.getPageNo().orElse(1), result.getPagination().getCurrentPage());
    assertEquals(1, result.getPagination().getTotalPages());
    assertEquals(itemBufferDomainDtos.size(), result.getPagination().getTotalRecords());

    verify(itemBufferPersistenceService, times(1)).findByOrgId(TestUtil.ORG_ID, pageable);

    assertEquals(page.getNumber() + 1, result.getPagination().getCurrentPage());
    assertEquals(page.getTotalPages(), result.getPagination().getTotalPages());
    assertEquals(page.getTotalElements(), result.getPagination().getTotalRecords());
  }

  @Test
  @DisplayName("Get Items List based on itemId and OrgId with pagination - Happy path")
  void getItemsListByOrgIdAndItemIdWithConfiguredBuffers() throws CommonServiceException {
    PageParams pageParams = new PageParams();
    pageParams.setPageSize(Optional.of(10));
    pageParams.setPageNo(Optional.of(1));
    List<ItemBufferDomainDto> itemBufferDomainDtos = List.of(testUtil.getItemBufferDomainDto());

    Pageable pageable =
        PageRequest.of(pageParams.getPageNo().get() - 1, pageParams.getPageSize().get());
    Page<ItemBufferDomainDto> page =
        new PageImpl<>(itemBufferDomainDtos, pageable, itemBufferDomainDtos.size());

    when(itemBufferPersistenceService.findItemBufferByItemIdsAndOrgId(
            TestUtil.ORG_ID, List.of(TestUtil.ITEM_ID_1), pageable))
        .thenReturn(page);

    PageResponseForItemBuffer result =
        itemBufferService.getItemsListWithConfiguredBuffers(
            TestUtil.ORG_ID, TestUtil.ITEM_ID_1, pageParams);

    assertNotNull(result);
    assertEquals(pageParams.getPageNo().get(), result.getPagination().getCurrentPage());
    assertEquals(1, result.getPagination().getTotalPages());
    assertEquals(itemBufferDomainDtos.size(), result.getPagination().getTotalRecords());

    verify(itemBufferPersistenceService, times(1))
        .findItemBufferByItemIdsAndOrgId(TestUtil.ORG_ID, List.of(TestUtil.ITEM_ID_1), pageable);

    assertEquals(page.getNumber() + 1, result.getPagination().getCurrentPage());
    assertEquals(page.getTotalPages(), result.getPagination().getTotalPages());
    assertEquals(page.getTotalElements(), result.getPagination().getTotalRecords());
  }

  @Test
  @DisplayName("Fetch Item Buffer - Happy path")
  void fetchItemBufferHappyPath() throws Exception {
    ItemBufferDomainDto bufferDomainDto = testUtil.getItemBufferDomainDto();
    when(itemBufferPersistenceService.findItemBufferByOrgIdAndId(TestUtil.ORG_ID, 1L))
        .thenReturn(Optional.of(bufferDomainDto));
    ItemBufferResponse response = itemBufferService.fetchItemBuffer(TestUtil.ORG_ID, 1L);
    assertNotNull(response);
    assertEquals(bufferDomainDto.getItemId(), response.getItemId());
    verify(itemBufferPersistenceService, times(1)).findItemBufferByOrgIdAndId(TestUtil.ORG_ID, 1L);
  }

  @Test
  @DisplayName("Fetch Item Buffer - Buffer Not Found")
  void fetchItemBufferNotFound() throws Exception {
    when(itemBufferPersistenceService.findItemBufferByOrgIdAndId(TestUtil.ORG_ID, 1L))
        .thenReturn(Optional.empty());

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> itemBufferService.fetchItemBuffer(TestUtil.ORG_ID, 1L));

    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    assertEquals("Item buffer not found for given orgId and Id", exception.getMessage());
    verify(itemBufferPersistenceService, times(1)).findItemBufferByOrgIdAndId(TestUtil.ORG_ID, 1L);
  }

  @Test
  @DisplayName("Delete Item Buffer By OrgId And Id - Happy path")
  void deleteItemBufferByOrgIdAndIdHappyPath() throws Exception {
    ItemBufferDomainDto bufferDomainDto = testUtil.getItemBufferDomainDto();
    when(itemBufferPersistenceService.findItemBufferByOrgIdAndId(TestUtil.ORG_ID, 1L))
        .thenReturn(Optional.of(bufferDomainDto));

    ItemBufferResponse response =
        itemBufferService.deleteItemBufferByOrgIdAndId(TestUtil.ORG_ID, 1L);

    assertNotNull(response);
    assertEquals(bufferDomainDto.getItemId(), response.getItemId());
    verify(itemBufferPersistenceService, times(1)).findItemBufferByOrgIdAndId(TestUtil.ORG_ID, 1L);
    verify(itemBufferPersistenceService, times(1)).deleteItemBuffer(bufferDomainDto);
  }

  @Test
  @DisplayName("Delete Item Buffer By OrgId And Id - Buffer Not Found")
  void deleteItemBufferByOrgIdAndIdNotFound() throws Exception {
    when(itemBufferPersistenceService.findItemBufferByOrgIdAndId(TestUtil.ORG_ID, 1L))
        .thenReturn(Optional.empty());

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> itemBufferService.deleteItemBufferByOrgIdAndId(TestUtil.ORG_ID, 1L));

    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    assertEquals("Item buffer not found for given orgId and Id", exception.getMessage());
    verify(itemBufferPersistenceService, times(1)).findItemBufferByOrgIdAndId(TestUtil.ORG_ID, 1L);
    verify(itemBufferPersistenceService, never()).deleteItemBuffer(any());
  }

  @Test
  @DisplayName("Update Item Buffer - Happy path")
  void updateItemBufferHappyPath() throws Exception {
    ItemBufferUpdateRequest updateRequest = testUtil.getItemBufferUpdateRequest();
    ItemBufferDomainDto existingBuffer = testUtil.getItemBufferDomainDto();
    existingBuffer.setId(2L);
    existingBuffer.setOrgId(TestUtil.ORG_ID);

    when(itemBufferPersistenceService.findItemBufferByOrgIdAndId(TestUtil.ORG_ID, 2L))
        .thenReturn(Optional.of(existingBuffer));
    when(itemBufferPersistenceService.save(any(ItemBufferDomainDto.class)))
        .thenReturn(existingBuffer);

    ItemBufferResponse response =
        itemBufferService.updateItemBuffer(TestUtil.ORG_ID, 2L, updateRequest);

    assertNotNull(response);
    assertEquals(existingBuffer.getItemId(), response.getItemId());
    verify(itemBufferPersistenceService, times(1)).findItemBufferByOrgIdAndId(TestUtil.ORG_ID, 2L);
    verify(itemBufferPersistenceService, times(1)).save(any(ItemBufferDomainDto.class));
  }

  @Test
  @DisplayName("Update Item Buffer - Invalid Buffer Hours")
  void updateItemBufferInvalidBufferHours() {
    ItemBufferUpdateRequest updateRequest = testUtil.getItemBufferUpdateRequest();
    updateRequest.setBufferHours(-1.0);
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> itemBufferService.updateItemBuffer(TestUtil.ORG_ID, 2L, updateRequest));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    assertEquals("bufferHours cannot be negative", exception.getMessage());
  }

  @Test
  @DisplayName("Update Item Buffer - Item Buffer Hours not present")
  void updateItemBufferByOrgIdAndIdNotFound() throws Exception {
    when(itemBufferPersistenceService.findItemBufferByOrgIdAndId(TestUtil.ORG_ID, 1L))
        .thenReturn(Optional.empty());

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                itemBufferService.updateItemBuffer(
                    TestUtil.ORG_ID, 1L, testUtil.getItemBufferUpdateRequest()));

    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    assertEquals("Item buffer not found for given orgId and Id", exception.getMessage());
    verify(itemBufferPersistenceService, times(1)).findItemBufferByOrgIdAndId(TestUtil.ORG_ID, 1L);
    verify(itemBufferPersistenceService, never()).saveItemBuffer(any());
  }
}
