package com.nextuple.item.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.item.domain.ItemDomain;
import com.nextuple.item.domain.entity.ItemEntity;
import com.nextuple.item.domain.inbound.ItemCreationRequest;
import com.nextuple.item.domain.inbound.ItemUpdationRequest;
import com.nextuple.item.domain.mapper.ItemMapper;
import com.nextuple.item.domain.outbound.ItemResponse;
import com.nextuple.item.exception.ItemBatchingDomainException;
import com.nextuple.item.exception.ItemDomainException;
import com.nextuple.postgres.config.ReaderDS;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@RequiredArgsConstructor
@Service
public class ItemService {

  private static final Logger logger = LoggerFactory.getLogger(ItemService.class);
  private static final String ITEM_ID = "itemId";
  private static final String ITEM_LIST = "itemList";
  private static final String ORG_ID = "orgId";
  private static final String UOM = "uom";

  private final ItemDomain itemDomain;
  @Autowired Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  public static final ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

  private static final String ITEM_EXCEPTION_MESSAGE = "Item not found with given details";

  private static final String ITEM_LIST_EXCEPTION_MESSAGE = "Items not found with given details";

  public ItemResponse createItem(ItemCreationRequest itemCreationRequest)
      throws ItemDomainException {

    Set<ConstraintViolation<ItemCreationRequest>> violations =
        validator.validate(itemCreationRequest);

    if (!violations.isEmpty()) {
      var sb = new StringBuilder();
      for (ConstraintViolation<ItemCreationRequest> constraintViolation : violations) {
        sb.append(constraintViolation.getPropertyPath())
            .append(" - ")
            .append(constraintViolation.getMessage());
      }
      throw new ConstraintViolationException("Error occurred: " + sb, violations);
    }

    var itemEntity = INSTANCE.toItemEntity(itemCreationRequest);
    if (!ObjectUtils.isEmpty(itemCreationRequest.getLastModifiedDate())) {
      itemEntity.setLastModifiedDate(itemCreationRequest.getLastModifiedDate().toDate());
    }
    return INSTANCE.toItemResponse(itemDomain.saveItemEntity(itemEntity));
  }

  public ItemResponse updateItemDetails(
      String itemId, String orgId, String uom, ItemUpdationRequest itemUpdationRequest)
      throws ItemDomainException, CommonServiceException {

    Optional<ItemEntity> existingItemEntity =
        itemDomain.findItemByItemIdAndOrgIdAndUom(itemId, orgId, uom);

    if (existingItemEntity.isEmpty()) {
      logger.error(ITEM_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(ITEM_ID, FieldError.builder().rejectedValue(itemId).build());
      errorMap.put(UOM, FieldError.builder().rejectedValue(uom).build());
      throw new CommonServiceException(
          ITEM_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    logger.info(
        "Response before updating of item data :{}",
        INSTANCE.toItemResponse(existingItemEntity.get()));
    INSTANCE.updateItemEntity(itemUpdationRequest, existingItemEntity.get());
    return INSTANCE.toItemResponse(itemDomain.saveItemEntity(existingItemEntity.get()));
  }

  @ReaderDS
  public ItemResponse getItemDetails(String itemId, String orgId, String uom)
      throws ItemDomainException, CommonServiceException {

    Optional<ItemEntity> existingItemEntity =
        itemDomain.findItemByItemIdAndOrgIdAndUom(itemId, orgId, uom);

    if (existingItemEntity.isEmpty()) {
      logger.error(ITEM_EXCEPTION_MESSAGE);
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
      logger.error(ITEM_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(ITEM_ID, FieldError.builder().rejectedValue(itemId).build());
      errorMap.put(UOM, FieldError.builder().rejectedValue(uom).build());
      throw new CommonServiceException(
          ITEM_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    logger.info(
        "Response before deleting of item data :{}", INSTANCE.toItemResponse(itemEntity.get()));
    var itemResponse = INSTANCE.toItemResponse(itemEntity.get());
    itemDomain.deleteItem(itemEntity.get());
    return itemResponse;
  }

  public List<ItemResponse> getItemList(List<String> itemList, String orgId)
      throws CommonServiceException, ItemBatchingDomainException {

    List<ItemEntity> existingItemEntity =
        itemDomain.findItemListByItemIdsAndOrgIdAndUom(itemList, orgId);

    if (existingItemEntity.isEmpty()) {
      logger.error(ITEM_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(ITEM_LIST, FieldError.builder().rejectedValue(itemList).build());
      throw new CommonServiceException(
          ITEM_LIST_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1772, errorMap);
    }

    return INSTANCE.toItemResponseList(existingItemEntity);
  }
}
