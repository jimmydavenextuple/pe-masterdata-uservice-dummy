package com.hbc.jobs.consumers.service;

import static com.hbc.jobs.framework.common.domain.enums.ApiStatusEnum.FAILURE;
import static com.hbc.jobs.framework.common.domain.enums.ApiStatusEnum.SUCCESS;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.hbc.common.context.CurrentThreadContext;
import com.hbc.common.util.JsonUtil;
import com.hbc.jobs.consumers.exception.FeignClientMapperException;
import com.hbc.jobs.consumers.exception.NodeCarrierMapperException;
import com.hbc.jobs.consumers.exception.TransitMapperException;
import com.hbc.jobs.framework.common.domain.pojo.RecordDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordInputDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.hbc.jobs.framework.common.utils.ExceptionUtils;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import feign.FeignException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;

public interface FeignClientMapper {

  Logger log = LoggerFactory.getLogger(FeignClientMapper.class);

  static HttpHeaders createHeaders(Headers headers) {
    HttpHeaders httpHeaders = new HttpHeaders();
    headers.forEach(
        header -> {
          String value = new String(header.value());
          httpHeaders.add(header.key(), value.replace("\"", ""));
        });

    return httpHeaders;
  }

  static <T> T getDTOFromJson(String jsonData, Class<T> dtoClass) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper.readValue(jsonData, dtoClass);
  }

  default RecordStatusDto getResponseFromAPI(RecordDto record) {
    log.info("Inside getResponseFromAPI service");

    RecordStatusDto recordStatusDto = new RecordStatusDto();
    recordStatusDto.setCorrelationId(CurrentThreadContext.getLogContext().getCorrelationId());
    recordStatusDto.setServiceCorrelationId(
        CurrentThreadContext.getLogContext().getServiceCorrelationId());
    recordStatusDto.setJobId(record.getJob().getJobId());
    recordStatusDto.setOrgId(record.getJob().getOrgId());
    recordStatusDto.setRecordNo(record.getRecordId());
    recordStatusDto.setJobType(record.getJob().getJobType());
    recordStatusDto.setTotalRecordsInJob(record.getJob().getTotalRecords());
    Stopwatch stopwatch = null;
    Object dtoFromRecord = null;
    stopwatch = Stopwatch.createStarted();
    try {
      dtoFromRecord = getDtoFromRecord(record);

      ResponseEntity<?> response = callApi(dtoFromRecord, record.getInputs());

      stopwatch.stop();
      recordStatusDto.setResponseBodyPresent(response.hasBody());
      recordStatusDto.setResponseBody(JsonUtil.convert(response.getBody()));
      recordStatusDto.setStatusCode(response.getStatusCode().value());
      recordStatusDto.setStatus(response.getStatusCode().is2xxSuccessful() ? SUCCESS : FAILURE);
    } catch (FeignException e) {
      log.error("Failed to perform the bulk action", e);
      recordStatusDto.setException(e.getClass().getName());
      recordStatusDto.setStatusCode(e.status());
      recordStatusDto.setStatus(FAILURE);
      recordStatusDto.setResponseBodyPresent(!ObjectUtils.isEmpty(e.contentUTF8()));
      recordStatusDto.setResponseBody(e.contentUTF8());
      recordStatusDto.setErrorMessage(ExceptionUtils.parseFeignException(e).getMessage());
    } catch (Exception e) {
      log.error("Error while performing the bulk action", e);
      recordStatusDto.setException(e.getClass().getName());
      recordStatusDto.setStatusCode(
          HttpStatus.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.name()).value());
      recordStatusDto.setStatus(FAILURE);
      recordStatusDto.setResponseBodyPresent(Boolean.FALSE);
      recordStatusDto.setResponseBody("Error: " + e.getMessage());
    }
    recordStatusDto.setRequestBody(JsonUtil.convert(dtoFromRecord));
    recordStatusDto.setResponseTime(stopwatch.elapsed(TimeUnit.MILLISECONDS));
    log.info("getResponseFromAPI method ends");
    return recordStatusDto;
  }

  default Object getDtoFromRecord(RecordDto record)
      throws FeignClientMapperException, IOException, TransitMapperException,
          NodeCarrierMapperException {
    switch (record.getRecordType()) {
      case JSON:
        return getDTOFromJson(record.getRecordData(), mapTODto());
      case CSV:
      case XLSX:
        return getDTOFromCustomMapper(record.getRecordData());
    }
    log.error("Error while comparing the record type");
    throw new FeignClientMapperException("Incorrect record data type", record.getRecordType());
  }

  Object getDTOFromCustomMapper(String record);

  default <T> Object getDTOFromCSV(String record, Class<T> dtoClass) {
    Reader reader =
        new InputStreamReader(new ByteArrayInputStream(record.getBytes(StandardCharsets.UTF_8)));

    CSVParser parser = new CSVParserBuilder().withSeparator(',').withIgnoreQuotations(true).build();

    CSVReader csvReader =
        new CSVReaderBuilder(reader).withSkipLines(0).withCSVParser(parser).build();

    HeaderColumnNameTranslateMappingStrategy<T> strategy =
        new HeaderColumnNameTranslateMappingStrategy<>();
    strategy.setType(dtoClass);
    String[] headerColumns = record.split("\n")[0].split(",");
    Map<String, String> columnMapping = getColumnNameMapping(headerColumns);
    strategy.setColumnMapping(columnMapping);
    CsvToBean<T> csvToBean = new CsvToBean<>();
    csvToBean.setMappingStrategy(strategy);
    csvToBean.setCsvReader(csvReader);
    List<T> parse = csvToBean.parse();

    return parse.get(0);
  }

  Map<String, String> getColumnNameMapping(String[] headerColumns);

  default Map<String, String> getDefaultColumnNameMapping(String[] headerColumns) {
    return Arrays.stream(headerColumns)
        .collect(Collectors.toMap(Function.identity(), Function.identity()));
  }

  Class mapTODto() throws TransitMapperException, NodeCarrierMapperException;

  ResponseEntity<?> callApi(Object dtoFromJson, RecordInputDto inputs)
      throws TransitMapperException, NodeCarrierMapperException;
}
