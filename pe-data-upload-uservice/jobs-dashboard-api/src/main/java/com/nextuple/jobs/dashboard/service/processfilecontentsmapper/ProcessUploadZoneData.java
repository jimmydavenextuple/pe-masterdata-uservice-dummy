/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.dashboard.service.processfilecontentsmapper;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.jobs.consumers.authentication.AuthService;
import com.nextuple.jobs.dashboard.service.ProcessFileContents;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.nextuple.jobs.framework.common.domain.pojo.RecordDto;
import com.nextuple.jobs.framework.common.domain.pojo.ZoneDataUpload;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessUploadZoneData implements ProcessFileContents {

  private static final String CSV_FILE_SUFFIX = ".csv";

  private final AuthService authService;

  @Value("${jobs-framework.kafka-publish.topic-name}")
  String dashboardProducerName;

  @Qualifier("JsonSerializerProducer")
  private final KafkaTemplate<String, RecordDto> kafkaTemplate;

  @Override
  public JobTypeEnum getJobType() {
    return JobTypeEnum.UPLOAD_ZONES;
  }

  @Override
  public List<Object> updateRequestObjectsList(JobDto jobDto, InputStream inputStream)
      throws IOException, CsvException, CommonServiceException {

    return new ArrayList<>(createUploadZonesJobRequest(jobDto.getOrgId(), inputStream));
  }

  private List<ZoneDataUpload> createUploadZonesJobRequest(String orgId, InputStream inputStream)
      throws IOException, CsvException {
    var inputStreamReader = new InputStreamReader(inputStream);
    var csvReader = new CSVReader(inputStreamReader);
    List<String[]> csvFileContents = csvReader.readAll();
    csvReader.close();
    String[] carrierServiceIdRow = csvFileContents.remove(0);
    String carrierServiceIdValue = carrierServiceIdRow[1];
    String[] sFsaListWithHeader = csvFileContents.remove(0);
    int size = csvFileContents.get(0).length;
    List<String> sFsaListWithOutHeader = Arrays.asList(sFsaListWithHeader).subList(1, size);
    List<ZoneDataUpload> zoneDataUploadList = new ArrayList<>();
    csvFileContents.stream()
        .filter(row -> row.length > 1)
        .forEach(
            row -> {
              var integer = new AtomicInteger(0);
              String destinationSfa = row[integer.getAndIncrement()];
              zoneDataUploadList.addAll(
                  createZonesDataCreationRequestObjects(
                      orgId,
                      sFsaListWithOutHeader,
                      carrierServiceIdValue,
                      row,
                      destinationSfa,
                      integer));
            });
    return zoneDataUploadList;
  }

  private List<ZoneDataUpload> createZonesDataCreationRequestObjects(
      String orgId,
      List<String> sFsaList,
      String carrierServiceIdValue,
      String[] row,
      String destinationSfa,
      AtomicInteger integer) {
    return sFsaList.stream()
        .map(
            sFsa ->
                ZoneDataUpload.builder()
                    .orgId(orgId)
                    .carrierServiceId(carrierServiceIdValue)
                    .destinationGeozone(destinationSfa)
                    .sourceGeozone(sFsa)
                    .zone(row[integer.getAndIncrement()])
                    .build())
        .collect(Collectors.toList());
  }
}
