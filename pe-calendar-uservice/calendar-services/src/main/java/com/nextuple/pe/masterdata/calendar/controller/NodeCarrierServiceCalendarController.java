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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling Node Carrier Service Calendar related APIs.
 *
 * <p>This class contains endpoints for creating and retrieving Node Carrier Service Calendar
 * details, including cache keys and service calendar information based on organization and node
 * identifiers.
 *
 * <p>The controller is tagged with "Node Carrier Service Calendar APIs" for easy categorization in
 * API documentation.
 */
@RestController
@Tag(name = "Node Carrier Service Calendar APIs")
@RequestMapping("/node-carrier-service-calendar")
@RequiredArgsConstructor
public class NodeCarrierServiceCalendarController {

  private static final Logger logger =
      LoggerFactory.getLogger(NodeCarrierServiceCalendarController.class);
  private final NodeCarrierServiceCalendarService nodeCarrierServiceCalendarService;

  /**
   * Creates a new Node Carrier Service Calendar.
   *
   * <p>This method processes a POST request to create a new Node Carrier Service Calendar using the
   * provided request data. It passes the request to the service layer for processing and returns a
   * response containing the created Node Carrier Service Calendar details.
   *
   * @param nodeCarrierServiceCalendarRequest The request body containing the details of the Node
   *     Carrier Service Calendar to be created. Must be a valid object.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the response data of
   *     type {@link NodeCarrierServiceCalendarResponse}, indicating successful creation.
   * @throws CalendarDomainException If there is an error related to the calendar domain.
   * @throws CommonServiceException If there is a common service-related exception during
   *     processing.
   * @throws DateException If there is an invalid date format or date-related error during
   *     processing.
   */
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

  /**
   * Fetches Node Carrier Service Calendar details.
   *
   * <p>This method processes a GET request to fetch Node Carrier Service Calendar details for the
   * specified organization, node, and carrier service. It accepts an optional service option
   * parameter and returns the corresponding calendar details.
   *
   * @param orgId The unique identifier of the organization. Cannot be null or empty.
   * @param nodeId The unique identifier of the node. Cannot be null or empty.
   * @param carrierServiceId The unique identifier of the carrier service. Cannot be null or empty.
   * @param serviceOption An optional parameter specifying the service option of the carrier. Can be
   *     null.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a list of {@link
   *     NodeCarrierServiceCalendarResponse} representing the fetched calendar details.
   * @throws CalendarDomainException If there is an error related to the calendar domain.
   */
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

  /**
   * Retrieves the list of Node Carrier Service Calendar cache keys.
   *
   * <p>This method processes a GET request to retrieve the list of all node carrier service
   * calendar cache keys. A limit can be specified to control the number of cache keys retrieved.
   *
   * @param limit The maximum number of cache keys to retrieve. This parameter is optional and
   *     defaults to a reasonable limit if not provided.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a list of {@link
   *     NodeCarrierCalendarCacheKeyDto} representing the fetched cache keys.
   * @throws CalendarDomainException If there is an error related to the calendar domain.
   */
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
}
