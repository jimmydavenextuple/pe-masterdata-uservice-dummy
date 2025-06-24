/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.service;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;
import static com.nextuple.common.constants.CommonConstants.DESC_SORT_ORDER;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.common.validation.ValidationGroups;
import com.nextuple.common.validation.ValidatorUtil;
import com.nextuple.item.domain.inbound.ItemBaseRequest;
import com.nextuple.item.domain.inbound.ItemCreationRequest;
import com.nextuple.item.domain.mapper.ItemMapper;
import com.nextuple.item.domain.outbound.ActiveItemBufferResponse;
import com.nextuple.item.domain.outbound.ItemListResponse;
import com.nextuple.item.domain.outbound.ItemResponse;
import com.nextuple.item.persistence.domain.ItemBufferDomainDto;
import com.nextuple.item.persistence.domain.ItemDomainDto;
import com.nextuple.item.persistence.domain.ItemSubstitutionDomainDto;
import com.nextuple.item.persistence.exception.ItemBatchingDomainException;
import com.nextuple.item.persistence.exception.ItemDomainException;
import com.nextuple.item.persistence.service.ItemBufferPersistenceService;
import com.nextuple.item.persistence.service.ItemPersistenceService;
import com.nextuple.item.persistence.service.ItemSubstitutionPersistenceService;
import com.nextuple.postgres.config.ReaderDS;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@RequiredArgsConstructor
@Service
public class ItemService {

  private static final Logger logger = LoggerFactory.getLogger(ItemService.class);
  private static final String ITEM_ID = "itemId";
  private static final String ITEM_LIST = "itemList";
  private static final String ORG_ID = "orgId";
  private static final String UOM = "uom";
  private static final String SORT_ORDER = "sortOrder";

  private final ItemPersistenceService itemPersistenceService;
  private final ItemBufferPersistenceService itemBufferPersistenceService;
  private final ItemSubstitutionPersistenceService itemSubstitutionPersistenceService;

  @Autowired Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  public static final ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

  private static final String ITEM_EXCEPTION_MESSAGE = "Item not found with given details";

  private static final String ITEM_LIST_EXCEPTION_MESSAGE = "Items not found with given details";

  private static final String KEY_SEPARATOR = "-";

  private final ValidatorUtil validatorUtil;
  private static final ExecutorService taskExecutor = Executors.newVirtualThreadPerTaskExecutor();

  public ItemResponse upsertItem(ItemCreationRequest request)
      throws ItemDomainException, CommonServiceException {

    Optional<ItemDomainDto> itemDomainDto =
        itemPersistenceService.findItemByItemIdAndOrgIdAndUom(
            request.getItemId(), request.getOrgId(), request.getUom());

    if (itemDomainDto.isPresent()) {
      ItemResponse itemResponse =
          updateItemDetails(
              itemDomainDto.get().getItemId(),
              itemDomainDto.get().getOrgId(),
              itemDomainDto.get().getUom(),
              request);
      logger.info("Item updated successfully: {}", itemResponse);
      return itemResponse;
    } else {
      ItemResponse itemResponse = createItem(request);
      logger.info("New item created successfully: {}", itemResponse);
      return itemResponse;
    }
  }

  public ItemResponse createItem(ItemCreationRequest itemCreationRequest)
      throws ItemDomainException {
    validatorUtil.validateWithGroup(itemCreationRequest, ValidationGroups.Create.class);

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

    var itemDomainDto = INSTANCE.toItemEntity(itemCreationRequest);
    if (!ObjectUtils.isEmpty(itemCreationRequest.getLastModifiedDate())) {
      itemDomainDto.setLastModifiedDate(itemCreationRequest.getLastModifiedDate().toDate());
    }
    return INSTANCE.toItemResponse(itemPersistenceService.saveItem(itemDomainDto));
  }

  public ItemResponse updateItemDetails(
      String itemId, String orgId, String uom, ItemBaseRequest itemBaseRequest)
      throws ItemDomainException, CommonServiceException {
    validatorUtil.validateWithGroup(itemBaseRequest, ValidationGroups.Update.class);

    Optional<ItemDomainDto> existingItemDomainDto =
        itemPersistenceService.findItemByItemIdAndOrgIdAndUom(itemId, orgId, uom);

    if (existingItemDomainDto.isEmpty()) {
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
        INSTANCE.toItemResponse(existingItemDomainDto.get()));
    INSTANCE.updateItemEntity(itemBaseRequest, existingItemDomainDto.get());
    if (Objects.nonNull(itemBaseRequest.getProcessingTime())) {
      Optional<Double> optionalProcessingTime = itemBaseRequest.getProcessingTime();
      if (optionalProcessingTime.isPresent()) {
        existingItemDomainDto.get().setProcessingTime(optionalProcessingTime.get());
      } else {
        existingItemDomainDto.get().setProcessingTime(null);
      }
    }
    return INSTANCE.toItemResponse(itemPersistenceService.saveItem(existingItemDomainDto.get()));
  }

  @ReaderDS
  public ItemResponse getItemDetails(String itemId, String orgId, String uom)
      throws ItemDomainException, CommonServiceException {

    Optional<ItemDomainDto> existingItemDomainDto =
        itemPersistenceService.findItemByItemIdAndOrgIdAndUom(itemId, orgId, uom);

    if (existingItemDomainDto.isEmpty()) {
      logger.error(ITEM_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(ITEM_ID, FieldError.builder().rejectedValue(itemId).build());
      errorMap.put(UOM, FieldError.builder().rejectedValue(uom).build());
      throw new CommonServiceException(
          ITEM_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }

    return INSTANCE.toItemResponse(existingItemDomainDto.get());
  }

  public ItemResponse deleteItem(String itemId, String orgId, String uom)
      throws ItemDomainException, CommonServiceException {

    Optional<ItemDomainDto> itemDomainDto =
        itemPersistenceService.findItemByItemIdAndOrgIdAndUom(itemId, orgId, uom);

    if (itemDomainDto.isEmpty()) {
      logger.error(ITEM_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(ITEM_ID, FieldError.builder().rejectedValue(itemId).build());
      errorMap.put(UOM, FieldError.builder().rejectedValue(uom).build());
      throw new CommonServiceException(
          ITEM_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    logger.info(
        "Response before deleting of item data :{}", INSTANCE.toItemResponse(itemDomainDto.get()));
    var itemResponse = INSTANCE.toItemResponse(itemDomainDto.get());
    itemPersistenceService.deleteItem(itemDomainDto.get());
    return itemResponse;
  }

  public List<ItemResponse> getItemList(
      List<String> itemList,
      String orgId,
      Boolean isItemBufferEnabled,
      Date promisingEngineDate,
      Map<String, Boolean> uomConversionEnabled)
      throws CommonServiceException, ItemBatchingDomainException {

    List<ItemDomainDto> existingItemDomainDto =
        itemPersistenceService.findItemListByItemIdsAndOrgId(itemList, orgId);

    if (existingItemDomainDto.isEmpty()) {
      logger.error(ITEM_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(ITEM_LIST, FieldError.builder().rejectedValue(itemList).build());
      throw new CommonServiceException(
          ITEM_LIST_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1772, errorMap);
    }

    List<ItemResponse> itemResponse = INSTANCE.toItemResponseList(existingItemDomainDto);

    ExecutorService executor = Executors.newFixedThreadPool(2);

    try {
      CompletableFuture<Void> activeItemBufferFuture =
          CompletableFuture.runAsync(
              () -> {
                try {
                  addActiveItemBuffer(
                      isItemBufferEnabled, promisingEngineDate, itemResponse, itemList, orgId);
                } catch (CommonServiceException e) {
                  throw new CompletionException(e);
                }
              },
              executor);

      CompletableFuture<Void> itemUOMSubstitutionFuture =
          CompletableFuture.runAsync(
              () -> {
                addItemUOMSubstitution(uomConversionEnabled, itemResponse, orgId);
              },
              executor);

      CompletableFuture.allOf(activeItemBufferFuture, itemUOMSubstitutionFuture).join();

    } finally {
      executor.shutdown();
    }

    return itemResponse;
  }

  private void addItemUOMSubstitution(
      Map<String, Boolean> uomConversionEnabled, List<ItemResponse> itemResponse, String orgId) {

    if (CollectionUtils.isEmpty(uomConversionEnabled)) return;
    Map<String, ItemResponse> itemMap = new HashMap<>();
    for (ItemResponse itemResponse1 : itemResponse) {
      itemMap.put(itemResponse1.getItemId(), itemResponse1);
    }

    List<Pair<String, String>> itemIdUomPairs =
        itemResponse.stream()
            .filter(
                itemResponseEntry ->
                    uomConversionEnabled.getOrDefault(itemResponseEntry.getItemId(), false))
            .map(item -> Pair.of(item.getItemId(), item.getUom()))
            .toList();

    List<ItemSubstitutionDomainDto> itemSubstitutionDetails =
        itemSubstitutionPersistenceService.findByOrgIdAndPrimaryItemIdAndPrimaryUomList(
            orgId, itemIdUomPairs);

    List<List<ItemSubstitutionDomainDto>> itemSubstitutionInfoList =
        groupByPrimaryItemIdAndUom(itemSubstitutionDetails);

    for (List<ItemSubstitutionDomainDto> itemSubstitutionInfos : itemSubstitutionInfoList) {
      if (!CollectionUtils.isEmpty(itemSubstitutionInfos)) {
        String itemId = itemSubstitutionInfos.getFirst().getPrimaryItemId();
        ItemResponse itemValue = itemMap.get(itemId);
        itemValue.setItemSubstitutionResponse(INSTANCE.toItemSubstituteList(itemSubstitutionInfos));
      }
    }
  }

  private List<List<ItemSubstitutionDomainDto>> groupByPrimaryItemIdAndUom(
      List<ItemSubstitutionDomainDto> itemSubstitutionDetails) {
    return itemSubstitutionDetails.stream()
        .collect(
            Collectors.groupingBy(item -> Pair.of(item.getPrimaryItemId(), item.getPrimaryUom())))
        .values()
        .stream()
        .map(group -> group.stream().toList())
        .toList();
  }

  private void addActiveItemBuffer(
      boolean isItemBufferEnabled,
      Date promisingEngineDate,
      List<ItemResponse> existingItemResponse,
      List<String> itemList,
      String orgId)
      throws CommonServiceException {

    if (!isItemBufferEnabled) {
      for (ItemResponse itemResponse : existingItemResponse) {
        itemResponse.setActiveItemBuffer(null);
      }
      return;
    }

    if (Objects.isNull(promisingEngineDate)) {
      promisingEngineDate = Date.from(Instant.now());
    }

    List<ItemBufferDomainDto> activeItemsBuffersDomainDto =
        itemBufferPersistenceService.findItemBuffersListByItemIdsAndOrgIdAndPromisingEngineDate(
            itemList, orgId, promisingEngineDate);

    Map<String, ItemBufferDomainDto> activeItemsBuffersDomainDtoMap = new HashMap<>();
    for (ItemBufferDomainDto dto : activeItemsBuffersDomainDto) {
      String key = dto.getItemId() + KEY_SEPARATOR + dto.getUom();
      activeItemsBuffersDomainDtoMap.put(key, dto);
    }

    for (ItemResponse itemResponse : existingItemResponse) {
      ActiveItemBufferResponse activeItemBufferResponse = new ActiveItemBufferResponse();
      mapItemBufferInfoToItemDomainDto(
          itemResponse, activeItemsBuffersDomainDtoMap, activeItemBufferResponse);
      itemResponse.setActiveItemBuffer(activeItemBufferResponse);
    }
  }

  private static void mapItemBufferInfoToItemDomainDto(
      ItemResponse itemResponse,
      Map<String, ItemBufferDomainDto> activeItemsBuffersDomainDtoMap,
      ActiveItemBufferResponse activeItemBufferResponse) {

    String key = itemResponse.getItemId() + KEY_SEPARATOR + itemResponse.getUom();
    ItemBufferDomainDto itemBuffer = activeItemsBuffersDomainDtoMap.get(key);

    if (Objects.nonNull(itemBuffer)) {
      activeItemBufferResponse.setBufferHours(itemBuffer.getBufferHours());
      activeItemBufferResponse.setBufferStartDate(itemBuffer.getBufferStartDate());
      activeItemBufferResponse.setBufferEndDate(itemBuffer.getBufferEndDate());
    }
  }

  public Page<ItemListResponse> getItemListByItemIdAndOrgId(
      String itemIds,
      String orgId,
      Integer pageNo,
      Integer pageSize,
      String sortBy,
      String sortOrder)
      throws CommonServiceException, ItemBatchingDomainException {
    List<String> itemList = Arrays.asList(itemIds.split(","));
    if (sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)
        || sortOrder.equalsIgnoreCase(DESC_SORT_ORDER)) {
      Pageable pageable;
      if (sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)) {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).ascending());
      } else {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).descending());
      }
      Page<ItemDomainDto> response =
          itemPersistenceService.getItemByItemIdListAndOrgId(itemList, orgId, pageable);
      return response.map(
          itemDomainDto -> {
            ItemListResponse itemResponse = INSTANCE.toItemListResponse(itemDomainDto);
            String serviceOptions =
                itemDomainDto.getServiceOptionEligibilities().entrySet().stream()
                    .filter(entry -> entry.getValue().equals(true))
                    .map(entry -> entry.getKey().replace("Eligible", "").toUpperCase())
                    .collect(Collectors.joining(","));
            itemResponse.setServiceOptions(serviceOptions);
            return itemResponse;
          });
    } else {
      logger.error("Invalid sort order");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(SORT_ORDER, FieldError.builder().rejectedValue(sortOrder).build());
      throw new CommonServiceException(
          "Invalid sort order, consider giving either ASC or DESC",
          HttpStatus.BAD_REQUEST,
          0x1773,
          errorMap);
    }
  }
}
