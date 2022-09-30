package com.hbc.transit;

import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import com.hbc.common.response.BaseResponse;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.hbc.transit.domain.dto.TransitTimeEntriesDto;
import com.hbc.transit.domain.entity.TransitEntity;
import com.hbc.transit.domain.inbound.TransitBufferCreationRequest;
import com.hbc.transit.domain.inbound.TransitDataCreationRequest;
import com.hbc.transit.domain.inbound.TransitDataUpdationRequest;
import com.hbc.transit.domain.outbound.TransitResponse;
import java.util.Date;
import java.util.List;

public class TestUtil {

  public static final String ORG_ID = "org-1";
  public static String SOURCE_GEOZONE = "source-geozone-1";
  public static String DESTINATION_GEOZONE = "destination-geozone-1";
  public static String CARRIER_SERVICE_ID = "carrier-service-id-1";

  public static final String CARRIER_NAME = "carrier-name-1";
  public static final String SERVICE_NAME = "service-name-1";
  public static final String SERVICE_OPTIONS = "service-options-1";
  public static final String CARRIER_ID = "carrier-1";
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

  public TransitEntity getTransitEntity3(Double bufferDays) {
    return TransitEntity.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .transitDays(TRANSIT_DAYS)
        .bufferDays(bufferDays)
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

  public TransitResponse getTransitResponse2(Double bufferDays) {
    Date bufferStartDate = new Date(1000);
    Date bufferEndDate = new Date(1000);
    return TransitResponse.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .transitDays(10f)
        .bufferDays(bufferDays)
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
    return TransitDataUpdationRequest.builder().transitDays(transitDays).build();
  }

  public TransitBufferCreationRequest getTransitBufferCreationRequest(Double bufferDays) {
    return TransitBufferCreationRequest.builder().bufferDays(bufferDays).build();
  }

  public TransitTimeEntriesDto getTransitTimeEntriesDto(String orgId, String carrierServiceId) {
    return TransitTimeEntriesDto.builder()
        .orgId(orgId)
        .carrierServiceId(carrierServiceId)
        .totalRecords(2)
        .build();
  }

  public BaseResponse<List<CarrierServiceResponse>> getCarrierServiceUpdateResponse() {
    var carrierResponse =
        List.of(
            CarrierServiceResponse.builder()
                .orgId(ORG_ID)
                .carrierId(CARRIER_ID)
                .carrierServiceId(CARRIER_SERVICE_ID)
                .carrierName(CARRIER_NAME)
                .serviceName(SERVICE_NAME)
                .serviceOptions(SERVICE_OPTIONS)
                .build());
    return BaseResponse.builder().payload(carrierResponse).build();
  }

  public BaseResponse<PostalCodeTimezoneDto> getBaseResponseOfPostalCodeTimezoneDto() {
    BaseResponse<PostalCodeTimezoneDto> response = new BaseResponse<>();
    response.setPayload(getPostalCodeTimezoneDto());
    response.setSuccess(true);
    return response;
  }

  public PostalCodeTimezoneDto getPostalCodeTimezoneDto() {
    return PostalCodeTimezoneDto.builder().orgId(ORG_ID).postalCodePrefix(SOURCE_GEOZONE).build();
  }
}
