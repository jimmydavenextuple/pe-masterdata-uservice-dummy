/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.csvdownload.common.TestUtil;
import com.nextuple.csvdownload.exception.*;
import com.nextuple.dataupload.common.config.TenantDatabaseConfig;
import com.opencsv.exceptions.CsvException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;

class DownloadTemplateServiceTest {
  @InjectMocks private DownloadTemplateService downloadTemplateService;
  @Mock TenantDatabaseConfig tenantDatabaseConfig;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    try (MockedStatic<ClassPathResource> classPathResourceStaticMock =
        Mockito.mockStatic(ClassPathResource.class)) {}
  }

  @DisplayName("Test invalid template type handling")
  @Test
  void downloadTemplateInvalidTemplateException() throws Exception {
    Exception exception =
        Assertions.assertThrows(
            InvalidTemplateTypeException.class,
            () ->
                downloadTemplateService.getTemplateData(
                    TestUtil.templateTypeInvalid, TestUtil.ORG_ID));
    Assertions.assertNotNull(exception);
  }

  @DisplayName("Test node template type csv file: success path")
  @Test
  void downloadTemplateNode() throws InvalidTemplateTypeException, CommonServiceException {

    when(tenantDatabaseConfig.fetchServiceOptions(anyString()))
        .thenReturn(TestUtil.tenantServiceOption);

    try (MockedConstruction<ClassPathResource> construction =
        mockConstruction(
            ClassPathResource.class,
            (mock, context) -> {
              when(mock.getInputStream())
                  .thenReturn(new ByteArrayInputStream(TestUtil.nodeCsvData.getBytes()));
            })) {

      ByteArrayOutputStream result =
          downloadTemplateService.getTemplateData("nodes", TestUtil.ORG_ID);

      assertEquals(TestUtil.nodeCsvExpectedData, result.toString().replaceAll("\"|\r", "").trim());
    }
  }

  @DisplayName("Test transit template type csv file: success path")
  @Test
  void downloadTemplateTransit()
      throws IOException, InvalidTemplateTypeException, CommonServiceException {

    try (MockedConstruction<ClassPathResource> construction =
        mockConstruction(
            ClassPathResource.class,
            (mock, context) -> {
              when(mock.getInputStream())
                  .thenReturn(new ByteArrayInputStream(TestUtil.transitCsvData.getBytes()));
            })) {

      ByteArrayOutputStream result =
          downloadTemplateService.getTemplateData("transit", TestUtil.ORG_ID);

      assertEquals(
          TestUtil.transitCsvExpectedData, result.toString().replaceAll("\"|\r", "").trim());
    }
  }

  @DisplayName("Test default template type csv file: success path")
  @Test
  void downloadTemplateForOthers()
      throws IOException, InvalidTemplateTypeException, CommonServiceException {

    try (MockedConstruction<ClassPathResource> construction =
        mockConstruction(
            ClassPathResource.class,
            (mock, context) -> {
              when(mock.getInputStream())
                  .thenReturn(new ByteArrayInputStream(TestUtil.carrierServiceCsvData.getBytes()));
            })) {

      ByteArrayOutputStream result =
          downloadTemplateService.getTemplateData("carrier-service", TestUtil.ORG_ID);

      assertEquals(
          TestUtil.carrierServiceCsvExpectedData, result.toString().replaceAll("\"|\r", "").trim());
    }
  }

  @DisplayName("Test EDD template type csv file with null header level custom attributes")
  @Test
  void downloadEDDTemplateTestHeaderCustomAttributeNull()
      throws IOException, InvalidTemplateTypeException, CommonServiceException {
    String tenantId = TestUtil.ORG_ID;
    when(tenantDatabaseConfig.getCurrentTenantCustomAttributes(any())).thenReturn(null);
    when(tenantDatabaseConfig.getCurrentTenantLinesCustomAttributes(any())).thenReturn(null);
    try (MockedConstruction<ClassPathResource> construction =
        mockConstruction(
            ClassPathResource.class,
            (mock, context) -> {
              when(mock.getInputStream())
                  .thenReturn(new ByteArrayInputStream(TestUtil.eddComputationData.getBytes()));
            })) {

      ByteArrayOutputStream result =
          downloadTemplateService.getTemplateData("edd-computation", TestUtil.ORG_ID);
      assertEquals(
          TestUtil.expectedEddComputationData, result.toString().replaceAll("\"|\r", "").trim());
      verify(tenantDatabaseConfig, times(1)).getCurrentTenantLinesCustomAttributes(TestUtil.ORG_ID);
      verify(tenantDatabaseConfig, times(1)).getCurrentTenantCustomAttributes(TestUtil.ORG_ID);
    }
  }

  @DisplayName("Test EDD template type csv file with null line level custom attributes")
  @Test
  void downloadEDDTemplateTestLineCustomAttributeNull()
      throws IOException, InvalidTemplateTypeException, CommonServiceException {
    String tenantId = TestUtil.ORG_ID;
    when(tenantDatabaseConfig.getCurrentTenantCustomAttributes(any())).thenReturn(Map.of());
    when(tenantDatabaseConfig.getCurrentTenantLinesCustomAttributes(any())).thenReturn(null);
    try (MockedConstruction<ClassPathResource> construction =
        mockConstruction(
            ClassPathResource.class,
            (mock, context) -> {
              when(mock.getInputStream())
                  .thenReturn(new ByteArrayInputStream(TestUtil.eddComputationData.getBytes()));
            })) {

      ByteArrayOutputStream result =
          downloadTemplateService.getTemplateData("edd-computation", TestUtil.ORG_ID);
      assertEquals(
          TestUtil.expectedEddComputationData, result.toString().replaceAll("\"|\r", "").trim());
      verify(tenantDatabaseConfig, times(1)).getCurrentTenantLinesCustomAttributes(TestUtil.ORG_ID);
      verify(tenantDatabaseConfig, times(1)).getCurrentTenantCustomAttributes(TestUtil.ORG_ID);
    }
  }

  @DisplayName(
      "Test EDD template type csv file with header and line level custom attributes: success path")
  @Test
  void updateEddComputationCSVWithCustomAttributes()
      throws IOException, CsvException, CommonServiceException {
    String tenantId = TestUtil.ORG_ID;
    when(tenantDatabaseConfig.getCurrentTenantCustomAttributes(any()))
        .thenReturn(TestUtil.getTenantCustomAttribute());
    when(tenantDatabaseConfig.getCurrentTenantLinesCustomAttributes(any()))
        .thenReturn(TestUtil.getTenantLinesCustomAttribute());
    ClassPathResource mockResource = mock(ClassPathResource.class);
    when(mockResource.getInputStream())
        .thenReturn(new ByteArrayInputStream(TestUtil.eddComputationData.getBytes()));

    ByteArrayOutputStream writer = new ByteArrayOutputStream();
    downloadTemplateService.updateEddComputationCSV(tenantId, writer, mockResource);

    String updatedCsvData = writer.toString();

    assertEquals(true, updatedCsvData.contains("customAttributes_key1"));
    assertEquals(true, updatedCsvData.contains("customAttributes_key2"));
    assertEquals(true, updatedCsvData.contains("lines_customAttributes_key3"));
    assertEquals(true, updatedCsvData.contains("lines_customAttributes_key4"));
    verify(tenantDatabaseConfig, times(1)).getCurrentTenantLinesCustomAttributes(TestUtil.ORG_ID);
    verify(tenantDatabaseConfig, times(1)).getCurrentTenantCustomAttributes(TestUtil.ORG_ID);
  }

  @DisplayName(
      "Test EDD template type csv file with no header and line level custom attributes: success path")
  @Test
  void updateEddComputationCSVWithNoCustomAttributes()
      throws IOException, CsvException, CommonServiceException {
    ClassPathResource mockResource = mock(ClassPathResource.class);
    when(mockResource.getInputStream())
        .thenReturn(new ByteArrayInputStream(TestUtil.eddComputationData.getBytes()));

    ByteArrayOutputStream writer = new ByteArrayOutputStream();
    downloadTemplateService.updateEddComputationCSV(TestUtil.ORG_ID, writer, mockResource);
    String updatedCsvData = writer.toString();

    assertEquals(true, updatedCsvData.contains(TestUtil.ORG_ID));
    assertEquals(false, updatedCsvData.contains("customAttributes_key1"));
    assertEquals(false, updatedCsvData.contains("customAttributes_key2"));
    verify(tenantDatabaseConfig, times(1)).getCurrentTenantLinesCustomAttributes(TestUtil.ORG_ID);
    verify(tenantDatabaseConfig, times(1)).getCurrentTenantCustomAttributes(TestUtil.ORG_ID);
  }

  @DisplayName("Test node template type csv file with exception while fetching service options")
  @Test
  void downloadTemplateCommonServiceException() throws CommonServiceException {
    when(tenantDatabaseConfig.fetchServiceOptions(any()))
        .thenThrow(new CommonServiceException(HttpStatus.NOT_FOUND, 0x1776, null));

    try (MockedConstruction<ClassPathResource> construction =
        mockConstruction(
            ClassPathResource.class,
            (mock, context) -> {
              when(mock.getInputStream())
                  .thenReturn(new ByteArrayInputStream(TestUtil.nodeCsvData.getBytes()));
            })) {

      CommonServiceException exception =
          Assertions.assertThrows(
              CommonServiceException.class,
              () -> downloadTemplateService.getTemplateData("nodes", TestUtil.ORG_ID));
      Assertions.assertNotNull(exception);
    }
    verify(tenantDatabaseConfig, times(1)).fetchServiceOptions(TestUtil.ORG_ID);
  }
}
