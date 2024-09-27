/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.item.persistence;

import com.nextuple.item.persistence.domain.ItemBufferDomainDto;
import com.nextuple.item.persistence.domain.ItemDomainDto;
import com.nextuple.item.persistence.entity.ItemBufferEntity;
import com.nextuple.item.persistence.entity.ItemEntity;
import com.nextuple.item.persistence.entity.key.ItemKey;
import com.nextuple.item.persistence.mapper.ItemEntityMapper;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mapstruct.factory.Mappers;

public class TestUtil {

  public static final String ORG_ID = "org-1";
  public static String ITEM_ID = "item-1";
  public static final String ITEM_ID_1 = "item-01";
  public static final String ITEM_ID_2 = "item-02";
  public static String UOM = "uom-1";
  public static String PRODUCT = "product-1";
  public static String COLOR = "color-1";
  public static String SIZE = "size-1";
  public static Boolean SHIP_ALONE = Boolean.TRUE;
  public static Boolean SHIP_ELIGIBLE = Boolean.TRUE;
  public static final String SORT_BY = "itemId";
  public static final String EXCEPTION_MESSAGE_PAGINATED = "Error while finding item list";

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

  private static final ItemEntityMapper ITEM_ENTITY_MAPPER =
      Mappers.getMapper(ItemEntityMapper.class);

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

  public ItemDomainDto getItemDomainDto() {
    return ITEM_ENTITY_MAPPER.toDomain(getItemEntity());
  }

  public List<ItemDomainDto> getItemDomainDtoList() {
    ItemDomainDto itemDomainDto1 = getItemDomainDto();
    itemDomainDto1.setItemId("item-01");
    ItemDomainDto itemDomainDto2 = getItemDomainDto();
    itemDomainDto2.setItemId("item-02");
    return List.of(itemDomainDto1, itemDomainDto2);
  }

  public ItemKey getItemId() {
    ItemKey id = new ItemKey();
    id.setItemId(ITEM_ID);
    id.setOrgId(ORG_ID);
    id.setUom(UOM);
    return id;
  }

  public static Map<String, Boolean> getServiceOptionEligibitiesMapForTest() {
    Map<String, Boolean> serviceOptionEligibilities = new HashMap<>();
    serviceOptionEligibilities.put(EXPRESS_ELIGIBLE, true);
    serviceOptionEligibilities.put(SDND_ELIGIBLE, true);
    serviceOptionEligibilities.put(NEXT_DAY_ELIGIBLE, true);
    return serviceOptionEligibilities;
  }

  public static Map<String, List<String>> getInventoryNodeTypeMap() {
    Map<String, List<String>> inventoryNodeTypes = new HashMap<>();
    inventoryNodeTypes.put(EXPRESS_ELIGIBLE, List.of("FC", "MFC"));
    inventoryNodeTypes.put(SDND_ELIGIBLE, List.of("STORE"));
    inventoryNodeTypes.put(NEXT_DAY_ELIGIBLE, List.of("MFC"));
    return inventoryNodeTypes;
  }

  public ItemBufferEntity getItemBufferEntity() {
    ItemBufferEntity itemBufferEntity = new ItemBufferEntity();
    itemBufferEntity.setId(2L);
    itemBufferEntity.setItemId(ITEM_ID);
    itemBufferEntity.setUom(UOM);
    itemBufferEntity.setBufferHours(1.0);
    itemBufferEntity.setBufferStartDate(new Date(1000));
    itemBufferEntity.setBufferEndDate(new Date(2000));
    return itemBufferEntity;
  }

  public ItemBufferDomainDto getItemBufferDomainDto() {
    ItemBufferDomainDto itemBufferDomainDto = new ItemBufferDomainDto();
    itemBufferDomainDto.setId(2L);
    itemBufferDomainDto.setItemId(ITEM_ID);
    itemBufferDomainDto.setUom(UOM);
    itemBufferDomainDto.setBufferHours(1.0);
    itemBufferDomainDto.setBufferStartDate(new Date(1000));
    itemBufferDomainDto.setBufferEndDate(new Date(2000));
    return itemBufferDomainDto;
  }
}
