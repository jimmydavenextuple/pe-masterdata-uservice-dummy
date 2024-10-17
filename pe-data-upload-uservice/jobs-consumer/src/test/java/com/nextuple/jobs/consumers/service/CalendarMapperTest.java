/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.service;

import static com.nextuple.jobs.consumers.common.TestUtil.EXCEPTION_DAYS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.nextuple.calendar.domain.feign.CalendarFeign;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.exception.CsvDataValidationException;
import com.nextuple.jobs.consumers.common.TestUtil;
import com.nextuple.jobs.consumers.exception.InvalidActionTypeException;
import com.nextuple.jobs.consumers.exception.InvalidJobTypeException;
import com.nextuple.jobs.consumers.exception.NodeCarrierMapperException;
import com.nextuple.jobs.consumers.exception.TransitMapperException;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.CalendarDataUpload;
import com.nextuple.jobs.framework.common.domain.pojo.CarrierCalendarUpload;
import com.nextuple.jobs.framework.common.domain.pojo.NodeCalendarUpload;
import com.nextuple.jobs.framework.common.domain.pojo.PickUpCalendarUpload;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CalendarMapperTest {

  @Mock private CalendarFeign calendarFeign;
  @InjectMocks private CalendarMapper calendarMapper;
  @InjectMocks private TestUtil testUtil;

  @Test
  void mapTODto() throws InvalidJobTypeException {
    calendarMapper.setJobType(JobTypeEnum.UPLOAD_NODE_CALENDER);
    Class nodeDataUpload = calendarMapper.mapTODto();
    Assertions.assertEquals(NodeCalendarUpload.class, nodeDataUpload);

    calendarMapper.setJobType(JobTypeEnum.UPLOAD_CARRIER_SERVICE_CALENDER);
    Class carrierCalendarUpload = calendarMapper.mapTODto();
    Assertions.assertEquals(CarrierCalendarUpload.class, carrierCalendarUpload);

    calendarMapper.setJobType(JobTypeEnum.UPLOAD_CALENDER);
    Class calendarUpload = calendarMapper.mapTODto();
    Assertions.assertEquals(CalendarDataUpload.class, calendarUpload);

    calendarMapper.setJobType(JobTypeEnum.UPLOAD_PICKUP_CALENDER);
    Class pickUpCalendarUpload = calendarMapper.mapTODto();
    Assertions.assertEquals(PickUpCalendarUpload.class, pickUpCalendarUpload);

    calendarMapper.setJobType(JobTypeEnum.TRANSIT_BUFFER_REQUEST);
    Exception exception =
        Assertions.assertThrows(InvalidJobTypeException.class, () -> calendarMapper.mapTODto());
    Assertions.assertNotNull(exception);
  }

  @Test
  void getModule() {
    ModuleEnum moduleEnum = calendarMapper.getModule();
    Assertions.assertEquals(ModuleEnum.CALENDAR, moduleEnum);
  }

  @Test
  void setJobType() {
    Assertions.assertDoesNotThrow(
        () -> calendarMapper.setJobType(JobTypeEnum.UPLOAD_NODE_CALENDER));
  }

  @Test
  void getDTOFromCustomMapper() {
    Assertions.assertNull(calendarMapper.getDTOFromCustomMapper("request"));
  }

  @Test
  void getColumnNameMapping() {
    Assertions.assertNotNull(calendarMapper.getColumnNameMapping(new String[] {"request"}));
  }

  @Test
  void callApiCreateNodeCalendar()
      throws TransitMapperException,
          NodeCarrierMapperException,
          CommonServiceException,
          InvalidActionTypeException,
          InvalidJobTypeException,
          JsonProcessingException {
    calendarMapper.setJobType(JobTypeEnum.UPLOAD_NODE_CALENDER);
    Object object = testUtil.getNodeCalendarUpload("CREATE");
    when(calendarFeign.createNodeCalendar(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getNodeResponse()).build());

    ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> response =
        (ResponseEntity<BaseResponse<PostalCodeTimezoneDto>>) calendarMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void callApiInvalidAction() {
    calendarMapper.setJobType(JobTypeEnum.UPLOAD_NODE_CALENDER);
    Object object = testUtil.getNodeCalendarUpload("DEL");

    Exception exception =
        Assertions.assertThrows(
            CsvDataValidationException.class, () -> calendarMapper.callApi(object, null));

    Assertions.assertNotNull(exception);
  }

  @Test
  void callApiCreateCarrierServiceCalendar()
      throws TransitMapperException,
          NodeCarrierMapperException,
          CommonServiceException,
          InvalidActionTypeException,
          InvalidJobTypeException,
          JsonProcessingException {
    calendarMapper.setJobType(JobTypeEnum.UPLOAD_CARRIER_SERVICE_CALENDER);
    Object object = testUtil.getCarrierCalendarUpload("CREATE");
    when(calendarFeign.createCarrierServiceCalendar(any()))
        .thenReturn(
            BaseResponse.builder().payload(testUtil.getCarrierServiceCalendarResponse()).build());

    ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> response =
        (ResponseEntity<BaseResponse<PostalCodeTimezoneDto>>) calendarMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void callApiCreateCarrierServiceCalendarInvalidAction() {
    calendarMapper.setJobType(JobTypeEnum.UPLOAD_CARRIER_SERVICE_CALENDER);
    Object object = testUtil.getCarrierCalendarUpload("DEL");

    Exception exception =
        Assertions.assertThrows(
            CsvDataValidationException.class, () -> calendarMapper.callApi(object, null));

    Assertions.assertNotNull(exception);
  }

  @Test
  void callApiInvalidJobType() {
    calendarMapper.setJobType(JobTypeEnum.TRANSIT_BUFFER_REQUEST);
    Object object = testUtil.getCarrierCalendarUpload("CREATE");

    Exception exception =
        Assertions.assertThrows(
            InvalidJobTypeException.class, () -> calendarMapper.callApi(object, null));

    Assertions.assertNotNull(exception);
  }

  @Test
  void callApiCreateCalendar()
      throws TransitMapperException,
          NodeCarrierMapperException,
          CommonServiceException,
          InvalidActionTypeException,
          InvalidJobTypeException,
          JsonProcessingException {
    calendarMapper.setJobType(JobTypeEnum.UPLOAD_CALENDER);
    Object object = testUtil.getCalendarDataUpload("CREATE", EXCEPTION_DAYS);
    when(calendarFeign.createCalendar(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getCalendarResponse()).build());

    ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> response =
        (ResponseEntity<BaseResponse<PostalCodeTimezoneDto>>) calendarMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void callApiCreateCalendarWithInvalidBooleanValues() {
    calendarMapper.setJobType(JobTypeEnum.UPLOAD_CALENDER);
    Object object = testUtil.getInvalidCalendarDataUpload("CREATE", EXCEPTION_DAYS);

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class, () -> calendarMapper.callApi(object, null));

    Assertions.assertNotNull(exception);
  }

  @Test
  void callApiCreateCalendarWithInvalidExceptionDays() {
    calendarMapper.setJobType(JobTypeEnum.UPLOAD_CALENDER);
    Object object = testUtil.getCalendarDataUpload("CREATE", "1");

    Exception exception =
        Assertions.assertThrows(
            MismatchedInputException.class, () -> calendarMapper.callApi(object, null));

    Assertions.assertNotNull(exception);
  }

  @Test
  void callApiCreateCalendarInvalidAction() {
    calendarMapper.setJobType(JobTypeEnum.UPLOAD_CALENDER);
    Object object = testUtil.getCalendarDataUpload("DEL", EXCEPTION_DAYS);

    Exception exception =
        Assertions.assertThrows(
            CsvDataValidationException.class, () -> calendarMapper.callApi(object, null));

    Assertions.assertNotNull(exception);
  }

  @Test
  void callApiCreatePickUpCalendar()
      throws TransitMapperException,
          NodeCarrierMapperException,
          CommonServiceException,
          InvalidActionTypeException,
          InvalidJobTypeException,
          JsonProcessingException {
    calendarMapper.setJobType(JobTypeEnum.UPLOAD_PICKUP_CALENDER);
    Object object = testUtil.getPickUpCalendarUpload("CREATE");
    when(calendarFeign.createNodeCarrierServiceCalendar(any()))
        .thenReturn(
            BaseResponse.builder()
                .payload(testUtil.getNodeCarrierServiceCalendarResponse())
                .build());

    ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> response =
        (ResponseEntity<BaseResponse<PostalCodeTimezoneDto>>) calendarMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void callApiCreatePickUpCalendarInvalidAction() {
    calendarMapper.setJobType(JobTypeEnum.UPLOAD_PICKUP_CALENDER);
    Object object = testUtil.getPickUpCalendarUpload("DEL");

    Exception exception =
        Assertions.assertThrows(
            CsvDataValidationException.class, () -> calendarMapper.callApi(object, null));

    Assertions.assertNotNull(exception);
  }
}
