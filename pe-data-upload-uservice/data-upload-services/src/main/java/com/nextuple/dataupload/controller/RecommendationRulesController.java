/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.common.inbound.ConfigureShipChargeCappingRequest;
import com.nextuple.dataupload.common.inbound.DeleteTargetProfitMarginRequest;
import com.nextuple.dataupload.common.inbound.TargetProfitMarginRequest;
import com.nextuple.dataupload.common.outbound.AttributeAndValuesTGMResponse;
import com.nextuple.dataupload.common.outbound.ConfigureShipChargeCappingResponse;
import com.nextuple.dataupload.common.outbound.ShipChargeDetailsTGMResponse;
import com.nextuple.dataupload.common.outbound.TargetProfitMarginResponse;
import com.nextuple.dataupload.controller.docs.ConfigureShipChargeCappingDoc;
import com.nextuple.dataupload.controller.docs.ConfigureTargetProfitMarginDoc;
import com.nextuple.dataupload.controller.docs.DeleteTargetProfitMarginDoc;
import com.nextuple.dataupload.controller.docs.FetchAttributeDetailsDoc;
import com.nextuple.dataupload.controller.docs.FetchShipChargeDetailsDoc;
import com.nextuple.dataupload.controller.docs.FetchTargetProfitMarginDoc;
import com.nextuple.dataupload.controller.docs.UpdateShipChargeCappingStatusDoc;
import com.nextuple.dataupload.controller.docs.UpdateTargetProfitMarginDoc;
import com.nextuple.dataupload.service.RecommendationRulesService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ui/recommendation-rules")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Recommendation rule APIs")
public class RecommendationRulesController {

  private final RecommendationRulesService recommendationRulesService;

  @ConfigureTargetProfitMarginDoc
  @PostMapping(value = "/{orgId}")
  public ResponseEntity<BaseResponse<TargetProfitMarginResponse>> createTargetProfitMargin(
      @PathVariable @Parameter(description = "Unique identifier for organization ID.") String orgId,
      @Valid @RequestBody TargetProfitMarginRequest targetProfitMarginRequest)
      throws CommonServiceException {
    log.debug(
        "Processing create gross profit target margin {} for orgId {},  attributeName {},  attribute value {} ",
        targetProfitMarginRequest.getTargetGrossProfitMargin(),
        orgId,
        targetProfitMarginRequest.getAttributeName(),
        targetProfitMarginRequest.getAttributeValue());
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder()
                .message("Target profit margin configured successfully")
                .payload(
                    recommendationRulesService.createTargetProfitMargin(
                        orgId, targetProfitMarginRequest))
                .build());
  }

  @PutMapping("/{orgId}")
  @UpdateTargetProfitMarginDoc
  public ResponseEntity<BaseResponse<TargetProfitMarginResponse>> updateTargetProfitMarginConfig(
      @PathVariable @Parameter(description = "Unique identifier for organization ID.") String orgId,
      @Valid @RequestBody TargetProfitMarginRequest targetProfitMarginRequest)
      throws CommonServiceException {
    log.debug(
        "Processing update gross profit target margin {} for orgId {},  attributeName {},  attribute value {} ",
        targetProfitMarginRequest.getTargetGrossProfitMargin(),
        orgId,
        targetProfitMarginRequest.getAttributeName(),
        targetProfitMarginRequest.getAttributeValue());
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder()
                .message("Target profit margin updated successfully")
                .payload(
                    recommendationRulesService.updateTargetProfitGrossMargin(
                        orgId, targetProfitMarginRequest))
                .build());
  }

  @PutMapping(value = "/{orgId}/delete")
  @DeleteTargetProfitMarginDoc
  public ResponseEntity<BaseResponse<String>> deleteTargetProfitMargin(
      @PathVariable @Parameter(description = "Unique identifier for organization ID.") String orgId,
      @RequestBody DeleteTargetProfitMarginRequest request)
      throws CommonServiceException {
    log.debug(
        "Processing delete gross profit target margin for orgId: {},  attributeName: {},  attribute values: {} ",
        orgId,
        request.getAttributeName(),
        String.join(",", request.getAttributeValues()));
    recommendationRulesService.deleteTargetProfitMargin(orgId, request);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder().message("Target profit margin(s) deleted successfully").build());
  }

  @GetMapping(value = "/{orgId}/{attributeName}")
  @FetchTargetProfitMarginDoc
  public ResponseEntity<BaseResponse<List<TargetProfitMarginResponse>>> fetchTargetProfitMargins(
      @PathVariable @Parameter(description = "Unique identifier for organization ID.") String orgId,
      @PathVariable @Parameter(description = "Attribute name of target profit margin")
          String attributeName)
      throws CommonServiceException {
    log.debug(
        "Processing fetch gross profit target margin for orgId: {},  attributeName: {}",
        orgId,
        attributeName);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder()
                .message("Target profit margin fetched successfully")
                .payload(recommendationRulesService.fetchTargetProfitMargin(orgId, attributeName))
                .build());
  }

  @GetMapping(value = "/fetch-attribute-details/{orgId}")
  @FetchAttributeDetailsDoc
  public ResponseEntity<BaseResponse<AttributeAndValuesTGMResponse>> fetchAttributeDetails(
      @PathVariable @Parameter(description = "Unique identifier for organization ID.") String orgId)
      throws CommonServiceException {
    log.debug("Processing fetch attribute details for orgId: {}", orgId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder()
                .message("Attribute fetched successfully")
                .payload(recommendationRulesService.fetchAttributeDetails(orgId))
                .build());
  }

  @GetMapping(value = "/ship-charge-capping-constants/{orgId}")
  @FetchShipChargeDetailsDoc
  public ResponseEntity<BaseResponse<ShipChargeDetailsTGMResponse>> fetchShipChargeDetailsDetails(
      @PathVariable @Parameter(description = "Unique identifier for organization ID.")
          String orgId) {
    log.debug("Processing fetch ship charge details for orgId: {}", orgId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder()
                .message("Ship charge capping constants fetched successfully")
                .payload(recommendationRulesService.fetchShipChargeDetails(orgId))
                .build());
  }

  @PostMapping(value = "/ship-charge-capping-constants/{orgId}")
  @ConfigureShipChargeCappingDoc
  public ResponseEntity<BaseResponse<ConfigureShipChargeCappingResponse>>
      configureShipChargeCapping(
          @PathVariable @Parameter(description = "Unique identifier for organization ID.")
              String orgId,
          @RequestBody ConfigureShipChargeCappingRequest request)
          throws CommonServiceException {
    log.debug("Processing configure ship charge details for orgId: {}", orgId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder()
                .message("Ship charge capping constants configured successfully")
                .payload(recommendationRulesService.configureShipChargeCapping(orgId, request))
                .build());
  }

  @PutMapping(value = "/update-ship-charge-capping-status/{orgId}")
  @UpdateShipChargeCappingStatusDoc
  public ResponseEntity<BaseResponse<String>> updateShipChargeCappingStatus(
      @PathVariable @Parameter(description = "Unique identifier for organization ID.") String orgId,
      @RequestParam
          @Parameter(description = "Flag to describe about the shipping logic is applied or not.")
          Boolean isShipChargeCappingLogicEnabled)
      throws CommonServiceException {
    log.debug(
        "Processing updating ship charge capping status for orgId: {} & isShipChargeCappingLogicEnabled: {}",
        orgId,
        isShipChargeCappingLogicEnabled);
    recommendationRulesService.updateShipChargeCappingStatus(
        orgId, isShipChargeCappingLogicEnabled);
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            BaseResponse.builder()
                .message("Ship charge capping logic status updated successfully")
                .build());
  }
}
