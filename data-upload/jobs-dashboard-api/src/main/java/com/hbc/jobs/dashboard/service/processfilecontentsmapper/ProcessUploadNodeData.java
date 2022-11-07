package com.hbc.jobs.dashboard.service.processfilecontentsmapper;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.BOPIS_ELIGIBLE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CITY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.COUNTRY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.EXPRESS_ELIGIBLE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.IS_ACTIVE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LATITUDE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LONGITUDE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.NEXTDAY_ELIGIBLE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.NODE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.NODE_TYPE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.POSTAL_CODE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.PROVINCE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SDND_ELIGIBLE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SHIP_TO_HOME;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.STREET;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.TIMEZONE;

import com.hbc.dataupload.common.headers.v1.DataUploadUtilityExpectedHeaders;
import com.hbc.jobs.dashboard.service.ProcessFileContents;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.NodeDataUpload;
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
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessUploadNodeData implements ProcessFileContents {

  @Override
  public JobTypeEnum getJobType() {
    return JobTypeEnum.UPLOAD_NODES;
  }

  @Override
  public List<Object> updateRequestObjectsList(JobTypeEnum jobType, InputStream inputStream)
      throws IOException, CsvException {
    return new ArrayList<>(createUploadNodeDataJobRequest(inputStream));
  }

  private List<NodeDataUpload> createUploadNodeDataJobRequest(InputStream inputStream)
      throws IOException {

    var bufferedReader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    var csvFormat =
        CSVFormat.DEFAULT.withHeader(
            DataUploadUtilityExpectedHeaders.getCSVExpectedHeaders(
                    ModuleEnum.NODES.getModuleValue())
                .toArray(new String[0]));
    var csvParser = new CSVParser(bufferedReader, csvFormat);

    Iterable<CSVRecord> csvRecords = csvParser.getRecords();
    Iterator<CSVRecord> iterator = csvRecords.iterator();
    iterator.next();
    List<NodeDataUpload> nodeDataUploadList = new ArrayList<>();

    while (iterator.hasNext()) {
      var csvRecord = iterator.next();
      var nodeCalendarUpload =
          NodeDataUpload.builder()
              .action(csvRecord.get(ACTION))
              .nodeId(csvRecord.get(NODE_ID))
              .orgId(csvRecord.get(ORG_ID))
              .street(csvRecord.get(STREET))
              .city(csvRecord.get(CITY))
              .province(csvRecord.get(PROVINCE))
              .postalCode(csvRecord.get(POSTAL_CODE))
              .country(csvRecord.get(COUNTRY))
              .latitude(csvRecord.get(LATITUDE))
              .longitude(csvRecord.get(LONGITUDE))
              .timezone(csvRecord.get(TIMEZONE))
              .serviceOptionEligibilities(
                  Map.of(
                      SDND_ELIGIBLE, Boolean.valueOf(csvRecord.get(SDND_ELIGIBLE)),
                      EXPRESS_ELIGIBLE, Boolean.valueOf(csvRecord.get(EXPRESS_ELIGIBLE)),
                      NEXTDAY_ELIGIBLE, Boolean.valueOf(csvRecord.get(NEXTDAY_ELIGIBLE))))
              .shipToHome(csvRecord.get(SHIP_TO_HOME))
              .bopisEligible(csvRecord.get(BOPIS_ELIGIBLE))
              .nodeType(csvRecord.get(NODE_TYPE))
              .isActive(csvRecord.get(IS_ACTIVE))
              .build();
      nodeDataUploadList.add(nodeCalendarUpload);
    }

    return nodeDataUploadList;
  }
}
