/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item;

import com.nextuple.common.pojo.PageParams;
import com.nextuple.item.domain.events.ItemMasterEvent;
import com.nextuple.item.domain.inbound.ItemBufferRequest;
import com.nextuple.item.domain.inbound.ItemBufferUpdateRequest;
import com.nextuple.item.domain.inbound.ItemCreationRequest;
import com.nextuple.item.domain.inbound.ItemUpdationRequest;
import com.nextuple.item.domain.outbound.ItemBufferResponse;
import com.nextuple.item.domain.outbound.ItemDetail;
import com.nextuple.item.domain.outbound.ItemListResponse;
import com.nextuple.item.domain.outbound.ItemResponse;
import com.nextuple.item.domain.outbound.PageResponseForItemBuffer;
import com.nextuple.item.persistence.domain.ItemBufferDomainDto;
import com.nextuple.item.persistence.domain.ItemDomainDto;
import com.nextuple.item.persistence.entity.ItemEntity;
import com.nextuple.item.persistence.mapper.ItemEntityMapper;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class TestUtil {

  public static final String ORG_ID = "org-1";
  public static final String SORT_BY = "itemId";
  private static final String ITEMSOURCE = "ORG";
  private static final String SERVICE_OPTIONS = "SDND,EXPRESS";
  public static String ITEM_ID = "item-1";
  public static String UOM = "uom-1";
  public static String PRODUCT = "product-1";
  public static String COLOR = "color-1";
  public static String SIZE = "size-1";
  public static Boolean SHIP_ALONE = Boolean.TRUE;
  public static Boolean SHIP_ELIGIBLE = Boolean.TRUE;
  public static Double BUFFER_HOURS = 2.0;

  public static Boolean DSV_ELIGIBLE = Boolean.TRUE;
  public static Boolean PARCEL_SHIPMENT_ELIGIBLE = Boolean.TRUE;
  public static Boolean PICK_ELIGIBLE = Boolean.TRUE;
  public static Double HEIGHT = 3.4;
  public static Double WEIGHT = 3.4;
  public static Double LENGTH = 3.4;
  public static Double VOLUME = 3.4;
  public static Double PROCESSING_TIME = 0.0;
  public static Boolean IS_HAZMAT = Boolean.TRUE;
  public static String COST = "cost-1";
  private static final String SDND_ELIGIBLE = "sdndEligible";
  private static final String SDND_ELIGIBLE_FOR_DC = "sdndEligibleForDC";
  private static final String NEXT_DAY_ELIGIBLE = "nextdayEligible";
  private static final String NEXT_DAY_ELIGIBLE_FOR_DC = "nextdayEligibleForDC";
  private static final String EXPRESS_ELIGIBLE = "expressEligible";
  public static final String ITEM_HANDLING_TYPE = "Conveyable";
  public static final String ITEM_ID_1 = "item-01";
  public static final String ITEM_ID_2 = "item-02";
  public static final Long LEAD_TIME = 1L;
  public static final String SHORT_DESC = "BoldFit Rope";

  public ItemEntity getItemEntity() {
    Map<String, Boolean> serviceOptionEligibilities =
        TestUtil.getServiceOptionEligibitiesMapForTest();
    Map<String, List<String>> inventoryNodeTypes = TestUtil.getInventoryNodeTypeMap();

    return ItemEntity.builder()
        .itemId(ITEM_ID)
        .orgId(ORG_ID)
        .uom(UOM)
        .color(COLOR)
        .size(SIZE)
        .length(LENGTH)
        .shipAlone(SHIP_ALONE)
        .pickEligible(PICK_ELIGIBLE)
        .isDSVEligible(DSV_ELIGIBLE)
        .serviceOptionEligibilities(serviceOptionEligibilities)
        .isHazmat(IS_HAZMAT)
        .cost(COST)
        .parcelShipmentEligible(PARCEL_SHIPMENT_ELIGIBLE)
        .processingTime(PROCESSING_TIME)
        .product(PRODUCT)
        .shipEligible(SHIP_ELIGIBLE)
        .height(HEIGHT)
        .weight(WEIGHT)
        .volume(VOLUME)
        .isDSVEligible(Boolean.TRUE)
        .departmentName("dName")
        .departmentNumber("dNumber")
        .imageUrl("/image")
        .shortDescription("desc")
        .inventoryNodeTypes(inventoryNodeTypes)
        .handlingType(ITEM_HANDLING_TYPE)
        .build();
  }

  private static final ItemEntityMapper ITEM_ENTITY_MAPPER =
      Mappers.getMapper(ItemEntityMapper.class);

  public ItemDomainDto getItemDomainDto() {
    return ITEM_ENTITY_MAPPER.toDomain(getItemEntity());
  }

  public ItemCreationRequest getItemCreationRequest() {
    Map<String, Boolean> serviceOptionEligibilities =
        TestUtil.getServiceOptionEligibitiesMapForTest();

    return ItemCreationRequest.builder()
        .itemId(ITEM_ID)
        .orgId(ORG_ID)
        .uom(UOM)
        .color(COLOR)
        .size(SIZE)
        .length(LENGTH)
        .shipAlone(SHIP_ALONE)
        .pickEligible(PICK_ELIGIBLE)
        .serviceOptionEligibilities(serviceOptionEligibilities)
        .isHazmat(IS_HAZMAT)
        .cost(COST)
        .parcelShipmentEligible(PARCEL_SHIPMENT_ELIGIBLE)
        .processingTime(PROCESSING_TIME)
        .product(PRODUCT)
        .shipEligible(SHIP_ELIGIBLE)
        .height(HEIGHT)
        .weight(WEIGHT)
        .volume(VOLUME)
        .isWhiteGlove(Boolean.FALSE)
        .handlingType(ITEM_HANDLING_TYPE)
        .build();
  }

  public ItemResponse getItemResponse() {
    Map<String, Boolean> serviceOptionEligibilities =
        TestUtil.getServiceOptionEligibitiesMapForTest();
    Map<String, List<String>> inventoryNodeTypes = TestUtil.getInventoryNodeTypeMap();

    return ItemResponse.builder()
        .itemId(ITEM_ID)
        .orgId(ORG_ID)
        .uom(UOM)
        .color(COLOR)
        .size(SIZE)
        .length(LENGTH)
        .shipAlone(SHIP_ALONE)
        .pickEligible(PICK_ELIGIBLE)
        .isHazmat(IS_HAZMAT)
        .cost(COST)
        .parcelShipmentEligible(PARCEL_SHIPMENT_ELIGIBLE)
        .serviceOptionEligibilities(serviceOptionEligibilities)
        .processingTime(PROCESSING_TIME)
        .product(PRODUCT)
        .shipEligible(SHIP_ELIGIBLE)
        .isDSVEligible(DSV_ELIGIBLE)
        .height(HEIGHT)
        .weight(WEIGHT)
        .volume(VOLUME)
        .inventoryNodeTypes(inventoryNodeTypes)
        .handlingType(ITEM_HANDLING_TYPE)
        .build();
  }

  public ItemUpdationRequest getItemUpdationRequest() {
    return ItemUpdationRequest.builder()
        .pickEligible(Boolean.FALSE)
        .color("color-2")
        .shipAlone(Boolean.FALSE)
        .weight(35.5)
        .build();
  }

  public ItemResponse getUpdatedItemResponse() {
    return ItemResponse.builder()
        .itemId(ITEM_ID)
        .orgId(ORG_ID)
        .uom(UOM)
        .color("color-2")
        .size(SIZE)
        .length(LENGTH)
        .shipAlone(Boolean.FALSE)
        .pickEligible(Boolean.FALSE)
        .isHazmat(IS_HAZMAT)
        .cost(COST)
        .parcelShipmentEligible(PARCEL_SHIPMENT_ELIGIBLE)
        .processingTime(PROCESSING_TIME)
        .product(PRODUCT)
        .shipEligible(SHIP_ELIGIBLE)
        .height(HEIGHT)
        .weight(35.5)
        .volume(VOLUME)
        .handlingType(ITEM_HANDLING_TYPE)
        .build();
  }

  public ItemEntity getUpdatedItemEntity() {
    return ItemEntity.builder()
        .itemId(ITEM_ID)
        .orgId(ORG_ID)
        .uom(UOM)
        .color("color-2")
        .size(SIZE)
        .length(LENGTH)
        .shipAlone(Boolean.FALSE)
        .pickEligible(Boolean.FALSE)
        .isHazmat(IS_HAZMAT)
        .cost(COST)
        .parcelShipmentEligible(PARCEL_SHIPMENT_ELIGIBLE)
        .processingTime(PROCESSING_TIME)
        .product(PRODUCT)
        .shipEligible(SHIP_ELIGIBLE)
        .height(HEIGHT)
        .weight(35.5)
        .volume(VOLUME)
        .handlingType(ITEM_HANDLING_TYPE)
        .build();
  }

  public ItemDomainDto getUpdatedItemDomainDto() {
    return ITEM_ENTITY_MAPPER.toDomain(getUpdatedItemEntity());
  }

  public ItemMasterEvent getItemMasterEvent() {

    return ItemMasterEvent.builder()
        .itemId(ITEM_ID)
        .orgId(ORG_ID)
        .uom(UOM)
        .color(COLOR)
        .size(SIZE)
        .length(LENGTH)
        .shipAlone(SHIP_ALONE)
        .pickEligible(PICK_ELIGIBLE)
        .isHazmat(IS_HAZMAT)
        .cost(COST)
        .parcelShipmentEligible(PARCEL_SHIPMENT_ELIGIBLE)
        .serviceOptionEligibilities(getServiceOptionEligibitiesMapForTest())
        .inventoryNodeTypes(getInventoryNodeTypeMap())
        .processingTime(PROCESSING_TIME)
        .product(PRODUCT)
        .shipEligible(SHIP_ELIGIBLE)
        .height(HEIGHT)
        .weight(WEIGHT)
        .volume(VOLUME)
        .handlingType(ITEM_HANDLING_TYPE)
        .build();
  }

  public ItemCreationRequest getItemRecordJSON() {
    ItemCreationRequest itemCreationRequest = new ItemCreationRequest();
    itemCreationRequest.setItemId(TestUtil.ITEM_ID);
    itemCreationRequest.setOrgId(TestUtil.ORG_ID);
    itemCreationRequest.setUom(TestUtil.UOM);
    itemCreationRequest.setLastModifiedDate(new DateTime());
    itemCreationRequest.setColor("");
    itemCreationRequest.setPickEligible(Boolean.TRUE);
    itemCreationRequest.setCost("");
    itemCreationRequest.setDepartmentName("");
    itemCreationRequest.setDepartmentNumber("");
    itemCreationRequest.setIsDSVEligible(Boolean.TRUE);
    // itemCreationRequest.setExpressEligible(Boolean.TRUE);
    itemCreationRequest.setIsWhiteGlove(Boolean.TRUE);
    itemCreationRequest.setHeight(0.0);
    itemCreationRequest.setParcelShipmentEligible(Boolean.TRUE);
    itemCreationRequest.setShipAlone(Boolean.TRUE);
    // itemCreationRequest.setSdndEligible(Boolean.TRUE);
    itemCreationRequest.setItemSource("");
    itemCreationRequest.setShipEligible(Boolean.TRUE);
    itemCreationRequest.setIsHazmat(Boolean.FALSE);
    itemCreationRequest.setDimensionUom("");
    itemCreationRequest.setLastModifiedDate(new DateTime());
    itemCreationRequest.setWeight(0.0);
    itemCreationRequest.setHeight(0.0);
    itemCreationRequest.setLength(0.0);
    // itemCreationRequest.setWeightUOM("");
    itemCreationRequest.setProduct("");
    itemCreationRequest.setSize("");
    itemCreationRequest.setVolumeUom("");
    itemCreationRequest.setShortDescription("");
    Map<String, Boolean> itemFlagMapForInvNodeCompute = new HashMap<>();
    itemFlagMapForInvNodeCompute.put("sdndEligible", true);
    itemFlagMapForInvNodeCompute.put("sdndEligibleForDC", false);
    itemFlagMapForInvNodeCompute.put("nextdayEligible", true);
    itemFlagMapForInvNodeCompute.put("nextdayEligibleForDC", false);
    itemCreationRequest.setServiceOptionEligibilities(itemFlagMapForInvNodeCompute);
    itemCreationRequest.setHandlingType(ITEM_HANDLING_TYPE);
    // itemCreationRequest.setSdndEligible(Boolean.TRUE);
    // itemCreationRequest.(Boolean.TRUE);
    // itemCreationRequest.setNextdayEligibleForDC(Boolean.TRUE);
    return itemCreationRequest;
  }

  public static Map<String, Boolean> getServiceOptionEligibitiesMapForTest() {
    Map<String, Boolean> serviceOptionEligibilities = new HashMap<>();
    serviceOptionEligibilities.put(EXPRESS_ELIGIBLE, true);
    serviceOptionEligibilities.put(SDND_ELIGIBLE, true);
    serviceOptionEligibilities.put(NEXT_DAY_ELIGIBLE, true);
    return serviceOptionEligibilities;
  }

  public static Map<String, Boolean> getServiceOptEligiblityMapForExceptionTest() {
    Map<String, Boolean> serviceOptionEligibilities = new HashMap<>();
    serviceOptionEligibilities.put(EXPRESS_ELIGIBLE, false);
    serviceOptionEligibilities.put(SDND_ELIGIBLE, false);
    serviceOptionEligibilities.put(SDND_ELIGIBLE_FOR_DC, false);
    serviceOptionEligibilities.put(NEXT_DAY_ELIGIBLE, false);
    serviceOptionEligibilities.put(NEXT_DAY_ELIGIBLE_FOR_DC, false);
    return serviceOptionEligibilities;
  }

  public static Map<String, List<String>> getInventoryNodeTypeMap() {
    Map<String, List<String>> inventoryNodeTypes = new HashMap<>();
    inventoryNodeTypes.put(EXPRESS_ELIGIBLE, List.of("FC", "MFC"));
    inventoryNodeTypes.put(SDND_ELIGIBLE, List.of("STORE"));
    inventoryNodeTypes.put(NEXT_DAY_ELIGIBLE, List.of("MFC"));
    return inventoryNodeTypes;
  }

  public static ItemBufferRequest getItemBufferRequest() {
    return ItemBufferRequest.builder()
        .orgId(ORG_ID)
        .itemId(ITEM_ID)
        .uom(UOM)
        .bufferHours(2.0)
        .bufferStartDate(new Date())
        .bufferEndDate(new Date())
        .build();
  }

  public static ItemBufferResponse getItemBufferResponse() {
    return ItemBufferResponse.builder()
        .id(1L)
        .orgId(ORG_ID)
        .itemId(ITEM_ID)
        .uom(UOM)
        .bufferHours(2.0)
        .bufferStartDate(new Date())
        .bufferEndDate(new Date(5000))
        .build();
  }

  public static List<ItemBufferResponse> getListOfItemBufferResponse() {
    return List.of(getItemBufferResponse());
  }

  public ItemBufferDomainDto getItemBufferDomainDto() {
    Date bufferStartDate = new Date(1000);
    Date bufferEndDate = new Date(2000);
    return ItemBufferDomainDto.builder()
        .orgId(ORG_ID)
        .itemId(ITEM_ID)
        .id(1L)
        .uom(UOM)
        .bufferHours(2.0)
        .bufferStartDate(bufferStartDate)
        .bufferEndDate(bufferEndDate)
        .build();
  }

  public ItemBufferUpdateRequest getItemBufferUpdateRequest() {
    return ItemBufferUpdateRequest.builder()
        .bufferHours(1.0)
        .bufferStartDate(new Date(2000))
        .bufferEndDate(new Date(4000))
        .build();
  }

  public PageParams getPageParams(
      Optional<Integer> pageNo,
      Optional<Integer> pageSize,
      Optional<String> sortBy,
      Optional<String> sortOrder) {
    PageParams pageParams = new PageParams();
    pageParams.setPageNo(pageNo);
    pageParams.setPageSize(pageSize);
    pageParams.setSortBy(sortBy);
    pageParams.setSortOrder(sortOrder);
    return pageParams;
  }

  public Page<ItemListResponse> getItemListPage(
      int totalPages, List<ItemListResponse> itemListResponses, int totalElements) {
    return new Page<ItemListResponse>() {
      @Override
      public int getTotalPages() {
        return totalPages;
      }

      @Override
      public long getTotalElements() {
        return totalElements;
      }

      @Override
      public <U> Page<U> map(Function<? super ItemListResponse, ? extends U> converter) {
        return null;
      }

      @Override
      public int getNumber() {
        return 0;
      }

      @Override
      public int getSize() {
        return 0;
      }

      @Override
      public int getNumberOfElements() {
        return 0;
      }

      @Override
      public List<ItemListResponse> getContent() {
        return itemListResponses;
      }

      @Override
      public boolean hasContent() {
        return false;
      }

      @Override
      public Sort getSort() {
        return null;
      }

      @Override
      public boolean isFirst() {
        return false;
      }

      @Override
      public boolean isLast() {
        return false;
      }

      @Override
      public boolean hasNext() {
        return false;
      }

      @Override
      public boolean hasPrevious() {
        return false;
      }

      @Override
      public Pageable nextPageable() {
        return null;
      }

      @Override
      public Pageable previousPageable() {
        return null;
      }

      @NotNull
      @Override
      public Iterator<ItemListResponse> iterator() {
        return null;
      }
    };
  }

  public List<ItemListResponse> getItemList() {
    return List.of(getItemListResponse(ITEM_ID_1), getItemListResponse(ITEM_ID_2));
  }

  public ItemListResponse getItemListResponse(String itemId) {
    return ItemListResponse.builder()
        .itemId(itemId)
        .itemSource(ITEMSOURCE)
        .leadTime(LEAD_TIME)
        .handlingType(ITEM_HANDLING_TYPE)
        .processingTime(PROCESSING_TIME)
        .shortDescription(SHORT_DESC)
        .serviceOptions(SERVICE_OPTIONS)
        .uom(UOM)
        .build();
  }

  public List<ItemDomainDto> getItemDomainDtoList() {
    ItemDomainDto itemDomainDto1 = getItemDomainDto();
    itemDomainDto1.setItemId("item-01");
    ItemDomainDto itemDomainDto2 = getItemDomainDto();
    itemDomainDto2.setItemId("item-02");
    return List.of(itemDomainDto1, itemDomainDto2);
  }

  public PageResponseForItemBuffer getPageResponseForItemBuffer() {
    ItemDetail itemDetail = ItemDetail.builder().itemId(ITEM_ID_1).uom(UOM).orgId(ORG_ID).build();
    return PageResponseForItemBuffer.builder().data(List.of(itemDetail)).build();
  }
}
