package com.hbc.csvdownload.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.exception.CsvFormatValidationFailedException;
import com.hbc.csvdownload.exception.CsvParsingException;
import com.hbc.csvdownload.exception.JobSubmissionException;
import com.hbc.csvdownload.exception.JsonParsingException;
import com.hbc.csvdownload.service.CsvUploadUtilityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class CsvUploadUtilityControllerTest {

  @Mock private CsvUploadUtilityService csvUploadUtilityService;
  @InjectMocks private CsvUploadUtilityController csvUploadUtilityController;

  @Test
  void uploadTransitTimesCSV() {
    MultipartFile csvFile = Mockito.mock(MultipartFile.class);
    ResponseEntity<BaseResponse<String>> res =
        csvUploadUtilityController.uploadTransitTimesCSV("BAY", csvFile);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res);
    Assertions.assertNotNull(res.getBody());
    Assertions.assertFalse(ObjectUtils.isEmpty(res.getBody().getMessage()));
  }

  @Test
  void uploadLeadProcessingTimeCSV()
      throws CsvParsingException, CsvFormatValidationFailedException, JobSubmissionException,
          JsonParsingException {
    MultipartFile csvFile = Mockito.mock(MultipartFile.class);
    when(csvUploadUtilityService.uploadProcessingLeadTimesCsv(any(), any()))
        .thenReturn("Job to bulk upload processing lead times is submitted successfully");
    ResponseEntity<BaseResponse<String>> res =
        csvUploadUtilityController.uploadProcessingLeadTimes("BAY", csvFile);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res);
    Assertions.assertNotNull(res.getBody());
    Assertions.assertFalse(ObjectUtils.isEmpty(res.getBody().getMessage()));
  }
}
