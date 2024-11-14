/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.dashboard.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.PreSignedUrlResponse;
import com.nextuple.jobs.dashboard.controller.docs.DownloadFileUrlDoc;
import com.nextuple.jobs.dashboard.controller.docs.GetPreSignedUrlDoc;
import com.nextuple.jobs.framework.common.service.PreSignedUrlInterface;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/data-upload/ui")
@RequiredArgsConstructor
@Tag(name = "File Dashboard APIs")
public class FileDashboardController {

  private final PreSignedUrlInterface preSignedUrlInterface;

  private static final Logger logger = LoggerFactory.getLogger(FileDashboardController.class);

  @GetMapping("/pre-signed-url")
  @GetPreSignedUrlDoc
  public ResponseEntity<BaseResponse<PreSignedUrlResponse>> getPreSignedUrl(
      @RequestParam
          @Pattern(
              regexp = "^[a-zA-Z0-9._ #()\\-]*$",
              message = "Invalid input provided for fileName.")
          @Parameter(description = "Name of the file.")
          String fileName,
      @RequestParam
          @Pattern(regexp = "^[A-Za-z-_]*$", message = "Invalid input provided for moduleName.")
          @Parameter(description = "Name of the module of uploading file.")
          String moduleName)
      throws CommonServiceException {
    try {

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Pre Signed Url generated successfully!")
              .payload(preSignedUrlInterface.getPreSignedURL(fileName, moduleName))
              .build());
    } catch (Exception e) {
      logger.error("Error in generating pre signed url");
      throw e;
    }
  }

  @GetMapping("/file/{fileMetaId}")
  @DownloadFileUrlDoc
  public ResponseEntity<BaseResponse<PreSignedUrlResponse>> downloadFileURL(
      @PathVariable @Parameter(description = "Unique identifier for file metadata.")
          long fileMetaId)
      throws CommonServiceException {
    try {

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("File URL generated successfully!")
              .payload(preSignedUrlInterface.downloadFileURLById(fileMetaId))
              .build());
    } catch (Exception e) {
      logger.error("Error in fetching file url");
      throw e;
    }
  }
}
