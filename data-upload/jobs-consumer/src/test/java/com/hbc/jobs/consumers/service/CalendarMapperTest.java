package com.hbc.jobs.consumers.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hbc.calendar.domain.feign.CalendarFeign;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.exception.CsvDataValidationException;
import com.hbc.jobs.consumers.common.TestUtil;
import com.hbc.jobs.consumers.exception.InvalidActionTypeException;
import com.hbc.jobs.consumers.exception.InvalidJobTypeException;
import com.hbc.jobs.consumers.exception.NodeCarrierMapperException;
import com.hbc.jobs.consumers.exception.TransitMapperException;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.CarrierCalendarUpload;
import com.hbc.jobs.framework.common.domain.pojo.NodeCalendarUpload;
import com.hbc.jobs.framework.common.enums.ModuleEnum;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
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
    Assertions.assertNull(calendarMapper.getColumnNameMapping(new String[] {"request"}));
  }

  @Test
  void callApiCreateNodeCalendar()
      throws TransitMapperException, NodeCarrierMapperException, CommonServiceException,
          InvalidActionTypeException, InvalidJobTypeException {
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
      throws TransitMapperException, NodeCarrierMapperException, CommonServiceException,
          InvalidActionTypeException, InvalidJobTypeException {
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
}
