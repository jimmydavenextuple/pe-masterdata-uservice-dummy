package com.nextuple.pe.masterdata.service;

import com.nextuple.pe.masterdata.domain.entity.*;
import com.nextuple.pe.masterdata.domain.inbound.*;
import com.nextuple.pe.masterdata.domain.outbound.*;
import java.util.HashMap;
import java.util.Map;

public class TestUtil {

  public static final String NODE_ID = "node-1";
  public static final String ORG_ID = "org-1";
  public static final String STREET = "street-1";
  public static final String CITY = "city-1";
  public static final String PROVINCE = "province-1";
  public static final String POSTAL_CODE = "33666";
  public static final String COUNTRY = "country-1";
  public static final String LATITUDE = "43.769912";
  public static final String LONGITUDE = "-79.296678";
  public static final String TIME_ZONE = "America/Toronto";
  public static Boolean SHIP_TO_TIME = Boolean.TRUE;
  public static Boolean BOPIS_ELIGIBLE = Boolean.TRUE;
  public static Boolean SDND_ELIGIBLE = Boolean.TRUE;
  public static Boolean EXPRESS_ELIGIBLE = Boolean.TRUE;
  public static String NODE_TYPE = "MFC";
  public static Boolean IS_ACTIVE = Boolean.TRUE;
  public static String ITEM_ID = "item-1";
  public static String UOM = "uom-1";
  public static String PRODUCT = "product-1";
  public static String COLOR = "color-1";
  public static String SIZE = "size-1";
  public static Boolean SHIP_ALONE = Boolean.TRUE;
  public static Boolean SHIP_ELIGIBLE = Boolean.TRUE;
  public static Boolean PARCEL_SHIPMENT_ELIGIBLE = Boolean.TRUE;
  public static Double HEIGHT = 3.4;
  public static Double WEIGHT = 3.4;
  public static Double LENGTH = 3.4;
  public static Double VOLUME = 3.4;
  public static Long PROCESSING_TIME = 0L;
  public static Boolean IS_HAZMAT = Boolean.TRUE;
  public static String COST = "cost-1";
  public static String SOURCE_GEOZONE = "source-geozone-1";
  public static String DESTINATION_GEOZONE = "destination-geozone-1";
  public static String CARRIER_SERVICE_ID = "carrier-service-id-1";
  public static Float TRANSIT_DAYS = Float.valueOf(10);

  public static final String CARRIER_ID = "carrier-1";
  public static final String SERVICE_ID = "service-1";
  public static final String CARRIER_NAME = "carrier-name-1";
  public static final String SERVICE_NAME = "service-name-1";
  public static final String SERVICE_OPTIONS = "service-options-1";

  public static final String SERVICE_OPTION = "serviceOption-1";
  public static final String INVENTORY_TYPE = "inventoryType-1";

  public NodeEntity getNodeEntity() {
    return NodeEntity.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(BOPIS_ELIGIBLE)
        .city(CITY)
        .country(COUNTRY)
        .expressEligible(EXPRESS_ELIGIBLE)
        .nodeType(NODE_TYPE)
        .isActive(IS_ACTIVE)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .postalCode(POSTAL_CODE)
        .sdndEligible(SDND_ELIGIBLE)
        .province(PROVINCE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .build();
  }

  public NodeRequest getNodeRequest() {
    return NodeRequest.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(BOPIS_ELIGIBLE)
        .city(CITY)
        .country(COUNTRY)
        .expressEligible(EXPRESS_ELIGIBLE)
        .nodeType(NODE_TYPE)
        .isActive(IS_ACTIVE)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .postalCode(POSTAL_CODE)
        .sdndEligible(SDND_ELIGIBLE)
        .province(PROVINCE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .build();
  }

  public NodeResponse getNodeResponse() {
    return NodeResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(BOPIS_ELIGIBLE)
        .city(CITY)
        .country(COUNTRY)
        .expressEligible(EXPRESS_ELIGIBLE)
        .nodeType(NODE_TYPE)
        .isActive(IS_ACTIVE)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .postalCode(POSTAL_CODE)
        .sdndEligible(SDND_ELIGIBLE)
        .province(PROVINCE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .build();
  }

  public NodeUpdationRequest getNodeUpdationRequest() {
    return NodeUpdationRequest.builder()
        .isActive(Boolean.FALSE)
        .city("city-2")
        .latitude("3526.5262")
        .build();
  }

  public NodeResponse getUpdatedNodeResponse() {
    return NodeResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(BOPIS_ELIGIBLE)
        .city("city-2")
        .country(COUNTRY)
        .expressEligible(EXPRESS_ELIGIBLE)
        .nodeType(NODE_TYPE)
        .isActive(Boolean.FALSE)
        .latitude("3526.5262")
        .longitude(LONGITUDE)
        .postalCode(POSTAL_CODE)
        .sdndEligible(SDND_ELIGIBLE)
        .province(PROVINCE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .build();
  }

  public NodeEntity getUpdatedNodeEntity() {
    return NodeEntity.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(BOPIS_ELIGIBLE)
        .city("city-2")
        .country(COUNTRY)
        .expressEligible(EXPRESS_ELIGIBLE)
        .nodeType(NODE_TYPE)
        .isActive(Boolean.FALSE)
        .latitude("3526.5262")
        .longitude(LONGITUDE)
        .postalCode(POSTAL_CODE)
        .sdndEligible(SDND_ELIGIBLE)
        .province(PROVINCE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .build();
  }

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

  public TransitEntity getTransitEntity(Float transitDays) {
    return TransitEntity.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .transitDays(transitDays)
        .build();
  }

  public TransitEntity getTransitEntities(String carrierServiceId) {
    return TransitEntity.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .carrierServiceId(carrierServiceId)
        .transitDays(TRANSIT_DAYS)
        .build();
  }

  public TransitResponse getTransitResponse(Float transitDays) {
    return TransitResponse.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .transitDays(transitDays)
        .build();
  }

  public TransitDataCreationRequest getTransitDataCreationRequest(Float transitDays) {
    return TransitDataCreationRequest.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .transitDays(transitDays)
        .build();
  }

  public CarrierServiceRequest getCarrierServiceRequest() {
    return CarrierServiceRequest.builder()
        .orgId(ORG_ID)
        .carrierId(CARRIER_ID)
        .serviceId(SERVICE_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .build();
  }

  public CarrierServiceResponse getCarrierServiceResponse() {
    return CarrierServiceResponse.builder()
        .orgId(ORG_ID)
        .carrierId(CARRIER_ID)
        .serviceId(SERVICE_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .build();
  }

  public CarrierServiceUpdateRequest getCarrierServiceUpdateRequest() {
    return CarrierServiceUpdateRequest.builder()
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .build();
  }

  public CarrierServiceResponse getCarrierServiceUpdateResponse() {
    return CarrierServiceResponse.builder()
        .orgId(ORG_ID)
        .carrierId(CARRIER_ID)
        .serviceId(SERVICE_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .build();
  }

  public CarrierServiceEntity getCarrierServiceEntity() {
    return CarrierServiceEntity.builder()
        .orgId(ORG_ID)
        .carrierId(CARRIER_ID)
        .serviceId(SERVICE_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .build();
  }

  public CarrierServiceEntity getUpdatedCarrierServiceEntity() {
    return CarrierServiceEntity.builder()
        .orgId(ORG_ID)
        .carrierId(CARRIER_ID)
        .serviceId(SERVICE_ID)
        .carrierName("carrier-name-1")
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .build();
  }

  public ServiceInventoryRequest getServiceInventoryRequest() {
    return ServiceInventoryRequest.builder()
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .inventoryType(INVENTORY_TYPE)
        .build();
  }

  public ServiceInventoryDto getServiceInventoryDto() {
    return ServiceInventoryDto.builder()
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .inventoryType(INVENTORY_TYPE)
        .build();
  }

  public ServiceOptionInventoryTypeEntity getServiceOptionInventoryTypeEntity() {
    return ServiceOptionInventoryTypeEntity.builder()
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .inventoryType(INVENTORY_TYPE)
        .build();
  }
}
