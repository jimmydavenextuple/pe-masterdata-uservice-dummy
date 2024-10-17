/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service.v1.impl;

import static com.nextuple.dataupload.common.constants.CommonDataUploadErrorConstants.NODE_CARRIER_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.nextuple.dataupload.common.constants.CommonDataUploadErrorConstants.NODE_CARRIER_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.nextuple.dataupload.common.constants.CommonDataUploadErrorConstants.NO_RECORDS_FOUND_IN_THE_CSV;

import com.google.gson.Gson;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.csvdownload.common.inbound.GenericUploadRequest;
import com.nextuple.csvdownload.exception.JobSubmissionException;
import com.nextuple.csvdownload.service.v1.AbstractProcessingRequest;
import com.nextuple.dataupload.common.utils.v1.DataUploadUtil;
import com.nextuple.jobs.framework.common.clients.FileMetaDataClient;
import com.nextuple.jobs.framework.common.clients.JobsDashboardClient;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.domain.pojo.NodeCarrierUpload;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NodeCarrierProcessingRequestImpl extends AbstractProcessingRequest {

  @Value("${download-page-size.node-carrier-service-options}")
  private Integer noOfRecordsPerPage;

  public NodeCarrierProcessingRequestImpl(
      JobsDashboardClient jobsDashboardClient,
      FileService fileService,
      PreSignedUrlInterface preSignedUrlInterface,
      FileMetaDataClient fileMetaDataClient) {
    super(jobsDashboardClient, fileService, preSignedUrlInterface, fileMetaDataClient);
  }

  @Override
  public String getModuleType() {
    return ModuleEnum.NODE_CARRIER.getModuleValue();
  }

  @Override
  public String submitJob(String orgId, long fileMetadataId) throws JobSubmissionException {
    return submitJob(orgId, JobTypeEnum.UPLOAD_NODE_CARRIER, fileMetadataId).getJobId();
  }

  @Override
  public void validate(GenericUploadRequest request, FileResponse fileResponse)
      throws CommonServiceException, CsvException, IOException {

    // validate file type
    DataUploadUtil.validateFileType(
        fileResponse.getContentType(), NODE_CARRIER_DATA_UPLOAD_INVALID_FILE_TYPE);

    var csvReader = new CSVReader(new InputStreamReader(fileResponse.getInputStream()));
    List<String[]> csvFileContents = csvReader.readAll();

    DataUploadUtil.validateEmptyCSV(csvFileContents, NO_RECORDS_FOUND_IN_THE_CSV, csvReader);
    DataUploadUtil.validateCSVHeaders(
        csvFileContents.get(0),
        getModuleType(),
        NODE_CARRIER_DATA_UPLOAD_INVALID_FILE_HEADERS,
        csvReader);

    csvReader.close();
  }

  @Override
  public String tempFilePrefix() {
    return "download-log-node-carrier";
  }

  @Override
  public void addErrorLine(CSVWriter writer, List<RecordStatusDto> recordStatusDtos)
      throws IOException {
    recordStatusDtos.forEach(dto -> constructNodeCarrierError(writer, dto));
    writer.flush();
  }

  private void constructNodeCarrierError(CSVWriter writer, RecordStatusDto recordStatusDto) {
    var gson = new Gson();
    var requestBody = gson.fromJson(recordStatusDto.getRequestBody(), NodeCarrierUpload.class);

    var req =
        new String[] {
          requestBody.getAction(),
          requestBody.getNodeId(),
          requestBody.getOrgId(),
          requestBody.getCarrierServiceId(),
          requestBody.getServiceOption(),
          requestBody.getLastPickupTime(),
          recordStatusDto.getErrorMessage()
        };
    writeToCSV(req, writer);
  }

  @Override
  public JobTypeEnum getJobType() {
    return JobTypeEnum.UPLOAD_NODE_CARRIER;
  }
}
