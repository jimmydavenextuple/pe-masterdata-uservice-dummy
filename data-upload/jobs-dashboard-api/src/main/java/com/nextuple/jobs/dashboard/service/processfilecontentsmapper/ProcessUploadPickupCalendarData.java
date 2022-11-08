package com.nextuple.jobs.dashboard.service.processfilecontentsmapper;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CALENDAR_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_SERVICE_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DESCRIPTION;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.EFFECTIVE_DATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.NODE_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;

import com.nextuple.dataupload.common.headers.v1.DataUploadUtilityExpectedHeaders;
import com.nextuple.jobs.dashboard.service.ProcessFileContents;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.PickUpCalendarUpload;
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
public class ProcessUploadPickupCalendarData implements ProcessFileContents {

  @Override
  public JobTypeEnum getJobType() {
    return JobTypeEnum.UPLOAD_PICKUP_CALENDER;
  }

  @Override
  public List<Object> updateRequestObjectsList(JobTypeEnum jobType, InputStream inputStream)
      throws IOException, CsvException {
    return new ArrayList<>(createUploadMarketRegionsJobRequest(inputStream));
  }

  private List<PickUpCalendarUpload> createUploadMarketRegionsJobRequest(InputStream inputStream)
      throws IOException {

    var bufferedReader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    var csvFormat =
        CSVFormat.DEFAULT
            .withQuote(null)
            .withHeader(
                DataUploadUtilityExpectedHeaders.getCSVExpectedHeaders(
                        ModuleEnum.PICKUP_CALENDAR.getModuleValue())
                    .toArray(new String[0]));
    var csvParser = new CSVParser(bufferedReader, csvFormat);

    Iterable<CSVRecord> csvRecords = csvParser.getRecords();
    Iterator<CSVRecord> iterator = csvRecords.iterator();
    iterator.next();
    List<PickUpCalendarUpload> pickUpCalendarUploadList = new ArrayList<>();

    while (iterator.hasNext()) {
      var csvRecord = iterator.next();
      var pickUpCalendarUpload =
          PickUpCalendarUpload.builder()
              .action(csvRecord.get(ACTION))
              .calendarId(csvRecord.get(CALENDAR_ID))
              .orgId(csvRecord.get(ORG_ID))
              .nodeId(csvRecord.get(NODE_ID))
              .carrierServiceId(csvRecord.get(CARRIER_SERVICE_ID))
              .description(csvRecord.get(DESCRIPTION))
              .effectiveDate(csvRecord.get(EFFECTIVE_DATE))
              .build();
      pickUpCalendarUploadList.add(pickUpCalendarUpload);
    }

    return pickUpCalendarUploadList;
  }
}
