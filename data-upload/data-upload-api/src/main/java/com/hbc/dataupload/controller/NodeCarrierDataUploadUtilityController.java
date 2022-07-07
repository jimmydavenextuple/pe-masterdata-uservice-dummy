package com.hbc.dataupload.controller;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.service.NodeCarrierDataUploadUtilityService;

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
public class NodeCarrierDataUploadUtilityController {
  private final NodeCarrierDataUploadUtilityService nodeCarrierDataUploadUtilityService;

  @PostMapping("/node/carrier")
  public ResponseEntity<BaseResponse<String>> uploadNodeCarrierData(
      @NotBlank @RequestParam String fileUri) throws IOException, CommonServiceException {
    log.debug("Processing upload Node Carrier Data request");
    try {
      return nodeCarrierDataUploadUtilityService.uploadNodeCarrierData(fileUri);
    } catch (Exception e) {
      log.error(String.valueOf(e), "Failed to process upload Node Carrier Data request!");
      throw e;
    }
  }
}
