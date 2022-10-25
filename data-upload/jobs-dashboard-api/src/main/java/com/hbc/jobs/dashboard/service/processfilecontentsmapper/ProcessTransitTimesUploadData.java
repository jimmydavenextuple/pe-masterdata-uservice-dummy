package com.hbc.jobs.dashboard.service.processfilecontentsmapper;

import com.hbc.csvdownload.common.pojo.TransitDataUpload;
import com.hbc.jobs.dashboard.service.ProcessFileContents;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class ProcessTransitTimesUploadData implements ProcessFileContents {

  @Override
  public JobTypeEnum getJobType() {
    return JobTypeEnum.UPLOAD_TRANSIT_TIMES;
  }

  @Override
  public List<Object> updateRequestObjectsList(JobTypeEnum jobType, InputStream inputStream)
      throws IOException, CsvException {
    return new ArrayList<>(createUploadTransitTimesJobRequest(inputStream));
  }

  private List<TransitDataUpload> createUploadTransitTimesJobRequest(InputStream inputStream)
      throws IOException, CsvException {

    var inputStreamReader = new InputStreamReader(inputStream);
    var csvReader = new CSVReader(inputStreamReader);
    List<String[]> csvFileContents = csvReader.readAll();
    csvReader.close();

    // Extract orgId value
    String[] orgIdRow = csvFileContents.remove(0);
    String orgIdValue = orgIdRow[1];
    // Extract carrierServiceId  value
    String[] carrierServiceIdRow = csvFileContents.remove(0);
    String carrierServiceIdValue = carrierServiceIdRow[1];

    // Extract destination/sourceFsa header and sourceFsa values
    String[] sFsaListWithHeader = csvFileContents.remove(0);

    int size = csvFileContents.get(0).length;
    List<String> sFsaListWithOutHeader = Arrays.asList(sFsaListWithHeader).subList(1, size);

    List<TransitDataUpload> transitDataUploadList = new ArrayList<>();
    csvFileContents.stream()
        .filter(row -> row.length > 1)
        .forEach(
            row -> {
              var integer = new AtomicInteger(0);
              String destinationSfa = row[integer.getAndIncrement()];
              transitDataUploadList.addAll(
                  createTransitDataCreationRequestObjects(
                      orgIdValue,
                      sFsaListWithOutHeader,
                      carrierServiceIdValue,
                      row,
                      destinationSfa,
                      integer));
            });

    return transitDataUploadList;
  }

  private List<TransitDataUpload> createTransitDataCreationRequestObjects(
      String orgId,
      List<String> sFsaList,
      String carrierServiceIdValue,
      String[] row,
      String destinationSfa,
      AtomicInteger integer) {
    return sFsaList.stream()
        .map(
            sFsa -> {
              var transitDataUpload = new TransitDataUpload();
              transitDataUpload.setOrgId(orgId);
              transitDataUpload.setCarrierServiceId(carrierServiceIdValue);
              transitDataUpload.setDestinationGeozone(destinationSfa);
              transitDataUpload.setSourceGeozone(sFsa);
              var transitDaysString = row[integer.getAndIncrement()];
              if (!ObjectUtils.isEmpty(transitDaysString)) {
                transitDataUpload.setTransitDays(transitDaysString);
                return transitDataUpload;
              }
              return null;
            })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }
}
