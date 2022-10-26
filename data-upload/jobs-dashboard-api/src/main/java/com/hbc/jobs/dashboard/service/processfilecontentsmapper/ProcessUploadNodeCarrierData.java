package com.hbc.jobs.dashboard.service.processfilecontentsmapper;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_SERVICE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LAST_PICKUP_TIME;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.NODE_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_OPTION;

import com.hbc.dataupload.common.headers.v1.DataUploadUtilityExpectedHeaders;
import com.hbc.jobs.dashboard.service.ProcessFileContents;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.NodeCarrierUpload;
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
public class ProcessUploadNodeCarrierData implements ProcessFileContents {

  @Override
  public JobTypeEnum getJobType() {
    return JobTypeEnum.UPLOAD_NODE_CARRIER;
  }

  @Override
  public List<Object> updateRequestObjectsList(JobTypeEnum jobType, InputStream inputStream)
      throws IOException, CsvException {
    return new ArrayList<>(createNodeCarrierUploadJobRequest(inputStream));
  }

  private List<NodeCarrierUpload> createNodeCarrierUploadJobRequest(InputStream inputStream)
      throws IOException {

    var bufferedReader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    var csvFormat =
        CSVFormat.DEFAULT.withHeader(
            DataUploadUtilityExpectedHeaders.getCSVExpectedHeaders(
                    ModuleEnum.NODE_CARRIER.getModuleValue())
                .toArray(new String[0]));
    var csvParser = new CSVParser(bufferedReader, csvFormat);

    Iterable<CSVRecord> csvRecords = csvParser.getRecords();
    Iterator<CSVRecord> iterator = csvRecords.iterator();
    iterator.next();
    List<NodeCarrierUpload> nodeCarrierUploadList = new ArrayList<>();

    while (iterator.hasNext()) {
      var csvRecord = iterator.next();
      var nodeCalendarUpload =
          NodeCarrierUpload.builder()
              .action(csvRecord.get(ACTION))
              .nodeId(csvRecord.get(NODE_ID))
              .orgId(csvRecord.get(ORG_ID))
              .carrierServiceId(csvRecord.get(CARRIER_SERVICE_ID))
              .serviceOption(csvRecord.get(SERVICE_OPTION))
              .lastPickupTime(csvRecord.get(LAST_PICKUP_TIME))
              .build();
      nodeCarrierUploadList.add(nodeCalendarUpload);
    }

    return nodeCarrierUploadList;
  }
}
