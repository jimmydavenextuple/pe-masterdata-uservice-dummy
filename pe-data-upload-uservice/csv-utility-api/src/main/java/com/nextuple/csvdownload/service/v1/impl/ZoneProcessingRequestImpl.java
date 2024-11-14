/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service.v1.impl;

import static com.nextuple.dataupload.common.constants.CommonDataUploadErrorConstants.NO_RECORDS_FOUND_IN_THE_CSV;
import static com.nextuple.dataupload.common.constants.CommonDataUploadErrorConstants.ZONES_DATA_UPLOAD_EMPTY_INVALID_HEADERS;
import static com.nextuple.dataupload.common.constants.CommonDataUploadErrorConstants.ZONES_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.nextuple.dataupload.common.constants.CommonDataUploadErrorConstants.ZONES_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_SERVICE_HEADER;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DESTINATION_SOURCE_GEOZONE_HEADER;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;

import com.google.gson.Gson;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.util.ObjectUtil;
import com.nextuple.csvdownload.common.inbound.GenericUploadRequest;
import com.nextuple.csvdownload.domain.mapper.ZoneDataRequestMapper;
import com.nextuple.csvdownload.domain.pojo.ZoneDataErrorLogsPojo;
import com.nextuple.csvdownload.exception.JobSubmissionException;
import com.nextuple.csvdownload.service.v1.AbstractProcessingRequest;
import com.nextuple.dataupload.common.utils.v1.DataUploadUtil;
import com.nextuple.jobs.framework.common.clients.FileMetaDataClient;
import com.nextuple.jobs.framework.common.clients.JobsDashboardClient;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.nextuple.jobs.framework.common.domain.pojo.ZoneDataUpload;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.jobs.framework.common.service.FileService;
import com.nextuple.jobs.framework.common.service.PreSignedUrlInterface;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Service
public class ZoneProcessingRequestImpl extends AbstractProcessingRequest {

  private final Logger logger = LoggerFactory.getLogger(ZoneProcessingRequestImpl.class);

  private static final ZoneDataRequestMapper INSTANCE =
      Mappers.getMapper(ZoneDataRequestMapper.class);

  public ZoneProcessingRequestImpl(
      JobsDashboardClient jobsDashboardClient,
      FileService fileService,
      PreSignedUrlInterface preSignedUrlInterface,
      FileMetaDataClient fileMetaDataClient) {
    super(jobsDashboardClient, fileService, preSignedUrlInterface, fileMetaDataClient);
  }

  @Override
  public String getModuleType() {
    return ModuleEnum.ZONES.getModuleValue();
  }

  @Override
  public String submitJob(String orgId, long fileMetadataId) throws JobSubmissionException {
    return submitJob(orgId, JobTypeEnum.UPLOAD_ZONES, fileMetadataId).getJobId();
  }

  @Override
  public void validate(GenericUploadRequest request, FileResponse fileResponse)
      throws CommonServiceException, CsvException, IOException {
    // validate file type
    DataUploadUtil.validateFileType(
        fileResponse.getContentType(), ZONES_DATA_UPLOAD_INVALID_FILE_TYPE);

    var csvReader = new CSVReader(new InputStreamReader(fileResponse.getInputStream()));

    // Extract carrierServiceId  value
    String[] carrierServiceIdHeaderRow = csvReader.readNext();
    validateEmptyRowHeader(carrierServiceIdHeaderRow, csvReader);
    String carrierServiceIdHeader = carrierServiceIdHeaderRow[0];

    // Extract destination/sourceFsa header and sourceFsa values
    String[] sGeoZoneListHeaderRow = csvReader.readNext();
    validateEmptyRowHeader(sGeoZoneListHeaderRow, csvReader);
    String sGeoZoneListHeader = sGeoZoneListHeaderRow[0];

    validateEmptyCSV(csvReader);

    DataUploadUtil.validateCSVHeaders(
        new String[] {carrierServiceIdHeader, sGeoZoneListHeader},
        getModuleType(),
        ZONES_DATA_UPLOAD_INVALID_FILE_HEADERS,
        csvReader);

    csvReader.close();
  }

  @Override
  public JobTypeEnum getJobType() {
    return JobTypeEnum.UPLOAD_ZONES;
  }

  @Override
  public String tempFilePrefix() {
    return "download-log-zones";
  }

  @Override
  public void addErrorLine(CSVWriter writer, List<RecordStatusDto> recordStatusDto)
      throws IOException, CommonServiceException {
    var zonetDataErrorLogsList =
        recordStatusDto.stream().map(this::getRequestBody).collect(Collectors.toList());

    var orgId = zonetDataErrorLogsList.get(0).getOrgId();
    var carrierServiceId = zonetDataErrorLogsList.get(0).getCarrierServiceId();

    Set<String> sourceGeozoneSet =
        zonetDataErrorLogsList.stream()
            .map(ZoneDataErrorLogsPojo::getSourceGeozone)
            .collect(Collectors.toSet());

    var sourceGeozoneList = new ArrayList<>(sourceGeozoneSet);

    sourceGeozoneList.add(0, DESTINATION_SOURCE_GEOZONE_HEADER);

    writer.writeNext(new String[] {ORG_ID, orgId});
    writer.writeNext(new String[] {CARRIER_SERVICE_HEADER, carrierServiceId});
    writer.writeNext(sourceGeozoneList.toArray(new String[0]));

    sourceGeozoneList.clear();

    Map<String, String> sourceAndZoneDataMap = new LinkedHashMap<>();

    sourceGeozoneSet.forEach(geozone -> sourceAndZoneDataMap.put(geozone, null));

    Map<String, Map<String, String>> zoneDataErrorLogsMap = new HashMap<>();

    zonetDataErrorLogsList.forEach(
        zoneRequest -> {
          if (CollectionUtils.isEmpty(
              zoneDataErrorLogsMap.get(zoneRequest.getDestinationGeozone()))) {
            zoneDataErrorLogsMap.put(
                zoneRequest.getDestinationGeozone(), new LinkedHashMap<>(sourceAndZoneDataMap));
          }
          zoneDataErrorLogsMap
              .get(zoneRequest.getDestinationGeozone())
              .put(zoneRequest.getSourceGeozone(), errorMessage(zoneRequest));
        });

    constructCsvDataForZone(zoneDataErrorLogsMap, writer);
  }

  private void constructCsvDataForZone(
      Map<String, Map<String, String>> zoneDataErrorLogsMap, CSVWriter writer) throws IOException {
    zoneDataErrorLogsMap
        .keySet()
        .forEach(
            destinationFsa ->
                writer.writeNext(constructCsvRowsForZone(destinationFsa, zoneDataErrorLogsMap)));
    writer.flush();
  }

  private String[] constructCsvRowsForZone(
      String destinationGeozone, Map<String, Map<String, String>> zoneDataMap) {
    Map<String, String> sourceGeozoneAndMessageMap = zoneDataMap.get(destinationGeozone);

    var rowContents =
        sourceGeozoneAndMessageMap.keySet().stream()
            .map(sourceFsa -> String.valueOf(sourceGeozoneAndMessageMap.get(sourceFsa)))
            .collect(Collectors.toCollection(ArrayList::new));
    rowContents.add(0, destinationGeozone);

    return rowContents.toArray(new String[0]);
  }

  private ZoneDataErrorLogsPojo getRequestBody(RecordStatusDto recordStatusDto) {
    var gson = new Gson();
    var errorLogsPojo =
        INSTANCE.convertToZoneDataErrorLogsPojo(
            gson.fromJson(recordStatusDto.getRequestBody(), ZoneDataUpload.class));
    errorLogsPojo.setErrorMessage(recordStatusDto.getErrorMessage());
    errorLogsPojo.setException(recordStatusDto.getException());
    return errorLogsPojo;
  }

  private void validateEmptyCSV(CSVReader csvReader)
      throws CommonServiceException, IOException, CsvValidationException {
    String[] csvRow = csvReader.readNext();
    if (ObjectUtil.isNull(csvRow) || csvRow.length == 1) {
      csvReader.close();
      throw new CommonServiceException(
          NO_RECORDS_FOUND_IN_THE_CSV, HttpStatus.BAD_REQUEST, 0x2773, null);
    }
  }

  private String errorMessage(ZoneDataErrorLogsPojo zoneDataErrorLogsPojo) {
    if (ObjectUtils.isEmpty(zoneDataErrorLogsPojo.getErrorMessage())) {
      logger.error("Empty error message received. Defaulting to internal server error message");
      return "Internal Server Error";
    } else if (("feign.RetryableException").equals(zoneDataErrorLogsPojo.getException())) {
      return String.valueOf(zoneDataErrorLogsPojo.getZone());
    } else {
      return zoneDataErrorLogsPojo.getErrorMessage();
    }
  }

  private void validateEmptyRowHeader(String[] rowHeader, CSVReader csvReader)
      throws CommonServiceException, IOException {
    if (Objects.isNull(rowHeader) || rowHeader[0].isEmpty()) {
      csvReader.close();
      throw new CommonServiceException(
          ZONES_DATA_UPLOAD_EMPTY_INVALID_HEADERS, HttpStatus.BAD_REQUEST, 0x2773, null);
    }
  }
}
