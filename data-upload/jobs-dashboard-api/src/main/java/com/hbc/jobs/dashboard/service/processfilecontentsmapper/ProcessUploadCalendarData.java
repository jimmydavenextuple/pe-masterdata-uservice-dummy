package com.hbc.jobs.dashboard.service.processfilecontentsmapper;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CALENDAR_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DESCRIPTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.EXCEPTION_DAYS;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.IS_FRIDAY_WORKING;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.IS_MONDAY_WORKING;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.IS_SATURDAY_WORKING;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.IS_SUNDAY_WORKING;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.IS_THURSDAY_WORKING;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.IS_TUESDAY_WORKING;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.IS_WEDNESDAY_WORKING;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbc.calendar.domain.pojo.ExceptionDays;
import com.hbc.dataupload.common.headers.v1.DataUploadUtilityExpectedHeaders;
import com.hbc.jobs.dashboard.service.ProcessFileContents;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.CalendarDataUpload;
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
public class ProcessUploadCalendarData implements ProcessFileContents {

  @Override
  public JobTypeEnum getJobType() {
    return JobTypeEnum.UPLOAD_CALENDER;
  }

  @Override
  public List<Object> updateRequestObjectsList(JobTypeEnum jobType, InputStream inputStream)
      throws IOException, CsvException {
    return new ArrayList<>(createUploadMarketRegionsJobRequest(inputStream));
  }

  private List<CalendarDataUpload> createUploadMarketRegionsJobRequest(InputStream inputStream)
      throws IOException {

    var bufferedReader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    var csvFormat =
        CSVFormat.DEFAULT
            .withHeader(
                DataUploadUtilityExpectedHeaders.getCSVExpectedHeaders(
                        ModuleEnum.CALENDAR.getModuleValue())
                    .toArray(new String[0]))
            .withEscape('\\');
    var csvParser = new CSVParser(bufferedReader, csvFormat);

    Iterable<CSVRecord> csvRecords = csvParser.getRecords();
    Iterator<CSVRecord> iterator = csvRecords.iterator();
    iterator.next();
    List<CalendarDataUpload> calendarDataUploadList = new ArrayList<>();
    var mapper = new ObjectMapper();

    while (iterator.hasNext()) {
      var csvRecord = iterator.next();
      var calendarDataUpload =
          CalendarDataUpload.builder()
              .action(csvRecord.get(ACTION))
              .calendarId(csvRecord.get(CALENDAR_ID))
              .orgId(csvRecord.get(ORG_ID))
              .description(csvRecord.get(DESCRIPTION))
              .isMondayWorking(Boolean.valueOf(csvRecord.get(IS_MONDAY_WORKING)))
              .isTuesdayWorking(Boolean.valueOf(csvRecord.get(IS_TUESDAY_WORKING)))
              .isWednesdayWorking(Boolean.valueOf(csvRecord.get(IS_WEDNESDAY_WORKING)))
              .isThursdayWorking(Boolean.valueOf(csvRecord.get(IS_THURSDAY_WORKING)))
              .isFridayWorking(Boolean.valueOf(csvRecord.get(IS_FRIDAY_WORKING)))
              .isSaturdayWorking(Boolean.valueOf(csvRecord.get(IS_SATURDAY_WORKING)))
              .isSundayWorking(Boolean.valueOf(csvRecord.get(IS_SUNDAY_WORKING)))
              .exceptionDays(
                  mapper.readValue(
                      csvRecord.get(EXCEPTION_DAYS), new TypeReference<List<ExceptionDays>>() {}))
              .build();
      calendarDataUploadList.add(calendarDataUpload);
    }

    return calendarDataUploadList;
  }
}
