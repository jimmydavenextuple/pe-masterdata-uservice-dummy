package com.hbc.dataupload.controller;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.service.PromiseSourcingRuleDataUploadService;
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
public class PromiseSourcingRuleDataUploadController {
  private final PromiseSourcingRuleDataUploadService promiseSourcingRuleDataUploadService;

  @PostMapping("/promise-sourcing-rule")
  public ResponseEntity<BaseResponse<String>> uploadPromiseSourcingRuleData(
      @NotBlank @RequestParam String fileUri) throws IOException, CommonServiceException {
    log.debug("Processing upload Promise Sourcing Rule Data request");
    try {
      return promiseSourcingRuleDataUploadService.uploadPromiseSourcingRuleData(fileUri);
    } catch (Exception e) {
      log.error(String.valueOf(e), "Failed to process upload Promise Sourcing Rule Data request!");
      throw e;
    }
  }
}
