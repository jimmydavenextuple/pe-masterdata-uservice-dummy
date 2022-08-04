package com.hbc.csvdownload.controller;

import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.common.pojo.TemplateTypes;
import com.hbc.csvdownload.exception.InvalidTemplateTypeException;
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


  @GetMapping(value = "/carrier-services/{carrierServiceId}/transit-times/download")
  public ResponseEntity<BaseResponse<String>> downloadTransitTimesDataCSV(HttpServletRequest request, HttpServletResponse response) {

    String msg = "Download transit times request registered successfully";
    return ResponseEntity.ok().body(BaseResponse.builder().message(msg).build());
  }

  @PostMapping(
      path = "/upload/transit-times",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<String>> uploadTransitTimesCSV(
      @RequestParam(required = true) MultipartFile csvFile) {
    log.debug("--Inside uploadTransitTimesCSV API--");
    String msg = "Job to upload transit times submitted successfully";
    return ResponseEntity.ok().body(BaseResponse.builder().message(msg).build());
  }

  @PostMapping(
      path = "/upload/processing-lead-times",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<String>> uploadLeadProcessingTimeCSV(
      @RequestParam(required = true) MultipartFile csvFile) {
    log.debug("--Inside uploadLeadProcessingTimeCSV API--");
    String msg = "Job to upload processing lead times is submitted successfully";
    return ResponseEntity.ok().body(BaseResponse.builder().message(msg).build());
  }
}
