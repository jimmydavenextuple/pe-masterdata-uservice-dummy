/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.util;

import com.nextuple.csvdownload.exception.CsvParsingException;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PageResponseForHolidayCutoff;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.HolidayCutoffColumnInfoDto;
import com.opencsv.CSVReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class CsvUtil {

  private CsvUtil() {}

  public static boolean validateCsvHeader(MultipartFile csvFile, String[] expectedHeaders)
      throws CsvParsingException {
    Predicate<String[]> isCommentedLine = predicateToFilterCommentedLine();
    try (var csvReader = new CSVReader(new InputStreamReader(csvFile.getInputStream()))) {
      String[] csvHeaders = csvReader.readNext();
      while (isCommentedLine.test(csvHeaders)) {
        csvHeaders = csvReader.readNext();
      }
      return Arrays.equals(csvHeaders, expectedHeaders);

    } catch (Exception e) {
      log.error("Error in validating csvHeader", e);
      throw new CsvParsingException("Error in validating csvHeader", e);
    }
  }

  /**
   * A method to find the number of comment lines exists in CSV file before the actual CSV content
   * starts (i.e. before any header or actual row).
   *
   * @param inputStream Byte array input stream of the csv file
   * @param commentLineFilter A predicate which determines the commented line
   * @return an integer indicating number of commented lines
   */
  public static int getCommentedLinesCount(
      InputStream inputStream, Predicate<String[]> commentLineFilter) throws CsvParsingException {
    var countedCommentedLines = 0;
    try (var csvReader = new CSVReader(new InputStreamReader(inputStream))) {
      boolean foundCommentedLine;
      String[] csvLine;
      do {
        csvLine = csvReader.readNextSilently();
        foundCommentedLine = csvLine != null && commentLineFilter.test(csvLine);
        if (foundCommentedLine) {
          countedCommentedLines++;
        }
      } while (foundCommentedLine);
    } catch (Exception e) {
      log.error("Failed to count the commented lines", e);
      throw new CsvParsingException("Error in parsing CSV comments", e);
    }
    return countedCommentedLines;
  }

  /**
   * A predicate that takes input of String Array and checks whether the input starts with hash (#)
   * or not
   *
   * @return <code>Predicate</code>
   * @see Predicate
   */
  public static Predicate<String[]> predicateToFilterCommentedLine() {
    return line -> line != null && line.length > 0 && line[0].startsWith("#");
  }

  public static String[] getHCOHeaderMetas(PageResponseForHolidayCutoff response) {
    return response.getData().getColumns().stream()
        .map(HolidayCutoffColumnInfoDto::getColumnMeta)
        .toArray(String[]::new);
  }

  public static String[] getHCOHeaderNames(PageResponseForHolidayCutoff response) {
    return response.getData().getColumns().stream()
        .map(HolidayCutoffColumnInfoDto::getColumnName)
        .toArray(String[]::new);
  }

  public static String getFilename(String fileName) {
    return fileName.concat("-").concat(new Date().toString()).concat(".csv");
  }
}
