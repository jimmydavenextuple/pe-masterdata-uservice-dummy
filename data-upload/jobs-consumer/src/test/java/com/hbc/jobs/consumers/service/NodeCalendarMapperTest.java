package com.hbc.jobs.consumers.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hbc.calendar.domain.feign.CalendarFeign;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.exception.CsvDataValidationException;
import com.hbc.jobs.consumers.common.TestUtil;
import com.hbc.jobs.consumers.exception.InvalidActionTypeException;
import com.hbc.jobs.consumers.exception.NodeCarrierMapperException;
import com.hbc.jobs.consumers.exception.TransitMapperException;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
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
class NodeCalendarMapperTest {

  @Mock private CalendarFeign calendarFeign;
  @InjectMocks private NodeCalendarMapper nodeCalendarMapper;
  @InjectMocks private TestUtil testUtil;

  @Test
  void mapTODto() {
    Class nodeDataUpload = nodeCalendarMapper.mapTODto();
    Assertions.assertEquals(NodeCalendarUpload.class, nodeDataUpload);
  }

  @Test
  void getModule() {
    ModuleEnum moduleEnum = nodeCalendarMapper.getModule();
    Assertions.assertEquals(ModuleEnum.NODE_CALENDAR, moduleEnum);
  }

  @Test
  void setJobType() {
    Assertions.assertDoesNotThrow(
        () -> nodeCalendarMapper.setJobType(JobTypeEnum.UPLOAD_NODE_CALENDER));
  }

  @Test
  void getDTOFromCustomMapper() {
    Assertions.assertNull(nodeCalendarMapper.getDTOFromCustomMapper("request"));
  }

  @Test
  void getColumnNameMapping() {
    Assertions.assertNull(nodeCalendarMapper.getColumnNameMapping(new String[] {"request"}));
  }

  @Test
  void callApiCreateNodeCalendar()
      throws TransitMapperException, NodeCarrierMapperException, CommonServiceException,
          InvalidActionTypeException {
    Object object = testUtil.getNodeCalendarUpload("CREATE");
    when(calendarFeign.createNodeCalendar(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getNodeResponse()).build());

    ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> response =
        (ResponseEntity<BaseResponse<PostalCodeTimezoneDto>>)
            nodeCalendarMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void callApiInvalidAction() {
    Object object = testUtil.getNodeCalendarUpload("DEL");

    Exception exception =
        Assertions.assertThrows(
            CsvDataValidationException.class, () -> nodeCalendarMapper.callApi(object, null));

    Assertions.assertNotNull(exception);
  }
}
