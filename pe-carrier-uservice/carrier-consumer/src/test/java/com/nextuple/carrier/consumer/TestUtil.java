/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.carrier.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.nextuple.carrier.consumer.dto.CarrierFeedDto;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.carrier.persistence.domain.CarrierServiceDomainDto;
import com.nextuple.common.enums.ActionEnum;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.master.data.integration.dto.ResponseDto;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;

public class TestUtil {
  public static final String CARRIER_ID = "carrier-1";
  public static final String CARRIER_SERVICE_ID = "carrier-service-1";
  public static final String ORG_ID = "org-1";
  public static final String CARRIER_NAME = "UPS";
  public static final String SERVICE_NAME = "GROUND";
  public static final String SERVICE_OPTIONS = "STANDARD";
  private static final JsonNode CUSTOM_ATTRIBUTES =
      JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2");

  public CarrierFeedDto createCarrierFeedDto() {
    return CarrierFeedDto.builder()
        .carrierId(CARRIER_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .orgId(ORG_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public BatchRequest<CarrierFeedDto> getCarrierFeedRequest(ActionEnum action) {
    BatchRequest<CarrierFeedDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(action);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(createCarrierFeedDto());
    return batchRequest;
  }

  public ResponseDto createResponseDto(int recordNo, int statusCode, String message) {
    return ResponseDto.builder().recordNo(recordNo).statusCode(statusCode).message(message).build();
  }

  public BatchResponse getCarrierBatchResponse(
      int totalRecords, int successfulRecords, int failedRecords) {
    return BatchResponse.builder()
        .totalRecords(totalRecords)
        .successfulRecords(successfulRecords)
        .failedRecords(failedRecords)
        .build();
  }

  public BaseResponse<CarrierServiceResponse> getBaseResponseOfCarrierFeed(String message) {
    return BaseResponse.builder()
        .message(message)
        .success(true)
        .payload(getCarrierResponse())
        .build();
  }

  public CarrierServiceResponse getCarrierResponse() {
    return CarrierServiceResponse.builder()
        .carrierId(CARRIER_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .orgId(ORG_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public CarrierServiceDomainDto getCarrierServiceDomainDto() {
    return CarrierServiceDomainDto.builder()
        .carrierId(CARRIER_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .orgId(ORG_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }
}
