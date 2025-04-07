/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.service.TransitDataUploadService;
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
 * Controller for Transit Data Upload APIs.
 *
 * <p>This controller provides an API to upload transit data from a specified file URI. It handles
 * processing the transit data file and returns the result of the upload operation.
 *
 * <p>The controller is tagged with "Transit Data Upload APIs" for easy categorization in API
 * documentation.
 */
@Validated
@Controller
@RequestMapping("/upload")
@Slf4j
@RequiredArgsConstructor
public class TransitDataUploadController {
  private final TransitDataUploadService transitDataUploadService;

  /**
   * Handles the upload of transit data from a specified file URI.
   *
   * <p>This method processes a POST request to upload transit data using the provided file URI. The
   * transit data file is uploaded for further processing, and a response containing the result of
   * the upload is returned.
   *
   * @param fileUri The URI of the transit data file to be uploaded.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the result of the
   *     upload operation.
   * @throws IOException If there is an error reading the file.
   * @throws CommonServiceException If there is an error processing the file upload.
   */
  @PostMapping("/transit")
  public ResponseEntity<BaseResponse<String>> uploadTransitData(
      @NotBlank(message = "fileUri can't be empty") @RequestParam String fileUri)
      throws IOException, CommonServiceException {
    log.debug("Processing upload Transit Data request");
    try {
      return transitDataUploadService.uploadTransitData(fileUri);
    } catch (Exception e) {
      log.error(String.valueOf(e), "Failed to process upload Transit Data request!");
      throw e;
    }
  }
}
