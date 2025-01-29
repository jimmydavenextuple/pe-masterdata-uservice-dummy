/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.service.WeightageConfigurationDataUploadService;
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
 * Controller for uploading weightage configuration data.
 *
 * <p>This controller provides an API to handle the upload of weightage configuration data. The data
 * is fetched from a specified file URI and processed for further operations.
 *
 * <p>Tagged under "Weightage Configuration APIs" for documentation categorization, the controller
 * ensures clear error handling for file processing and service operations.
 */
@Validated
@Controller
@RequestMapping("/upload")
@Slf4j
@RequiredArgsConstructor
public class WeightageConfigurationDataUploadController {
  private final WeightageConfigurationDataUploadService weightageConfigurationDataUploadService;

  /**
   * Uploads weightage configuration data.
   *
   * <p>This method processes a POST request to upload weightage configuration data from a file URI
   * provided as a request parameter.
   *
   * @param fileUri The URI of the weightage configuration data file to be uploaded.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a success message.
   * @throws IOException If an error occurs while processing the file.
   * @throws CommonServiceException If an error occurs during the service operation.
   */
  @PostMapping("/weightage")
  public ResponseEntity<BaseResponse<String>> uploadWeightageConfigurationData(
      @NotBlank(message = "fileUri can't be empty") @RequestParam String fileUri)
      throws IOException, CommonServiceException {
    log.debug("Processing upload Weightage Configuration Data request");
    try {
      return weightageConfigurationDataUploadService.uploadWeightageConfigurationData(fileUri);
    } catch (Exception e) {
      log.error(
          String.valueOf(e), "Failed to process upload Weightage Configuration Data request!");
      throw e;
    }
  }
}
