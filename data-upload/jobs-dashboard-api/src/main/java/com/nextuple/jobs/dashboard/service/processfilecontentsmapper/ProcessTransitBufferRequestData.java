package com.nextuple.jobs.dashboard.service.processfilecontentsmapper;

import com.nextuple.common.constants.CommonConstants;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.jobs.dashboard.service.ProcessFileContents;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.TransitBufferUpload;
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
public class ProcessTransitBufferRequestData implements ProcessFileContents {

  Logger logger = LoggerFactory.getLogger(ProcessTransitBufferRequestData.class);

  @Override
  public JobTypeEnum getJobType() {
    return JobTypeEnum.TRANSIT_BUFFER_REQUEST;
  }

  @Override
  public List<Object> updateRequestObjectsList(JobTypeEnum jobType, InputStream inputStream)
      throws IOException, CsvException {
    logger.debug("Processing transit buffer request upload data");
    return new ArrayList<>(createUploadTransitBufferJobRequest(inputStream));
  }

  private List<TransitBufferUpload> createUploadTransitBufferJobRequest(InputStream inputStream)
      throws IOException {
    var bufferedReader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    var csvFormat = CSVFormat.DEFAULT.withHeader(TransitBufferUpload.columnHeadersArray());
    var csvParser = new CSVParser(bufferedReader, csvFormat);

    Iterable<CSVRecord> csvRecords = csvParser.getRecords();
    Iterator<CSVRecord> iterator = csvRecords.iterator();
    iterator.next();
    List<TransitBufferUpload> transitBufferUploadList = new ArrayList<>();

    while (iterator.hasNext()) {
      var csvRecord = iterator.next();
      var transitBufferUpload =
          TransitBufferUpload.builder()
              .orgId(csvRecord.get(CommonConstants.ORG_ID))
              .carrierServiceId(csvRecord.get(CommonConstants.CARRIER_SERVICE_ID))
              .sourceGeozone(csvRecord.get(CommonConstants.SOURCE_GEOZONE))
              .destinationGeozone(csvRecord.get(CommonConstants.DESTINATION_GEOZONE))
              .bufferDays(csvRecord.get(CommonConstants.BUFFER_DAYS))
              .bufferStartDate(csvRecord.get(CommonConstants.BUFFER_START_DATE))
              .bufferEndDate(csvRecord.get(CommonConstants.BUFFER_END_DATE))
              .action(csvRecord.get(CommonConstants.ACTION_TYPE))
              .createdBy(csvRecord.get(CommonConstants.CREATE_BY))
              .build();
      transitBufferUploadList.add(transitBufferUpload);
    }

    return transitBufferUploadList;
  }
}
