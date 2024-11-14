/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.dashboard.service.processfilecontentsmapper;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.BOPIS_ELIGIBLE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CITY;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.COUNTRY;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.IS_ACTIVE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LAST_WORKING_TIME;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LATITUDE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LONGITUDE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.NODE_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.NODE_LABOUR_TIER;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.NODE_TYPE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SHIP_TO_HOME;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.START_WORKING_TIME;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.STATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.STREET;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.TIMEZONE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ZIP_CODE;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.dataupload.common.config.TenantDatabaseConfig;
import com.nextuple.dataupload.common.headers.v1.DataUploadUtilityExpectedHeaders;
import com.nextuple.jobs.dashboard.service.ProcessFileContents;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.nextuple.jobs.framework.common.domain.pojo.NodeDataUpload;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.opencsv.exceptions.CsvException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessUploadNodeData implements ProcessFileContents {

  private TenantDatabaseConfig tenantDatabaseConfig;

  @Autowired
  public ProcessUploadNodeData(TenantDatabaseConfig tenantDatabaseConfig) {
    this.tenantDatabaseConfig = tenantDatabaseConfig;
  }

  @Override
  public JobTypeEnum getJobType() {
    return JobTypeEnum.UPLOAD_NODES;
  }

  @Override
  public List<Object> updateRequestObjectsList(JobDto jobDto, InputStream inputStream)
      throws IOException, CsvException, CommonServiceException {
    return new ArrayList<>(createUploadNodeDataJobRequest(inputStream));
  }

  private List<NodeDataUpload> createUploadNodeDataJobRequest(InputStream inputStream)
      throws IOException, CommonServiceException {

    String[] serviceOptions = tenantDatabaseConfig.getCurrentTenantServiceOptions();

    List<String> headers =
        new ArrayList<>(
            DataUploadUtilityExpectedHeaders.getCSVExpectedHeaders(
                ModuleEnum.NODES.getModuleValue()));

    headers.addAll(Arrays.asList(serviceOptions));

    var bufferedReader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    var csvFormat = CSVFormat.DEFAULT.withHeader(headers.toArray(new String[0]));
    var csvParser = new CSVParser(bufferedReader, csvFormat);

    Iterable<CSVRecord> csvRecords = csvParser.getRecords();
    Iterator<CSVRecord> iterator = csvRecords.iterator();
    iterator.next();
    List<NodeDataUpload> nodeDataUploadList = new ArrayList<>();

    while (iterator.hasNext()) {
      var csvRecord = iterator.next();
      HashMap<String, String> serviceOptionsMap = new HashMap<>();

      for (String serviceOption : serviceOptions) {
        serviceOptionsMap.put(serviceOption, csvRecord.get(serviceOption));
      }

      var nodeCalendarUpload =
          NodeDataUpload.builder()
              .action(csvRecord.get(ACTION))
              .nodeId(csvRecord.get(NODE_ID))
              .orgId(csvRecord.get(ORG_ID))
              .street(csvRecord.get(STREET))
              .city(csvRecord.get(CITY))
              .state(csvRecord.get(STATE))
              .zipCode(csvRecord.get(ZIP_CODE))
              .country(csvRecord.get(COUNTRY))
              .latitude(csvRecord.get(LATITUDE))
              .longitude(csvRecord.get(LONGITUDE))
              .timezone(csvRecord.get(TIMEZONE))
              .serviceOptionEligibilities(serviceOptionsMap)
              .shipToHome(csvRecord.get(SHIP_TO_HOME))
              .bopisEligible(csvRecord.get(BOPIS_ELIGIBLE))
              .nodeType(csvRecord.get(NODE_TYPE))
              .nodeLabourTier(csvRecord.get(NODE_LABOUR_TIER))
              .isActive(csvRecord.get(IS_ACTIVE))
              .startWorkingTime(csvRecord.get(START_WORKING_TIME))
              .lastWorkingTime(csvRecord.get(LAST_WORKING_TIME))
              .build();
      nodeDataUploadList.add(nodeCalendarUpload);
    }

    return nodeDataUploadList;
  }
}
