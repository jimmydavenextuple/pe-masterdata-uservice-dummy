package com.hbc.csvdownload.service;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.csvdownload.domain.mapper.ProcessingLeadTimeMapper;
import com.hbc.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.hbc.csvdownload.exception.CsvDataValidationException;
import com.hbc.csvdownload.exception.CsvFormatValidationFailedException;
import com.hbc.csvdownload.exception.CsvParsingException;
import com.hbc.csvdownload.exception.InvalidActionType;
import com.hbc.csvdownload.exception.JobSubmissionException;
import com.hbc.csvdownload.util.CsvUtil;
import com.hbc.dataupload.common.constants.DataUploadUtilityConstants;
import com.hbc.jobs.framework.common.clients.JobsDashboardClient;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.utils.ExceptionUtils;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import feign.FeignException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CsvUploadUtilityService {

  private final Logger logger = LoggerFactory.getLogger(CsvUploadUtilityService.class);
  private final JobsDashboardClient jobsDashboardClient;

  public static final ProcessingLeadTimeMapper INSTANCE =
      Mappers.getMapper(ProcessingLeadTimeMapper.class);

  public String uploadProcessingLeadTimesCsv(String orgId, MultipartFile csvFile)
      throws CsvFormatValidationFailedException, JobSubmissionException, CsvParsingException {
    logger.debug("-- Inside upload processing lead time csv service --");

    String[] expectedHeaders = ProcessingLeadTimesRaw.columnHeadersArray();

    /** validate csv headers */
    if (!CsvUtil.validateCsvHeader(csvFile, expectedHeaders)) {
      throw new CsvFormatValidationFailedException("Invalid csv template", null);
    }

    /** CSV data parsed and map to NodeCarrierRequest object */
    CsvToBean<ProcessingLeadTimesRaw> processingLeadTimesRawCsvToBean =
        parseCSVToProcessingLeadTimesRaw(csvFile);

    List<ProcessingLeadTimesRaw> processingLeadTimesList =
        mapCsvBeanToProcessingLeadTime(processingLeadTimesRawCsvToBean);

    /** check if the list that is formed is empty */
    if (CollectionUtils.isEmpty(processingLeadTimesList)) {
      logger.error("CSV file can not be empty");
      throw new CsvFormatValidationFailedException("CSV file can not be empty", null);
    }

    /** invoke process jobJsonOffline method via jobsDashboardClient */
    submitJob(orgId, JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, csvFile);

    return "Job to bulk upload processing lead times is submitted successfully";
  }

  private List<ProcessingLeadTimesRaw> mapCsvBeanToProcessingLeadTime(
      CsvToBean<ProcessingLeadTimesRaw> processingLeadTimesRawCsvToBean) {
    var rowIndex = new AtomicInteger(0);
    return processingLeadTimesRawCsvToBean.stream()
        .map(
            processingLeadTimesRaw -> {
              if (!NumberUtils.isCreatable(processingLeadTimesRaw.getProcessingTime())) {
                logger.error(
                    "Invalid processing lead time: {} in row: {}",
                    processingLeadTimesRaw.getProcessingTime(),
                    rowIndex);
                throw new CsvDataValidationException(
                    "Invalid processing lead time: "
                        + processingLeadTimesRaw.getProcessingTime()
                        + " in row: "
                        + rowIndex);
              }
              if (!ObjectUtils.isEmpty(processingLeadTimesRaw.getActionType())) {
                if (DataUploadUtilityConstants.UPDATE_U.equalsIgnoreCase(
                        processingLeadTimesRaw.getActionType())
                    || DataUploadUtilityConstants.DELETE_D.equalsIgnoreCase(
                        processingLeadTimesRaw.getActionType())) {
                  rowIndex.getAndIncrement();
                  return processingLeadTimesRaw;
                } else {
                  logger.error(
                      "Invalid action type: {} in row: {}. Valid values U and D",
                      processingLeadTimesRaw.getActionType(),
                      rowIndex.get());
                  throw new InvalidActionType(
                      "Invalid action type: "
                          + processingLeadTimesRaw.getActionType()
                          + " in row: "
                          + rowIndex.get()
                          + ". Valid values U and D",
                      processingLeadTimesRaw.getActionType(),
                      rowIndex.getAndIncrement());
                }
              } else {
                logger.error("Action can not be blank in row: {}", rowIndex.get());
                throw new InvalidActionType(
                    "Action can not be blank in row: " + rowIndex.get(),
                    null,
                    rowIndex.getAndIncrement());
              }
            })
        .collect(Collectors.toList());
  }

  private CsvToBean<ProcessingLeadTimesRaw> parseCSVToProcessingLeadTimesRaw(MultipartFile csvFile)
      throws CsvFormatValidationFailedException {
    // Parse CSV content to ProcessingLeadTimesRaw Bean
    try {
      int linesToSkip =
          CsvUtil.getCommentedLinesCount(
              csvFile.getInputStream(), CsvUtil.predicateToFilterCommentedLine());
      return new CsvToBeanBuilder<ProcessingLeadTimesRaw>(
              new InputStreamReader(csvFile.getInputStream()))
          .withSkipLines(linesToSkip)
          .withType(ProcessingLeadTimesRaw.class)
          .withIgnoreLeadingWhiteSpace(true)
          .withFilter(
              rows -> {
                for (String row : rows) {
                  if (!StringUtils.isEmpty(row)) {
                    return true;
                  }
                }
                return false;
              })
          .build();
    } catch (Exception e) {
      logger.error("Unexpected error occurred while parsing the CSV Content to Bean", e);
      throw new CsvFormatValidationFailedException("Error in parsing the csv file", e, null);
    }
  }

  public String uploadTransitTimesCsv(String orgId, MultipartFile csvFile)
      throws IOException, CsvException, CsvFormatValidationFailedException, JobSubmissionException {

    // validate file type

    var inputStreamReader = new InputStreamReader(csvFile.getInputStream());
    var csvReader = new CSVReader(inputStreamReader);
    List<String[]> csvFileContents = csvReader.readAll();
    csvReader.close();

    if (CollectionUtils.isEmpty(csvFileContents)) {
      logger.error("CSV file can't be empty");
      throw new CsvFormatValidationFailedException("CSV file can't be empty", null);
    }

    // Extract orgId header and value
    String[] orgIdRow = csvFileContents.remove(0);
    String orgIdHeader = orgIdRow[0];

    // Extract carrierServiceId header and value
    String[] carrierServiceIdRow = csvFileContents.remove(0);
    String carrierServiceIdHeader = carrierServiceIdRow[0];

    // Extract destination/sourceFsa header and sourceFsa values
    String[] sFsaListWithHeader = csvFileContents.remove(0);

    // validate csv the headers
    validateHeaders(orgIdHeader, carrierServiceIdHeader, sFsaListWithHeader[0]);

    int size = csvFileContents.get(0).length;
    List<String> sFsaListWithOutHeader = Arrays.asList(sFsaListWithHeader).subList(1, size);

    csvFileContents.stream()
        .filter(row -> row.length != 0)
        .forEach(
            row -> {
              var integer = new AtomicInteger(0);
              String destinationSfa = row[integer.getAndIncrement()];
              validateTransitDaysInput(sFsaListWithOutHeader, row, destinationSfa, integer);
            });

    /** invoke process jobJsonOffline method via jobsDashboardClient */
    submitJob(orgId, JobTypeEnum.UPLOAD_TRANSIT_TIMES, csvFile);
    return "Job to bulk upload transit times is submitted successfully";
  }

  private void validateTransitDaysInput(
      List<String> sFsaList, String[] row, String destinationSfa, AtomicInteger integer) {
    sFsaList.forEach(
        sFsa -> {
          var transitDaysString = row[integer.getAndIncrement()];
          if (!ObjectUtils.isEmpty(transitDaysString)
              && !NumberUtils.isCreatable(transitDaysString)) {
            logger.error(
                "Invalid transit days: {} for destinationFsa: {} and sourceFsa: {}",
                transitDaysString,
                destinationSfa,
                sFsa);
            throw new CsvDataValidationException(
                "Invalid transit days: "
                    + transitDaysString
                    + " for destinatonFsa: "
                    + destinationSfa
                    + " and sourseFsa: "
                    + sFsa
                    + " combination");
          }
        });
  }

  private void validateHeaders(String orgIdHeader, String carrierServiceIdHeader, String fsaHeader)
      throws CsvFormatValidationFailedException {
    if (!orgIdHeader.equals("orgId")) {
      logger.error("Invalid header orgId");
      throw new CsvFormatValidationFailedException("Invalid header orgId", orgIdHeader);
    }
    if (!carrierServiceIdHeader.equals("Carrier Service:")) {
      logger.error("Invalid header carrierServiceIdHeader");
      throw new CsvFormatValidationFailedException(
          "Invalid header carrierServiceIdHeader", carrierServiceIdHeader);
    }
    if (!fsaHeader.equals("Destination FSA / Source FSA ->")) {
      logger.error("Invalid header fsaHeader");
      throw new CsvFormatValidationFailedException("Invalid header fsaHeader", fsaHeader);
    }
  }

  private void submitJob(String orgId, JobTypeEnum jobType, MultipartFile csvFile)
      throws JobSubmissionException {
    try {
      jobsDashboardClient.processJobOffline(
          orgId, jobType, csvFile.getBytes(), csvFile.getOriginalFilename());
    } catch (FeignException e) {
      logger.error("Feign exception while submitting job", e);
      var errorResponse = ExceptionUtils.parseFeignException(e);
      throw new JobSubmissionException(errorResponse.getMessage(), e, orgId);
    } catch (Exception e) {
      logger.error("Error while submitting job to job framework", e);
      throw new JobSubmissionException("Error while submitting job to job framework", e, orgId);
    }
  }
}
