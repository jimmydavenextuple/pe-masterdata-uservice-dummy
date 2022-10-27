package com.hbc.jobs.dashboard.service.processfilecontentsmapper;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.BUFFER_HOURS;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.END_TIME;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.NODE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_OPTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.START_TIME;

import com.hbc.dataupload.common.headers.v1.DataUploadUtilityExpectedHeaders;
import com.hbc.jobs.dashboard.service.ProcessFileContents;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.ProcessingTimeBufferUpload;
import com.hbc.jobs.framework.common.enums.ModuleEnum;
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
public class ProcessUploadProcessingTimeBufferData implements ProcessFileContents {

  @Override
  public JobTypeEnum getJobType() {
    return JobTypeEnum.UPLOAD_NODE_SERVICE_OPTION_BUFFER;
  }

  @Override
  public List<Object> updateRequestObjectsList(JobTypeEnum jobType, InputStream inputStream)
      throws IOException, CsvException {
    return new ArrayList<>(createUploadMarketRegionsJobRequest(inputStream));
  }

  private List<ProcessingTimeBufferUpload> createUploadMarketRegionsJobRequest(
      InputStream inputStream) throws IOException {

    var bufferedReader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    var csvFormat =
        CSVFormat.DEFAULT
            .withQuote(null)
            .withHeader(
                DataUploadUtilityExpectedHeaders.getCSVExpectedHeaders(
                        ModuleEnum.NODE_SERVICE_OPTION_BUFFER.getModuleValue())
                    .toArray(new String[0]));
    var csvParser = new CSVParser(bufferedReader, csvFormat);

    Iterable<CSVRecord> csvRecords = csvParser.getRecords();
    Iterator<CSVRecord> iterator = csvRecords.iterator();
    iterator.next();
    List<ProcessingTimeBufferUpload> processingTimeBufferUploadList = new ArrayList<>();

    while (iterator.hasNext()) {
      var csvRecord = iterator.next();
      var processingTimeBufferUpload =
          ProcessingTimeBufferUpload.builder()
              .orgId(csvRecord.get(ORG_ID))
              .nodeId(csvRecord.get(NODE_ID))
              .serviceOption(csvRecord.get(SERVICE_OPTION))
              .bufferHours(csvRecord.get(BUFFER_HOURS))
              .bufferStartDate(csvRecord.get(START_TIME))
              .bufferEndDate(csvRecord.get(END_TIME))
              .build();
      processingTimeBufferUploadList.add(processingTimeBufferUpload);
    }

    return processingTimeBufferUploadList;
  }
}
