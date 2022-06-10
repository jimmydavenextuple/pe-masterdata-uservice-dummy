package com.nextuple.pe.masterdata.domain;

import com.nextuple.pe.masterdata.domain.entity.ItemEntity;
import com.nextuple.pe.masterdata.domain.repository.ItemRepository;
import com.nextuple.pe.masterdata.exception.ItemDomainException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemDomain {

  private static final Logger logger = LoggerFactory.getLogger(ItemDomain.class);
  private final ItemRepository itemRepository;

  public ItemEntity saveItemEntity(ItemEntity itemEntity) throws ItemDomainException {

    try {
      return itemRepository.save(itemEntity);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to save item");
      throw new ItemDomainException(
          "Error while saving the item",
          itemEntity.getItemId(),
          itemEntity.getOrgId(),
          itemEntity.getUom());
    }
  }

  public Optional<ItemEntity> findItemByItemIdAndOrgIdAndUom(
      String itemId, String orgId, String uom) throws ItemDomainException {

    try {
      return itemRepository.findByItemIdAndOrgIdAndUom(itemId, orgId, uom);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find item");
      throw new ItemDomainException("Error while finding item", itemId, orgId, uom);
    }
  }

  public void deleteItem(ItemEntity itemEntity) throws ItemDomainException {
    try {
      itemRepository.delete(itemEntity);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to delete item");
      throw new ItemDomainException(
          "Error while deleting item",
          itemEntity.getItemId(),
          itemEntity.getOrgId(),
          itemEntity.getUom());
    }
  }
}
