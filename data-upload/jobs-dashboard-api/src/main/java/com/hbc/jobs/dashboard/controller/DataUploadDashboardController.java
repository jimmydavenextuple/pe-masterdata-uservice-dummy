package com.hbc.jobs.dashboard.controller;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.jobs.dashboard.service.PreSignedUrlInterface;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/data-upload/ui")
@RequiredArgsConstructor
public class DataUploadDashboardController {

  private final PreSignedUrlInterface s3SignedUrlInterface;

  private static final Logger logger = LoggerFactory.getLogger(DataUploadDashboardController.class);

  @GetMapping("/pre-signed-url")
  public ResponseEntity<BaseResponse<String>> getPreSignedUrl(
      @RequestParam String fileName, @RequestParam String moduleName)
      throws CommonServiceException {
    try {

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Pre Signed Url generated successfully!")
              .payload(s3SignedUrlInterface.getPreSignedURL(fileName, moduleName))
              .build());
    } catch (Exception e) {
      logger.error("Error in generating pre signed url");
      throw e;
    }
  }
}
