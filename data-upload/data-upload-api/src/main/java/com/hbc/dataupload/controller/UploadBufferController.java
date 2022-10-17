package com.hbc.dataupload.controller;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.exception.CsvFormatValidationFailedException;
import com.hbc.csvdownload.exception.JobSubmissionException;
import com.hbc.dataupload.service.UploadBufferService;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Validated
@Controller
@RequestMapping("/upload")
@Slf4j
@RequiredArgsConstructor
public class UploadBufferController {

  private final UploadBufferService uploadBufferService;

  @PostMapping("/node-service-option-buffer")
  public ResponseEntity<BaseResponse<String>> uploadNodeServiceOptionBufferData(
      @NotBlank(message = "fileUri can't be empty") @RequestParam String fileUri)
      throws IOException, CommonServiceException {
    log.debug("Processing upload Node ServiceOption Buffer Data request");
    return uploadBufferService.uploadNodeServiceOptionBufferData(fileUri);
  }

  @PostMapping("/transit-buffer")
  public ResponseEntity<BaseResponse<String>> uploadTransitBufferData(
      @NotBlank(message = "fileUri can't be empty") @RequestParam String fileUri)
      throws IOException, CommonServiceException, CsvException {
    log.debug("Processing upload Transit Buffer Data request");
    return uploadBufferService.uploadTransitBufferData(fileUri);
  }

  @DeleteMapping("/transit-buffer-delete")
  public ResponseEntity<BaseResponse<String>> uploadDeleteTransitBufferData(
      @NotBlank @RequestParam String fileUri)
      throws IOException, CommonServiceException, CsvException, CsvFormatValidationFailedException,
          JobSubmissionException {
    log.debug("Processing upload Transit Buffer Data request");
    return ResponseEntity.ok()
        .body(
            BaseResponse.builder()
                .payload(uploadBufferService.deleteTransitBuffer(fileUri))
                .build());
  }
}
