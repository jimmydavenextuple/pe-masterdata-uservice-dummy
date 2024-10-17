/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.exception.CsvDataValidationException;
import com.nextuple.dataupload.common.config.TenantDatabaseConfig;
import com.nextuple.jobs.consumers.common.TestUtil;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.NodeDataUpload;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.node.domain.feign.NodeFeign;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import org.junit.jupiter.api.Assertions;
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
class NodeMapperTest {

  @InjectMocks private NodeMapper nodeMapper;
  @InjectMocks private TestUtil testUtil;
  @Mock TenantDatabaseConfig tenantDatabaseConfig;
  @Mock private NodeFeign nodeFeign;

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(nodeMapper, "tenantDatabaseConfig", tenantDatabaseConfig);
  }

  @Test
  void mapTODto() {
    Class nodeDataUpload = nodeMapper.mapTODto();
    Assertions.assertEquals(NodeDataUpload.class, nodeDataUpload);
  }

  @Test
  void getModule() {
    ModuleEnum moduleEnum = nodeMapper.getModule();
    Assertions.assertEquals(ModuleEnum.NODES, moduleEnum);
  }

  @Test
  void setJobType() {
    Assertions.assertDoesNotThrow(() -> nodeMapper.setJobType(JobTypeEnum.UPLOAD_NODES));
  }

  @Test
  void getDTOFromCustomMapper() {
    Assertions.assertNull(nodeMapper.getDTOFromCustomMapper("request"));
  }

  @Test
  void getColumnNameMapping() {
    Assertions.assertNotNull(nodeMapper.getColumnNameMapping(new String[] {"request"}));
  }

  @Test
  void callApiCreateNode() throws CommonServiceException {
    when(tenantDatabaseConfig.getCurrentTenantServiceOptions(any()))
        .thenReturn(TestUtil.tenantServiceOptionExpected.toArray(new String[0]));

    Object object = testUtil.getNodeDataUpload("CREATE");
    when(nodeFeign.createNode(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getNodeResponse()).build());

    ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> response =
        (ResponseEntity<BaseResponse<PostalCodeTimezoneDto>>) nodeMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void callApiCreateNodeWithInvalidBoolean() throws CommonServiceException {
    when(tenantDatabaseConfig.getCurrentTenantServiceOptions(any()))
        .thenReturn(TestUtil.tenantServiceOptionExpected.toArray(new String[0]));

    Object object = testUtil.getNodeDataUploadWithInvalidBoolean("CREATE");

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class, () -> nodeMapper.callApi(object, null));

    Assertions.assertNotNull(exception);
  }

  @Test
  void callApiCreateNodeWithOrgIdNotPresent() {
    var object = (NodeDataUpload) testUtil.getNodeDataUploadWithInvalidBoolean("CREATE");
    object.setOrgId(null);
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class, () -> nodeMapper.callApi(object, null));

    Assertions.assertNotNull(exception);
    Assertions.assertEquals("OrgId not found in the request", exception.getMessage());
  }

  @Test
  void callApiUpdateNodeDetails() throws CommonServiceException {
    when(tenantDatabaseConfig.getCurrentTenantServiceOptions(any()))
        .thenReturn(TestUtil.tenantServiceOptionExpected.toArray(new String[0]));

    Object object = testUtil.getNodeDataUpload("UPDATE");
    when(nodeFeign.updateNodeDetails(any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getPostalCodeTimezoneDto()).build());

    ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> response =
        (ResponseEntity<BaseResponse<PostalCodeTimezoneDto>>) nodeMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void callApiUpdateNodeWithInvalidBooleanDetails() throws CommonServiceException {
    when(tenantDatabaseConfig.getCurrentTenantServiceOptions(any()))
        .thenReturn(TestUtil.tenantServiceOptionExpected.toArray(new String[0]));

    Object object = testUtil.getNodeDataUploadWithInvalidBoolean("UPDATE");

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class, () -> nodeMapper.callApi(object, null));

    Assertions.assertNotNull(exception);
  }

  @Test
  void callApiDeleteNode() throws CommonServiceException {
    when(tenantDatabaseConfig.getCurrentTenantServiceOptions(any()))
        .thenReturn(TestUtil.tenantServiceOptionExpected.toArray(new String[0]));

    Object object = testUtil.getNodeDataUpload("DELETE");
    when(nodeFeign.deleteNode(any(), any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getPostalCodeTimezoneDto()).build());

    ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> response =
        (ResponseEntity<BaseResponse<PostalCodeTimezoneDto>>) nodeMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void callApiInvalidAction() throws CommonServiceException {
    when(tenantDatabaseConfig.getCurrentTenantServiceOptions(any()))
        .thenReturn(TestUtil.tenantServiceOptionExpected.toArray(new String[0]));

    Object object = testUtil.getNodeDataUpload("DEL");

    Exception exception =
        Assertions.assertThrows(
            CsvDataValidationException.class, () -> nodeMapper.callApi(object, null));

    Assertions.assertNotNull(exception);
  }

  @Test
  void callApiWhenNoServiceOptionsAreConfiguredExceptionTest() throws CommonServiceException {
    when(tenantDatabaseConfig.getCurrentTenantServiceOptions(any()))
        .thenThrow(new CommonServiceException(HttpStatus.NOT_FOUND, 0x1776, null));

    Object object = testUtil.getNodeDataUpload("CREATE");

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class, () -> nodeMapper.callApi(object, null));

    Assertions.assertNotNull(exception);
  }
}
