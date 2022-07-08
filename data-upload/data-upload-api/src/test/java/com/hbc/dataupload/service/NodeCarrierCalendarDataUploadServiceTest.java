package com.hbc.dataupload.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hbc.calendar.domain.feign.CalendarFeign;
import com.hbc.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
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
class NodeCarrierCalendarDataUploadServiceTest {

  @InjectMocks NodeCarrierCalendarDataUploadService calendarDataUploadUtilityService;

  @InjectMocks TestUtil testUtil;

  @Mock CalendarFeign calendarFeign;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(calendarDataUploadUtilityService, "basePath", "");
    ReflectionTestUtils.setField(calendarDataUploadUtilityService, "maxSizeInKiloBytes", 10240);
    ReflectionTestUtils.setField(calendarDataUploadUtilityService, "maxRows", 1000);
  }

  @Test
  void uploadNodeCarrierCalendarDataSuccessTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "nodeCarrierCalendar", "nodeCarrierCalendar_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<NodeCarrierServiceCalendarResponse> baseResponse =
        testUtil.getBaseResponseOfNodeCarrierCalendar();
    when(calendarFeign.handleCreateNodeCarrierServiceCalendar(any())).thenReturn(baseResponse);

    ResponseEntity<BaseResponse<String>> response =
        calendarDataUploadUtilityService.uploadNodeCarrierCalendarData(absolutePath);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(
        "Node Carrier Calendar Data successfully uploaded!", response.getBody().getMessage());
  }

  @Test
  void uploadNodeCarrierCalendarInvalidHeadersExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeCarrierCalendar",
            "nodeCarrierCalendar_invalidHeaders.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> calendarDataUploadUtilityService.uploadNodeCarrierCalendarData(absolutePath));

    assertEquals(
        "Node Carrier Calendar data uploaded file has invalid headers.", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadNodeCarrierCalendarInvalidFileTypeExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeCarrierCalendar",
            "nodeCarrierCalendar_invalidFileType.html");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> calendarDataUploadUtilityService.uploadNodeCarrierCalendarData(absolutePath));

    assertEquals(
        "Node Carrier Calendar data uploaded file is not a csv file.", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadNodeCarrierCalendarLargeFileSizeExceptionTest() {
    ReflectionTestUtils.setField(calendarDataUploadUtilityService, "maxSizeInKiloBytes", 1);

    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "nodeCarrierCalendar", "nodeCarrierCalendar_largeFile.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> calendarDataUploadUtilityService.uploadNodeCarrierCalendarData(absolutePath));

    assertEquals(
        "Node Carrier Calendar data uploaded file has size greater than 10240 kB.",
        exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadNodeCarrierCalendarEmptyRecordsExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeCarrierCalendar",
            "nodeCarrierCalendar_emptyRecords.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> calendarDataUploadUtilityService.uploadNodeCarrierCalendarData(absolutePath));

    assertEquals(
        "Node Carrier Calendar data uploaded file has no records.", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadNodeCarrierCalendarLargeRowSizeExceptionTest() {
    ReflectionTestUtils.setField(calendarDataUploadUtilityService, "maxRows", 30);

    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "nodeCarrierCalendar", "nodeCarrierCalendar_largeFile.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> calendarDataUploadUtilityService.uploadNodeCarrierCalendarData(absolutePath));

    assertEquals(
        "Node Carrier Calendar data uploaded file has more than 1000 rows.",
        exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadNodeCarrierCalendarInvalidActionExceptionTest()
      throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeCarrierCalendar",
            "nodeCarrierCalendar_invalidAction.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    ResponseEntity<BaseResponse<String>> response =
        calendarDataUploadUtilityService.uploadNodeCarrierCalendarData(absolutePath);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void uploadNodeCarrierCalendarDataFailureTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "nodeCarrierCalendar", "nodeCarrierCalendar.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    ResponseEntity<BaseResponse<String>> response =
        calendarDataUploadUtilityService.uploadNodeCarrierCalendarData(absolutePath);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Node Carrier Calendar Data upload failed!", response.getBody().getMessage());
  }

  @Test
  void uploadNodeCarrierCalendarDataPartialUploadTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "nodeCarrierCalendar", "nodeCarrierCalendar.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<NodeCarrierServiceCalendarResponse> baseResponse =
        testUtil.getBaseResponseOfNodeCarrierCalendar();
    when(calendarFeign.handleCreateNodeCarrierServiceCalendar(any())).thenReturn(baseResponse);

    ResponseEntity<BaseResponse<String>> response =
        calendarDataUploadUtilityService.uploadNodeCarrierCalendarData(absolutePath);

    assertEquals(HttpStatus.MULTI_STATUS, response.getStatusCode());
    assertEquals(
        "Node Carrier Calendar Data partially uploaded with some rows failed!",
        response.getBody().getMessage());
  }
}
