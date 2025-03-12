/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.spring.cache.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.transit.cache.domain.TransferScheduleCacheKey;
import com.nextuple.transit.cache.domain.TransferScheduleCacheValue;
import com.nextuple.transit.cache.domain.TransitBufferV2CacheKey;
import com.nextuple.transit.cache.domain.TransitBufferV2CacheValue;
import com.nextuple.transit.cache.domain.TransitCacheKey;
import com.nextuple.transit.cache.domain.TransitCacheValue;
import com.nextuple.transit.cache.domain.ZoneCacheKey;
import com.nextuple.transit.cache.domain.ZoneCacheValue;
import com.nextuple.transit.domain.dto.TransitBufferDetailsDto;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import com.nextuple.transit.domain.outbound.TransitBufferDetailsResponse;
import com.nextuple.transit.domain.outbound.TransitResponse;
import com.nextuple.transit.domain.outbound.ZoneResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class TestUtil {

  public static final String ORG_ID = "org-1";
  public static final long RANDOM_ID = (long) (Math.random() * 1000);
  public static String SOURCE_GEOZONE = "source-geozone-1";
  public static String DESTINATION_GEOZONE = "destination-geozone-1";
  public static String CARRIER_SERVICE_ID = "carrier-service-id-1";
  public static Float TRANSIT_DAYS = 10F;
  public static String SOURCE_NODE = "TNode2";
  public static String DROPOFF_NODE = "SNode1";

  public static Double BUFFER_DAYS = 3.0;

  public static final String SERVICE_OPTION = "serviceOption-1";
  public static final String ZONE = "Zone1";
  public static final JsonNode CUSTOM_ATTRIBUTES =
      JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2");

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

  public ZoneCacheKey getZoneCacheKey() {
    return ZoneCacheKey.builder().orgId(ORG_ID).destinationGeozone(DESTINATION_GEOZONE).build();
  }

  private ZoneResponse getZoneResponse() {
    return ZoneResponse.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .zone(ZONE)
        .build();
  }

  public ZoneCacheValue getZoneCacheValue() {
    List<ZoneResponse> zoneResponseList = new ArrayList<>();
    zoneResponseList.add(getZoneResponse());
    return ZoneCacheValue.builder().zoneResponseList(zoneResponseList).build();
  }

  public BaseResponse<List<ZoneResponse>> getBaseResponseOfZone() {
    List<ZoneResponse> zoneResponseList = new ArrayList<>();
    zoneResponseList.add(getZoneResponse());
    return BaseResponse.builder().payload(zoneResponseList).build();
  }

  public TransitBufferDetailsResponse getTransitBufferDetailsResponse() {
    return TransitBufferDetailsResponse.builder()
        .sourceGeozone(SOURCE_GEOZONE)
        .orgId(ORG_ID)
        .destinationGeozone(DESTINATION_GEOZONE)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .transitBuffers(
            List.of(
                TransitBufferDetailsDto.builder()
                    .bufferDays(2D)
                    .bufferStartDate(new Date())
                    .bufferEndDate(new Date())
                    .customAttributes(CUSTOM_ATTRIBUTES)
                    .build()))
        .build();
  }

  public BaseResponse<List<TransitBufferDetailsResponse>> getBaseResponseOfTransitBuffer() {
    return BaseResponse.builder().payload(List.of(getTransitBufferDetailsResponse())).build();
  }

  public TransitBufferV2CacheValue getTransitBufferV2CacheValue() {
    return TransitBufferV2CacheValue.builder()
        .transitBufferDetailsResponseList(getBaseResponseOfTransitBuffer().getPayload())
        .build();
  }

  public TransitBufferV2CacheKey getTransitBufferV2CacheKey() {
    return TransitBufferV2CacheKey.builder()
        .orgId(ORG_ID)
        .destinationGeozone(DESTINATION_GEOZONE)
        .requestDate(LocalDate.now())
        .horizonDays(7)
        .build();
  }

  public TransferScheduleCacheKey getTransferScheduleCacheKey() {
    return TransferScheduleCacheKey.builder().orgId(ORG_ID).dropoffNode(DROPOFF_NODE).build();
  }

  public TransferScheduleCacheValue getTransferScheduleCacheValue() {
    return TransferScheduleCacheValue.builder()
        .transferScheduleResponseList(getListBaseResponseOfTransferScheduleResponse().getPayload())
        .build();
  }

  public BaseResponse<List<TransferScheduleResponse>>
      getListBaseResponseOfTransferScheduleResponse() {
    return BaseResponse.builder().payload(List.of(getTransferScheduleResponse())).build();
  }

  public TransferScheduleResponse getTransferScheduleResponse() {
    return TransferScheduleResponse.builder()
        .id(RANDOM_ID)
        .sourceNodeId(SOURCE_NODE)
        .orgId(ORG_ID)
        .dropoffNodeId(DROPOFF_NODE)
        .startTime(new DateTime(DateTimeZone.UTC).toDate())
        .endTime(new DateTime(DateTimeZone.UTC).plusHours(3).toDate())
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }
}
