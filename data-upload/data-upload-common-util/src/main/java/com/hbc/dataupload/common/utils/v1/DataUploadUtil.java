package com.hbc.dataupload.common.utils.v1;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CSV_HEADERS;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.FILE_TYPE;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.error.FieldError;
import com.hbc.dataupload.common.headers.v1.DataUploadUtilityExpectedHeaders;
import com.opencsv.CSVReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataUploadUtil {

  public static void validateFileType(String fileType, String errorMessage)
      throws CommonServiceException {
    log.debug("file type : {}", fileType);

    if (!fileType.equals("text/csv")) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(FILE_TYPE, FieldError.builder().rejectedValue(fileType).build());
      throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, 0x2773, errorMap);
    }
  }

  public static void validateCSVHeaders(
      String[] csvFileContents, String serviceName, String errorMessage, CSVReader csvReader)
      throws CommonServiceException, IOException {
    List<String> expectedHeaders =
        DataUploadUtilityExpectedHeaders.getCSVExpectedHeaders(serviceName);
    if (!expectedHeaders.equals(Arrays.asList(csvFileContents))) {
      csvReader.close();
      throw new CommonServiceException(
          errorMessage,
          HttpStatus.BAD_REQUEST,
          0x2777,
          Map.of(
              CSV_HEADERS,
              FieldError.builder()
                  .rejectedValue(csvFileContents)
                  .actualValue(expectedHeaders)
                  .build()));
    }
  }

  public static void validateEmptyCSV(
      List<String[]> csvFileContents, String errorMessage, CSVReader csvReader)
      throws CommonServiceException, IOException {
    if (CollectionUtils.isEmpty(csvFileContents) || csvFileContents.size() == 1) {
      csvReader.close();
      throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, 0x2773, null);
    }
  }
}
