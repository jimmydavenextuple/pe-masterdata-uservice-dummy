package com.hbc.transit;

import com.hbc.transit.domain.dto.TransitTimeEntriesDto;
import com.hbc.transit.domain.entity.TransitEntity;
import com.hbc.transit.domain.inbound.TransitDataCreationRequest;
import com.hbc.transit.domain.inbound.TransitDataUpdationRequest;
import com.hbc.transit.domain.outbound.TransitResponse;
import java.util.Date;

public class TestUtil {

  public static final String ORG_ID = "org-1";
  public static String SOURCE_GEOZONE = "source-geozone-1";
  public static String DESTINATION_GEOZONE = "destination-geozone-1";
  public static String CARRIER_SERVICE_ID = "carrier-service-id-1";
  public static Float TRANSIT_DAYS = 10F;

  public static Double BUFFER_DAYS = 3.0;

  public static final String SERVICE_OPTION = "serviceOption-1";

  public TransitEntity getTransitEntity(Float transitDays) {
    Date bufferStartDate = new Date(1000);
    Date bufferEndDate = new Date(1000);
    return TransitEntity.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .transitDays(transitDays)
        .bufferDays(BUFFER_DAYS)
        .bufferStartDate(bufferStartDate)
        .bufferEndDate(bufferEndDate)
        .build();
  }

  public TransitEntity getTransitEntity2(Float transitDays) {
    return TransitEntity.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .transitDays(transitDays)
        .bufferDays(BUFFER_DAYS)
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
    Date bufferStartDate = new Date(1000);
    Date bufferEndDate = new Date(1000);
    return TransitResponse.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .transitDays(transitDays)
        .bufferDays(3.0)
        .bufferStartDate(bufferStartDate)
        .bufferEndDate(bufferEndDate)
        .build();
  }

  public TransitResponse getTransitResponse2(Float transitDays) {
    return TransitResponse.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .transitDays(transitDays)
        .bufferDays(3.0)
        .build();
  }

  public TransitDataCreationRequest getTransitDataCreationRequest(Float transitDays) {
    Date bufferStartDate = new Date(1000);
    Date bufferEndDate = new Date(1000);
    return TransitDataCreationRequest.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .transitDays(transitDays)
        .bufferDays(BUFFER_DAYS)
        .bufferStartDate(bufferStartDate)
        .bufferEndDate(bufferEndDate)
        .build();
  }

  public TransitDataUpdationRequest getTransitDataUpdationRequest(Float transitDays) {
    return TransitDataUpdationRequest.builder()
        .transitDays(transitDays)
        .bufferDays(BUFFER_DAYS)
        .build();
  }

  public TransitTimeEntriesDto getTransitTimeEntriesDto(String orgId, String carrierServiceId) {
    return TransitTimeEntriesDto.builder()
        .orgId(orgId)
        .carrierServiceId(carrierServiceId)
        .totalRecords(2)
        .build();
  }
}
