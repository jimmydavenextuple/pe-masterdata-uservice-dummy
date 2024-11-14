/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.GroupDefinitionRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.GroupDefinitionListResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.GroupDefinitionResponse;
import com.nextuple.promise.sourcing.rule.service.GroupDefinitionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GroupDefinitionControllerTest {

  @Mock private GroupDefinitionService groupDefinitionService;
  @InjectMocks private GroupDefinitionController groupDefinitionController;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void addGroupDefinitionTest() throws PromiseEngineException, CommonServiceException {
    GroupDefinitionResponse groupDefinitionResponse = testUtil.getGroupDefinitionResponse();
    when(groupDefinitionService.processAddGroupDefinition(any(GroupDefinitionRequest.class)))
        .thenReturn(groupDefinitionResponse);

    ResponseEntity<BaseResponse<GroupDefinitionResponse>> responseEntity =
        groupDefinitionController.addGroupDefinition(testUtil.getGroupDefinitionRequest());

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(groupDefinitionResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(groupDefinitionService, times(1))
        .processAddGroupDefinition(any(GroupDefinitionRequest.class));
  }

  @Test
  void addGroupDefinitionExceptionTest() throws PromiseEngineException, CommonServiceException {
    when(groupDefinitionService.processAddGroupDefinition(any(GroupDefinitionRequest.class)))
        .thenThrow(new RuntimeException("Error in adding group definition"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              groupDefinitionController.addGroupDefinition(testUtil.getGroupDefinitionRequest());
            });
    assertEquals("Error in adding group definition", ex.getMessage());

    verify(groupDefinitionService, times(1))
        .processAddGroupDefinition(any(GroupDefinitionRequest.class));
  }

  @Test
  void getGroupDefinitionByIdTest() throws PromiseEngineException, CommonServiceException {
    GroupDefinitionResponse groupDefinitionResponse = testUtil.getGroupDefinitionResponse();
    when(groupDefinitionService.processGetGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(groupDefinitionResponse);

    ResponseEntity<BaseResponse<GroupDefinitionResponse>> responseEntity =
        groupDefinitionController.getGroupDefinitionByOrgIdAndId(TestUtil.ORG_ID, TestUtil.ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(groupDefinitionResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(groupDefinitionService, times(1))
        .processGetGroupDefinitionByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void getGroupDefinitionByIdExceptionTest() throws PromiseEngineException, CommonServiceException {
    when(groupDefinitionService.processGetGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenThrow(new RuntimeException("Error in fetching group definition by id"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              groupDefinitionController.getGroupDefinitionByOrgIdAndId(
                  TestUtil.ORG_ID, TestUtil.ID);
            });
    assertEquals("Error in fetching group definition by id", ex.getMessage());

    verify(groupDefinitionService, times(1))
        .processGetGroupDefinitionByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void getGroupDefinitionListTest() throws PromiseEngineException, CommonServiceException {
    when(groupDefinitionService
            .processGetGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId(
                anyString(), anyLong()))
        .thenReturn(testUtil.getGroupDefinitionListResponse());

    ResponseEntity<BaseResponse<GroupDefinitionListResponse>> responseEntity =
        groupDefinitionController.getGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId(
            TestUtil.ORG_ID, TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(1, responseEntity.getBody().getPayload().getGroupDefinitionInfoList().size());

    verify(groupDefinitionService, times(1))
        .processGetGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId(
            anyString(), anyLong());
  }

  @Test
  void getGroupDefinitionListExceptionTest() throws PromiseEngineException, CommonServiceException {
    when(groupDefinitionService
            .processGetGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId(
                anyString(), anyLong()))
        .thenThrow(new RuntimeException("Error in fetching group definition list"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              groupDefinitionController
                  .getGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId(
                      TestUtil.ORG_ID, TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID);
            });
    assertEquals("Error in fetching group definition list", ex.getMessage());

    verify(groupDefinitionService, times(1))
        .processGetGroupDefinitionListByOrgIdAndSourcingAttributesDefinitionId(
            anyString(), anyLong());
  }

  @Test
  void updateGroupDefinitionTest() throws PromiseEngineException, CommonServiceException {
    String updatedGroupName = "group2";
    GroupDefinitionRequest groupDefinitionRequest = testUtil.getGroupDefinitionRequest();
    groupDefinitionRequest.setGroupName(updatedGroupName);

    GroupDefinitionResponse groupDefinitionResponse = testUtil.getGroupDefinitionResponse();
    groupDefinitionResponse.setGroupName(updatedGroupName);

    when(groupDefinitionService.processUpdateGroupDefinition(any(GroupDefinitionRequest.class)))
        .thenReturn(groupDefinitionResponse);

    ResponseEntity<BaseResponse<GroupDefinitionResponse>> responseEntity =
        groupDefinitionController.updateGroupDefinition(testUtil.getGroupDefinitionRequest());

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(groupDefinitionResponse.getId(), responseEntity.getBody().getPayload().getId());
    assertEquals(updatedGroupName, responseEntity.getBody().getPayload().getGroupName());

    verify(groupDefinitionService, times(1))
        .processUpdateGroupDefinition(any(GroupDefinitionRequest.class));
  }

  @Test
  void updateGroupDefinitionExceptionTest() throws PromiseEngineException, CommonServiceException {
    String updatedGroupName = "group2";
    GroupDefinitionRequest groupDefinitionRequest = testUtil.getGroupDefinitionRequest();
    groupDefinitionRequest.setGroupName(updatedGroupName);

    when(groupDefinitionService.processUpdateGroupDefinition(any(GroupDefinitionRequest.class)))
        .thenThrow(new RuntimeException("Error in updating group definition"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              groupDefinitionController.updateGroupDefinition(testUtil.getGroupDefinitionRequest());
            });
    assertEquals("Error in updating group definition", ex.getMessage());

    verify(groupDefinitionService, times(1))
        .processUpdateGroupDefinition(any(GroupDefinitionRequest.class));
  }

  @Test
  void deleteGroupDefinitionTest() throws PromiseEngineException, CommonServiceException {
    GroupDefinitionResponse groupDefinitionResponse = testUtil.getGroupDefinitionResponse();
    when(groupDefinitionService.processDeleteGroupDefinition(anyLong(), anyString()))
        .thenReturn(groupDefinitionResponse);

    ResponseEntity<BaseResponse<GroupDefinitionResponse>> responseEntity =
        groupDefinitionController.deleteGroupDefinition(TestUtil.ORG_ID, TestUtil.ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(groupDefinitionResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(groupDefinitionService, times(1)).processDeleteGroupDefinition(anyLong(), anyString());
  }

  @Test
  void deleteGroupDefinitionExceptionTest() throws PromiseEngineException, CommonServiceException {
    when(groupDefinitionService.processDeleteGroupDefinition(anyLong(), anyString()))
        .thenThrow(new RuntimeException("Error in deleting group definition"));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              groupDefinitionController.deleteGroupDefinition(TestUtil.ORG_ID, TestUtil.ID);
            });
    assertEquals("Error in deleting group definition", ex.getMessage());

    verify(groupDefinitionService, times(1)).processDeleteGroupDefinition(anyLong(), anyString());
  }
}
