package com.hbc.jobs.dashboard.controller;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.jobs.framework.common.domain.outbound.PreSignedUrlResponse;
import com.hbc.jobs.framework.common.service.PreSignedUrlInterface;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/data-upload/ui")
@RequiredArgsConstructor
public class FileDashboardController {

  private final PreSignedUrlInterface preSignedUrlInterface;

  private static final Logger logger = LoggerFactory.getLogger(FileDashboardController.class);

  @GetMapping("/pre-signed-url")
  public ResponseEntity<BaseResponse<PreSignedUrlResponse>> getPreSignedUrl(
      @RequestParam String fileName, @RequestParam String moduleName)
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
  public ResponseEntity<BaseResponse<PreSignedUrlResponse>> downloadFileURL(
      @PathVariable long fileMetaId) throws CommonServiceException {
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
