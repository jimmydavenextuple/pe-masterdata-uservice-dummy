package com.hbc.item.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.item.TestUtil;
import com.hbc.item.domain.entity.ItemEntity;
import com.hbc.item.domain.entity.ItemPK;
import com.hbc.item.exception.ItemDomainException;
import com.hbc.item.repository.ItemRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ItemDomainTest {

  @InjectMocks private ItemDomain itemDomain;
  @InjectMocks private TestUtil testUtil;

  @Mock private ItemRepository itemRepository;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void saveItemTest() throws ItemDomainException {
    ItemEntity itemEntity = testUtil.getItemEntity();
    when(itemRepository.save(any())).thenReturn(itemEntity);

    ItemEntity item = itemDomain.saveItemEntity(itemEntity);
    Assertions.assertEquals(itemEntity, item);

    verify(itemRepository, times(1)).save(any());
  }

  @Test
  void saveItemExceptionTest() {
    ItemEntity itemEntity = testUtil.getItemEntity();
    when(itemRepository.save(any())).thenThrow(new RuntimeException("Error while saving"));

    Exception exception =
        assertThrows(ItemDomainException.class, () -> itemDomain.saveItemEntity(itemEntity));
    Assertions.assertEquals("Error while saving the item", exception.getMessage());
    verify(itemRepository, times(1)).save(any());
  }

  @Test
  void getItemDetailsTest() throws ItemDomainException {
    ItemEntity itemEntity = testUtil.getItemEntity();
    ItemPK id = testUtil.getItemId();
    when(itemRepository.findByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenReturn(Optional.of(itemEntity));

    Optional<ItemEntity> optionalItemEntity =
        itemDomain.findItemByItemIdAndOrgIdAndUom(TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM);
    Assertions.assertEquals(itemEntity, optionalItemEntity.get());
    Assertions.assertEquals(
        itemEntity.getIsDSVEligible(), optionalItemEntity.get().getIsDSVEligible());
    Assertions.assertEquals(
        itemEntity.getDepartmentName(), optionalItemEntity.get().getDepartmentName());
    Assertions.assertEquals(
        itemEntity.getDepartmentNumber(), optionalItemEntity.get().getDepartmentNumber());
    Assertions.assertEquals(itemEntity.getImageUrl(), optionalItemEntity.get().getImageUrl());
    Assertions.assertEquals(
        itemEntity.getShortDescription(), optionalItemEntity.get().getShortDescription());

    verify(itemRepository, times(1)).findByItemIdAndOrgIdAndUom(any(), any(), any());
    Assertions.assertEquals(TestUtil.ITEM_ID, id.getItemId());
    Assertions.assertEquals(TestUtil.ORG_ID, id.getOrgId());
    Assertions.assertEquals(TestUtil.UOM, id.getUom());
  }

  @Test
  void getItemDetailsTestException() {
    when(itemRepository.findByItemIdAndOrgIdAndUom(any(), any(), any()))
        .thenThrow(new RuntimeException("Error while fetching details"));

    Exception exception =
        assertThrows(
            ItemDomainException.class,
            () ->
                itemDomain.findItemByItemIdAndOrgIdAndUom(
                    TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM));
    Assertions.assertEquals("Error while finding item", exception.getMessage());
    verify(itemRepository, times(1)).findByItemIdAndOrgIdAndUom(any(), any(), any());
  }

  @Test
  void itemDeletionTest() throws ItemDomainException {
    ItemEntity itemEntity = new ItemEntity();
    doNothing().when(itemRepository).delete(any());
    itemDomain.deleteItem(itemEntity);

    verify(itemRepository, times(1)).delete(any());
  }

  @Test
  void itemDeletionTestException() {
    doThrow(new RuntimeException("error while deleting")).when(itemRepository).delete(any());

    Exception exception =
        assertThrows(
            ItemDomainException.class, () -> itemDomain.deleteItem(testUtil.getItemEntity()));
    Assertions.assertEquals("Error while deleting item", exception.getMessage());
    verify(itemRepository, times(1)).delete(any());
  }
}
