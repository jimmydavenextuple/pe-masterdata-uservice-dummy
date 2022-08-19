package com.hbc.csvdownload.controller;

import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.common.pojo.TemplateTypes;
import com.hbc.csvdownload.exception.InvalidTemplateTypeException;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class CsvUtilityController {

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

  @GetMapping(value = "/org/{orgId}/download/carrier-services/{carrierServiceId}/transit-time")
  public void downloadTransitTimesDataCSV(
      @PathVariable String orgId,
      @RequestParam String region1,
      @RequestParam String region2,
      HttpServletRequest request, HttpServletResponse response)
      throws InvalidTemplateTypeException {
    log.debug("Inside download transit times data as csv");
    downloadCSVTemplate("transitTime",request,response);
  }

  @PostMapping(
      path = "/org/{orgId}/upload/transit-times",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<String>> uploadTransitTimesCSV(
      @PathVariable String orgId,
      @RequestParam MultipartFile csvFile) {
    log.debug("--Inside uploadTransitTimesCSV API--");
    String msg = "Job to upload transit times submitted successfully";
    return ResponseEntity.ok().body(BaseResponse.builder().message(msg).build());
  }

  @PostMapping(
      path = "/org/{orgId}/upload/processing-lead-times",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<String>> uploadLeadProcessingTimeCSV(
      @PathVariable String orgId,
      @RequestParam MultipartFile csvFile) {
    log.debug("--Inside uploadLeadProcessingTimeCSV API--");
    String msg = "Job to upload processing lead times submitted successfully";
    return ResponseEntity.ok().body(BaseResponse.builder().message(msg).build());
  }

  @GetMapping(
      path = "/org/{orgId}/jobs/{jobId}/download")
  public void downloadErrorLogsByFilters(
      @PathVariable String orgId,
      @PathVariable String jobId,
      @RequestParam(required = false) Optional<String> status,
      HttpServletRequest request, HttpServletResponse response)
      throws InvalidTemplateTypeException {
    downloadCSVTemplate("transitTime", request, response);
  }
}
