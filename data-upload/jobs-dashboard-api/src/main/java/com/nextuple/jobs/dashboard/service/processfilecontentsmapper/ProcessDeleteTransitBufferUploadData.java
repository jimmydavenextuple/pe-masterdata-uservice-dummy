package com.nextuple.jobs.dashboard.service.processfilecontentsmapper;

import com.nextuple.csvdownload.common.pojo.TransitDataUpload;
import com.nextuple.jobs.dashboard.service.ProcessFileContents;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class ProcessDeleteTransitBufferUploadData implements ProcessFileContents {

  @Override
  public JobTypeEnum getJobType() {
    return JobTypeEnum.DELETE_TRANSIT_BUFFER;
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
    List<String[]> csvFileData = csvReader.readAll();
    csvReader.close();

    // Extract orgId value
    String[] orgIdRow = csvFileData.remove(0);
    String orgIdValue = orgIdRow[1];
    // Extract carrierServiceId  value
    String[] carrierServiceIdRow = csvFileData.remove(0);
    String carrierServiceIdValue = carrierServiceIdRow[1];

    // Extract destination/sourceFsa header and sourceFsa values
    String[] sFsaListWithHeader = csvFileData.remove(0);

    int size = csvFileData.get(0).length;
    List<String> sFsaListWithOutHeader = Arrays.asList(sFsaListWithHeader).subList(1, size);

    List<TransitDataUpload> transitDataList = new ArrayList<>();
    csvFileData.stream()
        .filter(row -> row.length > 1)
        .forEach(
            row -> {
              var integer = new AtomicInteger(0);
              String destinationSfa = row[integer.getAndIncrement()];
              transitDataList.addAll(
                  createTransitDataCreationRequestObjects(
                      orgIdValue,
                      sFsaListWithOutHeader,
                      carrierServiceIdValue,
                      row,
                      destinationSfa,
                      integer));
            });

    return transitDataList;
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
              var transitData = new TransitDataUpload();
              transitData.setOrgId(orgId);
              transitData.setCarrierServiceId(carrierServiceIdValue);
              transitData.setDestinationGeozone(destinationSfa);
              transitData.setSourceGeozone(sFsa);
              var transitDaysString = row[integer.getAndIncrement()];
              if (!ObjectUtils.isEmpty(transitDaysString)) {
                transitData.setTransitDays(transitDaysString);
                return transitData;
              }
              return null;
            })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }
}
