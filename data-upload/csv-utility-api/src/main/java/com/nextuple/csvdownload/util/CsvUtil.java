package com.nextuple.csvdownload.util;

import com.nextuple.csvdownload.exception.CsvParsingException;
import com.opencsv.CSVReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
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
}
