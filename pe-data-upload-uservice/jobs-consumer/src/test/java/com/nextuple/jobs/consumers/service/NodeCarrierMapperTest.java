/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.nextuple.csvdownload.exception.CsvDataValidationException;
import com.nextuple.dataupload.common.config.TenantDatabaseConfig;
import com.nextuple.jobs.consumers.common.TestUtil;
import com.nextuple.jobs.consumers.exception.InvalidActionTypeException;
import com.nextuple.jobs.consumers.exception.InvalidDateException;
import com.nextuple.jobs.consumers.exception.InvalidJobTypeException;
import com.nextuple.jobs.consumers.exception.NodeCarrierMapperException;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.NodeCarrierUpload;
import com.nextuple.jobs.framework.common.domain.pojo.ProcessingTimeBufferUpload;
import com.nextuple.node.carrier.domain.feign.impl.NodeCarrierV2Feign;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionBufferResponse;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.CollectionUtils;

@ExtendWith(MockitoExtension.class)
class NodeCarrierMapperTest {

  @Mock private NodeCarrierV2Feign nodeCarrierFeign;
  @InjectMocks private NodeCarrierMapper nodeCarrierMapper;
  @InjectMocks private TestUtil testUtil;
  @Mock TenantDatabaseConfig tenantDatabaseConfig;

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(nodeCarrierMapper, "nodeCarrierFeign", nodeCarrierFeign);
  }

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
  @DisplayName("Update node processing lead time, Happy path")
  void callApiUpdateAction()
      throws NodeCarrierMapperException, InvalidActionTypeException, CommonServiceException {
    Object object = testUtil.getProcessingLeadTime("UPDATE");
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);
    var expectedResponsePayload = testUtil.getNodeCarrierResponse();
    when(nodeCarrierFeign.createProcessingLeadTime(any()))
        .thenReturn(BaseResponse.builder().payload(expectedResponsePayload).build());
    when(tenantDatabaseConfig.fetchServiceOptions(any())).thenReturn("EXPRESS,SDND");
    ResponseEntity<BaseResponse<NodeCarrierResponse>> res =
        (ResponseEntity<BaseResponse<NodeCarrierResponse>>) nodeCarrierMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
    Assertions.assertEquals(
        expectedResponsePayload.getProcessingTime(),
        res.getBody().getPayload().getProcessingTime());
    verify(nodeCarrierFeign, times(1)).createProcessingLeadTime(any());
  }

  @Test
  @DisplayName("Delete node processing lead time, Happy path")
  void callApiDeleteAction()
      throws NodeCarrierMapperException, InvalidActionTypeException, CommonServiceException {
    Object object = testUtil.getProcessingLeadTime("DELETE");
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);
    var expectedResponsePayload = testUtil.getNodeCarrierResponse();
    when(nodeCarrierFeign.deleteNodeCarrierByOrgIdNodeIdAndServiceOption(
            any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(expectedResponsePayload).build());
    ResponseEntity<BaseResponse<NodeCarrierResponse>> res =
        (ResponseEntity<BaseResponse<NodeCarrierResponse>>) nodeCarrierMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
    Assertions.assertEquals(
        expectedResponsePayload.getProcessingTime(),
        res.getBody().getPayload().getProcessingTime());
    verify(nodeCarrierFeign, times(1))
        .deleteNodeCarrierByOrgIdNodeIdAndServiceOption(any(), any(), any(), any());
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
    ProcessingLeadTimesRaw processingLeadTimesRaw = testUtil.getProcessingLeadTime("UPDATE");
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
    ProcessingLeadTimesRaw object = testUtil.getProcessingLeadTime("DELETE");
    object.setNodeId(null);

    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);

    Assertions.assertThrows(
        CommonServiceException.class, () -> nodeCarrierMapper.callApi(object, null));
  }

  @Test
  void callApiDeleteActionException2() {
    ProcessingLeadTimesRaw object = testUtil.getProcessingLeadTime("DELETE");
    object.setServiceOption(null);

    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);

    Assertions.assertThrows(
        CommonServiceException.class, () -> nodeCarrierMapper.callApi(object, null));
  }

  @Test
  void callApiDeleteActionException3() {
    ProcessingLeadTimesRaw object = testUtil.getProcessingLeadTime("DELETE");
    object.setOrgId(null);

    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);

    Assertions.assertThrows(
        CommonServiceException.class, () -> nodeCarrierMapper.callApi(object, null));
  }

  @Test
  @DisplayName("Create node carrier, Happy path")
  void callApiUploadNodeCarrierCreateAction()
      throws NodeCarrierMapperException, InvalidActionTypeException, CommonServiceException {
    Object object = testUtil.getNodeCarrierUpload("CREATE");
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_NODE_CARRIER);
    var expectedResponsePayload = testUtil.getNodeCarrierResponse();
    when(nodeCarrierFeign.createNodeCarrier(any()))
        .thenReturn(BaseResponse.builder().payload(expectedResponsePayload).build());
    ResponseEntity<BaseResponse<NodeCarrierResponse>> res =
        (ResponseEntity<BaseResponse<NodeCarrierResponse>>) nodeCarrierMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
    Assertions.assertEquals(
        expectedResponsePayload.getLastPickupTime(),
        res.getBody().getPayload().getLastPickupTime());
    verify(nodeCarrierFeign, times(1)).createNodeCarrier(any());
  }

  @Test
  @DisplayName("Update node carrier, Happy path")
  void callApiUploadNodeCarrierUpdateAction()
      throws NodeCarrierMapperException, InvalidActionTypeException, CommonServiceException {
    Object object = testUtil.getNodeCarrierUpload("UPDATE");
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_NODE_CARRIER);
    var expectedResponsePayload = testUtil.getNodeCarrierResponse();
    when(nodeCarrierFeign.updateNodeCarrier(
            anyString(), anyString(), anyString(), anyString(), any()))
        .thenReturn(BaseResponse.builder().payload(expectedResponsePayload).build());
    ResponseEntity<BaseResponse<NodeCarrierResponse>> res =
        (ResponseEntity<BaseResponse<NodeCarrierResponse>>) nodeCarrierMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
    Assertions.assertEquals(
        expectedResponsePayload.getLastPickupTime(),
        res.getBody().getPayload().getLastPickupTime());
    verify(nodeCarrierFeign, times(1)).updateNodeCarrier(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Delete node carrier: Happy path")
  void callApiUploadNodeCarrierDeleteAction()
      throws NodeCarrierMapperException, InvalidActionTypeException, CommonServiceException {
    Object object = testUtil.getNodeCarrierUpload("DELETE");
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_NODE_CARRIER);
    var expectedResponsePayload = testUtil.getNodeCarrierResponse();
    when(nodeCarrierFeign.deleteNodeCarrier(anyString(), anyString(), anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(expectedResponsePayload).build());
    ResponseEntity<BaseResponse<NodeCarrierResponse>> res =
        (ResponseEntity<BaseResponse<NodeCarrierResponse>>) nodeCarrierMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
    Assertions.assertEquals(
        expectedResponsePayload.getLastPickupTime(),
        res.getBody().getPayload().getLastPickupTime());
    verify(nodeCarrierFeign, times(1)).deleteNodeCarrier(any(), any(), any(), any());
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
  @DisplayName("Create processing time buffer Happy path")
  void callApiUploadProcessingTimeBufferUpload()
      throws NodeCarrierMapperException, InvalidActionTypeException, CommonServiceException {
    Object object = testUtil.getProcessingTimeBufferUpload("CREATE");
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_NODE_SERVICE_OPTION_BUFFER);
    var expectedResponsePayload = testUtil.getNodeCarrierBufferResponse();
    when(nodeCarrierFeign.createBuffer(any()))
        .thenReturn(BaseResponse.builder().payload(expectedResponsePayload).build());
    ResponseEntity<BaseResponse<NodeCarrierResponse>> res =
        (ResponseEntity<BaseResponse<NodeCarrierResponse>>) nodeCarrierMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
    Assertions.assertEquals(
        expectedResponsePayload.getBufferStartDate(),
        res.getBody().getPayload().getBufferStartDate());
    Assertions.assertEquals(
        expectedResponsePayload.getBufferEndDate(), res.getBody().getPayload().getBufferEndDate());
    verify(nodeCarrierFeign, times(1)).createBuffer(any());
  }

  @Test
  @DisplayName("Call API for delete processing time buffer")
  void callApiUploadProcessingTimeBufferDeleteUpload()
      throws NodeCarrierMapperException, InvalidActionTypeException, CommonServiceException {
    ProcessingTimeBufferUpload processingTimeBufferUpload =
        testUtil.getProcessingTimeBufferUpload("DELETE");
    processingTimeBufferUpload.setBufferHours("abc");
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_NODE_SERVICE_OPTION_BUFFER);
    var expectedResponsePayload = testUtil.getNodeServiceOptionBufferResponse();
    when(nodeCarrierFeign.deleteBuffer(any()))
        .thenReturn(BaseResponse.builder().payload(expectedResponsePayload).build());
    ResponseEntity<BaseResponse<NodeServiceOptionBufferResponse>> res =
        (ResponseEntity<BaseResponse<NodeServiceOptionBufferResponse>>)
            nodeCarrierMapper.callApi(processingTimeBufferUpload, null);
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res.getBody());
    Assertions.assertEquals(
        expectedResponsePayload.getBufferStartDate(),
        res.getBody().getPayload().getBufferStartDate());
    Assertions.assertEquals(
        expectedResponsePayload.getBufferEndDate(), res.getBody().getPayload().getBufferEndDate());
    verify(nodeCarrierFeign, times(1)).deleteBuffer(any());
  }

  @Test
  @DisplayName("Processing buffer invalid action")
  void callApiInvalidActionProcessingTimeBufferUpload() {
    ProcessingTimeBufferUpload processingTimeBufferUpload =
        testUtil.getProcessingTimeBufferUpload("INVALID");
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_NODE_SERVICE_OPTION_BUFFER);
    Exception e =
        Assertions.assertThrows(
            CsvDataValidationException.class,
            () -> nodeCarrierMapper.callApi(processingTimeBufferUpload, null));
    Assertions.assertNotNull(e.getMessage());
    Assertions.assertEquals("Please provide the valid action: INVALID", e.getMessage());
  }

  @Test
  @DisplayName("Create Processing buffer, invalid buffer hours")
  void callApiInvalidProcessingTimeBufferUpload() {
    ProcessingTimeBufferUpload processingTimeBufferUpload =
        testUtil.getProcessingTimeBufferUpload("CREATE");
    processingTimeBufferUpload.setBufferHours("TRUE");
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_NODE_SERVICE_OPTION_BUFFER);
    Exception e =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> nodeCarrierMapper.callApi(processingTimeBufferUpload, null));
    Assertions.assertNotNull(e.getMessage());
    Assertions.assertEquals("Invalid buffer hours", e.getMessage());
  }

  @Test
  void callUploadProcessingTimeBufferUploadForBlankDate() {
    Object object = testUtil.getBlankDateProcessingTimeBufferUpload();
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_NODE_SERVICE_OPTION_BUFFER);
    Exception e =
        Assertions.assertThrows(
            InvalidDateException.class, () -> nodeCarrierMapper.callApi(object, null));
    Assertions.assertNotNull(e.getMessage());
    Assertions.assertEquals("Buffer date is empty or null ", e.getMessage());
  }

  @Test
  void callUploadProcessingTimeBufferUploadForNullDate() {
    Object object = testUtil.getNullDateProcessingTimeBufferUpload();
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_NODE_SERVICE_OPTION_BUFFER);
    Exception e =
        Assertions.assertThrows(
            InvalidDateException.class, () -> nodeCarrierMapper.callApi(object, null));
    Assertions.assertNotNull(e.getMessage());
    Assertions.assertEquals("Buffer date is empty or null ", e.getMessage());
  }

  @Test
  void callUploadProcessingTimeBufferForInvalidDate() {
    Object object = testUtil.getInvalidProcessingTimeBufferUpload();
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_NODE_SERVICE_OPTION_BUFFER);
    Exception e =
        Assertions.assertThrows(
            InvalidDateException.class, () -> nodeCarrierMapper.callApi(object, null));
    Assertions.assertNotNull(e.getMessage());
  }

  @Test
  void callApiUpdateActionInvalidServiceOption() throws CommonServiceException {
    Object object = testUtil.getProcessingLeadTime("UPDATE");
    nodeCarrierMapper.setJobTypeEnum(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);
    when(tenantDatabaseConfig.fetchServiceOptions(any())).thenReturn("EXPRESS");
    CsvDataValidationException exception =
        Assertions.assertThrows(
            CsvDataValidationException.class,
            () -> {
              nodeCarrierMapper.callApi(object, null);
            });
    Assertions.assertTrue(exception.getMessage().startsWith("Invalid service option"));
  }
}
