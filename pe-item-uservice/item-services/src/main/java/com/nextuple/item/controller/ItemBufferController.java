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
import com.nextuple.item.controller.docs.*;
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
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing Item Buffer operations.
 *
 * <p>This controller provides APIs for performing operations such as creating, updating, fetching,
 * and deleting item buffers. It also supports fetching and deleting item buffers by organization ID
 * and item buffer ID.
 *
 * <p>The controller is tagged with "Item Buffer APIs" for easy categorization in API documentation.
 */
@Validated
@RestController
@Tag(name = "Item Buffer APIs")
@RequestMapping("/item-buffer")
@RequiredArgsConstructor
@Slf4j
public class ItemBufferController {

  private final ItemBufferService itemBufferService;

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
   * Creates a new Item Buffer based on the provided request data.
   *
   * <p>This endpoint processes the creation of an Item Buffer using the data in the request body.
   *
   * @param itemBufferRequest the request body containing the details of the item buffer to be
   *     created.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the created {@link
   *     ItemBufferResponse}.
   * @throws CommonServiceException if an error occurs during the service execution.
   * @throws ItemDomainException if there is an issue specific to the item domain during the
   *     creation process.
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
   * Deletes an existing Item Buffer based on the provided request data.
   *
   * <p>This endpoint processes the deletion of an Item Buffer using the data in the request body.
   *
   * @param itemBufferRequest the request body containing the details of the item buffer to be
   *     deleted.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the deleted {@link
   *     ItemBufferResponse}.
   * @throws CommonServiceException if an error occurs during the service execution.
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
   * Retrieves an Item Buffer by the provided organization ID and item buffer ID.
   *
   * <p>This endpoint fetches an Item Buffer based on the given orgId and id.
   *
   * @param orgId the organization ID associated with the Item Buffer.
   * @param id the ID of the Item Buffer to be fetched.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the fetched {@link
   *     ItemBufferResponse}.
   * @throws CommonServiceException if an error occurs during the service execution.
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
   * Deletes an Item Buffer based on the provided organization ID and item buffer ID.
   *
   * <p>This endpoint deletes an Item Buffer identified by the orgId and id.
   *
   * @param orgId the organization ID associated with the Item Buffer.
   * @param id the ID of the Item Buffer to be deleted.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the response after
   *     deletion.
   * @throws CommonServiceException if an error occurs during the deletion process.
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
   * Updates an existing Item Buffer based on the provided organization ID and item buffer ID.
   *
   * <p>This endpoint updates an Item Buffer identified by the orgId and id with the new data
   * provided in the updateRequest.
   *
   * @param orgId the organization ID associated with the Item Buffer.
   * @param id the ID of the Item Buffer to be updated.
   * @param updateRequest the data used to update the Item Buffer.
   * @return a {@link ResponseEntity} containing a {@link BaseResponse} with the updated Item Buffer
   *     data.
   * @throws CommonServiceException if an error occurs during the update process.
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
