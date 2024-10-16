/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.consumer;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.master.data.integration.dto.ResponseDto;
import com.nextuple.master.data.integration.enums.ActionEnum;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.node.consumer.dto.NodeFeedDto;
import com.nextuple.node.domain.outbound.NodeResponse;
import com.nextuple.node.persistence.domain.NodeDomainDto;
import java.util.Map;

public class TestUtil {

  public static final String NODE_ID = "node-1";
  public static final String NODE_ID_2 = "node-2";
  public static final String ORG_ID = "org-1";
  public static final String STREET = "street-1";
  public static final String CITY = "city-1";
  public static final String STATE = "state-1";
  public static final String ZIP_CODE = "33666";
  public static final String COUNTRY = "IN";
  public static final String LATITUDE = "43.769912";
  public static final String LONGITUDE = "-79.296678";
  public static final String TIME_ZONE = "America/Toronto";
  public static Boolean SHIP_TO_TIME = Boolean.TRUE;
  public static Boolean BOPIS_ELIGIBLE = Boolean.TRUE;
  public static String NODE_TYPE = "MFC";
  public static Boolean IS_ACTIVE = Boolean.TRUE;
  public static final String NODE_LABOUR_TIER = "tier1";

  public NodeFeedDto createNodeFeedDto() {
    return NodeFeedDto.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(BOPIS_ELIGIBLE)
        .city(CITY)
        .nodeType("Store")
        .nodeLabourTier(NODE_LABOUR_TIER)
        .country(COUNTRY)
        .nodeType(NODE_TYPE)
        .isActive(IS_ACTIVE)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .zipCode(ZIP_CODE)
        .state(STATE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .serviceOptionEligibilities(getServiceOptionEligibilities())
        .build();
  }

  public Map<String, Boolean> getServiceOptionEligibilities() {
    return Map.of(
        "sdndEligible", Boolean.TRUE,
        "expressEligible", Boolean.TRUE,
        "nextdayEligible", Boolean.TRUE);
  }

  public BatchRequest<NodeFeedDto> getNodeFeedRequest(ActionEnum action) {
    BatchRequest<NodeFeedDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(action);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(createNodeFeedDto());
    return batchRequest;
  }

  public ResponseDto createResponseDto(int recordNo, int statusCode, String message) {
    return ResponseDto.builder().recordNo(recordNo).statusCode(statusCode).message(message).build();
  }

  public BatchResponse getNodeBatchResponse(
      int totalRecords, int successfulRecords, int failedRecords) {
    return BatchResponse.builder()
        .totalRecords(totalRecords)
        .successfulRecords(successfulRecords)
        .failedRecords(failedRecords)
        .build();
  }

  public BaseResponse<NodeResponse> getBaseResponseOfNodeFeed(String message) {
    return BaseResponse.builder().message(message).success(true).payload(getNodeResponse()).build();
  }

  public NodeResponse getNodeResponse() {
    return NodeResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(BOPIS_ELIGIBLE)
        .city(CITY)
        .country(COUNTRY)
        .nodeType(NODE_TYPE)
        .nodeLabourTier(NODE_LABOUR_TIER)
        .isActive(IS_ACTIVE)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .zipCode(ZIP_CODE)
        .state(STATE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .serviceOptionEligibilities(getServiceOptionEligibilities())
        .build();
  }

  public NodeDomainDto getNodeDomainDto() {
    return NodeDomainDto.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(BOPIS_ELIGIBLE)
        .city(CITY)
        .country(COUNTRY)
        .nodeType(NODE_TYPE)
        .nodeLabourTier(NODE_LABOUR_TIER)
        .isActive(IS_ACTIVE)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .zipCode(ZIP_CODE)
        .state(STATE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .serviceOptionEligibilities(getServiceOptionEligibilities())
        .build();
  }
}
