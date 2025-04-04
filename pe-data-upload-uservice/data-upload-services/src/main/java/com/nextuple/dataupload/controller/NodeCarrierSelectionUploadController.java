/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.service.NodeCarrierSelectionUploadService;
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
 * Controller for node carrier selection upload APIs.
 *
 * <p>This controller provides an API to upload node carrier selection data from a specified file
 * URI. It handles the ingestion and processing of data related to node carrier selection for
 * further use.
 *
 * <p>The controller is responsible for coordinating with the service layer to process the uploaded
 * data and return appropriate responses.
 */
@Validated
@Controller
@RequestMapping("/upload")
@Slf4j
@RequiredArgsConstructor
public class NodeCarrierSelectionUploadController {
  private final NodeCarrierSelectionUploadService nodeCarrierSelectionUploadService;

  /**
   * Uploads the node carrier selection data from the specified file URI.
   *
   * <p>This method processes a POST request to upload node carrier selection data by fetching the
   * file located at the specified file URI and processing it.
   *
   * @param fileUri The URI of the file containing the node carrier selection data to be uploaded.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a success message if
   *     the upload is successful.
   * @throws IOException If there is an error during file reading or processing.
   * @throws CommonServiceException If there is a general error during the service process.
   */
  @PostMapping("/node-carrier-selection-upload")
  public ResponseEntity<BaseResponse<String>> nodeCarrierSelectionUpload(
      @NotBlank(message = "fileUri can't be empty") @RequestParam String fileUri)
      throws IOException, CommonServiceException {
    log.debug("Processing node service selection upload request");
    return nodeCarrierSelectionUploadService.nodeCarrierSelectionUpload(fileUri);
  }
}
