/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.transit.controller.docs.CreateTransitBufferV2Doc;
import com.nextuple.transit.controller.docs.DeleteTransitBufferByOrgIdAndIdDoc;
import com.nextuple.transit.controller.docs.DeleteTransitBufferByOrgIdCarrierServiceIdSourceAndDestinationGeozoneStartAndEndDateDoc;
import com.nextuple.transit.controller.docs.GetTransitBufferByOrgIdAndIdDoc;
import com.nextuple.transit.controller.docs.GetTransitBuffersByOrgIdDestinationGeozoneRequestDateAndHorizonDaysDoc;
import com.nextuple.transit.controller.docs.UpdateTransitBufferByOrgIdAndIdDoc;
import com.nextuple.transit.controller.docs.UpdateTransitBufferV2Doc;
import com.nextuple.transit.domain.inbound.TransitBufferDeletionRequest;
import com.nextuple.transit.domain.inbound.TransitBufferRequest;
import com.nextuple.transit.domain.inbound.TransitBufferV2UpdationRequest;
import com.nextuple.transit.domain.outbound.TransitBufferDetailsResponse;
import com.nextuple.transit.domain.outbound.TransitBufferV2Response;
import com.nextuple.transit.persistence.exception.TransitDomainException;
import com.nextuple.transit.service.TransitBufferV2Service;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
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
 * Controller for managing Transit Buffer configurations (version 2). This includes creating,
 * updating, retrieving, and deleting transit buffer details.
 *
 * <p>The Transit Buffer Controller V2 provides API endpoints for interacting with transit buffers,
 * including fetching transit buffer details by organization ID, destination geo zone, request date,
 * and horizon days; creating and updating transit buffer configurations; and deleting existing
 * transit buffers.
 *
 * <p>The controller is tagged with "Transit Buffer V2 APIs" for easy categorization in API
 * documentation.
 */
@Validated
@RestController
@RequestMapping("/v2/transit/buffer")
@RequiredArgsConstructor
@Tag(name = "Transit Buffer V2 APIs")
public class TransitBufferV2Controller {
  private final TransitBufferV2Service transitBufferV2Service;

  private static final Logger logger = LoggerFactory.getLogger(TransitBufferV2Controller.class);

  /**
   * Retrieves transit buffer details by organization ID, destination geo zone, request date, and
   * horizon days.
   *
   * <p>This method processes a GET request to retrieve a list of transit buffers based on the
   * organization ID, destination geo zone, request date, and horizon days. It returns the list of
   * transit buffer details within the specified date range and horizon.
   *
   * @param orgId The unique identifier of the organization. Example: "NEXTUPLE".
   * @param destinationGeozone The destination geo zone of the transit. Example: "H1R".
   * @param requestDate The request date for fetching the transit buffers. Example: "2024-01-01".
   * @param horizonDays The number of days after the request date to fetch the transit buffers.
   *     Example: "7".
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a list of {@link
   *     TransitBufferDetailsResponse} objects and a success message.
   * @throws CommonServiceException If an error occurs while retrieving the transit buffers.
   */
  @GetTransitBuffersByOrgIdDestinationGeozoneRequestDateAndHorizonDaysDoc
  @GetMapping(path = "/{orgId}/{destinationGeozone}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<List<TransitBufferDetailsResponse>>>
      getTransitBuffersByOrgIdDestinationGeozoneRequestDateAndHorizonDays(
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
              @NotBlank(message = "orgId can't be empty")
              @PathVariable
              String orgId,
          @Parameter(description = "Destination geo zone of the transit.", example = "H1R")
              @NotBlank(message = "destinationGeozone can't be blank")
              @PathVariable
              String destinationGeozone,
          @Parameter(
                  description = "Request date for getting the transit buffers",
                  example = "2024-01-01")
              @NotNull(message = "Request date cannot be empty")
              @DateTimeFormat(pattern = "yyyy-MM-dd")
              @RequestParam
              LocalDate requestDate,
          @Parameter(description = "Horizon days to get buffers after request date", example = "7")
              @Min(value = 0, message = "horizonDays can't be negative")
              @NotNull(message = "Horizon days cannot be empty")
              @RequestParam
              Integer horizonDays)
          throws CommonServiceException {
    logger.debug("Processing get transit buffers by orgId and destination geozone");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit buffers fetched successfully")
            .payload(
                transitBufferV2Service
                    .getTransitBuffersByOrgIdDestinationGeozoneRequestDateAndHorizonDays(
                        orgId, destinationGeozone, requestDate, horizonDays))
            .build());
  }

  /**
   * Creates a new transit buffer.
   *
   * <p>This method processes a POST request to create a new transit buffer based on the provided
   * {@link TransitBufferRequest} payload. It calls the service layer to save the transit buffer and
   * returns a response containing the result of the creation process.
   *
   * @param transitBufferRequest The request payload containing the details needed for creating the
   *     transit buffer.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the payload of the
   *     created transit buffer and a success message.
   * @throws CommonServiceException If a general service error occurs while creating the transit
   *     buffer.
   * @throws TransitDomainException If a domain-specific error occurs during the creation process.
   */
  @CreateTransitBufferV2Doc
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TransitBufferV2Response>> createTransitBuffer(
      @Valid @RequestBody TransitBufferRequest transitBufferRequest)
      throws CommonServiceException, TransitDomainException {
    logger.debug("Processing transit buffer creation request");
    var response = transitBufferV2Service.saveTransitBuffer(transitBufferRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit buffer created successfully")
            .payload(response)
            .build());
  }

  /**
   * Retrieves a transit buffer by organization ID and transit buffer ID.
   *
   * <p>This method processes a GET request to fetch a transit buffer's details based on the
   * provided organization ID and the transit buffer's unique identifier. It returns the details of
   * the specified transit buffer.
   *
   * @param orgId The unique identifier of the organization. Example: "NEXTUPLE".
   * @param id The unique identifier of the transit buffer. Example: "3868429".
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the {@link
   *     TransitBufferV2Response} and a success message.
   * @throws CommonServiceException If an error occurs while retrieving the transit buffer details.
   */
  @GetTransitBufferByOrgIdAndIdDoc
  @GetMapping(path = "/{orgId}/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TransitBufferV2Response>> getByOrgIdAndId(
      @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          @NotBlank(message = "orgId can't be empty")
          @PathVariable
          String orgId,
      @Parameter(description = "Unique identifier of the transit buffer.", example = "3868429")
          @NotNull(message = "id can't be blank")
          @PathVariable
          Long id)
      throws CommonServiceException {
    logger.debug("Processing get all transit buffers by id");
    var response = transitBufferV2Service.getTransitBufferByOrgIdAndId(orgId, id);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit buffer details fetched successfully")
            .payload(response)
            .build());
  }

  /**
   * Updates an existing transit buffer.
   *
   * <p>This method processes a PUT request to update an existing transit buffer based on the
   * provided {@link TransitBufferRequest} payload. It calls the service layer to update the transit
   * buffer and returns a response containing the result of the update process.
   *
   * @param transitBufferRequest The request payload containing the details for updating the transit
   *     buffer.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the payload of the
   *     updated transit buffer and a success message.
   * @throws CommonServiceException If a general service error occurs while updating the transit
   *     buffer.
   * @throws TransitDomainException If a domain-specific error occurs during the update process.
   */
  @UpdateTransitBufferV2Doc
  @PutMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TransitBufferV2Response>> updateTransitBuffer(
      @Valid @RequestBody TransitBufferRequest transitBufferRequest)
      throws CommonServiceException, TransitDomainException {
    logger.debug("Processing transit buffer updation request");
    var response = transitBufferV2Service.updateTransitBuffer(transitBufferRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit buffer updated successfully")
            .payload(response)
            .build());
  }

  /**
   * Deletes an existing transit buffer.
   *
   * <p>This method processes a DELETE request to remove an existing transit buffer based on the
   * provided {@link TransitBufferRequest} payload. It calls the service layer to delete the transit
   * buffer and returns a response containing the result of the deletion process.
   *
   * @param transitBufferRequest The request payload containing the details for deleting the transit
   *     buffer.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the payload of the
   *     deleted transit buffer and a success message.
   * @throws CommonServiceException If a general service error occurs while deleting the transit
   *     buffer.
   */
  @DeleteMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TransitBufferV2Response>> deleteTransitBuffer(
      @Valid @RequestBody TransitBufferRequest transitBufferRequest) throws CommonServiceException {
    logger.debug("Processing transit buffer deletion request");
    var response = transitBufferV2Service.deleteTransitBuffer(transitBufferRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit buffer deleted successfully")
            .payload(response)
            .build());
  }

  /**
   * Updates a transit buffer by organization ID and transit buffer ID.
   *
   * <p>This method processes a PUT request to update an existing transit buffer based on the
   * provided organization ID, transit buffer ID, and {@link TransitBufferV2UpdationRequest}
   * payload. It calls the service layer to update the transit buffer and returns a response
   * containing the result of the update process.
   *
   * @param orgId The unique identifier of the organization. Example: "NEXTUPLE".
   * @param id The unique identifier of the transit buffer. Example: "3868429".
   * @param transitBufferV2UpdationRequest The request payload containing the details for updating
   *     the transit buffer.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the payload of the
   *     updated transit buffer and a success message.
   * @throws CommonServiceException If a general service error occurs while updating the transit
   *     buffer.
   * @throws TransitDomainException If a domain-specific error occurs during the update process.
   */
  @UpdateTransitBufferByOrgIdAndIdDoc
  @PutMapping(path = "/{orgId}/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TransitBufferV2Response>> updateByOrgIdAndId(
      @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          @NotBlank(message = "orgId can't be empty")
          @PathVariable
          String orgId,
      @Parameter(description = "Unique identifier of the transit buffer.", example = "3868429")
          @NotNull(message = "id can't be blank")
          @PathVariable
          Long id,
      @RequestBody TransitBufferV2UpdationRequest transitBufferV2UpdationRequest)
      throws CommonServiceException, TransitDomainException {
    logger.debug("Processing update transit buffers by id");
    var response =
        transitBufferV2Service.updateTransitBufferByOrgIdAndId(
            orgId, id, transitBufferV2UpdationRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit buffer updated successfully")
            .payload(response)
            .build());
  }

  /**
   * Deletes a transit buffer by organization ID and transit buffer ID.
   *
   * <p>This method processes a DELETE request to remove an existing transit buffer based on the
   * provided organization ID and transit buffer ID. It calls the service layer to delete the
   * transit buffer and returns a response containing the result of the deletion process.
   *
   * @param orgId The unique identifier of the organization. Example: "NEXTUPLE".
   * @param id The unique identifier of the transit buffer. Example: "3868429".
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the payload of the
   *     deleted transit buffer and a success message.
   * @throws CommonServiceException If a general service error occurs while deleting the transit
   *     buffer.
   */
  @DeleteTransitBufferByOrgIdAndIdDoc
  @DeleteMapping(path = "/{orgId}/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TransitBufferV2Response>> deleteTransitBufferById(
      @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          @NotBlank(message = "orgId can't be empty")
          @PathVariable
          String orgId,
      @Parameter(description = "Unique identifier of the transit buffer.", example = "3868429")
          @NotNull(message = "id can't be blank")
          @PathVariable
          Long id)
      throws CommonServiceException {
    logger.debug("Processing transit buffer deletion request");
    var response = transitBufferV2Service.deleteTransitBufferById(orgId, id);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit buffer deleted successfully")
            .payload(response)
            .build());
  }

  /**
   * Deletes a transit buffer record.
   *
   * <p>This method processes a DELETE request to remove a transit buffer record based on the
   * provided {@link TransitBufferDeletionRequest} payload.
   *
   * @param transitBufferDeletionRequest The request payload containing the details for deleting the
   *     transit buffer record.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the payload of the
   *     deleted transit buffer record and a success message.
   * @throws CommonServiceException If a general service error occurs while deleting the transit
   *     buffer record.
   */
  @DeleteTransitBufferByOrgIdCarrierServiceIdSourceAndDestinationGeozoneStartAndEndDateDoc
  @DeleteMapping(path = "/one", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TransitBufferV2Response>> deleteTransitBufferRecord(
      @Valid @RequestBody TransitBufferDeletionRequest transitBufferDeletionRequest)
      throws CommonServiceException {
    logger.debug("Processing transit buffer record deletion request");
    var response = transitBufferV2Service.deleteTransitBufferRecord(transitBufferDeletionRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit buffer record deleted successfully")
            .payload(response)
            .build());
  }
}
