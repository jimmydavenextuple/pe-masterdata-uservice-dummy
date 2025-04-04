/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.exception.CsvFormatValidationFailedException;
import com.nextuple.csvdownload.exception.JobSubmissionException;
import com.nextuple.dataupload.service.UploadBufferService;
import com.opencsv.exceptions.CsvException;
import jakarta.validation.constraints.NotBlank;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for handling buffer data uploads and operations.
 *
 * <p>This controller provides APIs to manage various buffer data operations, including uploading
 * and deleting transit buffer data and node service option buffer data. It supports error handling
 * for file processing, validation, and service operations.
 *
 * <p>The controller is tagged for clear API categorization in the documentation.
 */
@Validated
@Controller
@RequestMapping("/upload")
@Slf4j
@RequiredArgsConstructor
public class UploadBufferController {

  private final UploadBufferService uploadBufferService;

  /**
   * Uploads node service option buffer data.
   *
   * <p>This method processes a POST request to upload a node service option buffer data file. The
   * file URI should be provided as a request parameter.
   *
   * @param fileUri The URI of the file to be uploaded.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a success message.
   * @throws IOException If an error occurs while processing the file.
   * @throws CommonServiceException If an error occurs during the service operation.
   */
  @PostMapping("/node-service-option-buffer")
  public ResponseEntity<BaseResponse<String>> uploadNodeServiceOptionBufferData(
      @NotBlank(message = "fileUri can't be empty") @RequestParam String fileUri)
      throws IOException, CommonServiceException {
    log.debug("Processing upload Node ServiceOption Buffer Data request");
    return uploadBufferService.uploadNodeServiceOptionBufferData(fileUri);
  }

  /**
   * Uploads transit buffer data.
   *
   * <p>This method processes a POST request to upload transit buffer data from a file URI provided
   * as a request parameter.
   *
   * @param fileUri The URI of the transit buffer data file to be uploaded.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a success message.
   * @throws IOException If an error occurs while processing the file.
   * @throws CommonServiceException If an error occurs during the service operation.
   * @throws CsvException If an error occurs while parsing the CSV file.
   */
  @PostMapping("/transit-buffer")
  public ResponseEntity<BaseResponse<String>> uploadTransitBufferData(
      @NotBlank(message = "fileUri can't be empty") @RequestParam String fileUri)
      throws IOException, CommonServiceException, CsvException {
    log.debug("Processing upload Transit Buffer Data request");
    return uploadBufferService.uploadTransitBufferData(fileUri);
  }

  /**
   * Deletes transit buffer data based on the provided file URI.
   *
   * <p>This method processes a POST request to delete transit buffer data. It takes a file URI as
   * input, which is used to locate the data to be deleted.
   *
   * @param fileUri The URI of the file containing the transit buffer data to be deleted.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the result of the
   *     delete operation.
   * @throws IOException If an error occurs while processing the file.
   * @throws CommonServiceException If an error occurs during the service operation.
   * @throws CsvException If an error occurs while parsing the CSV file.
   * @throws CsvFormatValidationFailedException If the file format is invalid.
   * @throws JobSubmissionException If there is an issue submitting the delete job.
   */
  @PostMapping("/transit-buffer-delete")
  public ResponseEntity<BaseResponse<String>> uploadDeleteTransitBufferData(
      @NotBlank @RequestParam String fileUri)
      throws IOException,
          CommonServiceException,
          CsvException,
          CsvFormatValidationFailedException,
          JobSubmissionException {
    log.debug("Processing upload Transit Buffer Data request");
    return ResponseEntity.ok()
        .body(
            BaseResponse.builder()
                .payload(uploadBufferService.deleteTransitBuffer(fileUri))
                .build());
  }
}
