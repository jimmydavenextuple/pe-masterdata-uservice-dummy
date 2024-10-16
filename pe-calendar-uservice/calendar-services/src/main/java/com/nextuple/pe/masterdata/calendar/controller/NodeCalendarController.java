/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.masterdata.calendar.controller;

import com.nextuple.calendar.domain.dto.NodeCalendarCacheKeyDto;
import com.nextuple.calendar.domain.inbound.NodeCalendarRequest;
import com.nextuple.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.calendar.persistence.exception.CalendarDomainException;
import com.nextuple.calendar.persistence.exception.DateException;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.pe.masterdata.calendar.controller.docs.GetNodeCalendarCacheKeysDoc;
import com.nextuple.pe.masterdata.calendar.controller.docs.GetNodeCalendars;
import com.nextuple.pe.masterdata.calendar.controller.docs.HandleCreateNodeCalendarDoc;
import com.nextuple.pe.masterdata.calendar.controller.docs.HandleGetNodeCalendarDoc;
import com.nextuple.pe.masterdata.calendar.service.NodeCalendarService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@Tag(name = "Node Calendar APIs")
@RequestMapping("/node-calendar")
@RequiredArgsConstructor
public class NodeCalendarController {

  private static final Logger logger = LoggerFactory.getLogger(NodeCalendarController.class);
  private final NodeCalendarService nodeCalendarService;

  @HandleCreateNodeCalendarDoc
  @PostMapping
  public ResponseEntity<BaseResponse<NodeCalendarResponse>> handleCreateNodeCalendar(
      @Valid @RequestBody NodeCalendarRequest nodeCalendarRequest)
      throws CalendarDomainException, CommonServiceException, DateException {
    logger.debug(
        "Inside handleCreateNodeCalendar() for nodeCalendarRequest: {}", nodeCalendarRequest);
    try {
      var nodeCalendarResponse = nodeCalendarService.processCreateNodeCalendar(nodeCalendarRequest);
      logger.info("Response after creation of node calendar:{}", nodeCalendarResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node calendar created successfully!")
              .payload(nodeCalendarResponse)
              .build());
    } catch (Exception e) {
      logger.error("Error in handleCreateNodeCalendar()");
      throw e;
    }
  }

  @HandleGetNodeCalendarDoc
  @GetMapping("/{orgId}/{nodeId}")
  public ResponseEntity<BaseResponse<List<NodeCalendarResponse>>> handleGetNodeCalendar(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization", example = "NEXTUPLE")
          String orgId,
      @NotBlank(message = "nodeId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the node", example = "NODE01")
          String nodeId)
      throws CalendarDomainException {
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node calendar details fetched successfully!")
              .payload(nodeCalendarService.processGetNodeCalendar(orgId, nodeId))
              .build());
    } catch (Exception e) {
      logger.error("Error in handleGetNodeCalendar()");
      throw e;
    }
  }

  @GetMapping("/get-all-cache-keys")
  @GetNodeCalendarCacheKeysDoc
  public ResponseEntity<BaseResponse<List<NodeCalendarCacheKeyDto>>> getNodeCalendarCacheKeys(
      @RequestParam @Parameter(description = "Limit of cache keys", example = "20") Integer limit)
      throws CalendarDomainException {
    logger.debug("Processing get Node Calendar Cache Keys");

    var response = nodeCalendarService.getAllNodeCalendarCacheKeys(limit);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Calendar Cache Keys fetched successfully")
            .payload(response)
            .build());
  }

  @GetNodeCalendars
  @GetMapping("/get-calendar-association/{calendarId}/{orgId}")
  public ResponseEntity<BaseResponse<List<NodeCalendarResponse>>> getNodeCalendars(
      @NotBlank(message = "calendarId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the calendar", example = "CALENDAR2023")
          String calendarId,
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization", example = "NEXTUPLE")
          String orgId)
      throws CalendarDomainException {
    logger.debug("Processing get Node Calendars by orgId and calendarId");

    var response = nodeCalendarService.getNodeAssociationWithCalendar(calendarId, orgId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Calendar List fetched successfully")
            .payload(response)
            .build());
  }
}
