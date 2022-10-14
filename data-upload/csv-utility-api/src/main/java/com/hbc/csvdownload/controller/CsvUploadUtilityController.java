package com.hbc.csvdownload.controller;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.exception.CsvFormatValidationFailedException;
import com.hbc.csvdownload.exception.CsvParsingException;
import com.hbc.csvdownload.exception.JobSubmissionException;
import com.hbc.csvdownload.service.CsvUploadUtilityService;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequiredArgsConstructor
public class CsvUploadUtilityController {

  private final CsvUploadUtilityService csvUploadUtilityService;

  private static final Logger logger = LoggerFactory.getLogger(CsvUploadUtilityController.class);

  @PostMapping(
      path = "/org/{orgId}/upload/transit-times",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<String>> uploadTransitTimes(
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
      @RequestBody MultipartFile csvFile)
      throws CsvException, IOException, CsvFormatValidationFailedException, JobSubmissionException {
    logger.debug("-- Inside upload transit times --");

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Bulk upload of transit times uploaded successfully!")
            .payload(csvUploadUtilityService.uploadTransitTimesCsv(orgId, csvFile))
            .build());
  }

  @PostMapping(
      path = "/org/{orgId}/upload/processing-lead-times",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<String>> uploadProcessingLeadTimes(
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
      @RequestParam MultipartFile csvFile)
      throws CsvParsingException, CsvFormatValidationFailedException, JobSubmissionException,
          IOException, CsvException {
    logger.debug("-- Inside upload processing lead times --");

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Bulk upload of processing lead times uploaded successfully!")
            .payload(csvUploadUtilityService.uploadProcessingLeadTimesCsv(orgId, csvFile))
            .build());
  }
}
