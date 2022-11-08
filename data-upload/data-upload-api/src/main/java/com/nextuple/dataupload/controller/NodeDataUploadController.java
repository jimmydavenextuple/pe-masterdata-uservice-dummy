package com.nextuple.dataupload.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.service.NodeDataUploadService;
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
public class NodeDataUploadController {
  private final NodeDataUploadService nodeDataUploadService;

  @PostMapping("/node")
  public ResponseEntity<BaseResponse<String>> uploadNodeData(
      @NotBlank(message = "fileUri can't be empty") @RequestParam String fileUri)
      throws IOException, CommonServiceException {
    log.debug("Processing upload Node Data request");
    try {
      return nodeDataUploadService.uploadNodeData(fileUri);
    } catch (Exception e) {
      log.error(String.valueOf(e), "Failed to process upload Node Data request!");
      throw e;
    }
  }
}
