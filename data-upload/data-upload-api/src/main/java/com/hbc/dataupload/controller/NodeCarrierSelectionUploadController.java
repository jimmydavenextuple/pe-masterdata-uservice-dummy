package com.hbc.dataupload.controller;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.service.NodeCarrierSelectionUploadService;
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
public class NodeCarrierSelectionUploadController {
  private final NodeCarrierSelectionUploadService nodeCarrierSelectionUploadService;

  @PostMapping("/node-carrier-selection-upload")
  public ResponseEntity<BaseResponse<String>> nodeCarrierSelectionUpload(
      @NotBlank @RequestParam String fileUri) throws IOException, CommonServiceException {
    log.debug("Processing node service selection upload request");
    return nodeCarrierSelectionUploadService.nodeCarrierSelectionUpload(fileUri);
  }
}
