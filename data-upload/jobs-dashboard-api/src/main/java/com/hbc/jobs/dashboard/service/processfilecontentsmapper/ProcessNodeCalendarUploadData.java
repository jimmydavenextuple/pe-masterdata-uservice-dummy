package com.hbc.jobs.dashboard.service.processfilecontentsmapper;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CALENDAR_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DESCRIPTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.EFFECTIVE_DATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.NODE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;

import com.hbc.dataupload.common.headers.v1.DataUploadUtilityExpectedHeaders;
import com.hbc.jobs.dashboard.service.ProcessFileContents;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.NodeCalendarUpload;
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
public class ProcessNodeCalendarUploadData implements ProcessFileContents {

  @Override
  public JobTypeEnum getJobType() {
    return JobTypeEnum.UPLOAD_NODE_CALENDER;
  }

  @Override
  public List<Object> updateRequestObjectsList(JobTypeEnum jobType, InputStream inputStream)
      throws IOException, CsvException {
    return new ArrayList<>(createUploadNodeCalendarJobRequest(inputStream));
  }

  private List<NodeCalendarUpload> createUploadNodeCalendarJobRequest(InputStream inputStream)
      throws IOException {

    var bufferedReader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    var csvFormat =
        CSVFormat.DEFAULT.withHeader(
            DataUploadUtilityExpectedHeaders.getCSVExpectedHeaders(
                    ModuleEnum.NODE_CALENDAR.getModuleValue())
                .toArray(new String[0]));
    var csvParser = new CSVParser(bufferedReader, csvFormat);

    Iterable<CSVRecord> csvRecords = csvParser.getRecords();
    Iterator<CSVRecord> iterator = csvRecords.iterator();
    iterator.next();
    List<NodeCalendarUpload> nodeCalendarUploadList = new ArrayList<>();

    while (iterator.hasNext()) {
      var csvRecord = iterator.next();
      var nodeCalendarUpload =
          NodeCalendarUpload.builder()
              .action(csvRecord.get(ACTION))
              .calendarId(csvRecord.get(CALENDAR_ID))
              .nodeId(csvRecord.get(NODE_ID))
              .orgId(csvRecord.get(ORG_ID))
              .effectiveDate(csvRecord.get(EFFECTIVE_DATE))
              .description(csvRecord.get(DESCRIPTION))
              .build();
      nodeCalendarUploadList.add(nodeCalendarUpload);
    }

    return nodeCalendarUploadList;
  }
}
