package com.hbc.csvdownload.exception;

import com.hbc.common.response.error.ErrorResponse;
import com.hbc.csvdownload.common.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CsvUtilityExceptionHandlerTest {

  @InjectMocks private CsvUtilityExceptionHandler csvUtilityExceptionHandler;

  @Test
  void handleInvalidTemplateTypeException() {
    InvalidTemplateTypeException exception =
        new InvalidTemplateTypeException("Invalid template type", "invalidTemplateType");
    ResponseEntity<ErrorResponse> errorResponseResponseEntity =
        csvUtilityExceptionHandler.handleInvalidTemplateTypeException(exception);
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, errorResponseResponseEntity.getStatusCode());
    Assertions.assertNotNull(errorResponseResponseEntity.getBody());
  }

  @Test
  void handleCsvFormatValidationFailedException() {
    CsvFormatValidationFailedException exception =
        new CsvFormatValidationFailedException("Invalid Csv headers", null);
    CsvFormatValidationFailedException exception1 =
        new CsvFormatValidationFailedException("Invalid Csv headers", null, null);

    ResponseEntity<ErrorResponse> errorResponseResponseEntity =
        csvUtilityExceptionHandler.handleCsvFormatValidationFailedException(exception);
    ResponseEntity<ErrorResponse> errorResponseResponseEntity1 =
        csvUtilityExceptionHandler.handleCsvFormatValidationFailedException(exception1);

    Assertions.assertEquals(HttpStatus.BAD_REQUEST, errorResponseResponseEntity.getStatusCode());
    Assertions.assertNotNull(errorResponseResponseEntity.getBody());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, errorResponseResponseEntity1.getStatusCode());
    Assertions.assertNotNull(errorResponseResponseEntity1.getBody());
  }

  @Test
  void handleCsvParsingException() {
    CsvParsingException exception = new CsvParsingException("Invalid csv template");
    CsvParsingException exception1 = new CsvParsingException("Invalid csv template", null);
    ResponseEntity<ErrorResponse> errorResponseResponseEntity =
        csvUtilityExceptionHandler.handleCsvParsingException(exception);
    ResponseEntity<ErrorResponse> errorResponseResponseEntity1 =
        csvUtilityExceptionHandler.handleCsvParsingException(exception1);
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, errorResponseResponseEntity.getStatusCode());
    Assertions.assertNotNull(errorResponseResponseEntity.getBody());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, errorResponseResponseEntity1.getStatusCode());
    Assertions.assertNotNull(errorResponseResponseEntity1.getBody());
  }

  @Test
  void handleJobSubmissionException() {
    JobSubmissionException exception =
        new JobSubmissionException("Error while submitting the job", TestUtil.ORG_ID);
    ResponseEntity<ErrorResponse> errorResponseResponseEntity =
        csvUtilityExceptionHandler.handleJobSubmissionException(exception);
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, errorResponseResponseEntity.getStatusCode());
    Assertions.assertNotNull(errorResponseResponseEntity.getBody());
  }

  @Test
  void handleJsonParsingException() {
    JsonParsingException exception =
        new JsonParsingException("Error while parsing to json string", TestUtil.ORG_ID);
    JsonParsingException exception1 =
        new JsonParsingException("Error while parsing to json string", null, TestUtil.ORG_ID);
    ResponseEntity<ErrorResponse> errorResponseResponseEntity =
        csvUtilityExceptionHandler.handleJsonParsingException(exception);
    ResponseEntity<ErrorResponse> errorResponseResponseEntity1 =
        csvUtilityExceptionHandler.handleJsonParsingException(exception1);
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, errorResponseResponseEntity.getStatusCode());
    Assertions.assertNotNull(errorResponseResponseEntity.getBody());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, errorResponseResponseEntity1.getStatusCode());
    Assertions.assertNotNull(errorResponseResponseEntity1.getBody());
  }

  @Test
  void handleInvalidActionType() {
    InvalidActionType exception = new InvalidActionType("Invalid action", "invalidAction", 2);
    ResponseEntity<ErrorResponse> errorResponseResponseEntity =
        csvUtilityExceptionHandler.handleInvalidActionType(exception);
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, errorResponseResponseEntity.getStatusCode());
    Assertions.assertNotNull(errorResponseResponseEntity.getBody());
  }
}
