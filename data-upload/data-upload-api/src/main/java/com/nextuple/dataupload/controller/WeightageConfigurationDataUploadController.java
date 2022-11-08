package com.nextuple.dataupload.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.service.WeightageConfigurationDataUploadService;
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
public class WeightageConfigurationDataUploadController {
  private final WeightageConfigurationDataUploadService weightageConfigurationDataUploadService;

  @PostMapping("/weightage")
  public ResponseEntity<BaseResponse<String>> uploadWeightageConfigurationData(
      @NotBlank(message = "fileUri can't be empty") @RequestParam String fileUri)
      throws IOException, CommonServiceException {
    log.debug("Processing upload Weightage Configuration Data request");
    try {
      return weightageConfigurationDataUploadService.uploadWeightageConfigurationData(fileUri);
    } catch (Exception e) {
      log.error(
          String.valueOf(e), "Failed to process upload Weightage Configuration Data request!");
      throw e;
    }
  }
}
