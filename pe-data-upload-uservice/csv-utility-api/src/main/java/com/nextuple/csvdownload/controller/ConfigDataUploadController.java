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
import com.nextuple.csvdownload.common.inbound.GenericUploadRequest;
import com.nextuple.csvdownload.common.outbound.GenericUploadResponse;
import com.nextuple.csvdownload.controller.docs.UploadConfigDataDoc;
import com.nextuple.csvdownload.exception.JobSubmissionException;
import com.nextuple.csvdownload.service.ConfigDataUploadService;
import com.opencsv.exceptions.CsvException;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for uploading configuration data for a specified module.
 *
 * <p>This controller provides an API to upload configuration data for a specific module. It
 * processes a POST request containing the module name and configuration data, and returns the
 * result of the upload process. The controller handles errors related to job submission, file
 * handling, and CSV parsing.
 *
 * <p>The controller is tagged with "Config Data Upload APIs" for easy categorization in API
 * documentation.
 */
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Config Data Upload APIs")
public class ConfigDataUploadController {

  private final ConfigDataUploadService configDataUploadService;

  private static final Logger logger = LoggerFactory.getLogger(ConfigDataUploadController.class);

  /**
   * Uploads the configuration data for a specified module.
   *
   * <p>This method processes a POST request to upload configuration data based on the provided
   * module name and the generic upload request payload.
   *
   * @param moduleName The name of the module for which the config data is being uploaded.
   * @param uploadRequest The request payload containing the configuration data to be uploaded.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the result of the
   *     upload process.
   * @throws CommonServiceException If there is a general service exception during the process.
   * @throws JobSubmissionException If there is an error during job submission.
   * @throws IOException If there is an error during file handling.
   * @throws CsvException If there is an error during CSV file parsing.
   */
  @PostMapping("/v1/{moduleName}/process-request")
  @UploadConfigDataDoc
  public ResponseEntity<BaseResponse<GenericUploadResponse>> uploadConfigData(
      @PathVariable @Parameter(description = "Name of the module.") String moduleName,
      @RequestBody GenericUploadRequest uploadRequest)
      throws CommonServiceException, JobSubmissionException, IOException, CsvException {

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Config data uploaded successfully!")
            .payload(configDataUploadService.processUploadConfigData(moduleName, uploadRequest))
            .build());
  }
}
