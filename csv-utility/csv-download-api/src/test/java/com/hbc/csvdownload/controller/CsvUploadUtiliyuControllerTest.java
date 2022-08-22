package com.hbc.csvdownload.controller;

import static org.mockito.Mockito.*;

import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.common.TestUtil;
import com.hbc.csvdownload.exception.CsvFormatValidationFailedException;
import com.hbc.csvdownload.exception.CsvParsingException;
import com.hbc.csvdownload.exception.JobServiceException;
import com.hbc.csvdownload.exception.JobSubmissionException;
import com.hbc.csvdownload.exception.JobUpdationException;
import com.hbc.csvdownload.exception.JsonParsingException;
import com.hbc.csvdownload.service.CsvUploadUtilityService;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class CsvUploadUtiliyuControllerTest {

  @Mock private CsvUploadUtilityService csvUploadUtilityService;

  @InjectMocks private CsvUploadutilityController csvDownloadUtilityController;

  @Test
  void uploadProcessingLeadTimes()
      throws CsvParsingException, CsvFormatValidationFailedException, JobSubmissionException,
          JsonParsingException {
    MultipartFile csvFile = mock(MultipartFile.class);
    when(csvUploadUtilityService.uploadProcessingLeadTimesCsv(any(), any()))
        .thenReturn("Job to bulk upload processing lead times is submitted successfully");

    ResponseEntity<BaseResponse<String>> res =
        csvDownloadUtilityController.uploadProcessingLeadTimes(TestUtil.ORG_ID, csvFile);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
    Assertions.assertNotNull(res.getBody().getPayload());
    Assertions.assertFalse(ObjectUtils.isEmpty(res.getBody().getPayload()));
  }

  @Test
  void uploadProcessingLeadTimesException()
      throws CsvParsingException, CsvFormatValidationFailedException, JobSubmissionException,
          JsonParsingException {
    MultipartFile csvFile = mock(MultipartFile.class);
    when(csvUploadUtilityService.uploadProcessingLeadTimesCsv(any(), any()))
        .thenThrow(new CsvParsingException("Invalid CSV template"));

    Exception exception =
        Assertions.assertThrows(
            CsvParsingException.class,
            () -> csvDownloadUtilityController.uploadProcessingLeadTimes(TestUtil.ORG_ID, csvFile));
    Assertions.assertNotNull(exception);
  }

  @Test
  void uploadTransitTimes()
      throws CsvFormatValidationFailedException, JsonParsingException, JobServiceException,
          IOException, JobUpdationException, CsvException {
    MultipartFile csvFile = mock(MultipartFile.class);
    when(csvUploadUtilityService.uploadTransitTimesCsv(any(), any()))
        .thenReturn("Job to upload Transit times received successfully");

    ResponseEntity<BaseResponse<String>> res =
        csvDownloadUtilityController.uploadTransitTimes(TestUtil.ORG_ID, csvFile);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
    Assertions.assertNotNull(res.getBody().getPayload());
    Assertions.assertFalse(ObjectUtils.isEmpty(res.getBody().getPayload()));
  }

  @Test
  void uploadTransitTimesException()
      throws CsvFormatValidationFailedException, JsonParsingException, JobServiceException,
          IOException, JobUpdationException, CsvException {
    MultipartFile csvFile = mock(MultipartFile.class);
    when(csvUploadUtilityService.uploadTransitTimesCsv(any(), any()))
        .thenThrow(new CsvFormatValidationFailedException("Invalid header orgId", "orgIds"));

    Exception exception =
        Assertions.assertThrows(
            CsvFormatValidationFailedException.class,
            () -> csvDownloadUtilityController.uploadTransitTimes(TestUtil.ORG_ID, csvFile));
    Assertions.assertNotNull(exception);
  }
}
