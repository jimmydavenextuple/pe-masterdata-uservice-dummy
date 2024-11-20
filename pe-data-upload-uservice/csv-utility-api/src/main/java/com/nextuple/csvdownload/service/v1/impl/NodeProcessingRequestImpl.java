/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service.v1.impl;

import static com.nextuple.dataupload.common.constants.CommonDataUploadErrorConstants.NODE_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.nextuple.dataupload.common.constants.CommonDataUploadErrorConstants.NODE_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.nextuple.dataupload.common.constants.CommonDataUploadErrorConstants.NO_RECORDS_FOUND_IN_THE_CSV;

import com.google.gson.Gson;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.csvdownload.common.inbound.GenericUploadRequest;
import com.nextuple.csvdownload.exception.JobSubmissionException;
import com.nextuple.csvdownload.service.v1.AbstractProcessingRequest;
import com.nextuple.dataupload.common.utils.v1.DataUploadUtil;
import com.nextuple.dataupload.common.utils.v1.DynamicCsvHeadersValidation;
import com.nextuple.jobs.framework.common.clients.FileMetaDataClient;
import com.nextuple.jobs.framework.common.clients.JobsDashboardClient;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.domain.pojo.NodeDataUpload;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.jobs.framework.common.service.FileService;
import com.nextuple.jobs.framework.common.service.PreSignedUrlInterface;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NodeProcessingRequestImpl extends AbstractProcessingRequest {

  @Value("${download-page-size.node-carrier-service-options}")
  private Integer noOfRecordsPerPage;

  @Autowired private DynamicCsvHeadersValidation csvValidation;

  public NodeProcessingRequestImpl(
      JobsDashboardClient jobsDashboardClient,
      FileService fileService,
      PreSignedUrlInterface preSignedUrlInterface,
      FileMetaDataClient fileMetaDataClient) {
    super(jobsDashboardClient, fileService, preSignedUrlInterface, fileMetaDataClient);
  }

  @Override
  public String getModuleType() {
    return ModuleEnum.NODES.getModuleValue();
  }

  @Override
  public String submitJob(String orgId, long fileMetadataId) throws JobSubmissionException {
    return submitJob(orgId, JobTypeEnum.UPLOAD_NODES, fileMetadataId).getJobId();
  }

  @Override
  public void validate(GenericUploadRequest request, FileResponse fileResponse)
      throws CommonServiceException, CsvException, IOException {

    // validate file type
    DataUploadUtil.validateFileType(
        fileResponse.getContentType(), NODE_DATA_UPLOAD_INVALID_FILE_TYPE);

    var csvReader = new CSVReader(new InputStreamReader(fileResponse.getInputStream()));
    List<String[]> csvFileContents = csvReader.readAll();

    DataUploadUtil.validateEmptyCSV(csvFileContents, NO_RECORDS_FOUND_IN_THE_CSV, csvReader);
    csvValidation.validateCSVHeaders(
        csvFileContents.get(0), getModuleType(), NODE_DATA_UPLOAD_INVALID_FILE_HEADERS, csvReader);

    csvReader.close();
  }

  @Override
  public String tempFilePrefix() {
    return "download-log-node";
  }

  @Override
  public void addErrorLine(CSVWriter writer, List<RecordStatusDto> recordStatusDtos)
      throws IOException, CommonServiceException {
    for (RecordStatusDto recordStatusDto : recordStatusDtos) {
      constructNodeError(writer, recordStatusDto);
    }
    writer.flush();
  }

  void constructNodeError(CSVWriter writer, RecordStatusDto recordStatusDto)
      throws CommonServiceException {
    var gson = new Gson();
    String[] serviceOption = tenantDatabaseConfig.getCurrentTenantServiceOptions();
    String[] serviceOptionValue = new String[serviceOption.length];
    var requestBody = gson.fromJson(recordStatusDto.getRequestBody(), NodeDataUpload.class);
    for (int index = 0; index < serviceOption.length; index++) {
      serviceOptionValue[index] =
          requestBody.getServiceOptionEligibilities().get(serviceOption[index]) == null
              ? ""
              : requestBody.getServiceOptionEligibilities().get(serviceOption[index]);
    }
    var bopisEligible =
        requestBody.getBopisEligible() == null ? "" : requestBody.getBopisEligible();
    var req =
        new String[] {
          requestBody.getAction(),
          requestBody.getNodeId(),
          requestBody.getOrgId(),
          requestBody.getStreet(),
          requestBody.getCity(),
          requestBody.getState(),
          requestBody.getZipCode(),
          requestBody.getCountry(),
          requestBody.getLatitude(),
          requestBody.getLongitude(),
          requestBody.getTimezone(),
          requestBody.getShipToHome(),
          bopisEligible,
          requestBody.getNodeType(),
          requestBody.getNodeLabourTier(),
          requestBody.getIsActive(),
          requestBody.getStartWorkingTime(),
          requestBody.getLastWorkingTime()
        };
    String[] data = new String[req.length + serviceOption.length + 1];
    // Merging three arrays, basic node fields array, service option field array and error message
    System.arraycopy(req, 0, data, 0, req.length);
    System.arraycopy(serviceOptionValue, 0, data, req.length, serviceOption.length);
    data[data.length - 1] = recordStatusDto.getErrorMessage();
    writeToCSV(data, writer);
  }

  @Override
  public JobTypeEnum getJobType() {
    return JobTypeEnum.UPLOAD_NODES;
  }
}
