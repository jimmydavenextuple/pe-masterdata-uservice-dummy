package com.hbc.dataupload.controller;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.service.PostalCodeTimezoneDataUploadService;
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
public class PostalCodeTimezoneDataUploadController {
  private final PostalCodeTimezoneDataUploadService postalCodeTimezoneDataUploadUtilityService;

  @PostMapping("/postal-code-timezone")
  public ResponseEntity<BaseResponse<String>> uploadPostalCodeTimezoneData(
      @NotBlank @RequestParam String fileUri) throws IOException, CommonServiceException {
    log.info("Processing upload Postal Code Timezone Data request");
    try {
      return postalCodeTimezoneDataUploadUtilityService.uploadPostalCodeTimezoneData(fileUri);
    } catch (Exception e) {
      log.error(String.valueOf(e), "Failed to process upload Postal Code Timezone Data request!");
      throw e;
    }
  }
}
