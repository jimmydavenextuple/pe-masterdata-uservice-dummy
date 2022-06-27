package com.hbc.item.service;

import com.hbc.common.response.error.FieldError;
import com.hbc.item.domain.ItemDomain;
import com.hbc.item.domain.entity.ItemEntity;
import com.hbc.item.domain.inbound.ItemCreationRequest;
import com.hbc.item.domain.inbound.ItemUpdationRequest;
import com.hbc.item.domain.mapper.ItemMapper;
import com.hbc.item.domain.outbound.ItemResponse;
import com.hbc.item.exception.CommonServiceException;
import com.hbc.item.exception.ItemDomainException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ItemService {

  private static final Logger logger = LoggerFactory.getLogger(ItemService.class);
  private static final String ITEM_ID = "itemId";
  private static final String ORG_ID = "orgId";
  private static final String UOM = "uom";

  private final ItemDomain itemDomain;

  public static final ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

  private static final String ITEM_EXCEPTION_MESSAGE = "Item not found with given details";

  public ItemResponse createItem(ItemCreationRequest itemCreationRequest)
      throws ItemDomainException {

    ItemEntity itemEntity = INSTANCE.toItemEntity(itemCreationRequest);

    return INSTANCE.toItemResponse(itemDomain.saveItemEntity(itemEntity));
  }

  public ItemResponse updateItemDetails(
      String itemId, String orgId, String uom, ItemUpdationRequest itemUpdationRequest)
      throws ItemDomainException, CommonServiceException {

    Optional<ItemEntity> existingItemEntity =
        itemDomain.findItemByItemIdAndOrgIdAndUom(itemId, orgId, uom);

    if (existingItemEntity.isEmpty()) {
      logger.info(ITEM_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(ITEM_ID, FieldError.builder().rejectedValue(itemId).build());
      errorMap.put(UOM, FieldError.builder().rejectedValue(uom).build());
      throw new CommonServiceException(
          ITEM_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }

    INSTANCE.updateItemEntity(itemUpdationRequest, existingItemEntity.get());
    return INSTANCE.toItemResponse(itemDomain.saveItemEntity(existingItemEntity.get()));
  }

  public ItemResponse getItemDetails(String itemId, String orgId, String uom)
      throws ItemDomainException, CommonServiceException {

    Optional<ItemEntity> existingItemEntity =
        itemDomain.findItemByItemIdAndOrgIdAndUom(itemId, orgId, uom);

    if (existingItemEntity.isEmpty()) {
      logger.info(ITEM_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(ITEM_ID, FieldError.builder().rejectedValue(itemId).build());
      errorMap.put(UOM, FieldError.builder().rejectedValue(uom).build());
      throw new CommonServiceException(
          ITEM_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }

    return INSTANCE.toItemResponse(existingItemEntity.get());
  }

  public ItemResponse deleteItem(String itemId, String orgId, String uom)
      throws ItemDomainException, CommonServiceException {

    Optional<ItemEntity> itemEntity = itemDomain.findItemByItemIdAndOrgIdAndUom(itemId, orgId, uom);

    if (itemEntity.isEmpty()) {
      logger.info(ITEM_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(ITEM_ID, FieldError.builder().rejectedValue(itemId).build());
      errorMap.put(UOM, FieldError.builder().rejectedValue(uom).build());
      throw new CommonServiceException(
          ITEM_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }

    ItemResponse itemResponse = INSTANCE.toItemResponse(itemEntity.get());
    itemDomain.deleteItem(itemEntity.get());
    return itemResponse;
  }
}
