/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionStatus;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffRulesRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffUIRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffUpdateRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.HolidayCutoffRulesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PageResponseForHolidayCutoff;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.AttributeFilterInfo;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.HolidayCutoffInfo;
import com.nextuple.promise.sourcing.rule.domain.entity.HolidayCutoffEntity;
import com.nextuple.promise.sourcing.rule.domain.mapper.HolidayCutoffMapper;
import com.nextuple.promise.sourcing.rule.domain.repository.HolidayCutOffRepository;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributesDefinitionDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingAttributePersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingAttributesDefinitionPersistenceService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class HolidayCutoffServiceTest {

  @InjectMocks HolidayCutoffService holidayCutoffService;
  @Mock private HolidayCutOffRepository holidayCutOffRepository;

  @Mock
  private SourcingAttributesDefinitionPersistenceService
      sourcingAttributesDefinitionPersistenceService;

  @Mock private SourcingAttributePersistenceService sourcingAttributePersistenceService;

  @Mock private SourcingAttributesDefinitionService sourcingAttributesDefinitionService;

  @Mock private HolidayCutoffMapper holidayCutoffMapper;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("When we add the holiday cutoff successfully")
  void createHolidayCutoffSuccessfulTest()
      throws PromiseEngineException, CommonServiceException, ParseException {
    HolidayCutoffRequest request = testUtil.createHolidayCutoffRequest();
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any()))
        .thenReturn(Optional.empty());
    HolidayCutoffEntity holidayCutoffEntity = testUtil.getHolidayCutoffEntity();
    when(holidayCutOffRepository.save(any())).thenReturn(holidayCutoffEntity);
    var sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setScope(
        SourcingAttributesDefinitionScopeEnum.HOLIDAY_CUTOFF);
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    var result = holidayCutoffService.createHolidayCutoff(request);
    assertEquals(TestUtil.ORG_ID_1, result.getOrgId());
    assertEquals(TestUtil.HOLIDAY_CUTOFF_NAME, result.getHolidayCutoffName());
    assertEquals(TestUtil.HOLIDAY_CUTOFF_RULE, result.getHolidayCutoffRule());
    assertEquals(TestUtil.CUSTOM_ATTRIBUTES, result.getCustomAttributes());
    verify(holidayCutOffRepository, times(1)).save(any());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any());
  }

  @Test
  @DisplayName("When we try to add a holiday cutoff with the primary keys already configured.")
  void createHolidayCutoffWithAlreadyConfiguredEntitiesTest() throws PromiseEngineException {
    HolidayCutoffRequest request = testUtil.createHolidayCutoffRequest();
    HolidayCutoffEntity holidayCutoffEntity = testUtil.getHolidayCutoffEntity();
    HolidayCutoffInfo holidayCutoffInfo = testUtil.createHolidayCutoffInfo();
    when(holidayCutOffRepository.findByOrgIdAndHolidayCutoffNameAndHolidayCutoffRule(
            request.getOrgId(), request.getHolidayCutoffName(), request.getHolidayCutoffRule()))
        .thenReturn(Optional.of(holidayCutoffEntity));
    when(holidayCutoffMapper.toHolidayCutoffEntity(request)).thenReturn(holidayCutoffEntity);
    when(holidayCutOffRepository.save(holidayCutoffEntity)).thenReturn(holidayCutoffEntity);
    when(holidayCutoffMapper.toHolidayCutoffInfo(holidayCutoffEntity))
        .thenReturn(holidayCutoffInfo);

    var sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setScope(
        SourcingAttributesDefinitionScopeEnum.HOLIDAY_CUTOFF);
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(holidayCutOffRepository.save(any())).thenReturn(holidayCutoffEntity);

    Exception exception =
        assertThrows(
            CommonServiceException.class, () -> holidayCutoffService.createHolidayCutoff(request));
    assertEquals(
        "Holiday cutoff with given orgId, holidayCutoffName and holidayCutoffRule is already configured",
        exception.getMessage());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any());
    verify(holidayCutOffRepository, times(1))
        .findByOrgIdAndHolidayCutoffNameAndHolidayCutoffRule(any(), any(), any());
  }

  @Test
  @DisplayName("When we try to add a holiday cutoff with start date and required attributes.")
  void createHolidayCutoffWithSameAttributesAndCutoffDateTest() throws PromiseEngineException {
    HolidayCutoffRequest request = testUtil.createHolidayCutoffRequest();
    HolidayCutoffEntity holidayCutoffEntity = testUtil.getHolidayCutoffEntity();
    when(holidayCutOffRepository.findByOrgIdAndHolidayCutoffRuleAndHolidayCutoffDate(
            request.getOrgId(), request.getHolidayCutoffRule(), request.getHolidayCutoffDate()))
        .thenReturn(List.of(holidayCutoffEntity));

    var sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setScope(
        SourcingAttributesDefinitionScopeEnum.HOLIDAY_CUTOFF);
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(holidayCutOffRepository.save(any())).thenReturn(holidayCutoffEntity);

    Exception exception =
        assertThrows(
            CommonServiceException.class, () -> holidayCutoffService.createHolidayCutoff(request));
    assertEquals(
        "Holiday cutoff rule with same cutoff date is already configured.", exception.getMessage());
    verify(holidayCutOffRepository, times(1))
        .findByOrgIdAndHolidayCutoffRuleAndHolidayCutoffDate(any(), any(), any());
  }

  @Test
  @DisplayName(
      "When we try to add a holiday cutoff with the pre cuttoff days more than cut off window.")
  void createHolidayCutoffWithPreCutoffMoreThanCutoffWindow() throws PromiseEngineException {
    HolidayCutoffRequest request = testUtil.createHolidayCutoffRequest();
    HolidayCutoffEntity holidayCutoffEntity = testUtil.getHolidayCutoffEntity();
    request.setPreCutoffDays(5.5);
    var sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    when(holidayCutOffRepository.findByOrgIdAndHolidayCutoffRuleAndHolidayCutoffDate(
            request.getOrgId(), request.getHolidayCutoffRule(), request.getHolidayCutoffDate()))
        .thenReturn(Collections.emptyList());
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(holidayCutOffRepository.save(any())).thenReturn(holidayCutoffEntity);

    sourcingAttributesDefinitionEntity.setScope(
        SourcingAttributesDefinitionScopeEnum.HOLIDAY_CUTOFF);

    Exception exception =
        assertThrows(
            CommonServiceException.class, () -> holidayCutoffService.createHolidayCutoff(request));
    assertEquals(
        "Pre-cutoff days configured should be less than holiday cutoff window days",
        exception.getMessage());
    verify(holidayCutOffRepository, times(1))
        .findByOrgIdAndHolidayCutoffRuleAndHolidayCutoffDate(any(), any(), any());
  }

  @Test
  @DisplayName(
      "When we try to add a holiday cutoff with overlap in start date and holiday cutoff date.")
  void createHolidayCutoffWithOverlapInStartDateAndHolidayCutoff() throws PromiseEngineException {
    HolidayCutoffRequest request = testUtil.createHolidayCutoffRequest();
    request.setStartDate(new Date(126, Calendar.NOVEMBER, 25));
    request.setHolidayCutoffDate(new Date(125, Calendar.NOVEMBER, 25));
    HolidayCutoffEntity holidayCutoffEntity = testUtil.getHolidayCutoffEntity();
    var sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    when(holidayCutOffRepository.findByOrgIdAndHolidayCutoffRuleAndHolidayCutoffDate(
            request.getOrgId(), request.getHolidayCutoffRule(), request.getHolidayCutoffDate()))
        .thenReturn(Collections.emptyList());
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(holidayCutOffRepository.save(any())).thenReturn(holidayCutoffEntity);

    sourcingAttributesDefinitionEntity.setScope(
        SourcingAttributesDefinitionScopeEnum.HOLIDAY_CUTOFF);

    Exception exception =
        assertThrows(
            CommonServiceException.class, () -> holidayCutoffService.createHolidayCutoff(request));
    assertEquals("Overlap in start date and holiday cutoff date.", exception.getMessage());
    verify(holidayCutOffRepository, times(1))
        .findByOrgIdAndHolidayCutoffRuleAndHolidayCutoffDate(any(), any(), any());
  }

  @Test
  @DisplayName("When the sourcing attributes definition status is inactive")
  void createHolidayCutoffSourcingAttributesDefinitionInactiveTest() throws PromiseEngineException {
    HolidayCutoffRequest request = testUtil.createHolidayCutoffRequest();
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(
            Optional.ofNullable(
                testUtil.getSourcingRuleAttributesDefinitionEntity(
                    SourcingAttributesDefinitionStatus.INACTIVE)));

    Exception ex =
        assertThrows(
            CommonServiceException.class, () -> holidayCutoffService.createHolidayCutoff(request));
    assertEquals(
        "Invalid sourcing attributes definition for HOLIDAY_CUTOFF scope / Sourcing attributes definition exists but not in ACTIVE status",
        ex.getMessage());

    assertEquals(TestUtil.ORG_ID_1, request.getOrgId());
    assertEquals(TestUtil.HOLIDAY_CUTOFF_NAME, request.getHolidayCutoffName());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  @DisplayName("When the sourcing attributes definition is not found for the Holiday cutoff")
  void createHolidayCutoffSourcingDefinitionNotFoundExceptionTest() throws PromiseEngineException {
    HolidayCutoffRequest request = testUtil.createHolidayCutoffRequest();
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class, () -> holidayCutoffService.createHolidayCutoff(request));
    assertEquals(
        "Invalid sourcing attributes definition for HOLIDAY_CUTOFF scope / Sourcing attributes definition exists but not in ACTIVE status",
        ex.getMessage());

    assertEquals(TestUtil.ORG_ID_1, request.getOrgId());
    assertEquals(TestUtil.HOLIDAY_CUTOFF_NAME, request.getHolidayCutoffName());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  @DisplayName(
      "When all the required attributes values are not present while creating holiday cutoff rule")
  void createHolidayCutoffRuleWhereAllRequiredAttributesNotPresentTest()
      throws PromiseEngineException {
    HolidayCutoffRequest request = testUtil.createHolidayCutoffRequest();
    request.setHolidayCutoffRule("EXPRESS");
    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionDomainDto =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionDomainDto.setScope(
        SourcingAttributesDefinitionScopeEnum.HOLIDAY_CUTOFF);
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionDomainDto));

    Exception ex =
        assertThrows(
            CommonServiceException.class, () -> holidayCutoffService.createHolidayCutoff(request));
    assertEquals(
        "Can’t add or fetch the holiday cutoff rules as all the required attributes are not present",
        ex.getMessage());

    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
    verify(holidayCutOffRepository, times(0)).save(any());
  }

  @Test
  @DisplayName(
      "When length of attributes is more than optional and required attributes combined while creating holiday cutoff rule")
  void createHolidayCutoffRuleValidateRequiredAndOptionalLengthTest()
      throws PromiseEngineException {
    HolidayCutoffRequest request = testUtil.createHolidayCutoffRequest();
    request.setHolidayCutoffRule("EXPRESS:R2:Electronics:Store");
    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setScope(
        SourcingAttributesDefinitionScopeEnum.HOLIDAY_CUTOFF);
    sourcingAttributesDefinitionEntity.setOptAttributes("3");
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));

    Exception ex =
        assertThrows(
            CommonServiceException.class, () -> holidayCutoffService.createHolidayCutoff(request));
    assertEquals(
        "Can't add or fetch the holiday cutoff rules as length of attributes is more than optional and required attributes combined",
        ex.getMessage());

    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(anyLong(), anyString());
    verify(holidayCutOffRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("When we add a holiday cutoff with some missing optional fields")
  void createHolidayCutoffWithDefaultValuesForMissingFields()
      throws PromiseEngineException, CommonServiceException, ParseException {
    HolidayCutoffRequest request = testUtil.createHolidayCutoffRequestWithMissingFields();
    request.setStartDate(new Date(124, Calendar.NOVEMBER, 25));
    request.setHolidayCutoffDate(new Date(125, Calendar.NOVEMBER, 25));
    request.setHolidayDeliveryDate(new Date(125, Calendar.NOVEMBER, 29));
    request.setPreCutoffDays(3.0);
    request.setCustomAttributes(TestUtil.CUSTOM_ATTRIBUTES);
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any()))
        .thenReturn(Optional.empty());
    HolidayCutoffEntity holidayCutoffEntity = testUtil.getHolidayCutoffEntityWithDefaultFields();
    when(holidayCutOffRepository.save(any())).thenReturn(holidayCutoffEntity);
    var sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setScope(
        SourcingAttributesDefinitionScopeEnum.HOLIDAY_CUTOFF);
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    var result = holidayCutoffService.createHolidayCutoff(request);
    assertEquals(TestUtil.DEFAULT_STATUS, result.getStatus());
    assertEquals(TestUtil.DEFAULT_DAYS, result.getPreCutoffDays());
    assertEquals(TestUtil.DEFAULT_DAYS_TYPE, result.getDeliveryCoolDownDaysType());
    assertEquals(TestUtil.CUSTOM_ATTRIBUTES, result.getCustomAttributes());
    verify(holidayCutOffRepository, times(1)).save(any());
    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any());
  }

  @Test
  @DisplayName(
      "When holiday cutoff rules for the given order or line attributes are fetched successfully with only required attributes")
  void processFetchHolidayCutoffRulesWithOnlyRequiredAttributesTest()
      throws PromiseEngineException, CommonServiceException {
    HolidayCutoffRulesRequest request = testUtil.getHolidayCutoffRulesRequest();
    var sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setScope(
        SourcingAttributesDefinitionScopeEnum.HOLIDAY_CUTOFF);
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(holidayCutOffRepository.findByOrgIdAndSourcingAttributesDefinitionIdAndHolidayCutoffRule(
            any(), anyLong(), any()))
        .thenReturn(Optional.of(testUtil.getHolidayCutoffEntity()));
    HolidayCutoffRulesResponse result =
        holidayCutoffService.processFetchHolidayCutoffRules(request);
    assertEquals(1, result.getHolidayCutoffInfo().size());
    assertEquals(
        TestUtil.HOLIDAY_CUTOFF_RULE, result.getHolidayCutoffInfo().get(0).getHolidayCutoffRule());
    assertEquals(
        TestUtil.CUSTOM_ATTRIBUTES, result.getHolidayCutoffInfo().get(0).getCustomAttributes());

    verify(holidayCutOffRepository, times(0))
        .findByOrgIdAndSourcingAttributesDefinitionIdAndHolidayCutoffRuleIn(
            any(), anyLong(), any());
    verify(holidayCutOffRepository, times(1))
        .findByOrgIdAndSourcingAttributesDefinitionIdAndHolidayCutoffRule(any(), anyLong(), any());
    verify(sourcingAttributesDefinitionPersistenceService, times(2))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any());
  }

  @Test
  @DisplayName(
      "When holiday cutoff rules for the given order or line attributes are fetched successfully with only required attributes and no matching rules")
  void processFetchHolidayCutoffRulesWithOnlyRequiredAttributesWithEmptyRulesMatchingTest()
      throws PromiseEngineException, CommonServiceException {
    HolidayCutoffRulesRequest request = testUtil.getHolidayCutoffRulesRequest();
    var sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setScope(
        SourcingAttributesDefinitionScopeEnum.HOLIDAY_CUTOFF);
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(holidayCutOffRepository.findByOrgIdAndSourcingAttributesDefinitionIdAndHolidayCutoffRule(
            any(), anyLong(), any()))
        .thenReturn(Optional.empty());
    HolidayCutoffRulesResponse result =
        holidayCutoffService.processFetchHolidayCutoffRules(request);
    assertEquals(0, result.getHolidayCutoffInfo().size());

    verify(holidayCutOffRepository, times(0))
        .findByOrgIdAndSourcingAttributesDefinitionIdAndHolidayCutoffRuleIn(
            any(), anyLong(), any());
    verify(holidayCutOffRepository, times(1))
        .findByOrgIdAndSourcingAttributesDefinitionIdAndHolidayCutoffRule(any(), anyLong(), any());
    verify(sourcingAttributesDefinitionPersistenceService, times(2))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any());
  }

  @Test
  @DisplayName(
      "When holiday cutoff rules for the given order or line attributes are fetched successfully with both required and optional attributes")
  void processFetchHolidayCutoffRulesWithBothReqAndOptAttributesTest()
      throws PromiseEngineException, CommonServiceException {
    HolidayCutoffRulesRequest request = testUtil.getHolidayCutoffRulesRequest();
    request.getSourcingAttributeValuesInfo().setOptionalAttributesValue("O1:O2");
    var sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setScope(
        SourcingAttributesDefinitionScopeEnum.HOLIDAY_CUTOFF);
    sourcingAttributesDefinitionEntity.setOptAttributes("3,4");
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(holidayCutOffRepository.findByOrgIdAndSourcingAttributesDefinitionIdAndHolidayCutoffRuleIn(
            any(), anyLong(), any()))
        .thenReturn(
            testUtil.getMultipleHolidayCutoffEntities(
                List.of("EXPRESS:R1:O1:02", "EXPRESS:R1:O1:", "EXPRESS:R1::02", "EXPRESS:R1::")));
    HolidayCutoffRulesResponse result =
        holidayCutoffService.processFetchHolidayCutoffRules(request);
    assertEquals(4, result.getHolidayCutoffInfo().size());
    assertEquals(
        "EXPRESS:R1:O1:02", result.getHolidayCutoffInfo().getFirst().getHolidayCutoffRule());
    assertEquals(
        TestUtil.CUSTOM_ATTRIBUTES, result.getHolidayCutoffInfo().get(0).getCustomAttributes());

    verify(holidayCutOffRepository, times(1))
        .findByOrgIdAndSourcingAttributesDefinitionIdAndHolidayCutoffRuleIn(
            any(), anyLong(), any());
    verify(holidayCutOffRepository, times(0))
        .findByOrgIdAndSourcingAttributesDefinitionIdAndHolidayCutoffRule(any(), anyLong(), any());
    verify(sourcingAttributesDefinitionPersistenceService, times(2))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any());
  }

  @Test
  @DisplayName(
      "When holiday cutoff rules for the given order or line attributes are fetched successfully with single optional attributes")
  void processFetchHolidayCutoffRulesWithSingleOptAttributesTest()
      throws PromiseEngineException, CommonServiceException {
    HolidayCutoffRulesRequest request = testUtil.getHolidayCutoffRulesRequest();
    request.getSourcingAttributeValuesInfo().setOptionalAttributesValue("O1");
    var sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setScope(
        SourcingAttributesDefinitionScopeEnum.HOLIDAY_CUTOFF);
    sourcingAttributesDefinitionEntity.setOptAttributes("3");
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(holidayCutOffRepository.findByOrgIdAndSourcingAttributesDefinitionIdAndHolidayCutoffRuleIn(
            any(), anyLong(), any()))
        .thenReturn(
            testUtil.getMultipleHolidayCutoffEntities(List.of("EXPRESS:R1:O1", "EXPRESS:R1::")));
    HolidayCutoffRulesResponse result =
        holidayCutoffService.processFetchHolidayCutoffRules(request);
    assertEquals(2, result.getHolidayCutoffInfo().size());
    assertEquals("EXPRESS:R1:O1", result.getHolidayCutoffInfo().getFirst().getHolidayCutoffRule());
    assertEquals(
        TestUtil.CUSTOM_ATTRIBUTES, result.getHolidayCutoffInfo().get(0).getCustomAttributes());

    verify(holidayCutOffRepository, times(1))
        .findByOrgIdAndSourcingAttributesDefinitionIdAndHolidayCutoffRuleIn(
            any(), anyLong(), any());
    verify(holidayCutOffRepository, times(0))
        .findByOrgIdAndSourcingAttributesDefinitionIdAndHolidayCutoffRule(any(), anyLong(), any());
    verify(sourcingAttributesDefinitionPersistenceService, times(2))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any());
  }

  @Test
  @DisplayName(
      "When holiday cutoff rules for the given order or line attributes are fetched successfully with empty optional attribute")
  void processFetchHolidayCutoffRulesWithEmptyOptAttributeTest()
      throws PromiseEngineException, CommonServiceException {
    HolidayCutoffRulesRequest request = testUtil.getHolidayCutoffRulesRequest();
    request.getSourcingAttributeValuesInfo().setOptionalAttributesValue(":O2");
    var sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setScope(
        SourcingAttributesDefinitionScopeEnum.HOLIDAY_CUTOFF);
    sourcingAttributesDefinitionEntity.setOptAttributes("3,4");
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(holidayCutOffRepository.findByOrgIdAndSourcingAttributesDefinitionIdAndHolidayCutoffRuleIn(
            any(), anyLong(), any()))
        .thenReturn(
            testUtil.getMultipleHolidayCutoffEntities(List.of("EXPRESS:R1::02", "EXPRESS:R1::")));
    HolidayCutoffRulesResponse result =
        holidayCutoffService.processFetchHolidayCutoffRules(request);
    assertEquals(2, result.getHolidayCutoffInfo().size());
    assertEquals("EXPRESS:R1::02", result.getHolidayCutoffInfo().getFirst().getHolidayCutoffRule());
    assertEquals(
        TestUtil.CUSTOM_ATTRIBUTES, result.getHolidayCutoffInfo().get(0).getCustomAttributes());

    verify(holidayCutOffRepository, times(1))
        .findByOrgIdAndSourcingAttributesDefinitionIdAndHolidayCutoffRuleIn(
            any(), anyLong(), any());
    verify(holidayCutOffRepository, times(0))
        .findByOrgIdAndSourcingAttributesDefinitionIdAndHolidayCutoffRule(any(), anyLong(), any());
    verify(sourcingAttributesDefinitionPersistenceService, times(2))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any());
  }

  @Test
  @DisplayName(
      "When holiday cutoff rules for the given order or line attributes are fetched successfully with both empty optional attributes")
  void processFetchHolidayCutoffRulesWithBothEmptyOptionalOptAttributesTest()
      throws PromiseEngineException, CommonServiceException {
    HolidayCutoffRulesRequest request = testUtil.getHolidayCutoffRulesRequest();
    request.getSourcingAttributeValuesInfo().setOptionalAttributesValue(":");
    var sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setScope(
        SourcingAttributesDefinitionScopeEnum.HOLIDAY_CUTOFF);
    sourcingAttributesDefinitionEntity.setOptAttributes("3,4");
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));
    when(holidayCutOffRepository.findByOrgIdAndSourcingAttributesDefinitionIdAndHolidayCutoffRuleIn(
            any(), anyLong(), any()))
        .thenReturn(testUtil.getMultipleHolidayCutoffEntities(List.of("EXPRESS:R1::")));
    HolidayCutoffRulesResponse result =
        holidayCutoffService.processFetchHolidayCutoffRules(request);
    assertEquals(1, result.getHolidayCutoffInfo().size());
    assertEquals("EXPRESS:R1::", result.getHolidayCutoffInfo().getFirst().getHolidayCutoffRule());
    assertEquals(
        TestUtil.CUSTOM_ATTRIBUTES, result.getHolidayCutoffInfo().get(0).getCustomAttributes());

    verify(holidayCutOffRepository, times(1))
        .findByOrgIdAndSourcingAttributesDefinitionIdAndHolidayCutoffRuleIn(
            any(), anyLong(), any());
    verify(holidayCutOffRepository, times(0))
        .findByOrgIdAndSourcingAttributesDefinitionIdAndHolidayCutoffRule(any(), anyLong(), any());
    verify(sourcingAttributesDefinitionPersistenceService, times(2))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any());
  }

  @Test
  @DisplayName(
      "When all the required attributes values are not present while fetching holiday cutoff rules")
  void fetchHolidayCutoffRulesWhereAllRequiredAttributesNotPresentTest()
      throws PromiseEngineException {
    HolidayCutoffRulesRequest request = testUtil.getHolidayCutoffRulesRequest();
    request.getSourcingAttributeValuesInfo().setRequiredAttributesValue("EXPRESS");
    var sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setScope(
        SourcingAttributesDefinitionScopeEnum.HOLIDAY_CUTOFF);
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> holidayCutoffService.processFetchHolidayCutoffRules(request));

    assertEquals(
        "Can’t add or fetch the holiday cutoff rules as all the required attributes are not present",
        ex.getMessage());

    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any());
  }

  @Test
  @DisplayName(
      "When length of attributes is more than optional and required attributes combined while fetching holiday cutoff rules")
  void fetchHolidayCutoffRulesValidateRequiredAndOptionalLengthTest()
      throws PromiseEngineException {
    HolidayCutoffRulesRequest request = testUtil.getHolidayCutoffRulesRequest();
    request.getSourcingAttributeValuesInfo().setOptionalAttributesValue("Electronics:Store");
    var sourcingAttributesDefinitionEntity =
        testUtil.getSourcingRuleAttributesDefinitionEntity(
            SourcingAttributesDefinitionStatus.ACTIVE);
    sourcingAttributesDefinitionEntity.setScope(
        SourcingAttributesDefinitionScopeEnum.HOLIDAY_CUTOFF);
    sourcingAttributesDefinitionEntity.setOptAttributes("3");
    when(sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(sourcingAttributesDefinitionEntity));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> holidayCutoffService.processFetchHolidayCutoffRules(request));

    assertEquals(
        "Can't add or fetch the holiday cutoff rules as length of attributes is more than optional and required attributes combined",
        ex.getMessage());

    verify(sourcingAttributesDefinitionPersistenceService, times(1))
        .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(any(), any());
  }

  @Test
  @DisplayName("When we update the holiday cutoff successfully")
  void updateHolidayCutoffSuccessfulTest()
      throws PromiseEngineException, CommonServiceException, ParseException {
    HolidayCutoffUpdateRequest request = testUtil.updateHolidayCutoffRequest();
    HolidayCutoffEntity holidayCutoffEntity = testUtil.getHolidayCutoffEntity();
    when(holidayCutOffRepository.findByOrgIdAndHolidayCutoffNameAndHolidayCutoffRule(
            any(), any(), any()))
        .thenReturn(Optional.ofNullable(holidayCutoffEntity));
    when(holidayCutOffRepository.save(any())).thenReturn(holidayCutoffEntity);
    var result =
        holidayCutoffService.updateHolidayCutoff(
            TestUtil.ORG_ID_1, TestUtil.HOLIDAY_CUTOFF_NAME, TestUtil.HOLIDAY_CUTOFF_RULE, request);
    assertEquals(TestUtil.ORG_ID_1, result.getOrgId());
    assertEquals(TestUtil.HOLIDAY_CUTOFF_NAME, result.getHolidayCutoffName());
    assertEquals(TestUtil.HOLIDAY_CUTOFF_RULE, result.getHolidayCutoffRule());
    assertEquals(testUtil.createHolidayCutoffInfo(), result);
    assertEquals(TestUtil.CUSTOM_ATTRIBUTES, result.getCustomAttributes());
    verify(holidayCutOffRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("When holiday cutoff data is not found")
  void updateHolidayCutoffNotFoundTest()
      throws PromiseEngineException, CommonServiceException, ParseException {
    HolidayCutoffUpdateRequest request = testUtil.updateHolidayCutoffRequest();
    HolidayCutoffEntity holidayCutoffEntity = testUtil.getHolidayCutoffEntity();
    when(holidayCutOffRepository.findByOrgIdAndHolidayCutoffNameAndHolidayCutoffRule(
            any(), any(), any()))
        .thenReturn(Optional.empty());
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                holidayCutoffService.updateHolidayCutoff(
                    TestUtil.ORG_ID_2,
                    TestUtil.HOLIDAY_CUTOFF_NAME,
                    TestUtil.HOLIDAY_CUTOFF_RULE,
                    request));
    assertEquals("Holiday cutoff data for a given input not found", ex.getMessage());
    verify(holidayCutOffRepository, times(1))
        .findByOrgIdAndHolidayCutoffNameAndHolidayCutoffRule(any(), any(), any());
  }

  @Test
  @DisplayName("When there is overlap in start date and holiday cutoff date.")
  void updateHolidayCutoffInvalidStartDateTest()
      throws PromiseEngineException, CommonServiceException, ParseException {
    HolidayCutoffUpdateRequest request = testUtil.updateHolidayCutoffRequest();

    request.setStartDate(
        (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")).parse("2024-12-25T12:00:00Z"));
    request.setHolidayCutoffDate(
        (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")).parse("2024-12-01T12:00:00Z"));
    HolidayCutoffEntity holidayCutoffEntity = testUtil.getHolidayCutoffEntity();
    when(holidayCutOffRepository.findByOrgIdAndHolidayCutoffNameAndHolidayCutoffRule(
            any(), any(), any()))
        .thenReturn(Optional.ofNullable(holidayCutoffEntity));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                holidayCutoffService.updateHolidayCutoff(
                    TestUtil.ORG_ID_1,
                    TestUtil.HOLIDAY_CUTOFF_NAME,
                    TestUtil.HOLIDAY_CUTOFF_RULE,
                    request));
    assertEquals("Overlap in start date and holiday cutoff date.", ex.getMessage());
    verify(holidayCutOffRepository, times(1))
        .findByOrgIdAndHolidayCutoffNameAndHolidayCutoffRule(any(), any(), any());
  }

  @Test
  @DisplayName("When Pre-cutoff days configured is more than holiday cutoff window days")
  void updateHolidayCutoffInvalidPreCutoffDaysTest() throws ParseException {
    HolidayCutoffUpdateRequest request = testUtil.updateHolidayCutoffRequest();

    request.setStartDate(
        (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")).parse("2024-12-01T12:00:00Z"));
    request.setHolidayCutoffDate(
        (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")).parse("2024-12-15T12:00:00Z"));
    request.setPreCutoffDays(16.0);
    HolidayCutoffEntity holidayCutoffEntity = testUtil.getHolidayCutoffEntity();
    when(holidayCutOffRepository.findByOrgIdAndHolidayCutoffNameAndHolidayCutoffRule(
            any(), any(), any()))
        .thenReturn(Optional.ofNullable(holidayCutoffEntity));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                holidayCutoffService.updateHolidayCutoff(
                    TestUtil.ORG_ID_1,
                    TestUtil.HOLIDAY_CUTOFF_NAME,
                    TestUtil.HOLIDAY_CUTOFF_RULE,
                    request));
    assertEquals(
        "Pre-cutoff days configured should be less than holiday cutoff window days",
        ex.getMessage());
    verify(holidayCutOffRepository, times(1))
        .findByOrgIdAndHolidayCutoffNameAndHolidayCutoffRule(any(), any(), any());
  }

  @Test
  @DisplayName("When only status field is passed in update Holiday Cutoff request body")
  void updateHolidayCutoffWithJustStatusInRequestBodyTest()
      throws ParseException, CommonServiceException {
    HolidayCutoffUpdateRequest request = testUtil.updateHolidayCutoffRequestWithStatusField();
    HolidayCutoffEntity holidayCutoffEntity = testUtil.getHolidayCutoffEntity();
    when(holidayCutOffRepository.findByOrgIdAndHolidayCutoffNameAndHolidayCutoffRule(
            any(), any(), any()))
        .thenReturn(Optional.ofNullable(holidayCutoffEntity));
    when(holidayCutOffRepository.save(any())).thenReturn(holidayCutoffEntity);
    var result =
        holidayCutoffService.updateHolidayCutoff(
            TestUtil.ORG_ID_1, TestUtil.HOLIDAY_CUTOFF_NAME, TestUtil.HOLIDAY_CUTOFF_RULE, request);
    assertEquals(TestUtil.ORG_ID_1, result.getOrgId());
    assertEquals(TestUtil.HOLIDAY_CUTOFF_NAME, result.getHolidayCutoffName());
    assertEquals(TestUtil.HOLIDAY_CUTOFF_RULE, result.getHolidayCutoffRule());
    assertEquals(TestUtil.CUSTOM_ATTRIBUTES, result.getCustomAttributes());

    verify(holidayCutOffRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("When we fetch the holiday cutoff successfully")
  void fetchHolidayCutoffSuccessfulTest()
      throws PromiseEngineException, CommonServiceException, ParseException {
    HolidayCutoffEntity holidayCutoffEntity = testUtil.getHolidayCutoffEntity();
    when(holidayCutOffRepository.findByOrgIdAndHolidayCutoffNameAndHolidayCutoffRule(
            any(), any(), any()))
        .thenReturn(Optional.ofNullable(holidayCutoffEntity));
    var result =
        holidayCutoffService.fetchHolidayCutoff(
            TestUtil.ORG_ID_1, TestUtil.HOLIDAY_CUTOFF_NAME, TestUtil.HOLIDAY_CUTOFF_RULE);
    assertEquals(TestUtil.ORG_ID_1, result.getOrgId());
    assertEquals(TestUtil.HOLIDAY_CUTOFF_NAME, result.getHolidayCutoffName());
    assertEquals(TestUtil.HOLIDAY_CUTOFF_RULE, result.getHolidayCutoffRule());
    assertEquals(TestUtil.CUSTOM_ATTRIBUTES, result.getCustomAttributes());

    verify(holidayCutOffRepository, times(1))
        .findByOrgIdAndHolidayCutoffNameAndHolidayCutoffRule(any(), any(), any());
  }

  @Test
  @DisplayName("When holiday cutoff data is not found")
  void fetchHolidayCutoffNotFoundTest()
      throws PromiseEngineException, CommonServiceException, ParseException {
    HolidayCutoffEntity holidayCutoffEntity = testUtil.getHolidayCutoffEntity();
    when(holidayCutOffRepository.findByOrgIdAndHolidayCutoffNameAndHolidayCutoffRule(
            any(), any(), any()))
        .thenReturn(Optional.empty());
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                holidayCutoffService.fetchHolidayCutoff(
                    TestUtil.ORG_ID_2, TestUtil.HOLIDAY_CUTOFF_NAME, TestUtil.HOLIDAY_CUTOFF_RULE));
    assertEquals("Holiday cutoff data for a given input not found", ex.getMessage());
    verify(holidayCutOffRepository, times(1))
        .findByOrgIdAndHolidayCutoffNameAndHolidayCutoffRule(any(), any(), any());
  }

  @Test
  @DisplayName("When we delete the holiday cutoff successfully")
  void deleteHolidayCutoffSuccessfulTest()
      throws PromiseEngineException, CommonServiceException, ParseException {
    HolidayCutoffEntity holidayCutoffEntity = testUtil.getHolidayCutoffEntity();
    when(holidayCutOffRepository.findByOrgIdAndHolidayCutoffNameAndHolidayCutoffRule(
            any(), any(), any()))
        .thenReturn(Optional.ofNullable(holidayCutoffEntity));
    doNothing().when(holidayCutOffRepository).delete(any());
    var result =
        holidayCutoffService.deleteHolidayCutoff(
            TestUtil.ORG_ID_1, TestUtil.HOLIDAY_CUTOFF_NAME, TestUtil.HOLIDAY_CUTOFF_RULE);
    assertEquals(TestUtil.ORG_ID_1, result.getOrgId());
    assertEquals(TestUtil.HOLIDAY_CUTOFF_NAME, result.getHolidayCutoffName());
    assertEquals(TestUtil.HOLIDAY_CUTOFF_RULE, result.getHolidayCutoffRule());
    verify(holidayCutOffRepository, times(1)).delete(any());
  }

  @Test
  @DisplayName("When holiday cutoff data is not found")
  void deleteHolidayCutoffNotFoundTest()
      throws PromiseEngineException, CommonServiceException, ParseException {
    HolidayCutoffEntity holidayCutoffEntity = testUtil.getHolidayCutoffEntity();
    when(holidayCutOffRepository.findByOrgIdAndHolidayCutoffNameAndHolidayCutoffRule(
            any(), any(), any()))
        .thenReturn(Optional.empty());
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                holidayCutoffService.deleteHolidayCutoff(
                    TestUtil.ORG_ID_2, TestUtil.HOLIDAY_CUTOFF_NAME, TestUtil.HOLIDAY_CUTOFF_RULE));
    assertEquals("Holiday cutoff data for a given input not found", ex.getMessage());
    verify(holidayCutOffRepository, times(1))
        .findByOrgIdAndHolidayCutoffNameAndHolidayCutoffRule(any(), any(), any());
  }

  @Test
  @DisplayName("When there is no active sourcing rule")
  void testGetHolidayCutoffDetailsByOrgId_NoActiveSourcingRule() throws Exception {
    Boolean isPaginated = true;
    HolidayCutoffUIRequest holidayCutoffUIRequest =
        HolidayCutoffUIRequest.builder().sourcingAttributesDefinitionId(1L).build();
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
            anyLong(), anyString()))
        .thenReturn(
            testUtil.getSourcingRuleAttributesDefinitionResponse(
                SourcingAttributesDefinitionStatus.INACTIVE));

    PageResponseForHolidayCutoff response =
        holidayCutoffService.getHolidayCutoffDetailsBasedOnFilters(
            TestUtil.ORG_ID,
            isPaginated,
            testUtil.getPageParams(
                Optional.of(1), Optional.of(10), Optional.of("ruleName"), Optional.of("ASC")),
            holidayCutoffUIRequest);
    verify(sourcingAttributesDefinitionService, times(1))
        .processGetSourcingAttributesDefinitionByIdandOrgId(anyLong(), any());
    assertNull(response.getData());
    assertNull(response.getPagination());
  }

  @Test
  @DisplayName("when the sort order is invalid")
  void testGetHolidayCutoffDetailsByOrgId_InvalidSortOrder()
      throws PromiseEngineException, CommonServiceException {
    Boolean isPaginated = true;

    HolidayCutoffUIRequest holidayCutoffUIRequest =
        HolidayCutoffUIRequest.builder().sourcingAttributesDefinitionId(1L).build();
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
            anyLong(), anyString()))
        .thenThrow(new RuntimeException("Feign error"));

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> {
              holidayCutoffService.getHolidayCutoffDetailsBasedOnFilters(
                  TestUtil.ORG_ID,
                  isPaginated,
                  testUtil.getPageParams(
                      Optional.of(1),
                      Optional.of(10),
                      Optional.of("ruleName"),
                      Optional.of("invalid")),
                  holidayCutoffUIRequest);
            });
    verify(sourcingAttributesDefinitionService, times(0))
        .processGetSourcingAttributesDefinitionByIdandOrgId(anyLong(), any());
    assertEquals("Invalid sort order, consider giving either ASC or DESC", exception.getMessage());
  }

  @Test
  @DisplayName("Get Holiday Cutoff Details By OrgId Happy path with pagination")
  void testGetHolidayCutoffDetailsByOrgId_HappyPathWithPagination() throws Exception {
    Integer pageNo = 1;
    Boolean isPaginated = true;
    List<HolidayCutoffEntity> holidayCutoffEntityList = testUtil.getHolidayCutoffEntityList();
    Pageable pageable = PageRequest.of(pageNo - 1, 10, Sort.by("ASC"));

    Page<HolidayCutoffEntity> holidayCutoffEntityPage =
        new PageImpl<>(holidayCutoffEntityList, pageable, holidayCutoffEntityList.size());

    HolidayCutoffUIRequest holidayCutoffUIRequest =
        HolidayCutoffUIRequest.builder().sourcingAttributesDefinitionId(1L).build();
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
            anyLong(), anyString()))
        .thenReturn(
            testUtil.getSourcingRuleAttributesDefinitionResponse(
                SourcingAttributesDefinitionStatus.ACTIVE));

    when(sourcingAttributePersistenceService.fetchSourcingAttributeListByOrgIdAndAttributeIds(
            any(), any()))
        .thenReturn(testUtil.getSourcingAttributeDomainDtos());

    when(holidayCutOffRepository.findFilteredHolidayCutoffRules(any(), any(), any(), any()))
        .thenReturn(holidayCutoffEntityPage);

    PageResponseForHolidayCutoff response =
        holidayCutoffService.getHolidayCutoffDetailsBasedOnFilters(
            TestUtil.ORG_ID,
            isPaginated,
            testUtil.getPageParams(
                Optional.of(1), Optional.of(10), Optional.of("ruleName"), Optional.of("ASC")),
            holidayCutoffUIRequest);

    verify(sourcingAttributesDefinitionService, times(1))
        .processGetSourcingAttributesDefinitionByIdandOrgId(anyLong(), any());
    verify(holidayCutOffRepository, times(1))
        .findFilteredHolidayCutoffRules(any(), any(), any(), any());
    assertNotNull(response.getData());
    assertEquals(
        testUtil.getSourcingAttributeEntity().getAttributeName(),
        response.getData().getColumns().get(2).getColumnName());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(1, response.getPagination().getTotalPages());
    assertEquals(
        testUtil.getHolidayCutoffEntity().getHolidayCutoffName(),
        response.getData().getRows().get(0).get(TestUtil.RULE_NAME_UI));
    assertEquals(
        testUtil.getHolidayCutoffEntity2().getHolidayCutoffName(),
        response.getData().getRows().get(1).get(TestUtil.RULE_NAME_UI));

    assertEquals("ASC", response.getPagination().getSortOrder());
  }

  @Test
  @DisplayName(
      "Get Holiday Cutoff Details By OrgId Happy path with holiday cutoff definition not having optional attributes")
  void testGetHolidayCutoffDetailsByOrgId_HappyPathRuleDefintionNotHaveOptionalAttr()
      throws Exception {
    Integer pageNo = 1;
    Boolean isPaginated = true;
    List<HolidayCutoffEntity> holidayCutoffEntityList = testUtil.getHolidayCutoffEntityList();
    Pageable pageable = PageRequest.of(pageNo - 1, 10, Sort.by("ASC"));

    Page<HolidayCutoffEntity> holidayCutoffEntityPage =
        new PageImpl<>(holidayCutoffEntityList, pageable, holidayCutoffEntityList.size());
    var holidayCutoffAttributeDefinition =
        testUtil.getSourcingRuleAttributesDefinitionResponse(
            SourcingAttributesDefinitionStatus.ACTIVE);
    holidayCutoffAttributeDefinition.setOptAttributes(" ");
    HolidayCutoffUIRequest holidayCutoffUIRequest =
        HolidayCutoffUIRequest.builder().sourcingAttributesDefinitionId(1L).build();
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
            anyLong(), anyString()))
        .thenReturn(holidayCutoffAttributeDefinition);

    when(sourcingAttributePersistenceService.fetchSourcingAttributeListByOrgIdAndAttributeIds(
            any(), any()))
        .thenReturn(testUtil.getSourcingAttributeDomainDtos());

    when(holidayCutOffRepository.findFilteredHolidayCutoffRules(any(), any(), any(), any()))
        .thenReturn(holidayCutoffEntityPage);

    PageResponseForHolidayCutoff response =
        holidayCutoffService.getHolidayCutoffDetailsBasedOnFilters(
            TestUtil.ORG_ID,
            isPaginated,
            testUtil.getPageParams(
                Optional.of(1), Optional.of(10), Optional.of("ruleName"), Optional.of("ASC")),
            holidayCutoffUIRequest);

    verify(sourcingAttributesDefinitionService, times(1))
        .processGetSourcingAttributesDefinitionByIdandOrgId(anyLong(), any());
    verify(holidayCutOffRepository, times(1))
        .findFilteredHolidayCutoffRules(any(), any(), any(), any());
    assertNotNull(response.getData());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(1, response.getPagination().getTotalPages());
    assertEquals("ASC", response.getPagination().getSortOrder());
  }

  @Test
  @DisplayName("Get Holiday Cutoff Details By OrgId Happy path without pagination")
  void testGetHolidayCutoffDetailsByOrgId_HappyPathWithOutPagination() throws Exception {
    Integer pageNo = 1;
    Boolean isPaginated = false;
    List<HolidayCutoffEntity> holidayCutoffEntityList = testUtil.getHolidayCutoffEntityList();
    Pageable pageable = PageRequest.of(pageNo - 1, Integer.MAX_VALUE);

    Page<HolidayCutoffEntity> holidayCutoffEntityPage =
        new PageImpl<>(holidayCutoffEntityList, pageable, holidayCutoffEntityList.size());

    HolidayCutoffUIRequest holidayCutoffUIRequest =
        HolidayCutoffUIRequest.builder().sourcingAttributesDefinitionId(1L).build();
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
            anyLong(), anyString()))
        .thenReturn(
            testUtil.getSourcingRuleAttributesDefinitionResponse(
                SourcingAttributesDefinitionStatus.ACTIVE));

    when(sourcingAttributePersistenceService.fetchSourcingAttributeListByOrgIdAndAttributeIds(
            any(), any()))
        .thenReturn(testUtil.getSourcingAttributeDomainDtos());

    when(holidayCutOffRepository.findFilteredHolidayCutoffRules(any(), any(), any(), any()))
        .thenReturn(holidayCutoffEntityPage);

    PageResponseForHolidayCutoff response =
        holidayCutoffService.getHolidayCutoffDetailsBasedOnFilters(
            TestUtil.ORG_ID,
            isPaginated,
            testUtil.getPageParams(
                Optional.of(1), Optional.of(10), Optional.of("ruleName"), Optional.of("ASC")),
            holidayCutoffUIRequest);

    verify(sourcingAttributesDefinitionService, times(1))
        .processGetSourcingAttributesDefinitionByIdandOrgId(any(), any());
    verify(holidayCutOffRepository, times(1))
        .findFilteredHolidayCutoffRules(any(), any(), any(), any());
    assertNotNull(response.getData());
    assertNotNull(response.getData());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(1, response.getPagination().getTotalPages());
    assertEquals("ASC", response.getPagination().getSortOrder());
  }

  @Test
  @DisplayName(
      "Get Holiday Cutoff Details : With active sourcing rule and no data in holiday_cutoff table")
  void testGetHolidayCutoffDetailsByOrgId_WithNoRecordsIntheHolidayCutoffTable() throws Exception {
    Boolean isPaginated = false;
    List<HolidayCutoffEntity> holidayCutoffEntityList = testUtil.getHolidayCutoffEmptyList();
    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).descending());

    Page<HolidayCutoffEntity> holidayCutoffEntityPage =
        new PageImpl<>(holidayCutoffEntityList, pageable, holidayCutoffEntityList.size());

    HolidayCutoffUIRequest holidayCutoffUIRequest =
        HolidayCutoffUIRequest.builder().sourcingAttributesDefinitionId(1L).build();
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
            anyLong(), anyString()))
        .thenReturn(
            testUtil.getSourcingRuleAttributesDefinitionResponse(
                SourcingAttributesDefinitionStatus.ACTIVE));

    when(sourcingAttributePersistenceService.fetchSourcingAttributeListByOrgIdAndAttributeIds(
            any(), any()))
        .thenReturn(testUtil.getSourcingAttributeDomainDtos());

    when(holidayCutOffRepository.findFilteredHolidayCutoffRules(any(), any(), any(), any()))
        .thenReturn(holidayCutoffEntityPage);

    PageResponseForHolidayCutoff response =
        holidayCutoffService.getHolidayCutoffDetailsBasedOnFilters(
            TestUtil.ORG_ID,
            isPaginated,
            testUtil.getPageParams(
                Optional.of(1), Optional.of(10), Optional.of("ruleName"), Optional.of("ASC")),
            holidayCutoffUIRequest);
    verify(sourcingAttributesDefinitionService, times(1))
        .processGetSourcingAttributesDefinitionByIdandOrgId(anyLong(), any());
    verify(holidayCutOffRepository, times(1))
        .findFilteredHolidayCutoffRules(any(), any(), any(), any());
    assertNotNull(response.getData());
    assertNull(response.getPagination());
  }

  @Test
  @DisplayName("Get Holiday Cutoff Details when filters are passed")
  void testGetHolidayCutoffDetailsByFilters_HappyPathWithPagination() throws Exception {
    Integer pageNo = 1;
    Boolean isPaginated = true;
    List<HolidayCutoffEntity> holidayCutoffEntityList = testUtil.getHolidayCutoffEntityList();
    Pageable pageable = PageRequest.of(pageNo - 1, 10, Sort.by("ASC"));

    Page<HolidayCutoffEntity> holidayCutoffEntityPage =
        new PageImpl<>(holidayCutoffEntityList, pageable, holidayCutoffEntityList.size());
    AttributeFilterInfo attributeFilterInfo =
        AttributeFilterInfo.builder()
            .attributeName(TestUtil.ATTRIBUTE_NAME)
            .attributeId(String.valueOf(1))
            .attributeFilterValues(List.of("EXPRESS"))
            .build();

    HolidayCutoffUIRequest holidayCutoffUIRequest =
        HolidayCutoffUIRequest.builder()
            .sourcingAttributesDefinitionId(1L)
            .requiredAttributes(List.of(attributeFilterInfo))
            .holidayCutoffNames(List.of(TestUtil.HOLIDAY_CUTOFF_NAME))
            .build();
    when(sourcingAttributesDefinitionService.processGetSourcingAttributesDefinitionByIdandOrgId(
            anyLong(), anyString()))
        .thenReturn(
            testUtil.getSourcingRuleAttributesDefinitionResponse(
                SourcingAttributesDefinitionStatus.ACTIVE));

    when(sourcingAttributePersistenceService.fetchSourcingAttributeListByOrgIdAndAttributeIds(
            any(), any()))
        .thenReturn(testUtil.getSourcingAttributeDomainDtos());

    when(holidayCutOffRepository.findFilteredHolidayCutoffRules(any(), any(), any(), any()))
        .thenReturn(holidayCutoffEntityPage);

    PageResponseForHolidayCutoff response =
        holidayCutoffService.getHolidayCutoffDetailsBasedOnFilters(
            TestUtil.ORG_ID,
            isPaginated,
            testUtil.getPageParams(
                Optional.of(1), Optional.of(10), Optional.of("ruleName"), Optional.of("ASC")),
            holidayCutoffUIRequest);

    verify(sourcingAttributesDefinitionService, times(1))
        .processGetSourcingAttributesDefinitionByIdandOrgId(anyLong(), any());
    verify(holidayCutOffRepository, times(1))
        .findFilteredHolidayCutoffRules(any(), any(), any(), any());
    assertNotNull(response.getData());
    assertEquals(
        testUtil.getSourcingAttributeEntity().getAttributeName(),
        response.getData().getColumns().get(2).getColumnName());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(1, response.getPagination().getTotalPages());
    assertEquals(
        testUtil.getHolidayCutoffEntity().getHolidayCutoffName(),
        response.getData().getRows().get(0).get(TestUtil.RULE_NAME_UI));
    assertEquals(
        testUtil.getHolidayCutoffEntity2().getHolidayCutoffName(),
        response.getData().getRows().get(1).get(TestUtil.RULE_NAME_UI));

    assertEquals("ASC", response.getPagination().getSortOrder());
  }
}
