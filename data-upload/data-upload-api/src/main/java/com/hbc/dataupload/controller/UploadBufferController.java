package com.hbc.dataupload.controller;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.service.UploadBufferService;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/upload")
@Slf4j
@RequiredArgsConstructor
public class UploadBufferController {
  private final UploadBufferService uploadBufferService;

  @PostMapping("/node-service-option-buffer")
  public ResponseEntity<BaseResponse<String>> uploadNodeServiceOptionBufferData(
      @NotBlank @RequestParam String fileUri) throws IOException, CommonServiceException {
    log.debug("Processing upload Node ServiceOption Buffer Data request");
    return uploadBufferService.uploadNodeServiceOptionBufferData(fileUri);
  }

  @PostMapping("/transit-buffer")
  public ResponseEntity<BaseResponse<String>> uploadTransitBufferData(
      @NotBlank @RequestParam String fileUri)
      throws IOException, CommonServiceException, CsvException {
    log.debug("Processing upload Transit Buffer Data request");
    return uploadBufferService.uploadTransitBufferData(fileUri);
  }
}
