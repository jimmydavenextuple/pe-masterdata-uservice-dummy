package com.hbc.csvdownload.controller;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.csvdownload.common.pojo.TemplateTypes;
import com.hbc.csvdownload.exception.CarrierServiceException;
import com.hbc.csvdownload.exception.CsvDownloadUtilityServiceException;
import com.hbc.csvdownload.exception.InvalidTemplateTypeException;
import com.hbc.csvdownload.exception.PostalCodeTimezoneServiceException;
import com.hbc.csvdownload.exception.TransitServiceException;
import com.hbc.csvdownload.service.CsvDownloadUtilityService;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Slf4j
@RequiredArgsConstructor
public class CsvDownloadUtilityController {

  private final CsvDownloadUtilityService csvDownloadUtilityService;

  @GetMapping(value = "/{templateType}/download", produces = "text/csv")
  public void downloadCSVTemplate(
      @NotBlank(message = "templateType can't be empty") @PathVariable String templateType,
      HttpServletRequest request,
      HttpServletResponse response)
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
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
      @NotBlank(message = "carrierServiceId can't be empty") @PathVariable String carrierServiceId,
      @NotBlank(message = "sourceRegion can't be empty") @RequestParam String sourceRegion,
      @NotBlank(message = "destinationRegion can't be empty") @RequestParam
          String destinationRegion,
      HttpServletRequest request,
      HttpServletResponse response)
      throws TransitServiceException, PostalCodeTimezoneServiceException, IOException,
          CsvDownloadUtilityServiceException {
    log.debug("Inside download transit times data as csv");
    String csvContents =
        csvDownloadUtilityService.downloadTransitTimesForSourceAndDestinationRegion(
            orgId, carrierServiceId, sourceRegion, destinationRegion);
    response.setStatus(HttpStatus.OK.value());
    response.setContentLength(csvContents.length());
    response.getOutputStream().write(csvContents.getBytes());
    response.flushBuffer();
  }

  @GetMapping(value = "/org/{orgId}/download/market-regions")
  public void downloadMarketRegionDataCSV(
      @PathVariable String orgId,
      @RequestParam String country,
      HttpServletRequest request,
      HttpServletResponse response)
      throws PostalCodeTimezoneServiceException, IOException {
    log.debug("Inside download market region data as csv");
    String csvContents =
        csvDownloadUtilityService.downloadMarketRegionForOrgIdAndCountry(orgId, country);
    response.setStatus(HttpStatus.OK.value());
    response.setContentLength(csvContents.length());
    response.getOutputStream().write(csvContents.getBytes());
    response.flushBuffer();
  }

  @GetMapping(value = "/org/{orgId}/download/carrier-services")
  public void downloadCarrierServiceCSV(
      @PathVariable String orgId, HttpServletRequest request, HttpServletResponse response)
      throws IOException, CarrierServiceException {
    log.debug("Inside download carrier service data as csv");

    var file = csvDownloadUtilityService.downloadCarrierServiceDataCSV(orgId);
    try (var inputStream = new FileInputStream(file)) {
      response.setStatus(HttpStatus.OK.value());
      response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
      response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));
      IOUtils.copy(inputStream, response.getOutputStream());
      response.flushBuffer();
      file.delete(); // NOSONAR
    }
  }

  @GetMapping(path = "/org/{orgId}/jobs/{jobId}/download")
  public void downloadLogsByFilters(
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
      @NotBlank(message = "jobId can't be empty") @PathVariable String jobId,
      @RequestParam(required = false) Optional<String> status,
      HttpServletRequest request,
      HttpServletResponse response)
      throws IOException, CommonServiceException {
    log.debug("Inside download logs by filters");
    String csvContent = csvDownloadUtilityService.downloadLogsAsCsv(jobId, orgId, status);
    response.setStatus(HttpStatus.OK.value());
    response.setContentLength(csvContent.length());
    response.getOutputStream().write(csvContent.getBytes());
    response.flushBuffer();
  }
}
