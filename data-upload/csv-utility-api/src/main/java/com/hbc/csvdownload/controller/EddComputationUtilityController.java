package com.hbc.csvdownload.controller;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.csvdownload.service.EddComputationService;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Slf4j
@RequiredArgsConstructor
public class EddComputationUtilityController {

  private final EddComputationService eddComputationService;

  @PostMapping("/edd-computation")
  public void eddComputationData(
      @NotBlank(message = "fileUri can't be empty") @RequestParam String fileUri,
      HttpServletResponse response)
      throws IOException, CommonServiceException {
    log.debug("Processing Edd Computation Data request");
    try {
      final var file = eddComputationService.uploadEddCompuationData(fileUri);
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
