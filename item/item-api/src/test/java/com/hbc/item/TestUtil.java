package com.hbc.item;

import com.hbc.item.domain.entity.ItemEntity;
import com.hbc.item.domain.entity.ItemPK;
import com.hbc.item.domain.events.ItemMasterEvent;
import com.hbc.item.domain.inbound.ItemCreationRequest;
import com.hbc.item.domain.inbound.ItemUpdationRequest;
import com.hbc.item.domain.outbound.ItemResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class TestUtil {

  public static final String ORG_ID = "org-1";
  public static String ITEM_ID = "item-1";
  public static String UOM = "uom-1";
  public static String PRODUCT = "product-1";
  public static String COLOR = "color-1";
  public static String SIZE = "size-1";
  public static Boolean SHIP_ALONE = Boolean.TRUE;
  public static Boolean SHIP_ELIGIBLE = Boolean.TRUE;
  public static Boolean PARCEL_SHIPMENT_ELIGIBLE = Boolean.TRUE;
  public static Boolean BOPIS_ELIGIBLE = Boolean.TRUE;
  public static Double HEIGHT = 3.4;
  public static Double WEIGHT = 3.4;
  public static Double LENGTH = 3.4;
  public static Double VOLUME = 3.4;
  public static Double PROCESSING_TIME = 0.0;
  public static Boolean IS_HAZMAT = Boolean.TRUE;
  public static String COST = "cost-1";

  public ItemEntity getItemEntity() {
    Map<String, Boolean> serviceOptionEligibilities = new HashMap<>();
    serviceOptionEligibilities.put("expressEligible", true);
    serviceOptionEligibilities.put("sdndEligible", true);

    return ItemEntity.builder()
        .itemId(ITEM_ID)
        .orgId(ORG_ID)
        .uom(UOM)
        .color(COLOR)
        .size(SIZE)
        .length(LENGTH)
        .shipAlone(SHIP_ALONE)
        .bopisEligible(BOPIS_ELIGIBLE)
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
        .build();
  }

  public ItemCreationRequest getItemCreationRequest() {
    Map<String, Boolean> serviceOptionEligibilities = new HashMap<>();
    serviceOptionEligibilities.put("expressEligible", true);
    serviceOptionEligibilities.put("sdndEligible", true);

    return ItemCreationRequest.builder()
        .itemId(ITEM_ID)
        .orgId(ORG_ID)
        .uom(UOM)
        .color(COLOR)
        .size(SIZE)
        .length(LENGTH)
        .shipAlone(SHIP_ALONE)
        .bopisEligible(BOPIS_ELIGIBLE)
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
        .build();
  }

  public ItemResponse getItemResponse() {
    Map<String, Boolean> serviceOptionEligibilities = new HashMap<>();
    serviceOptionEligibilities.put("expressEligible", true);
    serviceOptionEligibilities.put("sdndEligible", true);

    return ItemResponse.builder()
        .itemId(ITEM_ID)
        .orgId(ORG_ID)
        .uom(UOM)
        .color(COLOR)
        .size(SIZE)
        .length(LENGTH)
        .shipAlone(SHIP_ALONE)
        .bopisEligible(BOPIS_ELIGIBLE)
        .isHazmat(IS_HAZMAT)
        .cost(COST)
        .parcelShipmentEligible(PARCEL_SHIPMENT_ELIGIBLE)
        .serviceOptionEligibilities(serviceOptionEligibilities)
        .processingTime(PROCESSING_TIME)
        .product(PRODUCT)
        .shipEligible(SHIP_ELIGIBLE)
        .height(HEIGHT)
        .weight(WEIGHT)
        .volume(VOLUME)
        .build();
  }

  public ItemUpdationRequest getItemUpdationRequest() {
    return ItemUpdationRequest.builder()
        .bopisEligible(Boolean.FALSE)
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
        .bopisEligible(Boolean.FALSE)
        .isHazmat(IS_HAZMAT)
        .cost(COST)
        .parcelShipmentEligible(PARCEL_SHIPMENT_ELIGIBLE)
        .processingTime(PROCESSING_TIME)
        .product(PRODUCT)
        .shipEligible(SHIP_ELIGIBLE)
        .height(HEIGHT)
        .weight(35.5)
        .volume(VOLUME)
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
        .bopisEligible(Boolean.FALSE)
        .isHazmat(IS_HAZMAT)
        .cost(COST)
        .parcelShipmentEligible(PARCEL_SHIPMENT_ELIGIBLE)
        .processingTime(PROCESSING_TIME)
        .product(PRODUCT)
        .shipEligible(SHIP_ELIGIBLE)
        .height(HEIGHT)
        .weight(35.5)
        .volume(VOLUME)
        .build();
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
        .bopisEligible(BOPIS_ELIGIBLE)
        .isHazmat(IS_HAZMAT)
        .cost(COST)
        .parcelShipmentEligible(PARCEL_SHIPMENT_ELIGIBLE)
        .sdndEligible(Boolean.TRUE)
        .expressEligible(Boolean.FALSE)
        .processingTime(PROCESSING_TIME)
        .product(PRODUCT)
        .shipEligible(SHIP_ELIGIBLE)
        .height(HEIGHT)
        .weight(WEIGHT)
        .volume(VOLUME)
        .build();
  }

  public ItemRecord getItemRecord() {
    ItemRecord record = new ItemRecord();
    record.setItemId(TestUtil.ITEM_ID);
    record.setOrgId(TestUtil.ORG_ID);
    record.setUom(TestUtil.UOM);
    record.setLastModifiedDate(Instant.ofEpochSecond(1000L));
    return record;
  }

  public ItemPK getItemId() {
    ItemPK id = new ItemPK();
    id.setItemId(ITEM_ID);
    id.setOrgId(ORG_ID);
    id.setUom(UOM);
    return id;
  }
}
