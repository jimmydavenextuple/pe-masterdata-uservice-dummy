package com.hbc.jobs.consumers.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.hbc.csvdownload.exception.CsvDataValidationException;
import com.hbc.jobs.consumers.common.TestUtil;
import com.hbc.jobs.consumers.exception.InvalidActionTypeException;
import com.hbc.jobs.consumers.exception.InvalidJobTypeException;
import com.hbc.jobs.consumers.exception.NodeCarrierMapperException;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.NodeCarrierUpload;
import com.hbc.jobs.framework.common.domain.pojo.ProcessingTimeBufferUpload;
import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
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
class NodeCarrierMapperTest {

  @Mock private NodeCarrierFeign nodeCarrierFeign;
  @InjectMocks private NodeCarrierMapper nodeCarrierMapper;
  @InjectMocks private TestUtil testUtil;

  @Test
  void getFromCustomMapper() {
    String csvData =
        "nodeId,orgId,serviceOptions,processingTime (in hrs)\n"
            + "1554,BAY,SDND,2\n"
            + "1560,BAY,SDND,2\n"
            + "1101,BAY,SDND,2\n"
            + "1518,BAY,NEXTDAY,6\n"
            + "1634,BAY,EXPRESS,30.92\n"
            + "1601,BAY,EXPRESS,22.55\n"
            + "1125,BAY,EXPRESS,19.9\n"
            + "1114,BAY,SDND,24.97";
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);
    Object res = nodeCarrierMapper.getDTOFromCustomMapper(csvData);
    Assertions.assertNotNull(res);
  }

  @Test
  void getFromCustomMapperInvalidJobType() {
    String csvData =
        "nodeId,orgId,serviceOptions,processingTime (in hrs)\n"
            + "1554,BAY,SDND,2\n"
            + "1560,BAY,SDND,2\n"
            + "1101,BAY,SDND,2\n"
            + "1518,BAY,NEXTDAY,6\n"
            + "1634,BAY,EXPRESS,30.92\n"
            + "1601,BAY,EXPRESS,22.55\n"
            + "1125,BAY,EXPRESS,19.9\n"
            + "1114,BAY,SDND,24.97";
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_TRANSIT_TIMES);
    Object res = nodeCarrierMapper.getDTOFromCustomMapper(csvData);
    Assertions.assertNull(res);
  }

  @Test
  void getColumnNameMapping() {
    String[] headerColumns = {"nodeId", "orgId", "serviceOptions", "processingTime (in hrs)"};
    Map<String, String> stringStringMap = nodeCarrierMapper.getColumnNameMapping(headerColumns);
    Assertions.assertFalse(CollectionUtils.isEmpty(stringStringMap));
  }

  @Test
  void mapTODto() throws NodeCarrierMapperException, InvalidJobTypeException {
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);
    Class res = nodeCarrierMapper.mapTODto();
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_NODE_CARRIER);
    Class uploadNodeCarrier = nodeCarrierMapper.mapTODto();
    Assertions.assertEquals(NodeCarrierUpload.class, uploadNodeCarrier);
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_NODE_SERVICE_OPTION_BUFFER);
    Class processingTimeBufferUpload = nodeCarrierMapper.mapTODto();
    Assertions.assertEquals(ProcessingTimeBufferUpload.class, processingTimeBufferUpload);
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_TRANSIT_TIMES);
    Exception exception =
        Assertions.assertThrows(InvalidJobTypeException.class, () -> nodeCarrierMapper.mapTODto());
    Assertions.assertNotNull(exception);
  }

  @Test
  void callApiUpdateAction()
      throws NodeCarrierMapperException, InvalidActionTypeException, CommonServiceException {
    Object object = testUtil.getProcessingLeadTime("U");
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);
    when(nodeCarrierFeign.createNodeCarrier(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getNodeCarrierResponse()).build());
    ResponseEntity<BaseResponse<NodeCarrierResponse>> res =
        (ResponseEntity<BaseResponse<NodeCarrierResponse>>) nodeCarrierMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
  }

  @Test
  void callApiDeleteAction()
      throws NodeCarrierMapperException, InvalidActionTypeException, CommonServiceException {
    Object object = testUtil.getProcessingLeadTime("D");
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);
    when(nodeCarrierFeign.deleteNodeCarrierByOrgIdNodeIdAndServiceOption(
            any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getNodeCarrierResponse()).build());
    ResponseEntity<BaseResponse<NodeCarrierResponse>> res =
        (ResponseEntity<BaseResponse<NodeCarrierResponse>>) nodeCarrierMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
  }

  @Test
  void callApiException() {
    Object object = testUtil.getNodeCarrierRequest();
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_TRANSIT_TIMES);
    Exception exception =
        Assertions.assertThrows(
            NodeCarrierMapperException.class, () -> nodeCarrierMapper.callApi(object, null));
    Assertions.assertNotNull(exception);
  }

  @Test
  void callApiInvalidAction() {
    Object object = testUtil.getProcessingLeadTime("A");
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);
    Exception exception =
        Assertions.assertThrows(
            InvalidActionTypeException.class, () -> nodeCarrierMapper.callApi(object, null));
    Assertions.assertNotNull(exception);
  }

  @Test
  void callApiInvalidProcessingEadTime() {
    ProcessingLeadTimesRaw processingLeadTimesRaw = testUtil.getProcessingLeadTime("U");
    processingLeadTimesRaw.setProcessingTime("invalid");
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);
    Exception exception =
        Assertions.assertThrows(
            CsvDataValidationException.class,
            () -> nodeCarrierMapper.callApi(processingLeadTimesRaw, null));
    Assertions.assertNotNull(exception);
  }

  @Test
  void callApiDeleteActionException1() {
    ProcessingLeadTimesRaw object = testUtil.getProcessingLeadTime("D");
    object.setNodeId(null);

    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);

    Assertions.assertThrows(
        CommonServiceException.class, () -> nodeCarrierMapper.callApi(object, null));
  }

  @Test
  void callApiDeleteActionException2() {
    ProcessingLeadTimesRaw object = testUtil.getProcessingLeadTime("D");
    object.setServiceOption(null);

    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);

    Assertions.assertThrows(
        CommonServiceException.class, () -> nodeCarrierMapper.callApi(object, null));
  }

  @Test
  void callApiDeleteActionException3() {
    ProcessingLeadTimesRaw object = testUtil.getProcessingLeadTime("D");
    object.setOrgId(null);

    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);

    Assertions.assertThrows(
        CommonServiceException.class, () -> nodeCarrierMapper.callApi(object, null));
  }

  @Test
  void callApiUploadNodeCarrierCreateAction()
      throws NodeCarrierMapperException, InvalidActionTypeException, CommonServiceException {
    Object object = testUtil.getNodeCarrierUpload("CREATE");
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_NODE_CARRIER);
    when(nodeCarrierFeign.createNodeCarrier(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getNodeCarrierResponse()).build());
    ResponseEntity<BaseResponse<NodeCarrierResponse>> res =
        (ResponseEntity<BaseResponse<NodeCarrierResponse>>) nodeCarrierMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
  }

  @Test
  void callApiUploadNodeCarrierUpdateAction()
      throws NodeCarrierMapperException, InvalidActionTypeException, CommonServiceException {
    Object object = testUtil.getNodeCarrierUpload("UPDATE");
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_NODE_CARRIER);
    when(nodeCarrierFeign.updateNodeCarrier(
            anyString(), anyString(), anyString(), anyString(), any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getNodeCarrierResponse()).build());
    ResponseEntity<BaseResponse<NodeCarrierResponse>> res =
        (ResponseEntity<BaseResponse<NodeCarrierResponse>>) nodeCarrierMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
  }

  @Test
  void callApiUploadNodeCarrierDeleteAction()
      throws NodeCarrierMapperException, InvalidActionTypeException, CommonServiceException {
    Object object = testUtil.getNodeCarrierUpload("DELETE");
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_NODE_CARRIER);
    when(nodeCarrierFeign.deleteNodeCarrier(anyString(), anyString(), anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getNodeCarrierResponse()).build());
    ResponseEntity<BaseResponse<NodeCarrierResponse>> res =
        (ResponseEntity<BaseResponse<NodeCarrierResponse>>) nodeCarrierMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
  }

  @Test
  void callApiUploadNodeCarrierInvalidAction() {
    Object object = testUtil.getNodeCarrierUpload("C");
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_NODE_CARRIER);

    Exception exception =
        Assertions.assertThrows(
            CsvDataValidationException.class, () -> nodeCarrierMapper.callApi(object, null));
    Assertions.assertNotNull(exception);
  }

  @Test
  void callApiUploadProcessingTimeBufferUpload()
      throws NodeCarrierMapperException, InvalidActionTypeException, CommonServiceException {
    Object object = testUtil.getProcessingTimeBufferUpload();
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_NODE_SERVICE_OPTION_BUFFER);
    when(nodeCarrierFeign.updateBuffer(any()))
        .thenReturn(
            BaseResponse.builder().payload(testUtil.getNodeCarrierBufferResponse()).build());
    ResponseEntity<BaseResponse<NodeCarrierResponse>> res =
        (ResponseEntity<BaseResponse<NodeCarrierResponse>>) nodeCarrierMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
  }
}
