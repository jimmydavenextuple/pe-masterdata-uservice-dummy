package com.hbc.jobs.dashboard.service.processfilecontentsmapper;

import com.hbc.dataupload.common.constants.DataUploadUtilityConstants;
import com.hbc.dataupload.common.headers.v1.DataUploadUtilityExpectedHeaders;
import com.hbc.jobs.dashboard.service.ProcessFileContents;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.CarrierServiceUpload;
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
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class ProcessUploadCarrierServiceData implements ProcessFileContents {

  @Override
  public JobTypeEnum getJobType() {
    return JobTypeEnum.UPLOAD_CARRIER_SERVICE;
  }

  @Override
  public List<Object> updateRequestObjectsList(JobTypeEnum jobType, InputStream inputStream)
      throws IOException, CsvException {
    return new ArrayList<>(createCarrierServiceUploadJobRequest(inputStream));
  }

  private List<CarrierServiceUpload> createCarrierServiceUploadJobRequest(InputStream inputStream)
      throws IOException {

    var bufferedReader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    var csvFormat =
        CSVFormat.DEFAULT.withHeader(
            DataUploadUtilityExpectedHeaders.getCSVExpectedHeaders(
                    ModuleEnum.CARRIER_SERVICE.getModuleValue())
                .toArray(new String[0]));
    var csvParser = new CSVParser(bufferedReader, csvFormat);

    Iterable<CSVRecord> csvRecords = csvParser.getRecords();
    Iterator<CSVRecord> iterator = csvRecords.iterator();
    iterator.next();
    List<CarrierServiceUpload> carrierServiceUploadList = new ArrayList<>();

    /** CSV data parsed and map to NodeCarrierRequest object */
    while (iterator.hasNext()) {
      var csvRecord = iterator.next();
      var carrierServiceUpload =
          CarrierServiceUpload.builder()
              .action(csvRecord.get(DataUploadUtilityConstants.ACTION))
              .orgId(csvRecord.get(DataUploadUtilityConstants.ORG_ID))
              .carrierId(csvRecord.get(DataUploadUtilityConstants.CARRIER_ID))
              .carrierServiceId(csvRecord.get(DataUploadUtilityConstants.CARRIER_SERVICE_ID))
              .carrierName(csvRecord.get(DataUploadUtilityConstants.CARRIER_NAME))
              .serviceName(csvRecord.get(DataUploadUtilityConstants.SERVICE_NAME))
              .serviceOptions(csvRecord.get(DataUploadUtilityConstants.SERVICE_OPTIONS))
              .build();
      carrierServiceUploadList.add(carrierServiceUpload);
    }

    /** form job request string */
    return carrierServiceUploadList;
  }
}
