/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.transit.controller.docs.AddZoneDataDoc;
import com.nextuple.transit.controller.docs.DeleteZoneDetailsDoc;
import com.nextuple.transit.controller.docs.GetZoneDetailsDoc;
import com.nextuple.transit.controller.docs.GetZoneDetailsListForDestinationGeoZoneDoc;
import com.nextuple.transit.controller.docs.UpdateZoneDataDoc;
import com.nextuple.transit.domain.inbound.ZoneRequest;
import com.nextuple.transit.domain.inbound.ZoneUpdateRequest;
import com.nextuple.transit.domain.outbound.ZoneResponse;
import com.nextuple.transit.service.ZoneService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/zone")
@RequiredArgsConstructor
@Tag(name = "Zone APIs")
public class ZoneController {

  private static final Logger logger = LoggerFactory.getLogger(ZoneController.class);

  private final ZoneService zoneService;

  @AddZoneDataDoc
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<ZoneResponse>> addZoneData(
      @Valid @RequestBody ZoneRequest zoneRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing zone creation request");
    var zoneResponse = zoneService.addZoneData(zoneRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Zone data successfully added")
            .payload(zoneResponse)
            .build());
  }

  @UpdateZoneDataDoc
  @PutMapping(
      path = "/{orgId}/{sourceGeozone}/{destinationGeozone}/{carrierServiceId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<ZoneResponse>> updateZone(
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
      @Valid @RequestBody ZoneUpdateRequest zoneUpdateRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing update zone request");
    var zoneResponse =
        zoneService.updateZone(
            orgId, sourceGeozone, destinationGeozone, carrierServiceId, zoneUpdateRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Zone data successfully updated")
            .payload(zoneResponse)
            .build());
  }

  @GetZoneDetailsDoc
  @GetMapping(
      path = "/{orgId}/{sourceGeozone}/{destinationGeozone}/{carrierServiceId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<ZoneResponse>> getZoneDetails(
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
          String carrierServiceId)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing get zone details");

    var zoneResponse =
        zoneService.getZoneDetails(orgId, sourceGeozone, destinationGeozone, carrierServiceId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Zone details fetched successfully")
            .payload(zoneResponse)
            .build());
  }

  @DeleteZoneDetailsDoc
  @DeleteMapping(
      path = "/{orgId}/{sourceGeozone}/{destinationGeozone}/{carrierServiceId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<ZoneResponse>> deleteZoneDetails(
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
          String carrierServiceId)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing delete zone details");

    var zoneResponse =
        zoneService.deleteZoneDetails(orgId, sourceGeozone, destinationGeozone, carrierServiceId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Zone details deleted successfully")
            .payload(zoneResponse)
            .build());
  }

  @GetZoneDetailsListForDestinationGeoZoneDoc
  @GetMapping(path = "/{orgId}/{destinationGeozone}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<List<ZoneResponse>>> getZoneDetailsList(
      @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          @NotBlank(message = "orgId can't be empty")
          @PathVariable
          String orgId,
      @Parameter(description = "Destination geo zone to get the transit details.", example = "H1R")
          @NotBlank(message = "destinationGeozone can't be empty")
          @PathVariable
          String destinationGeozone)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing get zone details list");

    var zoneResponseList = zoneService.getZoneDetailsList(orgId, destinationGeozone);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Zones list fetched successfully")
            .payload(zoneResponseList)
            .build());
  }
}
