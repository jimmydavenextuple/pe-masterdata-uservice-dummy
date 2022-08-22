package com.hbc.csvdownload.controller;

import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.exception.CsvFormatValidationFailedException;
import com.hbc.csvdownload.exception.CsvParsingException;
import com.hbc.csvdownload.exception.JobServiceException;
import com.hbc.csvdownload.exception.JobSubmissionException;
import com.hbc.csvdownload.exception.JobUpdationException;
import com.hbc.csvdownload.exception.JsonParsingException;
import com.hbc.csvdownload.service.CsvUploadUtilityService;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CsvUploadutilityController {

  private final CsvUploadUtilityService csvUploadUtilityService;

  @PostMapping(
      path = "org/{orgId}/upload/processing-lead-times",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<String>> uploadProcessingLeadTimes(
      @PathVariable String orgId, @RequestParam MultipartFile csvFile)
      throws CsvParsingException, CsvFormatValidationFailedException, JobSubmissionException,
          JsonParsingException {
    log.debug("-- Inside upload processing lead times --");

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Bulk upload of processing lead times uploaded successfully!")
            .payload(csvUploadUtilityService.uploadProcessingLeadTimesCsv(orgId, csvFile))
            .build());
  }

  @PostMapping(
      path = "/org/{orgId}/upload/transit-times",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<String>> uploadTransitTimes(
      @PathVariable String orgId, @RequestBody MultipartFile csvFile)
      throws CsvException, IOException, CsvFormatValidationFailedException, JobServiceException,
          JobUpdationException, JsonParsingException {
    log.debug("-- Inside upload transit times --");

    return ResponseEntity.ok(
        BaseResponse.builder()
            .payload(csvUploadUtilityService.uploadTransitTimesCsv(orgId, csvFile))
            .build());
  }
}
