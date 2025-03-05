/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.promise.sourcing.rule.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingConstraintRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingConstraintUpdationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingConstraintDetailsResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingConstraintsResponse;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingConstraintDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.service.GroupDefinitionPersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingConstraintPersistenceService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SourcingConstraintServiceTest {
  @InjectMocks private SourcingConstraintService sourcingConstraintService;

  @Mock private SourcingConstraintPersistenceService sourcingConstraintPersistenceService;
  @Mock private GroupDefinitionPersistenceService groupDefinitionPersistenceService;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void processAddSourcingConstraintTest() throws PromiseEngineException, CommonServiceException {
    SourcingConstraintRequest sourcingConstraintRequest = testUtil.getSourcingConstraintRequest();

    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupIdAndConstraint(
            anyString(), anyString(), any()))
        .thenReturn(List.of());

    when(sourcingConstraintPersistenceService.saveSourcingConstraintEntity(
            any(SourcingConstraintDomainDto.class)))
        .thenReturn(testUtil.getSourcingConstraintEntity());
    when(groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getGroupDefinitionEntity()));

    SourcingConstraintDetailsResponse sourcingConstraintDetailsResponse =
        sourcingConstraintService.processAddSourcingConstraint(sourcingConstraintRequest);
    assertEquals(
        testUtil.getSourcingRulesEntity().getId(), sourcingConstraintDetailsResponse.getId());
    assertEquals(
        testUtil.getSourcingRulesEntity().getCustomAttributes(),
        sourcingConstraintDetailsResponse.getCustomAttributes());
    verify(sourcingConstraintPersistenceService, times(1))
        .fetchByOrgIdAndGroupIdAndConstraint(anyString(), anyString(), any());
    verify(sourcingConstraintPersistenceService, times(1))
        .saveSourcingConstraintEntity(any(SourcingConstraintDomainDto.class));
    verify(groupDefinitionPersistenceService, times(1))
        .fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processAddSourcingConstraintExceptionTest1() throws PromiseEngineException {

    when(groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getGroupDefinitionEntity()));
    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupIdAndConstraint(
            anyString(), anyString(), any()))
        .thenReturn(List.of(testUtil.getSourcingConstraintEntity()));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingConstraintService.processAddSourcingConstraint(
                    testUtil.getSourcingConstraintRequest()));
    assertEquals("This constraint is already defined for given orgId", ex.getMessage());

    verify(sourcingConstraintPersistenceService, times(1))
        .fetchByOrgIdAndGroupIdAndConstraint(anyString(), anyString(), any());
  }

  @Test
  void processAddSourcingConstraintExceptionTest2() throws PromiseEngineException {

    SourcingConstraintRequest sourcingConstraintRequest = testUtil.getSourcingConstraintRequest();
    sourcingConstraintRequest.setSourcingConstraintValue("3");

    when(groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getGroupDefinitionEntity()));
    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupIdAndConstraint(
            anyString(), anyString(), any()))
        .thenReturn(List.of());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingConstraintService.processAddSourcingConstraint(sourcingConstraintRequest));
    assertEquals("Invalid constraint value", ex.getMessage());

    verify(sourcingConstraintPersistenceService, times(1))
        .fetchByOrgIdAndGroupIdAndConstraint(anyString(), anyString(), any());
  }

  @Test
  void processAddSourcingConstraintWhenGroupIdNotFoundExceptionTest()
      throws PromiseEngineException {

    SourcingConstraintRequest sourcingConstraintRequest = testUtil.getSourcingConstraintRequest();

    when(groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingConstraintService.processAddSourcingConstraint(sourcingConstraintRequest));
    assertEquals(
        "Group Definition details not found for the given groupId and orgId", ex.getMessage());

    verify(groupDefinitionPersistenceService, times(1))
        .fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processFetchSourcingConstraintsListTest1() throws PromiseEngineException {

    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupId(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getSourcingConstraintEntity()));

    SourcingConstraintsResponse sourcingConstraintsResponse =
        sourcingConstraintService.processFetchSourcingConstraintsList(
            TestUtil.ORG_ID, TestUtil.GROUP_ID);
    assertEquals(1, sourcingConstraintsResponse.getSourcingConstraintsInfo().size());

    verify(sourcingConstraintPersistenceService, times(1))
        .fetchByOrgIdAndGroupId(anyString(), anyString());
  }

  @Test
  void processFetchSourcingConstraintsListTest2() throws PromiseEngineException {

    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupId(anyString(), anyString()))
        .thenReturn(List.of());

    SourcingConstraintsResponse sourcingConstraintsResponse =
        sourcingConstraintService.processFetchSourcingConstraintsList(
            TestUtil.ORG_ID, TestUtil.GROUP_ID);
    assertEquals(0, sourcingConstraintsResponse.getSourcingConstraintsInfo().size());

    verify(sourcingConstraintPersistenceService, times(2))
        .fetchByOrgIdAndGroupId(anyString(), anyString());
  }

  @Test
  void processFetchSourcingConstraintsListTest3() throws PromiseEngineException {

    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupId(anyString(), anyString()))
        .thenReturn(List.of());
    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupId(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getSourcingConstraintEntity()));

    SourcingConstraintsResponse sourcingConstraintsResponse =
        sourcingConstraintService.processFetchSourcingConstraintsList(
            TestUtil.ORG_ID, TestUtil.GROUP_ID);
    assertEquals(1, sourcingConstraintsResponse.getSourcingConstraintsInfo().size());

    verify(sourcingConstraintPersistenceService, times(1))
        .fetchByOrgIdAndGroupId(anyString(), anyString());
  }

  @Test
  void processFetchSourcingConstraintByIdAndOrgIdTest()
      throws PromiseEngineException, CommonServiceException {

    SourcingConstraintDomainDto sourcingConstraintEntity = testUtil.getSourcingConstraintEntity();
    when(sourcingConstraintPersistenceService.getSourcingConstraintEntityByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(sourcingConstraintEntity));

    SourcingConstraintDetailsResponse sourcingConstraintDetailsResponse =
        sourcingConstraintService.processFetchSourcingConstraintsByIdAndOrgId(
            TestUtil.ID, TestUtil.ORG_ID);
    assertEquals(sourcingConstraintEntity.getId(), sourcingConstraintDetailsResponse.getId());

    verify(sourcingConstraintPersistenceService, times(1))
        .getSourcingConstraintEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processFetchSourcingConstraintByIdAndOrgIdExceptionTest() throws PromiseEngineException {

    when(sourcingConstraintPersistenceService.getSourcingConstraintEntityByIdAndOrgId(
            anyLong(), anyString()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingConstraintService.processFetchSourcingConstraintsByIdAndOrgId(
                    TestUtil.ID, TestUtil.ORG_ID));
    assertEquals("Sourcing Constraint not found", ex.getMessage());

    verify(sourcingConstraintPersistenceService, times(1))
        .getSourcingConstraintEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processUpdateSourcingConstraintTest() throws PromiseEngineException, CommonServiceException {

    SourcingConstraintUpdationRequest sourcingConstraintUpdationRequest =
        testUtil.getSourcingConstraintUpdationRequest();

    SourcingConstraintDomainDto updatedSourcingConstraintEntity =
        testUtil.getUpdatedSourcingConstraintEntity();
    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupIdAndConstraint(
            anyString(), anyString(), any()))
        .thenReturn(List.of(testUtil.getSourcingConstraintEntity()));

    when(sourcingConstraintPersistenceService.saveSourcingConstraintEntity(
            any(SourcingConstraintDomainDto.class)))
        .thenReturn(updatedSourcingConstraintEntity);
    when(groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getGroupDefinitionEntity()));

    SourcingConstraintDetailsResponse sourcingConstraintDetailsResponse =
        sourcingConstraintService.processUpdateSourcingConstraint(
            TestUtil.ORG_ID,
            TestUtil.SOURCING_CONSTRAINT,
            sourcingConstraintUpdationRequest,
            TestUtil.GROUP_ID);
    assertEquals(
        updatedSourcingConstraintEntity.getSourcingConstraintValue(),
        sourcingConstraintDetailsResponse.getSourcingConstraintValue());

    verify(sourcingConstraintPersistenceService, times(1))
        .fetchByOrgIdAndGroupIdAndConstraint(anyString(), anyString(), any());
    verify(sourcingConstraintPersistenceService, times(1))
        .saveSourcingConstraintEntity(any(SourcingConstraintDomainDto.class));
    verify(groupDefinitionPersistenceService, times(1))
        .fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processUpdateSourcingConstraintExceptionTest() throws PromiseEngineException {

    SourcingConstraintUpdationRequest sourcingConstraintUpdationRequest =
        testUtil.getSourcingConstraintUpdationRequest();
    when(groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getGroupDefinitionEntity()));
    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupIdAndConstraint(
            anyString(), anyString(), any()))
        .thenReturn(List.of());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingConstraintService.processUpdateSourcingConstraint(
                    TestUtil.ORG_ID,
                    TestUtil.SOURCING_CONSTRAINT,
                    sourcingConstraintUpdationRequest,
                    TestUtil.GROUP_ID));
    assertEquals("Sourcing Constraint not found", ex.getMessage());

    verify(sourcingConstraintPersistenceService, times(1))
        .fetchByOrgIdAndGroupIdAndConstraint(anyString(), anyString(), any());
  }

  @Test
  void processUpdateSourcingConstraintWhenGroupIdNotFoundExceptionTest()
      throws PromiseEngineException {

    SourcingConstraintUpdationRequest sourcingConstraintUpdationRequest =
        testUtil.getSourcingConstraintUpdationRequest();

    when(groupDefinitionPersistenceService.fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingConstraintService.processUpdateSourcingConstraint(
                    TestUtil.ORG_ID,
                    TestUtil.SOURCING_CONSTRAINT,
                    sourcingConstraintUpdationRequest,
                    TestUtil.GROUP_ID));
    assertEquals(
        "Group Definition details not found for the given groupId and orgId", ex.getMessage());

    verify(groupDefinitionPersistenceService, times(1))
        .fetchGroupDefinitionByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void processDeleteSourcingConstraintTest() throws PromiseEngineException, CommonServiceException {

    SourcingConstraintDomainDto sourcingConstraintEntity = testUtil.getSourcingConstraintEntity();
    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupIdAndConstraint(
            anyString(), anyString(), any()))
        .thenReturn(List.of(sourcingConstraintEntity));
    doNothing()
        .when(sourcingConstraintPersistenceService)
        .deleteSourcingConstraint(any(SourcingConstraintDomainDto.class));

    SourcingConstraintDetailsResponse sourcingConstraintDetailsResponse =
        sourcingConstraintService.processDeleteSourcingConstraintDetails(
            TestUtil.ORG_ID, TestUtil.SOURCING_CONSTRAINT, TestUtil.GROUP_ID);
    assertEquals(sourcingConstraintEntity.getId(), sourcingConstraintDetailsResponse.getId());

    verify(sourcingConstraintPersistenceService, times(1))
        .fetchByOrgIdAndGroupIdAndConstraint(anyString(), anyString(), any());
    verify(sourcingConstraintPersistenceService, times(1))
        .deleteSourcingConstraint(any(SourcingConstraintDomainDto.class));
  }

  @Test
  void processDeleteSourcingConstraintExceptionTest() throws PromiseEngineException {

    when(sourcingConstraintPersistenceService.fetchByOrgIdAndGroupIdAndConstraint(
            anyString(), anyString(), any()))
        .thenReturn(List.of());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingConstraintService.processDeleteSourcingConstraintDetails(
                    TestUtil.ORG_ID, TestUtil.SOURCING_CONSTRAINT, TestUtil.GROUP_ID));
    assertEquals("Sourcing Constraint not found", ex.getMessage());

    verify(sourcingConstraintPersistenceService, times(1))
        .fetchByOrgIdAndGroupIdAndConstraint(anyString(), anyString(), any());
  }
}
