package com.hbc.jobs.dashboard.controller;

import com.hbc.common.response.BaseResponse;
import com.hbc.jobs.dashboard.domain.inbound.FileMetaDataCreationRequest;
import com.hbc.jobs.dashboard.domain.inbound.FileMetaDataUpdationRequest;
import com.hbc.jobs.dashboard.domain.outbound.FileMetaDataResponse;
import com.hbc.jobs.dashboard.exception.FileMetaDataException;
import com.hbc.jobs.dashboard.service.FileMetaDataService;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/file-metadata")
@RequiredArgsConstructor
public class FileMetaDataController {

  private static final Logger logger = LoggerFactory.getLogger(FileMetaDataController.class);

  private final FileMetaDataService fileMetadataService;

  @PostMapping
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

  @GetMapping("/{id}")
  public ResponseEntity<BaseResponse<FileMetaDataResponse>> findFileMetadataById(
      @NotBlank @PathVariable("id") Long id) throws FileMetaDataException {
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

  @PutMapping("/{id}")
  public ResponseEntity<BaseResponse<FileMetaDataResponse>> updateFileMetadataById(
      @NotBlank @PathVariable("id") Long id,
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

  @DeleteMapping("/{id}")
  public ResponseEntity<BaseResponse<FileMetaDataResponse>> deleteFileMetadataById(
      @NotBlank @PathVariable("id") Long id) throws FileMetaDataException {

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
