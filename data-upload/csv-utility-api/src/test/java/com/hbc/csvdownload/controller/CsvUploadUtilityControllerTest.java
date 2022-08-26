package com.hbc.csvdownload.controller;

import com.hbc.common.response.BaseResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class CsvUploadUtilityControllerTest {

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
  void uploadLeadProcessingTimeCSV() {
    MultipartFile csvFile = Mockito.mock(MultipartFile.class);
    ResponseEntity<BaseResponse<String>> res =
        csvUploadUtilityController.uploadLeadProcessingTimeCSV("BAY", csvFile);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res);
    Assertions.assertNotNull(res.getBody());
    Assertions.assertFalse(ObjectUtils.isEmpty(res.getBody().getMessage()));
  }
}
