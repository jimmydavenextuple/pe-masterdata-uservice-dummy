package com.hbc.item;

import com.hbc.item.domain.entity.ItemEntity;
import com.hbc.item.domain.entity.ItemPK;
import com.hbc.item.domain.events.ItemMasterEvent;
import com.hbc.item.domain.inbound.ItemCreationRequest;
import com.hbc.item.domain.inbound.ItemUpdationRequest;
import com.hbc.item.domain.outbound.ItemResponse;
import com.hbc.item.util.ItemUtils;
import com.hbc.streams.promising.messages.PromisingRecord;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;

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
  private static final String SDND_ELIGIBLE = "sdndEligible";
  private static final String SDND_ELIGIBLE_FOR_DC = "sdndEligibleForDC";
  private static final String NEXT_DAY_ELIGIBLE = "nextdayEligible";
  private static final String NEXT_DAY_ELIGIBLE_FOR_DC = "nextdayEligibleForDC";
  private static final String EXPRESS_ELIGIBLE = "expressEligible";

  public ItemEntity getItemEntity() {
    Map<String, Boolean> serviceOptionEligibilities =
        TestUtil.getServiceOptionEligibitiesMapForTest();
    Map<String, List<String>> inventoryNodeTypes =
        ItemUtils.getInventoryNodeTypeMap(serviceOptionEligibilities);

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
        .inventoryNodeTypes(inventoryNodeTypes)
        .build();
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
    Map<String, Boolean> serviceOptionEligibilities =
        TestUtil.getServiceOptionEligibitiesMapForTest();
    Map<String, List<String>> inventoryNodeTypes =
        ItemUtils.getInventoryNodeTypeMap(serviceOptionEligibilities);

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
        .inventoryNodeTypes(inventoryNodeTypes)
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

  public PromisingRecord getItemRecord() {
    PromisingRecord record = new PromisingRecord();
    record.setItemId(TestUtil.ITEM_ID);
    record.setOrgId(TestUtil.ORG_ID);
    record.setUom(TestUtil.UOM);
    record.setLastModifiedDate(new DateTime());
    record.setColor("");
    record.setBopisEligible(Boolean.TRUE);
    record.setCost("");
    record.setDepartmentName("");
    record.setDepartmentNumber("");
    record.setIsDSVEligible(Boolean.TRUE);
    record.setExpressEligible(Boolean.TRUE);
    record.setIsWhiteGlove(Boolean.TRUE);
    record.setHeight(0.0);
    record.setParcelShipmentEligible(Boolean.TRUE);
    record.setBopisEligible(Boolean.TRUE);
    record.setShipAlone(Boolean.TRUE);
    record.setSdndEligible(Boolean.TRUE);
    record.setItemSource("");
    record.setShipEligible(Boolean.TRUE);
    record.setIsHazmat(Boolean.FALSE);
    record.setDimensionUom("");
    record.setLastModifiedDate(new DateTime());
    record.setWeight(0.0);
    record.setHeight(0.0);
    record.setLength(0.0);
    record.setWeightUOM("");
    record.setProduct("");
    record.setSize("");
    record.setVolumeUom("");
    record.setShortDescription("");
    record.setSdndEligibleForDC(Boolean.TRUE);
    record.setNextdayEligible(Boolean.TRUE);
    record.setNextdayEligibleForDC(Boolean.TRUE);
    return record;
  }

  public PromisingRecord getItemRecordItemEligibleStore() {
    PromisingRecord record = new PromisingRecord();
    record.setItemId(TestUtil.ITEM_ID);
    record.setOrgId(TestUtil.ORG_ID);
    record.setUom(TestUtil.UOM);
    record.setLastModifiedDate(new DateTime());
    record.setColor("");
    record.setBopisEligible(Boolean.TRUE);
    record.setCost("");
    record.setDepartmentName("");
    record.setDepartmentNumber("");
    record.setIsDSVEligible(Boolean.TRUE);
    record.setExpressEligible(Boolean.TRUE);
    record.setIsWhiteGlove(Boolean.TRUE);
    record.setHeight(0.0);
    record.setParcelShipmentEligible(Boolean.TRUE);
    record.setBopisEligible(Boolean.TRUE);
    record.setShipAlone(Boolean.TRUE);
    record.setItemSource("");
    record.setShipEligible(Boolean.TRUE);
    record.setIsHazmat(Boolean.FALSE);
    record.setDimensionUom("");
    record.setLastModifiedDate(new DateTime());
    record.setWeight(0.0);
    record.setHeight(0.0);
    record.setLength(0.0);
    record.setWeightUOM("");
    record.setProduct("");
    record.setSize("");
    record.setVolumeUom("");
    record.setShortDescription("");
    record.setSdndEligible(Boolean.TRUE);
    record.setSdndEligibleForDC(Boolean.FALSE);
    record.setNextdayEligible(Boolean.TRUE);
    record.setNextdayEligibleForDC(Boolean.FALSE);
    return record;
  }

  public PromisingRecord getItemRecordItemEligibleDC() {
    PromisingRecord record = new PromisingRecord();
    record.setItemId(TestUtil.ITEM_ID);
    record.setOrgId(TestUtil.ORG_ID);
    record.setUom(TestUtil.UOM);
    record.setLastModifiedDate(new DateTime());
    record.setColor("");
    record.setBopisEligible(Boolean.TRUE);
    record.setCost("");
    record.setDepartmentName("");
    record.setDepartmentNumber("");
    record.setIsDSVEligible(Boolean.TRUE);
    record.setExpressEligible(Boolean.TRUE);
    record.setIsWhiteGlove(Boolean.TRUE);
    record.setHeight(0.0);
    record.setParcelShipmentEligible(Boolean.TRUE);
    record.setBopisEligible(Boolean.TRUE);
    record.setShipAlone(Boolean.TRUE);
    record.setItemSource("");
    record.setShipEligible(Boolean.TRUE);
    record.setIsHazmat(Boolean.FALSE);
    record.setDimensionUom("");
    record.setLastModifiedDate(new DateTime());
    record.setWeight(0.0);
    record.setHeight(0.0);
    record.setLength(0.0);
    record.setWeightUOM("");
    record.setProduct("");
    record.setSize("");
    record.setVolumeUom("");
    record.setShortDescription("");
    record.setSdndEligible(Boolean.FALSE);
    record.setSdndEligibleForDC(Boolean.TRUE);
    record.setNextdayEligible(Boolean.FALSE);
    record.setNextdayEligibleForDC(Boolean.TRUE);
    return record;
  }

  public PromisingRecord getItemRecordItemEligibleNone() {
    PromisingRecord record = new PromisingRecord();
    record.setItemId(TestUtil.ITEM_ID);
    record.setOrgId(TestUtil.ORG_ID);
    record.setUom(TestUtil.UOM);
    record.setLastModifiedDate(new DateTime());
    record.setColor("");
    record.setBopisEligible(Boolean.TRUE);
    record.setCost("");
    record.setDepartmentName("");
    record.setDepartmentNumber("");
    record.setIsDSVEligible(Boolean.TRUE);
    record.setExpressEligible(Boolean.TRUE);
    record.setIsWhiteGlove(Boolean.TRUE);
    record.setHeight(0.0);
    record.setParcelShipmentEligible(Boolean.TRUE);
    record.setBopisEligible(Boolean.TRUE);
    record.setShipAlone(Boolean.TRUE);
    record.setItemSource("");
    record.setShipEligible(Boolean.TRUE);
    record.setIsHazmat(Boolean.FALSE);
    record.setDimensionUom("");
    record.setLastModifiedDate(new DateTime());
    record.setWeight(0.0);
    record.setHeight(0.0);
    record.setLength(0.0);
    record.setWeightUOM("");
    record.setProduct("");
    record.setSize("");
    record.setVolumeUom("");
    record.setShortDescription("");
    record.setSdndEligible(Boolean.FALSE);
    record.setSdndEligibleForDC(Boolean.FALSE);
    record.setNextdayEligible(Boolean.FALSE);
    record.setNextdayEligibleForDC(Boolean.FALSE);
    return record;
  }

  public ItemPK getItemId() {
    ItemPK id = new ItemPK();
    id.setItemId(ITEM_ID);
    id.setOrgId(ORG_ID);
    id.setUom(UOM);
    return id;
  }

  public static Map<String, Boolean> getServiceOptionEligibitiesMapForTest() {
    Map<String, Boolean> serviceOptionEligibilities = new HashMap<>();
    serviceOptionEligibilities.put(EXPRESS_ELIGIBLE, true);
    serviceOptionEligibilities.put(SDND_ELIGIBLE, true);
    serviceOptionEligibilities.put(SDND_ELIGIBLE_FOR_DC, true);
    serviceOptionEligibilities.put(NEXT_DAY_ELIGIBLE, true);
    serviceOptionEligibilities.put(NEXT_DAY_ELIGIBLE_FOR_DC, true);
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
}
