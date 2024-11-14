/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.csvdownload.exception.InvalidTemplateTypeException;
import com.nextuple.dataupload.common.config.TenantDatabaseConfig;
import com.nextuple.dataupload.common.utils.v1.DynamicCsvHeadersValidation;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DownloadTemplateService {

  private final TenantDatabaseConfig tenantDatabaseConfig;

  public static final String NODES = "nodes";
  public static final String TRANSIT = "transit";
  public static final String ORG_ID = "orgId";
  public static final String EDD_COMPUTATION = "edd-computation";

  public ByteArrayOutputStream getTemplateData(String templateType, String tenantId)
      throws InvalidTemplateTypeException, CommonServiceException {

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    try {
      var cs = new ClassPathResource("/static/templates/" + templateType.toLowerCase() + ".csv");

      switch (templateType) {
        case NODES:
          updateOrgIdAndServiceOptions(tenantId, byteArrayOutputStream, cs);
          break;
        case TRANSIT:
          updateTransitOrgId(tenantId, byteArrayOutputStream, cs);
          break;
        case EDD_COMPUTATION:
          updateEddComputationCSV(tenantId, byteArrayOutputStream, cs);
          break;
        default:
          updateOrgId(tenantId, byteArrayOutputStream, cs);
          break;
      }

      return byteArrayOutputStream;
    } catch (CommonServiceException e) {
      throw e;
    } catch (Exception e) {
      throw new InvalidTemplateTypeException("Invalid template type", templateType);
    }
  }

  public void updateOrgIdAndServiceOptions(
      String tenantId, ByteArrayOutputStream writer, ClassPathResource cs)
      throws IOException, CsvException, CommonServiceException {

    CSVReader reader = new CSVReader(new InputStreamReader(cs.getInputStream()));
    String serviceOptionString = tenantDatabaseConfig.fetchServiceOptions(tenantId);
    String[] serviceOptions = serviceOptionString.split(",");

    List<String[]> lines = reader.readAll();
    reader.close();

    // Add the new column header
    List<String> headers = new ArrayList<>(Arrays.asList(lines.get(0)));
    List<String> values = new ArrayList<>();

    int orgIdIndex = headers.indexOf(ORG_ID);

    for (String serviceOption : serviceOptions) {
      headers.add(serviceOption.toLowerCase() + "Eligible");
      values.add("FALSE");
    }

    lines.set(0, headers.toArray(new String[0]));

    for (int i = 1; i < lines.size(); i++) {
      List<String> line = new ArrayList<>(Arrays.asList(lines.get(i)));
      line.addAll(values);
      line.set(orgIdIndex, tenantId);
      lines.set(i, line.toArray(new String[0]));
    }

    CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(writer));
    csvWriter.writeAll(lines);
    csvWriter.close();
  }

  public void updateOrgId(String tenantId, ByteArrayOutputStream writer, ClassPathResource cs)
      throws IOException, CsvException {

    CSVReader reader = new CSVReader(new InputStreamReader(cs.getInputStream()));

    List<String[]> lines = reader.readAll();
    reader.close();

    List<String> headers = new ArrayList<>(Arrays.asList(lines.get(0)));
    int orgIdIndex = headers.indexOf(ORG_ID);

    if (orgIdIndex >= 0) {
      for (int i = 1; i < lines.size(); i++) {
        String[] line = lines.get(i);
        line[orgIdIndex] = tenantId;
        lines.set(i, line);
      }
    }

    CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(writer));
    csvWriter.writeAll(lines);
    csvWriter.close();
  }

  public void updateTransitOrgId(
      String tenantId, ByteArrayOutputStream writer, ClassPathResource cs)
      throws IOException, CsvException {
    CSVReader reader = new CSVReader(new InputStreamReader(cs.getInputStream()));

    List<String[]> lines = reader.readAll();
    reader.close();

    for (String[] line : lines) {
      if (line[0].equals(ORG_ID)) {
        line[1] = tenantId;
        break;
      }
    }
    CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(writer));
    csvWriter.writeAll(lines);
    csvWriter.close();
  }

  public void updateEddComputationCSV(
      String tenantId, ByteArrayOutputStream writer, ClassPathResource cs)
      throws IOException, CsvException, CommonServiceException {
    Map<String, String> customAttributes =
        tenantDatabaseConfig.getCurrentTenantCustomAttributes(tenantId);
    Map<String, String> lineCustomAttributes =
        tenantDatabaseConfig.getCurrentTenantLinesCustomAttributes(tenantId);
    if (Objects.nonNull(customAttributes) && Objects.nonNull(lineCustomAttributes)) {
      addCustomAttributesToEDDHeaders(customAttributes, lineCustomAttributes, writer, cs, tenantId);
    } else {
      updateOrgId(tenantId, writer, cs);
    }
  }

  private void addCustomAttributesToEDDHeaders(
      Map<String, String> customAttributes,
      Map<String, String> lineCustomAttributes,
      ByteArrayOutputStream writer,
      ClassPathResource cs,
      String tenantId)
      throws IOException, CsvException {
    CSVReader reader = new CSVReader(new InputStreamReader(cs.getInputStream()));
    List<String[]> lines = reader.readAll();
    reader.close();
    // Add the new column header
    List<String> headers = new ArrayList<>(Arrays.asList(lines.get(0)));
    List<String> values = new ArrayList<>();

    int orgIdIndex = headers.indexOf(ORG_ID);
    lineCustomAttributes.forEach(
        (key, value) -> {
          headers.add(DynamicCsvHeadersValidation.LINES_CUSTOM_ATTRIBUTES_HEADER_PREFIX + key);
          values.add(value);
        });
    customAttributes.forEach(
        (key, value) -> {
          headers.add(DynamicCsvHeadersValidation.CUSTOM_ATTRIBUTES_HEADER_PREFIX + key);
          values.add(value);
        });

    lines.set(0, headers.toArray(new String[0]));

    for (int i = 1; i < lines.size(); ++i) {
      List<String> line = new ArrayList<>(Arrays.asList(lines.get(i)));
      List<String> nonEmptyLine =
          line.stream().filter(str -> !str.isEmpty()).collect(Collectors.toList());

      nonEmptyLine.addAll(values);
      nonEmptyLine.set(orgIdIndex, tenantId);
      lines.set(i, nonEmptyLine.toArray(new String[nonEmptyLine.size()]));
    }

    CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(writer));
    csvWriter.writeAll(lines);
    csvWriter.close();
  }
}
