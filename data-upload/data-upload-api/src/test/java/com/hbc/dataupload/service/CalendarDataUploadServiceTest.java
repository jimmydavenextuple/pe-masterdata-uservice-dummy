package com.hbc.dataupload.service;

import static com.hbc.dataupload.helper.CalendarDataUploadConstants.CALENDAR_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hbc.calendar.domain.feign.CalendarFeign;
import com.hbc.calendar.domain.outbound.CalendarResponse;
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
class CalendarDataUploadServiceTest {

  @InjectMocks CalendarDataUploadService calendarDataUploadService;

  @InjectMocks TestUtil testUtil;

  @Mock CalendarFeign calendarFeign;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(calendarDataUploadService, "basePath", "");
    ReflectionTestUtils.setField(calendarDataUploadService, "maxSizeInKiloBytes", 10240);
    ReflectionTestUtils.setField(calendarDataUploadService, "maxRows", 1000);
  }

  @Test
  void uploadCalendarDataSuccessTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "calendar", "calendar_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<CalendarResponse> baseResponse = testUtil.getBaseResponseOfCalendar();
    when(calendarFeign.handleCreateCalendar(any())).thenReturn(baseResponse);

    ResponseEntity<BaseResponse<String>> response =
        calendarDataUploadService.uploadCalendarData(absolutePath);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Calendar Data successfully uploaded!", response.getBody().getMessage());
  }

  @Test
  void uploadCalendarInvalidHeadersExceptionTest() {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "calendar", "calendar_invalidHeaders.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> calendarDataUploadService.uploadCalendarData(absolutePath));

    assertEquals(CALENDAR_DATA_UPLOAD_INVALID_FILE_HEADERS, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadCalendarInvalidFileTypeExceptionTest() {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "calendar", "calendar_invalidFileType.html");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> calendarDataUploadService.uploadCalendarData(absolutePath));

    assertEquals("Calendar data uploaded file is not a csv file.", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadCalendarLargeFileSizeExceptionTest() {
    ReflectionTestUtils.setField(calendarDataUploadService, "maxSizeInKiloBytes", 1);

    Path resourceDirectory =
        Paths.get("src", "test", "resources", "calendar", "calendar_largeFile.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> calendarDataUploadService.uploadCalendarData(absolutePath));

    assertEquals(
        "Calendar data uploaded file has size greater than 10240 kB.", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadCalendarEmptyRecordsExceptionTest() {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "calendar", "calendar_emptyRecords.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> calendarDataUploadService.uploadCalendarData(absolutePath));

    assertEquals("Calendar data uploaded file has no records.", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadCalendarLargeRowSizeExceptionTest() {
    ReflectionTestUtils.setField(calendarDataUploadService, "maxRows", 30);

    Path resourceDirectory =
        Paths.get("src", "test", "resources", "calendar", "calendar_largeFile.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> calendarDataUploadService.uploadCalendarData(absolutePath));

    assertEquals("Calendar data uploaded file has more than 1000 rows.", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void uploadCalendarInvalidActionExceptionTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "calendar", "calendar_invalidAction.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    ResponseEntity<BaseResponse<String>> response =
        calendarDataUploadService.uploadCalendarData(absolutePath);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void uploadCalendarDataFailureTest() throws CommonServiceException, IOException {
    Path resourceDirectory = Paths.get("src", "test", "resources", "calendar", "calendar.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    ResponseEntity<BaseResponse<String>> response =
        calendarDataUploadService.uploadCalendarData(absolutePath);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Calendar Data upload failed!", response.getBody().getMessage());
  }

  @Test
  void uploadCalendarDataPartialUploadTest() throws CommonServiceException, IOException {
    Path resourceDirectory = Paths.get("src", "test", "resources", "calendar", "calendar.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<CalendarResponse> baseResponse = testUtil.getBaseResponseOfCalendar();
    when(calendarFeign.handleCreateCalendar(any())).thenReturn(baseResponse);

    ResponseEntity<BaseResponse<String>> response =
        calendarDataUploadService.uploadCalendarData(absolutePath);

    assertEquals(HttpStatus.MULTI_STATUS, response.getStatusCode());
    assertEquals(
        "Calendar Data partially uploaded with some rows failed!", response.getBody().getMessage());
  }
}
