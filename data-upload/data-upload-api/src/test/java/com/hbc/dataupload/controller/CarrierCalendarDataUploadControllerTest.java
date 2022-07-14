package com.hbc.dataupload.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.service.CarrierCalendarDataUploadService;
import com.hbc.dataupload.util.TestUtil;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CarrierCalendarDataUploadControllerTest {

  @InjectMocks CarrierCalendarDataUploadController calendarDataUploadUtilityController;

  @InjectMocks private TestUtil testUtil;

  @Mock CarrierCalendarDataUploadService carrierCalendarDataUploadService;

  @Test
  void uploadCarrierCalendarDataSuccessTest() throws CommonServiceException, IOException {
    String fileUri = "fileName.csv";
    ResponseEntity<BaseResponse<String>> response =
        testUtil.getBaseResponse(HttpStatus.OK, "Carrier Calendar Data successfully uploaded!");
    when(carrierCalendarDataUploadService.uploadCarrierCalendarData(any())).thenReturn(response);

    ResponseEntity<BaseResponse<String>> actualResponse =
        calendarDataUploadUtilityController.uploadCarrierCalendarData(fileUri);

    assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
    assertEquals(
        "Carrier Calendar Data successfully uploaded!", actualResponse.getBody().getMessage());
    verify(carrierCalendarDataUploadService, times(1)).uploadCarrierCalendarData(any());
  }

  @Test
  void uploadCarrierCalendarDataUploadFailedTest() throws CommonServiceException, IOException {
    String fileUri = "fileName.csv";
    ResponseEntity<BaseResponse<String>> response =
        testUtil.getBaseResponse(HttpStatus.BAD_REQUEST, "Carrier Calendar Data upload failed!");
    when(carrierCalendarDataUploadService.uploadCarrierCalendarData(any())).thenReturn(response);

    ResponseEntity<BaseResponse<String>> actualResponse =
        calendarDataUploadUtilityController.uploadCarrierCalendarData(fileUri);

    assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
    assertEquals("Carrier Calendar Data upload failed!", actualResponse.getBody().getMessage());
    verify(carrierCalendarDataUploadService, times(1)).uploadCarrierCalendarData(any());
  }

  @Test
  void uploadCarrierCalendarDataPartialUploadTest() throws CommonServiceException, IOException {
    String fileUri = "fileName.csv";
    ResponseEntity<BaseResponse<String>> response =
        testUtil.getBaseResponse(
            HttpStatus.MULTI_STATUS,
            "Carrier Calendar Data partially uploaded with some rows failed!");
    when(carrierCalendarDataUploadService.uploadCarrierCalendarData(any())).thenReturn(response);

    ResponseEntity<BaseResponse<String>> actualResponse =
        calendarDataUploadUtilityController.uploadCarrierCalendarData(fileUri);

    assertEquals(HttpStatus.MULTI_STATUS, actualResponse.getStatusCode());
    assertEquals(
        "Carrier Calendar Data partially uploaded with some rows failed!",
        actualResponse.getBody().getMessage());
    verify(carrierCalendarDataUploadService, times(1)).uploadCarrierCalendarData(any());
  }

  @Test
  void uploadCarrierCalendarDataExceptionTest() throws CommonServiceException, IOException {
    String fileUri = "fileName.csv";
    when(carrierCalendarDataUploadService.uploadCarrierCalendarData(any()))
        .thenThrow(new IOException("File not found!"));

    Exception exception =
        assertThrows(
            IOException.class,
            () -> calendarDataUploadUtilityController.uploadCarrierCalendarData(fileUri));

    assertEquals("File not found!", exception.getMessage());
    verify(carrierCalendarDataUploadService, times(1)).uploadCarrierCalendarData(any());
  }
}
