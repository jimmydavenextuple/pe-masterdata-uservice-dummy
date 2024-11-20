/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.common.util.DateValidationUtil;
import com.nextuple.item.domain.inbound.ItemBufferRequest;
import com.nextuple.item.domain.inbound.ItemBufferUpdateRequest;
import com.nextuple.item.domain.mapper.ItemBufferMapper;
import com.nextuple.item.domain.outbound.ItemBufferResponse;
import com.nextuple.item.domain.outbound.ItemDetail;
import com.nextuple.item.domain.outbound.PageResponseForItemBuffer;
import com.nextuple.item.domain.outbound.PaginationAttributeForItemBuffer;
import com.nextuple.item.persistence.domain.ItemBufferDomainDto;
import com.nextuple.item.persistence.domain.ItemDomainDto;
import com.nextuple.item.persistence.exception.ItemDomainException;
import com.nextuple.item.persistence.service.ItemBufferPersistenceService;
import com.nextuple.item.persistence.service.ItemPersistenceService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemBufferService {

  private final ItemPersistenceService itemPersistenceService;
  private final ItemBufferPersistenceService itemBufferPersistenceService;
  private final DateValidationUtil dateValidationUtil;
  private static final ItemBufferMapper INSTANCE = Mappers.getMapper(ItemBufferMapper.class);

  private static final Long NO_EXISTING_ID = null;
  private static final String ITEM_EXCEPTION_MESSAGE = "Item not found for given details";
  private static final String ITEM_ID = "itemId";
  private static final String ORG_ID = "orgId";
  private static final String BUFFER_HOURS = "bufferHours";
  private static final String BUFFER_START_DATE = "bufferStartDate";
  private static final String BUFFER_END_DATE = "bufferEndDate";
  private static final String UOM = "uom";

  public ItemBufferResponse createItemBuffer(ItemBufferRequest itemBufferRequest)
      throws ItemDomainException, CommonServiceException {
    validateItemDetails(itemBufferRequest);
    validateBufferHours(itemBufferRequest.getBufferHours());
    dateValidationUtil.validateBufferStartAndEndDate(
        itemBufferRequest.getBufferStartDate(), itemBufferRequest.getBufferEndDate());
    Optional<ItemBufferDomainDto> existingDomainDto =
        itemBufferPersistenceService.findByItemIdAndOrgIdAndUomAndBufferStartDateAndBufferEndDate(
            itemBufferRequest.getItemId(),
            itemBufferRequest.getOrgId(),
            itemBufferRequest.getUom(),
            itemBufferRequest.getBufferStartDate(),
            itemBufferRequest.getBufferEndDate());
    if (existingDomainDto.isPresent()) {
      return updateItemBuffer(
          existingDomainDto.get().getOrgId(),
          existingDomainDto.get().getId(),
          ItemBufferUpdateRequest.builder()
              .bufferHours(itemBufferRequest.getBufferHours())
              .build());
    } else {
      checkOverlapsWithExistingBuffers(
          INSTANCE.toItemBufferDomainDto(itemBufferRequest), NO_EXISTING_ID);
      return INSTANCE.toItemBufferResponse(
          itemBufferPersistenceService.saveItemBuffer(
              INSTANCE.toItemBufferDomainDto(itemBufferRequest)));
    }
  }

  @Transactional
  public ItemBufferResponse deleteItemBuffer(ItemBufferRequest itemBufferRequest)
      throws CommonServiceException {
    Optional<ItemBufferDomainDto> existingItemBufferDomainDto =
        itemBufferPersistenceService.findItemBuffer(
            INSTANCE.toItemBufferDomainDto(itemBufferRequest));
    if (existingItemBufferDomainDto.isEmpty()) {
      throwEntityNotFoundException(itemBufferRequest);
    }
    itemBufferPersistenceService.deleteItemBuffer(existingItemBufferDomainDto.get());
    return INSTANCE.toItemBufferResponse(existingItemBufferDomainDto.get());
  }

  public ItemBufferResponse fetchItemBuffer(String orgId, Long id) throws CommonServiceException {
    Optional<ItemBufferDomainDto> existingItemBufferDomainDto =
        itemBufferPersistenceService.findItemBufferByOrgIdAndId(orgId, id);
    if (existingItemBufferDomainDto.isEmpty()) {
      throwEntityNotFoundException(orgId, id);
    }
    return INSTANCE.toItemBufferResponse(existingItemBufferDomainDto.get());
  }

  public ItemBufferResponse updateItemBuffer(
      String orgId, Long id, ItemBufferUpdateRequest updateRequest) throws CommonServiceException {
    validateBufferHours(updateRequest.getBufferHours());
    dateValidationUtil.validateBufferStartAndEndDate(
        updateRequest.getBufferStartDate(), updateRequest.getBufferEndDate());
    Optional<ItemBufferDomainDto> existingItemBufferDomainDto =
        itemBufferPersistenceService.findItemBufferByOrgIdAndId(orgId, id);
    if (existingItemBufferDomainDto.isEmpty()) {
      throwEntityNotFoundException(orgId, id);
    }
    if (Objects.nonNull(updateRequest.getBufferStartDate())
        && Objects.nonNull(updateRequest.getBufferEndDate())) {
      ItemBufferDomainDto updateEntity =
          ItemBufferDomainDto.builder()
              .orgId(orgId)
              .itemId(existingItemBufferDomainDto.get().getItemId())
              .uom(existingItemBufferDomainDto.get().getUom())
              .bufferStartDate(updateRequest.getBufferStartDate())
              .bufferEndDate(updateRequest.getBufferEndDate())
              .build();

      checkOverlapsWithExistingBuffers(updateEntity, id);
    }
    INSTANCE.updateItemBufferDomainDtoFromRequest(updateRequest, existingItemBufferDomainDto.get());
    return INSTANCE.toItemBufferResponse(
        itemBufferPersistenceService.save(existingItemBufferDomainDto.get()));
  }

  @Transactional
  public ItemBufferResponse deleteItemBufferByOrgIdAndId(String orgId, Long id)
      throws CommonServiceException {
    Optional<ItemBufferDomainDto> existingItemBuffer =
        itemBufferPersistenceService.findItemBufferByOrgIdAndId(orgId, id);
    if (existingItemBuffer.isEmpty()) {
      throwEntityNotFoundException(orgId, id);
    }
    itemBufferPersistenceService.deleteItemBuffer(existingItemBuffer.get());
    return INSTANCE.toItemBufferResponse(existingItemBuffer.get());
  }

  public List<ItemBufferResponse> getItemBuffersByItemIdAndOrgIdAndUom(
      String itemId, String orgId, String uom) throws CommonServiceException {
    return INSTANCE.toItemBufferResponse(
        itemBufferPersistenceService.findItemBuffersListByItemIdAndOrgIdAndUom(itemId, orgId, uom));
  }

  public PageResponseForItemBuffer getItemsListWithConfiguredBuffers(
      String orgId, String itemIds, PageParams pageParams) throws CommonServiceException {
    Pageable pageable =
        PageRequest.of(pageParams.getPageNo().orElse(1) - 1, pageParams.getPageSize().orElse(10));
    Page<ItemBufferDomainDto> itemBufferDomainDtos = null;
    if (ObjectUtils.isEmpty(itemIds)) {
      itemBufferDomainDtos = itemBufferPersistenceService.findByOrgId(orgId, pageable);
    } else {
      List<String> itemIdsList = Arrays.asList(itemIds.split(","));
      itemBufferDomainDtos =
          itemBufferPersistenceService.findItemBufferByItemIdsAndOrgId(
              orgId, itemIdsList, pageable);
    }
    List<ItemDetail> itemDetails = getItemDetails(itemBufferDomainDtos.getContent());
    return getPageResponseForItemBuffer(pageParams, itemDetails, itemBufferDomainDtos);
  }

  private PageResponseForItemBuffer getPageResponseForItemBuffer(
      PageParams pageParams,
      List<ItemDetail> itemDetails,
      Page<ItemBufferDomainDto> itemBufferDomainDtos) {
    PageResponseForItemBuffer pageResponseForItemBuffer = new PageResponseForItemBuffer();
    pageResponseForItemBuffer.setData(itemDetails);
    pageResponseForItemBuffer.setPagination(
        PaginationAttributeForItemBuffer.builder()
            .currentPage(pageParams.getPageNo().orElse(1))
            .totalPages(itemBufferDomainDtos.getTotalPages())
            .totalRecords(itemBufferDomainDtos.getTotalElements())
            .build());
    return pageResponseForItemBuffer;
  }

  private List<ItemDetail> getItemDetails(List<ItemBufferDomainDto> itemBufferDomainDtos) {
    List<ItemDetail> itemDetails = new ArrayList<>();
    for (ItemBufferDomainDto itemBufferDomainDto : itemBufferDomainDtos) {
      itemDetails.add(
          ItemDetail.builder()
              .orgId(itemBufferDomainDto.getOrgId())
              .itemId(itemBufferDomainDto.getItemId())
              .uom(itemBufferDomainDto.getUom())
              .build());
    }
    return itemDetails;
  }

  private void validateItemDetails(ItemBufferRequest itemBufferRequest)
      throws ItemDomainException, CommonServiceException {
    Optional<ItemDomainDto> existingItemDomainDto =
        itemPersistenceService.findItemByItemIdAndOrgIdAndUom(
            itemBufferRequest.getItemId(),
            itemBufferRequest.getOrgId(),
            itemBufferRequest.getUom());
    if (existingItemDomainDto.isEmpty()) {
      log.error(ITEM_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          ORG_ID, FieldError.builder().rejectedValue(itemBufferRequest.getOrgId()).build());
      errorMap.put(
          ITEM_ID, FieldError.builder().rejectedValue(itemBufferRequest.getItemId()).build());
      errorMap.put(UOM, FieldError.builder().rejectedValue(itemBufferRequest.getUom()).build());
      throw new CommonServiceException(
          ITEM_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x2000, errorMap);
    }
  }

  void validateBufferHours(Double bufferHours) throws CommonServiceException {
    if (bufferHours != null && bufferHours < 0) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(BUFFER_HOURS, FieldError.builder().rejectedValue(bufferHours).build());
      throw new CommonServiceException(
          "bufferHours cannot be negative", HttpStatus.BAD_REQUEST, 0x1780, null);
    }
  }

  private void checkOverlapsWithExistingBuffers(
      ItemBufferDomainDto itemBufferDomainDto, Long existingId) throws CommonServiceException {
    List<ItemBufferDomainDto> entities =
        itemBufferPersistenceService.findByItemIdAndOrgIdAndUom(
            itemBufferDomainDto.getItemId(),
            itemBufferDomainDto.getOrgId(),
            itemBufferDomainDto.getUom());
    boolean hasOverlap =
        entities.stream()
            .filter(entity -> Objects.isNull(existingId) || !entity.getId().equals(existingId))
            .anyMatch(
                entity ->
                    isBufferWindowOverlap(
                        entity,
                        itemBufferDomainDto.getBufferStartDate(),
                        itemBufferDomainDto.getBufferEndDate()));
    if (hasOverlap) {
      throwOverlappingBuffersException(
          itemBufferDomainDto.getBufferStartDate(), itemBufferDomainDto.getBufferEndDate());
    }
  }

  private boolean isBufferWindowOverlap(
      ItemBufferDomainDto existingBuffer, Date newBufferStartDate, Date newBufferEndDate) {

    DateTime newStart = new DateTime(newBufferStartDate, DateTimeZone.UTC);
    DateTime newEnd = new DateTime(newBufferEndDate, DateTimeZone.UTC);

    DateTime existingStart = new DateTime(existingBuffer.getBufferStartDate(), DateTimeZone.UTC);
    DateTime existingEnd = new DateTime(existingBuffer.getBufferEndDate(), DateTimeZone.UTC);

    boolean startOverlap = newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
    boolean endOverlap = newEnd.isAfter(existingStart) && newStart.isBefore(existingEnd);
    boolean containsNewBuffer = existingStart.isBefore(newStart) && existingEnd.isAfter(newEnd);
    boolean containedInNewBuffer = newStart.isBefore(existingStart) && newEnd.isAfter(existingEnd);

    boolean sameStartAsExistingEnd = newStart.isEqual(existingEnd);
    boolean sameEndAsExistingStart = newEnd.isEqual(existingStart);
    boolean sameWindow = newStart.isEqual(existingStart) && newEnd.isEqual(existingEnd);

    return startOverlap
        || endOverlap
        || containsNewBuffer
        || containedInNewBuffer
        || sameStartAsExistingEnd
        || sameEndAsExistingStart
        || sameWindow;
  }

  private static void throwOverlappingBuffersException(Date bufferStartDate, Date bufferEndDate)
      throws CommonServiceException {
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(BUFFER_START_DATE, FieldError.builder().rejectedValue(bufferStartDate).build());
    errorMap.put(BUFFER_END_DATE, FieldError.builder().rejectedValue(bufferEndDate).build());
    throw new CommonServiceException(
        "Item Buffer window already exists or overlaps",
        HttpStatus.PRECONDITION_FAILED,
        0x2001,
        errorMap);
  }

  private static void throwEntityNotFoundException(ItemBufferRequest itemBufferRequest)
      throws CommonServiceException {
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(ORG_ID, FieldError.builder().rejectedValue(itemBufferRequest.getOrgId()).build());
    errorMap.put(
        ITEM_ID, FieldError.builder().rejectedValue(itemBufferRequest.getItemId()).build());
    errorMap.put(UOM, FieldError.builder().rejectedValue(itemBufferRequest.getUom()).build());
    errorMap.put(
        BUFFER_HOURS,
        FieldError.builder().rejectedValue(itemBufferRequest.getBufferHours()).build());
    errorMap.put(
        BUFFER_START_DATE,
        FieldError.builder().rejectedValue(itemBufferRequest.getBufferStartDate()).build());
    errorMap.put(
        BUFFER_END_DATE,
        FieldError.builder().rejectedValue(itemBufferRequest.getBufferEndDate()).build());
    throw new CommonServiceException(
        "Item buffer not found for given details", HttpStatus.NOT_FOUND, 0x2002, errorMap);
  }

  private static void throwEntityNotFoundException(String orgId, Long id)
      throws CommonServiceException {
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
    errorMap.put("id", FieldError.builder().rejectedValue(id).build());
    throw new CommonServiceException(
        "Item buffer not found for given orgId and Id", HttpStatus.NOT_FOUND, 0x1781, errorMap);
  }
}
