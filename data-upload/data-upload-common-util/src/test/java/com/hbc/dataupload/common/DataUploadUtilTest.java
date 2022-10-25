package com.hbc.dataupload.common;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.dataupload.common.utils.v1.DataUploadUtil;
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
    Path path = Paths.get("src", "test", "resources", "market", "market.csv");
    var csvReader = new CSVReader(new InputStreamReader(Files.newInputStream(path)));
    List<String[]> csvFileContents = csvReader.readAll();
    DataUploadUtil.validateCSVHeaders(csvFileContents.get(0), "market-region", "");
    DataUploadUtil.validateEmptyCSV(csvFileContents, "");
    csvReader.close();

    path = Paths.get("src", "test", "resources", "market", "marketInvalidHeader.csv");
    csvReader = new CSVReader(new InputStreamReader(Files.newInputStream(path)));
    csvFileContents = csvReader.readAll();
    List<String[]> finalCsvFileContents = csvFileContents;
    Assertions.assertThrows(
        Exception.class,
        () -> DataUploadUtil.validateCSVHeaders(finalCsvFileContents.get(0), "market-region", ""));
    csvReader.close();
  }

  @Test
  void validateEmptyCsvTest() throws IOException, CsvException {
    Path path = Paths.get("src", "test", "resources", "market", "marketEmpty.csv");
    var csvReader = new CSVReader(new InputStreamReader(Files.newInputStream(path)));
    List<String[]> csvFileContents = csvReader.readAll();
    Assertions.assertThrows(
        Exception.class, () -> DataUploadUtil.validateEmptyCSV(csvFileContents, ""));
    csvReader.close();
  }
}
