/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.common;

import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.dataupload.common.config.TenantDatabaseConfig;
import com.nextuple.dataupload.common.utils.DataUploadUtil;
import com.nextuple.dataupload.common.utils.v1.DynamicCsvHeadersValidation;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.csv.CSVParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DynamicCsvHeadersValidationTest {

  @InjectMocks DynamicCsvHeadersValidation dynamicCsvHeadersValidation;
  @Mock TenantDatabaseConfig tenantDatabaseConfig;

  @Mock CSVParser csvParser;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @DisplayName("test get csv expected header: success path")
  @Test
  void getCSVExpectedHeadersTest() throws CommonServiceException {
    when(tenantDatabaseConfig.getCurrentTenantServiceOptions())
        .thenReturn(TestUtil.tenantServiceOptionExpected.toArray(new String[0]));

    List<String> expectedHeaders = dynamicCsvHeadersValidation.getCSVExpectedHeaders("nodes");
    Assertions.assertTrue(expectedHeaders.containsAll(TestUtil.tenantServiceOptionExpected));
  }

  @DisplayName("test validate csv header: success path")
  @Test
  void validateCsvHeaderTest() throws IOException, CommonServiceException, CsvException {
    when(tenantDatabaseConfig.getCurrentTenantServiceOptions())
        .thenReturn(TestUtil.tenantServiceOptionExpected.toArray(new String[0]));

    Path path = Paths.get("src", "test", "resources", "node", "nodes.csv");
    var csvReader = new CSVReader(new InputStreamReader(Files.newInputStream(path)));
    List<String[]> csvFileContents = csvReader.readAll();

    dynamicCsvHeadersValidation.validateCSVHeaders(csvFileContents.get(0), "nodes", "", csvReader);

    csvReader.close();

    path = Paths.get("src", "test", "resources", "node", "nodesInvalidHeaders.csv");
    var csvReader1 = new CSVReader(new InputStreamReader(Files.newInputStream(path)));
    List<String[]> csvFileContents1 = csvReader1.readAll();
    Assertions.assertThrows(
        Exception.class,
        () ->
            dynamicCsvHeadersValidation.validateCSVHeaders(
                csvFileContents1.get(0), "nodes", "", csvReader1));
  }

  @DisplayName("test validate csv header with custom attributes: success path")
  @Test
  void validateCSVHeadersForEddWithCustomAttributes() throws IOException, CommonServiceException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "eddComputation", "edd_comp_valid.csv");
    InputStream inputStream = Files.newInputStream(resourceDirectory);
    Reader reader = new BufferedReader(new InputStreamReader(inputStream));
    DataUploadUtil.getCSVParserWithSetQuoteMode(reader);
    when(csvParser.getHeaderNames())
        .thenReturn(TestUtil.getExpectedEDDHeadersWithCustomAttributes());
    Assertions.assertDoesNotThrow(
        () -> {
          dynamicCsvHeadersValidation.validateCSVHeadersForEDD(
              csvParser,
              "edd-computation",
              "invalid header files",
              TestUtil.getTenantCustomAttribute(),
              TestUtil.getTenantLinesCustomAttribute());
        });
  }

  @DisplayName("test validate csv header with custom attributes: failure path")
  @Test
  void validateCSVHeadersForEddWithCustomAttributesAndException()
      throws IOException, CommonServiceException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "eddComputation", "edd_comp_valid.csv");
    InputStream inputStream = Files.newInputStream(resourceDirectory);
    Reader reader = new BufferedReader(new InputStreamReader(inputStream));
    var csvParser = DataUploadUtil.getCSVParserWithSetQuoteMode(reader);

    Assertions.assertThrows(
        Exception.class,
        () ->
            dynamicCsvHeadersValidation.validateCSVHeadersForEDD(
                csvParser,
                "edd-computation",
                "invalid header files",
                TestUtil.getTenantCustomAttribute(),
                TestUtil.getTenantLinesCustomAttribute()));
  }

  @DisplayName("test validate csv header with empty custom attributes: success path")
  @Test
  void validateCSVHeadersForEddWithEmptyCustomAttributesMap()
      throws IOException, CommonServiceException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "eddComputation", "edd_comp_valid.csv");
    InputStream inputStream = Files.newInputStream(resourceDirectory);
    Reader reader = new BufferedReader(new InputStreamReader(inputStream));
    var csvParser = DataUploadUtil.getCSVParserWithSetQuoteMode(reader);
    List<String> headers = csvParser.getHeaderNames();
    dynamicCsvHeadersValidation.validateCSVHeadersForEDD(
        csvParser,
        "edd-computation",
        "invalid header files",
        TestUtil.getEmptyCustomAttribute(),
        TestUtil.getEmptyCustomAttribute());
    List<String> expectedHeaders = TestUtil.getExpectedEDDHeaders();

    Assertions.assertEquals(expectedHeaders, headers);
  }

  @DisplayName("test validate csv header with null header level custom attributes: failure path")
  @Test
  void validateCSVHeadersForEddWithNullHeaderCustomAttribute()
      throws IOException, CommonServiceException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "eddComputation", "edd_comp_valid.csv");
    InputStream inputStream = Files.newInputStream(resourceDirectory);
    Reader reader = new BufferedReader(new InputStreamReader(inputStream));
    var csvParser = DataUploadUtil.getCSVParserWithSetQuoteMode(reader);
    List<String> headers = csvParser.getHeaderNames();
    Assertions.assertThrows(
        Exception.class,
        () ->
            dynamicCsvHeadersValidation.validateCSVHeadersForEDD(
                csvParser,
                "edd-computation",
                "invalid header files",
                null,
                TestUtil.getEmptyCustomAttribute()));
  }

  @DisplayName("test validate csv header with null line level custom attributes: failure path")
  @Test
  void validateCSVHeadersForEddWithNullLinesCustomAttribute()
      throws IOException, CommonServiceException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "eddComputation", "edd_comp_valid.csv");
    InputStream inputStream = Files.newInputStream(resourceDirectory);
    Reader reader = new BufferedReader(new InputStreamReader(inputStream));
    var csvParser = DataUploadUtil.getCSVParserWithSetQuoteMode(reader);
    List<String> headers = csvParser.getHeaderNames();
    Assertions.assertThrows(
        Exception.class,
        () ->
            dynamicCsvHeadersValidation.validateCSVHeadersForEDD(
                csvParser,
                "edd-computation",
                "invalid header files",
                TestUtil.getEmptyCustomAttribute(),
                null));
  }
}
