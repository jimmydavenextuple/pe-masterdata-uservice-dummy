/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.item.persistence.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.item.persistence.TestUtil;
import com.nextuple.item.persistence.domain.ItemDomainDto;
import com.nextuple.item.persistence.domain.key.ItemDomainKey;
import com.nextuple.item.persistence.entity.ItemEntity;
import com.nextuple.item.persistence.entity.key.ItemKey;
import com.nextuple.item.persistence.exception.ItemBatchingDomainException;
import com.nextuple.item.persistence.exception.ItemDomainException;
import com.nextuple.item.persistence.mapper.ItemEntityMapper;
import com.nextuple.item.persistence.repository.ItemRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

class ItemPersistenceServiceImplTest {
  @InjectMocks private ItemPersistenceServiceImpl itemPersistenceService;
  @InjectMocks private TestUtil testUtil;

  @Mock private ItemRepository itemRepository;

  @Mock private ItemEntityMapper itemEntityMapper;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    // Added this
    ReflectionTestUtils.setField(itemPersistenceService, "repository", itemRepository);
    ReflectionTestUtils.setField(itemPersistenceService, "mapper", itemEntityMapper);
  }

  @Test
  void saveItemTest() throws ItemDomainException {
    ItemEntity itemEntity = testUtil.getItemEntity();
    ItemDomainDto itemDomainDto = testUtil.getItemDomainDto();
    when(itemRepository.save(any())).thenReturn(itemEntity);
    when(itemEntityMapper.toEntity(any(ItemDomainDto.class))).thenReturn(itemEntity);
    when(itemEntityMapper.toDomain(any(ItemEntity.class))).thenReturn(itemDomainDto);
    ItemDomainDto item = itemPersistenceService.saveItem(itemDomainDto);
    Assertions.assertEquals(itemDomainDto, item);
    verify(itemRepository, times(1)).save(any());
  }

  @Test
  void saveItemExceptionTest() {
    ItemEntity itemEntity = testUtil.getItemEntity();
    ItemDomainDto itemDomainDto = testUtil.getItemDomainDto();
    when(itemRepository.save(any())).thenThrow(new RuntimeException("Error while saving"));
    when(itemEntityMapper.toEntity(any(ItemDomainDto.class))).thenReturn(itemEntity);
    when(itemEntityMapper.toDomain(any(ItemEntity.class))).thenReturn(itemDomainDto);
    Exception exception =
        assertThrows(
            ItemDomainException.class, () -> itemPersistenceService.saveItem(itemDomainDto));
    Assertions.assertEquals("Error while saving the item", exception.getMessage());
    verify(itemRepository, times(1)).save(any());
  }

  @Test
  void getItemDetailsTest() throws ItemDomainException {
    ItemEntity itemEntity = testUtil.getItemEntity();

    ItemDomainDto itemDomainDto = testUtil.getItemDomainDto();
    ItemKey id = testUtil.getItemId();
    when(itemRepository.findById(any(ItemKey.class))).thenReturn(Optional.of(itemEntity));
    when(itemEntityMapper.toEntityKey(any(ItemDomainKey.class)))
        .thenReturn(
            ItemKey.builder()
                .orgId(itemDomainDto.getOrgId())
                .itemId(itemDomainDto.getItemId())
                .uom(itemDomainDto.getUom())
                .build());
    when(itemEntityMapper.toDomain(any(ItemEntity.class))).thenReturn(itemDomainDto);

    Optional<ItemDomainDto> optionalItemEntity =
        itemPersistenceService.findItemByItemIdAndOrgIdAndUom(
            TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM);
    Assertions.assertEquals(itemDomainDto, optionalItemEntity.get());
    Assertions.assertEquals(
        itemEntity.getIsDSVEligible(), optionalItemEntity.get().getIsDSVEligible());
    Assertions.assertEquals(
        itemEntity.getDepartmentName(), optionalItemEntity.get().getDepartmentName());
    Assertions.assertEquals(
        itemEntity.getDepartmentNumber(), optionalItemEntity.get().getDepartmentNumber());
    Assertions.assertEquals(itemEntity.getImageUrl(), optionalItemEntity.get().getImageUrl());
    Assertions.assertEquals(
        itemEntity.getShortDescription(), optionalItemEntity.get().getShortDescription());

    verify(itemRepository, times(1)).findById(any());
    Assertions.assertEquals(TestUtil.ITEM_ID, id.getItemId());
    Assertions.assertEquals(TestUtil.ORG_ID, id.getOrgId());
    Assertions.assertEquals(TestUtil.UOM, id.getUom());
  }

  @Test
  void getItemDetailsTestException() {
    when(itemEntityMapper.toEntityKey(any(ItemDomainKey.class)))
        .thenReturn(
            ItemKey.builder()
                .orgId(TestUtil.ORG_ID)
                .itemId(TestUtil.ITEM_ID)
                .uom(TestUtil.UOM)
                .build());

    when(itemRepository.findById(any(ItemKey.class)))
        .thenThrow(new RuntimeException("Error while fetching details"));

    Exception exception =
        assertThrows(
            ItemDomainException.class,
            () ->
                itemPersistenceService.findItemByItemIdAndOrgIdAndUom(
                    TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM));
    Assertions.assertEquals("Error while finding item", exception.getMessage());
    verify(itemRepository, times(1)).findById(any());
  }

  @Test
  void itemDeletionTest() throws ItemDomainException {
    when(itemEntityMapper.toEntity(any(ItemDomainDto.class))).thenReturn(testUtil.getItemEntity());
    doNothing().when(itemRepository).delete(any(ItemEntity.class));
    itemPersistenceService.deleteItem(testUtil.getItemDomainDto());
    verify(itemRepository, times(1)).delete(any(ItemEntity.class));
  }

  @Test
  void itemDeletionTestException() {
    when(itemEntityMapper.toEntity(any(ItemDomainDto.class))).thenReturn(testUtil.getItemEntity());
    doThrow(new RuntimeException("error while deleting"))
        .when(itemRepository)
        .delete(any(ItemEntity.class));

    Exception exception =
        assertThrows(
            ItemDomainException.class,
            () -> itemPersistenceService.deleteItem(testUtil.getItemDomainDto()));
    Assertions.assertEquals("Error while deleting item", exception.getMessage());
    verify(itemRepository, times(1)).delete(any());
  }

  @Test
  void getItemListTest() throws ItemBatchingDomainException {
    ItemEntity itemEntity = testUtil.getItemEntity();
    ItemKey id = testUtil.getItemId();
    List<String> itemList = new ArrayList<>();
    List<ItemEntity> itemEntityList = new ArrayList<>();
    itemEntityList.add(itemEntity);
    when(itemRepository.findByOrgIdAndItemIdIn(any(), any())).thenReturn(itemEntityList);
    List<ItemDomainDto> itemDomainDtoList = List.of(testUtil.getItemDomainDto());
    when(itemEntityMapper.toDomain(anyList())).thenReturn(itemDomainDtoList);

    List<ItemDomainDto> optionalItemEntityResponse =
        itemPersistenceService.findItemListByItemIdsAndOrgId(itemList, TestUtil.ORG_ID);
    Assertions.assertEquals(itemDomainDtoList, optionalItemEntityResponse);
    Assertions.assertEquals(
        itemEntity.getIsDSVEligible(), optionalItemEntityResponse.get(0).getIsDSVEligible());
    Assertions.assertEquals(
        itemEntity.getDepartmentName(), optionalItemEntityResponse.get(0).getDepartmentName());
    Assertions.assertEquals(
        itemEntity.getDepartmentNumber(), optionalItemEntityResponse.get(0).getDepartmentNumber());
    Assertions.assertEquals(
        itemEntity.getImageUrl(), optionalItemEntityResponse.get(0).getImageUrl());
    Assertions.assertEquals(
        itemEntity.getShortDescription(), optionalItemEntityResponse.get(0).getShortDescription());

    verify(itemRepository, times(1)).findByOrgIdAndItemIdIn(any(), any());
    Assertions.assertEquals(TestUtil.ITEM_ID, id.getItemId());
    Assertions.assertEquals(TestUtil.ORG_ID, id.getOrgId());
    Assertions.assertEquals(TestUtil.UOM, id.getUom());
  }

  @Test
  @DisplayName("Happy Path: ItemId records got fetched successfully")
  void getItemListPaginated() throws ItemBatchingDomainException {
    ItemEntity item1 = testUtil.getItemEntity();
    item1.setItemId(TestUtil.ITEM_ID_1);
    ItemEntity item2 = testUtil.getItemEntity();
    item2.setItemId(TestUtil.ITEM_ID_2);
    List<ItemEntity> itemEntityList = List.of(item1, item2);
    List<ItemDomainDto> itemDomainDtoList = testUtil.getItemDomainDtoList();
    Pageable pageable = PageRequest.of(1, 15, Sort.by(TestUtil.SORT_BY).ascending());
    Page<ItemEntity> itemEntityPage =
        new PageImpl<>(itemEntityList, pageable, itemEntityList.size());
    when(itemRepository.findByItemIdInAndOrgId(
            List.of(TestUtil.ITEM_ID_1, TestUtil.ITEM_ID_2), TestUtil.ORG_ID, pageable))
        .thenReturn(itemEntityPage);
    when(itemEntityMapper.toDomain(item1)).thenReturn(itemDomainDtoList.getFirst());
    when(itemEntityMapper.toDomain(item2)).thenReturn(itemDomainDtoList.getLast());
    Page<ItemDomainDto> itemDomainDtoPage =
        itemPersistenceService.getItemByItemIdListAndOrgId(
            List.of(TestUtil.ITEM_ID_1, TestUtil.ITEM_ID_2), TestUtil.ORG_ID, pageable);
    Assertions.assertEquals(2, itemDomainDtoPage.getContent().size());
    Assertions.assertEquals(
        TestUtil.ITEM_ID_1, itemDomainDtoPage.getContent().getFirst().getItemId());
    Assertions.assertEquals(
        TestUtil.ITEM_ID_2, itemDomainDtoPage.getContent().getLast().getItemId());
    verify(itemRepository, times(1)).findByItemIdInAndOrgId(any(), any(), any());
  }

  @Test
  @DisplayName("Exception: Exception occurred while fetching the records")
  void getItemListPaginatedException() {
    Exception expectedException = new RuntimeException();
    Pageable pageable = PageRequest.of(1, 15, Sort.by(TestUtil.SORT_BY).ascending());
    when(itemRepository.findByItemIdInAndOrgId(
            List.of(TestUtil.ITEM_ID_1, TestUtil.ITEM_ID_2), TestUtil.ORG_ID, pageable))
        .thenThrow(expectedException);
    ItemBatchingDomainException thrownException =
        assertThrows(
            ItemBatchingDomainException.class,
            () -> {
              itemPersistenceService.getItemByItemIdListAndOrgId(
                  List.of(TestUtil.ITEM_ID_1, TestUtil.ITEM_ID_2), TestUtil.ORG_ID, pageable);
            });
    Assertions.assertEquals(TestUtil.EXCEPTION_MESSAGE_PAGINATED, thrownException.getMessage());
    Assertions.assertEquals(
        List.of(TestUtil.ITEM_ID_1, TestUtil.ITEM_ID_2), thrownException.getItemList());
    verify(itemRepository, times(1)).findByItemIdInAndOrgId(any(), any(), any());
  }
}
