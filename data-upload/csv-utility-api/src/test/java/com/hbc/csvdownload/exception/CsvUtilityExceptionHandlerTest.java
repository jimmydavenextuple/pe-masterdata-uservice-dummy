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

  @InjectMocks CsvUtilityExceptionHandler csvUtilityExceptionHandler;

  @Test
  void handleInvalidTemplateTypeException() {
    InvalidTemplateTypeException invalidTemplateTypeException =
        new InvalidTemplateTypeException("The template type is invalid", "invalidTemplateType");
    ResponseEntity<ErrorResponse> errorResponse =
        csvUtilityExceptionHandler.handleInvalidTemplateTypeException(invalidTemplateTypeException);
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
    Assertions.assertNotNull(errorResponse.getBody());
  }

  @Test
  void handleCsvDownloadUtilityServiceException() {
    CsvDownloadUtilityServiceException csvDownloadUtilityServiceException =
        new CsvDownloadUtilityServiceException(
            "Error while forming csv contents string", TestUtil.ORG_ID);
    ResponseEntity<ErrorResponse> errorResponse =
        csvUtilityExceptionHandler.handleCsvDownloadUtilityServiceException(
            csvDownloadUtilityServiceException);
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
    Assertions.assertNotNull(errorResponse.getBody());
  }

  @Test
  void handlePostalCodeTimezoneServiceException() {
    PostalCodeTimezoneServiceException postalCodeTimezoneServiceException =
        new PostalCodeTimezoneServiceException(
            "Error while fetching list of FSAs", TestUtil.ORG_ID, TestUtil.SOURCE_REGION);
    ResponseEntity<ErrorResponse> errorResponse =
        csvUtilityExceptionHandler.handlePostalCodeTimezoneServiceException(
            postalCodeTimezoneServiceException);
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
    Assertions.assertNotNull(errorResponse.getBody());
  }

  @Test
  void handleTransitServiceException() {
    TransitServiceException transitServiceException =
        new TransitServiceException(
            "Error while fetching transit details", TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);
    ResponseEntity<ErrorResponse> errorResponse =
        csvUtilityExceptionHandler.handleTransitServiceException(transitServiceException);
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
    Assertions.assertNotNull(errorResponse.getBody());
  }
}
