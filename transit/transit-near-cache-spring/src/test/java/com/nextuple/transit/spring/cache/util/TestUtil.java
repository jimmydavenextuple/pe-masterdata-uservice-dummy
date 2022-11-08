package com.nextuple.transit.spring.cache.util;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.transit.cache.domain.TransitCacheKey;
import com.nextuple.transit.cache.domain.TransitCacheValue;
import com.nextuple.transit.domain.outbound.TransitResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestUtil {

  public static final String ORG_ID = "org-1";
  public static String SOURCE_GEOZONE = "source-geozone-1";
  public static String DESTINATION_GEOZONE = "destination-geozone-1";
  public static String CARRIER_SERVICE_ID = "carrier-service-id-1";
  public static Float TRANSIT_DAYS = 10F;

  public static Double BUFFER_DAYS = 3.0;

  public static final String SERVICE_OPTION = "serviceOption-1";

  public TransitCacheKey getTransitCacheKey() {
    return TransitCacheKey.builder().orgId(ORG_ID).destinationGeozone(DESTINATION_GEOZONE).build();
  }

  public TransitCacheValue getTransitCacheValue() {
    List<TransitResponse> transitResponseList = new ArrayList<>();
    transitResponseList.add(getTransitResponse(1.0F));
    return TransitCacheValue.builder().transitResponseList(transitResponseList).build();
  }

  private TransitResponse getTransitResponse(Float transitDays) {
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

  public BaseResponse<List<TransitResponse>> getBaseResponseOfTransit() {
    List<TransitResponse> transitResponseList = new ArrayList<>();
    transitResponseList.add(getTransitResponse(1.0F));
    return BaseResponse.builder().payload(transitResponseList).build();
  }
}
