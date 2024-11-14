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
