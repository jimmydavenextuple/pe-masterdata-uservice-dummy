package com.hbc.csvdownload.service;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.csvdownload.domain.mapper.ProcessingLeadTimeMapper;
import com.hbc.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.hbc.csvdownload.exception.CsvFormatValidationFailedException;
import com.hbc.csvdownload.exception.CsvParsingException;
import com.hbc.csvdownload.exception.JobSubmissionException;
import com.hbc.csvdownload.util.CsvUtil;
import com.hbc.dataupload.common.constants.DataUploadUtilityConstants;
import com.hbc.jobs.framework.common.clients.JobsDashboardClient;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.utils.ExceptionUtils;
import com.hbc.transit.domain.feign.TransitBufferConfigRequestFeign;
import com.hbc.transit.domain.inbound.TransitBufferConfigRequest;
import com.hbc.transit.domain.outbound.TransitBufferConfigResponse;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import feign.FeignException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CsvUploadUtilityService {

  private final Logger logger = LoggerFactory.getLogger(CsvUploadUtilityService.class);
  private final JobsDashboardClient jobsDashboardClient;

  public static final ProcessingLeadTimeMapper INSTANCE =
      Mappers.getMapper(ProcessingLeadTimeMapper.class);

  private final TransitBufferConfigRequestFeign transitBufferConfigRequestFeign;

  private static final String ORG_ID = "orgId";

  public String uploadProcessingLeadTimesCsv(String orgId, MultipartFile csvFile)
      throws CsvFormatValidationFailedException, JobSubmissionException, CsvParsingException,
          IOException, CsvException {
    logger.debug("-- Inside upload processing lead time csv service --");

    String[] expectedHeaders = ProcessingLeadTimesRaw.columnHeadersArray();

    /** validate csv headers */
    if (!CsvUtil.validateCsvHeader(csvFile, expectedHeaders)) {
      throw new CsvFormatValidationFailedException("Invalid csv template", null);
    }

    /** reading the csv file contents */
    List<String[]> csvFileContents = readCsvContents(csvFile);

    /** check if the list that is formed is empty */
    if (CollectionUtils.isEmpty(csvFileContents) || csvFileContents.size() == 1) {
      logger.error("CSV file can not be empty");
      throw new CsvFormatValidationFailedException("CSV file can not be empty", null);
    }

    /** invoke process jobJsonOffline method via jobsDashboardClient */
    submitJob(orgId, JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, csvFile);

    return "Job to bulk upload processing lead times is submitted successfully";
  }

  public String uploadTransitTimesCsv(String orgId, MultipartFile csvFile)
      throws IOException, CsvException, CsvFormatValidationFailedException, JobSubmissionException {

    // validate file type

    List<String[]> csvFileContents = readCsvContents(csvFile);

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

    /** invoke process jobJsonOffline method via jobsDashboardClient */
    submitJob(orgId, JobTypeEnum.UPLOAD_TRANSIT_TIMES, csvFile);
    return "Job to bulk upload transit times is submitted successfully";
  }

  private void validateHeaders(String orgIdHeader, String carrierServiceIdHeader, String fsaHeader)
      throws CsvFormatValidationFailedException {
    if (!orgIdHeader.equals(ORG_ID)) {
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

  private static List<String[]> readCsvContents(MultipartFile csvFile)
      throws IOException, CsvException {
    var inputStreamReader = new InputStreamReader(csvFile.getInputStream());
    var csvReader = new CSVReader(inputStreamReader);
    List<String[]> csvFileContents = csvReader.readAll();
    csvReader.close();
    return csvFileContents;
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

  public TransitBufferConfigResponse uploadTransitBufferData(
      TransitBufferConfigRequest transitBufferConfigRequest) throws CommonServiceException {
    transitBufferConfigRequest.setAction(DataUploadUtilityConstants.CREATE_C);
    return callTransitBufferApi(transitBufferConfigRequest);
  }

  public TransitBufferConfigResponse updatingTransitBufferData(
      TransitBufferConfigRequest transitBufferConfigRequest) throws CommonServiceException {
    transitBufferConfigRequest.setAction(DataUploadUtilityConstants.UPDATE_U);
    return callTransitBufferApi(transitBufferConfigRequest);
  }

  public void deletingTransitBufferData(Long transitBufferRequestId, String createdBy)
      throws CommonServiceException {
    try {
      transitBufferConfigRequestFeign.deleteTransitBufferConfigRequest(
          transitBufferRequestId, createdBy);
    } catch (FeignException e) {
      logger.error("Feign exception while deleting transit buffer records", e);
      var errorResponse = ExceptionUtils.parseFeignException(e);
      throw new CommonServiceException(
          errorResponse.getMessage(), HttpStatus.BAD_REQUEST, 0xfffff5, null);
    } catch (Exception e) {
      logger.error("Error while deleting transit buffer records", e);
      throw new CommonServiceException(e.getMessage(), HttpStatus.BAD_REQUEST, 0xfffff5, null);
    }
  }

  private TransitBufferConfigResponse callTransitBufferApi(
      TransitBufferConfigRequest transitBufferConfigRequest) throws CommonServiceException {
    try {
      return transitBufferConfigRequestFeign
          .processTransitBufferConfigRequest(transitBufferConfigRequest)
          .getPayload();
    } catch (FeignException e) {
      logger.error("Feign exception while processing transit buffer records", e);
      var errorResponse = ExceptionUtils.parseFeignException(e);
      throw new CommonServiceException(
          errorResponse.getMessage(), HttpStatus.BAD_REQUEST, 0xfffff4, null);
    } catch (Exception e) {
      logger.error("Error while processing transit buffer records", e);
      throw new CommonServiceException(e.getMessage(), HttpStatus.BAD_REQUEST, 0xfffff4, null);
    }
  }
}
