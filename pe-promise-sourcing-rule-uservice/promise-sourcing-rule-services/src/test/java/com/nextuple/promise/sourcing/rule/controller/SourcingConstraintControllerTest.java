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
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingConstraintRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingConstraintUpdationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingConstraintDetailsResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingConstraintsResponse;
import com.nextuple.promise.sourcing.rule.service.SourcingConstraintService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class SourcingConstraintControllerTest {

  @Mock private SourcingConstraintService sourcingConstraintService;
  @InjectMocks private SourcingConstraintController sourcingConstraintController;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void addSourcingConstraintTest() throws PromiseEngineException, CommonServiceException {
    SourcingConstraintDetailsResponse sourcingConstraintResponse =
        testUtil.getSourcingConstraintDetails();
    when(sourcingConstraintService.processAddSourcingConstraint(
            any(SourcingConstraintRequest.class)))
        .thenReturn(sourcingConstraintResponse);

    ResponseEntity<BaseResponse<SourcingConstraintDetailsResponse>> responseEntity =
        sourcingConstraintController.addSourcingConstraint(testUtil.getSourcingConstraintRequest());

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(sourcingConstraintResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(sourcingConstraintService, times(1))
        .processAddSourcingConstraint(any(SourcingConstraintRequest.class));
  }

  @Test
  void addSourcingConstraintExceptionTest() throws PromiseEngineException, CommonServiceException {
    SourcingConstraintRequest sourcingConstraintRequest = testUtil.getSourcingConstraintRequest();
    when(sourcingConstraintService.processAddSourcingConstraint(
            any(SourcingConstraintRequest.class)))
        .thenThrow(new RuntimeException("Error in adding sourcing constraints"));

    Exception ex =
        assertThrows(
            RuntimeException.class,
            () -> sourcingConstraintController.addSourcingConstraint(sourcingConstraintRequest));

    assertEquals("Error in adding sourcing constraints", ex.getMessage());
    verify(sourcingConstraintService, times(1))
        .processAddSourcingConstraint(any(SourcingConstraintRequest.class));
  }

  @Test
  void fetchSourcingConstraintByOrgIdAndIdTest()
      throws PromiseEngineException, CommonServiceException {
    SourcingConstraintDetailsResponse sourcingConstraintResponse =
        testUtil.getSourcingConstraintDetails();
    when(sourcingConstraintService.processFetchSourcingConstraintsByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(sourcingConstraintResponse);

    ResponseEntity<BaseResponse<SourcingConstraintDetailsResponse>> responseEntity =
        sourcingConstraintController.fetchSourcingConstraintByOrgIdAndId(
            TestUtil.ORG_ID, TestUtil.ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(sourcingConstraintResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(sourcingConstraintService, times(1))
        .processFetchSourcingConstraintsByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void fetchSourcingConstraintByOrgIdAndIdExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(sourcingConstraintService.processFetchSourcingConstraintsByIdAndOrgId(
            anyLong(), anyString()))
        .thenThrow(new RuntimeException("Error in fetching sourcing constraint by Id"));

    Exception ex =
        assertThrows(
            RuntimeException.class,
            () ->
                sourcingConstraintController.fetchSourcingConstraintByOrgIdAndId(
                    TestUtil.ORG_ID, TestUtil.ID));

    assertEquals("Error in fetching sourcing constraint by Id", ex.getMessage());
    verify(sourcingConstraintService, times(1))
        .processFetchSourcingConstraintsByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void fetchSourcingConstraintsListTest() throws PromiseEngineException {
    SourcingConstraintsResponse sourcingConstraintResponse =
        testUtil.getSourcingConstraintsResponse();
    when(sourcingConstraintService.processFetchSourcingConstraintsList(anyString(), anyString()))
        .thenReturn(sourcingConstraintResponse);

    ResponseEntity<BaseResponse<SourcingConstraintsResponse>> responseEntity =
        sourcingConstraintController.fetchSourcingConstraintsList(
            TestUtil.ORG_ID, TestUtil.GROUP_ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        sourcingConstraintResponse.getSourcingConstraintsInfo(),
        responseEntity.getBody().getPayload().getSourcingConstraintsInfo());

    verify(sourcingConstraintService, times(1))
        .processFetchSourcingConstraintsList(anyString(), anyString());
  }

  @Test
  void fetchSourcingConstraintsListExceptionTest() throws PromiseEngineException {
    when(sourcingConstraintService.processFetchSourcingConstraintsList(anyString(), anyString()))
        .thenThrow(new RuntimeException("Error in fetching sourcing constraints list"));

    Exception ex =
        assertThrows(
            RuntimeException.class,
            () ->
                sourcingConstraintController.fetchSourcingConstraintsList(
                    TestUtil.ORG_ID, TestUtil.GROUP_ID));

    assertEquals("Error in fetching sourcing constraints list", ex.getMessage());
    verify(sourcingConstraintService, times(1))
        .processFetchSourcingConstraintsList(anyString(), anyString());
  }

  @Test
  void updateSourcingConstraintTest() throws PromiseEngineException, CommonServiceException {
    SourcingConstraintDetailsResponse sourcingConstraintResponse =
        testUtil.getUpdatedSourcingConstraintDetails();
    when(sourcingConstraintService.processUpdateSourcingConstraint(
            anyString(), any(), any(SourcingConstraintUpdationRequest.class), anyString()))
        .thenReturn(sourcingConstraintResponse);

    ResponseEntity<BaseResponse<SourcingConstraintDetailsResponse>> responseEntity =
        sourcingConstraintController.updateSourcingConstraint(
            TestUtil.ORG_ID,
            TestUtil.GROUP_ID,
            TestUtil.SOURCING_CONSTRAINT,
            testUtil.getSourcingConstraintUpdationRequest());

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(sourcingConstraintResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(sourcingConstraintService, times(1))
        .processUpdateSourcingConstraint(
            anyString(), any(), any(SourcingConstraintUpdationRequest.class), anyString());
  }

  @Test
  void updateSourcingConstraintExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    SourcingConstraintUpdationRequest sourcingConstraintUpdationRequest =
        testUtil.getSourcingConstraintUpdationRequest();
    when(sourcingConstraintService.processUpdateSourcingConstraint(
            anyString(), any(), any(SourcingConstraintUpdationRequest.class), anyString()))
        .thenThrow(new RuntimeException("Error in updating sourcing constraint"));

    Exception ex =
        assertThrows(
            RuntimeException.class,
            () ->
                sourcingConstraintController.updateSourcingConstraint(
                    TestUtil.ORG_ID,
                    TestUtil.GROUP_ID,
                    TestUtil.SOURCING_CONSTRAINT,
                    sourcingConstraintUpdationRequest));

    assertEquals("Error in updating sourcing constraint", ex.getMessage());

    verify(sourcingConstraintService, times(1))
        .processUpdateSourcingConstraint(
            anyString(), any(), any(SourcingConstraintUpdationRequest.class), anyString());
  }

  @Test
  void deleteSourcingConstraintTest() throws PromiseEngineException, CommonServiceException {
    SourcingConstraintDetailsResponse sourcingConstraintResponse =
        testUtil.getSourcingConstraintDetails();
    when(sourcingConstraintService.processDeleteSourcingConstraintDetails(
            anyString(), any(), anyString()))
        .thenReturn(sourcingConstraintResponse);

    ResponseEntity<BaseResponse<SourcingConstraintDetailsResponse>> responseEntity =
        sourcingConstraintController.deleteSourcingConstraint(
            TestUtil.ORG_ID, TestUtil.GROUP_ID, TestUtil.SOURCING_CONSTRAINT);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(sourcingConstraintResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(sourcingConstraintService, times(1))
        .processDeleteSourcingConstraintDetails(anyString(), any(), anyString());
  }

  @Test
  void deleteSourcingConstraintExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(sourcingConstraintService.processDeleteSourcingConstraintDetails(
            anyString(), any(), anyString()))
        .thenThrow(new RuntimeException("Error in deleting sourcing constraint"));

    Exception ex =
        assertThrows(
            RuntimeException.class,
            () ->
                sourcingConstraintController.deleteSourcingConstraint(
                    TestUtil.ORG_ID, TestUtil.GROUP_ID, TestUtil.SOURCING_CONSTRAINT));

    assertEquals("Error in deleting sourcing constraint", ex.getMessage());

    verify(sourcingConstraintService, times(1))
        .processDeleteSourcingConstraintDetails(anyString(), any(), anyString());
  }
}
