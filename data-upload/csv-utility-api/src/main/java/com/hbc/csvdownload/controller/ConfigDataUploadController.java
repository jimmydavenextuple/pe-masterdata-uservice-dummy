package com.hbc.csvdownload.controller;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.common.inbound.GenericUploadRequest;
import com.hbc.csvdownload.common.outbound.GenericUploadResponse;
import com.hbc.csvdownload.exception.JobSubmissionException;
import com.hbc.csvdownload.service.ConfigDataUploadService;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class ConfigDataUploadController {

  private final ConfigDataUploadService configDataUploadService;

  private static final Logger logger = LoggerFactory.getLogger(ConfigDataUploadController.class);

  @PostMapping("/v1/{moduleName}/process-request")
  public ResponseEntity<BaseResponse<GenericUploadResponse>> uploadConfigData(
      @PathVariable String moduleName, @RequestBody GenericUploadRequest uploadRequest)
      throws CommonServiceException, JobSubmissionException, IOException, CsvException {

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Config data uploaded successfully!")
            .payload(configDataUploadService.processUploadConfigData(moduleName, uploadRequest))
            .build());
  }
}
