package com.nextuple.dataupload.common;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.dataupload.common.utils.v1.DataUploadUtil;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
}
