/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.dashboard.service.processfilecontentsmapper;

import com.nextuple.common.constants.CommonConstants;
import com.nextuple.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.nextuple.dataupload.common.constants.DataUploadUtilityConstants;
import com.nextuple.jobs.dashboard.service.ProcessFileContents;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.opencsv.exceptions.CsvException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessUploadProcessingLeadTimesData implements ProcessFileContents {

  @Override
  public JobTypeEnum getJobType() {
    return JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES;
  }

  @Override
  public List<Object> updateRequestObjectsList(JobDto jobDto, InputStream inputStream)
      throws IOException, CsvException {
    return new ArrayList<>(createUploadProcessingLeadTimesJobRequest(inputStream));
  }

  private List<ProcessingLeadTimesRaw> createUploadProcessingLeadTimesJobRequest(
      InputStream inputStream) throws IOException {

    var bufferedReader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    var csvFormat = CSVFormat.DEFAULT.withHeader(ProcessingLeadTimesRaw.columnHeadersArray());
    var csvParser = new CSVParser(bufferedReader, csvFormat);

    Iterable<CSVRecord> csvRecords = csvParser.getRecords();
    Iterator<CSVRecord> iterator = csvRecords.iterator();
    iterator.next();
    List<ProcessingLeadTimesRaw> processingLeadTimesRawList = new ArrayList<>();

    /** CSV data parsed and map to NodeCarrierRequest object */
    while (iterator.hasNext()) {
      var csvRecord = iterator.next();
      var processingLeadTime =
          ProcessingLeadTimesRaw.builder()
              .orgId(csvRecord.get(DataUploadUtilityConstants.ORG_ID))
              .nodeId(csvRecord.get(DataUploadUtilityConstants.NODE_ID))
              .processingTime(csvRecord.get(DataUploadUtilityConstants.PROCESSING_TIME_IN_HRS))
              .serviceOption(csvRecord.get(CommonConstants.SERVICE_OPTION))
              .actionType(csvRecord.get(DataUploadUtilityConstants.ACTION))
              .carrierServiceId("")
              .build();
      processingLeadTimesRawList.add(processingLeadTime);
    }

    /** form job request string */
    return processingLeadTimesRawList;
  }
}
