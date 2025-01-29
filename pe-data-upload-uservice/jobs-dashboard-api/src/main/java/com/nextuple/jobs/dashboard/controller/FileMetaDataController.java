/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.dashboard.controller;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.jobs.dashboard.controller.docs.CreateFileMetadata;
import com.nextuple.jobs.dashboard.controller.docs.DeleteFileMetedataDoc;
import com.nextuple.jobs.dashboard.controller.docs.FindFileMetadataDoc;
import com.nextuple.jobs.dashboard.controller.docs.UpdateFileMetadataDoc;
import com.nextuple.jobs.dashboard.exception.FileMetaDataException;
import com.nextuple.jobs.dashboard.service.FileMetaDataService;
import com.nextuple.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.nextuple.jobs.framework.common.inbound.FileMetaDataCreationRequest;
import com.nextuple.jobs.framework.common.inbound.FileMetaDataUpdationRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing file metadata operations.
 *
 * <p>This controller provides APIs to create, retrieve, update, and delete file metadata entries.
 * It serves as the backend interface for handling file metadata associated with uploaded files.
 *
 * <p>Tagged under "File Metadata APIs" for documentation categorization, this controller ensures
 * seamless file metadata management with robust exception handling and validation.
 */
@RestController
@RequestMapping("/file-metadata")
@RequiredArgsConstructor
@Tag(name = "File Metadata APIs")
public class FileMetaDataController {

  private static final Logger logger = LoggerFactory.getLogger(FileMetaDataController.class);

  private final FileMetaDataService fileMetadataService;

  /**
   * Creates a new file metadata entry.
   *
   * <p>This method handles the creation of a new file metadata entry by accepting a {@link
   * FileMetaDataCreationRequest} containing the metadata details. It returns the created file
   * metadata response if successful.
   *
   * @param fileMetadataCreationRequest The request body containing the metadata details.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the file metadata
   *     response.
   * @throws FileMetaDataException If there is an error while creating the file metadata.
   */
  @PostMapping
  @CreateFileMetadata
  public ResponseEntity<BaseResponse<FileMetaDataResponse>> createFileMetadata(
      @Valid @RequestBody FileMetaDataCreationRequest fileMetadataCreationRequest)
      throws FileMetaDataException {
    logger.debug("Processing file metadata creation request");
    try {
      var fileMetadataResponse =
          fileMetadataService.createFileMetadata(fileMetadataCreationRequest);
      logger.debug("Response after creation of file metadata:");
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Metadata file created successfully")
              .payload(fileMetadataResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to create file metadata detail");
      throw e;
    }
  }

  /**
   * Retrieves the file metadata by its unique identifier.
   *
   * <p>This method fetches the file metadata associated with the provided identifier. It returns
   * the metadata details if the file is found.
   *
   * @param id The unique identifier for the file metadata.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the file metadata
   *     response.
   * @throws FileMetaDataException If there is an error while fetching the file metadata.
   */
  @GetMapping("/{id}")
  @FindFileMetadataDoc
  public ResponseEntity<BaseResponse<FileMetaDataResponse>> findFileMetadataById(
      @NotBlank @PathVariable("id") @Parameter(description = "Unique identifier for metadata.")
          Long id)
      throws FileMetaDataException {
    try {
      var fileMetadataResponse = fileMetadataService.getFileMetadataById(id);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Metadata file fetched successfully")
              .payload(fileMetadataResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to get file metadata");
      throw e;
    }
  }

  /**
   * Updates the file metadata for the specified identifier.
   *
   * <p>This method allows updating the existing file metadata based on the given identifier and the
   * updated metadata details provided in the request body.
   *
   * @param id The unique identifier for the file metadata to be updated.
   * @param newfilemetadata The request body containing the updated metadata details.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated file
   *     metadata response.
   * @throws FileMetaDataException If there is an error while updating the file metadata.
   */
  @PutMapping("/{id}")
  @UpdateFileMetadataDoc
  public ResponseEntity<BaseResponse<FileMetaDataResponse>> updateFileMetadataById(
      @NotBlank @PathVariable("id") @Parameter(description = "Unique identifier for metadata.")
          Long id,
      @RequestBody FileMetaDataUpdationRequest newfilemetadata)
      throws FileMetaDataException {
    try {
      var fileMetadataResponse = fileMetadataService.updateFileMetadata(id, newfilemetadata);
      logger.debug("Response after update of file metadata :{}", fileMetadataResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Metadata file updated successfully")
              .payload(fileMetadataResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to update file metadata");
      throw e;
    }
  }

  /**
   * Deletes the file metadata by its unique identifier.
   *
   * <p>This method removes the file metadata associated with the provided identifier. It returns a
   * response confirming the deletion.
   *
   * @param id The unique identifier for the file metadata to be deleted.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the deleted file
   *     metadata response.
   * @throws FileMetaDataException If there is an error while deleting the file metadata.
   */
  @DeleteMapping("/{id}")
  @DeleteFileMetedataDoc
  public ResponseEntity<BaseResponse<FileMetaDataResponse>> deleteFileMetadataById(
      @NotBlank @PathVariable("id") @Parameter(description = "Unique identifier for metadata.")
          Long id)
      throws FileMetaDataException {

    try {
      var fileMetadataResponse = fileMetadataService.deleteFileMetadataById(id);
      logger.debug("Response after deletion of file metadata :{}", fileMetadataResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Metadata file deleted successfully")
              .payload(fileMetadataResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to delete file metadata");
      throw e;
    }
  }
}
