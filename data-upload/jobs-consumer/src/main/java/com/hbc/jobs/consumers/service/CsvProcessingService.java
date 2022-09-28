package com.hbc.jobs.consumers.service;

import com.hbc.common.constants.CommonConstants;
import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.csvdownload.common.pojo.TransitDataUpload;
import com.hbc.csvdownload.domain.mapper.ProcessingLeadTimeMapper;
import com.hbc.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.hbc.csvdownload.exception.JsonParsingException;
import com.hbc.jobs.consumers.util.StringUtil;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class CsvProcessingService {

  public static final ProcessingLeadTimeMapper INSTANCE =
      Mappers.getMapper(ProcessingLeadTimeMapper.class);

  private final Logger logger = LoggerFactory.getLogger(CsvProcessingService.class);

  public String processInputCsvFile(InputStream inputStream, JobTypeEnum jobType, String orgId)
      throws JsonParsingException, IOException, CsvException {
    if (jobType == JobTypeEnum.UPLOAD_TRANSIT_TIMES) {
      logger.debug("Processing transit times upload data");
      return createUploadTransitTimesJobRequest(inputStream, orgId);
    } else if (jobType == JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES) {
      logger.debug("Processing processing lead times upload data");
      return createUploadProcessingLeadTimesJobRequest(inputStream, orgId);
    } else if (jobType == JobTypeEnum.DELETE_TRANSIT_BUFFER) {
      logger.debug("Processing delete transit buffer upload data");
      return createUploadTransitTimesJobRequest(inputStream, orgId);
    }
    return null;
  }

  private String createUploadProcessingLeadTimesJobRequest(InputStream inputStream, String orgId)
      throws JsonParsingException, IOException {

    var bufferedReader =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    var csvFormat = CSVFormat.DEFAULT.withHeader(ProcessingLeadTimesRaw.columnHeadersArray());
    var csvParser = new CSVParser(bufferedReader, csvFormat);

    Iterable<CSVRecord> csvRecords = csvParser.getRecords();
    Iterator<CSVRecord> iterator = csvRecords.iterator();
    iterator.next();
    List<ProcessingLeadTimesRaw> processingLeadTimesRawList = new ArrayList<>();

    /** CSV data parsed and map to NodeCarrierRequest object */
    while (iterator.hasNext()) {
      var csvRecord = iterator.next();
      var processingLeadTime =
          ProcessingLeadTimesRaw.builder()
              .orgId(csvRecord.get(CommonConstants.ORG_ID))
              .nodeId(csvRecord.get(CommonConstants.NODE_ID))
              .processingTime(csvRecord.get(CommonConstants.PROCESSING_TIME))
              .serviceOption(csvRecord.get(CommonConstants.SERVICE_OPTION))
              .actionType(csvRecord.get(CommonConstants.ACTION_TYPE))
              .carrierServiceId("")
              .build();
      processingLeadTimesRawList.add(processingLeadTime);
    }

    /** form job request string */
    return StringUtil.createJobRequest(processingLeadTimesRawList, orgId);
  }

  private String createUploadTransitTimesJobRequest(InputStream inputStream, String orgId)
      throws IOException, CsvException, JsonParsingException {

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
        .filter(row -> row.length != 0)
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

    return StringUtil.createJobRequest(transitDataUploadList, orgId);
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
