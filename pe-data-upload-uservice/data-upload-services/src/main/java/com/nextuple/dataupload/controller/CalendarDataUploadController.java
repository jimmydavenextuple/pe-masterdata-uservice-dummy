/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.service.CalendarDataUploadService;
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
 * Controller for calendar data upload APIs.
 *
 * <p>This controller provides APIs to upload calendar data from a specified file URI and process it
 * for storage or further use.
 *
 * <p>The controller is tagged for handling calendar data upload functionality.
 */
@Validated
@Controller
@RequestMapping("/upload")
@Slf4j
@RequiredArgsConstructor
public class CalendarDataUploadController {
  private final CalendarDataUploadService calendarDataUploadService;

  /**
   * Uploads the calendar data from the specified file URI.
   *
   * <p>This method processes a POST request to upload calendar data by fetching the file located at
   * the specified file URI and processing it.
   *
   * @param fileUri The URI of the file containing the calendar data to be uploaded.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a success message if
   *     the upload is successful.
   * @throws IOException If there is an error during file reading or processing.
   * @throws CommonServiceException If there is a general error during the service process.
   */
  @PostMapping("/calendar")
  public ResponseEntity<BaseResponse<String>> uploadCalendarData(
      @NotBlank(message = "fileUri can't be empty") @RequestParam String fileUri)
      throws IOException, CommonServiceException {
    log.debug("Processing upload Calendar Data request");
    try {
      return calendarDataUploadService.uploadCalendarData(fileUri);
    } catch (Exception e) {
      log.error(String.valueOf(e), "Failed to process upload Calendar Data request!");
      throw e;
    }
  }
}
