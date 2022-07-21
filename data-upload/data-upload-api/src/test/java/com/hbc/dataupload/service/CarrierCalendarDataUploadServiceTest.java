package com.hbc.dataupload.service;

import static com.hbc.dataupload.helper.CarrierCalendarDataUploadConstants.CARRIER_CALENDAR_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hbc.calendar.domain.feign.CalendarFeign;
import com.hbc.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.util.TestUtil;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CarrierCalendarDataUploadServiceTest {

  @InjectMocks CarrierCalendarDataUploadService calendarDataUploadUtilityService;

  @InjectMocks TestUtil testUtil;

  @Mock CalendarFeign calendarFeign;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(calendarDataUploadUtilityService, "basePath", "");
    ReflectionTestUtils.setField(calendarDataUploadUtilityService, "maxSizeInKiloBytes", 10240);
    ReflectionTestUtils.setField(calendarDataUploadUtilityService, "maxRows", 1000);
  }

  @Test
  void uploadCarrierCalendarDataSuccessTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "carrierCalendar", "carrierCalendar_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<CarrierServiceCalendarResponse> baseResponse =
        testUtil.getBaseResponseOfCarrierCalendar();
    when(calendarFeign.handleCreateCarrierServiceCalendar(any())).thenReturn(baseResponse);

    ResponseEntity<BaseResponse<String>> response =
        calendarDataUploadUtilityService.uploadCarrierCalendarData(absolutePath);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Carrier Calendar Data successfully uploaded!", response.getBody().getMessage());
  }

  @Test
  void uploadCarrierCalendarInvalidHeadersExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "carrierCalendar", "carrierCalendar_invalidHeaders.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> calendarDataUploadUtilityService.uploadCarrierCalendarData(absolutePath));

    assertEquals(CARRIER_CALENDAR_DATA_UPLOAD_INVALID_FILE_HEADERS, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadCarrierCalendarInvalidFileTypeExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "carrierCalendar", "carrierCalendar_invalidFileType.html");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> calendarDataUploadUtilityService.uploadCarrierCalendarData(absolutePath));

    assertEquals("Carrier Calendar data uploaded file is not a csv file.", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadCarrierCalendarLargeFileSizeExceptionTest() {
    ReflectionTestUtils.setField(calendarDataUploadUtilityService, "maxSizeInKiloBytes", 1);

    Path resourceDirectory =
        Paths.get("src", "test", "resources", "carrierCalendar", "carrierCalendar_largeFile.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> calendarDataUploadUtilityService.uploadCarrierCalendarData(absolutePath));

    assertEquals(
        "Carrier Calendar data uploaded file has size greater than 10240 kB.",
        exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadCarrierCalendarEmptyRecordsExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "carrierCalendar", "carrierCalendar_emptyRecords.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> calendarDataUploadUtilityService.uploadCarrierCalendarData(absolutePath));

    assertEquals("Carrier Calendar data uploaded file has no records.", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadCarrierCalendarLargeRowSizeExceptionTest() {
    ReflectionTestUtils.setField(calendarDataUploadUtilityService, "maxRows", 30);

    Path resourceDirectory =
        Paths.get("src", "test", "resources", "carrierCalendar", "carrierCalendar_largeFile.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> calendarDataUploadUtilityService.uploadCarrierCalendarData(absolutePath));

    assertEquals(
        "Carrier Calendar data uploaded file has exceeded maximum file size limit.",
        exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadCarrierCalendarInvalidActionExceptionTest()
      throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "carrierCalendar", "carrierCalendar_invalidAction.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    ResponseEntity<BaseResponse<String>> response =
        calendarDataUploadUtilityService.uploadCarrierCalendarData(absolutePath);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void uploadCarrierCalendarDataFailureTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "carrierCalendar", "carrierCalendar.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    ResponseEntity<BaseResponse<String>> response =
        calendarDataUploadUtilityService.uploadCarrierCalendarData(absolutePath);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Carrier Calendar Data upload failed!", response.getBody().getMessage());
  }

  @Test
  void uploadCarrierCalendarDataPartialUploadTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "carrierCalendar", "carrierCalendar.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<CarrierServiceCalendarResponse> baseResponse =
        testUtil.getBaseResponseOfCarrierCalendar();
    when(calendarFeign.handleCreateCarrierServiceCalendar(any())).thenReturn(baseResponse);

    ResponseEntity<BaseResponse<String>> response =
        calendarDataUploadUtilityService.uploadCarrierCalendarData(absolutePath);

    assertEquals(HttpStatus.MULTI_STATUS, response.getStatusCode());
    assertEquals(
        "Carrier Calendar Data partially uploaded with some rows failed!",
        response.getBody().getMessage());
  }
}
