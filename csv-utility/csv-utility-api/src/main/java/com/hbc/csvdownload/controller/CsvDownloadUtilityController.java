package com.hbc.csvdownload.controller;

import com.hbc.csvdownload.common.pojo.TemplateTypes;
import com.hbc.csvdownload.exception.InvalidTemplateTypeException;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CsvDownloadUtilityController {

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
      @PathVariable String carrierServiceId,
      @RequestParam String sourceRegion,
      @RequestParam String destinationRegion,
      HttpServletRequest request,
      HttpServletResponse response)
      throws InvalidTemplateTypeException {
    log.debug("Inside download transit times data as csv");
    /** TODO need to add service layer logic */
    downloadCSVTemplate("transitTime", request, response);
  }

  @GetMapping(path = "/org/{orgId}/jobs/{jobId}/download")
  public void downloadLogsByFilters(
      @PathVariable String orgId,
      @PathVariable String jobId,
      @RequestParam(required = false) Optional<String> status,
      HttpServletRequest request,
      HttpServletResponse response)
      throws InvalidTemplateTypeException {
    log.debug("Inside download logs by filters");
    /** TODO need to add service layer logic */
    downloadCSVTemplate("transitTime", request, response);
  }
}
