package com.hbc.dataupload.common.utils;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION_INVALID_MESSAGE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.FILE_URI;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.UPDATE_ACTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DELETE_D;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.UPDATE_U;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.common.response.error.FieldError;
import com.hbc.dataupload.common.headers.DataUploadUtilityExpectedHeaders;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.QuoteMode;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataUploadUtil {
  public static void compareHeaders(CSVParser csvParser, String serviceName, String errorMessage)
      throws CommonServiceException {

    List<String> expectedHeaders =
        DataUploadUtilityExpectedHeaders.getCSVExpectedHeaders(serviceName);
    if (!csvParser.getHeaderNames().equals(expectedHeaders)) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          FILE_URI,
          FieldError.builder()
              .rejectedValue(csvParser.getHeaderNames())
              .actualValue(expectedHeaders)
              .errorMessage("CSV File Headers are invalid")
              .build());
      throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, 0x2777, errorMap);
    }
  }

  public static void validateUpdateAction(Path path) throws IOException, CommonServiceException {
    var line = "";
    List<String> actions = new ArrayList<>();

    try (var br = Files.newBufferedReader(path)) {
      while ((line = br.readLine()) != null) {
        String[] values = line.split(",");
        actions.add(values[2]);
      }
      actions.remove("action");
      for (Object action : actions) {
        if (!(action.toString().equalsIgnoreCase(UPDATE_ACTION))) {
          throw new CommonServiceException(
              ACTION_INVALID_MESSAGE, HttpStatus.BAD_REQUEST, 0x1777, null);
        }
      }
    }
  }

  public static void validateAction(Path path) throws CommonServiceException, IOException {
    var line = "";
    List<String> actions = new ArrayList<>();

    try (var br = Files.newBufferedReader(path)) {
      while ((line = br.readLine()) != null) {
        String[] values = line.split(",");
        actions.add(values[5]);
      }
      actions.remove("action");
      for (Object action : actions) {
        if (action == null || !(action.toString().equalsIgnoreCase(UPDATE_U)
            || action.toString().equalsIgnoreCase(DELETE_D))) {
          throw new CommonServiceException(
              ACTION_INVALID_MESSAGE, HttpStatus.BAD_REQUEST, 0x1777, null);
        }
      }
    }
  }

  public static ResponseEntity<BaseResponse<String>> getResponse(
      Map<String, Boolean> resultMap, String serviceName) {
    if (resultMap.get("isAllPassed").equals(true)) {
      return ResponseEntity.ok(
          BaseResponse.builder().message(serviceName + " Data successfully uploaded!").build());
    }
    if (resultMap.get("isAllFailed").equals(true)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(BaseResponse.builder().message(serviceName + " Data upload failed!").build());
    }
    return ResponseEntity.status(HttpStatus.MULTI_STATUS)
        .body(
            BaseResponse.builder()
                .message(serviceName + " Data partially uploaded with some rows failed!")
                .build());
  }

  public static void validateFileType(String fileUri, String errorMessage)
      throws IOException, CommonServiceException {
    String fileType = FilenameUtils.getExtension(fileUri);
    log.debug("file type : {}", fileType);

    if (!fileType.equals("csv")) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(FILE_URI, FieldError.builder().rejectedValue(fileUri).build());
      throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, 0x2773, errorMap);
    }
  }

  public static void validateFileSize(
      Path path, String fileUri, double maxSizeInKiloBytes, String errorMessage)
      throws IOException, CommonServiceException {
    double sizeOfFileInKiloBytes = Files.size(path) / 1024.0;
    log.debug("Size in kB : {}", sizeOfFileInKiloBytes);
    if (sizeOfFileInKiloBytes > maxSizeInKiloBytes) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          FILE_URI,
          FieldError.builder()
              .rejectedValue(fileUri)
              .errorMessage(
                  "Actual file size is "
                      + sizeOfFileInKiloBytes
                      + " kB, Maximum file size allowed is "
                      + maxSizeInKiloBytes
                      + " kB")
              .build());
      throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, 0x2774, errorMap);
    }
  }

  public static void validateFileRows(Path path, String fileUri, long maxRows, String errorMessage)
      throws IOException, CommonServiceException {
    long numberOfRowsInFile;
    try (Stream<String> stringStream = Files.lines(path)) {
      numberOfRowsInFile = stringStream.count();
    }
    log.debug("Number of rows : {}", numberOfRowsInFile);
    if (numberOfRowsInFile > maxRows) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          FILE_URI,
          FieldError.builder()
              .rejectedValue(fileUri)
              .errorMessage(
                  "Actual file contains "
                      + numberOfRowsInFile
                      + " rows, Maximum number of rows allowed is "
                      + maxRows)
              .build());
      throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, 0x2776, errorMap);
    }
  }

  public static void checkForEmptyRecords(Path path, String fileUri, String errorMessage)
      throws IOException, CommonServiceException {
    long numberOfRowsInFile;
    try (Stream<String> stringStream = Files.lines(path)) {
      numberOfRowsInFile = stringStream.count();
    }
    if (numberOfRowsInFile < 2) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(FILE_URI, FieldError.builder().rejectedValue(fileUri).build());
      throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, 0x2775, errorMap);
    }
  }

  public static CSVParser getCSVParserWithSetQuoteMode(Reader reader) throws IOException {
    return new CSVParser(
        reader,
        CSVFormat.DEFAULT
            .builder()
            .setHeader()
            .setIgnoreHeaderCase(true)
            .setSkipHeaderRecord(true)
            .setTrim(true)
            .setEscape('\\')
            .setQuoteMode(QuoteMode.NONE)
            .build());
  }

  public static CSVParser getCSVParser(Reader reader) throws IOException {
    return new CSVParser(
        reader,
        CSVFormat.DEFAULT
            .builder()
            .setHeader()
            .setIgnoreHeaderCase(true)
            .setSkipHeaderRecord(true)
            .setTrim(true)
            .build());
  }

  public static Path getPath(String basePath, String fileUri) {
    var path = Paths.get(basePath.concat(fileUri));
    log.debug("fileName : {}", path.getFileName());
    return path;
  }

  public static Map<String, Boolean> storeToMap(boolean isAllPassed, boolean isAllFailed) {
    Map<String, Boolean> resultMap = new HashMap<>();
    resultMap.put("isAllPassed", isAllPassed);
    resultMap.put("isAllFailed", isAllFailed);
    return resultMap;
  }
}
