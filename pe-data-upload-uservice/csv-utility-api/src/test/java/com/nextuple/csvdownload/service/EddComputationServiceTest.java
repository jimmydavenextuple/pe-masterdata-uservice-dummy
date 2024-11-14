/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service;

import static com.nextuple.csvdownload.util.TestUtil.FILE_PATH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.csvdownload.common.TestUtil;
import com.nextuple.csvdownload.common.inbound.GenericUploadRequest;
import com.nextuple.dataupload.common.config.TenantDatabaseConfig;
import com.nextuple.dataupload.common.utils.v1.DynamicCsvHeadersValidation;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.service.FileService;
import com.nextuple.promise.domain.CoreEngineResponse;
import com.nextuple.promise.domain.OrderRequest;
import com.nextuple.promise.feign.PromisingFeign;
import com.opencsv.exceptions.CsvException;
import feign.FeignException;
import feign.Request;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jdk.jfr.Description;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ObjectUtils;

class EddComputationServiceTest {
  @InjectMocks private EddComputationService eddComputationService;
  @Mock private PromisingFeign promisingFeign;
  @InjectMocks private TestUtil testUtil;
  @Mock DynamicCsvHeadersValidation dynamicCsvHeadersValidation;

  @Mock TenantDatabaseConfig tenantDatabaseConfig;
  @Mock FileService fileService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(eddComputationService, "maxEddComputationLines", 3);
    ReflectionTestUtils.setField(eddComputationService, "maxEddComputationOrders", 15);
  }

  @DisplayName("Test upload Edd computation: success path")
  @Test
  void uploadEddComputationDataTest() throws CommonServiceException, IOException, CsvException {
    when(tenantDatabaseConfig.getCurrentTenantCustomAttributes(any()))
        .thenReturn(TestUtil.getTenantCustomAttribute());
    when(tenantDatabaseConfig.getCurrentTenantLinesCustomAttributes(any()))
        .thenReturn(TestUtil.getTenantLinesCustomAttribute());
    doNothing()
        .when(dynamicCsvHeadersValidation)
        .validateCSVHeadersForEDD(any(), any(), any(), any(), any());
    when(promisingFeign.promiseEdd(any(OrderRequest.class)))
        .thenReturn(testUtil.getCoreEngineResponse());
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse());
    File csvContent =
        eddComputationService.uploadEddCompuationData(
            GenericUploadRequest.builder().filePath(FILE_PATH).build());
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
    verify(tenantDatabaseConfig, times(1)).getCurrentTenantLinesCustomAttributes(any());
    verify(tenantDatabaseConfig, times(1)).getCurrentTenantCustomAttributes(any());
  }

  @DisplayName("Test upload Edd computation header exception: failure path")
  @Test
  void uploadEddComputationDataTestHeaderException()
      throws CommonServiceException, IOException, CsvException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "eddComputation", "edd_comp_invalid_headers.csv");
    InputStream inputStream = Files.newInputStream(resourceDirectory);
    FileResponse response = testUtil.getFileResponse2();
    response.setInputStream(inputStream);
    when(fileService.getFile(anyString(), anyString())).thenReturn(response);
    doThrow(CommonServiceException.class)
        .when(dynamicCsvHeadersValidation)
        .validateCSVHeadersForEDD(any(), any(), any(), any(), any());
    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            eddComputationService.uploadEddCompuationData(
                GenericUploadRequest.builder().filePath(FILE_PATH).build()));
  }

  @DisplayName("Test upload Edd computation with header & line custom attribute: success path")
  @Test
  void uploadEddComputationDataTestWithCustomAttributes()
      throws CommonServiceException, IOException, CsvException {
    doNothing()
        .when(dynamicCsvHeadersValidation)
        .validateCSVHeadersForEDD(any(), any(), any(), any(), any());
    when(tenantDatabaseConfig.getCurrentTenantCustomAttributes(any()))
        .thenReturn(TestUtil.getTenantCustomAttribute());
    when(tenantDatabaseConfig.getCurrentTenantLinesCustomAttributes(any()))
        .thenReturn(TestUtil.getTenantLinesCustomAttribute());
    when(promisingFeign.promiseEdd(any(OrderRequest.class)))
        .thenReturn(testUtil.getCoreEngineResponse7());
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse10());
    File csvContent =
        eddComputationService.uploadEddCompuationData(
            GenericUploadRequest.builder().filePath(FILE_PATH).build());
    String data = FileUtils.readFileToString(csvContent, "UTF-8");
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
    Assertions.assertEquals(data, testUtil.outPutDataForMultipleLineOrder());
    verify(promisingFeign, times(1)).promiseEdd(any());
    verify(tenantDatabaseConfig, times(1)).getCurrentTenantLinesCustomAttributes(any());
    verify(tenantDatabaseConfig, times(1)).getCurrentTenantCustomAttributes(any());
  }

  @DisplayName("Test upload Edd computation on empty cart id exception: failure path")
  @Test
  void uploadEddComputationDataTestWithCartIdException()
      throws CommonServiceException, IOException, CsvException {
    when(tenantDatabaseConfig.getCurrentTenantCustomAttributes(any()))
        .thenReturn(TestUtil.getTenantCustomAttribute());
    when(tenantDatabaseConfig.getCurrentTenantLinesCustomAttributes(any()))
        .thenReturn(TestUtil.getTenantLinesCustomAttribute());
    doNothing()
        .when(dynamicCsvHeadersValidation)
        .validateCSVHeadersForEDD(any(), any(), any(), any(), any());
    when(promisingFeign.promiseEdd(any(OrderRequest.class)))
        .thenReturn(testUtil.getCoreEngineResponse());
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse3());
    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              eddComputationService.uploadEddCompuationData(
                  GenericUploadRequest.builder().filePath(FILE_PATH).build());
            });

    Assertions.assertEquals("Cart id is blank at row: 4", exception.getMessage());
    verify(tenantDatabaseConfig, times(1)).getCurrentTenantLinesCustomAttributes(any());
    verify(tenantDatabaseConfig, times(1)).getCurrentTenantCustomAttributes(any());
  }

  @DisplayName("Test upload Edd computation with no custom attribute with no orgId: success path")
  @Test
  void uploadEddComputationDataTestWithNoCustomAttributesNoOrgId()
      throws CommonServiceException, IOException, CsvException {
    doNothing()
        .when(dynamicCsvHeadersValidation)
        .validateCSVHeadersForEDD(any(), any(), any(), any(), any());
    when(promisingFeign.promiseEdd(any(OrderRequest.class)))
        .thenReturn(testUtil.getCoreEngineResponse7());
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse());
    File csvContent =
        eddComputationService.uploadEddCompuationData(
            GenericUploadRequest.builder().filePath(FILE_PATH).build());
    String data = FileUtils.readFileToString(csvContent, "UTF-8");
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
    Assertions.assertEquals(data, testUtil.outPutDataForMultipleLineOrder());
    verify(promisingFeign, times(1)).promiseEdd(any());
  }

  @DisplayName("Test upload Edd computation with no custom attribute with orgId: success path")
  @Test
  void uploadEddComputationDataTestWithNoCustomAttributesWithOrgId()
      throws CommonServiceException, IOException, CsvException {
    doNothing()
        .when(dynamicCsvHeadersValidation)
        .validateCSVHeadersForEDD(any(), any(), any(), any(), any());
    when(promisingFeign.promiseEdd(any(OrderRequest.class)))
        .thenReturn(testUtil.getCoreEngineResponse7());
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse10());
    File csvContent =
        eddComputationService.uploadEddCompuationData(
            GenericUploadRequest.builder().orgId("NEXTUPLE").filePath(FILE_PATH).build());
    String data = FileUtils.readFileToString(csvContent, "UTF-8");
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
    Assertions.assertEquals(data, testUtil.outPutDataForMultipleLineOrder());
    verify(promisingFeign, times(1)).promiseEdd(any());
  }

  @DisplayName("Test upload Edd computation feign exception: failure path")
  @Test
  void uploadEddComputationDataTestFeignException()
      throws CommonServiceException, IOException, CsvException {

    String messages[] = {
      "{\"message\":\"Bad Request\",\"payload\":{\"type\":\"ERROR\",\"code\":2,\"fields\":{\"sessionId\":{\"rejectedValue\":\"\",\"errorMessage\":\"session id must not be blank\"}}}}",
      "",
      "{\"message\":\"FeignException\",\"payload\":{\"type\":\"ERROR\",\"code\":2,\"fields\":{\"sessionId\":{\"rejectedValue\":\"\",\"errorMessage\":\"FeignException \"message\" : \"there\" \"}}}}",
    };
    for (String message : messages) {
      Map<String, Collection<String>> headers = new HashMap<>();
      FeignException exception =
          new FeignException.BadRequest(
              "FeignException",
              Request.create(Request.HttpMethod.GET, "", new HashMap<>(), null, null, null),
              message.getBytes(),
              headers);
      doNothing()
          .when(dynamicCsvHeadersValidation)
          .validateCSVHeadersForEDD(any(), any(), any(), any(), any());
      when(promisingFeign.promiseEdd(any(OrderRequest.class))).thenThrow(exception);
      when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse());
      Assertions.assertThrows(
          CommonServiceException.class,
          () ->
              eddComputationService.uploadEddCompuationData(
                  GenericUploadRequest.builder().filePath(FILE_PATH).orgId("NEXTUPLE").build()));
    }
  }

  @Test
  @Description(
      "To check whether CSV is generated for one line and multiple serviceOptions when there is exceptions as well as solutions")
  void downloadEddComputationDataTest() throws IOException {
    File csvContent =
        eddComputationService.downloadEddComputation(List.of(testUtil.getCoreEngineResponse()));
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  @Description(
      "To check whether CSV is generated for one line and one serviceOptions when there is no exceptions")
  void downloadEddComputationDataTest2() throws IOException {
    File csvContent =
        eddComputationService.downloadEddComputation(List.of(testUtil.getCoreEngineResponse2()));
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  @Description(
      "To check whether CSV is generated for one line and one serviceOptions when there is exception and no solutions")
  void downloadEddComputationDataTest3() throws IOException {
    File csvContent =
        eddComputationService.downloadEddComputation(List.of(testUtil.getCoreEngineResponse3()));
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  @Description(
      "To check whether CSV is generated for one line and multiple serviceOptions when there is exception and no solutions")
  void downloadEddComputationDataTest4() throws IOException {
    File csvContent =
        eddComputationService.downloadEddComputation(List.of(testUtil.getCoreEngineResponse4()));
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  @Description("To check whether CSV is generated for one line and multiple serviceOptions")
  void downloadEddComputationDataTest5() throws IOException {
    File csvContent =
        eddComputationService.downloadEddComputation(List.of(testUtil.getCoreEngineResponse5()));
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  @Description("To check whether CSV is generated for multiple lines and multiple serviceOptions")
  void downloadEddComputationDataTest6() throws IOException {
    File csvContent =
        eddComputationService.downloadEddComputation(List.of(testUtil.getCoreEngineResponse6()));
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  @Description("To check whether CSV is generated for multiple lines and one serviceOption")
  void downloadEddComputationDataTest7() throws IOException {
    File csvContent =
        eddComputationService.downloadEddComputation(List.of(testUtil.getCoreEngineResponse7()));
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  @Description("To check whether CSV is generated for order that might return some null values")
  void downloadEddComputationDataTest8() throws IOException {
    File csvContent =
        eddComputationService.downloadEddComputation(List.of(testUtil.getCoreEngineResponse8()));
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @DisplayName("Test upload Edd computation for single order: success path")
  @Test
  void uploadEddComputationDataTestForSingleLineOrder() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "eddComputation", "edd_comp_valid.csv");
    InputStream inputStream = Files.newInputStream(resourceDirectory);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(promisingFeign.promiseEdd(any(OrderRequest.class)))
        .thenReturn(testUtil.getCoreEngineResponse2());
    doNothing()
        .when(dynamicCsvHeadersValidation)
        .validateCSVHeadersForEDD(any(), any(), any(), any(), any());
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse4());
    File csvContent =
        eddComputationService.uploadEddCompuationData(
            GenericUploadRequest.builder().filePath(FILE_PATH).build());
    String data = FileUtils.readFileToString(csvContent, "UTF-8");
    Assertions.assertEquals(data, testUtil.outPutDataForSingleLineOrder());
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @DisplayName(
      "Test upload Edd computation for single order with multiple service options: success path")
  @Test
  void uploadEddComputationDataTestForSingleLineOrderWithMultipleServiceOptions()
      throws CommonServiceException, IOException {
    doNothing()
        .when(dynamicCsvHeadersValidation)
        .validateCSVHeadersForEDD(any(), any(), any(), any(), any());
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "eddComputation", "edd_comp_valid.csv");
    InputStream inputStream = Files.newInputStream(resourceDirectory);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(promisingFeign.promiseEdd(any(OrderRequest.class)))
        .thenReturn(testUtil.getCoreEngineResponse5());
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse6());
    File csvContent =
        eddComputationService.uploadEddCompuationData(
            GenericUploadRequest.builder().filePath(FILE_PATH).build());
    String data = FileUtils.readFileToString(csvContent, "UTF-8");
    Assertions.assertEquals(data, testUtil.outPutDataForSingleLineOrderMultipleServiceOptions());
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @DisplayName("Test upload Edd computation for multiple order: success path")
  @Test
  void uploadEddComputationDataTestForMultipleOrders() throws CommonServiceException, IOException {
    doNothing()
        .when(dynamicCsvHeadersValidation)
        .validateCSVHeadersForEDD(any(), any(), any(), any(), any());
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "eddComputation", "edd_comp_valid.csv");
    InputStream inputStream = Files.newInputStream(resourceDirectory);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(promisingFeign.promiseEdd(any(OrderRequest.class)))
        .thenReturn(testUtil.getCoreEngineResponse2());
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse5());
    File csvContent =
        eddComputationService.uploadEddCompuationData(
            GenericUploadRequest.builder().filePath(FILE_PATH).build());
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @DisplayName("Test upload Edd computation for multiple order lines: success path")
  @Test
  void uploadEddComputationDataTestForMultipleLineOrder()
      throws CommonServiceException, IOException {
    doNothing()
        .when(dynamicCsvHeadersValidation)
        .validateCSVHeadersForEDD(any(), any(), any(), any(), any());
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "eddComputation", "edd_comp_valid.csv");
    InputStream inputStream = Files.newInputStream(resourceDirectory);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(promisingFeign.promiseEdd(any(OrderRequest.class)))
        .thenReturn(testUtil.getCoreEngineResponse7());
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse());
    File csvContent =
        eddComputationService.uploadEddCompuationData(
            GenericUploadRequest.builder().filePath(FILE_PATH).build());
    String data = FileUtils.readFileToString(csvContent, "UTF-8");
    Assertions.assertEquals(data, testUtil.outPutDataForMultipleLineOrder());
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @DisplayName("Test upload Edd computation for empty csv data: success path")
  @Test
  void uploadWithEmptyCsvData() throws CommonServiceException, IOException {
    doNothing()
        .when(dynamicCsvHeadersValidation)
        .validateCSVHeadersForEDD(any(), any(), any(), any(), any());
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "eddComputation", "edd_comp_valid.csv");
    InputStream inputStream = Files.newInputStream(resourceDirectory);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse7());
    File csvContent =
        eddComputationService.uploadEddCompuationData(
            GenericUploadRequest.builder().filePath(FILE_PATH).build());

    String data = FileUtils.readFileToString(csvContent, "UTF-8");
    Assertions.assertEquals(testUtil.outputForNoOrderRequest(), data);
  }

  @DisplayName(
      "Test upload Edd computation for single order with multiple service options: success path")
  @Test
  void uploadWithExceedingOrderLimitCsvData() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "eddComputation", "edd_comp_valid.csv");
    InputStream inputStream = Files.newInputStream(resourceDirectory);
    FileResponse response = testUtil.getFileResponse8();
    response.setInputStream(inputStream);
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse8());
    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            eddComputationService.uploadEddCompuationData(
                GenericUploadRequest.builder().filePath(FILE_PATH).build()));
  }

  @DisplayName("Test upload Edd computation for exceeding order lines: success path")
  @Test
  void uploadWithExceedingOrderLinesLimitCsvData() throws CommonServiceException, IOException {
    doNothing()
        .when(dynamicCsvHeadersValidation)
        .validateCSVHeadersForEDD(any(), any(), any(), any(), any());
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "eddComputation", "edd_comp_valid.csv");
    InputStream inputStream = Files.newInputStream(resourceDirectory);
    FileResponse response = testUtil.getFileResponse8();
    response.setInputStream(inputStream);
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse9());
    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            eddComputationService.uploadEddCompuationData(
                GenericUploadRequest.builder().filePath(FILE_PATH).build()));
  }

  @DisplayName("Test upload Edd computation with session id & page name: success path")
  @Test
  void uploadEddComputationDataWithSessionIdAndPageNameFieldsTest()
      throws CommonServiceException, IOException, CsvException {
    CoreEngineResponse coreEngineResponse = testUtil.getCoreEngineResponse();
    coreEngineResponse.setSessionId("12345");
    coreEngineResponse.setPageName("CART");
    doNothing()
        .when(dynamicCsvHeadersValidation)
        .validateCSVHeadersForEDD(any(), any(), any(), any(), any());
    when(promisingFeign.promiseEdd(any(OrderRequest.class))).thenReturn(coreEngineResponse);
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse());
    File csvContent =
        eddComputationService.uploadEddCompuationData(
            GenericUploadRequest.builder().filePath(FILE_PATH).build());
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }
}
