package com.nextuple.dataupload.service;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.BUFFER_HOURS;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CARRIER_SERVICE_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DATE_FORMAT;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DELETE_D;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DESTINATION_GEO_ZONE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.END_TIME;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.FILE_URI;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.NODE_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ORG_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_OPTION;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SOURCE_AND_DESTINATION_GEO_ZONE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SOURCE_GEO_ZONE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.START_TIME;
import static com.nextuple.dataupload.helper.UploadBufferDataConstants.NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.nextuple.dataupload.helper.UploadBufferDataConstants.NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_INCORRECT_DATA;
import static com.nextuple.dataupload.helper.UploadBufferDataConstants.NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.nextuple.dataupload.helper.UploadBufferDataConstants.NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.nextuple.dataupload.helper.UploadBufferDataConstants.NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.nextuple.dataupload.helper.UploadBufferDataConstants.NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_LARGE_ROW_SIZE;
import static com.nextuple.dataupload.helper.UploadBufferDataConstants.TRANSIT_BUFFER_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.nextuple.dataupload.helper.UploadBufferDataConstants.TRANSIT_BUFFER_DATA_UPLOAD_INCORRECT_DATA;
import static com.nextuple.dataupload.helper.UploadBufferDataConstants.TRANSIT_BUFFER_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.nextuple.dataupload.helper.UploadBufferDataConstants.TRANSIT_BUFFER_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.nextuple.dataupload.helper.UploadBufferDataConstants.TRANSIT_BUFFER_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.nextuple.dataupload.helper.UploadBufferDataConstants.TRANSIT_BUFFER_DATA_UPLOAD_LARGE_ROW_SIZE;
import static com.opencsv.ICSVWriter.DEFAULT_ESCAPE_CHARACTER;
import static com.opencsv.ICSVWriter.DEFAULT_LINE_END;
import static com.opencsv.ICSVWriter.DEFAULT_SEPARATOR;
import static com.opencsv.ICSVWriter.NO_QUOTE_CHARACTER;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.csvdownload.exception.CsvFormatValidationFailedException;
import com.nextuple.csvdownload.exception.JobSubmissionException;
import com.nextuple.dataupload.common.headers.DataUploadUtilityExpectedHeaders;
import com.nextuple.dataupload.common.utils.DataUploadUtil;
import com.nextuple.jobs.framework.common.clients.JobsDashboardClient;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.utils.ExceptionUtils;
import com.nextuple.node.carrier.domain.feign.NodeCarrierFeign;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierBufferRequest;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.transit.domain.feign.TransitFeign;
import com.nextuple.transit.domain.inbound.DistinctGeozonesResponse;
import com.nextuple.transit.domain.inbound.TransitBufferCreationRequest;
import com.nextuple.transit.domain.outbound.TransitResponse;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import feign.FeignException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class UploadBufferService {

  @Value("${dataupload.base-path}")
  private String basePath;

  @Value("${dataupload.maxsize-in-kilobytes}")
  private double maxSizeInKiloBytes;

  @Value("${dataupload.max-rows}")
  private long maxRows;

  private static final String DELETE_TRANSIT_BUFFER = "delete-transit-buffer";
  private final NodeCarrierFeign nodeCarrierFeign;
  private final TransitFeign transitFeign;
  private final JobsDashboardClient jobsDashboardClient;
  private final TransitDataService transitDataService;

  public ResponseEntity<BaseResponse<String>> uploadNodeServiceOptionBufferData(String fileUri)
      throws CommonServiceException, IOException {
    var path = DataUploadUtil.getPath(basePath, fileUri);

    DataUploadUtil.validateFileType(
        fileUri, NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_INVALID_FILE_TYPE);
    DataUploadUtil.validateFileSize(
        path, fileUri, maxSizeInKiloBytes, NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_LARGE_FILE_SIZE);
    DataUploadUtil.validateFileRows(
        path, fileUri, maxRows, NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_LARGE_ROW_SIZE);
    DataUploadUtil.checkForEmptyRecords(
        path, fileUri, NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_FILE_EMPTY_RECORDS);

    Map<String, Boolean> resultMap = nodeServiceOptionBufferCsvReader(path);
    return DataUploadUtil.getResponse(resultMap, "Node ServiceOption Buffer");
  }

  private Map<String, Boolean> nodeServiceOptionBufferCsvReader(Path path)
      throws IOException, CommonServiceException {
    var isAllFailedForNodeServiceOptionBuffer = true;
    var isAllPassedForNodeServiceOptionBuffer = true;
    var nodeServiceOptionBufferResult = false;

    try (Reader reader = Files.newBufferedReader(path);
        var csvParser = DataUploadUtil.getCSVParser(reader)) {
      DataUploadUtil.compareHeaders(
          csvParser,
          "node-service-option-buffer",
          NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_INVALID_FILE_HEADERS);

      for (CSVRecord csvRecord : csvParser) {
        long row = csvParser.getCurrentLineNumber();
        try {
          // Accessing Values by Column Header Name
          String orgId = csvRecord.get(ORG_ID);
          String nodeId = csvRecord.get(NODE_ID);
          String serviceOption = csvRecord.get(SERVICE_OPTION);
          Double bufferHours = Double.parseDouble(csvRecord.get(BUFFER_HOURS));
          bufferHours = Double.valueOf(new DecimalFormat("0.00").format(bufferHours));
          var bufferStartDT =
              LocalDateTime.parse(
                  csvRecord.get(START_TIME), DateTimeFormatter.ofPattern(DATE_FORMAT));
          var bufferEndDT =
              LocalDateTime.parse(
                  csvRecord.get(END_TIME), DateTimeFormatter.ofPattern(DATE_FORMAT));

          var bufferStartDateTime =
              Date.from(bufferStartDT.atZone(ZoneId.systemDefault()).toInstant());
          var bufferEndDateTime = Date.from(bufferEndDT.atZone(ZoneId.systemDefault()).toInstant());

          if (bufferHours < 0.00 && bufferStartDateTime != null && bufferEndDateTime != null) {
            log.error(
                "Incorrect Buffer Details for: " + orgId + "::" + nodeId + "::" + serviceOption);
            Map<String, FieldError> errorMap = new HashMap<>();
            errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
            errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
            errorMap.put(SERVICE_OPTION, FieldError.builder().rejectedValue(serviceOption).build());
            throw new CommonServiceException(
                NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_INCORRECT_DATA,
                HttpStatus.BAD_REQUEST,
                0x1772,
                errorMap);
          }

          var nodeServiceOptionBufferRequest =
              NodeCarrierBufferRequest.builder()
                  .nodeId(nodeId)
                  .orgId(orgId)
                  .serviceOption(serviceOption)
                  .bufferHours(bufferHours)
                  .bufferStartDate(bufferStartDateTime)
                  .bufferEndDate(bufferEndDateTime)
                  .build();
          BaseResponse<NodeCarrierResponse> baseResponse;
          baseResponse = nodeCarrierFeign.updateBuffer(nodeServiceOptionBufferRequest);
          nodeServiceOptionBufferResult = baseResponse.isSuccess();
          log.debug(baseResponse.getMessage());
        } catch (Exception e) {
          if (isAllPassedForNodeServiceOptionBuffer) {
            isAllPassedForNodeServiceOptionBuffer = false;
          }
          log.error("Failed to store Node ServiceOption Buffer CSV data for row number : {}", row);
        }

        if (isAllPassedForNodeServiceOptionBuffer) {
          isAllPassedForNodeServiceOptionBuffer = nodeServiceOptionBufferResult;
        }
        if (isAllFailedForNodeServiceOptionBuffer) {
          isAllFailedForNodeServiceOptionBuffer = !nodeServiceOptionBufferResult;
        }
      }
      return DataUploadUtil.storeToMap(
          isAllPassedForNodeServiceOptionBuffer, isAllFailedForNodeServiceOptionBuffer);
    }
  }

  public ResponseEntity<BaseResponse<String>> uploadTransitBufferData(String fileUri)
      throws CommonServiceException, IOException, CsvException {
    var path = DataUploadUtil.getPath(basePath, fileUri);

    DataUploadUtil.validateFileType(fileUri, TRANSIT_BUFFER_DATA_UPLOAD_INVALID_FILE_TYPE);
    DataUploadUtil.validateFileSize(
        path, fileUri, maxSizeInKiloBytes, TRANSIT_BUFFER_DATA_UPLOAD_LARGE_FILE_SIZE);
    DataUploadUtil.validateFileRows(
        path, fileUri, maxRows, TRANSIT_BUFFER_DATA_UPLOAD_LARGE_ROW_SIZE);
    checkForEmptyFile(fileUri, path);

    Map<String, Boolean> resultMap = transitBufferCsvReader(path);
    return DataUploadUtil.getResponse(resultMap, "Transit Buffer");
  }

  private static void checkForEmptyFile(String fileUri, Path path)
      throws CommonServiceException, IOException {
    long numberOfRowsInFile;
    try (Stream<String> stringStream = Files.lines(path)) {
      numberOfRowsInFile = stringStream.count();
    }
    if (numberOfRowsInFile < 1) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(FILE_URI, FieldError.builder().rejectedValue(fileUri).build());
      throw new CommonServiceException(
          TRANSIT_BUFFER_DATA_UPLOAD_FILE_EMPTY_RECORDS, HttpStatus.BAD_REQUEST, 0x2775, errorMap);
    }
  }

  private Map<String, Boolean> transitBufferCsvReader(Path path)
      throws IOException, CommonServiceException, CsvException {
    var isAllFailedForTransitBuffer = true;
    var isAllPassedForTransitBuffer = true;
    var transitBufferResult = false;

    try (var reader = new BufferedReader(new FileReader(path.toFile()))) {
      var csvReader = new CSVReader(reader);
      List<String[]> csvFileContents = csvReader.readAll();
      csvReader.close();

      // Extract orgId header and value
      String[] orgIdRow = csvFileContents.remove(0);
      String orgIdHeader = orgIdRow[0];

      // Extract carrierServiceId header and value
      String[] carrierServiceIdRow = csvFileContents.remove(0);
      String carrierServiceIdHeader = carrierServiceIdRow[0];

      // Extract buffer start time header and value
      String[] startTimeRow = csvFileContents.remove(0);
      String startTimeHeader = startTimeRow[0];

      // Extract buffer end time header and value
      String[] endTimeRow = csvFileContents.remove(0);
      String endTimeHeader = endTimeRow[0];

      // Extract destination/sourceFsa header and sourceFsa values
      String[] sFsaListWithHeader = csvFileContents.remove(0);

      // validate csv the headers
      validateHeaders(
          List.of(
              orgIdHeader,
              carrierServiceIdHeader,
              startTimeHeader,
              endTimeHeader,
              sFsaListWithHeader[0]),
          "transit-buffer");

      // store orgId and carrierServiceId into variables
      String orgIdValue = orgIdRow[1];
      String carrierServiceIdValue = carrierServiceIdRow[1];
      String startTimeValue = startTimeRow[1];
      String endTimeValue = endTimeRow[1];

      if (ObjectUtils.isEmpty(startTimeValue) || ObjectUtils.isEmpty(endTimeValue)) {
        log.error(
            "Incorrect Buffer Details for: {} :: {} :: {} :: {}",
            orgIdValue,
            carrierServiceIdValue,
            null,
            null);
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgIdValue).build());
        errorMap.put(
            CARRIER_SERVICE_ID, FieldError.builder().rejectedValue(carrierServiceIdValue).build());
        errorMap.put(SOURCE_GEO_ZONE, FieldError.builder().rejectedValue(null).build());
        errorMap.put(DESTINATION_GEO_ZONE, FieldError.builder().rejectedValue(null).build());
        throw new CommonServiceException(
            TRANSIT_BUFFER_DATA_UPLOAD_INCORRECT_DATA, HttpStatus.BAD_REQUEST, 0x1772, errorMap);
      }

      int size = csvFileContents.get(0).length;

      var bufferStartDT =
          LocalDateTime.parse(startTimeValue, DateTimeFormatter.ofPattern(DATE_FORMAT));
      var bufferEndDT = LocalDateTime.parse(endTimeValue, DateTimeFormatter.ofPattern(DATE_FORMAT));

      var bufferStartDateTime = Date.from(bufferStartDT.atZone(ZoneId.systemDefault()).toInstant());

      var bufferEndDateTime = Date.from(bufferEndDT.atZone(ZoneId.systemDefault()).toInstant());

      List<String> sourceGeozoneListListWithOutHeader =
          Arrays.asList(sFsaListWithHeader).subList(1, size);

      List<TransitBufferCreationRequest> transitBufferCreationRequests = new ArrayList<>();
      for (String[] row : csvFileContents) {
        if (row.length != 0) {
          var index = 0;
          transitBufferCreationRequests.addAll(
              createTransitDataCreationRequestObjects(
                  orgIdValue,
                  sourceGeozoneListListWithOutHeader,
                  carrierServiceIdValue,
                  row,
                  bufferStartDateTime,
                  bufferEndDateTime,
                  index));
        }
      }

      var rowIndex = 0L;
      for (TransitBufferCreationRequest transitBufferRequest : transitBufferCreationRequests) {
        try {
          BaseResponse<TransitResponse> baseResponse;
          baseResponse = transitFeign.updateTransitBufferDetails(transitBufferRequest);
          transitBufferResult = baseResponse.isSuccess();
          log.debug(baseResponse.getMessage());
        } catch (Exception e) {
          if (isAllPassedForTransitBuffer) {
            isAllPassedForTransitBuffer = false;
          }
          log.error("Failed to store Transit Buffer CSV data for row number : {}", rowIndex);
        }

        if (isAllPassedForTransitBuffer) {
          isAllPassedForTransitBuffer = transitBufferResult;
        }
        if (isAllFailedForTransitBuffer) {
          isAllFailedForTransitBuffer = !transitBufferResult;
        }
        rowIndex++;
      }
      return DataUploadUtil.storeToMap(isAllPassedForTransitBuffer, isAllFailedForTransitBuffer);
    }
  }

  private List<TransitBufferCreationRequest> createTransitDataCreationRequestObjects(
      String orgId,
      List<String> sourceGeoZoneList,
      String carrierServiceIdValue,
      String[] row,
      Date bufferStartDateTime,
      Date bufferEndDateTime,
      int index)
      throws CommonServiceException {
    List<TransitBufferCreationRequest> transitBufferCreationRequestList = new ArrayList<>();
    var destinationGeozone = row[index++];
    for (String sourceGeoZone : sourceGeoZoneList) {
      if (bufferStartDateTime != null && bufferEndDateTime != null) {
        var transitBufferRequest =
            TransitBufferCreationRequest.builder()
                .orgId(orgId)
                .carrierServiceId(carrierServiceIdValue)
                .sourceGeozone(sourceGeoZone)
                .destinationGeozone(destinationGeozone)
                .bufferDays(Double.valueOf(row[index++]))
                .bufferStartDate(bufferStartDateTime)
                .bufferEndDate(bufferEndDateTime)
                .build();
        transitBufferCreationRequestList.add(transitBufferRequest);
      } else {
        log.error(
            "Incorrect Buffer Details for: {} :: {} :: {} :: {}",
            orgId,
            carrierServiceIdValue,
            sourceGeoZone,
            destinationGeozone);
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
        errorMap.put(
            CARRIER_SERVICE_ID, FieldError.builder().rejectedValue(carrierServiceIdValue).build());
        errorMap.put(SOURCE_GEO_ZONE, FieldError.builder().rejectedValue(sourceGeoZone).build());
        errorMap.put(
            DESTINATION_GEO_ZONE, FieldError.builder().rejectedValue(destinationGeozone).build());
        throw new CommonServiceException(
            TRANSIT_BUFFER_DATA_UPLOAD_INCORRECT_DATA, HttpStatus.BAD_REQUEST, 0x1772, errorMap);
      }
    }
    return transitBufferCreationRequestList;
  }

  private void validateHeaders(List<String> headersList, String expectedHeadersKey)
      throws CommonServiceException {
    List<String> expectedHeaders =
        DataUploadUtilityExpectedHeaders.getCSVExpectedHeaders(expectedHeadersKey);

    if (!headersList.equals(expectedHeaders)) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          FILE_URI,
          FieldError.builder()
              .rejectedValue(headersList)
              .actualValue(expectedHeaders)
              .errorMessage("CSV File Headers are invalid")
              .build());
      throw new CommonServiceException(
          TRANSIT_BUFFER_DATA_UPLOAD_INVALID_FILE_HEADERS,
          HttpStatus.BAD_REQUEST,
          0x2777,
          errorMap);
    }
  }

  public String deleteTransitBuffer(String fileUri)
      throws CommonServiceException, IOException, CsvException, CsvFormatValidationFailedException,
          JobSubmissionException {
    var path = DataUploadUtil.getPath(basePath, fileUri);

    DataUploadUtil.validateFileType(fileUri, TRANSIT_BUFFER_DATA_UPLOAD_INVALID_FILE_TYPE);
    DataUploadUtil.validateFileSize(
        path, fileUri, maxSizeInKiloBytes, TRANSIT_BUFFER_DATA_UPLOAD_LARGE_FILE_SIZE);
    DataUploadUtil.validateFileRows(
        path, fileUri, maxRows, TRANSIT_BUFFER_DATA_UPLOAD_LARGE_ROW_SIZE);
    checkForEmptyFile(fileUri, path);

    return deleteTransitBufferCsvReader(path);
  }

  private String deleteTransitBufferCsvReader(Path path)
      throws IOException, CsvException, CommonServiceException, CsvFormatValidationFailedException,
          JobSubmissionException {

    List<String[]> csvFileContents = readCsvContents(path);

    String[] orgIdRow = csvFileContents.remove(0);
    String[] carrierServiceIdRow = csvFileContents.remove(0);
    String[] actionTypeRow = csvFileContents.remove(0);

    String orgIdHeader = orgIdRow[0];
    String carrierServiceIdHeader = carrierServiceIdRow[0];
    String actionTypeHeader = actionTypeRow[0];

    validateHeaders(
        List.of(orgIdHeader, carrierServiceIdHeader, actionTypeHeader), DELETE_TRANSIT_BUFFER);

    String orgIdValue = orgIdRow[1];
    String carrierServiceIdValue = carrierServiceIdRow[1];
    String actionTypeValue = actionTypeRow[1];

    if (ObjectUtils.isEmpty(orgIdValue)) {
      log.error("Org id can not be empty");
      throw new CsvFormatValidationFailedException("Org id can not be empty", orgIdHeader);
    } else if (ObjectUtils.isEmpty(carrierServiceIdValue)) {
      log.error("Carrier service id can not be null");
      throw new CsvFormatValidationFailedException(
          "Carrier service id can not be null", carrierServiceIdHeader);
    } else if (ObjectUtils.isEmpty(actionTypeValue)) {
      log.error("action can not be null");
      throw new CsvFormatValidationFailedException("action can not be null", actionTypeHeader);
    }

    DistinctGeozonesResponse geozonesResponse =
        transitDataService.getDistinctGeozonesList(orgIdValue, carrierServiceIdValue);

    List<String> distinctSourceGeozones = new ArrayList<>(geozonesResponse.getSourceGeozones());
    List<String> distinctDestinationGeozones =
        new ArrayList<>(geozonesResponse.getDestinationGeozones());

    List<String[]> destinationFsaAndActionList =
        distinctDestinationGeozones.stream()
            .map(
                dFsa -> {
                  var actionTypesList =
                      distinctSourceGeozones.stream()
                          .map(sFsa -> DELETE_D)
                          .collect(Collectors.toList());
                  actionTypesList.add(0, dFsa);
                  return actionTypesList.toArray(new String[0]);
                })
            .collect(Collectors.toList());

    FileAttribute<Set<PosixFilePermission>> attr =
        PosixFilePermissions.asFileAttribute(setFilePermissions());
    Path tempFile =
        Files.createTempFile(DELETE_TRANSIT_BUFFER + new Date().getTime(), ".csv", attr);

    Writer writer = Files.newBufferedWriter(tempFile);
    try (var csvWriter =
        new CSVWriter(
            writer,
            DEFAULT_SEPARATOR,
            NO_QUOTE_CHARACTER,
            DEFAULT_ESCAPE_CHARACTER,
            DEFAULT_LINE_END)) {

      csvWriter.writeNext(new String[] {orgIdHeader, orgIdValue});
      csvWriter.writeNext(new String[] {carrierServiceIdHeader, carrierServiceIdValue});
      distinctSourceGeozones.add(0, SOURCE_AND_DESTINATION_GEO_ZONE);
      csvWriter.writeNext(distinctSourceGeozones.toArray(new String[0]));

      destinationFsaAndActionList.forEach(csvWriter::writeNext);
    }

    submitJob(
        orgIdValue,
        JobTypeEnum.DELETE_TRANSIT_BUFFER,
        FileUtils.readFileToByteArray(tempFile.toFile()),
        tempFile.getFileName().toString());

    return "Delete transit buffer job submitted to job framework successfully";
  }

  private void submitJob(String orgId, JobTypeEnum jobType, byte[] csvContents, String fileName)
      throws JobSubmissionException {
    try {
      jobsDashboardClient.processJobOffline(orgId, jobType, csvContents, fileName);
    } catch (FeignException e) {
      log.error("Feign exception while submitting job", e);
      var errorResponse = ExceptionUtils.parseFeignException(e);
      throw new JobSubmissionException(errorResponse.getMessage(), e, orgId);
    } catch (Exception e) {
      log.error("Error while submitting job to job framework", e);
      throw new JobSubmissionException("Error while submitting job to job framework", e, orgId);
    }
  }

  private static List<String[]> readCsvContents(Path path) throws IOException, CsvException {
    var reader = new BufferedReader(new FileReader(path.toFile()));
    List<String[]> csvFileContents;
    try (var csvReader = new CSVReader(reader)) {
      csvFileContents = csvReader.readAll();
    }
    return csvFileContents;
  }

  private Set<PosixFilePermission> setFilePermissions() {
    Set<PosixFilePermission> posixFilePermissions = new HashSet<>();
    posixFilePermissions.add(PosixFilePermission.OWNER_READ);
    posixFilePermissions.add(PosixFilePermission.OWNER_WRITE);
    return posixFilePermissions;
  }
}
