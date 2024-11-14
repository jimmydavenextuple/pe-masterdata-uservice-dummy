/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.exception;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.csvdownload.common.TestUtil;
import feign.FeignException;
import feign.Request;
import feign.Request.HttpMethod;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
            "Error while fetching list of FSAs", TestUtil.ORG_ID, TestUtil.SOURCE_REGION, null);
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

  @Test
  void handleFeignException() {
    Map<String, Collection<String>> headers = new HashMap<>();
    FeignException exception =
        new FeignException.BadRequest(
            "Error when fetching fsaList",
            Request.create(HttpMethod.GET, "", new HashMap<>(), null, null, null),
            "Error when fetching fsaList".getBytes(),
            headers);
    ResponseEntity<ErrorResponse> errorResponse =
        csvUtilityExceptionHandler.handleFeignException(exception);
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
    Assertions.assertNotNull(errorResponse.getBody());
  }

  @Test
  void handleCsvDataValidationException() {
    CsvDataValidationException exception = new CsvDataValidationException("Invalid csv data");
    ResponseEntity<ErrorResponse> errorResponseResponse =
        csvUtilityExceptionHandler.handleCsvDataValidationException(exception);
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, errorResponseResponse.getStatusCode());
    Assertions.assertNotNull(errorResponseResponse.getBody());
  }

  @Test
  void handleCustomRegionServiceException() {
    CustomRegionServiceException customRegionServiceException =
        new CustomRegionServiceException(
            "Error while fetching custom region details", TestUtil.ORG_ID, TestUtil.REGION_ID);
    ResponseEntity<ErrorResponse> errorResponse =
        csvUtilityExceptionHandler.handleCustomRegionServiceException(customRegionServiceException);
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
    Assertions.assertNotNull(errorResponse.getBody());
  }
}
