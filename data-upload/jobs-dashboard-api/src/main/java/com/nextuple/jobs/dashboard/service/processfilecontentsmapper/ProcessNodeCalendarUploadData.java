package com.nextuple.jobs.dashboard.service.processfilecontentsmapper;

import com.nextuple.dataupload.common.constants.DataUploadUtilityConstants;
import com.nextuple.dataupload.common.headers.v1.DataUploadUtilityExpectedHeaders;
import com.nextuple.jobs.dashboard.service.ProcessFileContents;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.NodeCalendarUpload;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
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
    return new ArrayList<>(createNodeCarrierUploadJobRequest(inputStream));
  }

  private List<NodeCalendarUpload> createNodeCarrierUploadJobRequest(InputStream inputStream)
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

    /** CSV data parsed and map to NodeCarrierRequest object */
    while (iterator.hasNext()) {
      var csvRecord = iterator.next();
      var nodeCalendarUpload =
          NodeCalendarUpload.builder()
              .action(csvRecord.get(DataUploadUtilityConstants.ACTION))
              .orgId(csvRecord.get(DataUploadUtilityConstants.ORG_ID))
              .calendarId(csvRecord.get(DataUploadUtilityConstants.CALENDAR_ID))
              .nodeId(csvRecord.get(DataUploadUtilityConstants.NODE_ID))
              .effectiveDate(csvRecord.get(DataUploadUtilityConstants.EFFECTIVE_DATE))
              .description(csvRecord.get(DataUploadUtilityConstants.DESCRIPTION))
              .build();
      nodeCalendarUploadList.add(nodeCalendarUpload);
    }

    return nodeCalendarUploadList;
  }
}
