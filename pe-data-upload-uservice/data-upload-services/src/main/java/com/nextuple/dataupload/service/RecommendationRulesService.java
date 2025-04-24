/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static com.nextuple.common.constants.CommonConstants.ORG_ID;
import static com.nextuple.common.constants.ConfigKeyConstants.ALLOWED_ATTRIBUTE_VALUES_FOR_TARGET_MARGINS_CONFIG_KEY;
import static com.nextuple.common.constants.ConfigKeyConstants.CAPPING_LOGIC_ENABLED_CONFIG_KEY;
import static com.nextuple.common.constants.ConfigKeyConstants.SELECTED_ATTRIBUTE_FOR_TARGET_MARGINS_CONFIG_KEY;
import static com.nextuple.common.constants.ConfigKeyConstants.SHIP_CHARGE_CAPPING_CONSTANTS_CONFIG_KEY;
import static com.nextuple.common.constants.ConfigKeyConstants.TARGET_GROSS_PROFIT_MARGINS_CONFIG_KEY;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.configuration.feign.ConfigurationFeign;
import com.nextuple.configuration.inbound.TenantConfigdataBaseRequest;
import com.nextuple.configuration.inbound.TenantConfigdataRequest;
import com.nextuple.configuration.outbound.TenantConfigdataResponse;
import com.nextuple.dataupload.common.inbound.ConfigureShipChargeCappingRequest;
import com.nextuple.dataupload.common.inbound.DeleteTargetProfitMarginRequest;
import com.nextuple.dataupload.common.inbound.TargetProfitMarginRequest;
import com.nextuple.dataupload.common.outbound.AttributeAndValuesTGMResponse;
import com.nextuple.dataupload.common.outbound.ConfigureShipChargeCappingResponse;
import com.nextuple.dataupload.common.outbound.ShipChargeDetailsTGMResponse;
import com.nextuple.dataupload.common.outbound.TargetProfitMarginResponse;
import com.nextuple.jobs.framework.common.utils.ExceptionUtils;
import feign.FeignException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationRulesService {

  private final ConfigurationFeign configurationFeign;

  public static final String ATTRIBUTE_NAME = "attributeName";
  public static final String ATTRIBUTE_VALUE = "attributeValue";
  private static final String CONFIGURE_TARGET_PROFIT_MARGIN_ERROR_MESSAGE =
      "Error while configuring target profit margin records";
  public static final String DELETE_TARGET_PROFIT_MARGIN_ERROR_MESSAGE =
      "Error while deleting target profit margin records";
  public static final String FETCH_TARGET_PROFIT_MARGIN_ERROR_MESSAGE =
      "Error while fetching target profit margin records";
  public static final String FETCH_ATTRIBUTE_AND_VALUES_ERROR_MESSAGE =
      "Error while fetching attribute and it's values";
  public static final String CONFIGURE_SHIP_CHARGE_CAPPING_ERROR_MESSAGE =
      "Error while configuring ship charge capping";
  public static final String UPDATE_SHIP_CHARGE_CAPPING_STATUS_ERROR_MESSAGE =
      "Error while updating ship charge capping status";
  public static final String TARGET_GROSS_PROFIT_MARGIN = "targetGrossProfitMargin";
  private static final String UPDATE_TARGET_PROFIT_MARGIN_ERROR_MESSAGE =
      "Error while updating target profit margin.";

  private String getConfigValue(String orgId, String configKey) {
    return configurationFeign
        .getTenantConfigdataByOrgIdAndConfigKey(orgId, configKey)
        .getPayload()
        .getConfigValue();
  }

  private void logFeignError(FeignException feignException, String operation) {
    var errorResponse = ExceptionUtils.parseFeignException(feignException);
    log.error("Feign exception while {}: {}", operation, errorResponse.getMessage());
  }

  private Map<String, Integer> extractConfigValuesToMap(String configValue) {
    return Arrays.stream(configValue.split(","))
        .map(s -> s.split(":"))
        .collect(Collectors.toMap(arr -> arr[0], arr -> Integer.parseInt(arr[1])));
  }

  private static String getUpdatedConfigValue(Map<String, Integer> configValueMap) {
    return configValueMap.entrySet().stream()
        .map(entry -> entry.getKey() + ":" + entry.getValue())
        .collect(Collectors.joining(","));
  }

  private static String getConfigKey(String attributeName) {
    return TARGET_GROSS_PROFIT_MARGINS_CONFIG_KEY + attributeName;
  }

  public TargetProfitMarginResponse createTargetProfitMargin(
      String orgId, TargetProfitMarginRequest targetProfitMarginRequest)
      throws CommonServiceException {
    String configKey = getConfigKey(targetProfitMarginRequest.getAttributeName());
    try {
      TenantConfigdataResponse tenantConfigdataResponse = null;
      tenantConfigdataResponse =
          getTenantConfigdataResponse(
              orgId, targetProfitMarginRequest, tenantConfigdataResponse, configKey);
      if (Objects.nonNull(tenantConfigdataResponse)) {
        String configValue = tenantConfigdataResponse.getConfigValue();
        Map<String, Integer> configValueMap = extractConfigValuesToMap(configValue);
        validateAttributeValueAlreadyExists(
            orgId, targetProfitMarginRequest, configValueMap, configKey);
        configValueMap.put(
            targetProfitMarginRequest.getAttributeValue(),
            targetProfitMarginRequest.getTargetGrossProfitMargin());
        String configValueUpdated = getUpdatedConfigValue(configValueMap);
        configurationFeign.updateTenantConfigdata(
            orgId,
            configKey,
            TenantConfigdataBaseRequest.builder().configValue(configValueUpdated).build());
      }
    } catch (FeignException exception) {
      log.error("{}: {}", CONFIGURE_TARGET_PROFIT_MARGIN_ERROR_MESSAGE, exception.getMessage());
      throw new CommonServiceException(
          CONFIGURE_TARGET_PROFIT_MARGIN_ERROR_MESSAGE,
          HttpStatus.PRECONDITION_FAILED,
          0xffff44,
          null);
    }
    return TargetProfitMarginResponse.builder()
        .orgId(orgId)
        .attributeValue(targetProfitMarginRequest.getAttributeValue())
        .attributeName(targetProfitMarginRequest.getAttributeName())
        .targetGrossProfitMargin(targetProfitMarginRequest.getTargetGrossProfitMargin())
        .build();
  }

  public TargetProfitMarginResponse updateTargetProfitGrossMargin(
      String orgId, TargetProfitMarginRequest targetProfitMarginRequest)
      throws CommonServiceException {
    String configKey = getConfigKey(targetProfitMarginRequest.getAttributeName());
    try {
      TenantConfigdataResponse tenantConfigdataResponse =
          configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(orgId, configKey).getPayload();
      String configValue = tenantConfigdataResponse.getConfigValue();
      Map<String, Integer> configValueMap = extractConfigValuesToMap(configValue);
      checkAttributeValueNotExists(orgId, targetProfitMarginRequest, configValueMap, configKey);
      configValueMap.put(
          targetProfitMarginRequest.getAttributeValue(),
          targetProfitMarginRequest.getTargetGrossProfitMargin());
      String configValueUpdated = getUpdatedConfigValue(configValueMap);
      configurationFeign.updateTenantConfigdata(
          orgId,
          TARGET_GROSS_PROFIT_MARGINS_CONFIG_KEY + targetProfitMarginRequest.getAttributeName(),
          TenantConfigdataBaseRequest.builder().configValue(configValueUpdated).build());
    } catch (FeignException exception) {
      log.error("{}: {}", UPDATE_TARGET_PROFIT_MARGIN_ERROR_MESSAGE, exception.getMessage());
      throw new CommonServiceException(
          UPDATE_TARGET_PROFIT_MARGIN_ERROR_MESSAGE, HttpStatus.BAD_REQUEST, 0xffff46, null);
    }
    return TargetProfitMarginResponse.builder()
        .orgId(orgId)
        .attributeValue(targetProfitMarginRequest.getAttributeValue())
        .attributeName(targetProfitMarginRequest.getAttributeName())
        .targetGrossProfitMargin(targetProfitMarginRequest.getTargetGrossProfitMargin())
        .build();
  }

  private TenantConfigdataResponse getTenantConfigdataResponse(
      String orgId,
      TargetProfitMarginRequest targetProfitMarginRequest,
      TenantConfigdataResponse tenantConfigdataResponse,
      String configKey) {
    try {
      tenantConfigdataResponse =
          configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(orgId, configKey).getPayload();
    } catch (FeignException e) {
      StringBuilder newConfigValue = new StringBuilder();
      newConfigValue.append(targetProfitMarginRequest.getAttributeValue());
      newConfigValue.append(":");
      newConfigValue.append(targetProfitMarginRequest.getTargetGrossProfitMargin());
      log.info(
          "No existing tenant config data found for orgId {}, configKey {}, configValue {}",
          orgId,
          configKey,
          newConfigValue);
      configurationFeign.addTenantConfigdata(
          TenantConfigdataRequest.builder()
              .orgId(orgId)
              .configKey(configKey)
              .configValue(newConfigValue.toString())
              .build());
    }
    return tenantConfigdataResponse;
  }

  private void validateAttributeValueAlreadyExists(
      String orgId,
      TargetProfitMarginRequest targetProfitMarginRequest,
      Map<String, Integer> configValueMap,
      String configKey)
      throws CommonServiceException {
    if (configValueMap.containsKey(targetProfitMarginRequest.getAttributeValue())) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(ATTRIBUTE_NAME, FieldError.builder().rejectedValue(configKey).build());
      errorMap.put(
          ATTRIBUTE_VALUE,
          FieldError.builder()
              .rejectedValue(targetProfitMarginRequest.getAttributeValue())
              .build());
      throw new CommonServiceException(
          "Target profit margin already configured for given orgId, attributeName and attributeValue.",
          HttpStatus.BAD_REQUEST,
          0x1774,
          errorMap);
    }
  }

  private static void checkAttributeValueNotExists(
      String orgId,
      TargetProfitMarginRequest targetProfitMarginRequest,
      Map<String, Integer> configValueMap,
      String configKey)
      throws CommonServiceException {
    if (!configValueMap.containsKey(targetProfitMarginRequest.getAttributeValue())) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(ATTRIBUTE_NAME, FieldError.builder().rejectedValue(configKey).build());
      errorMap.put(
          ATTRIBUTE_VALUE,
          FieldError.builder()
              .rejectedValue(targetProfitMarginRequest.getAttributeValue())
              .build());
      throw new CommonServiceException(
          "Target profit margin not configured for given orgId , attributeName and attributeValue.",
          HttpStatus.BAD_REQUEST,
          0x1776,
          errorMap);
    }
  }

  public void deleteTargetProfitMargin(String orgId, DeleteTargetProfitMarginRequest request)
      throws CommonServiceException {
    String configKey = getConfigKey(request.getAttributeName());
    try {
      String configValue = getConfigValue(orgId, configKey);
      Map<String, Integer> configValueMap = extractConfigValuesToMap(configValue);
      Set<String> attributeValues = Set.copyOf(request.getAttributeValues());
      configValueMap.keySet().removeAll(attributeValues);
      String configValueUpdated = getUpdatedConfigValue(configValueMap);
      if (StringUtils.hasLength(configValueUpdated)) {
        TenantConfigdataBaseRequest updateRequest =
            TenantConfigdataBaseRequest.builder().configValue(configValueUpdated).build();
        configurationFeign.updateTenantConfigdata(orgId, configKey, updateRequest);
      } else {
        configurationFeign.deleteTenantConfigdata(orgId, configKey);
      }
    } catch (FeignException feignException) {
      logFeignError(feignException, "deleting target profit margin records");
      throw new CommonServiceException(
          DELETE_TARGET_PROFIT_MARGIN_ERROR_MESSAGE, HttpStatus.BAD_REQUEST, 0xffff44, null);
    } catch (Exception exception) {
      log.error("{}: {}", DELETE_TARGET_PROFIT_MARGIN_ERROR_MESSAGE, exception.getMessage());
      throw new CommonServiceException(
          DELETE_TARGET_PROFIT_MARGIN_ERROR_MESSAGE, HttpStatus.BAD_REQUEST, 0xffff44, null);
    }
  }

  public List<TargetProfitMarginResponse> fetchTargetProfitMargin(
      String orgId, String attributeName) throws CommonServiceException {
    String configKey = getConfigKey(attributeName);
    try {
      TenantConfigdataResponse tenantConfigData =
          configurationFeign.getTenantConfigdataByOrgIdAndConfigKey(orgId, configKey).getPayload();
      Map<String, Integer> configValueMap =
          extractConfigValuesToMap(tenantConfigData.getConfigValue());
      return configValueMap.entrySet().stream()
          .map(
              entry ->
                  TargetProfitMarginResponse.builder()
                      .orgId(orgId)
                      .attributeName(attributeName)
                      .attributeValue(entry.getKey())
                      .targetGrossProfitMargin(entry.getValue())
                      .build())
          .toList();
    } catch (FeignException feignException) {
      logFeignError(feignException, "fetching target profit margin records");
      throw new CommonServiceException(
          FETCH_TARGET_PROFIT_MARGIN_ERROR_MESSAGE, HttpStatus.BAD_REQUEST, 0xffff45, null);
    } catch (Exception exception) {
      log.error("{}: {}", FETCH_TARGET_PROFIT_MARGIN_ERROR_MESSAGE, exception.getMessage());
      throw new CommonServiceException(
          FETCH_TARGET_PROFIT_MARGIN_ERROR_MESSAGE, HttpStatus.BAD_REQUEST, 0xffff45, null);
    }
  }

  public AttributeAndValuesTGMResponse fetchAttributeDetails(String orgId)
      throws CommonServiceException {
    try {
      String attributeName =
          getConfigValue(orgId, SELECTED_ATTRIBUTE_FOR_TARGET_MARGINS_CONFIG_KEY);
      String configKey = ALLOWED_ATTRIBUTE_VALUES_FOR_TARGET_MARGINS_CONFIG_KEY + attributeName;
      String attributeValues = getConfigValue(orgId, configKey);

      return AttributeAndValuesTGMResponse.builder()
          .orgId(orgId)
          .attributeName(attributeName)
          .attributeValues(Arrays.asList(attributeValues.split(",")))
          .build();
    } catch (FeignException feignException) {
      logFeignError(feignException, "fetching attribute details");
      throw new CommonServiceException(
          FETCH_ATTRIBUTE_AND_VALUES_ERROR_MESSAGE, HttpStatus.BAD_REQUEST, 0xffff47, null);
    }
  }

  public ShipChargeDetailsTGMResponse fetchShipChargeDetails(String orgId) {
    String attributeValues = fetchShipChargeCappingFromTenantConfig(orgId);
    List<Integer> shipChargeCappings =
        (attributeValues != null)
            ? Arrays.stream(attributeValues.split(",")).map(Integer::parseInt).toList()
            : Collections.emptyList();
    return ShipChargeDetailsTGMResponse.builder()
        .orgId(orgId)
        .shipChargeCappingConstantOne(fetchShipChargeCapping(0, shipChargeCappings))
        .shipChargeCappingConstantTwo(fetchShipChargeCapping(1, shipChargeCappings))
        .isShipChargeCappingLogicEnabled(fetchBooleanConfigValue(orgId))
        .build();
  }

  private String fetchShipChargeCappingFromTenantConfig(String orgId) {
    try {
      return configurationFeign
          .getTenantConfigdataByOrgIdAndConfigKey(orgId, SHIP_CHARGE_CAPPING_CONSTANTS_CONFIG_KEY)
          .getPayload()
          .getConfigValue();
    } catch (Exception e) {
      if (e.getMessage().contains("Tenant configuration data not found")) {
        return null;
      }
      throw e;
    }
  }

  private Integer fetchShipChargeCapping(int index, List<Integer> shipChargeCappings) {
    try {
      return shipChargeCappings.size() > index ? shipChargeCappings.get(index) : null;
    } catch (FeignException feignException) {
      logFeignError(feignException, "fetching ship charges capping");
      return null;
    }
  }

  private Boolean fetchBooleanConfigValue(String orgId) {
    try {
      return Boolean.valueOf(getConfigValue(orgId, CAPPING_LOGIC_ENABLED_CONFIG_KEY));
    } catch (FeignException feignException) {
      logFeignError(feignException, "fetching ship charges capping flag");
      return false;
    }
  }

  public ConfigureShipChargeCappingResponse configureShipChargeCapping(
      String orgId, ConfigureShipChargeCappingRequest request) throws CommonServiceException {
    String attributeValuesFromRequest =
        String.join(
            ",",
            String.valueOf(request.getShipChargeCappingConstantOne()),
            String.valueOf(request.getShipChargeCappingConstantTwo()));
    try {
      String prevAttributeValues = getConfigValue(orgId, SHIP_CHARGE_CAPPING_CONSTANTS_CONFIG_KEY);
      log.debug("Values before updating ship charge capping: {}", prevAttributeValues);
      upsertConfig(
          orgId,
          SHIP_CHARGE_CAPPING_CONSTANTS_CONFIG_KEY,
          attributeValuesFromRequest,
          true,
          CONFIGURE_SHIP_CHARGE_CAPPING_ERROR_MESSAGE);
    } catch (FeignException feignException) {
      logFeignError(feignException, "fetching ship charges capping, attempting to create");
      upsertConfig(
          orgId,
          SHIP_CHARGE_CAPPING_CONSTANTS_CONFIG_KEY,
          attributeValuesFromRequest,
          false,
          CONFIGURE_SHIP_CHARGE_CAPPING_ERROR_MESSAGE);
    }
    return ConfigureShipChargeCappingResponse.builder()
        .orgId(orgId)
        .shipChargeCappingConstantOne(request.getShipChargeCappingConstantOne())
        .shipChargeCappingConstantTwo(request.getShipChargeCappingConstantTwo())
        .build();
  }

  public void updateShipChargeCappingStatus(String orgId, Boolean isShipChargeCappingLogicEnabled)
      throws CommonServiceException {
    String configValue = String.valueOf(isShipChargeCappingLogicEnabled);
    try {
      String prevAttributeValues = getConfigValue(orgId, CAPPING_LOGIC_ENABLED_CONFIG_KEY);
      log.debug("Values before updating ship charge capping status: {}", prevAttributeValues);
      upsertConfig(
          orgId,
          CAPPING_LOGIC_ENABLED_CONFIG_KEY,
          configValue,
          true,
          UPDATE_SHIP_CHARGE_CAPPING_STATUS_ERROR_MESSAGE);
    } catch (FeignException feignException) {
      logFeignError(feignException, "fetching ship charges capping status, attempting to create");
      upsertConfig(
          orgId,
          CAPPING_LOGIC_ENABLED_CONFIG_KEY,
          configValue,
          false,
          UPDATE_SHIP_CHARGE_CAPPING_STATUS_ERROR_MESSAGE);
    }
  }

  private void upsertConfig(
      String orgId, String configKey, String configValue, boolean isUpdate, String exceptionMessage)
      throws CommonServiceException {
    try {
      if (isUpdate) {
        TenantConfigdataBaseRequest updateRequest =
            TenantConfigdataBaseRequest.builder().configValue(configValue).build();
        configurationFeign.updateTenantConfigdata(orgId, configKey, updateRequest);
      } else {
        TenantConfigdataRequest createRequest =
            TenantConfigdataRequest.builder()
                .orgId(orgId)
                .configKey(configKey)
                .configValue(configValue)
                .build();
        configurationFeign.addTenantConfigdata(createRequest);
      }
    } catch (FeignException feignException) {
      String action = isUpdate ? "updating" : "creating";
      logFeignError(feignException, action + " configuration for key: " + configKey);
      throw new CommonServiceException(exceptionMessage, HttpStatus.BAD_REQUEST, 0xffff48, null);
    }
  }
}
