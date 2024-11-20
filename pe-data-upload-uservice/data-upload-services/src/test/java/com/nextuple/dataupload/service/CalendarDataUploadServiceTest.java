/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static com.nextuple.dataupload.helper.CalendarDataUploadConstants.CALENDAR_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nextuple.calendar.domain.feign.CalendarFeign;
import com.nextuple.calendar.domain.outbound.CalendarResponse;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.util.TestUtil;
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
    when(calendarFeign.createCalendar(any())).thenReturn(baseResponse);

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

    assertEquals(
        "Calendar data uploaded file has exceeded maximum file size limit.",
        exception.getMessage());
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
    when(calendarFeign.createCalendar(any())).thenReturn(baseResponse);

    ResponseEntity<BaseResponse<String>> response =
        calendarDataUploadService.uploadCalendarData(absolutePath);

    assertEquals(HttpStatus.MULTI_STATUS, response.getStatusCode());
    assertEquals(
        "Calendar Data partially uploaded with some rows failed!", response.getBody().getMessage());
  }
}
