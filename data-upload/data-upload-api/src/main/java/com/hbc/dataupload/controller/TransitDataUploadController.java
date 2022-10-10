package com.hbc.dataupload.controller;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.service.TransitDataUploadService;
import java.io.IOException;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Validated
@Controller
@RequestMapping("/upload")
@Slf4j
@RequiredArgsConstructor
public class TransitDataUploadController {
  private final TransitDataUploadService transitDataUploadService;

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
