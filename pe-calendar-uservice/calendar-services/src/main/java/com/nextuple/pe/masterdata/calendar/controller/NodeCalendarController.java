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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing Node Calendar configurations and related operations.
 *
 * <p>This class provides endpoints for creating, retrieving, and managing node calendars, including
 * fetching calendar cache keys and retrieving calendar associations with nodes. It supports
 * operations such as creating a node calendar, getting details by organization and node IDs, and
 * handling pagination for calendar cache keys.
 *
 * <p>The controller is tagged with "Node Calendar APIs" for easy categorization in API
 * documentation.
 */
@Validated
@RestController
@Tag(name = "Node Calendar APIs")
@RequestMapping("/node-calendar")
@RequiredArgsConstructor
public class NodeCalendarController {

  private static final Logger logger = LoggerFactory.getLogger(NodeCalendarController.class);
  private final NodeCalendarService nodeCalendarService;

  /**
   * Creates a new node calendar based on the provided request.
   *
   * <p>This method processes a POST request to create a new node calendar. It takes the {@link
   * NodeCalendarRequest} object, validates it, and passes it to the service for processing. If the
   * operation is successful, it returns a {@link NodeCalendarResponse} containing details of the
   * created node calendar.
   *
   * @param nodeCalendarRequest The request object containing the details for creating a node
   *     calendar. It is validated before being processed.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a {@link
   *     NodeCalendarResponse} representing the details of the created node calendar.
   * @throws CalendarDomainException If there is an error related to the calendar domain.
   * @throws CommonServiceException If there is a general service exception.
   * @throws DateException If there is an issue related to date validation.
   */
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

  /**
   * Retrieves the node calendar details for the given organization and node ID.
   *
   * <p>This method processes a GET request to fetch the node calendar details for a specific
   * organization and node identifier. The method interacts with the service layer to retrieve the
   * necessary details and returns them in a {@link NodeCalendarResponse} format.
   *
   * @param orgId The unique identifier of the organization. It must not be blank.
   * @param nodeId The unique identifier of the node. It must not be blank.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a list of {@link
   *     NodeCalendarResponse} representing the node calendar details.
   * @throws CalendarDomainException If there is an error related to the calendar domain.
   */
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

  /**
   * Retrieves the Node Calendar Cache Keys.
   *
   * <p>This method processes a GET request to fetch the cache keys related to Node Calendars. It
   * retrieves the cache keys from the service layer and returns them in a list of {@link
   * NodeCalendarCacheKeyDto} objects. The number of cache keys to retrieve is determined by the
   * provided limit.
   *
   * @param limit The number of cache keys to fetch. If not provided, a default value will be used.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a list of {@link
   *     NodeCalendarCacheKeyDto} representing the Node Calendar Cache Keys.
   * @throws CalendarDomainException If there is an error related to the calendar domain.
   */
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

  /**
   * Retrieves Node Calendars associated with a specific calendar and organization.
   *
   * <p>This method processes a GET request to fetch the list of Node Calendars associated with a
   * specific calendar and organization. It retrieves the node calendars from the service layer
   * based on the provided calendar ID and organization ID.
   *
   * @param calendarId The unique identifier of the calendar. Must not be blank.
   * @param orgId The unique identifier of the organization. Must not be blank.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a list of {@link
   *     NodeCalendarResponse} representing the associated Node Calendars.
   * @throws CalendarDomainException If there is an error related to the calendar domain.
   */
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
