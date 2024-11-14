/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.controller;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.controller.docs.DeleteTransitBufferUploadDoc;
import com.nextuple.csvdownload.controller.docs.UpdateTransitBufferUploadDoc;
import com.nextuple.csvdownload.controller.docs.UploadProcessingLeadTimesDoc;
import com.nextuple.csvdownload.controller.docs.UploadTransitBufferDoc;
import com.nextuple.csvdownload.controller.docs.UploadTransitTimesDoc;
import com.nextuple.csvdownload.exception.CsvFormatValidationFailedException;
import com.nextuple.csvdownload.exception.CsvParsingException;
import com.nextuple.csvdownload.exception.JobSubmissionException;
import com.nextuple.csvdownload.service.CsvUploadUtilityService;
import com.nextuple.transit.domain.inbound.TransitBufferConfigRequest;
import com.nextuple.transit.domain.outbound.TransitBufferConfigResponse;
import com.opencsv.exceptions.CsvException;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "CSV Upload Utility APIs")
public class CsvUploadUtilityController {

  private final CsvUploadUtilityService csvUploadUtilityService;

  private static final Logger logger = LoggerFactory.getLogger(CsvUploadUtilityController.class);

  @PostMapping(
      path = "/org/{orgId}/upload/transit-times",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @UploadTransitTimesDoc
  public ResponseEntity<BaseResponse<String>> uploadTransitTimes(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organization ID.")
          String orgId,
      @RequestBody @Parameter(description = "Transit times file to be uploaded.")
          MultipartFile csvFile)
      throws CsvException, IOException, CsvFormatValidationFailedException, JobSubmissionException {
    logger.debug("-- Inside upload transit times --");

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Bulk upload of transit times uploaded successfully!")
            .payload(csvUploadUtilityService.uploadTransitTimesCsv(orgId, csvFile))
            .build());
  }

  @PostMapping(
      path = "/org/{orgId}/upload/processing-lead-times",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @UploadProcessingLeadTimesDoc
  public ResponseEntity<BaseResponse<String>> uploadProcessingLeadTimes(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organization ID.")
          String orgId,
      @RequestParam @Parameter(description = "Processing lead times file to be uploaded.")
          MultipartFile csvFile)
      throws CsvParsingException,
          CsvFormatValidationFailedException,
          JobSubmissionException,
          IOException,
          CsvException {
    logger.debug("-- Inside upload processing lead times --");

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Bulk upload of processing lead times uploaded successfully!")
            .payload(csvUploadUtilityService.uploadProcessingLeadTimesCsv(orgId, csvFile))
            .build());
  }

  @PostMapping("/ui/upload/transit-buffer")
  @UploadTransitBufferDoc
  public ResponseEntity<BaseResponse<TransitBufferConfigResponse>> uploadTransitBuffer(
      @RequestBody TransitBufferConfigRequest transitBufferConfigRequest)
      throws CommonServiceException {
    logger.debug("-- Inside upload transit buffer --");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Upload of transit buffer uploaded successfully!")
            .payload(csvUploadUtilityService.uploadTransitBufferData(transitBufferConfigRequest))
            .build());
  }

  @PutMapping("/ui/upload/transit-buffer")
  @UpdateTransitBufferUploadDoc
  public ResponseEntity<BaseResponse<TransitBufferConfigResponse>> updateTransitBufferUpload(
      @RequestBody TransitBufferConfigRequest transitBufferConfigRequest)
      throws CommonServiceException {
    logger.debug("-- Inside update transit buffer records --");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit buffer records updated successfully!")
            .payload(csvUploadUtilityService.updatingTransitBufferData(transitBufferConfigRequest))
            .build());
  }

  @DeleteMapping("/ui/upload/transit-buffer")
  @DeleteTransitBufferUploadDoc
  public ResponseEntity<BaseResponse<TransitBufferConfigResponse>> deleteTransitBufferUpload(
      @NotNull(message = "transitBufferRequestId can't be null")
          @RequestParam
          @Parameter(description = "Unique identifier for the transit buffer request.")
          Long transitBufferRequestId,
      @NotBlank(message = "createdBy can't be blank")
          @Parameter(description = "Name of the user.")
          @RequestParam
          String createdBy)
      throws CommonServiceException {
    logger.debug("-- Inside delete transit buffer records --");
    csvUploadUtilityService.deletingTransitBufferData(transitBufferRequestId, createdBy);
    return ResponseEntity.ok(
        BaseResponse.builder().message("Transit buffer records deleted successfully!").build());
  }
}
