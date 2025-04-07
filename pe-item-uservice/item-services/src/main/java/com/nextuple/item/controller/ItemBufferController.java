/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.common.response.error.ErrorType;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.item.controller.docs.CreateItemBufferDoc;
import com.nextuple.item.controller.docs.DeleteItemBufferByOrgIdAndIdDoc;
import com.nextuple.item.controller.docs.DeleteItemBufferDoc;
import com.nextuple.item.controller.docs.GetItemBufferByOrgIdAndIdDoc;
import com.nextuple.item.controller.docs.UpdateItemBufferByOrgIdAndIdDoc;
import com.nextuple.item.domain.constants.ItemConstants;
import com.nextuple.item.domain.inbound.ItemBufferRequest;
import com.nextuple.item.domain.inbound.ItemBufferUpdateRequest;
import com.nextuple.item.domain.outbound.ItemBufferResponse;
import com.nextuple.item.persistence.exception.ItemDomainException;
import com.nextuple.item.service.ItemBufferService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing Item Buffer operations.
 *
 * <p>This class provides endpoints for creating, updating, retrieving, and deleting item buffers.
 * It handles the management of item buffer configurations and supports operations at both
 * organization and individual buffer levels.
 *
 * <p>Key features include:
 *
 * <ul>
 *   <li>Creation and deletion of item buffers
 *   <li>Retrieval of specific item buffer details
 *   <li>Update operations for existing item buffers
 *   <li>Support for organization-level operations
 * </ul>
 *
 * <p>The controller includes error handling for invalid format exceptions and validates all
 * incoming requests.
 *
 * @see ItemBufferService
 * @see ItemBufferRequest
 * @see ItemBufferResponse
 */
@Validated
@RestController
@Tag(name = "Item Buffer APIs")
@RequestMapping("/item-buffer")
@RequiredArgsConstructor
@Slf4j
public class ItemBufferController {

  private final ItemBufferService itemBufferService;

  /**
   * Handles validation errors for invalid format exceptions.
   *
   * <p>This exception handler processes invalid format exceptions and returns appropriate error
   * responses with detailed field error information.
   *
   * @param ex The invalid format exception to be handled
   * @return A {@link ResponseEntity} containing the error response
   */
  @ExceptionHandler(InvalidFormatException.class)
  public ResponseEntity<ErrorResponse> handleInvalidFormatException(InvalidFormatException ex) {
    return ResponseEntity.badRequest()
        .body(
            // In Nextuple, we use code 2 for any JSON errors.
            ErrorResponse.builder(ErrorType.ERROR, 2)
                .message(ex.getOriginalMessage())
                .errorField(
                    // Using getFirst() because ex.getPath() will contain only one object at any
                    // given point.
                    ex.getPath().getFirst().getFieldName(),
                    FieldError.builder()
                        .rejectedValue(ex.getValue())
                        .errorMessage(ex.getOriginalMessage())
                        .build())
                .build());
  }

  /**
   * Creates a new item buffer based on the provided request.
   *
   * <p>This method processes a POST request to create a new item buffer. The creation is performed
   * based on the details provided in the {@link ItemBufferRequest}.
   *
   * @param itemBufferRequest The request object containing the item buffer details
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the created {@link
   *     ItemBufferResponse}
   * @throws CommonServiceException If there is an error in processing the request
   * @throws ItemDomainException If there is a domain-specific error
   */
  @CreateItemBufferDoc
  @PostMapping
  public ResponseEntity<BaseResponse<ItemBufferResponse>> createItemBuffer(
      @Valid @RequestBody ItemBufferRequest itemBufferRequest)
      throws CommonServiceException, ItemDomainException {
    log.debug("Processing item buffer creation request {}", itemBufferRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Item Buffer successfully created")
            .payload(itemBufferService.createItemBuffer(itemBufferRequest))
            .build());
  }

  /**
   * Deletes an item buffer based on the provided request.
   *
   * <p>This method processes a DELETE request to remove an existing item buffer configuration. The
   * deletion is performed based on the details provided in the {@link ItemBufferRequest}.
   *
   * @param itemBufferRequest The request object containing the item buffer details to be deleted.
   *     Must not be null and must contain valid organization and buffer IDs.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the deleted {@link
   *     ItemBufferResponse} details.
   * @throws CommonServiceException If there is an error in processing the request, such as when the
   *     item buffer doesn't exist or cannot be deleted.
   */
  @DeleteItemBufferDoc
  @DeleteMapping
  public ResponseEntity<BaseResponse<ItemBufferResponse>> deleteItemBuffer(
      @Valid @RequestBody ItemBufferRequest itemBufferRequest) throws CommonServiceException {
    log.debug("Processing item buffer deletion request {}", itemBufferRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Item Buffer successfully deleted")
            .payload(itemBufferService.deleteItemBuffer(itemBufferRequest))
            .build());
  }

  /**
   * Retrieves an item buffer by organization ID and buffer ID.
   *
   * <p>This method processes a GET request to fetch a specific item buffer configuration identified
   * by the combination of organization ID and buffer ID. The method validates both parameters
   * before processing the request.
   *
   * @param orgId The unique identifier for the organization. Must not be blank. Example value:
   *     "NEXTUPLE"
   * @param id The unique identifier for the item buffer. Must not be null. Example value: 12345
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the retrieved {@link
   *     ItemBufferResponse}.
   * @throws CommonServiceException If there is an error in processing the request, such as when the
   *     item buffer doesn't exist or cannot be accessed.
   */
  @GetItemBufferByOrgIdAndIdDoc
  @GetMapping("/{orgId}/{id}")
  public ResponseEntity<BaseResponse<ItemBufferResponse>> getItemBufferByOrgIdAndId(
      @NotBlank(message = "orgId can't be empty")
          @Parameter(description = ItemConstants.ORG_ID, example = ItemConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId,
      @NotNull(message = "id can't be empty")
          @Parameter(description = ItemConstants.ID, example = ItemConstants.ID)
          @PathVariable
          Long id)
      throws CommonServiceException {
    log.debug("Processing item buffer get request with orgId: {} and id: {}", orgId, id);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Item Buffer successfully fetched")
            .payload(itemBufferService.fetchItemBuffer(orgId, id))
            .build());
  }

  /**
   * Deletes an item buffer by organization ID and buffer ID.
   *
   * <p>This method processes a DELETE request to remove a specific item buffer configuration
   * identified by the combination of organization ID and buffer ID. The method validates both
   * parameters before processing the deletion request.
   *
   * @param orgId The unique identifier for the organization. Must not be blank. Example value:
   *     "NEXTUPLE"
   * @param id The unique identifier for the item buffer. Must not be null. Example value: 12345
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the deleted {@link
   *     ItemBufferResponse}.
   * @throws CommonServiceException If there is an error in processing the request, such as when the
   *     item buffer doesn't exist or cannot be deleted.
   */
  @DeleteItemBufferByOrgIdAndIdDoc
  @DeleteMapping("/{orgId}/{id}")
  public ResponseEntity<BaseResponse<ItemBufferResponse>> deleteItemBufferByOrgIdAndId(
      @NotBlank(message = "orgId can't be empty")
          @Parameter(description = ItemConstants.ORG_ID, example = ItemConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId,
      @NotNull(message = "id can't be empty")
          @Parameter(description = ItemConstants.ID, example = ItemConstants.ID)
          @PathVariable
          Long id)
      throws CommonServiceException {
    log.debug("Processing item buffer deletion request with orgId: {} and id: {}", orgId, id);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Item Buffer successfully deleted")
            .payload(itemBufferService.deleteItemBufferByOrgIdAndId(orgId, id))
            .build());
  }

  /**
   * Updates an existing item buffer configuration by organization ID and buffer ID.
   *
   * <p>This method processes a PUT request to update a specific item buffer configuration
   * identified by the combination of organization ID and buffer ID. The update is performed using
   * the details provided in the {@link ItemBufferUpdateRequest}.
   *
   * @param orgId The unique identifier for the organization. Must not be blank. Example value:
   *     "NEXTUPLE"
   * @param id The unique identifier for the item buffer. Must not be null. Example value: 12345
   * @param updateRequest The {@link ItemBufferUpdateRequest} containing the updated configuration
   *     details for the item buffer.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated {@link
   *     ItemBufferResponse}.
   * @throws CommonServiceException If there is an error in processing the request, such as when the
   *     item buffer doesn't exist or the update is invalid.
   */
  @UpdateItemBufferByOrgIdAndIdDoc
  @PutMapping("/{orgId}/{id}")
  public ResponseEntity<BaseResponse<ItemBufferResponse>> updateItemBufferByOrgIdAndId(
      @NotBlank(message = "orgId can't be empty")
          @Parameter(description = ItemConstants.ORG_ID, example = ItemConstants.ORG_ID_EXAMPLE)
          @PathVariable
          String orgId,
      @NotNull(message = "id can't be empty")
          @Parameter(description = ItemConstants.ID, example = ItemConstants.ID)
          @PathVariable
          Long id,
      @Valid @RequestBody ItemBufferUpdateRequest updateRequest)
      throws CommonServiceException {
    log.debug("Processing item buffer update request with orgId: {} and id: {}", orgId, id);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Item Buffer successfully updated")
            .payload(itemBufferService.updateItemBuffer(orgId, id, updateRequest))
            .build());
  }
}
