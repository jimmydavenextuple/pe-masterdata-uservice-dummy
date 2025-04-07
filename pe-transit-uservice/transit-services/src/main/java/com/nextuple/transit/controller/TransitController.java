/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.transit.controller.docs.AddTransitDataDoc;
import com.nextuple.transit.controller.docs.DeleteTransitionDetailsDoc;
import com.nextuple.transit.controller.docs.GetDistinctDestinationGeoZonesDoc;
import com.nextuple.transit.controller.docs.GetDistinctSourceAndDestinationGeozonesDoc;
import com.nextuple.transit.controller.docs.GetTransitDetailsDoc;
import com.nextuple.transit.controller.docs.GetTransitDetailsListDoc;
import com.nextuple.transit.controller.docs.GetTransitDetailsListForDestinationGeoZoneDoc;
import com.nextuple.transit.controller.docs.GetTransitTimeDetailsForDestinationGeoZonesListDoc;
import com.nextuple.transit.controller.docs.GetTransitTimeEntriesDoc;
import com.nextuple.transit.controller.docs.UpdateTransitBufferDayDoc;
import com.nextuple.transit.controller.docs.UpdateTransitBufferDetailsDoc;
import com.nextuple.transit.controller.docs.UpdateTransitDataDoc;
import com.nextuple.transit.domain.dto.TransitTimeEntriesDto;
import com.nextuple.transit.domain.inbound.DistinctGeozonesResponse;
import com.nextuple.transit.domain.inbound.TransitBufferCreationRequest;
import com.nextuple.transit.domain.inbound.TransitDataCreationRequest;
import com.nextuple.transit.domain.inbound.TransitDataUpdationRequest;
import com.nextuple.transit.domain.inbound.TransitDetailsRequest;
import com.nextuple.transit.domain.outbound.TransitResponse;
import com.nextuple.transit.persistence.exception.TransitDomainException;
import com.nextuple.transit.service.TransitService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing transit data and buffer configurations.
 *
 * <p>This controller provides a set of APIs for adding, updating, fetching, and deleting transit
 * details, as well as managing transit buffer configurations. It facilitates interaction with the
 * transit service layer, handling operations based on provided transit and buffer requests,
 * including CRUD (Create, Read, Update, Delete) functionalities.
 *
 * <p>The controller is tagged with "Transit APIs" for easy categorization in API documentation.
 *
 * <p>All API methods are designed to process various transit-related tasks, such as adding new
 * data, updating existing records, fetching transit details, and deleting outdated information,
 * with appropriate error handling for domain-specific and general service exceptions.
 */
@Validated
@RestController
@RequestMapping("/transit")
@RequiredArgsConstructor
@Tag(name = "Transit APIs")
public class TransitController {

  private static final Logger logger = LoggerFactory.getLogger(TransitController.class);
  public static final String FETCH_TRANSIT_DETAILS_LIST_ERROR_MESSAGE =
      "Failed to fetch transit details list";
  private final TransitService transitService;

  /**
   * Adds new transit data.
   *
   * <p>This method processes a POST request to add new transit data based on the provided {@link
   * TransitDataCreationRequest} payload. It calls the service layer to add the transit data and
   * returns a response containing the result of the addition process.
   *
   * @param transitDataCreationRequest The request payload containing the details for adding the
   *     transit data.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the payload of the
   *     added transit data and a success message.
   * @throws CommonServiceException If a general service error occurs while adding the transit data.
   * @throws TransitDomainException If a domain-specific error occurs during the addition process.
   */
  @AddTransitDataDoc
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TransitResponse>> addTransitData(
      @Valid @RequestBody TransitDataCreationRequest transitDataCreationRequest)
      throws CommonServiceException, TransitDomainException {
    logger.debug("Processing transit data creation request");
    try {
      var transitResponse = transitService.addTransitInfo(transitDataCreationRequest);
      logger.info("Response after addition of transit data :{}", transitResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Transit data successfully added")
              .payload(transitResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to add transit data");
      throw e;
    }
  }

  /**
   * Updates the details of a transit buffer.
   *
   * <p>This method processes a PUT request to update the details of an existing transit buffer
   * based on the provided {@link TransitBufferCreationRequest} payload. It calls the service layer
   * to update the transit buffer and returns a response containing the result of the update
   * process.
   *
   * @param transitBufferCreationRequest The request payload containing the details for updating the
   *     transit buffer.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the payload of the
   *     updated transit data and a success message.
   * @throws TransitDomainException If a domain-specific error occurs during the update process.
   * @throws CommonServiceException If a general service error occurs while updating the transit
   *     buffer details.
   */
  @UpdateTransitBufferDetailsDoc
  @PutMapping(
      path = "/buffer",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TransitResponse>> updateTransitBufferDetails(
      @Valid @RequestBody TransitBufferCreationRequest transitBufferCreationRequest)
      throws TransitDomainException, CommonServiceException {
    logger.debug("Processing update transit buffer data");
    try {
      var transitResponse = transitService.updateTransitBufferDetails(transitBufferCreationRequest);
      logger.info("Response after updation of transit buffer details :{}", transitResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Transit details updated successfully")
              .payload(transitResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to update transit buffer details");
      throw e;
    }
  }

  /**
   * Updates the transit data based on various parameters.
   *
   * <p>This method processes a PUT request to update the transit data based on the provided
   * organization ID, source geo zone, destination geo zone, carrier service ID, and the {@link
   * TransitDataUpdationRequest} payload. It calls the service layer to update the transit data and
   * returns a response containing the result of the update process.
   *
   * @param orgId The unique identifier of the organization. Example: "NEXTUPLE".
   * @param sourceGeozone The source geo zone to update the transit details. Example: "A1B".
   * @param destinationGeozone The destination geo zone to update the transit details. Example:
   *     "H1R".
   * @param carrierServiceId The unique identifier of the carrier service. Example: "UPS-GROUND".
   * @param transitDataUpdationRequest The request payload containing the details for updating the
   *     transit data.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the payload of the
   *     updated transit data and a success message.
   * @throws TransitDomainException If a domain-specific error occurs during the update process.
   * @throws CommonServiceException If a general service error occurs while updating the transit
   *     data.
   */
  @UpdateTransitDataDoc
  @PutMapping(
      path = "/{orgId}/{sourceGeozone}/{destinationGeozone}/{carrierServiceId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TransitResponse>> updateTransitData(
      @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          @NotBlank(message = "orgId can't be empty")
          @PathVariable
          String orgId,
      @Parameter(description = "Source geo zone to update the transit details.", example = "A1B")
          @NotBlank(message = "sourceGeozone can't be empty")
          @PathVariable
          String sourceGeozone,
      @Parameter(
              description = "Destination geo zone to update the transit details.",
              example = "H1R")
          @NotBlank(message = "destinationGeozone can't be empty")
          @PathVariable
          String destinationGeozone,
      @Parameter(description = "Unique identifier of the carrier service.", example = "UPS-GROUND")
          @NotBlank(message = "carrierServiceId can't be empty")
          @PathVariable
          String carrierServiceId,
      @Valid @RequestBody TransitDataUpdationRequest transitDataUpdationRequest)
      throws TransitDomainException, CommonServiceException {
    logger.debug("Processing update transit data");
    try {

      var transitResponse =
          transitService.updateTransitDetails(
              orgId,
              sourceGeozone,
              destinationGeozone,
              carrierServiceId,
              transitDataUpdationRequest);
      logger.info("Response after updation of transit data :{}", transitResponse);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Transit details updated successfully")
              .payload(transitResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to update transit details");
      throw e;
    }
  }

  /**
   * Retrieves the transit details based on various parameters.
   *
   * <p>This method processes a GET request to fetch transit details based on the provided
   * organization ID, source geo zone, destination geo zone, carrier service ID, and service option.
   * It calls the service layer to get the transit details and returns a response containing the
   * result.
   *
   * @param orgId The unique identifier of the organization. Example: "NEXTUPLE".
   * @param sourceGeozone The source geo zone to fetch the transit details. Example: "A1B".
   * @param destinationGeozone The destination geo zone to fetch the transit details. Example:
   *     "H1R".
   * @param carrierServiceId The unique identifier of the carrier service. Example: "UPS-GROUND".
   * @param serviceOption The service option to fetch the transit details. Example: "EXPRESS".
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the payload of the
   *     transit details and a success message.
   * @throws TransitDomainException If a domain-specific error occurs while fetching the transit
   *     details.
   * @throws CommonServiceException If a general service error occurs while fetching the transit
   *     details.
   */
  @GetTransitDetailsDoc
  @GetMapping(
      path = "/{orgId}/{sourceGeozone}/{destinationGeozone}/{carrierServiceId}/{serviceOption}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TransitResponse>> getTransitDetails(
      @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          @NotBlank(message = "orgId can't be empty")
          @PathVariable
          String orgId,
      @Parameter(description = "Source geo zone to get the transit details.", example = "A1B")
          @NotBlank(message = "sourceGeozone can't be empty")
          @PathVariable
          String sourceGeozone,
      @Parameter(description = "Destination geo zone to get the transit details.", example = "H1R")
          @NotBlank(message = "destinationGeozone can't be empty")
          @PathVariable
          String destinationGeozone,
      @Parameter(description = "Unique identifier of the carrier service.", example = "UPS-GROUND")
          @NotBlank(message = "carrierServiceId can't be empty")
          @PathVariable
          String carrierServiceId,
      @Parameter(description = "Service option to get the transit details.", example = "EXPRESS")
          @NotBlank
          @PathVariable
          String serviceOption)
      throws TransitDomainException, CommonServiceException {
    logger.debug("Processing get transit details");
    try {

      var transitResponse =
          transitService.getTransitDetails(
              orgId, sourceGeozone, destinationGeozone, carrierServiceId, serviceOption);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Transit details fetched successfully")
              .payload(transitResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to fetch transit details");
      throw e;
    }
  }

  /**
   * Deletes the transit details based on various parameters.
   *
   * <p>This method processes a DELETE request to delete the transit details based on the provided
   * organization ID, source geo zone, destination geo zone, and carrier service ID. It calls the
   * service layer to delete the transit details and returns a response confirming the deletion.
   *
   * @param orgId The unique identifier of the organization. Example: "NEXTUPLE".
   * @param sourceGeozone The source geo zone to delete the transit details. Example: "A1B".
   * @param destinationGeozone The destination geo zone to delete the transit details. Example:
   *     "H1R".
   * @param carrierServiceId The unique identifier of the carrier service. Example: "UPS-GROUND".
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a success message and
   *     the result of the deletion.
   * @throws TransitDomainException If a domain-specific error occurs while deleting the transit
   *     details.
   * @throws CommonServiceException If a general service error occurs while deleting the transit
   *     details.
   */
  @DeleteTransitionDetailsDoc
  @DeleteMapping(
      path = "/{orgId}/{sourceGeozone}/{destinationGeozone}/{carrierServiceId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TransitResponse>> deleteTransitDetails(
      @Parameter(description = "Unique identifier of the organization", example = "NEXTUPLE")
          @NotBlank(message = "orgId can't be empty")
          @PathVariable
          String orgId,
      @Parameter(
              description = "Source geo zone details to delete the transit details.",
              example = "A1B")
          @NotBlank(message = "sourceGeozone can't be empty")
          @PathVariable
          String sourceGeozone,
      @Parameter(
              description = "Destination geo zone details to delete the transit details.",
              example = "H1R")
          @NotBlank(message = "destinationGeozone can't be empty")
          @PathVariable
          String destinationGeozone,
      @Parameter(description = "Unique identifier of the carrier service.", example = "UPS-GROUND")
          @NotBlank(message = "carrierServiceId can't be empty")
          @PathVariable
          String carrierServiceId)
      throws TransitDomainException, CommonServiceException {
    logger.debug("Processing delete transit details");
    try {

      var transitResponse =
          transitService.deleteTransitDetails(
              orgId, sourceGeozone, destinationGeozone, carrierServiceId);
      logger.info("Response after deletion of transit data :{}", transitResponse);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Transit details deleted successfully")
              .payload(transitResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to delete transit details");
      throw e;
    }
  }

  /**
   * Retrieves a list of transit details based on organization ID, destination geo zone, and source
   * geo zones.
   *
   * <p>This method processes a GET request to fetch a list of transit details for the specified
   * organization, destination geo zone, and a list of source geo zones. The service layer is called
   * to get the data, and the response is returned with the list of transit details.
   *
   * @param orgId The unique identifier of the organization. Example: "NEXTUPLE".
   * @param destinationGeozone The destination geo zone to get the transit details. Example: "H1R".
   * @param sourceGeozones A list of source geo zones to get the transit details. Example: ["A1B",
   *     "A2B"].
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a success message and
   *     the list of transit details.
   * @throws TransitDomainException If a domain-specific error occurs while fetching the transit
   *     details.
   */
  @GetTransitDetailsListDoc
  @GetMapping(path = "/{orgId}/{destinationGeozone}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<List<TransitResponse>>> getTransitDetailsList(
      @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          @NotBlank(message = "orgId can't be empty")
          @PathVariable
          String orgId,
      @Parameter(description = "Destination geo zone to get the transit details.", example = "H1R")
          @NotBlank(message = "destinationGeozone can't be empty")
          @PathVariable
          String destinationGeozone,
      @Parameter(
              description = "List of source geo zone to get the transit details.",
              example = "[A1B, A2B]")
          @NotEmpty
          @RequestParam
          List<String> sourceGeozones)
      throws TransitDomainException {
    logger.debug("Processing get transit details list");
    try {

      var transitResponse =
          transitService.getListOfTransitDetails(orgId, destinationGeozone, sourceGeozones);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("List of transit details fetched successfully")
              .payload(transitResponse)
              .build());
    } catch (Exception e) {
      logger.error(FETCH_TRANSIT_DETAILS_LIST_ERROR_MESSAGE);
      throw e;
    }
  }

  /**
   * Retrieves a distinct list of destination geo zones based on the organization ID, source geo
   * zone, and carrier service IDs.
   *
   * <p>This method processes a POST request to fetch a distinct list of destination geo zones for
   * the specified organization, source geo zone, and list of carrier service IDs. The service layer
   * is called to get the data, and the response is returned with the list of distinct destination
   * geo zones.
   *
   * @param orgId The unique identifier of the organization. Example: "NEXTUPLE".
   * @param sourceGeozone The source geo zone to get the transit details. Example: "A1B".
   * @param carrierServiceIds A list of carrier service IDs to filter the destination geo zones.
   *     Example: ["UPS-Ground", "Canpar Express"].
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a success message and
   *     the list of distinct destination geo zones.
   * @throws TransitDomainException If a domain-specific error occurs while fetching the destination
   *     geo zones.
   */
  @GetDistinctDestinationGeoZonesDoc
  @PostMapping(
      path = "/distinct/dFSA/{orgId}/{sourceGeozone}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<List<String>>> getDistinctDestinationGeoZones(
      @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          @NotBlank(message = "orgId can't be blank")
          @PathVariable
          String orgId,
      @Parameter(description = "Source geo zone to get the transit details.", example = "A1B")
          @NotBlank(message = "sourceGeozone can't be empty")
          @PathVariable
          String sourceGeozone,
      @Parameter(
              description = "List of carrier service IDs to get the transit details.",
              example = "[UPS-Ground, Canpar Express]")
          @NotEmpty(message = "carrier service id list can't be empty")
          @RequestBody
          List<String> carrierServiceIds)
      throws TransitDomainException {
    logger.debug("Processing distinct destination geozone request");
    try {
      var transitResponse = transitService.getDistinctDFSA(orgId, sourceGeozone, carrierServiceIds);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Fetched distinct list of destination geozones")
              .payload(transitResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to fetch distinct destination geozones");
      throw e;
    }
  }

  /**
   * Retrieves the transit time entries for the specified organization and carrier service.
   *
   * <p>This method processes a GET request to fetch the transit time entries for the given
   * organization and carrier service ID. The service layer is called to get the data, and the
   * response is returned with the transit time entries.
   *
   * @param orgId The unique identifier of the organization. Example: "NEXTUPLE".
   * @param carrierServiceId The unique identifier of the carrier service. Example: "UPS-GROUND".
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a success message and
   *     the transit time entries.
   * @throws TransitDomainException If a domain-specific error occurs while fetching the transit
   *     time entries.
   */
  @GetTransitTimeEntriesDoc
  @GetMapping(
      path = "/transit-entries/{orgId}/{carrierServiceId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TransitTimeEntriesDto>> getTransitTimeEntries(
      @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          @NotBlank(message = "orgId can't be empty")
          @PathVariable
          String orgId,
      @Parameter(description = "Unique identifier of the carrier service.", example = "UPS-GROUND")
          @NotBlank(message = "carrierServiceId can't be empty")
          @PathVariable
          String carrierServiceId)
      throws TransitDomainException {
    logger.debug("Processing get transit time entries");
    var transitTimeEntriesDto = transitService.getTransitTimeEntries(orgId, carrierServiceId);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit time entries fetched successfully")
            .payload(transitTimeEntriesDto)
            .build());
  }

  /**
   * Retrieves the list of transit details for the specified organization and destination geo zone.
   *
   * <p>This method processes a GET request to fetch the list of transit details for a given
   * organization and destination geo zone. The service layer is called to retrieve the data, and
   * the response is returned containing the transit details.
   *
   * @param orgId The unique identifier of the organization. Example: "NEXTUPLE".
   * @param destinationGeozone The destination geo zone to get the transit details for. Example:
   *     "H1R".
   * @return A {@link BaseResponse} containing the transit details for the specified geo zone.
   * @throws CommonServiceException If an error occurs while fetching the transit details.
   */
  @GetTransitDetailsListForDestinationGeoZoneDoc
  @GetMapping(
      path = "/batch-transit/{orgId}/{destinationGeozone}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public BaseResponse<List<TransitResponse>> getTransitDetailsListForDestinationGeoZone(
      @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          @NotBlank(message = "orgId can't be empty")
          @PathVariable
          String orgId,
      @Parameter(description = "Destination geo zone to get the transit details.", example = "H1R")
          @NotBlank(message = "destinationGeozone can't be empty")
          @PathVariable
          String destinationGeozone)
      throws CommonServiceException {
    logger.debug("Processing get transit details list");
    try {
      return BaseResponse.builder()
          .payload(
              transitService.getListOfTransitDetailsForDestinationGeoZone(
                  orgId, destinationGeozone))
          .build();
    } catch (Exception e) {
      logger.error(FETCH_TRANSIT_DETAILS_LIST_ERROR_MESSAGE);
      throw e;
    }
  }

  /**
   * Retrieves the transit time details for the specified organization, carrier service, and list of
   * destination geo zones.
   *
   * <p>This method processes a POST request to fetch the transit time details for multiple
   * destination geo zones, based on the provided organization ID, carrier service ID, and a list of
   * destination geo zones.
   *
   * @param orgId The unique identifier of the organization. Example: "NEXTUPLE".
   * @param carrierServiceId The unique identifier of the carrier service. Example: "UPS-GROUND".
   * @param transitDetailsRequest A request object containing the list of destination geo zones to
   *     fetch transit details for.
   * @return A {@link ResponseEntity} with a {@link BaseResponse} containing the transit time
   *     details for the given geo zones.
   * @throws TransitDomainException If an error occurs while fetching the transit time details.
   */
  @GetTransitTimeDetailsForDestinationGeoZonesListDoc
  @PostMapping(
      path = "/transit-entries/{orgId}/{carrierServiceId}/geozones",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<List<TransitResponse>>>
      getTransitTimeDetailsForDestinationGeoZonesList(
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
              @NotBlank(message = "orgId can't be empty")
              @PathVariable
              String orgId,
          @Parameter(
                  description = "Unique identifier of the carrier service.",
                  example = "UPS-GROUND")
              @NotBlank(message = "carrierServiceId can't be empty")
              @PathVariable
              String carrierServiceId,
          @RequestBody TransitDetailsRequest transitDetailsRequest)
          throws TransitDomainException {
    logger.debug("Processing get transit time entries for geoZones");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit time entries fetched successfully")
            .payload(
                transitService.getTransitDetailsForDestinationGeozones(
                    orgId, carrierServiceId, transitDetailsRequest.getDestinationGeozones()))
            .build());
  }

  /**
   * Updates the transit buffer days for the specified organization, carrier service, and geo zones.
   *
   * <p>This method processes a PUT request to update the transit buffer days for a specific
   * combination of organization ID, carrier service ID, source geo zone, and destination geo zone.
   *
   * @param orgId The unique identifier of the organization. Example: "NEXTUPLE".
   * @param carrierServiceId The unique identifier of the carrier service. Example: "UPS-GROUND".
   * @param sourceGeoZone The source geo zone to update the transit buffer days for. Example: "A1B".
   * @param destinationGeoZone The destination geo zone to update the transit buffer days for.
   *     Example: "H1R".
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a success message and
   *     the updated transit data.
   * @throws TransitDomainException If an error occurs while updating the transit buffer days.
   */
  @UpdateTransitBufferDayDoc
  @PutMapping(
      path = "/{orgId}/{carrierServiceId}/buffer-days",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TransitResponse>> updateTransitBufferDays(
      @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          @PathVariable
          String orgId,
      @Parameter(description = "Unique identifier of the carrier service.", example = "UPS-GROUND")
          @PathVariable
          String carrierServiceId,
      @Parameter(description = "Source geo zone to update the transit details.", example = "A1B")
          @NotBlank(message = "sourceGeoZone can't be blank")
          @RequestParam
          String sourceGeoZone,
      @Parameter(
              description = "Destination geo zone to update the transit details.",
              example = "H1R")
          @NotBlank(message = "destinationGeoZone can't be blank")
          @RequestParam
          String destinationGeoZone)
      throws TransitDomainException {
    logger.debug("Processing delete transit buffer days");
    return ResponseEntity.ok()
        .body(
            BaseResponse.builder()
                .message("Transit buffer days removed successfully")
                .payload(
                    transitService.updateTransitBufferDays(
                        orgId, carrierServiceId, sourceGeoZone, destinationGeoZone))
                .build());
  }

  /**
   * Fetches the distinct source and destination geo zones for the specified organization and
   * carrier service.
   *
   * <p>This method processes a GET request to retrieve a list of distinct source and destination
   * geo zones associated with a given organization and carrier service.
   *
   * @param orgId The unique identifier of the organization. Example: "NEXTUPLE".
   * @param carrierServiceId The unique identifier of the carrier service. Example: "UPS-GROUND".
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a success message and
   *     the list of distinct geo zones.
   * @throws TransitDomainException If an error occurs while fetching the distinct geo zones.
   */
  @GetDistinctSourceAndDestinationGeozonesDoc
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<DistinctGeozonesResponse>>
      getDistinctSourceAndDestinationGeozones(
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
              @RequestParam
              String orgId,
          @Parameter(
                  description = "Unique identifier of the carrier service.",
                  example = "UPS-GROUND")
              @RequestParam
              String carrierServiceId)
          throws TransitDomainException {
    logger.debug("Processing get distinct source and destination lists");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Distinct source and destination lists fetched successfully")
            .payload(
                transitService.getDistinctSourceAndDestinationGeoZones(orgId, carrierServiceId))
            .build());
  }

  /**
   * Fetches the list of transit details for a given organization and destination geo zone.
   *
   * <p>This method processes a GET request to retrieve the transit details for the specified
   * destination geo zone and organization.
   *
   * @param orgId The unique identifier of the organization. Example: "NEXTUPLE".
   * @param destinationGeozone The destination geo zone to fetch transit details for. Example:
   *     "H1R".
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a success message and
   *     the list of transit details.
   * @throws CommonServiceException If a common service exception occurs while processing the
   *     request.
   * @throws TransitDomainException If an error occurs related to the transit domain.
   */
  @GetTransitDetailsListForDestinationGeoZoneDoc
  @GetMapping(
      path = "/v2/{orgId}/{destinationGeozone}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<List<TransitResponse>>>
      getListOfTransitDetailsForDestinationGeoZone(
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
              @NotBlank(message = "orgId can't be empty")
              @PathVariable
              String orgId,
          @Parameter(
                  description = "Destination geo zone to get the transit details.",
                  example = "H1R")
              @NotBlank(message = "destinationGeozone can't be empty")
              @PathVariable
              String destinationGeozone)
          throws CommonServiceException, TransitDomainException {
    logger.debug("Processing get transit details list");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit data fetched successfully")
            .payload(
                transitService.getTransitDetailsForDestinationGeoZone(orgId, destinationGeozone))
            .build());
  }
}
