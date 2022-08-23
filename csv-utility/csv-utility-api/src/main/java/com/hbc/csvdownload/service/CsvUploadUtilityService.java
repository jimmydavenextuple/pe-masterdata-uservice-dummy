package com.hbc.csvdownload.service;

import com.hbc.common.response.error.ErrorResponse;
import com.hbc.common.util.JsonUtil;
import com.hbc.csvdownload.domain.mapper.ProcessingLeadTimeMapper;
import com.hbc.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.hbc.csvdownload.exception.CsvFormatValidationFailedException;
import com.hbc.csvdownload.exception.CsvParsingException;
import com.hbc.csvdownload.exception.JobSubmissionException;
import com.hbc.csvdownload.exception.JsonParsingException;
import com.hbc.csvdownload.util.CsvUtil;
import com.hbc.jobs.framework.common.clients.JobsDashboardClient;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.utils.ExceptionUtils;
import com.hbc.node.carrier.domain.inbound.NodeCarrierRequest;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import feign.FeignException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class CsvUploadUtilityService {

  private final JobsDashboardClient jobsDashboardClient;

  public static final ProcessingLeadTimeMapper INSTANCE =
      Mappers.getMapper(ProcessingLeadTimeMapper.class);

  public String uploadProcessingLeadTimesCsv(String orgId, MultipartFile csvFile)
      throws CsvParsingException, CsvFormatValidationFailedException, JsonParsingException,
          JobSubmissionException {
    log.debug("-- Inside upload processing lead time csv service --");

    String[] expectedHeaders = ProcessingLeadTimesRaw.columnHeadersArray();

    /** validate csv headers */
    if (!CsvUtil.validateCsvHeader(csvFile, expectedHeaders)) {
      throw new CsvFormatValidationFailedException("Invalid csv template");
    }

    /** CSV data parsed and map to NodeCarrierRequest object */
    CsvToBean<ProcessingLeadTimesRaw> processingLeadTimesRawCsvToBean =
        parseCSVToProcessingLeadTimesRaw(csvFile);
    List<NodeCarrierRequest> nodeCarrierRequestList =
        mapCsvBeanToNodeCarrierRequest(processingLeadTimesRawCsvToBean);

    /** check if the list that is formed is empty */
    if (CollectionUtils.isEmpty(nodeCarrierRequestList)) {
      throw new CsvFormatValidationFailedException("CSV file can not be empty");
    }

    /** form job request string */
    String jobRequest;
    try {
      Function<List<NodeCarrierRequest>, String> dataMapper =
          request -> JsonUtil.convert(nodeCarrierRequestList);
      jobRequest = dataMapper.apply(nodeCarrierRequestList);
    } catch (Exception e) {
      log.error("Error while parsing job json string", e);
      throw new JsonParsingException("Error while parsing job json string", e, orgId);
    }

    /** invoke process jobJsonOffline method via jobsDashboardClient */
    try {
      jobsDashboardClient.processJobJsonOffline(
          JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, orgId, jobRequest);
      return "Job to bulk upload processing lead times is submitted successfully";
    } catch (FeignException e) {
      log.error("Feign exception while submitting job", e);
      ErrorResponse errorResponse = ExceptionUtils.parseFeignException(e);
      throw new JobSubmissionException(errorResponse.getMessage(), e, orgId);
    } catch (Exception e) {
      log.error("Error while submitting job to job framework", e);
      throw new JobSubmissionException("Error while submitting job to job framework", e, orgId);
    }
  }

  private List<NodeCarrierRequest> mapCsvBeanToNodeCarrierRequest(
      CsvToBean<ProcessingLeadTimesRaw> processingLeadTimesRawCsvToBean) {
    return processingLeadTimesRawCsvToBean.stream()
        .map(
            processingLeadTimesRaw -> {
              NodeCarrierRequest nodeCarrierRequest =
                  INSTANCE.convertToNodeCarrierRequest(processingLeadTimesRaw);
              nodeCarrierRequest.setCarrierServiceId("");
              return nodeCarrierRequest;
            })
        .collect(Collectors.toList());
  }

  private CsvToBean<ProcessingLeadTimesRaw> parseCSVToProcessingLeadTimesRaw(MultipartFile csvFile)
      throws CsvFormatValidationFailedException {
    // Parse CSV content to ProcessingLeadTimesRaw Bean
    try {
      int linesToSkip =
          CsvUtil.getCommentedLinesCount(csvFile, CsvUtil.predicateToFilterCommentedLine());
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
      log.error("Unexpected error occurred while parsing the CSV Content to Bean", e);
      throw new CsvFormatValidationFailedException("Error in parsing the csv file", e);
    }
  }
}
