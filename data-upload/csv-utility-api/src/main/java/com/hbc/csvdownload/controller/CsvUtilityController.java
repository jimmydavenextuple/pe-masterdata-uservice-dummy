package com.hbc.csvdownload.controller;

import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.common.util.TemplateTypes;
import com.hbc.csvdownload.exception.CsvFormatValidationFailedException;
import com.hbc.csvdownload.exception.CsvParsingException;
import com.hbc.csvdownload.exception.InvalidTemplateTypeException;
import com.hbc.csvdownload.exception.JobSubmissionException;
import com.hbc.csvdownload.exception.JsonParsingException;
import com.hbc.csvdownload.service.CsvUploadUtilityService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CsvUtilityController {

  private final CsvUploadUtilityService csvUploadUtilityService;

  @GetMapping(value = "/{templateType}/download", produces = "text/csv")
  public void downloadCSVTemplate(
      @PathVariable String templateType, HttpServletRequest request, HttpServletResponse response)
      throws InvalidTemplateTypeException {
    log.debug("Inside downloadCSVTemplate for type: {}", templateType);

    String templateData = TemplateTypes.getTemplateData(templateType);
    if (ObjectUtils.isEmpty(templateData)) {
      throw new InvalidTemplateTypeException("Invalid template type", templateType);
    }

    try {
      response.setStatus(HttpStatus.OK.value());
      response.setContentLength(templateData.length());
      response.getOutputStream().write(templateData.getBytes());
      response.flushBuffer();
    } catch (Exception e) {
      log.error("Error while downloading the csv template", e);
      response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  @PostMapping(
      path = "org/{orgId}/upload/processing-lead-times",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<String>> uploadProcessingLeadTimes(
      @PathVariable String orgId, @RequestParam MultipartFile csvFile)
      throws CsvParsingException, CsvFormatValidationFailedException, JobSubmissionException,
          JsonParsingException {
    log.debug("-- Inside upload processing lead times --");

    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Bulk upload of processing lead times uploaded successfully!")
              .payload(csvUploadUtilityService.uploadProcessingLeadTimesCsv(orgId, csvFile))
              .build());
    } catch (Exception e) {
      log.error("Error while performing bulk upload of processing lead times");
      throw e;
    }
  }
}
