/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.persistence.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.item.persistence.TestUtil;
import com.nextuple.item.persistence.domain.ItemBufferDomainDto;
import com.nextuple.item.persistence.entity.ItemBufferEntity;
import com.nextuple.item.persistence.mapper.ItemBufferEntityMapper;
import com.nextuple.item.persistence.repository.ItemBufferRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

class ItemBufferPersistenceServiceImplTest {

  private final String ERROR_WHILE_SAVING = "Error while saving the item buffer.";
  private final String ERROR_WHILE_FINDING = "Error while finding the item buffer.";
  private final String ERROR_WHILE_DELETING = "Error while deleting the item buffer.";

  @InjectMocks private ItemBufferPersistenceServiceImpl itemBufferPersistenceService;

  @Mock private ItemBufferRepository itemBufferRepository;

  @Mock private ItemBufferEntityMapper itemBufferEntityMapper;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    // Added this
    ReflectionTestUtils.setField(itemBufferPersistenceService, "repository", itemBufferRepository);
    ReflectionTestUtils.setField(itemBufferPersistenceService, "mapper", itemBufferEntityMapper);
  }

  @Test
  @DisplayName("Save Item Buffer - Happy path")
  void saveItemBufferTest() throws CommonServiceException {
    when(itemBufferRepository.save(any())).thenReturn(testUtil.getItemBufferEntity());
    when(itemBufferEntityMapper.toEntity(any(ItemBufferDomainDto.class)))
        .thenReturn(testUtil.getItemBufferEntity());
    when(itemBufferEntityMapper.toDomain(any(ItemBufferEntity.class)))
        .thenReturn(testUtil.getItemBufferDomainDto());
    ItemBufferDomainDto itemBuffer =
        itemBufferPersistenceService.saveItemBuffer(testUtil.getItemBufferDomainDto());
    assertEquals(testUtil.getItemBufferDomainDto(), itemBuffer);
    verify(itemBufferRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Save Item Buffer - Exception")
  void saveItemBufferExceptionTest() {
    when(itemBufferRepository.save(any())).thenThrow(new RuntimeException(ERROR_WHILE_SAVING));
    when(itemBufferEntityMapper.toEntity(any(ItemBufferDomainDto.class)))
        .thenReturn(testUtil.getItemBufferEntity());
    when(itemBufferEntityMapper.toDomain(any(ItemBufferEntity.class)))
        .thenReturn(testUtil.getItemBufferDomainDto());
    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () -> itemBufferPersistenceService.saveItemBuffer(testUtil.getItemBufferDomainDto()));
    assertEquals(ERROR_WHILE_SAVING, exception.getMessage());
    verify(itemBufferRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Find Item Buffer by orgId and ItemId and Uom - Happy path")
  void findByItemIdAndOrgIdAndUomTest() throws CommonServiceException {
    List<ItemBufferEntity> itemBufferEntityList = new ArrayList<>();
    itemBufferEntityList.add(testUtil.getItemBufferEntity());
    when(itemBufferRepository.findByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(itemBufferEntityList);
    List<ItemBufferDomainDto> itemBufferDomainDtoList = List.of(testUtil.getItemBufferDomainDto());
    when(itemBufferEntityMapper.toDomain(anyList())).thenReturn(itemBufferDomainDtoList);

    List<ItemBufferDomainDto> optionalItemBufferResponse =
        itemBufferPersistenceService.findByItemIdAndOrgIdAndUom(
            TestUtil.ORG_ID, TestUtil.ITEM_ID, TestUtil.UOM);
    assertEquals(itemBufferDomainDtoList, optionalItemBufferResponse);
    assertEquals(
        testUtil.getItemBufferEntity().getBufferHours(),
        optionalItemBufferResponse.get(0).getBufferHours());
    assertEquals(
        testUtil.getItemBufferEntity().getBufferStartDate(),
        optionalItemBufferResponse.get(0).getBufferStartDate());
    assertEquals(
        testUtil.getItemBufferEntity().getBufferEndDate(),
        optionalItemBufferResponse.get(0).getBufferEndDate());
    verify(itemBufferRepository, times(1)).findByItemIdAndOrgIdAndUom(any(), any(), any());
  }

  @Test
  @DisplayName("Find Item Buffer by orgId and ItemId and Uom - Exception")
  void findByItemIdAndOrgIdAndUomTestException() {
    when(itemBufferRepository.findByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenThrow(new RuntimeException(ERROR_WHILE_FINDING));
    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                itemBufferPersistenceService.findByItemIdAndOrgIdAndUom(
                    TestUtil.ORG_ID, TestUtil.ITEM_ID, TestUtil.UOM));
    assertEquals(ERROR_WHILE_FINDING, exception.getMessage());
    verify(itemBufferRepository, times(1)).findByItemIdAndOrgIdAndUom(any(), any(), any());
  }

  @Test
  @DisplayName(
      "Find Item Buffer by orgId and ItemId and Uom and bufferHours and bufferStartDate and bufferEndDate- Happy path")
  void findItemBufferTest() throws CommonServiceException {
    when(itemBufferRepository
            .findByItemIdAndOrgIdAndUomAndAndBufferStartDateAndBufferEndDateAndBufferHours(
                any(), any(), any(), any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getItemBufferEntity()));
    when(itemBufferEntityMapper.toDomain(any(ItemBufferEntity.class)))
        .thenReturn(new ItemBufferDomainDto());
    Optional<ItemBufferDomainDto> optionalItemBufferEntityResponse =
        itemBufferPersistenceService.findItemBuffer(new ItemBufferDomainDto());
    assertTrue(optionalItemBufferEntityResponse.isPresent());
    verify(itemBufferRepository, times(1))
        .findByItemIdAndOrgIdAndUomAndAndBufferStartDateAndBufferEndDateAndBufferHours(
            any(), any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName(
      "Find Item Buffer by orgId and ItemId and Uom and bufferHours and bufferStartDate and bufferEndDate- Exception")
  void findItemBufferTestException() {
    when(itemBufferRepository
            .findByItemIdAndOrgIdAndUomAndAndBufferStartDateAndBufferEndDateAndBufferHours(
                any(), any(), any(), any(), any(), any()))
        .thenThrow(new RuntimeException(ERROR_WHILE_FINDING));
    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () -> itemBufferPersistenceService.findItemBuffer(new ItemBufferDomainDto()));
    assertEquals(ERROR_WHILE_FINDING, exception.getMessage());
    verify(itemBufferRepository, times(1))
        .findByItemIdAndOrgIdAndUomAndAndBufferStartDateAndBufferEndDateAndBufferHours(
            any(), any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Find Item Buffers by orgId and itemId and uom - Happy path")
  void findItemBuffersListByItemIdAndOrgIdAndUomHappyPathTest() throws CommonServiceException {
    List<ItemBufferEntity> itemBufferEntities = new ArrayList<>();
    itemBufferEntities.add(testUtil.getItemBufferEntity());

    when(itemBufferRepository.findByItemIdAndOrgIdAndUom(
            TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM))
        .thenReturn(itemBufferEntities);

    when(itemBufferEntityMapper.toDomain(itemBufferEntities))
        .thenReturn(Collections.singletonList(new ItemBufferDomainDto()));

    List<ItemBufferDomainDto> result =
        itemBufferPersistenceService.findItemBuffersListByItemIdAndOrgIdAndUom(
            TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM);
    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    verify(itemBufferRepository, times(1))
        .findByItemIdAndOrgIdAndUom(TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM);

    verify(itemBufferEntityMapper, times(1)).toDomain(itemBufferEntities);
  }

  @Test
  @DisplayName("Find Item Buffers by orgId and itemId and uom- Exception")
  void findItemBuffersListByItemIdAndOrgIdAndUomExceptionTest() {
    when(itemBufferRepository.findByItemIdAndOrgIdAndUom(
            TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM))
        .thenThrow(new RuntimeException("Error while finding"));
    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                itemBufferPersistenceService.findItemBuffersListByItemIdAndOrgIdAndUom(
                    TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM));

    assertEquals(ERROR_WHILE_FINDING, exception.getMessage());
    verify(itemBufferRepository, times(1))
        .findByItemIdAndOrgIdAndUom(TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM);
    verify(itemBufferEntityMapper, never()).toDomain(anyList());
  }

  @Test
  @DisplayName("Find Item Buffers by orgId - Happy path")
  void findByOrgIdHappyPathTest() throws CommonServiceException {
    Pageable pageable = PageRequest.of(0, 10);
    List<ItemBufferEntity> itemBufferEntities = new ArrayList<>();
    itemBufferEntities.add(testUtil.getItemBufferEntity());
    Page<ItemBufferEntity> itemBufferPage =
        new PageImpl<>(itemBufferEntities, pageable, itemBufferEntities.size());
    when(itemBufferRepository.findDistinctItemIdAndOrgIdAndUomByOrgId(TestUtil.ORG_ID, pageable))
        .thenReturn(itemBufferPage);
    when(itemBufferEntityMapper.toDomain(itemBufferEntities))
        .thenReturn(Collections.singletonList(new ItemBufferDomainDto()));
    Page<ItemBufferDomainDto> result =
        itemBufferPersistenceService.findByOrgId(TestUtil.ORG_ID, pageable);
    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(1, result.getNumberOfElements());

    verify(itemBufferRepository, times(1))
        .findDistinctItemIdAndOrgIdAndUomByOrgId(TestUtil.ORG_ID, pageable);
    verify(itemBufferEntityMapper, times(1)).toDomain(itemBufferEntities);
  }

  @Test
  @DisplayName("Find Item Buffers by orgId - Exception")
  void findByOrgIdExceptionTest() {
    Pageable pageable = PageRequest.of(0, 10);
    when(itemBufferRepository.findDistinctItemIdAndOrgIdAndUomByOrgId(TestUtil.ORG_ID, pageable))
        .thenThrow(new RuntimeException(ERROR_WHILE_FINDING));
    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () -> itemBufferPersistenceService.findByOrgId(TestUtil.ORG_ID, pageable));
    assertEquals(ERROR_WHILE_FINDING, exception.getMessage());
    verify(itemBufferRepository, times(1))
        .findDistinctItemIdAndOrgIdAndUomByOrgId(TestUtil.ORG_ID, pageable);
    verify(itemBufferEntityMapper, never()).toDomain(anyList());
  }

  @Test
  @DisplayName("Find Item Buffers by itemIds and orgId - Happy path")
  void findByItemIdsAndOrgIdHappyPathTest() throws CommonServiceException {
    Pageable pageable = PageRequest.of(0, 10);
    List<ItemBufferEntity> itemBufferEntities = new ArrayList<>();
    itemBufferEntities.add(testUtil.getItemBufferEntity());
    Page<ItemBufferEntity> itemBufferPage =
        new PageImpl<>(itemBufferEntities, pageable, itemBufferEntities.size());
    when(itemBufferRepository.findDistinctItemIdAndOrgIdAndUomByItemIdsAndOrgId(
            List.of(TestUtil.ITEM_ID_1), TestUtil.ORG_ID, pageable))
        .thenReturn(itemBufferPage);
    when(itemBufferEntityMapper.toDomain(itemBufferEntities))
        .thenReturn(Collections.singletonList(new ItemBufferDomainDto()));
    Page<ItemBufferDomainDto> result =
        itemBufferPersistenceService.findItemBufferByItemIdsAndOrgId(
            TestUtil.ORG_ID, List.of(TestUtil.ITEM_ID_1), pageable);
    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(1, result.getNumberOfElements());

    verify(itemBufferRepository, times(1))
        .findDistinctItemIdAndOrgIdAndUomByItemIdsAndOrgId(
            List.of(TestUtil.ITEM_ID_1), TestUtil.ORG_ID, pageable);
    verify(itemBufferEntityMapper, times(1)).toDomain(itemBufferEntities);
  }

  @Test
  @DisplayName("Find Item Buffers by itemIds and orgId - Exception")
  void findByItemIdsAndOrgIdExceptionTest() {
    Pageable pageable = PageRequest.of(0, 10);
    when(itemBufferRepository.findDistinctItemIdAndOrgIdAndUomByItemIdsAndOrgId(
            List.of(TestUtil.ITEM_ID_1), TestUtil.ORG_ID, pageable))
        .thenThrow(new RuntimeException(ERROR_WHILE_FINDING));
    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                itemBufferPersistenceService.findItemBufferByItemIdsAndOrgId(
                    TestUtil.ORG_ID, List.of(TestUtil.ITEM_ID_1), pageable));
    assertEquals(ERROR_WHILE_FINDING, exception.getMessage());
    verify(itemBufferRepository, times(1))
        .findDistinctItemIdAndOrgIdAndUomByItemIdsAndOrgId(
            List.of(TestUtil.ITEM_ID_1), TestUtil.ORG_ID, pageable);
    verify(itemBufferEntityMapper, never()).toDomain(anyList());
  }

  @Test
  @DisplayName("Delete Item Buffer - Happy path")
  void deleteItemBufferTest() throws CommonServiceException {
    itemBufferPersistenceService.deleteItemBuffer(testUtil.getItemBufferDomainDto());
    verify(itemBufferRepository, times(1)).delete(any());
  }

  @Test
  @DisplayName("Delete Item Buffer - Exception")
  void deleteItemBufferExceptionTest() {
    when(itemBufferEntityMapper.toEntity(any(ItemBufferDomainDto.class)))
        .thenReturn(testUtil.getItemBufferEntity());
    when(itemBufferEntityMapper.toDomain(any(ItemBufferEntity.class)))
        .thenReturn(testUtil.getItemBufferDomainDto());

    RuntimeException runtimeException = new RuntimeException(ERROR_WHILE_DELETING);
    doThrow(runtimeException).when(itemBufferRepository).delete(any());

    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () -> itemBufferPersistenceService.deleteItemBuffer(testUtil.getItemBufferDomainDto()));
    assertEquals(ERROR_WHILE_DELETING, exception.getMessage());
    verify(itemBufferRepository, times(1)).delete(any());
  }

  @Test
  @DisplayName("Find Item Buffer by OrgId and Id - Happy path")
  void findItemBufferByOrgIdAndIdHappyPathTest() throws CommonServiceException {
    ItemBufferEntity itemBufferEntity = testUtil.getItemBufferEntity();
    when(itemBufferRepository.findByOrgIdAndId(TestUtil.ORG_ID, 1L))
        .thenReturn(Optional.of(itemBufferEntity));

    ItemBufferDomainDto itemBufferDomainDto = testUtil.getItemBufferDomainDto();
    when(itemBufferEntityMapper.toDomain(itemBufferEntity)).thenReturn(itemBufferDomainDto);

    Optional<ItemBufferDomainDto> result =
        itemBufferPersistenceService.findItemBufferByOrgIdAndId(TestUtil.ORG_ID, 1L);

    assertTrue(result.isPresent());
    assertEquals(itemBufferDomainDto, result.get());

    verify(itemBufferRepository, times(1)).findByOrgIdAndId(TestUtil.ORG_ID, 1L);
    verify(itemBufferEntityMapper, times(1)).toDomain(itemBufferEntity);
  }

  @Test
  @DisplayName("Find Item Buffer by OrgId and Id - Not found")
  void findItemBufferByOrgIdAndIdNotFoundTest() throws CommonServiceException {
    when(itemBufferRepository.findByOrgIdAndId(TestUtil.ORG_ID, 1L)).thenReturn(Optional.empty());

    Optional<ItemBufferDomainDto> result =
        itemBufferPersistenceService.findItemBufferByOrgIdAndId(TestUtil.ORG_ID, 1L);

    assertFalse(result.isPresent());

    verify(itemBufferRepository, times(1)).findByOrgIdAndId(TestUtil.ORG_ID, 1L);
    verify(itemBufferEntityMapper, never()).toDomain((ItemBufferEntity) any());
  }

  @Test
  @DisplayName("Find Item Buffer by OrgId and Id - Exception")
  void findItemBufferByOrgIdAndIdExceptionTest() {
    when(itemBufferRepository.findByOrgIdAndId(TestUtil.ORG_ID, 1L))
        .thenThrow(new RuntimeException(ERROR_WHILE_FINDING));

    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () -> itemBufferPersistenceService.findItemBufferByOrgIdAndId(TestUtil.ORG_ID, 1L));

    assertEquals(ERROR_WHILE_FINDING, exception.getMessage());
    verify(itemBufferRepository, times(1)).findByOrgIdAndId(TestUtil.ORG_ID, 1L);
    verify(itemBufferEntityMapper, never()).toDomain((ItemBufferEntity) any());
  }

  @Test
  @DisplayName(
      "Find Item Buffer by orgId, itemId, uom, bufferStartDate, and bufferEndDate - Happy path")
  void findByItemIdAndOrgIdAndUomAndBufferStartDateAndBufferEndDateHappyPathTest()
      throws CommonServiceException {
    Date bufferStartDate = new Date();
    Date bufferEndDate = new Date();
    ItemBufferEntity itemBufferEntity = testUtil.getItemBufferEntity();
    when(itemBufferRepository.findByItemIdAndOrgIdAndUomAndBufferStartDateAndBufferEndDate(
            TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM, bufferStartDate, bufferEndDate))
        .thenReturn(Optional.of(itemBufferEntity));
    ItemBufferDomainDto expectedResponse = testUtil.getItemBufferDomainDto();
    when(itemBufferEntityMapper.toDomain(itemBufferEntity)).thenReturn(expectedResponse);

    Optional<ItemBufferDomainDto> result =
        itemBufferPersistenceService.findByItemIdAndOrgIdAndUomAndBufferStartDateAndBufferEndDate(
            TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM, bufferStartDate, bufferEndDate);

    assertTrue(result.isPresent());
    assertEquals(expectedResponse, result.get());
    verify(itemBufferRepository, times(1))
        .findByItemIdAndOrgIdAndUomAndBufferStartDateAndBufferEndDate(
            TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM, bufferStartDate, bufferEndDate);
    verify(itemBufferEntityMapper, times(1)).toDomain(itemBufferEntity);
  }

  @Test
  @DisplayName(
      "Find Item Buffer by orgId, itemId, uom, bufferStartDate, and bufferEndDate - Exception")
  void findByItemIdAndOrgIdAndUomAndBufferStartDateAndBufferEndDateExceptionTest() {
    Date bufferStartDate = new Date();
    Date bufferEndDate = new Date();
    when(itemBufferRepository.findByItemIdAndOrgIdAndUomAndBufferStartDateAndBufferEndDate(
            TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM, bufferStartDate, bufferEndDate))
        .thenThrow(new RuntimeException(ERROR_WHILE_FINDING));

    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                itemBufferPersistenceService
                    .findByItemIdAndOrgIdAndUomAndBufferStartDateAndBufferEndDate(
                        TestUtil.ITEM_ID,
                        TestUtil.ORG_ID,
                        TestUtil.UOM,
                        bufferStartDate,
                        bufferEndDate));

    assertEquals(ERROR_WHILE_FINDING, exception.getMessage());
    verify(itemBufferRepository, times(1))
        .findByItemIdAndOrgIdAndUomAndBufferStartDateAndBufferEndDate(
            TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM, bufferStartDate, bufferEndDate);
    verify(itemBufferEntityMapper, never()).toDomain((ItemBufferEntity) any());
  }

  @Test
  @DisplayName("Find Item Buffers by ItemIds, OrgId, and Promising Engine Date - Happy path")
  void findItemBuffersListByItemIdsAndOrgIdAndPromisingEngineDateHappyPathTest()
      throws CommonServiceException {
    List<String> itemIdList = List.of("item1", "item2");
    Date promisingEngineDate = new Date();
    List<ItemBufferEntity> itemBufferEntities = new ArrayList<>();
    itemBufferEntities.add(testUtil.getItemBufferEntity());

    when(itemBufferRepository.findByItemIdInAndOrgIdAndBufferStartDateBeforeAndBufferEndDateAfter(
            itemIdList, TestUtil.ORG_ID, promisingEngineDate, promisingEngineDate))
        .thenReturn(itemBufferEntities);

    when(itemBufferEntityMapper.toDomain(itemBufferEntities))
        .thenReturn(List.of(testUtil.getItemBufferDomainDto()));

    List<ItemBufferDomainDto> result =
        itemBufferPersistenceService.findItemBuffersListByItemIdsAndOrgIdAndPromisingEngineDate(
            itemIdList, TestUtil.ORG_ID, promisingEngineDate);
    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    assertEquals(testUtil.getItemBufferDomainDto(), result.get(0));
    verify(itemBufferRepository, times(1))
        .findByItemIdInAndOrgIdAndBufferStartDateBeforeAndBufferEndDateAfter(
            itemIdList, TestUtil.ORG_ID, promisingEngineDate, promisingEngineDate);
    verify(itemBufferEntityMapper, times(1)).toDomain(itemBufferEntities);
  }

  @Test
  @DisplayName("Find Item Buffers by ItemIds, OrgId, and Promising Engine Date - Exception")
  void findItemBuffersListByItemIdsAndOrgIdAndPromisingEngineDateExceptionTest() {
    List<String> itemIdList = List.of("item1", "item2");
    Date promisingEngineDate = new Date();

    when(itemBufferRepository.findByItemIdInAndOrgIdAndBufferStartDateBeforeAndBufferEndDateAfter(
            itemIdList, TestUtil.ORG_ID, promisingEngineDate, promisingEngineDate))
        .thenThrow(new RuntimeException(ERROR_WHILE_FINDING));

    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                itemBufferPersistenceService
                    .findItemBuffersListByItemIdsAndOrgIdAndPromisingEngineDate(
                        itemIdList, TestUtil.ORG_ID, promisingEngineDate));

    assertEquals(ERROR_WHILE_FINDING, exception.getMessage());

    verify(itemBufferRepository, times(1))
        .findByItemIdInAndOrgIdAndBufferStartDateBeforeAndBufferEndDateAfter(
            itemIdList, TestUtil.ORG_ID, promisingEngineDate, promisingEngineDate);
    verify(itemBufferEntityMapper, never()).toDomain(anyList());
  }
}
