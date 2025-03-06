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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.promise.sourcing.rule.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionStatus;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingAttributesDefinitionRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingAttributesDefinitionUpdationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributeDefinitionUIResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributesDefinitionResponse;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributeDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributesDefinitionDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingAttributePersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingAttributesDefinitionPersistenceService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

class SourcingAttributesDefinitionServiceTest {
  @InjectMocks private SourcingAttributesDefinitionService sourcingAttributesDefinitionService;

  @Mock
  private SourcingAttributesDefinitionPersistenceService
      sourcingAttributesDefinitionPersistenceService;

  @Mock private SourcingAttributePersistenceService sourcingAttributePersistenceService;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createSourcingAttributesDefinitionTest1()
      throws PromiseEngineException, CommonServiceException {
    List<SourcingAttributeDomainDto> sourcingAttributeEntityList = new ArrayList<>();
    SourcingAttributeDomainDto sourcingAttributeEntity = testUtil.getSourcingAttributeEntity();
    SourcingAttributeDomainDto sourcingAttributeEntity1 = testUtil.getSourcingAttributeEntity();
    sourcingAttributeEntity1.setId(2L);
    sourcingAttributeEntityList.add(sourcingAttributeEntity);
    sourcingAttributeEntityList.add(sourcingAttributeEntity1);
    SourcingAttributesDefinitionRequest sourcingRuleAttributesDefinitionRequest =
        testUtil.getSourcingRuleAttributesDefinitionRequest(
            SourcingAttributesDefinitionStatus.ACTIVE);
    when(sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndName(anyString(), anyString()))
        .thenReturn(List.of());
    when(sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
                anyString(), any(), any()))
        .thenReturn(List.of());
    when(sourcingAttributePersistenceService.getSourcingAttributeListByOrgId(anyString()))
        .thenReturn(sourcingAttributeEntityList);
    when(sourcingAttributesDefinitionPersistenceService.saveSourcingRuleAttributesDefinitionEntity(
            any(SourcingAttributesDefinitionDomainDto.class)))
        .thenReturn(
            testUtil.getSourcingRuleAttributesDefinitionEntity(
                SourcingAttributesDefinitionStatus.ACTIVE));

    SourcingAttributesDefinitionResponse sourcingRuleAttributesDefinitionResponse =
        sourcingAttributesDefinitionService.processCreateSourcingAttributesDefinition(
            sourcingRuleAttributesDefinitionRequest);
    assertEquals(
        testUtil.getSourcingAttributeEntity().getId(),
        sourcingRuleAttributesDefinitionResponse.getId());
    assertEquals(
        testUtil.getSourcingAttributeEntity().getCustomAttributes(),
        sourcingRuleAttributesDefinitionResponse.getCustomAttributes());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
            anyString(), any(), any());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .fetchSourcingRuleAttributesDefinitionListByOrgIdAndName(anyString(), anyString());
    verify(sourcingAttributePersistenceService, times(1))
        .getSourcingAttributeListByOrgId(anyString());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .saveSourcingRuleAttributesDefinitionEntity(
            any(SourcingAttributesDefinitionDomainDto.class));
  }

  @Test
  void createSourcingAttributesDefinitionTest2()
      throws PromiseEngineException, CommonServiceException {
    List<SourcingAttributeDomainDto> sourcingAttributeEntityList = new ArrayList<>();
    SourcingAttributeDomainDto sourcingAttributeEntity = testUtil.getSourcingAttributeEntity();
    SourcingAttributeDomainDto sourcingAttributeEntity1 = testUtil.getSourcingAttributeEntity();
    sourcingAttributeEntity1.setId(2L);
    sourcingAttributeEntityList.add(sourcingAttributeEntity);
    sourcingAttributeEntityList.add(sourcingAttributeEntity1);
    SourcingAttributesDefinitionRequest sourcingRuleAttributesDefinitionRequest =
        testUtil.getSourcingRuleAttributesDefinitionRequest(
            SourcingAttributesDefinitionStatus.INACTIVE);
    when(sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndName(anyString(), anyString()))
        .thenReturn(List.of());
    when(sourcingAttributePersistenceService.getSourcingAttributeListByOrgId(anyString()))
        .thenReturn(sourcingAttributeEntityList);
    when(sourcingAttributesDefinitionPersistenceService.saveSourcingRuleAttributesDefinitionEntity(
            any(SourcingAttributesDefinitionDomainDto.class)))
        .thenReturn(
            testUtil.getSourcingRuleAttributesDefinitionEntity(
                SourcingAttributesDefinitionStatus.INACTIVE));

    SourcingAttributesDefinitionResponse sourcingRuleAttributesDefinitionResponse =
        sourcingAttributesDefinitionService.processCreateSourcingAttributesDefinition(
            sourcingRuleAttributesDefinitionRequest);
    assertEquals(
        testUtil.getSourcingAttributeEntity().getId(),
        sourcingRuleAttributesDefinitionResponse.getId());
    assertEquals(
        testUtil.getSourcingAttributeEntity().getCustomAttributes(),
        sourcingRuleAttributesDefinitionResponse.getCustomAttributes());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .fetchSourcingRuleAttributesDefinitionListByOrgIdAndName(anyString(), anyString());
    verify(sourcingAttributePersistenceService, times(1))
        .getSourcingAttributeListByOrgId(anyString());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .saveSourcingRuleAttributesDefinitionEntity(
            any(SourcingAttributesDefinitionDomainDto.class));
  }

  @Test
  void createSourcingAttributesDefinitionTest3()
      throws PromiseEngineException, CommonServiceException {
    List<SourcingAttributeDomainDto> sourcingAttributeEntityList = new ArrayList<>();
    SourcingAttributeDomainDto sourcingAttributeEntity = testUtil.getSourcingAttributeEntity();
    SourcingAttributeDomainDto sourcingAttributeEntity1 = testUtil.getSourcingAttributeEntity();
    sourcingAttributeEntity1.setId(2L);
    SourcingAttributeDomainDto sourcingAttributeEntity2 = testUtil.getSourcingAttributeEntity();
    sourcingAttributeEntity2.setId(3L);
    sourcingAttributeEntityList.add(sourcingAttributeEntity);
    sourcingAttributeEntityList.add(sourcingAttributeEntity1);
    sourcingAttributeEntityList.add(sourcingAttributeEntity2);
    SourcingAttributesDefinitionRequest sourcingRuleAttributesDefinitionRequest =
        testUtil.getSourcingRuleAttributesDefinitionRequest(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingRuleAttributesDefinitionRequest.setOptAttributes(TestUtil.OPTIONAL_ATTRIBUTES);
    when(sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndName(anyString(), anyString()))
        .thenReturn(List.of());
    when(sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
                anyString(), any(), any()))
        .thenReturn(List.of());
    when(sourcingAttributePersistenceService.getSourcingAttributeListByOrgId(anyString()))
        .thenReturn(sourcingAttributeEntityList);
    when(sourcingAttributesDefinitionPersistenceService.saveSourcingRuleAttributesDefinitionEntity(
            any(SourcingAttributesDefinitionDomainDto.class)))
        .thenReturn(
            testUtil.getSourcingRuleAttributesDefinitionEntity(
                SourcingAttributesDefinitionStatus.ACTIVE));

    SourcingAttributesDefinitionResponse sourcingRuleAttributesDefinitionResponse =
        sourcingAttributesDefinitionService.processCreateSourcingAttributesDefinition(
            sourcingRuleAttributesDefinitionRequest);
    assertEquals(
        testUtil.getSourcingAttributeEntity().getId(),
        sourcingRuleAttributesDefinitionResponse.getId());
    assertEquals(
        testUtil.getSourcingAttributeEntity().getCustomAttributes(),
        sourcingRuleAttributesDefinitionResponse.getCustomAttributes());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
            anyString(), any(), any());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .fetchSourcingRuleAttributesDefinitionListByOrgIdAndName(anyString(), anyString());
    verify(sourcingAttributePersistenceService, times(1))
        .getSourcingAttributeListByOrgId(anyString());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .saveSourcingRuleAttributesDefinitionEntity(
            any(SourcingAttributesDefinitionDomainDto.class));
  }

  @Test
  void createSourcingAttributesDefinitionExceptionTest1() throws PromiseEngineException {
    SourcingAttributesDefinitionRequest sourcingRuleAttributesDefinitionRequest =
        testUtil.getSourcingRuleAttributesDefinitionRequest(
            SourcingAttributesDefinitionStatus.ACTIVE);
    when(sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndName(anyString(), anyString()))
        .thenReturn(List.of());
    when(sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
                anyString(), any(), any()))
        .thenReturn(List.of());
    when(sourcingAttributePersistenceService.getSourcingAttributeListByOrgId(anyString()))
        .thenReturn(List.of(testUtil.getSourcingAttributeEntity()));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingAttributesDefinitionService.processCreateSourcingAttributesDefinition(
                    sourcingRuleAttributesDefinitionRequest));
    assertEquals("Attribute not found with given attribute id", ex.getMessage());

    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
            anyString(), any(), any());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .fetchSourcingRuleAttributesDefinitionListByOrgIdAndName(anyString(), anyString());
    verify(sourcingAttributePersistenceService, times(1))
        .getSourcingAttributeListByOrgId(anyString());
  }

  @Test
  void createSourcingAttributesDefinitionExceptionTest2() throws PromiseEngineException {
    SourcingAttributesDefinitionRequest sourcingRuleAttributesDefinitionRequest =
        testUtil.getSourcingRuleAttributesDefinitionRequest(
            SourcingAttributesDefinitionStatus.ACTIVE);
    when(sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndName(anyString(), anyString()))
        .thenReturn(
            List.of(
                testUtil.getSourcingRuleAttributesDefinitionEntity(
                    SourcingAttributesDefinitionStatus.ACTIVE)));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingAttributesDefinitionService.processCreateSourcingAttributesDefinition(
                    sourcingRuleAttributesDefinitionRequest));
    assertEquals("Combination of orgId and name should be unique", ex.getMessage());

    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .fetchSourcingRuleAttributesDefinitionListByOrgIdAndName(anyString(), anyString());
  }

  @Test
  void createSourcingAttributesDefinitionExceptionTest3() throws PromiseEngineException {
    SourcingAttributesDefinitionRequest sourcingRuleAttributesDefinitionRequest =
        testUtil.getSourcingRuleAttributesDefinitionRequest(
            SourcingAttributesDefinitionStatus.ACTIVE);
    when(sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndName(anyString(), anyString()))
        .thenReturn(List.of());
    when(sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
                anyString(), any(), any()))
        .thenReturn(
            List.of(
                testUtil.getSourcingRuleAttributesDefinitionEntity(
                    SourcingAttributesDefinitionStatus.ACTIVE)));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingAttributesDefinitionService.processCreateSourcingAttributesDefinition(
                    sourcingRuleAttributesDefinitionRequest));
    assertEquals(
        "A sourcing attributes definition is already active for given orgId and scope",
        ex.getMessage());

    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .fetchSourcingRuleAttributesDefinitionListByOrgIdAndName(anyString(), anyString());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
            anyString(), any(), any());
  }

  @Test
  void createSourcingAttributesDefinitionExceptionTest4() throws PromiseEngineException {
    SourcingAttributesDefinitionRequest sourcingRuleAttributesDefinitionRequest =
        testUtil.getSourcingRuleAttributesDefinitionRequest(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingRuleAttributesDefinitionRequest.setReqAttributes("abc");
    when(sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndName(anyString(), anyString()))
        .thenReturn(List.of());
    when(sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
                anyString(), any(), any()))
        .thenReturn(List.of());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                sourcingAttributesDefinitionService.processCreateSourcingAttributesDefinition(
                    sourcingRuleAttributesDefinitionRequest));
    assertEquals("Attribute can have only numeric values", ex.getMessage());

    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .fetchSourcingRuleAttributesDefinitionListByOrgIdAndName(anyString(), anyString());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
            anyString(), any(), any());
  }

  @Test
  void createSourcingAttributesDefinitionExceptionTestWithSameReqAndOptAttributes()
      throws PromiseEngineException, CommonServiceException {
    List<SourcingAttributeDomainDto> sourcingAttributeEntityList = new ArrayList<>();
    SourcingAttributeDomainDto sourcingAttributeEntity = testUtil.getSourcingAttributeEntity();
    SourcingAttributeDomainDto sourcingAttributeEntity1 = testUtil.getSourcingAttributeEntity();
    sourcingAttributeEntity1.setId(2L);
    sourcingAttributeEntityList.add(sourcingAttributeEntity);
    sourcingAttributeEntityList.add(sourcingAttributeEntity1);

    SourcingAttributesDefinitionRequest sourcingRuleAttributesDefinitionRequest =
        testUtil.getSourcingRuleAttributesDefinitionRequest(
            SourcingAttributesDefinitionStatus.ACTIVE);

    sourcingRuleAttributesDefinitionRequest.setOptAttributes(TestUtil.REQUIRED_ATTRIBUTES);

    when(sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndName(anyString(), anyString()))
        .thenReturn(List.of());
    when(sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
                anyString(), any(), any()))
        .thenReturn(List.of());
    when(sourcingAttributePersistenceService.getSourcingAttributeListByOrgId(anyString()))
        .thenReturn(sourcingAttributeEntityList);
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              sourcingAttributesDefinitionService.processCreateSourcingAttributesDefinition(
                  sourcingRuleAttributesDefinitionRequest);
            });
    assertEquals("Required Attributes and Optional Attributes can't be the same", ex.getMessage());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .fetchSourcingRuleAttributesDefinitionListByOrgIdAndName(anyString(), anyString());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
            anyString(), any(), any());
  }

  @Test
  void getSourcingAttributesDefinitionByIdandOrgIdTest()
      throws PromiseEngineException, CommonServiceException {
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(
            Optional.ofNullable(
                testUtil.getSourcingRuleAttributesDefinitionEntity(
                    SourcingAttributesDefinitionStatus.ACTIVE)));

    SourcingAttributesDefinitionResponse sourcingAttributeResponse =
        sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
            TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID, TestUtil.ORG_ID);
    assertEquals(testUtil.getSourcingAttributeEntity().getId(), sourcingAttributeResponse.getId());
    assertEquals(
        testUtil.getSourcingAttributeEntity().getCustomAttributes(),
        sourcingAttributeResponse.getCustomAttributes());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void getSourcingAttributesDefinitionByIdandOrgIdExceptionTest() throws PromiseEngineException {
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());
    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              sourcingAttributesDefinitionService
                  .processGetSourcingAttributesDefinitionByIdandOrgId(
                      TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID, TestUtil.ORG_ID);
            });

    assertEquals("Sourcing attributes definition not found", ex.getMessage());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void getSourcingAttributesDefinitionInActiveStatusTest()
      throws PromiseEngineException, CommonServiceException {
    when(sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
                anyString(), any(), any()))
        .thenReturn(
            List.of(
                testUtil.getSourcingRuleAttributesDefinitionEntity(
                    SourcingAttributesDefinitionStatus.ACTIVE)));

    SourcingAttributesDefinitionResponse sourcingAttributeResponse =
        sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionInActiveStatus(
            TestUtil.ORG_ID, SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);
    assertEquals(
        testUtil
            .getSourcingRuleAttributesDefinitionEntity(SourcingAttributesDefinitionStatus.ACTIVE)
            .getId(),
        sourcingAttributeResponse.getId());
    assertEquals(
        testUtil.getSourcingAttributeEntity().getCustomAttributes(),
        sourcingAttributeResponse.getCustomAttributes());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
            anyString(), any(), any());
  }

  @Test
  void getSourcingAttributesDefinitionInActiveStatusExceptionTest() throws PromiseEngineException {
    when(sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
                anyString(), any(), any()))
        .thenReturn(List.of());
    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              sourcingAttributesDefinitionService
                  .processGetSourcingAttributesDefinitionInActiveStatus(
                      TestUtil.ORG_ID, SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);
            });

    assertEquals(
        "No active sourcing rule attributes definition exists for given orgId and scope",
        ex.getMessage());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
            anyString(), any(), any());
  }

  @Test
  void getSourcingAttributesDefinitionInActiveStatusExceptionTestScopeOptimization()
      throws PromiseEngineException, CommonServiceException {
    when(sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
                anyString(), any(), any()))
        .thenReturn(List.of());
    SourcingAttributesDefinitionResponse sourcingAttributeResponse =
        sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionInActiveStatus(
            TestUtil.ORG_ID, SourcingAttributesDefinitionScopeEnum.OPTIMIZATION);
    Assertions.assertNull(sourcingAttributeResponse);
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
            anyString(), any(), any());
  }

  @Test
  void updateSourcingAttributesDefinitionTest1()
      throws PromiseEngineException, CommonServiceException {
    SourcingAttributesDefinitionUpdationRequest sourcingRuleAttributesDefinitionUpdationRequest =
        new SourcingAttributesDefinitionUpdationRequest(
            null, null, null, SourcingAttributesDefinitionStatus.ACTIVE, null);
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(
            Optional.ofNullable(
                testUtil.getSourcingRuleAttributesDefinitionEntity(
                    SourcingAttributesDefinitionStatus.INACTIVE)));
    when(sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
                anyString(), any(), any()))
        .thenReturn(List.of());
    SourcingAttributesDefinitionDomainDto sourcingRuleAttributesDefinitionEntity =
        testUtil.getUpdatedSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    when(sourcingAttributesDefinitionPersistenceService.saveSourcingRuleAttributesDefinitionEntity(
            any(SourcingAttributesDefinitionDomainDto.class)))
        .thenReturn(sourcingRuleAttributesDefinitionEntity);

    SourcingAttributesDefinitionResponse sourcingRuleAttributesDefinitionResponse =
        sourcingAttributesDefinitionService.updateSourcingAttributesDefinition(
            TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID,
            TestUtil.ORG_ID,
            sourcingRuleAttributesDefinitionUpdationRequest);
    assertEquals(
        testUtil
            .getSourcingRuleAttributesDefinitionEntity(SourcingAttributesDefinitionStatus.INACTIVE)
            .getId(),
        sourcingRuleAttributesDefinitionResponse.getId());

    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
            anyString(), any(), any());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .saveSourcingRuleAttributesDefinitionEntity(
            any(SourcingAttributesDefinitionDomainDto.class));
  }

  @Test
  void updateSourcingAttributesDefinitionTest2()
      throws PromiseEngineException, CommonServiceException {
    List<SourcingAttributeDomainDto> sourcingAttributeEntityList = new ArrayList<>();
    SourcingAttributeDomainDto sourcingAttributeEntity = testUtil.getSourcingAttributeEntity();
    sourcingAttributeEntity.setId(3L);
    sourcingAttributeEntityList.add(sourcingAttributeEntity);
    SourcingAttributesDefinitionUpdationRequest sourcingRuleAttributesDefinitionUpdationRequest =
        testUtil.getSourcingRuleAttributesDefinitionUpdationRequest(
            SourcingAttributesDefinitionStatus.DRAFT);
    sourcingRuleAttributesDefinitionUpdationRequest.setName("def2");
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(
            Optional.ofNullable(
                testUtil.getSourcingRuleAttributesDefinitionEntity(
                    SourcingAttributesDefinitionStatus.DRAFT)));
    when(sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndName(anyString(), anyString()))
        .thenReturn(List.of());
    when(sourcingAttributePersistenceService.getSourcingAttributeListByOrgId(anyString()))
        .thenReturn(sourcingAttributeEntityList);
    SourcingAttributesDefinitionDomainDto sourcingRuleAttributesDefinitionEntity =
        testUtil.getUpdatedSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.DRAFT);
    when(sourcingAttributesDefinitionPersistenceService.saveSourcingRuleAttributesDefinitionEntity(
            any(SourcingAttributesDefinitionDomainDto.class)))
        .thenReturn(sourcingRuleAttributesDefinitionEntity);

    SourcingAttributesDefinitionResponse sourcingRuleAttributesDefinitionResponse =
        sourcingAttributesDefinitionService.updateSourcingAttributesDefinition(
            TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID,
            TestUtil.ORG_ID,
            sourcingRuleAttributesDefinitionUpdationRequest);
    assertEquals(
        testUtil.getSourcingAttributeEntity().getId(),
        sourcingRuleAttributesDefinitionResponse.getId());

    verify(sourcingAttributePersistenceService, times(1))
        .getSourcingAttributeListByOrgId(anyString());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .saveSourcingRuleAttributesDefinitionEntity(
            any(SourcingAttributesDefinitionDomainDto.class));
  }

  @Test
  void updateSourcingAttributesDefinitionTest3() throws PromiseEngineException {
    SourcingAttributesDefinitionUpdationRequest sourcingRuleAttributesDefinitionUpdationRequest =
        new SourcingAttributesDefinitionUpdationRequest(
            null, null, null, SourcingAttributesDefinitionStatus.DRAFT, null);
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(
            Optional.ofNullable(
                testUtil.getSourcingRuleAttributesDefinitionEntity(
                    SourcingAttributesDefinitionStatus.INACTIVE)));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              sourcingAttributesDefinitionService.updateSourcingAttributesDefinition(
                  TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID,
                  TestUtil.ORG_ID,
                  sourcingRuleAttributesDefinitionUpdationRequest);
            });

    assertEquals(
        "The given attributes definition can only be activated/deactivated , can't be changed to draft status",
        ex.getMessage());
  }

  @Test
  void updateSourcingAttributesDefinitionTest4() throws PromiseEngineException {
    SourcingAttributesDefinitionUpdationRequest sourcingRuleAttributesDefinitionUpdationRequest =
        new SourcingAttributesDefinitionUpdationRequest(
            "def3", null, null, SourcingAttributesDefinitionStatus.DRAFT, null);
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(
            Optional.ofNullable(
                testUtil.getSourcingRuleAttributesDefinitionEntity(
                    SourcingAttributesDefinitionStatus.INACTIVE)));

    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              sourcingAttributesDefinitionService.updateSourcingAttributesDefinition(
                  TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID,
                  TestUtil.ORG_ID,
                  sourcingRuleAttributesDefinitionUpdationRequest);
            });

    assertEquals(
        "Can't update the attribute definition details as it's in ACTIVE/INACTIVE statue.Only the status field can be modified!",
        ex.getMessage());
  }

  @Test
  void updateSourcingAttributesDefinitionTest5()
      throws PromiseEngineException, CommonServiceException {
    List<SourcingAttributeDomainDto> sourcingAttributeEntityList = new ArrayList<>();
    SourcingAttributeDomainDto sourcingAttributeEntity = testUtil.getSourcingAttributeEntity();
    sourcingAttributeEntity.setId(3L);
    sourcingAttributeEntityList.add(sourcingAttributeEntity);
    SourcingAttributesDefinitionUpdationRequest sourcingRuleAttributesDefinitionUpdationRequest =
        testUtil.getSourcingRuleAttributesDefinitionUpdationRequest(
            SourcingAttributesDefinitionStatus.DRAFT);
    sourcingRuleAttributesDefinitionUpdationRequest.setName("");
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(
            Optional.ofNullable(
                testUtil.getSourcingRuleAttributesDefinitionEntity(
                    SourcingAttributesDefinitionStatus.DRAFT)));
    when(sourcingAttributePersistenceService.getSourcingAttributeListByOrgId(anyString()))
        .thenReturn(sourcingAttributeEntityList);
    SourcingAttributesDefinitionDomainDto sourcingRuleAttributesDefinitionEntity =
        testUtil.getUpdatedSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.DRAFT);
    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              sourcingAttributesDefinitionService.updateSourcingAttributesDefinition(
                  TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID,
                  TestUtil.ORG_ID,
                  sourcingRuleAttributesDefinitionUpdationRequest);
            });
    assertEquals("Can't assign a  blank value to name / reqAttributes field", ex.getMessage());

    verify(sourcingAttributePersistenceService, times(1))
        .getSourcingAttributeListByOrgId(anyString());
  }

  @Test
  void updateSourcingAttributesDefinitionExceptionTest() throws PromiseEngineException {
    SourcingAttributesDefinitionUpdationRequest sourcingRuleAttributesDefinitionUpdationRequest =
        testUtil.getSourcingRuleAttributesDefinitionUpdationRequest(
            SourcingAttributesDefinitionStatus.DRAFT);
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());
    Exception ex =
        assertThrows(
            Exception.class,
            () -> {
              sourcingAttributesDefinitionService.updateSourcingAttributesDefinition(
                  TestUtil.SOURCING_ATTRIBUTES_DEFINITION_ID,
                  TestUtil.ORG_ID,
                  sourcingRuleAttributesDefinitionUpdationRequest);
            });

    assertEquals("Sourcing attributes definition not found", ex.getMessage());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void updateSourcingAttributesPreExistingNameCheck()
      throws PromiseEngineException, CommonServiceException {
    SourcingAttributesDefinitionUpdationRequest sourcingRuleAttributesDefinitionUpdationRequest =
        new SourcingAttributesDefinitionUpdationRequest(
            "definition1", "1", null, SourcingAttributesDefinitionStatus.DRAFT, null);
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(
            Optional.of(
                testUtil.getSourcingRuleAttributesDefinitionEntity(
                    SourcingAttributesDefinitionStatus.DRAFT)));
    when(sourcingAttributePersistenceService.getSourcingAttributeListByOrgId(anyString()))
        .thenReturn(Arrays.asList(testUtil.getSourcingAttributeEntity()));
    when(sourcingAttributesDefinitionPersistenceService.saveSourcingRuleAttributesDefinitionEntity(
            any(SourcingAttributesDefinitionDomainDto.class)))
        .thenReturn(
            testUtil.getSourcingRuleAttributesDefinitionEntity(
                SourcingAttributesDefinitionStatus.DRAFT));
    SourcingAttributesDefinitionResponse sourcingAttributesDefinitionResponse =
        sourcingAttributesDefinitionService.updateSourcingAttributesDefinition(
            anyLong(), anyString(), sourcingRuleAttributesDefinitionUpdationRequest);
    assertEquals(
        sourcingAttributesDefinitionResponse.getName(),
        sourcingRuleAttributesDefinitionUpdationRequest.getName());
    verify(sourcingAttributesDefinitionPersistenceService, times(0))
        .fetchSourcingRuleAttributesDefinitionListByOrgIdAndName(anyString(), anyString());
  }

  @Test
  void processGetActiveSourcingAttributeDefinitionForUI()
      throws PromiseEngineException, CommonServiceException {
    when(sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(any(), any(), any()))
        .thenReturn(
            List.of(
                testUtil.getSourcingRuleAttributesDefinitionEntity(
                    SourcingAttributesDefinitionStatus.ACTIVE)));
    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));
    SourcingAttributeDefinitionUIResponse response =
        sourcingAttributesDefinitionService.processGetActiveSourcingAttributeDefinitionForUI(
            TestUtil.ORG_ID, SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);
    Assertions.assertNotNull(response);
    Assertions.assertEquals(TestUtil.ORG_ID, response.getOrgId());
    Assertions.assertEquals(2, response.getRequiredAttributes().size());
    Assertions.assertEquals(0, response.getOptionalAttributes().size());
  }

  @Test
  void processGetActiveSourcingAttributeDefinitionForUIScopeIsOPTIMIZATION()
      throws PromiseEngineException, CommonServiceException {
    when(sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(any(), any(), any()))
        .thenReturn(
            List.of(
                testUtil.getSourcingRuleAttributesDefinitionEntity2(
                    SourcingAttributesDefinitionStatus.ACTIVE)));
    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));
    SourcingAttributeDefinitionUIResponse response =
        sourcingAttributesDefinitionService.processGetActiveSourcingAttributeDefinitionForUI(
            TestUtil.ORG_ID, SourcingAttributesDefinitionScopeEnum.OPTIMIZATION);

    Assertions.assertNotNull(response);
    Assertions.assertEquals(TestUtil.ORG_ID, response.getOrgId());
    Assertions.assertEquals(2, response.getRequiredAttributes().size());
    Assertions.assertEquals(0, response.getOptionalAttributes().size());
  }

  @Test
  void processGetActiveSourcingAttributeDefinitionForUIScopeIsNull()
      throws PromiseEngineException, CommonServiceException {
    when(sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(any(), any(), any()))
        .thenReturn(
            List.of(
                testUtil.getSourcingRuleAttributesDefinitionEntity(
                    SourcingAttributesDefinitionStatus.ACTIVE)));
    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.of(testUtil.getSourcingAttributeEntity()));
    SourcingAttributeDefinitionUIResponse response =
        sourcingAttributesDefinitionService.processGetActiveSourcingAttributeDefinitionForUI(
            TestUtil.ORG_ID, null);
    Assertions.assertNotNull(response);
    Assertions.assertEquals(TestUtil.ORG_ID, response.getOrgId());
    Assertions.assertEquals(2, response.getRequiredAttributes().size());
    Assertions.assertEquals(0, response.getOptionalAttributes().size());
  }

  @Test
  void processGetActiveSourcingAttributeDefinitionForUITest2() throws PromiseEngineException {
    when(sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(any(), any(), any()))
        .thenReturn(new ArrayList<>());
    CommonServiceException e =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              sourcingAttributesDefinitionService.processGetActiveSourcingAttributeDefinitionForUI(
                  TestUtil.ORG_ID, SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);
            });
    Assertions.assertEquals(
        "No active attributes definition exists for given orgId and SOURCING_RULE", e.getMessage());
    Assertions.assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
  }

  @Test
  void processGetActiveSourcingAttributeDefinitionForUITest3()
      throws PromiseEngineException, CommonServiceException {
    when(sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(any(), any(), any()))
        .thenReturn(
            List.of(
                testUtil.getSourcingRuleAttributesDefinitionEntity(
                    SourcingAttributesDefinitionStatus.ACTIVE)));
    when(sourcingAttributePersistenceService.getSourcingAttributeById(anyLong()))
        .thenReturn(Optional.empty(), Optional.of(testUtil.getSourcingAttributeEntity()));
    CommonServiceException e =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              sourcingAttributesDefinitionService.processGetActiveSourcingAttributeDefinitionForUI(
                  TestUtil.ORG_ID, SourcingAttributesDefinitionScopeEnum.SOURCING_RULE);
            });
    Assertions.assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
  }
}
