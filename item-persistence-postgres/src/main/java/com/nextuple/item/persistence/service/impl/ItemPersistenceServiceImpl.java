/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.item.persistence.service.impl;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.item.persistence.domain.ItemDomainDto;
import com.nextuple.item.persistence.domain.key.ItemDomainKey;
import com.nextuple.item.persistence.entity.ItemEntity;
import com.nextuple.item.persistence.entity.key.ItemKey;
import com.nextuple.item.persistence.exception.ItemBatchingDomainException;
import com.nextuple.item.persistence.exception.ItemDomainException;
import com.nextuple.item.persistence.mapper.ItemEntityMapper;
import com.nextuple.item.persistence.repository.ItemRepository;
import com.nextuple.item.persistence.service.ItemPersistenceService;
import com.nextuple.postgres.service.CommonPersistenceService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemPersistenceServiceImpl
    extends CommonPersistenceService<
        ItemDomainDto, ItemDomainKey, ItemEntity, ItemKey, ItemRepository, ItemEntityMapper>
    implements ItemPersistenceService {

  private static final Logger logger = LoggerFactory.getLogger(ItemPersistenceServiceImpl.class);

  @Override
  public ItemDomainDto saveItem(ItemDomainDto domainDto) throws ItemDomainException {

    try {
      return this.save(domainDto);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to save item");
      throw new ItemDomainException(
          "Error while saving the item",
          domainDto.getItemId(),
          domainDto.getOrgId(),
          domainDto.getUom());
    }
  }

  @Override
  public Optional<ItemDomainDto> findItemByItemIdAndOrgIdAndUom(
      String itemId, String orgId, String uom) throws ItemDomainException {

    try {
      return this.findByKey(ItemDomainKey.builder().orgId(orgId).itemId(itemId).uom(uom).build());
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find item");
      throw new ItemDomainException("Error while finding item", itemId, orgId, uom);
    }
  }

  @Override
  public void deleteItem(ItemDomainDto itemDomainDto) throws ItemDomainException {
    try {
      this.delete(itemDomainDto);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to delete item");
      throw new ItemDomainException(
          "Error while deleting item",
          itemDomainDto.getItemId(),
          itemDomainDto.getOrgId(),
          itemDomainDto.getUom());
    }
  }

  @Override
  public List<ItemDomainDto> findItemListByItemIdsAndOrgId(List<String> itemList, String orgId)
      throws ItemBatchingDomainException {

    try {
      List<ItemEntity> itemEntities = getRepository().findByOrgIdAndItemIdIn(orgId, itemList);
      return getMapper().toDomain(itemEntities);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find item list: {}", itemList);
      throw new ItemBatchingDomainException("Error while finding item list", itemList, orgId);
    }
  }

  @Override
  public Page<ItemDomainDto> getItemByItemIdListAndOrgId(
      List<String> itemList, String orgId, Pageable pageable) throws ItemBatchingDomainException {
    try {
      return getRepository()
          .findByItemIdInAndOrgId(itemList, orgId, pageable)
          .map(getMapper()::toDomain);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find item list");
      throw new ItemBatchingDomainException("Error while finding item list", itemList, orgId);
    }
  }
}
