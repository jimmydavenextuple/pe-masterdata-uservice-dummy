/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.masterdata.calendar.controller;

import com.nextuple.calendar.domain.dto.NodeCarrierCalendarCacheKeyDto;
import com.nextuple.calendar.domain.inbound.NodeCarrierServiceCalendarRequest;
import com.nextuple.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.nextuple.calendar.persistence.exception.CalendarDomainException;
import com.nextuple.calendar.persistence.exception.DateException;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.pe.masterdata.calendar.controller.docs.GetNodeCarrierCalendarCacheKeysDoc;
import com.nextuple.pe.masterdata.calendar.controller.docs.GetNodeCarrierServiceCalendarDoc;
import com.nextuple.pe.masterdata.calendar.controller.docs.HandleCreateNodeCarrierServiceCalendarDoc;
import com.nextuple.pe.masterdata.calendar.controller.docs.HandleGetNodeCarrierServiceCalendarDoc;
import com.nextuple.pe.masterdata.calendar.service.NodeCarrierServiceCalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Node Carrier Service Calendar APIs")
@RequestMapping("/node-carrier-service-calendar")
@RequiredArgsConstructor
public class NodeCarrierServiceCalendarController {

  private static final Logger logger =
      LoggerFactory.getLogger(NodeCarrierServiceCalendarController.class);
  private final NodeCarrierServiceCalendarService nodeCarrierServiceCalendarService;

  @HandleCreateNodeCarrierServiceCalendarDoc
  @PostMapping
  public ResponseEntity<BaseResponse<NodeCarrierServiceCalendarResponse>>
      handleCreateNodeCarrierServiceCalendar(
          @Valid @RequestBody NodeCarrierServiceCalendarRequest nodeCarrierServiceCalendarRequest)
          throws CalendarDomainException, CommonServiceException, DateException {
    logger.debug(
        "Inside handleCreateNodeCarrierServiceCalendar() for nodeCarrierServiceCalendarRequest: {}",
        nodeCarrierServiceCalendarRequest);
    try {
      var nodeCarrierServiceCalendarResponse =
          nodeCarrierServiceCalendarService.processCreateNodeCarrierServiceCalendarResponse(
              nodeCarrierServiceCalendarRequest);
      logger.info(
          "Response after creation of node carrier service calendar:{}",
          nodeCarrierServiceCalendarResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node carrier service calendar created successfully!")
              .payload(nodeCarrierServiceCalendarResponse)
              .build());
    } catch (Exception e) {
      logger.error("Error in handleCreateNodeCarrierServiceCalendar()");
      throw e;
    }
  }

  @HandleGetNodeCarrierServiceCalendarDoc
  @GetMapping("/{orgId}/{nodeId}/{carrierServiceId}")
  public ResponseEntity<BaseResponse<List<NodeCarrierServiceCalendarResponse>>>
      handleGetNodeCarrierServiceCalendar(
          @NotBlank(message = "orgId can't be empty")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization",
                  example = "NEXTUPLE")
              String orgId,
          @NotBlank(message = "orgId can't be empty")
              @PathVariable
              @Parameter(description = "Unique identifier of the node", example = "NODE01")
              String nodeId,
          @NotBlank(message = "orgId can't be empty")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the carrier service",
                  example = "ALL-SDND")
              String carrierServiceId,
          @RequestParam @Parameter(description = "Service option of the carrier", example = "SDND")
              Optional<String> serviceOption)
          throws CalendarDomainException {
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node carrier&Service calendar details fetched successfully!")
              .payload(
                  nodeCarrierServiceCalendarService.processGetNodeCarrierServiceCalendar(
                      orgId, nodeId, carrierServiceId, serviceOption))
              .build());
    } catch (Exception e) {
      logger.error("Error in handleGetNodeCarrierServiceCalendar()");
      throw e;
    }
  }

  @Operation(
      summary = "Get Node Carrier Service Calendar Cache Keys",
      description = "Retrieves the list of the node carrier service calendar cache keys.")
  @ApiResponse(
      responseCode = "200",
      description =
          "A 200 success code indicates that the list of all node carrier service calendar cache keys is retrieved successfully.")
  @GetNodeCarrierCalendarCacheKeysDoc
  @GetMapping("/get-all-cache-keys")
  public ResponseEntity<BaseResponse<List<NodeCarrierCalendarCacheKeyDto>>>
      getNodeCarrierCalendarCacheKeys(
          @RequestParam @Parameter(description = "Limit of cache keys", example = "20")
              Integer limit)
          throws CalendarDomainException {
    logger.debug("Processing get Node Carrier Calendar Cache Keys");

    var response = nodeCarrierServiceCalendarService.getAllNodeCarrierCalendarCacheKeys(limit);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier Calendar Cache Keys fetched successfully")
            .payload(response)
            .build());
  }

  @GetMapping("org/{orgId}")
  @GetNodeCarrierServiceCalendarDoc
  public ResponseEntity<BaseResponse<List<NodeCarrierServiceCalendarResponse>>>
      getAllNodeCarrierServiceCalendarsByOrgId(
          @NotBlank(message = "orgId can't be empty")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization",
                  example = "NEXTUPLE")
              String orgId)
          throws CalendarDomainException {
    logger.debug("Processing get Node Carrier Calendar by orgId");
    var response = nodeCarrierServiceCalendarService.processGetAllNodeCarrierServiceCalendar(orgId);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier Calendar Cache Keys fetched successfully")
            .payload(response)
            .build());
  }
}
