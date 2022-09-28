package com.hbc.jobs.consumers.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.common.pojo.TransitDataUpload;
import com.hbc.csvdownload.exception.CsvDataValidationException;
import com.hbc.jobs.consumers.common.TestUtil;
import com.hbc.jobs.consumers.exception.TransitMapperException;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.transit.domain.feign.TransitFeign;
import com.hbc.transit.domain.outbound.TransitResponse;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

@ExtendWith(MockitoExtension.class)
class TransitMapperTest {

  @Mock private TransitFeign transitFeign;
  @InjectMocks private TransitMapper transitMapper;
  @InjectMocks private TestUtil testUtil;

  @Test
  void getColumnNameMapping() {
    String[] csvHeaders = {"header"};
    Map<String, String> map = transitMapper.getColumnNameMapping(csvHeaders);
    Assertions.assertFalse(CollectionUtils.isEmpty(map));
  }

  @Test
  void getDTOFromCustomMapper() {
    Assertions.assertNull(transitMapper.getDTOFromCustomMapper(""));
  }

  @Test
  void mapToDtoForUploadTransitTimes() throws TransitMapperException {
    transitMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_TRANSIT_TIMES);
    Class res = transitMapper.mapTODto();
    Assertions.assertEquals(TransitDataUpload.class, res);
  }

  @Test
  void mapToDtoForDeleteTransitBuffer() throws TransitMapperException {
    transitMapper.setJobTypeEnum(JobTypeEnum.DELETE_TRANSIT_BUFFER);
    Class res = transitMapper.mapTODto();
    Assertions.assertEquals(TransitDataUpload.class, res);
  }

  @Test
  void mapToDtoInvalidJobTypeException() {
    transitMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);
    Exception exception =
        Assertions.assertThrows(TransitMapperException.class, () -> transitMapper.mapTODto());
    Assertions.assertNotNull(exception);
  }

  @Test
  void callApi() throws TransitMapperException {
    Object object = testUtil.getTransitDataUpload();
    transitMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_TRANSIT_TIMES);
    when(transitFeign.addTransitData(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getTransitResponse()).build());
    ResponseEntity<BaseResponse<TransitResponse>> res =
        (ResponseEntity<BaseResponse<TransitResponse>>) transitMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
  }

  @Test
  void callApiForDeleteTransitBuffer() throws TransitMapperException {
    Object object = testUtil.getTransitDataUpload();
    transitMapper.setJobTypeEnum(JobTypeEnum.DELETE_TRANSIT_BUFFER);
    when(transitFeign.deleteBufferDays(any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getTransitResponse()).build());
    ResponseEntity<BaseResponse<TransitResponse>> res =
        (ResponseEntity<BaseResponse<TransitResponse>>) transitMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
  }

  @Test
  void callApiDeleteTransitEntryTest() throws TransitMapperException {
    TransitDataUpload transitDataUpload = testUtil.getTransitDataUpload();
    transitDataUpload.setTransitDays("D");
    transitMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_TRANSIT_TIMES);
    when(transitFeign.deleteTransitDetails(any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getTransitResponse()).build());
    ResponseEntity<BaseResponse<TransitResponse>> res =
        (ResponseEntity<BaseResponse<TransitResponse>>)
            transitMapper.callApi(transitDataUpload, null);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
  }

  @Test
  void callApiInvalidTransitEntryTest() {
    var transitDataUpload = testUtil.getTransitDataUpload();
    transitDataUpload.setTransitDays("invalid");
    transitMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_TRANSIT_TIMES);
    Exception exception =
        Assertions.assertThrows(
            CsvDataValidationException.class, () -> transitMapper.callApi(transitDataUpload, null));

    Assertions.assertNotNull(exception);
  }

  @Test
  void callApiException() {
    TransitDataUpload transitDataUpload = testUtil.getTransitDataUpload();
    transitDataUpload.setTransitDays("");

    transitMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);
    Exception exception =
        Assertions.assertThrows(
            TransitMapperException.class, () -> transitMapper.callApi(transitDataUpload, null));
    Assertions.assertNotNull(exception);
  }
}
