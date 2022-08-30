package com.hbc.csvdownload.controller;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class CsvUploadUtilityController {

  private static final Logger logger = LoggerFactory.getLogger(CsvUploadUtilityController.class);

  @PostMapping(
      path = "/org/{orgId}/upload/transit-times",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<String>> uploadTransitTimesCSV(
      @PathVariable String orgId, @RequestParam MultipartFile csvFile) {
    logger.debug("--Inside uploadTransitTimesCSV API--");
    // need to add service layer logic
    var msg = "Job to upload transit times submitted successfully";
    return ResponseEntity.ok().body(BaseResponse.builder().message(msg).build());
  }

  @PostMapping(
      path = "/org/{orgId}/upload/processing-lead-times",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<String>> uploadLeadProcessingTimeCSV(
      @PathVariable String orgId, @RequestParam MultipartFile csvFile) {
    logger.debug("--Inside uploadLeadProcessingTimeCSV API--");
    // need to add service layer logic
    var msg = "Job to upload processing lead times submitted successfully";
    return ResponseEntity.ok().body(BaseResponse.builder().message(msg).build());
  }
}
