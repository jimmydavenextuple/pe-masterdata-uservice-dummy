/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.sourcing.cost.config.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.sourcing.cost.config.domain.entity.TenantCostTypeEntity;
import com.nextuple.sourcing.cost.config.domain.repository.SelectorAndCostItineraryMappingRepository;
import com.nextuple.sourcing.cost.config.domain.repository.TenantCostTypeRepository;
import com.nextuple.sourcing.cost.config.inbound.TenantCostTypeUpdateRequest;
import com.nextuple.sourcing.cost.config.outbound.TenantCostTypeResponse;
import com.nextuple.sourcing.cost.config.utils.TestUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

@DisplayName("TenantCostTypeService Test Cases")
class TenantCostTypeServiceTest {

  private static final String DISPLAY_NAME = "displayName";
  @Mock private TenantCostTypeRepository tenantCostTypeRepository;

  @InjectMocks private TenantCostTypeService tenantCostTypeService;
  @Mock private SelectorAndCostItineraryMappingRepository selectorAndCostItineraryMappingRepository;

  @InjectMocks private TestUtil testUtil;

  private static final String TENANT_COST_TYPE_EXCEPTION =
      "Tenant cost type not found with given details";
  private static final String TENANT_COST_TYPE_ASSOCIATION_EXCEPTION =
      "Tenant cost type associated with active cost itineraries. Remove those itineraries before performing this update.";
  private static final String ORG_ID = "orgId";
  private static final String ID = "id";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Test creating a Tenant Cost Type")
  void createTenantCostType() throws CommonServiceException {

    when(tenantCostTypeRepository.findByOrgIdAndCostType(
            TestUtil.ORG_ID, TestUtil.COST_TYPE_SHIPPING_COST))
        .thenReturn(Optional.empty());
    when(tenantCostTypeRepository.save(any())).thenReturn(testUtil.getTenantCostTypeEntity());
    TenantCostTypeResponse response =
        tenantCostTypeService.createTenantCostType(
            TestUtil.ORG_ID, testUtil.getTenantCostTypeRequest());
    assertNotNull(response);
    assertEquals(TestUtil.ID, response.getId());
    assertEquals(TestUtil.ORG_ID, response.getOrgId());
    assertEquals(TestUtil.COST_TYPE_SHIPPING_COST, response.getCostType());
  }

  @Test
  @DisplayName("Test creating a Tenant Cost Type - Conflict")
  void createTenantCostType_conflict() {
    when(tenantCostTypeRepository.findByOrgIdAndCostType(
            TestUtil.ORG_ID, TestUtil.COST_TYPE_SHIPPING_COST))
        .thenReturn(Optional.of(testUtil.getTenantCostTypeEntity()));
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                tenantCostTypeService.createTenantCostType(
                    TestUtil.ORG_ID, testUtil.getTenantCostTypeRequest()));
    assertEquals("Tenant cost type already exist for given orgId", exception.getMessage());
    assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
    assertEquals(0x1771, exception.getErrorCode());
    assertTrue(exception.getFieldInfo().containsKey(ORG_ID));
  }

  @Test
  @DisplayName("Test creating a Tenant Cost Type Display Name - Conflict")
  void createTenantCostTypeDisplayName_conflict() {
    when(tenantCostTypeRepository.findByOrgIdAndDisplayName(any(), any()))
        .thenReturn(List.of(testUtil.getTenantCostTypeEntity()));
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                tenantCostTypeService.createTenantCostType(
                    TestUtil.ORG_ID, testUtil.getTenantCostTypeRequest()));
    assertEquals(
        "Tenant cost type display name already exists for given orgId", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    assertEquals(0x1771, exception.getErrorCode());
    assertTrue(exception.getFieldInfo().containsKey(ORG_ID));
  }

  @Test
  @DisplayName("Test retrieving a Tenant Cost Type")
  void getTenantCostType() throws CommonServiceException {
    when(tenantCostTypeRepository.findByIdAndOrgId(TestUtil.ID, TestUtil.ORG_ID))
        .thenReturn(Optional.ofNullable(testUtil.getTenantCostTypeEntity()));
    TenantCostTypeResponse response =
        tenantCostTypeService.getTenantCostType(TestUtil.ORG_ID, TestUtil.ID);
    assertNotNull(response);
    assertEquals(TestUtil.ID, response.getId());
    assertEquals(TestUtil.ORG_ID, response.getOrgId());
    assertEquals(TestUtil.COST_TYPE_SHIPPING_COST, response.getCostType());
    assertEquals(TestUtil.DISPLAY_NAME, response.getDisplayName());

    verify(tenantCostTypeRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  @DisplayName("Test retrieving a Tenant Cost Type - Not Found")
  void getTenantCostType_NotFound() {
    when(tenantCostTypeRepository.findByIdAndOrgId(TestUtil.ID, TestUtil.ORG_ID))
        .thenReturn(Optional.empty());
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> tenantCostTypeService.getTenantCostType(TestUtil.ORG_ID, TestUtil.ID));
    assertEquals(TENANT_COST_TYPE_EXCEPTION, exception.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    assertEquals(0x1771, exception.getErrorCode());
    assertTrue(exception.getFieldInfo().containsKey(ID));
    assertTrue(exception.getFieldInfo().containsKey(ORG_ID));
  }

  @Test
  @DisplayName("Test retrieving a Tenant Cost Type by orgId")
  void getTenantCostTypeByOrgId() throws CommonServiceException {
    when(tenantCostTypeRepository.findByOrgId(TestUtil.ORG_ID))
        .thenReturn(List.of(testUtil.getTenantCostTypeEntity()));
    List<TenantCostTypeResponse> response =
        tenantCostTypeService.getTenantCostTypeByOrgId(TestUtil.ORG_ID);
    assertNotNull(response);
    assertEquals(1, response.size());
    assertEquals(TestUtil.ID, response.get(0).getId());
    assertEquals(TestUtil.ORG_ID, response.get(0).getOrgId());
    assertEquals(TestUtil.COST_TYPE_SHIPPING_COST, response.get(0).getCostType());
    assertEquals(TestUtil.DISPLAY_NAME, response.get(0).getDisplayName());

    verify(tenantCostTypeRepository, times(1)).findByOrgId(anyString());
  }

  @Test
  @DisplayName("Test retrieving a Tenant Cost Type by orgId - Not Found")
  void getTenantCostTypeByOrgId_NotFound() {
    when(tenantCostTypeRepository.findByOrgId(TestUtil.ORG_ID)).thenReturn(List.of());
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> tenantCostTypeService.getTenantCostTypeByOrgId(TestUtil.ORG_ID));
    assertEquals(TENANT_COST_TYPE_EXCEPTION, exception.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    assertEquals(0x1771, exception.getErrorCode());
    assertTrue(exception.getFieldInfo().containsKey(ORG_ID));
  }

  @Test
  @DisplayName("Test updating a Tenant Cost Type")
  void updateTenantCostType() throws CommonServiceException {
    String updateDisplayName = "Updated display name";
    TenantCostTypeUpdateRequest request =
        TenantCostTypeUpdateRequest.builder()
            .displayName(updateDisplayName)
            .customAttributes(JsonNodeFactory.instance.objectNode().put("key1", "value1"))
            .build();
    TenantCostTypeEntity entity = testUtil.getTenantCostTypeEntity();

    TenantCostTypeEntity updatedEntity = testUtil.getTenantCostTypeEntity();
    updatedEntity.setDisplayName(updateDisplayName);
    updatedEntity.setCustomAttributes(JsonNodeFactory.instance.objectNode().put("key1", "value1"));
    when(tenantCostTypeRepository.findByIdAndOrgId(TestUtil.ID, TestUtil.ORG_ID))
        .thenReturn(Optional.of(entity));
    when(tenantCostTypeRepository.save(any())).thenReturn(updatedEntity);
    TenantCostTypeResponse response =
        tenantCostTypeService.updateTenantCostType(TestUtil.ID, TestUtil.ORG_ID, request);
    assertNotNull(response);
    assertEquals(TestUtil.ID, response.getId());
    assertEquals(TestUtil.ORG_ID, response.getOrgId());
    assertEquals(request.getDisplayName(), response.getDisplayName());
    assertEquals(request.getCustomAttributes(), response.getCustomAttributes());
    verify(selectorAndCostItineraryMappingRepository, times((0)))
        .findByOrgIdAndCostType(any(), any());
  }

  @Test
  @DisplayName("Test updating a Tenant Cost Type - Not Found")
  void updateTenantCostType_NotFound() {

    TenantCostTypeUpdateRequest request = testUtil.getTenantCostTypeUpdateRequest();
    when(tenantCostTypeRepository.findByIdAndOrgId(TestUtil.ID, TestUtil.ORG_ID))
        .thenReturn(Optional.empty());
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                tenantCostTypeService.updateTenantCostType(TestUtil.ID, TestUtil.ORG_ID, request));
    assertEquals(TENANT_COST_TYPE_EXCEPTION, exception.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    assertEquals(0x1771, exception.getErrorCode());
    assertTrue(exception.getFieldInfo().containsKey(ID));
    assertTrue(exception.getFieldInfo().containsKey(ORG_ID));
  }

  @Test
  @DisplayName(
      "Test updating a Tenant Cost Type - combination of orgId and display name not unique")
  void updateTenantCostTypeOrgIdDisplayNameToBeUnique() {
    TenantCostTypeUpdateRequest request = testUtil.getTenantCostTypeUpdateRequest();
    when(tenantCostTypeRepository.findByIdAndOrgId(TestUtil.ID, TestUtil.ORG_ID))
        .thenReturn(Optional.of(testUtil.getTenantCostTypeEntity()));
    TenantCostTypeEntity tenantCostType = testUtil.getTenantCostTypeEntity();
    tenantCostType.setDisplayName("Ship cost");
    request.setDisplayName("Ship cost");
    when(tenantCostTypeRepository.findByOrgIdAndDisplayName(any(), any()))
        .thenReturn(List.of(tenantCostType));
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                tenantCostTypeService.updateTenantCostType(TestUtil.ID, TestUtil.ORG_ID, request));
    assertEquals(
        "Tenant cost type display name already exists for given orgId", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    assertEquals(0x1771, exception.getErrorCode());
    assertTrue(exception.getFieldInfo().containsKey(ORG_ID));
    assertTrue(exception.getFieldInfo().containsKey(DISPLAY_NAME));
  }

  @Test
  @DisplayName("Test updating a Tenant Cost Type - Display name same as before")
  void updateTenantCostTypeSameDisplayName() {
    TenantCostTypeUpdateRequest request = testUtil.getTenantCostTypeUpdateRequest();
    TenantCostTypeEntity tenantCostType = testUtil.getTenantCostTypeEntity();
    tenantCostType.setDisplayName("Ship cost");
    when(tenantCostTypeRepository.findByIdAndOrgId(TestUtil.ID, TestUtil.ORG_ID))
        .thenReturn(Optional.of(tenantCostType));
    request.setDisplayName("Ship cost");
    when(tenantCostTypeRepository.findByOrgIdAndDisplayName(any(), any()))
        .thenReturn(List.of(tenantCostType));
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                tenantCostTypeService.updateTenantCostType(TestUtil.ID, TestUtil.ORG_ID, request));
    assertEquals("Tenant cost type display name same as before", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    assertEquals(0x1771, exception.getErrorCode());
    assertTrue(exception.getFieldInfo().containsKey(ORG_ID));
    assertTrue(exception.getFieldInfo().containsKey(DISPLAY_NAME));
  }

  @Test
  @DisplayName("Delete tenant cost type - happy path")
  void deleteTenantCostTypeTest() throws CommonServiceException {
    when(tenantCostTypeRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantCostTypeEntity()));
    when(selectorAndCostItineraryMappingRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of());
    when(tenantCostTypeRepository.deleteByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantCostTypeEntity()));
    TenantCostTypeResponse response =
        tenantCostTypeService.deleteTenantCostType(1L, TestUtil.ORG_ID);
    assertNotNull(response);
    assertEquals(TestUtil.ID, response.getId());
    assertEquals(TestUtil.ORG_ID, response.getOrgId());
    assertEquals(TestUtil.COST_TYPE_SHIPPING_COST, response.getCostType());
    assertEquals(TestUtil.DISPLAY_NAME, response.getDisplayName());
  }

  @Test
  @DisplayName("Delete tenant cost type - when tenant cost type is not present in db")
  void deleteTenantCostTypeWhenEntityIsNotThereInDBTest() {
    when(tenantCostTypeRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> tenantCostTypeService.deleteTenantCostType(1L, TestUtil.ORG_ID));

    assertEquals("Tenant cost type not found with given details", ex.getMessage());
    verify(tenantCostTypeRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(tenantCostTypeRepository, times(0)).delete(any(TenantCostTypeEntity.class));
  }

  @Test
  @DisplayName("Delete tenant cost type - when cost type is associated with a itinerary")
  void deleteTenantCostTypeWhenCostTypeIsAssociatedTest() throws CommonServiceException {
    when(tenantCostTypeRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(testUtil.getTenantCostTypeEntity()));
    when(selectorAndCostItineraryMappingRepository.findByOrgIdAndCostType(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> tenantCostTypeService.deleteTenantCostType(1L, TestUtil.ORG_ID));

    assertEquals(
        "Tenant cost type associated with active cost itineraries. Remove those itineraries before performing this delete.",
        ex.getMessage());
    verify(tenantCostTypeRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findByOrgIdAndCostType(any(), any());
    verify(tenantCostTypeRepository, times(0)).delete(any(TenantCostTypeEntity.class));
  }

  @Test
  @DisplayName("Get cache keys for all tenant cost type")
  void getAllTenantCostTypeCacheKeysTest() {
    List<TenantCostTypeEntity> result = testUtil.getTenantCostTypeEntityList();
    when(tenantCostTypeRepository.findAllTenantCostTypeEntities(any())).thenReturn(result);
    Assertions.assertEquals(1, result.size());
  }
}
