package com.hbc.jobs.consumers.service;

import com.hbc.csvdownload.exception.JsonParsingException;
import com.hbc.jobs.consumers.common.TestUtil;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.opencsv.exceptions.CsvException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ObjectUtils;

@ExtendWith(MockitoExtension.class)
class CsvProcessingServiceTest {

  @InjectMocks private CsvProcessingService csvProcessingService;

  @Test
  void processInputProcessingLeadTimesCsvFile()
      throws IOException, JsonParsingException, CsvException {
    String csvContents = TestUtil.CSV_CONTENTS_PROCESSING_LEAD_TIMES;
    InputStream inputStream = new ByteArrayInputStream(csvContents.getBytes());
    String jobString =
        csvProcessingService.processInputCsvFile(
            inputStream, JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, TestUtil.ORG_ID);

    Assertions.assertFalse(ObjectUtils.isEmpty(jobString));
  }

  @Test
  void processInputTransitTimesCsvFile() throws IOException, JsonParsingException, CsvException {
    String csvContents = TestUtil.CSV_CONTENTS_TRANSIT_TIMES;
    InputStream inputStream = new ByteArrayInputStream(csvContents.getBytes());
    String jobString =
        csvProcessingService.processInputCsvFile(
            inputStream, JobTypeEnum.UPLOAD_TRANSIT_TIMES, TestUtil.ORG_ID);

    Assertions.assertFalse(ObjectUtils.isEmpty(jobString));
  }

  @Test
  void processInputDeleteTransitBufferCsvFile()
      throws IOException, JsonParsingException, CsvException {
    String csvContents = TestUtil.CSV_CONTENTS_TRANSIT_TIMES;
    InputStream inputStream = new ByteArrayInputStream(csvContents.getBytes());
    String jobString =
        csvProcessingService.processInputCsvFile(
            inputStream, JobTypeEnum.DELETE_TRANSIT_BUFFER, TestUtil.ORG_ID);

    Assertions.assertFalse(ObjectUtils.isEmpty(jobString));
  }
}
