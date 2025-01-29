/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.csvdownload.common.inbound.GenericUploadRequest;
import com.nextuple.csvdownload.controller.docs.EddComputationDoc;
import com.nextuple.csvdownload.service.EddComputationService;
import com.opencsv.exceptions.CsvException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for EDD computation utility APIs.
 *
 * <p>This controller provides APIs to process and compute Estimated Delivery Date (EDD) data based
 * on the provided input request. The computed data is returned as a downloadable file.
 *
 * <p>The controller is tagged with "EDD Computation Utility APIs" for easy categorization in API
 * documentation.
 */
@Validated
@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "EDD Computation Utility APIs")
public class EddComputationUtilityController {

  private final EddComputationService eddComputationService;

  /**
   * Processes and downloads the EDD computation data for the specified upload request.
   *
   * <p>This method processes a POST request to compute the EDD (Estimated Delivery Date) based on
   * the provided upload request containing the necessary data. The computed data is then returned
   * as a downloadable file.
   *
   * @param uploadRequest The request payload containing the data required for EDD computation.
   * @param response The HTTP response used to send the computed EDD data as a downloadable file.
   * @throws IOException If there is an error while reading or writing the file.
   * @throws CommonServiceException If there is an error during the service processing.
   * @throws CsvException If there is an error with CSV processing during the computation.
   */
  @PostMapping("/edd-computation")
  @EddComputationDoc
  public void eddComputationData(
      @RequestBody GenericUploadRequest uploadRequest, HttpServletResponse response)
      throws IOException, CommonServiceException, CsvException {
    log.debug("Processing Edd Computation Data request");
    try {
      final var file = eddComputationService.uploadEddCompuationData(uploadRequest);
      try (var inputStream = new FileInputStream(file)) {
        response.setStatus(HttpStatus.OK.value());
        response.setHeader(
            HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));
        IOUtils.copy(inputStream, response.getOutputStream());
        response.flushBuffer();
      } finally {
        Files.delete(file.toPath());
      }
    } catch (Exception e) {
      log.error(String.valueOf(e), "Failed to process Edd Computation Data request!");
      throw e;
    }
  }
}
