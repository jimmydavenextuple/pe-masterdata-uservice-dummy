/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.PreSignedUrlResponse;
import com.nextuple.csvdownload.common.pojo.DownloadNodeCarrierServiceAndServiceOptionPojo;
import com.nextuple.csvdownload.common.pojo.TemplateTypes;
import com.nextuple.csvdownload.controller.docs.DownloadCostDefinitionDoc;
import com.nextuple.csvdownload.controller.docs.DownloadCsvTemplateDoc;
import com.nextuple.csvdownload.controller.docs.DownloadCsvTemplateFileDoc;
import com.nextuple.csvdownload.controller.docs.DownloadCustomRegionsByOrgIdAndRegionId;
import com.nextuple.csvdownload.controller.docs.DownloadCustomRegionsCSVDoc;
import com.nextuple.csvdownload.controller.docs.DownloadLogsDoc;
import com.nextuple.csvdownload.controller.docs.DownloadMarketRegionDataDoc;
import com.nextuple.csvdownload.controller.docs.DownloadNodeAndServiceOptionsDoc;
import com.nextuple.csvdownload.controller.docs.DownloadNodeCarrierServiceAndServiceOptionDoc;
import com.nextuple.csvdownload.controller.docs.DownloadNodeCarrierServicePickupCalendar;
import com.nextuple.csvdownload.controller.docs.DownloadNodeDataCSVDoc;
import com.nextuple.csvdownload.controller.docs.DownloadProcessingTimeBufferDoc;
import com.nextuple.csvdownload.controller.docs.DownloadTransitBufferDoc;
import com.nextuple.csvdownload.controller.docs.DownloadTransitTimeDataDoc;
import com.nextuple.csvdownload.exception.CarrierServiceException;
import com.nextuple.csvdownload.exception.CsvDownloadUtilityServiceException;
import com.nextuple.csvdownload.exception.CustomRegionServiceException;
import com.nextuple.csvdownload.exception.InvalidTemplateTypeException;
import com.nextuple.csvdownload.exception.PostalCodeTimezoneServiceException;
import com.nextuple.csvdownload.exception.TransitServiceException;
import com.nextuple.csvdownload.service.CsvDownloadUtilityService;
import com.nextuple.csvdownload.service.DownloadTemplateService;
import com.nextuple.csvdownload.util.CsvUtil;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffUIRequest;
import com.nextuple.sourcing.cost.config.inbound.CostDefinitionRequest;
import com.nextuple.transit.domain.inbound.FetchTransferScheduleRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for CSV download utility APIs.
 *
 * <p>This controller provides APIs to download various types of CSV data, such as templates,
 * transit times, market regions, carrier services, nodes, and cost definitions.
 *
 * <p>The controller is tagged with "Csv Download Utility APIs" for easy categorization in API
 * documentation.
 */
@Validated
@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Csv Download Utility APIs ")
public class CsvDownloadUtilityController {

  private final CsvDownloadUtilityService csvDownloadUtilityService;
  private final DownloadTemplateService downloadTemplateService;

  /**
   * Downloads the CSV template for the specified template type.
   *
   * <p>This method processes a GET request to provide a CSV template based on the specified
   * template type. If the template type is invalid, an exception is thrown.
   *
   * @param templateType The type of the CSV template (e.g., "edd-computation").
   * @param request The HTTP request.
   * @param response The HTTP response.
   * @throws InvalidTemplateTypeException If the provided template type is invalid.
   */
  @GetMapping(value = "/{templateType}/download", produces = "text/csv")
  @DownloadCsvTemplateDoc
  public void downloadCSVTemplate(
      @NotBlank(message = "templateType can't be empty")
          @PathVariable
          @Parameter(description = "Type of the CSV template, eg edd-computation.")
          String templateType,
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

  /**
   * Downloads the CSV template for the specified template type and organization.
   *
   * <p>This method processes a GET request to provide a CSV template for a given template type and
   * organization, retrieving the template from a file.
   *
   * @param templateType The type of the CSV template (e.g., "edd-computation").
   * @param orgId The unique identifier for the organization.
   * @param request The HTTP request.
   * @param response The HTTP response.
   * @throws InvalidTemplateTypeException If the provided template type is invalid.
   * @throws IOException If an error occurs during file handling.
   * @throws CommonServiceException If an error occurs while processing the request.
   */
  @GetMapping(value = "/v1/{orgId}/{templateType}/download", produces = "text/csv")
  @DownloadCsvTemplateFileDoc
  public void downloadCSVTemplateFromFile(
      @NotBlank(message = "templateType can't be empty")
          @PathVariable
          @Parameter(description = "Type of the CSV template, eg edd-computation.")
          String templateType,
      @PathVariable @Parameter(description = "Unique identifier for organization ID.") String orgId,
      HttpServletRequest request,
      HttpServletResponse response)
      throws InvalidTemplateTypeException, IOException, CommonServiceException {
    log.debug("Inside downloadCSVTemplate for type: {}", templateType);
    try (var templateDataStream = downloadTemplateService.getTemplateData(templateType, orgId)) {

      String templateData =
          new String(templateDataStream.toByteArray(), StandardCharsets.UTF_8).replaceAll("\r", "");

      response.setStatus(HttpStatus.OK.value());
      response.setContentLength(templateData.length());
      response.getOutputStream().write(templateData.getBytes());
      response.flushBuffer();
    } catch (Exception e) {
      log.error("Error while downloading the csv template", e);
      response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
      throw (e);
    }
  }

  /**
   * Downloads the transit times data for the specified organization, carrier service, and regions.
   *
   * <p>This method processes a GET request to download transit times data as a CSV for a specific
   * organization, carrier service, source region, and destination region.
   *
   * @param orgId The unique identifier for the organization.
   * @param carrierServiceId The unique identifier for the carrier service.
   * @param sourceRegion The unique identifier for the source region.
   * @param destinationRegion The unique identifier for the destination region.
   * @param request The HTTP request.
   * @param response The HTTP response.
   * @throws TransitServiceException If there is an error during the transit service process.
   * @throws PostalCodeTimezoneServiceException If there is an error during the timezone service
   *     process.
   * @throws IOException If there is an error while writing the response.
   * @throws CsvDownloadUtilityServiceException If there is an error during the CSV download
   *     process.
   */
  @GetMapping(value = "/org/{orgId}/download/carrier-services/{carrierServiceId}/transit-time")
  @DownloadTransitTimeDataDoc
  public void downloadTransitTimesDataCSV(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organization ID.")
          String orgId,
      @NotBlank(message = "carrierServiceId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for carrier service.")
          String carrierServiceId,
      @NotBlank(message = "sourceRegion can't be empty")
          @RequestParam
          @Parameter(description = "Unique identifier for source geoZone.")
          String sourceRegion,
      @NotBlank(message = "destinationRegion can't be empty")
          @RequestParam
          @Parameter(description = "Unique identifier for destination geoZone.")
          String destinationRegion,
      HttpServletRequest request,
      HttpServletResponse response)
      throws TransitServiceException,
          PostalCodeTimezoneServiceException,
          IOException,
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

  /**
   * Downloads the market region data for the specified organization and country.
   *
   * <p>This method processes a GET request to download market region data as a CSV for a specific
   * organization and country.
   *
   * @param orgId The unique identifier for the organization.
   * @param country The unique identifier for the country.
   * @param request The HTTP request.
   * @param response The HTTP response.
   * @throws PostalCodeTimezoneServiceException If there is an error during the timezone service
   *     process.
   * @throws IOException If there is an error while writing the response.
   */
  @GetMapping(value = "/org/{orgId}/download/market-regions")
  @DownloadMarketRegionDataDoc
  public void downloadMarketRegionDataCSV(
      @PathVariable @Parameter(description = "Unique identifier for organization ID.") String orgId,
      @RequestParam @Parameter(description = "Unique identifier for country.") String country,
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

  /**
   * Downloads the processing time buffer data for the specified organization and optional node IDs.
   *
   * <p>This method processes a GET request to download processing time buffer data as a CSV for a
   * specific organization, with an optional parameter for node IDs.
   *
   * @param orgId The unique identifier for the organization.
   * @param nodeIds Optional comma-separated string containing node IDs to be searched for.
   * @param response The HTTP response.
   * @throws IOException If there is an error while writing the response.
   */
  @GetMapping(value = "/org/{orgId}/download/processing-time-buffers")
  @DownloadProcessingTimeBufferDoc
  public void downloadProcessingTimeBufferDataCSV(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organization ID.")
          String orgId,
      @RequestParam(required = false)
          @Parameter(
              description =
                  "Comma separated string that contains references of the nodes to be searched for.")
          String nodeIds,
      HttpServletResponse response)
      throws IOException {
    log.debug("Inside download processing time buffers data as csv");
    final var file = csvDownloadUtilityService.downloadProcessingTimeBuffersByOrgId(orgId, nodeIds);
    try (var inputStream = new FileInputStream(file)) {
      response.setStatus(HttpStatus.OK.value());
      response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
      response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));
      IOUtils.copy(inputStream, response.getOutputStream());
      response.flushBuffer();
    } finally {
      Files.delete(file.toPath());
    }
  }

  /**
   * Downloads the carrier service data for the specified organization.
   *
   * <p>This method processes a GET request to download carrier service data as a CSV for a specific
   * organization.
   *
   * @param orgId The unique identifier for the organization.
   * @param request The HTTP request.
   * @param response The HTTP response.
   * @throws IOException If there is an error while writing the response.
   * @throws CarrierServiceException If there is an error while processing the carrier service
   *     request.
   */
  @GetMapping(value = "/org/{orgId}/download/carrier-services")
  public void downloadCarrierServiceCSV(
      @PathVariable @Parameter(description = "Unique identifier for organization ID.") String orgId,
      HttpServletRequest request,
      HttpServletResponse response)
      throws IOException, CarrierServiceException {
    log.debug("Inside download carrier service data as csv");

    var file = csvDownloadUtilityService.downloadCarrierServiceDataCSV(orgId);
    try (var inputStream = new FileInputStream(file)) {

      response.setStatus(HttpStatus.OK.value());
      response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
      response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));
      IOUtils.copy(inputStream, response.getOutputStream());
      response.flushBuffer();
    } finally {
      file.delete(); // NOSONAR
    }
  }

  /**
   * Downloads the logs for the specified job in CSV format, with optional filters for job status.
   *
   * <p>This method processes a GET request to download logs as a CSV for a specific job and
   * organization, with an optional filter for job status.
   *
   * @param orgId The unique identifier for the organization.
   * @param jobId The unique identifier for the job.
   * @param status Optional parameter specifying the status of the job.
   * @param request The HTTP request.
   * @param response The HTTP response.
   * @throws IOException If there is an error while writing the response.
   * @throws CommonServiceException If there is an error while processing the request.
   */
  @GetMapping(path = "/org/{orgId}/jobs/{jobId}/download")
  @DownloadLogsDoc
  public void downloadLogsByFilters(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organization ID.")
          String orgId,
      @NotBlank(message = "jobId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for job.")
          String jobId,
      @RequestParam(required = false) @Parameter(description = "Status of the job.")
          Optional<String> status,
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

  /**
   * Downloads the logs for the specified job in CSV format (version 1), with optional filters for
   * job status.
   *
   * <p>This method processes a GET request to download logs as a CSV for a specific job and
   * organization, with an optional filter for job status (version 1 of the download).
   *
   * @param orgId The unique identifier for the organization.
   * @param jobId The unique identifier for the job.
   * @param status Optional parameter specifying the status of the job.
   * @param request The HTTP request.
   * @param httpServletResponse The HTTP response.
   * @throws IOException If there is an error while writing the response.
   * @throws CommonServiceException If there is an error while processing the request.
   */
  @GetMapping(path = "/v1/org/{orgId}/jobs/{jobId}/download")
  @DownloadLogsDoc
  public ResponseEntity<BaseResponse<PreSignedUrlResponse>> downloadLogsByFiltersV1(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organization ID.")
          String orgId,
      @NotBlank(message = "jobId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for job.")
          String jobId,
      @RequestParam(required = false) @Parameter(description = "Status of the job.")
          Optional<String> status,
      HttpServletRequest request,
      HttpServletResponse httpServletResponse)
      throws IOException, CommonServiceException {
    log.debug("Inside download logs by filters");
    final var file = csvDownloadUtilityService.downloadLogsAsCsvV1(jobId, orgId, status);
    return ResponseEntity.ok()
        .body(
            BaseResponse.builder()
                .payload(file)
                .message("Log history URL fetched successfully")
                .build());
  }

  /**
   * Downloads the logs for the specified job in CSV format (version 2), with optional filters for
   * job status and origin.
   *
   * <p>This method processes a GET request to download logs as a CSV for a specific job and
   * organization, with an optional filter for job status and record origin (version 2 of the
   * download).
   *
   * @param orgId The unique identifier for the organization.
   * @param jobId The unique identifier for the job.
   * @param status Optional parameter specifying the status of the job.
   * @param origin The origin of the record.
   * @throws CommonServiceException If there is an error while processing the request.
   */
  @GetMapping(path = "/v2/org/{orgId}/jobs/{jobId}/download")
  @DownloadLogsDoc
  public ResponseEntity<BaseResponse<PreSignedUrlResponse>> downloadLogsByFiltersV2(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organization ID.")
          String orgId,
      @NotBlank(message = "jobId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for job.")
          String jobId,
      @RequestParam(required = false) @Parameter(description = "Status of the job.")
          Optional<String> status,
      @RequestParam(required = true, defaultValue = "PE")
          @Parameter(description = "Origin of the record.")
          String origin)
      throws CommonServiceException {
    log.debug("Inside download logs by filters");
    final var file = csvDownloadUtilityService.downloadLogsAsCsvV2(jobId, orgId, status, origin);
    return ResponseEntity.ok()
        .body(
            BaseResponse.builder()
                .payload(file)
                .message("Log history URL fetched successfully")
                .build());
  }

  /**
   * Downloads the node carrier service and service options data as a CSV for the specified
   * organization.
   *
   * <p>This method processes a GET request to download node carrier service and service options
   * data as CSV for a specific organization.
   *
   * @param orgId The unique identifier for the organization.
   * @param request The HTTP request.
   * @param response The HTTP response.
   * @throws IOException If there is an error while writing the response.
   */
  @GetMapping(value = "/org/{orgId}/download/node-carrier-service-option")
  @DownloadNodeCarrierServiceAndServiceOptionDoc
  public void downloadNodeCarrierServiceAndServiceOptionsDataCSV(
      @PathVariable @Parameter(description = "Unique identifier for organization ID.") String orgId,
      HttpServletRequest request,
      HttpServletResponse response)
      throws IOException {
    log.debug("Processing download node carrier-service and service-options data CSV");

    DownloadNodeCarrierServiceAndServiceOptionPojo pojo =
        csvDownloadUtilityService.downloadNodeCarrierServiceAndServiceOptionsDataCSV(orgId);
    response.setStatus(HttpStatus.OK.value());
    response.setContentLength(Math.toIntExact(pojo.getContentsLength()));
    response.getOutputStream().write(pojo.getFileContents());
    response.flushBuffer();
  }

  /**
   * Downloads the node and service options data as a CSV for the specified organization.
   *
   * <p>This method processes a GET request to download node and service options data as CSV for a
   * specific organization.
   *
   * @param orgId The unique identifier for the organization.
   * @param response The HTTP response.
   * @throws IOException If there is an error while writing the response.
   */
  @GetMapping(value = "/org/{orgId}/download/node-service-option")
  @DownloadNodeAndServiceOptionsDoc
  public void downloadNodeAndServiceOptionsDataCSV(
      @PathVariable @Parameter(description = "Unique identifier for organization ID.") String orgId,
      HttpServletResponse response)
      throws IOException {
    log.debug("Processing download node and service-options data CSV");

    DownloadNodeCarrierServiceAndServiceOptionPojo pojo =
        csvDownloadUtilityService.downloadNodeAndServiceOptionsDataCSV(orgId);
    response.setStatus(HttpStatus.OK.value());
    response.setContentLength(Math.toIntExact(pojo.getContentsLength()));
    response.getOutputStream().write(pojo.getFileContents());
    response.flushBuffer();
  }

  /**
   * Downloads the nodes data for the specified organization and optional node IDs and node types.
   *
   * <p>This method processes a GET request to download nodes data as a CSV for a specific
   * organization, with optional parameters for node IDs and node types.
   *
   * @param orgId The unique identifier for the organization.
   * @param nodeIds Optional comma-separated string containing node IDs.
   * @param nodeType Optional identifier for the type of node.
   * @param httpServletResponse The HTTP response.
   * @throws IOException If there is an error while writing the response.
   */
  @GetMapping(value = "/org/{orgId}/download/nodes")
  @DownloadNodeDataCSVDoc
  public void downloadNodesDataCSV(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organization ID.")
          String orgId,
      @RequestParam(required = false)
          @Parameter(
              description =
                  "Comma separated string that contains references of the nodes to be searched for.")
          String nodeIds,
      @RequestParam(required = false) @Parameter(description = "Identifier for type of node.")
          String nodeType,
      HttpServletResponse httpServletResponse)
      throws IOException, CommonServiceException {
    log.debug("Inside download nodes data as csv");
    final var file = csvDownloadUtilityService.downloadNodesByOrgId(orgId, nodeIds, nodeType);
    try (var inputStream = new FileInputStream(file)) {

      httpServletResponse.setStatus(HttpStatus.OK.value());
      httpServletResponse.setHeader(
          HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=%s".formatted(file.getName()));
      httpServletResponse.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));
      IOUtils.copy(inputStream, httpServletResponse.getOutputStream());
      httpServletResponse.flushBuffer();
    } finally {
      Files.delete(file.toPath());
    }
  }

  /**
   * Downloads the node carrier service pickup calendar data for a specific organization.
   *
   * <p>This endpoint processes a GET request to download all node carrier service pickup calendar
   * entries associated with the specified organization ID. The method generates a CSV file
   * containing pickup calendar details for all nodes and carrier services within the organization.
   *
   * @param orgId The unique identifier of the organization. Must not be blank. Example value:
   *     "NEXTUPLE"
   * @param httpServletResponse The HTTP servlet response used to write the CSV file data and set
   *     appropriate headers.
   * @throws IOException If there is an error in writing to the response output stream or handling
   *     the temporary file.
   * @throws CommonServiceException If there is an error in processing the request or accessing
   *     organization data.
   * @throws CarrierServiceException If there is an error in retrieving carrier service pickup
   *     calendar information.
   */
  @GetMapping(value = "/org/{orgId}/download/node-carrier-pickup-calendar")
  @DownloadNodeCarrierServicePickupCalendar
  public void downloadNodesCarrierServicePickupCalendarDataCSV(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for organization ID.")
          String orgId,
      HttpServletResponse httpServletResponse)
      throws IOException, CommonServiceException, CarrierServiceException {
    log.debug("Inside download nodes carrier pickup calendar data as csv");
    final var file = csvDownloadUtilityService.downloadNodesCarrierPickupCalendarByOrgId(orgId);
    try (var inputStream = new FileInputStream(file)) {
      httpServletResponse.setStatus(HttpStatus.OK.value());
      httpServletResponse.setHeader(
          HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=%s".formatted(file.getName()));
      httpServletResponse.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));
      IOUtils.copy(inputStream, httpServletResponse.getOutputStream());
      httpServletResponse.flushBuffer();
    } finally {
      Files.delete(file.toPath());
    }
  }

  /**
   * Downloads the transit buffer data for the specified transit buffer request ID.
   *
   * <p>This method processes a GET request to download transit buffer data as a CSV for a specific
   * transit buffer request.
   *
   * @param transitBufferConfigRequestId The unique identifier for the transit buffer request.
   * @param createdBy The name of the user who created the request.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the pre-signed URL for
   *     downloading the buffer.
   */
  @GetMapping(value = "/download/transitBuffer/{transitBufferConfigRequestId}")
  @DownloadTransitBufferDoc
  public ResponseEntity<BaseResponse<PreSignedUrlResponse>> downloadTransitBuffer(
      @PathVariable @Parameter(description = "Unique identifier for Transit Buffer .")
          Long transitBufferConfigRequestId,
      @NotBlank(message = "createdBy can't be empty")
          @RequestParam
          @Parameter(description = "Name of the user.")
          String createdBy) {
    log.debug("Inside download transit buffer data as csv");

    var response =
        csvDownloadUtilityService.downloadTransitBufferDetails(
            transitBufferConfigRequestId, createdBy);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit buffer details downloaded successfully")
            .payload(response)
            .build());
  }

  /**
   * Downloads the custom regions data for the specified organization.
   *
   * <p>This method processes a GET request to download custom regions data as a CSV for a specific
   * organization.
   *
   * @param orgId The unique identifier for the organization.
   * @param httpServletResponse The HTTP response.
   * @throws IOException If there is an error while writing the response.
   */
  @GetMapping(value = "/org/{orgId}/download/custom-regions")
  @DownloadCustomRegionsCSVDoc
  public void downloadCustomRegionsByOrgIdDataCSV(
      @PathVariable @Parameter(description = "Unique identifier for organization ID.") String orgId,
      HttpServletResponse httpServletResponse)
      throws IOException {
    log.debug("Inside download custom regions by orgId data as csv");
    final var file = csvDownloadUtilityService.downloadCustomRegionsForOrgId(orgId);
    try (var inputStream = new FileInputStream(file)) {

      httpServletResponse.setStatus(HttpStatus.OK.value());
      httpServletResponse.setHeader(
          HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=%s".formatted(file.getName()));
      httpServletResponse.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));
      IOUtils.copy(inputStream, httpServletResponse.getOutputStream());
      httpServletResponse.flushBuffer();
    } finally {
      Files.delete(file.toPath());
    }
  }

  /**
   * Downloads custom regions data for a specific organization and country as a CSV file.
   *
   * <p>This endpoint processes a GET request to download custom regions data filtered by
   * organization ID and country. The method supports additional filtering by region IDs and region
   * names through optional query parameters.
   *
   * @param orgId The unique identifier of the organization. Must not be blank. Example value:
   *     "NEXTUPLE"
   * @param country The country code to filter custom regions. Example value: "US"
   * @param regionIds Optional comma-separated list of custom region IDs to filter the results.
   *     Example value: "REGION1,REGION2"
   * @param regionNames Optional comma-separated list of custom region names to filter the results.
   *     Example value: "East Coast,West Coast"
   * @param httpServletResponse The HTTP servlet response used to write the CSV file data and set
   *     appropriate headers.
   * @throws IOException If there is an error in writing to the response output stream or handling
   *     the temporary file.
   */
  @GetMapping(value = "/org/{orgId}/country/{country}/download/custom-regions")
  @DownloadCustomRegionsCSVDoc
  public void downloadCustomRegionsByOrgIdAndCountryDataCSV(
      @PathVariable @Parameter(description = "Unique identifier for organization ID.") String orgId,
      @PathVariable @Parameter(description = "Identifier for Country.") String country,
      @RequestParam(required = false)
          @Parameter(description = "Comma Separated values for custom region ID(s)")
          String regionIds,
      @RequestParam(required = false)
          @Parameter(description = "Comma Separated values for custom region name(s)")
          String regionNames,
      HttpServletResponse httpServletResponse)
      throws IOException {
    final var file =
        csvDownloadUtilityService.downloadCustomRegionDetails(
            orgId, country, regionIds, regionNames);
    try (var inputStream = new FileInputStream(file)) {
      httpServletResponse.setStatus(HttpStatus.OK.value());
      httpServletResponse.setHeader(
          HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=%s".formatted(file.getName()));
      httpServletResponse.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));
      IOUtils.copy(inputStream, httpServletResponse.getOutputStream());
      httpServletResponse.flushBuffer();
    } finally {
      Files.delete(file.toPath());
    }
  }

  /**
   * Downloads the custom regions data for the specified organization and region.
   *
   * <p>This method processes a GET request to download custom regions data as a CSV for a specific
   * organization and region.
   *
   * @param orgId The unique identifier for the organization.
   * @param regionId The unique identifier for the region.
   * @param response The HTTP response.
   * @throws IOException If there is an error while writing the response.
   * @throws CustomRegionServiceException If there is an error with the custom region service.
   */
  @GetMapping(value = "/org/{orgId}/download/custom-region/regionId/{regionId}")
  @DownloadCustomRegionsByOrgIdAndRegionId
  public void downloadCustomRegionsByOrgIdAndRegionIdDataCSV(
      @PathVariable String orgId, @PathVariable String regionId, HttpServletResponse response)
      throws IOException, CustomRegionServiceException {
    log.debug("Inside download custom regions by orgId data as csv");
    String csvContents =
        csvDownloadUtilityService.downloadCustomRegionsForOrgIdAndRegionId(orgId, regionId);
    response.setStatus(HttpStatus.OK.value());
    response.setContentLength(csvContents.length());
    response.getOutputStream().write(csvContents.getBytes());
    response.flushBuffer();
  }

  /**
   * Downloads the cost definition data for the specified organization.
   *
   * <p>This method processes a POST request to download the cost definition data as a CSV for a
   * specific organization.
   *
   * @param orgId The unique identifier for the organization.
   * @param request The request payload containing the cost definition details.
   * @param response The HTTP response.
   * @throws IOException If there is an error while writing the response.
   * @throws CommonServiceException If there is an error while processing the request.
   */
  @DownloadCostDefinitionDoc
  @PostMapping(value = "/cost-config/ui/cost-definition/download/{orgId}")
  public void downloadCostDefinition(
      @NotBlank(message = "OrgId can't be empty") @PathVariable String orgId,
      @Valid @RequestBody CostDefinitionRequest request,
      HttpServletResponse response)
      throws IOException, CommonServiceException {
    log.debug("Inside download cost definition for orgId {}", orgId);
    InputStream inputStream =
        csvDownloadUtilityService.downloadCostDefinitionForOrgId(orgId, request);
    response.setStatus(HttpStatus.OK.value());
    response.setHeader(
        HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=" + CsvUtil.getFilename("download-cost-definition"));
    IOUtils.copy(inputStream, response.getOutputStream());
    response.flushBuffer();
  }

  /**
   * Downloads the holiday cutoff rules for the specified organization.
   *
   * <p>This method processes a POST request to download holiday cutoff rules as a CSV for a
   * specific organization.
   *
   * @param orgId The unique identifier for the organization.
   * @param holidayCutoffUIRequest The request payload containing holiday cutoff rules details.
   * @param response The HTTP response.
   * @throws IOException If there is an error while writing the response.
   */
  @PostMapping(value = "/holiday-cutoff/v1/ui/orgId/{orgId}/download")
  public void downloadHCORules(
      @NotBlank(message = "OrgId can't be empty") @PathVariable String orgId,
      @Valid @RequestBody HolidayCutoffUIRequest holidayCutoffUIRequest,
      HttpServletResponse response)
      throws IOException {
    log.debug("Inside download holiday cutoff rules for orgId {}", orgId);
    response.setContentType("text/csv");
    response.setStatus(HttpStatus.OK.value());
    response.setHeader(
        HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=" + CsvUtil.getFilename("download-holiday-cutoff"));
    InputStream inputStream =
        csvDownloadUtilityService.downloadHolidayCutoffRulesForOrgId(orgId, holidayCutoffUIRequest);
    IOUtils.copy(inputStream, response.getOutputStream());
    response.flushBuffer();
  }

  /**
   * Downloads transfer schedules data for a specific organization as a CSV file.
   *
   * <p>This endpoint processes a POST request to download transfer schedules data filtered by
   * organization ID and additional criteria specified in the request body. The method generates a
   * CSV file containing transfer schedule details.
   *
   * @param orgId The unique identifier of the organization. Must not be blank. Example value:
   *     "NEXTUPLE"
   * @param request The {@link FetchTransferScheduleRequest} containing filter criteria for transfer
   *     schedules to be downloaded.
   * @param httpServletResponse The HTTP servlet response used to write the CSV file data and set
   *     appropriate headers.
   * @throws IOException If there is an error in writing to the response output stream or handling
   *     the temporary file.
   */
  @PostMapping(value = "/transfer-schedule/ui/orgId/{orgId}/download")
  public void downloadTransferSchedules(
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
      @Valid @RequestBody FetchTransferScheduleRequest request,
      HttpServletResponse httpServletResponse)
      throws IOException {
    log.debug("Inside download transfer schedules for orgId {}", orgId);
    final var file = csvDownloadUtilityService.downloadTransferSchedulesData(orgId, request);
    try (var inputStream = new FileInputStream(file)) {
      httpServletResponse.setStatus(HttpStatus.OK.value());
      httpServletResponse.setHeader(
          HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=%s".formatted(file.getName()));
      httpServletResponse.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));
      IOUtils.copy(inputStream, httpServletResponse.getOutputStream());
      httpServletResponse.flushBuffer();
    } finally {
      Files.delete(file.toPath());
    }
  }
}
