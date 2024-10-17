/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.common;

import com.nextuple.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.dataupload.common.utils.v1.DataUploadUtil;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DataUploadUtilTest {

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void validateFileTest() throws CommonServiceException {
    DataUploadUtil.validateFileType("text/csv", "Csv format error");

    Assertions.assertThrows(
        Exception.class, () -> DataUploadUtil.validateFileType("", "Csv format error"));
  }

  @Test
  void validateCsvHeaderTest() throws IOException, CommonServiceException, CsvException {
    Path path =
        Paths.get("src", "test", "resources", "postalCodeTimezone", "postalCodeTimezone.csv");
    var csvReader = new CSVReader(new InputStreamReader(Files.newInputStream(path)));
    List<String[]> csvFileContents = csvReader.readAll();
    DataUploadUtil.validateCSVHeaders(
        csvFileContents.get(0), "postal-code-timezone", "", csvReader);
    DataUploadUtil.validateEmptyCSV(csvFileContents, "", csvReader);
    csvReader.close();

    path =
        Paths.get(
            "src",
            "test",
            "resources",
            "postalCodeTimezone",
            "postalCodeTimezoneInvalidHeader.csv");
    var csvReader1 = new CSVReader(new InputStreamReader(Files.newInputStream(path)));
    List<String[]> csvFileContents1 = csvReader1.readAll();
    Assertions.assertThrows(
        Exception.class,
        () ->
            DataUploadUtil.validateCSVHeaders(
                csvFileContents1.get(0), "postal-code-timezone", "", csvReader1));
  }

  @Test
  void validateEmptyCsvTest() throws IOException, CsvException {
    Path path =
        Paths.get("src", "test", "resources", "postalCodeTimezone", "postalCodeTimezoneEmpty.csv");
    var csvReader = new CSVReader(new InputStreamReader(Files.newInputStream(path)));
    List<String[]> csvFileContents = csvReader.readAll();
    Assertions.assertThrows(
        Exception.class, () -> DataUploadUtil.validateEmptyCSV(csvFileContents, "", csvReader));
  }

  @Test
  void validateForEmptySourceGeozoneCSVDataTest()
      throws IOException, CsvException, CommonServiceException {
    Path path = Paths.get("src", "test", "resources", "transit", "transitSourceGeoZoneEmpty.csv");
    var csvReader = new CSVReader(new InputStreamReader(Files.newInputStream(path)));
    List<String[]> csvFileContents = csvReader.readAll();

    Assertions.assertThrows(
        Exception.class,
        () ->
            DataUploadUtil.validateForEmptySourceOrDestinationGeozoneCSVData(
                csvFileContents, "", csvReader));
  }

  @Test
  void validateForEmptyDestinationGeozoneCSVDataTest()
      throws IOException, CsvException, CommonServiceException {
    Path path =
        Paths.get("src", "test", "resources", "transit", "transitDestinationGeoZoneEmpty.csv");
    var csvReader = new CSVReader(new InputStreamReader(Files.newInputStream(path)));
    List<String[]> csvFileContents = csvReader.readAll();

    Assertions.assertThrows(
        Exception.class,
        () ->
            DataUploadUtil.validateForEmptySourceOrDestinationGeozoneCSVData(
                csvFileContents, "", csvReader));
  }

  @Test
  void getActiveCalendarResponseTest() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    cal.add(Calendar.DATE, 1);
    List<CarrierServiceCalendarResponse> carrierServiceCalendarResponses =
        List.of(
            CarrierServiceCalendarResponse.builder()
                .carrierServiceId("ALL_SDND")
                .calendarId("calendarID")
                .orgId("BAY")
                .effectiveDate(cal.getTime().toString())
                .build());
    Optional<CarrierServiceCalendarResponse> carrierServiceCalendarResponse =
        com.nextuple.dataupload.common.utils.DataUploadUtil.getActiveCalendarResponse(
            carrierServiceCalendarResponses);
    Assertions.assertTrue(carrierServiceCalendarResponse.isPresent());
  }
}
