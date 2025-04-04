/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.PreSignedUrlResponse;
import com.nextuple.transit.controller.docs.CreateTransitBufferDoc;
import com.nextuple.transit.controller.docs.DeleteTransitBufferDetailsDoc;
import com.nextuple.transit.controller.docs.GetByOrgIdAndDestinationGeozoneDoc;
import com.nextuple.transit.controller.docs.GetTransitBufferDetailsDoc;
import com.nextuple.transit.controller.docs.UpdateTransitBufferDoc;
import com.nextuple.transit.domain.inbound.TransitBufferRequest;
import com.nextuple.transit.domain.outbound.TransitBufferResponse;
import com.nextuple.transit.persistence.exception.TransitDomainException;
import com.nextuple.transit.service.TransitBufferService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.io.IOException;
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
 * Controller for managing Transit Buffer configurations. This includes creating, updating,
 * retrieving, and deleting transit buffer details.
 *
 * <p>The Transit Buffer Controller provides API endpoints for interacting with transit buffers,
 * including fetching details by organization ID and destination geozone, creating and updating
 * transit buffer configurations, and deleting existing transit buffers.
 *
 * <p>The controller is tagged with "Transit Buffer APIs" for easy categorization in API
 * documentation.
 */
@RestController
@RequestMapping("/transit/v1/buffer")
@RequiredArgsConstructor
@Validated
@Tag(name = "Transit Buffer APIs")
public class TransitBufferController {

  private final TransitBufferService transitBufferService;

  private static final Logger logger = LoggerFactory.getLogger(TransitBufferController.class);

  /**
   * Creates a new transit buffer configuration.
   *
   * <p>This method processes the request to create a new transit buffer based on the provided
   * transit buffer data. The response will indicate whether the transit buffer was successfully
   * created.
   *
   * @param transitBufferRequest The request body containing the details of the transit buffer to be
   *     created. This is a required parameter and must be validated using the {@link Valid}
   *     annotation.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the {@link
   *     TransitBufferResponse} indicating the result of the creation operation and a success
   *     message.
   * @throws CommonServiceException If there is a general error during the service processing.
   * @throws TransitDomainException If an error specific to the transit buffer domain occurs.
   */
  @CreateTransitBufferDoc
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TransitBufferResponse>> createTransitBuffer(
      @Valid @RequestBody TransitBufferRequest transitBufferRequest)
      throws CommonServiceException, TransitDomainException {
    logger.debug("Processing transit buffer creation request");
    var response = transitBufferService.saveTransitBuffer(transitBufferRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit buffer created successfully")
            .payload(response)
            .build());
  }

  /**
   * Fetches the transit buffer details based on the organization ID and destination geo zone.
   *
   * <p>This method retrieves a list of transit buffer details for the given organization and
   * destination geozone. The response will include the transit buffer data for the specified
   * criteria.
   *
   * @param orgId The unique identifier of the organization. It is a required parameter and cannot
   *     be blank.
   * @param destinationGeozone The destination geo zone of the transit. It is a required parameter
   *     and cannot be blank.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a list of {@link
   *     TransitBufferResponse} that matches the organization ID and destination geo zone.
   * @throws CommonServiceException If an error occurs during the retrieval of transit buffer
   *     details.
   */
  @GetByOrgIdAndDestinationGeozoneDoc
  @GetMapping(path = "/org/{orgId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<List<TransitBufferResponse>>> getByOrgIdAndDestinationGeozone(
      @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          @NotBlank(message = "orgId can't be empty")
          @PathVariable
          String orgId,
      @Parameter(description = "Destination geo zone of the transit.", example = "H1R")
          @NotBlank(message = "destinationGeozone can't be blank")
          @RequestParam
          String destinationGeozone)
      throws CommonServiceException {
    logger.debug("Processing get all transit buffers by orgId and destination geozone");
    var response =
        transitBufferService.getTransitBuffersByOrgIdAndDestinationGeozone(
            orgId, destinationGeozone);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit buffer details fetched successfully")
            .payload(response)
            .build());
  }

  /**
   * Updates the existing transit buffer with the provided details.
   *
   * <p>This method processes the request to update a transit buffer based on the details provided
   * in the {@link TransitBufferRequest}. The transit buffer will be updated in the system, and a
   * response with the updated transit buffer details will be returned.
   *
   * @param transitBufferRequest The request body containing the updated details for the transit
   *     buffer.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated {@link
   *     TransitBufferResponse}.
   * @throws CommonServiceException If an error occurs during the processing of the request.
   * @throws TransitDomainException If an error occurs related to transit buffer domain rules.
   */
  @UpdateTransitBufferDoc
  @PutMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TransitBufferResponse>> updateTransitBuffer(
      @Valid @RequestBody TransitBufferRequest transitBufferRequest)
      throws CommonServiceException, TransitDomainException {
    logger.debug("Processing transit buffer creation request");
    var response = transitBufferService.updateTransitBuffer(transitBufferRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit buffer updated successfully")
            .payload(response)
            .build());
  }

  /**
   * Deletes the transit buffer details based on the provided request.
   *
   * <p>This method processes the request to delete a transit buffer based on the details provided
   * in the {@link TransitBufferRequest}. The transit buffer corresponding to the given
   * organization, carrier service, source geo zone, and destination geo zone will be deleted.
   *
   * @param transitBufferRequest The request body containing the details of the transit buffer to be
   *     deleted.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the {@link
   *     TransitBufferResponse} indicating the result of the deletion.
   * @throws CommonServiceException If an error occurs during the processing of the request.
   */
  @DeleteTransitBufferDetailsDoc
  @DeleteMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<TransitBufferResponse>> deleteTransitBufferDetails(
      @Valid @RequestBody TransitBufferRequest transitBufferRequest) throws CommonServiceException {
    logger.debug("Processing delete transit buffer details");

    var response =
        transitBufferService.deleteTransitBufferDetails(
            transitBufferRequest.getOrgId(),
            transitBufferRequest.getCarrierServiceId(),
            transitBufferRequest.getSourceGeozone(),
            transitBufferRequest.getDestinationGeozone());

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit buffer deleted successfully")
            .payload(response)
            .build());
  }

  /**
   * Retrieves the transit buffer details for given transit buffer config request ID and generates a
   * pre-signed URL for file download.
   *
   * <p>This method retrieves the transit buffer details associated with a given {@code
   * transitBufferConfigRequestId} and checks if a pre-signed URL for an already uploaded file is
   * available. If the file is not present, it creates a new CSV file, uploads it to S3, and returns
   * a pre-signed URL for downloading the file.
   *
   * @param transitBufferConfigRequestId The unique identifier of the transit buffer configuration
   *     request.
   * @param createdBy The user who created the request.
   * @return A {@link PreSignedUrlResponse} containing the pre-signed URL for downloading the
   *     transit buffer file.
   * @throws IOException If an error occurs while creating or uploading the CSV file.
   * @throws CommonServiceException If an error occurs during the processing of the request.
   */
  @GetTransitBufferDetailsDoc
  @GetMapping("/{transitBufferConfigRequestId}")
  public ResponseEntity<BaseResponse<PreSignedUrlResponse>> getTransitBufferDetails(
      @Parameter(
              description = "Unique identifier of the transit buffer configuration request.",
              example = "1231231231")
          @PathVariable
          Long transitBufferConfigRequestId,
      @Parameter(description = "User who created the request.", example = "user@email.com")
          @NotBlank(message = "createdBy can't be empty")
          @RequestParam
          String createdBy)
      throws IOException, CommonServiceException {
    logger.debug("Processing get transit buffer details by transitBufferRequestId");

    var response =
        transitBufferService.getTransitBufferDetails(transitBufferConfigRequestId, createdBy);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit buffer details with pre signed url fetched successfully")
            .payload(response)
            .build());
  }
}
