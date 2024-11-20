/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.dashboard.service.processfilecontentsmapper;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CUSTOM_REGION_DESCRIPTION;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CUSTOM_REGION_NAME;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.GEOZONES;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.REGION_ID;

import com.nextuple.dataupload.common.headers.v1.DataUploadUtilityExpectedHeaders;
import com.nextuple.jobs.dashboard.service.ProcessFileContents;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.CustomRegionUpload;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.opencsv.exceptions.CsvException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessCustomRegionUploadData implements ProcessFileContents {

  @Override
  public JobTypeEnum getJobType() {
    return JobTypeEnum.UPLOAD_CUSTOM_REGION;
  }

  @Override
  public List<Object> updateRequestObjectsList(JobDto jobDto, InputStream inputStream)
      throws IOException, CsvException {
    return new ArrayList<>(createUploadCustomRegionsJobRequest(inputStream));
  }

  private List<CustomRegionUpload> createUploadCustomRegionsJobRequest(InputStream inputStream)
      throws IOException {

    var bufferedReader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    var csvFormat =
        CSVFormat.DEFAULT.withHeader(
            DataUploadUtilityExpectedHeaders.getCSVExpectedHeaders("custom-region")
                .toArray(new String[0]));
    var csvParser = new CSVParser(bufferedReader, csvFormat);

    Iterable<CSVRecord> csvRecords = csvParser.getRecords();
    Iterator<CSVRecord> iterator = csvRecords.iterator();
    iterator.next();
    List<CustomRegionUpload> customRegionDtoList = new ArrayList<>();

    while (iterator.hasNext()) {
      var csvRecord = iterator.next();

      String[] codes;
      codes = csvRecord.get(GEOZONES).split(":");

      List<String> codesList = Arrays.asList(codes);

      var customRegionUpload =
          CustomRegionUpload.builder()
              .action(csvRecord.get(ACTION))
              .orgId(csvRecord.get(ORG_ID))
              .id(csvRecord.get(REGION_ID))
              .customRegionName(csvRecord.get(CUSTOM_REGION_NAME))
              .customRegionDescription(csvRecord.get(CUSTOM_REGION_DESCRIPTION))
              .codes(codesList)
              .build();
      customRegionDtoList.add(customRegionUpload);
    }

    return customRegionDtoList;
  }
}
