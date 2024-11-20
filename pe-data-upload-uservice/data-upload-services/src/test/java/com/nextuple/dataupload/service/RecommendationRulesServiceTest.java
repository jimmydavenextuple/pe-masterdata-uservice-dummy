/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static com.nextuple.common.constants.ConfigKeyConstants.CAPPING_LOGIC_ENABLED_CONFIG_KEY;
import static com.nextuple.common.constants.ConfigKeyConstants.SELECTED_ATTRIBUTE_FOR_TARGET_MARGINS_CONFIG_KEY;
import static com.nextuple.common.constants.ConfigKeyConstants.SHIP_CHARGE_CAPPING_CONSTANTS_CONFIG_KEY;
import static com.nextuple.dataupload.service.RecommendationRulesService.DELETE_TARGET_PROFIT_MARGIN_ERROR_MESSAGE;
import static com.nextuple.dataupload.service.RecommendationRulesService.FETCH_ATTRIBUTE_AND_VALUES_ERROR_MESSAGE;
import static com.nextuple.dataupload.service.RecommendationRulesService.FETCH_TARGET_PROFIT_MARGIN_ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.configuration.feign.ConfigurationFeign;
import com.nextuple.dataupload.common.inbound.DeleteTargetProfitMarginRequest;
import com.nextuple.dataupload.common.outbound.TargetProfitMarginResponse;
import com.nextuple.dataupload.util.TestUtil;
import feign.FeignException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecommendationRulesServiceTest {

  @InjectMocks private RecommendationRulesService recommendationRulesService;

  @InjectMocks private TestUtil testUtil;

  @Mock private ConfigurationFeign configurationFeign;

  @Test
  @DisplayName("Happy Path: Creating a new tenant config")
  void createTargetProfitMarginConfigKeyDoesNotExist() throws CommonServiceException {
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenThrow(FeignException.class);
    when(configurationFeign.addTenantConfigdata(any()))
        .thenReturn(
            testUtil.getDummyTenantConfigData(
                TestUtil.ORG_ID,
                TestUtil.CONFIG_KEY_ITEM_CATEGORY,
                TestUtil.CONFIG_VALUE_ITEM_CATEGORY));
    TargetProfitMarginResponse expectedResponse = testUtil.getTargetProfitMarginResponse();
    TargetProfitMarginResponse targetProfitMarginResponse =
        recommendationRulesService.createTargetProfitMargin(
            TestUtil.ORG_ID, testUtil.getTargetProfitMarginRequest());

    Assertions.assertEquals(expectedResponse, targetProfitMarginResponse);
    verify(configurationFeign, times(1)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
    verify(configurationFeign, times(1)).addTenantConfigdata(any());
    verify(configurationFeign, times(0)).updateTenantConfigdata(any(), any(), any());
  }

  @Test
  @DisplayName("Adding new attribute value to an existing tenant config")
  void addNewAttributeValueConfigKeyExists() throws CommonServiceException {
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenReturn(
            testUtil.getDummyTenantConfigData(
                TestUtil.ORG_ID,
                TestUtil.CONFIG_KEY_ITEM_CATEGORY,
                TestUtil.CONFIG_VALUE_ITEM_CATEGORY));
    when(configurationFeign.updateTenantConfigdata(any(), any(), any()))
        .thenReturn(
            testUtil.getDummyTenantConfigData(
                TestUtil.ORG_ID,
                TestUtil.CONFIG_KEY_ITEM_CATEGORY,
                TestUtil.CONFIG_VALUE_ITEM_CATEGORY_UPDATED));
    TargetProfitMarginResponse expectedResponse = testUtil.getTargetProfitMarginResponse();
    TargetProfitMarginResponse targetProfitMarginResponse =
        recommendationRulesService.createTargetProfitMargin(
            TestUtil.ORG_ID, testUtil.getTargetProfitMarginRequest());
    Assertions.assertEquals(expectedResponse, targetProfitMarginResponse);
    verify(configurationFeign, times(1)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
    verify(configurationFeign, times(0)).addTenantConfigdata(any());
    verify(configurationFeign, times(1)).updateTenantConfigdata(any(), any(), any());
  }

  @Test
  @DisplayName("Adding new attribute value to an existing tenant config")
  void addingExistingAttributeValue() {
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenReturn(
            testUtil.getDummyTenantConfigData(
                TestUtil.ORG_ID,
                TestUtil.CONFIG_KEY_ITEM_CATEGORY,
                TestUtil.CONFIG_VALUE_ITEM_KITCHEN));
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> {
              recommendationRulesService.createTargetProfitMargin(
                  TestUtil.ORG_ID, testUtil.getTargetProfitMarginRequest());
            });
    assertTrue(
        exception
            .getMessage()
            .contains(
                "Target profit margin already configured for given orgId, attributeName and attributeValue."));
    verify(configurationFeign, times(1)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
    verify(configurationFeign, times(0)).addTenantConfigdata(any());
    verify(configurationFeign, times(0)).updateTenantConfigdata(any(), any(), any());
  }

  @Test
  @DisplayName("Exception while updating tenant config")
  void exceptionWhileUpdatingTenantConfig() {
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenReturn(
            testUtil.getDummyTenantConfigData(
                TestUtil.ORG_ID,
                TestUtil.CONFIG_KEY_ITEM_CATEGORY,
                TestUtil.CONFIG_VALUE_ITEM_KITCHEN));
    when(configurationFeign.updateTenantConfigdata(any(), any(), any()))
        .thenThrow(FeignException.class);
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> {
              recommendationRulesService.createTargetProfitMargin(
                  TestUtil.ORG_ID, testUtil.getTargetProfitMarginRequestElectronics());
            });
    assertTrue(
        exception.getMessage().contains("Error while configuring target profit margin records"));
    verify(configurationFeign, times(1)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
    verify(configurationFeign, times(0)).addTenantConfigdata(any());
    verify(configurationFeign, times(1)).updateTenantConfigdata(any(), any(), any());
  }

  @Test
  @DisplayName("Happy Path: Update the target profit margin value for existing attribute value")
  void updateTargetProfitMargin() throws CommonServiceException {
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenReturn(
            testUtil.getDummyTenantConfigData(
                TestUtil.ORG_ID,
                TestUtil.CONFIG_KEY_ITEM_CATEGORY,
                TestUtil.CONFIG_VALUE_ITEM_KITCHEN));
    when(configurationFeign.updateTenantConfigdata(any(), any(), any()))
        .thenReturn(
            testUtil.getDummyTenantConfigData(
                TestUtil.ORG_ID,
                TestUtil.CONFIG_KEY_ITEM_CATEGORY,
                TestUtil.CONFIG_VALUE_ITEM_CATEGORY_UPDATED_TARGET_MARGIN));
    TargetProfitMarginResponse expectedResponse = testUtil.getTargetProfitMarginResponse();
    TargetProfitMarginResponse targetProfitMarginResponse =
        recommendationRulesService.updateTargetProfitGrossMargin(
            TestUtil.ORG_ID, testUtil.getTargetProfitMarginRequest());
    Assertions.assertEquals(expectedResponse, targetProfitMarginResponse);
    verify(configurationFeign, times(1)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
    verify(configurationFeign, times(1)).updateTenantConfigdata(any(), any(), any());
  }

  @Test
  @DisplayName("Exception: Updating target profit gross margin for attribute which does not exists")
  void exceptionUpdateForAttributeNotPresent() {
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenReturn(
            testUtil.getDummyTenantConfigData(
                TestUtil.ORG_ID,
                TestUtil.CONFIG_KEY_ITEM_CATEGORY,
                TestUtil.CONFIG_VALUE_ITEM_KITCHEN));
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> {
              recommendationRulesService.updateTargetProfitGrossMargin(
                  TestUtil.ORG_ID, testUtil.getTargetProfitMarginRequestElectronics());
            });
    assertTrue(
        exception
            .getMessage()
            .contains(
                "Target profit margin not configured for given orgId , attributeName and attributeValue."));
    verify(configurationFeign, times(1)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
    verify(configurationFeign, times(0)).updateTenantConfigdata(any(), any(), any());
  }

  @Test
  @DisplayName(
      "Exception: Updating target profit gross margin for tenant config which does not exists.")
  void exceptionUpdateForTenantConfigNotPresent() {
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenThrow(FeignException.class);
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> {
              recommendationRulesService.updateTargetProfitGrossMargin(
                  TestUtil.ORG_ID, testUtil.getTargetProfitMarginRequestElectronics());
            });
    assertTrue(exception.getMessage().contains("Error while updating target profit margin."));
    verify(configurationFeign, times(1)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
    verify(configurationFeign, times(0)).updateTenantConfigdata(any(), any(), any());
  }

  @Test
  @DisplayName("Delete config when one or more attributes are left")
  void deleteTargetProfitMarginWhenAttributesLeft() throws CommonServiceException {
    var response =
        testUtil.getDummyTenantConfigData(
            TestUtil.ORG_ID,
            TestUtil.CONFIG_KEY_ITEM_CATEGORY,
            TestUtil.CONFIG_VALUE_ITEM_CATEGORY_UPDATED);
    DeleteTargetProfitMarginRequest request = testUtil.getDeleteTargetProfitMarginRequestSingle();

    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenReturn(response);

    recommendationRulesService.deleteTargetProfitMargin(TestUtil.ORG_ID, request);

    verify(configurationFeign, times(1)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
    verify(configurationFeign, times(0)).deleteTenantConfigdata(any(), any());
    verify(configurationFeign, times(1)).updateTenantConfigdata(any(), any(), any());
  }

  @Test
  @DisplayName("Delete config when no attributes are left")
  void deleteTargetProfitMarginWhenNoAttributesLeft() throws CommonServiceException {
    var response =
        testUtil.getDummyTenantConfigData(
            TestUtil.ORG_ID, TestUtil.CONFIG_KEY_ITEM_CATEGORY, TestUtil.CONFIG_VALUE_ITEM_KITCHEN);
    DeleteTargetProfitMarginRequest request = testUtil.getDeleteTargetProfitMarginRequestSingle();

    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenReturn(response);

    recommendationRulesService.deleteTargetProfitMargin(TestUtil.ORG_ID, request);

    verify(configurationFeign, times(1)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
    verify(configurationFeign, times(1)).deleteTenantConfigdata(any(), any());
    verify(configurationFeign, times(0)).updateTenantConfigdata(any(), any(), any());
  }

  @Test
  @DisplayName("Handle exception: FeignException when deleting target profit margin")
  void deleteTargetProfitMarginFeignException() {
    DeleteTargetProfitMarginRequest request = testUtil.getDeleteTargetProfitMarginRequest();

    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenThrow(FeignException.class);

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> recommendationRulesService.deleteTargetProfitMargin(TestUtil.ORG_ID, request));

    assertTrue(exception.getMessage().contains(DELETE_TARGET_PROFIT_MARGIN_ERROR_MESSAGE));

    verify(configurationFeign, times(1)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
    verify(configurationFeign, times(0)).updateTenantConfigdata(any(), any(), any());
    verify(configurationFeign, times(0)).deleteTenantConfigdata(any(), any());
  }

  @Test
  @DisplayName("Handle exception: General Exception while deleting target profit margin")
  void deleteTargetProfitMarginGeneralException() {
    DeleteTargetProfitMarginRequest request = testUtil.getDeleteTargetProfitMarginRequest();

    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenThrow(RuntimeException.class);

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> recommendationRulesService.deleteTargetProfitMargin(TestUtil.ORG_ID, request));

    assertTrue(exception.getMessage().contains(DELETE_TARGET_PROFIT_MARGIN_ERROR_MESSAGE));

    verify(configurationFeign, times(1)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
    verify(configurationFeign, times(0)).updateTenantConfigdata(any(), any(), any());
    verify(configurationFeign, times(0)).deleteTenantConfigdata(any(), any());
  }

  @Test
  @DisplayName("Fetch config: Happy Path")
  void fetchTargetProfitMarginTest() throws CommonServiceException {
    var tenantConfigDataResponse =
        testUtil.getDummyTenantConfigData(
            TestUtil.ORG_ID, TestUtil.CONFIG_KEY_ITEM_CATEGORY, TestUtil.CONFIG_VALUE_ITEM_KITCHEN);

    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenReturn(tenantConfigDataResponse);

    var response =
        recommendationRulesService.fetchTargetProfitMargin(
            TestUtil.ORG_ID, TestUtil.ATTRIBUTE_NAME_ITEM_CATEGORY);

    assertEquals(
        TestUtil.TARGET_GROSS_PROFIT_MARGIN, response.getFirst().getTargetGrossProfitMargin());
    assertEquals(TestUtil.ATTRIBUTE_VALUE_KITCHEN, response.getFirst().getAttributeValue());
    verify(configurationFeign, times(1)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
  }

  @Test
  @DisplayName("Handle exception: FeignException when fetching target profit margin")
  void fetchTargetProfitMarginFeignException() {
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenThrow(FeignException.class);

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                recommendationRulesService.fetchTargetProfitMargin(
                    TestUtil.ORG_ID, TestUtil.ATTRIBUTE_NAME_ITEM_CATEGORY));

    assertTrue(exception.getMessage().contains(FETCH_TARGET_PROFIT_MARGIN_ERROR_MESSAGE));

    verify(configurationFeign, times(1)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
  }

  @Test
  @DisplayName("Handle exception: General Exception while deleting target profit margin")
  void fetchTargetProfitMarginGeneralException() {
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenThrow(RuntimeException.class);

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                recommendationRulesService.fetchTargetProfitMargin(
                    TestUtil.ORG_ID, TestUtil.ATTRIBUTE_NAME_ITEM_CATEGORY));

    assertTrue(exception.getMessage().contains(FETCH_TARGET_PROFIT_MARGIN_ERROR_MESSAGE));

    verify(configurationFeign, times(1)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
  }

  @Test
  @DisplayName("Fetch attribute details: Happy Path")
  void fetchAttributeDetailsTest() throws CommonServiceException {
    var tenantConfigDataResponseForAttributeName =
        testUtil.getDummyTenantConfigData(
            TestUtil.ORG_ID,
            SELECTED_ATTRIBUTE_FOR_TARGET_MARGINS_CONFIG_KEY,
            TestUtil.CONFIG_KEY_ITEM_CATEGORY);

    var tenantConfigDataResponseForAttributeValues =
        testUtil.getDummyTenantConfigData(
            TestUtil.ORG_ID, TestUtil.CONFIG_KEY_ITEM_CATEGORY, TestUtil.ATTRIBUTE_VALUE_KITCHEN);

    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenReturn(
            tenantConfigDataResponseForAttributeName, tenantConfigDataResponseForAttributeValues);

    var response = recommendationRulesService.fetchAttributeDetails(TestUtil.ORG_ID);

    assertEquals(TestUtil.CONFIG_KEY_ITEM_CATEGORY, response.getAttributeName());
    assertEquals(List.of(TestUtil.ATTRIBUTE_VALUE_KITCHEN), response.getAttributeValues());
    verify(configurationFeign, times(2)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
  }

  @Test
  @DisplayName("Handle exception: FeignException when fetching attribute details")
  void fetchAttributeDetailsFeignException() {
    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenThrow(FeignException.class);

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> recommendationRulesService.fetchAttributeDetails(TestUtil.ORG_ID));

    assertTrue(exception.getMessage().contains(FETCH_ATTRIBUTE_AND_VALUES_ERROR_MESSAGE));

    verify(configurationFeign, times(1)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
  }

  @Test
  @DisplayName("Fetch ship charge details")
  void fetchShipChargeDetailsTest() {
    var tenantConfigDataResponseForCapping =
        testUtil.getDummyTenantConfigData(
            TestUtil.ORG_ID,
            SHIP_CHARGE_CAPPING_CONSTANTS_CONFIG_KEY,
            TestUtil.SHIP_CHARGE_CAPPING_CONSTANT);

    var tenantConfigDataResponseForStatus =
        testUtil.getDummyTenantConfigData(
            TestUtil.ORG_ID, CAPPING_LOGIC_ENABLED_CONFIG_KEY, TestUtil.SHIP_CHARGE_CAPPING_STATUS);

    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenReturn(tenantConfigDataResponseForCapping, tenantConfigDataResponseForStatus);

    var response = recommendationRulesService.fetchShipChargeDetails(TestUtil.ORG_ID);

    assertEquals(10, response.getShipChargeCappingConstantOne());
    assertEquals(20, response.getShipChargeCappingConstantTwo());
    assertEquals(true, response.getIsShipChargeCappingLogicEnabled());
    verify(configurationFeign, times(2)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
  }

  @Test
  @DisplayName("Configure ship charge capping: update")
  void configureShipChargeCappingUpdateTest() throws CommonServiceException {
    var request = testUtil.getConfigureShipChargeCappingRequest();
    var tenantConfigDataResponseForCapping =
        testUtil.getDummyTenantConfigData(
            TestUtil.ORG_ID,
            SHIP_CHARGE_CAPPING_CONSTANTS_CONFIG_KEY,
            TestUtil.SHIP_CHARGE_CAPPING_CONSTANT);

    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenReturn(tenantConfigDataResponseForCapping);
    when(configurationFeign.updateTenantConfigdata(any(), any(), any()))
        .thenReturn(tenantConfigDataResponseForCapping);

    var response = recommendationRulesService.configureShipChargeCapping(TestUtil.ORG_ID, request);

    assertEquals(10, response.getShipChargeCappingConstantOne());
    assertEquals(20, response.getShipChargeCappingConstantTwo());
    assertEquals(TestUtil.ORG_ID, response.getOrgId());
    verify(configurationFeign, times(1)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
    verify(configurationFeign, times(1)).updateTenantConfigdata(any(), any(), any());
  }

  @Test
  @DisplayName("Configure ship charge capping: create")
  void configureShipChargeCappingCreateTest() throws CommonServiceException {
    var request = testUtil.getConfigureShipChargeCappingRequest();
    var tenantConfigDataResponseForCapping =
        testUtil.getDummyTenantConfigData(
            TestUtil.ORG_ID,
            SHIP_CHARGE_CAPPING_CONSTANTS_CONFIG_KEY,
            TestUtil.SHIP_CHARGE_CAPPING_CONSTANT);

    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenThrow(FeignException.class);
    when(configurationFeign.addTenantConfigdata(any()))
        .thenReturn(tenantConfigDataResponseForCapping);

    var response = recommendationRulesService.configureShipChargeCapping(TestUtil.ORG_ID, request);

    assertEquals(10, response.getShipChargeCappingConstantOne());
    assertEquals(20, response.getShipChargeCappingConstantTwo());
    assertEquals(TestUtil.ORG_ID, response.getOrgId());
    verify(configurationFeign, times(1)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
    verify(configurationFeign, times(1)).addTenantConfigdata(any());
  }

  @Test
  @DisplayName("Update ship charge capping status: update")
  void updateShipChargeCappingStatusUpdateTest() throws CommonServiceException {
    var tenantConfigDataResponseForCapping =
        testUtil.getDummyTenantConfigData(
            TestUtil.ORG_ID, CAPPING_LOGIC_ENABLED_CONFIG_KEY, TestUtil.SHIP_CHARGE_CAPPING_STATUS);

    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenReturn(tenantConfigDataResponseForCapping);
    when(configurationFeign.updateTenantConfigdata(any(), any(), any()))
        .thenReturn(tenantConfigDataResponseForCapping);

    recommendationRulesService.updateShipChargeCappingStatus(
        TestUtil.ORG_ID, Boolean.valueOf(TestUtil.SHIP_CHARGE_CAPPING_STATUS));

    verify(configurationFeign, times(1)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
    verify(configurationFeign, times(1)).updateTenantConfigdata(any(), any(), any());
  }

  @Test
  @DisplayName("Update ship charge capping status: create")
  void updateShipChargeCappingStatusCreateTest() throws CommonServiceException {
    var tenantConfigDataResponseForCapping =
        testUtil.getDummyTenantConfigData(
            TestUtil.ORG_ID, CAPPING_LOGIC_ENABLED_CONFIG_KEY, TestUtil.SHIP_CHARGE_CAPPING_STATUS);

    when(configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(any(), any()))
        .thenThrow(FeignException.class);
    when(configurationFeign.addTenantConfigdata(any()))
        .thenReturn(tenantConfigDataResponseForCapping);

    recommendationRulesService.updateShipChargeCappingStatus(
        TestUtil.ORG_ID, Boolean.valueOf(TestUtil.SHIP_CHARGE_CAPPING_STATUS));

    verify(configurationFeign, times(1)).getTenantConfigdataByOrgIdAndConfigKey(any(), any());
    verify(configurationFeign, times(1)).addTenantConfigdata(any());
  }
}
