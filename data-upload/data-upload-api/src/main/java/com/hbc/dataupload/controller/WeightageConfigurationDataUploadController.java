package com.hbc.dataupload.controller;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.service.WeightageConfigurationDataUploadService;
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
public class WeightageConfigurationDataUploadController {
  private final WeightageConfigurationDataUploadService weightageConfigurationDataUploadService;

  @PostMapping("/weightage")
  public ResponseEntity<BaseResponse<String>> uploadWeightageConfigurationData(
      @NotBlank @RequestParam String fileUri) throws IOException, CommonServiceException {
    log.info("Processing upload Weightage Configuration Data request");
    try {
      return weightageConfigurationDataUploadService.uploadWeightageConfigurationData(fileUri);
    } catch (Exception e) {
      log.error(
          String.valueOf(e), "Failed to process upload Weightage Configuration Data request!");
      throw e;
    }
  }
}
