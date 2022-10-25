package com.hbc.jobs.dashboard.service.processfilecontentsmapper;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CITY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.COUNTRY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LATITUDE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.LONGITUDE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.POSTAL_CODE_PREFIX;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.STATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.TIMEZONE1;

import com.hbc.dataupload.common.headers.v1.DataUploadUtilityExpectedHeaders;
import com.hbc.jobs.dashboard.service.ProcessFileContents;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.PostalCodeTimezoneUpload;
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
public class ProcessPostalCodeTimeZoneUploadData implements ProcessFileContents {

  @Override
  public JobTypeEnum getJobType() {
    return JobTypeEnum.UPLOAD_POSTAL_CODE_TIMEZONE;
  }

  @Override
  public List<Object> updateRequestObjectsList(JobTypeEnum jobType, InputStream inputStream)
      throws IOException, CsvException {
    return new ArrayList<>(createUploadMarketRegionsJobRequest(inputStream));
  }

  private List<PostalCodeTimezoneUpload> createUploadMarketRegionsJobRequest(
      InputStream inputStream) throws IOException {

    var bufferedReader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    var csvFormat =
        CSVFormat.DEFAULT.withHeader(
            DataUploadUtilityExpectedHeaders.getCSVExpectedHeaders("postal-code-timezone")
                .toArray(new String[0]));
    var csvParser = new CSVParser(bufferedReader, csvFormat);

    Iterable<CSVRecord> csvRecords = csvParser.getRecords();
    Iterator<CSVRecord> iterator = csvRecords.iterator();
    iterator.next();
    List<PostalCodeTimezoneUpload> postalCodeTimezoneDtoList = new ArrayList<>();

    while (iterator.hasNext()) {
      var csvRecord = iterator.next();
      var postalCodeTimezoneUpload =
          PostalCodeTimezoneUpload.builder()
              .action(csvRecord.get(ACTION))
              .orgId(csvRecord.get(ORG_ID))
              .postalCodePrefix(csvRecord.get(POSTAL_CODE_PREFIX))
              .country(csvRecord.get(COUNTRY))
              .state(csvRecord.get(STATE))
              .city(csvRecord.get(CITY))
              .latitude(csvRecord.get(LATITUDE))
              .longitude(csvRecord.get(LONGITUDE))
              .timeZone(csvRecord.get(TIMEZONE1))
              .build();
      postalCodeTimezoneDtoList.add(postalCodeTimezoneUpload);
    }

    return postalCodeTimezoneDtoList;
  }
}
