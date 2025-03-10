/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.consumer;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.master.data.integration.dto.ResponseDto;
import com.nextuple.master.data.integration.enums.ActionEnum;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.transit.consumer.dto.TransferScheduleDto;
import com.nextuple.transit.consumer.dto.TransitBufferFeedDto;
import com.nextuple.transit.consumer.dto.TransitFeedDto;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import com.nextuple.transit.domain.outbound.TransitBufferResponse;
import com.nextuple.transit.domain.outbound.TransitBufferV2Response;
import com.nextuple.transit.domain.outbound.TransitResponse;
import com.nextuple.transit.persistence.domain.TransitBufferV2DomainDto;
import com.nextuple.transit.persistence.domain.TransitDomainDto;
import jakarta.validation.constraints.NotNull;
import org.joda.time.DateTime;

import java.util.Date;

public class TestUtil {
  public static final String ORG_ID = "NEXTUPLE";
  public static final String SOURCE_GEOZONE = "A1B";
  public static final String DESTINATION_GEOZONE = "H1R";
  public static final String CARRIER_SERVICE_ID = "UPS-GROUND";
  public static final float TRANSIT_DAYS = 2;
  public static final Date BUFFER_START_DATE = new Date();
  public static final Date BUFFER_END_DATE = new Date();
  public static final Double TRANSIT_BUFFER_DAYS = 2.0;

  public TransitFeedDto createTransitFeedDto() {
    return TransitFeedDto.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .transitDays(TRANSIT_DAYS)
        .build();
  }

  public TransitBufferFeedDto createTransitBufferFeedDto() {
    return TransitBufferFeedDto.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .bufferStartDate(BUFFER_START_DATE)
        .bufferEndDate(BUFFER_END_DATE)
        .bufferDays(TRANSIT_BUFFER_DAYS)
        .build();
  }

  public BatchRequest<TransitFeedDto> getTransitFeedRequest(ActionEnum action) {
    BatchRequest<TransitFeedDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(action);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(createTransitFeedDto());
    return batchRequest;
  }

  public BatchRequest<TransitBufferFeedDto> getTransitBufferFeedRequest(ActionEnum action) {
    BatchRequest<TransitBufferFeedDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(action);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(createTransitBufferFeedDto());
    return batchRequest;
  }

  public ResponseDto createResponseDto(int recordNo, int statusCode, String message) {
    return ResponseDto.builder().recordNo(recordNo).statusCode(statusCode).message(message).build();
  }

  public BatchResponse getTransitBatchResponse(
      int totalRecords, int successfulRecords, int failedRecords) {
    return BatchResponse.builder()
        .totalRecords(totalRecords)
        .successfulRecords(successfulRecords)
        .failedRecords(failedRecords)
        .build();
  }

  public BatchResponse getTransitBufferBatchResponse(
      int totalRecords, int successfulRecords, int failedRecords) {
    return BatchResponse.builder()
        .totalRecords(totalRecords)
        .successfulRecords(successfulRecords)
        .failedRecords(failedRecords)
        .build();
  }

  public BaseResponse<TransitResponse> getBaseResponseOfTransitFeed(String message) {
    return BaseResponse.builder()
        .message(message)
        .success(true)
        .payload(getTransitResponse())
        .build();
  }

  public BaseResponse<TransitBufferV2Response> getBaseResponseOfTransitBufferFeed(String message) {
    return BaseResponse.builder()
        .message(message)
        .success(true)
        .payload(getTransitBufferResponse())
        .build();
  }

  public TransitResponse getTransitResponse() {
    return TransitResponse.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .transitDays(TRANSIT_DAYS)
        .build();
  }

  public TransitBufferResponse getTransitBufferResponse() {
    return TransitBufferResponse.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .destinationGeozone(DESTINATION_GEOZONE)
        .bufferStartDate(BUFFER_START_DATE)
        .bufferEndDate(BUFFER_END_DATE)
        .bufferDays(TRANSIT_BUFFER_DAYS)
        .build();
  }

  public TransitDomainDto getTransitDomainDto() {
    return TransitDomainDto.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .transitDays(TRANSIT_DAYS)
        .build();
  }

  public TransitBufferV2DomainDto getTransitBufferDomainDto() {
    return TransitBufferV2DomainDto.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .bufferStartDate(BUFFER_START_DATE)
        .bufferEndDate(BUFFER_END_DATE)
        .bufferDays(TRANSIT_BUFFER_DAYS)
        .build();
  }

  public BatchRequest<TransferScheduleDto> getTransferScheduleFeedRequest(ActionEnum actionEnum) {
    BatchRequest<TransferScheduleDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(actionEnum);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(createTransferFeedDto());
    return batchRequest;
  }

  private TransferScheduleDto createTransferFeedDto() {
    return TransferScheduleDto.builder().orgId(ORG_ID).sourceNodeId("A1B").dropoffNodeId("H1R").startTime(new DateTime()).endTime(new DateTime().plusHours(3)).build();
  }

  public BaseResponse<TransferScheduleResponse> getBaseResponseOfTransferScheduleFeed(String message) {
    return BaseResponse.builder()
            .message(message)
            .success(true)
            .payload(getTransferScheduleResponse())
            .build();
  }

  private TransferScheduleResponse getTransferScheduleResponse() {
    return TransferScheduleResponse.builder()
            .orgId(ORG_ID)
            .sourceNodeId("A1B")
            .dropoffNodeId("H1R")
            .startTime(new Date())
            .endTime(new Date())
            .build();
  }

  public BatchResponse getTransferScheduleBatchResponse(
          int totalRecords, int successfulRecords, int failedRecords) {
    return BatchResponse.builder()
            .totalRecords(totalRecords)
            .successfulRecords(successfulRecords)
            .failedRecords(failedRecords)
            .build();
  }
}
