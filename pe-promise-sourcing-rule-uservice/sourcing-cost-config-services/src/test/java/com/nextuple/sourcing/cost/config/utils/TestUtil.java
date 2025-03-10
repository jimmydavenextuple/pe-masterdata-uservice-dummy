/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.nextuple.promise.sourcing.rule.persistence.domain.NamedOptimizationStrategyDomainDto;
import com.nextuple.sourcing.cost.config.domain.entity.CostAttributeDetailsEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostAttributeMappingEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorAuditLogEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorBucketTypeEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorContiguousBucketEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorDiscreteBucketEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostItineraryAndFactorsEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostValueEntity;
import com.nextuple.sourcing.cost.config.domain.entity.OptimizationAndCostTypesMappingEntity;
import com.nextuple.sourcing.cost.config.domain.entity.PreferenceSelectorEntity;
import com.nextuple.sourcing.cost.config.domain.entity.SelectorAndCostItineraryMappingEntity;
import com.nextuple.sourcing.cost.config.domain.entity.TenantCostTypeEntity;
import com.nextuple.sourcing.cost.config.dto.CostAttributeDetailsCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostAttributeDto;
import com.nextuple.sourcing.cost.config.dto.CostAttributeMappingCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorBucketTypeCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorBucketTypeDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorContiguousBucketCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorContiguousBucketDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorDiscreteBucketCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorDiscreteBucketDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorHeadersInfoDto;
import com.nextuple.sourcing.cost.config.dto.CostItineraryAndFactorsCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostItineraryAndFactorsDto;
import com.nextuple.sourcing.cost.config.dto.CostValueCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.FilterCostFactorInfoDto;
import com.nextuple.sourcing.cost.config.dto.PreferenceSelectorCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.PreferenceSelectorDto;
import com.nextuple.sourcing.cost.config.dto.RateCardColumnsDto;
import com.nextuple.sourcing.cost.config.dto.RateCardRowsDto;
import com.nextuple.sourcing.cost.config.dto.SelectorAndCostItineraryMappingCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.SelectorCostFactorInfoDto;
import com.nextuple.sourcing.cost.config.dto.TenantCostTypeCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.CostFactorDescriptionDto;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.CostFactorUIValues;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.CostFactorValueDto;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.CostTypeDtoInfo;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.RowColumnDto;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.SelectorCfInfo;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.SelectorCfUIInfo;
import com.nextuple.sourcing.cost.config.enums.BucketTypeEnum;
import com.nextuple.sourcing.cost.config.enums.CostFactorTypeEnum;
import com.nextuple.sourcing.cost.config.enums.DataTypeEnum;
import com.nextuple.sourcing.cost.config.enums.ExpressionLibraryEnum;
import com.nextuple.sourcing.cost.config.enums.ItineraryStatusEnum;
import com.nextuple.sourcing.cost.config.enums.LabelEnum;
import com.nextuple.sourcing.cost.config.enums.LevelAppliedEnum;
import com.nextuple.sourcing.cost.config.enums.LookupContextEnum;
import com.nextuple.sourcing.cost.config.inbound.*;
import com.nextuple.sourcing.cost.config.outbound.CostAttributeMappingResponse;
import com.nextuple.sourcing.cost.config.outbound.CostDefinitionResponse;
import com.nextuple.sourcing.cost.config.outbound.CostTypeResponse;
import com.nextuple.sourcing.cost.config.outbound.CostTypeValidationResponse;
import com.nextuple.sourcing.cost.config.outbound.CostValueResponse;
import com.nextuple.sourcing.cost.config.outbound.ExpressionValidationResponse;
import com.nextuple.sourcing.cost.config.outbound.OptimizationAndCostTypesMappingResponse;
import com.nextuple.sourcing.cost.config.outbound.SelectorAndCostItineraryMappingResponse;
import com.nextuple.sourcing.cost.config.outbound.TenantCostTypeResponse;
import com.nextuple.sourcing.cost.config.outbound.UpdateRateCardStatusResponse;
import com.nextuple.sourcing.cost.config.pojo.*;
import com.nimbusds.oauth2.sdk.util.OrderedJSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class TestUtil {

  public static final Long COST_FACTOR_ITINERARY_ID = 1L;
  public static final String COST_ITINERARY = "SHIPPING_COST_UPSLIKE";

  public static final String ATTRIBUTE_VALUE = "attributeValue";

  public static final String FORMULA_VALIDATION_EXCEPTION =
      "Expecting numeric values for formula validation";

  public static final Long PREFERENCE_SELECTOR_ID = 1L;
  public static final Long COST_FACTOR_ID = 2L;
  public static final String ORG_ID = "NEXTUPLE";

  public static final String LOOKUP_CONTEXT = "lookupContext";

  public static final String ORG_ID_KEY = "orgId";
  public static final String COST_TABLE_ID = "costValueId";
  public static final String SELECTOR_CF = "carrierServiceId";
  public static final String INVALID_SELECTOR_CF = "BillWeightUps";
  public static final String COST_FACTOR = "BillWeightUps";
  public static final String COST_FACTOR_2 = "Zone";
  public static final String NOTATION = "M";
  public static final String NOTATION_DISPLAY_NAME = "Medium";
  public static final Double FROM_VALUE = 1.0;
  public static final Double TO_VALUE = 10.0;
  public static final String COST_TYPE_CAMEL_CASE = "shippingCost";
  private static final BucketTypeEnum DISCRETE_BUCKET_TYPE = BucketTypeEnum.DISCRETE;
  private static final BucketTypeEnum CONTIGUOUS_BUCKET_TYPE = BucketTypeEnum.CONTIGUOUS;
  public static final String FORMULA = "(l*b*h)/5000";
  public static final String DISPLAY_NAME = "SHIPPING_COST";
  public static final String DISPLAY_NAME_REVENUE = "SHIP_REVENUE";
  public static final String SELECTOR_CF_DISPLAY_NAME = "Carrier service Id";
  public static final String VALUES = "S,M,L";
  public static final String COST_FACTOR_VALUES = "UPS_GROUND,FEDEX_GROUND";
  public static final String VALUE_LIST = "Kitchen,Electronics,Garden";
  public static final String DEFAULT_VALUE = "S";
  public static final LevelAppliedEnum LEVEL_APPLIED = LevelAppliedEnum.SHIPMENT;
  public static final Long SELECTOR_ID = 1L;
  public static final String COST_TYPE = "SHIPPING_COST";
  public static final String COST_TYPE_DISPLAY_NAME = "Shipping Cost";
  public static final Long ID = 1L;
  public static final String SELECTOR_CF_VALUE = "UPS_GROUND";
  public static final String COST_ITINERARY_UPSLIKE = "SHIPPING_COST_UPSLIKE";
  public static final String COST_TYPE_SHIPPING_COST = "SHIPPING_COST ";
  public static final String COST_TYPE_SHIPPING_REVENUE = "SHIP_REVENUE ";
  public static final String CANONICAL_NAME = "itemLength";
  public static final String ATTRIBUTE_NAME = "length";
  public static final String CANONICAL_ATTRIBUTE_DISPLAY_NAME = "Item Length";
  public static final String DESCRIPTION = "Cost Desc";
  public static final String JAVA_CLASS_NAME = "com.org.impl.CostImpl";
  public static final String OPT_STRATEGY = "COST";
  public static final String OPT_STRATEGY_PROFIT = "PROFIT";

  public static final String COST_ITINERARY_KEY = "costItinerary";
  public static final String COST_TYPE_KEY = "costType";
  public static final String SELECTOR_CF_KEY = "selectorCf";
  public static final String UOM = "lbs";
  public static final String NODE_PROCESSING_COST = "NODE_PROCESSING_COST";
  public static final String DISPLAY_NAME_NODE_PROCESSING_COST1 = "Node Processing cost";
  public static final String BUYING_COST = "BUYING_COST";
  public static final String DISPLAY_NAME_BUYING_COST = "Buying Cost";
  public static final String ATTRIBUTE_PATH = "/length";
  public static final double COST_VALUE_WITH_OUT_PREV_SLB = 20.0;
  public static final String COST_FACTOR_COMBINATION_KEY = "UPS_GROUND|NON_HOLIDAYS|Z1|XXL";
  public static final String PREV_SLAB_VALUE = "UPS_GROUND|NON_HOLIDAYS|Z1|XL";
  public static final double COST_VALUE_WITH_PREV_SLB = 0.63;
  public static final String COST_TABLE_NOT_FOUND_EXCEPTION =
      "Cost value not found for given details";

  public static final String COST_ITINERARY_NOT_FOUND = "Cost itinerary not found for given orgId";
  public static final Boolean RATE_CARD_ACTIVE_TRUE = true;
  public static final Boolean RATE_CARD_ACTIVE_FALSE = false;
  public static final String SELECTOR_CF_VALUE_UPS_AIR = "UPS_AIR";
  public static final String FILTER_COST_FACTOR = "Surge";
  public static final String NON_HOLIDAYS = "NON_HOLIDAYS";
  public static final String FILTER_COST_FACTOR_VALUE = "NON_HOLIDAYS";
  public static final String COST_FACTOR_PREFIX_KEY = "UPS_GROUND|NON_HOLIDAYS";
  public static final String PIPE_DELIMITER = "|";

  public static final String COLUMN_NAME = "Shipping Cost";
  public static final String COLUMN_META = "billweightups";
  public static final String EXPRESSION_FORMULA = "width*width";

  public static final String COST_FACTOR_SURGE = "Surge";
  public static final String COST_FACTOR_BILL_WEIGHT_UPS = "billWeightUps";
  public static final String COST_FACTOR_ZONE = "zone";
  public static final String LIBRARY_NAME = "spel";
  public static final double EXPRESSION_VALUE = 100.0;
  public static final String SHIPPING_COST = "SHIPPING_COST";

  public static final String INVALID_LEVEL_APPLIED_EXCEPTION =
      "Level applied of cost factor is not valid";
  private static final JsonNode CUSTOM_ATTRIBUTES =
      JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2");

  public List<PreferenceSelectorCacheKeyDto> getPreferenceSelectorCacheKeys() {
    PreferenceSelectorCacheKeyDto preferenceSelectorCacheKeyDto1 =
        PreferenceSelectorCacheKeyDto.builder().orgId(ORG_ID).costType(COST_TYPE).build();

    PreferenceSelectorCacheKeyDto preferenceSelectorCacheKeyDto2 =
        PreferenceSelectorCacheKeyDto.builder()
            .orgId(ORG_ID)
            .costType(COST_TYPE_CAMEL_CASE)
            .build();

    return List.of(preferenceSelectorCacheKeyDto1, preferenceSelectorCacheKeyDto2);
  }

  public List<SelectorAndCostItineraryMappingCacheKeyDto> getSelectorAndCostItineraryCacheKeys() {
    SelectorAndCostItineraryMappingCacheKeyDto selectorAndCostItineraryMappingCacheKeyDto1 =
        SelectorAndCostItineraryMappingCacheKeyDto.builder()
            .orgId(ORG_ID)
            .costType(COST_TYPE)
            .selectorCf(SELECTOR_CF)
            .build();

    SelectorAndCostItineraryMappingCacheKeyDto selectorAndCostItineraryMappingCacheKeyDto2 =
        SelectorAndCostItineraryMappingCacheKeyDto.builder()
            .orgId(ORG_ID)
            .costType(COST_TYPE_CAMEL_CASE)
            .selectorCf(SELECTOR_CF)
            .build();

    return List.of(
        selectorAndCostItineraryMappingCacheKeyDto1, selectorAndCostItineraryMappingCacheKeyDto2);
  }

  public List<TenantCostTypeCacheKeyDto> getTenantCostTypeCacheKeys() {
    TenantCostTypeCacheKeyDto preferenceSelectorCacheKeyDto1 =
        TenantCostTypeCacheKeyDto.builder().orgId(ORG_ID).build();

    TenantCostTypeCacheKeyDto preferenceSelectorCacheKeyDto2 =
        TenantCostTypeCacheKeyDto.builder().orgId(ORG_ID).build();

    return List.of(preferenceSelectorCacheKeyDto1, preferenceSelectorCacheKeyDto2);
  }

  public List<CostAttributeDetailsCacheKeyDto> getCostAttributeDetailsCacheKeys() {
    CostAttributeDetailsCacheKeyDto costAttributeDetailsCacheKeyDto1 =
        CostAttributeDetailsCacheKeyDto.builder().attributeName(ATTRIBUTE_NAME).build();

    CostAttributeDetailsCacheKeyDto costAttributeDetailsCacheKeyDto2 =
        CostAttributeDetailsCacheKeyDto.builder().attributeName(ATTRIBUTE_NAME).build();

    return List.of(costAttributeDetailsCacheKeyDto1, costAttributeDetailsCacheKeyDto2);
  }

  public List<CostAttributeMappingCacheKeyDto> getCostAttributeMappingCacheKeys() {
    CostAttributeMappingCacheKeyDto costAttributeMappingCacheKeyDto1 =
        CostAttributeMappingCacheKeyDto.builder()
            .orgId(ORG_ID)
            .canonicalName(CANONICAL_NAME)
            .build();

    CostAttributeMappingCacheKeyDto costAttributeMappingCacheKeyDto2 =
        CostAttributeMappingCacheKeyDto.builder()
            .orgId(ORG_ID)
            .canonicalName(CANONICAL_NAME)
            .build();

    return List.of(costAttributeMappingCacheKeyDto1, costAttributeMappingCacheKeyDto2);
  }

  public List<PreferenceSelectorEntity> getPreferenceSelectorEntityList() {
    return Arrays.asList(getPreferenceSelectorEntity(), getPreferenceSelectorEntity2());
  }

  public List<SelectorAndCostItineraryMappingEntity> getSelectorAndCostItineraryEntityList() {
    return Arrays.asList(getSelectorAndCostItineraryEntityWith());
  }

  public List<PreferenceSelectorCacheKeyDto> getPreferenceSelectorCacheKeyList() {
    return Arrays.asList(getPreferenceCacheKeyEntity());
  }

  public PreferenceSelectorDto getPreferenceSelectorResponse() {
    return PreferenceSelectorDto.builder()
        .id(SELECTOR_ID)
        .orgId(ORG_ID)
        .selectorCf(SELECTOR_CF)
        .costType(COST_TYPE)
        .build();
  }

  public PreferenceSelectorEntity getPreferenceSelectorEntity() {
    PreferenceSelectorEntity preferenceSelectorEntity = new PreferenceSelectorEntity();
    preferenceSelectorEntity.setId(SELECTOR_ID);
    preferenceSelectorEntity.setOrgId(ORG_ID);
    preferenceSelectorEntity.setSelectorCf(SELECTOR_CF);
    preferenceSelectorEntity.setCostType(COST_TYPE);
    return preferenceSelectorEntity;
  }

  public PreferenceSelectorEntity getPreferenceSelectorEntity2() {
    PreferenceSelectorEntity preferenceSelectorEntity = new PreferenceSelectorEntity();
    preferenceSelectorEntity.setId(SELECTOR_ID);
    preferenceSelectorEntity.setOrgId(ORG_ID);
    preferenceSelectorEntity.setSelectorCf(SELECTOR_CF);
    preferenceSelectorEntity.setCostType(COST_TYPE_CAMEL_CASE);
    return preferenceSelectorEntity;
  }

  public PreferenceSelectorEntity getPreferenceSelectorEntity3() {
    PreferenceSelectorEntity preferenceSelectorEntity = new PreferenceSelectorEntity();
    preferenceSelectorEntity.setId(SELECTOR_ID);
    preferenceSelectorEntity.setOrgId(ORG_ID);
    preferenceSelectorEntity.setSelectorCf(INVALID_SELECTOR_CF);
    preferenceSelectorEntity.setCostType(COST_TYPE_CAMEL_CASE);
    return preferenceSelectorEntity;
  }

  public PreferenceSelectorCacheKeyDto getPreferenceCacheKeyEntity() {
    return PreferenceSelectorCacheKeyDto.builder().orgId(ORG_ID).costType(COST_TYPE).build();
  }

  public CreatePreferenceSelectorRequest getUpsertPreferenceSelectorRequest() {
    return CreatePreferenceSelectorRequest.builder()
        .selectorCf(SELECTOR_CF)
        .costType(COST_TYPE)
        .build();
  }

  public UpdatePreferenceSelectorRequest getUpdatePreferenceSelectorRequest() {
    return UpdatePreferenceSelectorRequest.builder().selectorCf(SELECTOR_CF).build();
  }

  public CostFactorEntity getCostFactorEntity() {
    CostFactorEntity costFactorEntity = new CostFactorEntity();
    costFactorEntity.setId(PREFERENCE_SELECTOR_ID);
    costFactorEntity.setOrgId(ORG_ID);
    costFactorEntity.setCostFactor(COST_FACTOR);
    costFactorEntity.setFormula(FORMULA);
    costFactorEntity.setDisplayName(COST_TYPE_DISPLAY_NAME);
    costFactorEntity.setValues(VALUES);
    costFactorEntity.setDefaultValue(DEFAULT_VALUE);
    costFactorEntity.setLevelApplied(LEVEL_APPLIED);
    costFactorEntity.setUom(UOM);
    costFactorEntity.setDataType(DataTypeEnum.NUMBER);
    costFactorEntity.setIsBucketed(true);
    costFactorEntity.setIsRateCardLookUpRequired(true);
    return costFactorEntity;
  }

  public CostFactorEntity getCostFactorEntityForRateCardStatus() {
    CostFactorEntity costFactorEntity = new CostFactorEntity();
    costFactorEntity.setId(PREFERENCE_SELECTOR_ID);
    costFactorEntity.setOrgId(ORG_ID);
    costFactorEntity.setCostFactor("carrierServiceId");
    costFactorEntity.setFormula("carrierServiceId");
    costFactorEntity.setDisplayName(COST_TYPE_DISPLAY_NAME);
    costFactorEntity.setValues(COST_FACTOR_VALUES);
    costFactorEntity.setDefaultValue("UPS_GROUND");
    costFactorEntity.setLevelApplied(LEVEL_APPLIED);
    costFactorEntity.setUom(UOM);
    costFactorEntity.setDataType(DataTypeEnum.NUMBER);
    costFactorEntity.setIsBucketed(true);
    return costFactorEntity;
  }

  public CostFactorEntity getCostFactorEntityForCostFactorValidation() {
    CostFactorEntity costFactorEntity = new CostFactorEntity();
    costFactorEntity.setId(PREFERENCE_SELECTOR_ID);
    costFactorEntity.setOrgId(ORG_ID);
    costFactorEntity.setCostFactor(COST_FACTOR_SURGE);
    costFactorEntity.setFormula(FORMULA);
    costFactorEntity.setDisplayName(COST_TYPE_DISPLAY_NAME);
    costFactorEntity.setValues(NON_HOLIDAYS);
    costFactorEntity.setDefaultValue(DEFAULT_VALUE);
    costFactorEntity.setLevelApplied(LEVEL_APPLIED);
    costFactorEntity.setUom(UOM);
    costFactorEntity.setDataType(DataTypeEnum.NUMBER);
    costFactorEntity.setIsBucketed(true);
    return costFactorEntity;
  }

  public CostFactorEntity getCostFactorEntityForSelectorCF() {
    CostFactorEntity costFactorEntity = new CostFactorEntity();
    costFactorEntity.setId(PREFERENCE_SELECTOR_ID);
    costFactorEntity.setOrgId(ORG_ID);
    costFactorEntity.setCostFactor(SELECTOR_CF);
    costFactorEntity.setFormula(SELECTOR_CF);
    costFactorEntity.setDisplayName(SELECTOR_CF_DISPLAY_NAME);
    costFactorEntity.setValues(SELECTOR_CF_VALUE);
    costFactorEntity.setDefaultValue(SELECTOR_CF_VALUE);
    costFactorEntity.setLevelApplied(LEVEL_APPLIED);
    costFactorEntity.setUom(UOM);
    costFactorEntity.setIsRateCardLookUpRequired(false);
    return costFactorEntity;
  }

  public CostFactorDto getCostFactorResponse() {
    return CostFactorDto.builder()
        .id(COST_FACTOR_ID)
        .orgId(ORG_ID)
        .costFactor(SELECTOR_CF)
        .dataType(DataTypeEnum.NUMBER)
        .formula(FORMULA)
        .displayName(DISPLAY_NAME)
        .values(VALUES)
        .defaultValue(DEFAULT_VALUE)
        .levelApplied(LEVEL_APPLIED)
        .uom(UOM)
        .isBucketed(true)
        .build();
  }

  public CostFactorRequest getCreateCostFactorRequest() {
    return CostFactorRequest.builder()
        .costFactor(SELECTOR_CF)
        .dataType(DataTypeEnum.NUMBER)
        .formula(FORMULA)
        .displayName(DISPLAY_NAME)
        .values(VALUES)
        .defaultValue(DEFAULT_VALUE)
        .levelApplied(LEVEL_APPLIED)
        .costFactorType(CostFactorTypeEnum.DERIVED)
        .library(ExpressionLibraryEnum.PARSII)
        .uom(UOM)
        .isBucketed(true)
        .isRateCardLookUpRequired(true)
        .build();
  }

  public CostFactorUpdateRequest getCostFactorUpdationRequest() {
    return CostFactorUpdateRequest.builder()
        .displayName(DISPLAY_NAME)
        .defaultValue(DEFAULT_VALUE)
        .uom(UOM)
        .build();
  }

  public CostFactorAuditLogEntity getCostFactorAuditLogEntity() {
    CostFactorAuditLogEntity costFactorAuditLogEntity = new CostFactorAuditLogEntity();
    costFactorAuditLogEntity.setCostFactor(SELECTOR_CF);
    costFactorAuditLogEntity.setDataType(DataTypeEnum.NUMBER);
    costFactorAuditLogEntity.setFormula(FORMULA);
    costFactorAuditLogEntity.setDisplayName(DISPLAY_NAME);
    costFactorAuditLogEntity.setValues(VALUES);
    costFactorAuditLogEntity.setDefaultValue(DEFAULT_VALUE);
    costFactorAuditLogEntity.setLevelApplied(LEVEL_APPLIED);
    costFactorAuditLogEntity.setCostFactorType(CostFactorTypeEnum.DERIVED);
    costFactorAuditLogEntity.setTimestamp(new Date());
    costFactorAuditLogEntity.setUom(UOM);
    return costFactorAuditLogEntity;
  }

  public CostItineraryAndFactorsEntity getCostItineraryAndFactorsEntity() {
    CostItineraryAndFactorsEntity costItineraryAndFactorsEntity =
        new CostItineraryAndFactorsEntity();
    costItineraryAndFactorsEntity.setId(COST_FACTOR_ITINERARY_ID);
    costItineraryAndFactorsEntity.setOrgId(ORG_ID);
    costItineraryAndFactorsEntity.setCostItinerary(COST_ITINERARY);
    costItineraryAndFactorsEntity.setCostFactors(COST_FACTOR);
    costItineraryAndFactorsEntity.setItineraryStatus(ItineraryStatusEnum.DRAFT);
    costItineraryAndFactorsEntity.setIsActive(Boolean.TRUE);
    costItineraryAndFactorsEntity.setLevelApplied(LEVEL_APPLIED);
    return costItineraryAndFactorsEntity;
  }

  public CostItineraryAndFactorsEntity getCreatedCostItineraryAndFactorsEntity() {
    CostItineraryAndFactorsEntity costItineraryAndFactorsEntity =
        new CostItineraryAndFactorsEntity();
    costItineraryAndFactorsEntity.setId(COST_FACTOR_ITINERARY_ID);
    costItineraryAndFactorsEntity.setOrgId(ORG_ID);
    costItineraryAndFactorsEntity.setCostItinerary(COST_ITINERARY);
    costItineraryAndFactorsEntity.setCostFactors(
        SELECTOR_CF + "," + COST_FACTOR + "," + COST_FACTOR + "," + COST_FACTOR);
    costItineraryAndFactorsEntity.setItineraryStatus(ItineraryStatusEnum.CREATED);
    costItineraryAndFactorsEntity.setLevelApplied(LEVEL_APPLIED);

    return costItineraryAndFactorsEntity;
  }

  public CostItineraryAndFactorsDto getCostItineraryAndFactorsResponse() {
    return CostItineraryAndFactorsDto.builder()
        .id(COST_FACTOR_ID)
        .orgId(ORG_ID)
        .costItinerary(COST_ITINERARY)
        .costFactors(COST_FACTOR)
        .itineraryStatus(ItineraryStatusEnum.DRAFT)
        .levelApplied(LEVEL_APPLIED)
        .build();
  }

  public CostItineraryAndFactorsRequest getUpsertCostItineraryAndFactorsRequest() {
    return CostItineraryAndFactorsRequest.builder()
        .costItinerary(COST_ITINERARY)
        .costFactors(COST_FACTOR)
        .levelApplied(LEVEL_APPLIED)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public CostItineraryAndFactorsStatusRequest getUpsertCostItineraryAndFactorsStatusRequest() {
    return CostItineraryAndFactorsStatusRequest.builder()
        .itineraryStatus(ItineraryStatusEnum.DRAFT)
        .build();
  }

  public TenantCostTypeRequest getTenantCostTypeRequest() {

    return TenantCostTypeRequest.builder()
        .costType(COST_TYPE_SHIPPING_COST)
        .displayName(DISPLAY_NAME)
        .label(LabelEnum.COST)
        .build();
  }

  public TenantCostTypeUpdateRequest getTenantCostTypeUpdateRequest() {

    return TenantCostTypeUpdateRequest.builder().displayName(DISPLAY_NAME).build();
  }

  public TenantCostTypeResponse getTenantCostTypeResponse() {

    return TenantCostTypeResponse.builder()
        .id(ID)
        .orgId(ORG_ID)
        .costType(COST_TYPE_SHIPPING_COST)
        .displayName(DISPLAY_NAME)
        .label(LabelEnum.COST)
        .build();
  }

  public TenantCostTypeEntity getTenantCostTypeEntity() {
    return new TenantCostTypeEntity(
        ID, ORG_ID, COST_TYPE_SHIPPING_COST, DISPLAY_NAME, LabelEnum.COST);
  }

  public List<TenantCostTypeEntity> getTenantCostTypeEntityList() {
    return Arrays.asList(
        new TenantCostTypeEntity(
            ID, ORG_ID, COST_TYPE_SHIPPING_COST, DISPLAY_NAME, LabelEnum.COST));
  }

  public List<TenantCostTypeEntity> getTenantCostTypeEntityListWithCostAndRevenue() {
    return Arrays.asList(
        new TenantCostTypeEntity(ID, ORG_ID, COST_TYPE_SHIPPING_COST, DISPLAY_NAME, LabelEnum.COST),
        new TenantCostTypeEntity(
            ID + 1, ORG_ID, COST_TYPE_SHIPPING_REVENUE, DISPLAY_NAME_REVENUE, LabelEnum.REVENUE));
  }

  public SelectorAndCostItineraryMappingRequest getSelectorAndCostItineraryMappingRequest() {

    return SelectorAndCostItineraryMappingRequest.builder()
        .costItinerary(COST_ITINERARY_UPSLIKE)
        .costType(COST_TYPE_SHIPPING_COST)
        .selectorCfValue(SELECTOR_CF_VALUE)
        .selectorCf(SELECTOR_CF)
        .build();
  }

  public UpdateSelectorAndCostItineraryMappingRequest
      getUpdateSelectorAndCostItineraryMappingRequest() {

    return UpdateSelectorAndCostItineraryMappingRequest.builder()
        .costItinerary(COST_ITINERARY_UPSLIKE)
        .build();
  }

  public SelectorAndCostItineraryMappingRequest
      getSelectorAndCostItineraryMappingRequestWithNullSelectorCfValue() {

    return SelectorAndCostItineraryMappingRequest.builder()
        .costItinerary(COST_ITINERARY_UPSLIKE)
        .costType(COST_TYPE_SHIPPING_COST)
        .selectorCfValue(null)
        .selectorCf(SELECTOR_CF)
        .build();
  }

  public SelectorAndCostItineraryMappingResponse getSelectorAndCostItineraryMappingResponse() {
    return SelectorAndCostItineraryMappingResponse.builder()
        .id(ID)
        .orgId(ORG_ID)
        .selectorCf(SELECTOR_CF)
        .selectorCfValue(SELECTOR_CF_VALUE)
        .costItinerary(COST_ITINERARY_UPSLIKE)
        .costType(COST_TYPE_SHIPPING_COST)
        .build();
  }

  public SelectorAndCostItineraryMappingEntity getSelectorAndCostItineraryMappingEntity() {
    return new SelectorAndCostItineraryMappingEntity(
        ID,
        ORG_ID,
        SELECTOR_CF,
        SELECTOR_CF_VALUE,
        COST_ITINERARY_UPSLIKE,
        COST_TYPE_SHIPPING_COST);
  }

  public CostAttributeMappingRequest getCostAttributeMappingRequest() {

    return CostAttributeMappingRequest.builder()
        .canonicalName(CANONICAL_NAME)
        .displayName(CANONICAL_ATTRIBUTE_DISPLAY_NAME)
        .attributeName(ATTRIBUTE_NAME)
        .build();
  }

  public CostAttributeMappingResponse getCostAttributeMappingResponse() {

    return CostAttributeMappingResponse.builder()
        .id(ID)
        .orgId(ORG_ID)
        .canonicalName(CANONICAL_NAME)
        .displayName(CANONICAL_ATTRIBUTE_DISPLAY_NAME)
        .attributeName(ATTRIBUTE_NAME)
        .build();
  }

  public CostAttributeMappingEntity getCostAttributeMappingEntity() {

    return new CostAttributeMappingEntity(
        ID, ORG_ID, CANONICAL_NAME, CANONICAL_ATTRIBUTE_DISPLAY_NAME, ATTRIBUTE_NAME);
  }

  public List<CostAttributeMappingEntity> getCostAttributeMappingEntityList() {
    return Arrays.asList(getCostAttributeMappingEntity());
  }

  public CostAttributeDetailsEntity getCostAttributeDetailsEntity() {
    CostAttributeDetailsEntity costAttributeDetailsEntity = new CostAttributeDetailsEntity();
    costAttributeDetailsEntity.setId(ID);
    costAttributeDetailsEntity.setAttributeName(ATTRIBUTE_NAME);
    costAttributeDetailsEntity.setPath(ATTRIBUTE_PATH);
    costAttributeDetailsEntity.setDisplayName(ATTRIBUTE_NAME);
    costAttributeDetailsEntity.setIsPublished(Boolean.TRUE);
    costAttributeDetailsEntity.setLookupContext(LookupContextEnum.SOLUTION);
    return costAttributeDetailsEntity;
  }

  public List<CostAttributeDetailsEntity> getCostAttributeDetailsEntityList() {
    return Arrays.asList(getCostAttributeDetailsEntity());
  }

  public CostAttributeRequest getCostAttributeRequest() {

    return CostAttributeRequest.builder()
        .attributeName(ATTRIBUTE_NAME)
        .path(ATTRIBUTE_PATH)
        .displayName(ATTRIBUTE_NAME)
        .isPublished(Boolean.TRUE)
        .lookupContext(LookupContextEnum.SOLUTION)
        .build();
  }

  public CostAttributeDto getCostAttributeDto() {

    return CostAttributeDto.builder()
        .attributeName(ATTRIBUTE_NAME)
        .path(ATTRIBUTE_PATH)
        .displayName(ATTRIBUTE_NAME)
        .isPublished(Boolean.TRUE)
        .lookupContext(LookupContextEnum.SOLUTION)
        .build();
  }

  public CostAttributeUpdateRequest getCostAttributeUpdateRequest() {

    return CostAttributeUpdateRequest.builder()
        .path(ATTRIBUTE_PATH)
        .displayName(ATTRIBUTE_NAME)
        .isPublished(Boolean.TRUE)
        .lookupContext(LookupContextEnum.SOLUTION)
        .build();
  }

  public CostValueResponse getCostValueResponse(Boolean withPrevSlab) {
    CostValueResponse.CostValueResponseBuilder costValueResponseBuilder =
        CostValueResponse.builder()
            .id(ID)
            .orgId(ORG_ID)
            .costValue(COST_VALUE_WITH_OUT_PREV_SLB)
            .costItinerary(COST_ITINERARY)
            .customAttributes(CUSTOM_ATTRIBUTES)
            .costFactorCombinationKey(COST_FACTOR_COMBINATION_KEY);
    if (Boolean.TRUE.equals(withPrevSlab)) {
      costValueResponseBuilder.costValue(COST_VALUE_WITH_PREV_SLB);
      costValueResponseBuilder.prevSlabValue(PREV_SLAB_VALUE);
    }
    return costValueResponseBuilder.build();
  }

  public CreateCostValueRequest getCreateCostValueRequest(Boolean withPrevSlab) {
    CreateCostValueRequest.CreateCostValueRequestBuilder createCostValueRequestBuilder =
        CreateCostValueRequest.builder()
            .costValue(COST_VALUE_WITH_OUT_PREV_SLB)
            .costItinerary(COST_ITINERARY)
            .customAttributes(CUSTOM_ATTRIBUTES)
            .costFactorCombinationKey(COST_FACTOR_COMBINATION_KEY);
    if (Boolean.TRUE.equals(withPrevSlab)) {
      createCostValueRequestBuilder.costValue(COST_VALUE_WITH_PREV_SLB);
      createCostValueRequestBuilder.prevSlabValue(PREV_SLAB_VALUE);
    }
    return createCostValueRequestBuilder.build();
  }

  public UpdateCostValueRequest getUpdateCostValueRequest(Boolean withPrevSlab) {

    UpdateCostValueRequest.UpdateCostValueRequestBuilder updateCostValueRequestBuilder =
        UpdateCostValueRequest.builder()
            .customAttributes(CUSTOM_ATTRIBUTES)
            .costValue(COST_VALUE_WITH_OUT_PREV_SLB);
    if (Boolean.TRUE.equals(withPrevSlab)) {
      updateCostValueRequestBuilder.costValue(COST_VALUE_WITH_PREV_SLB);
      updateCostValueRequestBuilder.prevSlabValue(PREV_SLAB_VALUE);
    }
    return updateCostValueRequestBuilder.build();
  }

  public CostValueEntity getCostValueEntity(Boolean withPrevSlab) {
    if (Boolean.TRUE.equals(withPrevSlab)) {

      return new CostValueEntity(
          ID,
          ORG_ID,
          COST_ITINERARY,
          COST_FACTOR_COMBINATION_KEY,
          COST_VALUE_WITH_PREV_SLB,
          PREV_SLAB_VALUE);
    }
    return new CostValueEntity(
        ID,
        ORG_ID,
        COST_ITINERARY,
        COST_FACTOR_COMBINATION_KEY,
        COST_VALUE_WITH_OUT_PREV_SLB,
        null);
  }

  public UpdateRateCardStatusRequest getUpdateRateCardStatusRequest() {
    SelectorInfo selector = new SelectorInfo(SELECTOR_CF, SELECTOR_CF_VALUE);
    return UpdateRateCardStatusRequest.builder()
        .costType(COST_TYPE)
        .selector(selector)
        .isRateCardActive(RATE_CARD_ACTIVE_TRUE)
        .build();
  }

  public UpdateRateCardStatusRequest getUpdateRateCardStatusSelectorNullValuesRequest() {
    SelectorInfo selector = new SelectorInfo(null, null);
    return UpdateRateCardStatusRequest.builder()
        .costType(COST_TYPE)
        .selector(selector)
        .isRateCardActive(RATE_CARD_ACTIVE_TRUE)
        .build();
  }

  public UpdateRateCardStatusRequest getUpdateRateCardStatusSelectorCfIsnullRequest() {
    SelectorInfo selector = new SelectorInfo(null, SELECTOR_CF_VALUE);
    return UpdateRateCardStatusRequest.builder()
        .costType(COST_TYPE)
        .selector(selector)
        .isRateCardActive(RATE_CARD_ACTIVE_TRUE)
        .build();
  }

  public UpdateRateCardStatusResponse getUpdateRateCardStatusResponse() {
    SelectorInfo selector = new SelectorInfo(SELECTOR_CF, SELECTOR_CF_VALUE);
    return UpdateRateCardStatusResponse.builder()
        .costType(COST_TYPE)
        .selector(selector)
        .isRateCardActive(RATE_CARD_ACTIVE_TRUE)
        .build();
  }

  public SelectorAndCostItineraryMappingEntity
      getSelectorAndCostItineraryEntityToUpdateRateCardStatus() {
    return new SelectorAndCostItineraryMappingEntity(
        ID, ORG_ID, SELECTOR_CF, SELECTOR_CF_VALUE, COST_ITINERARY_UPSLIKE, COST_TYPE);
  }

  public UpdateRateCardStatusRequest
      getUpdateRateCardStatusRequestWithEmptySelectorCfAndStatusIsTrue() {
    SelectorInfo selector = new SelectorInfo("", SELECTOR_CF_VALUE);
    return UpdateRateCardStatusRequest.builder()
        .costType(COST_TYPE)
        .selector(selector)
        .isRateCardActive(RATE_CARD_ACTIVE_TRUE)
        .build();
  }

  public UpdateRateCardStatusRequest getUpdateRateCardStatusRequestWithEmptySelectorCf() {
    SelectorInfo selector = new SelectorInfo(SELECTOR_CF, "");
    return UpdateRateCardStatusRequest.builder()
        .costType(COST_TYPE)
        .selector(selector)
        .isRateCardActive(RATE_CARD_ACTIVE_TRUE)
        .build();
  }

  public UpdateRateCardStatusRequest getUpdateRateCardStatusRequestWithNullSelector() {
    return UpdateRateCardStatusRequest.builder()
        .costType(COST_TYPE)
        .selector(null)
        .isRateCardActive(RATE_CARD_ACTIVE_TRUE)
        .build();
  }

  public UpdateRateCardStatusRequest getUpdateRateCardStatusRequestWithNullSelectorFalse() {
    return UpdateRateCardStatusRequest.builder()
        .costType(COST_TYPE)
        .selector(null)
        .isRateCardActive(RATE_CARD_ACTIVE_FALSE)
        .build();
  }

  public UpdateRateCardStatusRequest getUpdateRateCardStatusRequestForDefaultItinerary() {
    SelectorInfo selector = new SelectorInfo(SELECTOR_CF, null);
    return UpdateRateCardStatusRequest.builder()
        .costType(COST_TYPE)
        .selector(selector)
        .isRateCardActive(RATE_CARD_ACTIVE_FALSE)
        .build();
  }

  public UpdateRateCardStatusRequest
      getUpdateRateCardStatusRequestWithEmptySelectorCfValueAndStatusIsTrue() {
    SelectorInfo selector = new SelectorInfo(SELECTOR_CF, "");
    return UpdateRateCardStatusRequest.builder()
        .costType(COST_TYPE)
        .selector(selector)
        .isRateCardActive(RATE_CARD_ACTIVE_TRUE)
        .build();
  }

  public SelectorAndCostItineraryMappingEntity
      getSelectorAndCostItineraryEntityWithEmptySelectorCf() {
    return new SelectorAndCostItineraryMappingEntity(
        ID, ORG_ID, "", "", COST_ITINERARY_UPSLIKE, COST_TYPE);
  }

  public SelectorAndCostItineraryMappingEntity getSelectorAndCostItineraryEntityWith() {
    return new SelectorAndCostItineraryMappingEntity(
        ID, ORG_ID, SELECTOR_CF, SELECTOR_CF_VALUE, COST_ITINERARY_UPSLIKE, COST_TYPE);
  }

  public SelectorAndCostItineraryMappingEntity
      getSelectorAndCostItineraryEntityWithEmptySelectorCfValue() {
    return new SelectorAndCostItineraryMappingEntity(
        ID, ORG_ID, SELECTOR_CF, "", COST_ITINERARY_UPSLIKE, COST_TYPE);
  }

  public SelectorAndCostItineraryMappingEntity
      getSelectorAndCostItineraryEntityWithNullSelectorCfValue() {
    return new SelectorAndCostItineraryMappingEntity(
        ID, ORG_ID, SELECTOR_CF, null, COST_ITINERARY_UPSLIKE, COST_TYPE);
  }

  public CostFactorBucketTypeDto getCostFactorBucketTypeResponse() {
    return CostFactorBucketTypeDto.builder()
        .id(ID)
        .orgId(ORG_ID)
        .costFactor(COST_FACTOR)
        .bucketType(DISCRETE_BUCKET_TYPE)
        .build();
  }

  public CostFactorBucketTypeRequest getCostFactorBucketTypeRequest() {
    return CostFactorBucketTypeRequest.builder()
        .bucketType(DISCRETE_BUCKET_TYPE)
        .costFactor(COST_FACTOR)
        .build();
  }

  public UpdateCostFactorBucketTypeRequest updateCostFactorBucketTypeRequest() {
    return UpdateCostFactorBucketTypeRequest.builder().bucketType(DISCRETE_BUCKET_TYPE).build();
  }

  public CostFactorBucketTypeEntity getCostFactorBucketTypeEntity() {
    return CostFactorBucketTypeEntity.builder()
        .id(ID)
        .orgId(ORG_ID)
        .costFactor(COST_FACTOR)
        .bucketType(DISCRETE_BUCKET_TYPE)
        .build();
  }

  public UpdateCostFactorBucketTypeRequest getUpdateCostFactorBucketTypeRequest() {
    return UpdateCostFactorBucketTypeRequest.builder().bucketType(DISCRETE_BUCKET_TYPE).build();
  }

  public CostFactorContiguousBucketRequest getCostFactorContiguousBucketRequest() {
    return CostFactorContiguousBucketRequest.builder()
        .costFactor(COST_FACTOR)
        .notation(NOTATION)
        .notationDisplayName(NOTATION_DISPLAY_NAME)
        .fromValue(FROM_VALUE)
        .isFromValueInclusive(true)
        .toValue(TO_VALUE)
        .isToValueInclusive(false)
        .build();
  }

  public CostFactorContiguousBucketDto getCostFactorContiguousBucketDto() {
    return CostFactorContiguousBucketDto.builder()
        .id(ID)
        .costFactor(COST_FACTOR)
        .notation(NOTATION)
        .notationDisplayName(NOTATION_DISPLAY_NAME)
        .fromValue(FROM_VALUE)
        .isFromValueInclusive(true)
        .toValue(TO_VALUE)
        .isToValueInclusive(false)
        .build();
  }

  public GetCostFactorContiguousBucketRequest getGetCostFactorContiguousBucketRequest() {
    return GetCostFactorContiguousBucketRequest.builder().costFactor(COST_FACTOR).build();
  }

  public CostFactorDiscreteBucketRequest getCostFactorDiscreteBucketRequest() {
    return CostFactorDiscreteBucketRequest.builder()
        .costFactor(COST_FACTOR)
        .notation(NOTATION)
        .notationDisplayName(NOTATION_DISPLAY_NAME)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .valueList(VALUE_LIST)
        .build();
  }

  public CostFactorDiscreteBucketDto getCostFactorDiscreteBucketDto() {
    return CostFactorDiscreteBucketDto.builder()
        .id(ID)
        .orgId(ORG_ID)
        .costFactor(COST_FACTOR)
        .notation(NOTATION)
        .notationDisplayName(NOTATION_DISPLAY_NAME)
        .valueList(VALUE_LIST)
        .build();
  }

  public CostFactorContiguousBucketEntity getCostFactorContiguousBucketEntity() {
    return CostFactorContiguousBucketEntity.builder()
        .id(ID)
        .orgId(ORG_ID)
        .costFactor(COST_FACTOR)
        .notation(NOTATION)
        .notationDisplayName(NOTATION_DISPLAY_NAME)
        .fromValue(FROM_VALUE)
        .isFromValueInclusive(true)
        .toValue(TO_VALUE)
        .isToValueInclusive(false)
        .build();
  }

  public CostFactorDiscreteBucketEntity getCostFactorDiscreteBucketEntity() {
    return CostFactorDiscreteBucketEntity.builder()
        .id(ID)
        .orgId(ORG_ID)
        .costFactor(COST_FACTOR)
        .notation(NOTATION)
        .notationDisplayName(NOTATION_DISPLAY_NAME)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .valueList(VALUE_LIST)
        .build();
  }

  public CostDefinitionRequest getCostDefinitionRequest(
      String selectorCf, String rowCf, String columnCf) {
    return CostDefinitionRequest.builder()
        .costType(COST_TYPE)
        .selector(
            SelectorCostFactorInfoDto.builder()
                .selectorCf(selectorCf)
                .selectorCfValue(SELECTOR_CF_VALUE)
                .build())
        .filters(getFilterCostFactorInfoDto())
        .row(rowCf)
        .column(columnCf)
        .build();
  }

  private List<FilterCostFactorInfoDto> getFilterCostFactorInfoDto() {
    FilterCostFactorInfoDto filterCostFactorInfoDto =
        FilterCostFactorInfoDto.builder()
            .costFactor(FILTER_COST_FACTOR)
            .costFactorValue(FILTER_COST_FACTOR_VALUE)
            .build();
    return List.of(filterCostFactorInfoDto);
  }

  public CostDefinitionResponse getCostDefinitionResponse() {
    return CostDefinitionResponse.builder()
        .isRateCardActive(true)
        .columns(getColumnData())
        .rows(getRowData())
        .build();
  }

  private RateCardColumnsDto getColumnData() {
    CostFactorHeadersInfoDto costFactorHeadersInfoDto1 =
        CostFactorHeadersInfoDto.builder()
            .columnName("BillWeight UPS")
            .columnMeta("billWeightUPS")
            .isCostFactor(true)
            .build();

    CostFactorHeadersInfoDto costFactorHeadersInfoDto2 =
        CostFactorHeadersInfoDto.builder()
            .columnName("Zone1")
            .columnMeta("zone1")
            .isCostFactor(false)
            .build();
    return RateCardColumnsDto.builder()
        .title("Shipping Zones")
        .headers(List.of(costFactorHeadersInfoDto1, costFactorHeadersInfoDto2))
        .build();
  }

  private RateCardRowsDto getRowData() {
    Map<String, String> rowDataMap1 = new HashMap<>();
    rowDataMap1.put("billWeightUPS", "S");
    rowDataMap1.put("zone1", "1.0");
    rowDataMap1.put("isDynamicBucket", "false");

    Map<String, String> rowDataMap2 = new HashMap<>();
    rowDataMap2.put("M", "4.0");
    rowDataMap2.put("zone2", "2.0");
    rowDataMap2.put("isDynamicBucket", "true");

    return RateCardRowsDto.builder().data(List.of(rowDataMap1, rowDataMap2)).build();
  }

  public List<CostValueEntity> getCostValueList(List<String> costFactorKey) {
    return costFactorKey.stream().map(this::getCostValueEntity).collect(Collectors.toList());
  }

  public CostValueEntity getCostValueEntity(String costFactorKey) {
    CostValueEntity costValueEntity = new CostValueEntity();
    costValueEntity.setId(ID);
    costValueEntity.setOrgId(ORG_ID);
    costValueEntity.setCostItinerary(COST_ITINERARY);
    costValueEntity.setCostValue(COST_VALUE_WITH_PREV_SLB);
    costValueEntity.setCostFactorCombinationKey(costFactorKey);
    costValueEntity.setCustomAttributes(CUSTOM_ATTRIBUTES);
    return costValueEntity;
  }

  public List<String> getCostFactorCombinationKeyForGrid(List<String> rowCfValues) {
    return rowCfValues.stream()
        .flatMap(
            rowCfValue ->
                rowCfValues.stream()
                    .map(
                        columnCfValue ->
                            String.join(
                                PIPE_DELIMITER, COST_FACTOR_PREFIX_KEY, columnCfValue, rowCfValue)))
        .collect(Collectors.toList());
  }

  public List<String> getCostFactorCombinationKeyForTable(List<String> rowCfValues) {
    return rowCfValues.stream()
        .map(rowCfValue -> String.join(PIPE_DELIMITER, COST_FACTOR_PREFIX_KEY, rowCfValue))
        .collect(Collectors.toList());
  }

  public List<CostFactorDiscreteBucketEntity> getCostFactorDiscreteBucketEntityList() {
    List<CostFactorDiscreteBucketEntity> costFactorDiscreteBucketEntities = new ArrayList<>();
    costFactorDiscreteBucketEntities.add(
        CostFactorDiscreteBucketEntity.builder()
            .id(ID)
            .orgId(ORG_ID)
            .costFactor(COST_FACTOR)
            .notation(NOTATION)
            .notationDisplayName(NOTATION_DISPLAY_NAME)
            .customAttributes(CUSTOM_ATTRIBUTES)
            .valueList(VALUE_LIST)
            .build());
    costFactorDiscreteBucketEntities.add(
        CostFactorDiscreteBucketEntity.builder()
            .id(ID)
            .orgId(ORG_ID)
            .costFactor(COST_FACTOR)
            .notation("S")
            .notationDisplayName(NOTATION_DISPLAY_NAME)
            .valueList(VALUE_LIST)
            .customAttributes(CUSTOM_ATTRIBUTES)
            .build());
    costFactorDiscreteBucketEntities.add(
        CostFactorDiscreteBucketEntity.builder()
            .id(ID)
            .orgId(ORG_ID)
            .costFactor(COST_FACTOR)
            .notation("L")
            .notationDisplayName(NOTATION_DISPLAY_NAME)
            .valueList(VALUE_LIST)
            .customAttributes(CUSTOM_ATTRIBUTES)
            .build());
    return costFactorDiscreteBucketEntities;
  }

  public List<CostFactorContiguousBucketEntity> getCostFactorContiguousBucketEntityList() {
    List<CostFactorContiguousBucketEntity> costFactorContiguousBucketEntities = new ArrayList<>();
    costFactorContiguousBucketEntities.add(
        CostFactorContiguousBucketEntity.builder()
            .id(ID)
            .orgId(ORG_ID)
            .costFactor(COST_FACTOR)
            .notation(NOTATION)
            .notationDisplayName(NOTATION_DISPLAY_NAME)
            .build());
    costFactorContiguousBucketEntities.add(
        CostFactorContiguousBucketEntity.builder()
            .id(ID)
            .orgId(ORG_ID)
            .costFactor(COST_FACTOR)
            .notation("S")
            .notationDisplayName(NOTATION_DISPLAY_NAME)
            .build());
    costFactorContiguousBucketEntities.add(
        CostFactorContiguousBucketEntity.builder()
            .id(ID)
            .orgId(ORG_ID)
            .costFactor(COST_FACTOR)
            .notation("L")
            .notationDisplayName(NOTATION_DISPLAY_NAME)
            .build());
    return costFactorContiguousBucketEntities;
  }

  public CostTypeResponse getCostTypeResponseWithSelector(String costType) {
    return CostTypeResponse.builder()
        .currency("USD")
        .customAttributes(CUSTOM_ATTRIBUTES)
        .costTypeList(
            List.of(
                CostTypeDtoInfo.builder()
                    .costType(costType)
                    .displayName(costType)
                    .selectorCf(SELECTOR_CF)
                    .selectorCfDisplayName(SELECTOR_CF_DISPLAY_NAME)
                    .row(null)
                    .column(null)
                    .costFactors(List.of())
                    .selectorCfInfo(
                        List.of(
                            SelectorCfUIInfo.builder()
                                .selectorCfValue(SELECTOR_CF_VALUE)
                                .displayName(SELECTOR_CF_VALUE_UPS_AIR)
                                .row(
                                    RowColumnDto.builder()
                                        .costFactor("zone")
                                        .displayName("Zone")
                                        .uom("String")
                                        .build())
                                .column(
                                    RowColumnDto.builder()
                                        .costFactor("billWeight")
                                        .displayName("Bill Weight")
                                        .uom("Number")
                                        .build())
                                .costFactors(
                                    List.of(
                                        CostFactorUIValues.builder()
                                            .costFactor("surge")
                                            .uom("Number")
                                            .displayName("Surge")
                                            .values(
                                                List.of(
                                                    CostFactorValueDto.builder()
                                                        .value("HOLIDAY")
                                                        .displayName("Holiday")
                                                        .build()))
                                            .build()))
                                .build()))
                    .build()))
        .build();
  }

  public CostTypeResponse getCostTypeResponseWithSelectorButNoSelectorCfValue(String costType) {
    return CostTypeResponse.builder()
        .currency("USD")
        .customAttributes(CUSTOM_ATTRIBUTES)
        .costTypeList(
            List.of(
                CostTypeDtoInfo.builder()
                    .costType(costType)
                    .displayName(costType)
                    .selectorCf(SELECTOR_CF)
                    .selectorCfDisplayName(SELECTOR_CF_DISPLAY_NAME)
                    .row(null)
                    .column(null)
                    .costFactors(List.of())
                    .selectorCfInfo(
                        List.of(
                            SelectorCfUIInfo.builder()
                                .selectorCfValue("")
                                .displayName("DEFAULT")
                                .row(
                                    RowColumnDto.builder()
                                        .costFactor("zone")
                                        .displayName("Zone")
                                        .uom("String")
                                        .build())
                                .column(
                                    RowColumnDto.builder()
                                        .costFactor("billWeight")
                                        .displayName("Bill Weight")
                                        .uom("Number")
                                        .build())
                                .costFactors(
                                    List.of(
                                        CostFactorUIValues.builder()
                                            .costFactor("surge")
                                            .uom("Number")
                                            .displayName("Surge")
                                            .values(
                                                List.of(
                                                    CostFactorValueDto.builder()
                                                        .value("HOLIDAY")
                                                        .displayName("Holiday")
                                                        .build()))
                                            .build()))
                                .build()))
                    .build()))
        .build();
  }

  public CostTypeResponse getCostTypeResponseWithoutSelector(String costType) {
    return CostTypeResponse.builder()
        .currency("USD")
        .customAttributes(CUSTOM_ATTRIBUTES)
        .costTypeList(
            List.of(
                CostTypeDtoInfo.builder()
                    .costType(costType)
                    .displayName(costType)
                    .selectorCf("")
                    .selectorCfDisplayName("")
                    .row(
                        RowColumnDto.builder()
                            .costFactor("zone")
                            .displayName("Zone")
                            .uom("String")
                            .build())
                    .column(
                        RowColumnDto.builder()
                            .costFactor("billWeight")
                            .displayName("Bill Weight")
                            .uom("Number")
                            .build())
                    .costFactors(
                        List.of(
                            CostFactorUIValues.builder()
                                .costFactor("surge")
                                .uom("Number")
                                .displayName("Surge")
                                .values(
                                    List.of(
                                        CostFactorValueDto.builder()
                                            .value("HOLIDAY")
                                            .displayName("Holiday")
                                            .build()))
                                .build()))
                    .selectorCfInfo(List.of())
                    .build()))
        .build();
  }

  public CostTypeResponse getCostTypeResponseNotFound() {
    return CostTypeResponse.builder()
        .currency("USD")
        .customAttributes(CUSTOM_ATTRIBUTES)
        .costTypeList(List.of())
        .build();
  }

  public CostTypeValidationResponse getCostTypeValidationResponse() {
    return CostTypeValidationResponse.builder()
        .currency("USD")
        .costType(SHIPPING_COST)
        .displayName(SHIPPING_COST)
        .selectorCf("carrierServiceId")
        .selectorCfDisplayName("Carrier service Id")
        .selectorCfInfo(
            List.of(
                SelectorCfInfo.builder()
                    .selectorCfValue("UPS-GROUND")
                    .costItinerary("UPS-ITN")
                    .row(
                        CostFactorDescriptionDto.builder()
                            .costFactor("billWeight")
                            .values(List.of("0<<=5", "5<<=10", "10<<=20", "30+"))
                            .build())
                    .column(
                        CostFactorDescriptionDto.builder()
                            .costFactor("zone")
                            .values(List.of("zone1", "zone2", "zone3"))
                            .build())
                    .costFactors(
                        List.of(
                            CostFactorDescriptionDto.builder()
                                .costFactor("surge")
                                .values(List.of("NON-HOLIDAY", "HOLIDAY"))
                                .build()))
                    .build(),
                SelectorCfInfo.builder()
                    .selectorCfValue("FEDEX-GROUND")
                    .costItinerary("FEDEX-ITN")
                    .row(
                        CostFactorDescriptionDto.builder()
                            .costFactor("billWeight")
                            .values(List.of("0<<=10", "10<<=20", "20<<=30", "40+"))
                            .build())
                    .column(
                        CostFactorDescriptionDto.builder()
                            .costFactor("zone")
                            .values(List.of("zoneA", "zoneB", "zoneC"))
                            .build())
                    .costFactors(
                        List.of(
                            CostFactorDescriptionDto.builder()
                                .costFactor("surge")
                                .values(List.of("WEEKDAY", "WEEKEND"))
                                .build()))
                    .build()))
        .build();
  }

  public ExpressionValidationResponse getExpressionValidationResponse() {

    return ExpressionValidationResponse.builder()
        .expressionValue(EXPRESSION_VALUE)
        .sampleRequest(getSampleRequest())
        .sampleSolution(getSampleSolution())
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public SampleSourcingSolutionForFormulaValidation getSampleSolution() {

    return SampleSourcingSolutionForFormulaValidation.builder()
        .orgId("NEXTUPLE_GR")
        .serviceOption("EXPRESS") // This might be another class or specific structure
        .totalLines(2)
        .totalCost(EXPRESSION_VALUE)
        .shipments(List.of(getShipment()))
        .build();
  }

  public Shipment getShipment() {
    return Shipment.builder()
        .items(List.of(getItem()))
        .node(getNodeDetails())
        .carrierServiceId("carrierServiceId1")
        .zone("zone1")
        .cost(10.0)
        .build();
  }

  public NodeDetails getNodeDetails() {
    return NodeDetails.builder()
        .nodeId("nodeId1")
        .nodeType("nodeType2")
        .zipCode("nodeZipCode2")
        .nodeLabourTier("nodeLabourTier1")
        .build();
  }

  public Item getItem() {
    return Item.builder()
        .itemId("OPT-ITEM-4")
        .uom("qty")
        .length(10.0)
        .width(15.0)
        .height(20.0)
        .weight(50.0)
        .weightUom("g")
        .dimensionUom("m")
        .handlingType("MANUAL")
        .requestedQuantity(8.0)
        .fulfilledQuantity(5.0) // This might be another class or specific structure
        .build();
  }

  public SampleSourcingRequestForFormulaValidation getSampleRequest() {

    return SampleSourcingRequestForFormulaValidation.builder()
        .orgId("NEXTUPLE_GR")
        .serviceOption("EXPRESS")
        .orderDate("2023-03-15")
        .orderNo("SO-100201")
        .orderType("MARKETPLACE")
        .shipDestinationDetails(getShipDestinationDetails())
        .carrierServiceId("FEDEX_GROUND")
        .orderLines(getOrderLines())
        .build();
  }

  public List<SourcingOrderLines> getOrderLines() {
    List<SourcingOrderLines> orderLines = new ArrayList<>();
    orderLines.add(getSourcingOrderLines());
    return orderLines;
  }

  public SourcingOrderLines getSourcingOrderLines() {
    SourcingOrderLines orderLine =
        SourcingOrderLines.builder()
            .orderedQuantity(60.0)
            .carrierServiceId("FEDEX_GROUND")
            .shipDestinationDetails(getShipDestinationDetails())
            .item(getSourcingItem())
            .customAttributes(new OrderedJSONObject())
            .build();
    return orderLine;
  }

  public SourcingItem getSourcingItem() {
    SourcingItem item =
        SourcingItem.builder()
            .itemId("OPT-ITEM-4")
            .productClass("ELECTRONICS")
            .unitOfMeasure("EACH")
            .build();
    return item;
  }

  public ShipDestinationDetails getShipDestinationDetails() {
    ShipDestinationDetails shipDestinationDetails =
        ShipDestinationDetails.builder()
            .zipCode("RRR 1B5")
            .region("RRR")
            .state("Ontario")
            .country("CA")
            .timezone("UTC")
            .build();
    return shipDestinationDetails;
  }

  public ExpressionValidationRequest getExpressionValidationRequest() {
    return ExpressionValidationRequest.builder()
        .expression(EXPRESSION_FORMULA)
        .sampleRequest(getSampleRequest())
        .customAttributes(CUSTOM_ATTRIBUTES)
        .sampleSolution(getSampleSolution())
        .build();
  }

  public Map<String, Object> getAlgoValueMap() {
    Map<String, Object> algoValueMap = new HashMap<>();
    algoValueMap.put("l", 10);
    algoValueMap.put("b", 20);
    algoValueMap.put("h", 30);
    return algoValueMap;
  }

  public Map<String, Boolean> getSetScope() {
    Map<String, Boolean> setScope = new HashMap<>();
    setScope.put("l", Boolean.TRUE);
    setScope.put("b", Boolean.TRUE);
    setScope.put("h", Boolean.TRUE);
    return setScope;
  }

  public StandardEvaluationContext getSpelContext() throws NoSuchMethodException {

    StandardEvaluationContext context = new StandardEvaluationContext();
    context.registerFunction(
        "max", Math.class.getDeclaredMethod("max", new Class[] {double.class, double.class}));

    return context;
  }

  public List<CostFactorBucketTypeCacheKeyDto> getCostFactorBucketTypeCacheKeysDtoList() {
    CostFactorBucketTypeCacheKeyDto costFactorBucketTypeCacheKeyDto1 =
        CostFactorBucketTypeCacheKeyDto.builder().orgId(ORG_ID).costFactor(COST_FACTOR).build();
    CostFactorBucketTypeCacheKeyDto costFactorBucketTypeCacheKeyDto2 =
        CostFactorBucketTypeCacheKeyDto.builder().orgId(ORG_ID).costFactor(COST_FACTOR_2).build();
    return List.of(costFactorBucketTypeCacheKeyDto1, costFactorBucketTypeCacheKeyDto2);
  }

  public List<CostFactorContiguousBucketCacheKeyDto>
      getCostFactorContiguousBucketCacheKeysDtoList() {
    CostFactorContiguousBucketCacheKeyDto costFactorContiguousBucketCacheKeyDto1 =
        CostFactorContiguousBucketCacheKeyDto.builder()
            .orgId(ORG_ID)
            .costFactor(COST_FACTOR)
            .build();
    CostFactorContiguousBucketCacheKeyDto costFactorContiguousBucketCacheKeyDto2 =
        CostFactorContiguousBucketCacheKeyDto.builder()
            .orgId(ORG_ID)
            .costFactor(COST_FACTOR_2)
            .build();
    return List.of(costFactorContiguousBucketCacheKeyDto1, costFactorContiguousBucketCacheKeyDto2);
  }

  public List<CostFactorDiscreteBucketCacheKeyDto> getCostFactorDiscreteBucketCacheKeysDtoList() {
    CostFactorDiscreteBucketCacheKeyDto costFactorDiscreteBucketCacheKeyDto1 =
        CostFactorDiscreteBucketCacheKeyDto.builder().orgId(ORG_ID).costFactor(COST_FACTOR).build();
    CostFactorDiscreteBucketCacheKeyDto costFactorDiscreteBucketCacheKeyDto2 =
        CostFactorDiscreteBucketCacheKeyDto.builder()
            .orgId(ORG_ID)
            .costFactor(COST_FACTOR_2)
            .build();
    return List.of(costFactorDiscreteBucketCacheKeyDto1, costFactorDiscreteBucketCacheKeyDto2);
  }

  public List<CostItineraryAndFactorsCacheKeyDto> getCostItineraryAndFactorsCacheKeyDtoList() {
    CostItineraryAndFactorsCacheKeyDto costItineraryAndFactorsCacheKeyDto1 =
        CostItineraryAndFactorsCacheKeyDto.builder()
            .orgId(ORG_ID)
            .costItinerary(COST_ITINERARY)
            .itineraryStatus(ItineraryStatusEnum.CREATED)
            .isActive(true)
            .build();
    CostItineraryAndFactorsCacheKeyDto costItineraryAndFactorsCacheKeyDto2 =
        CostItineraryAndFactorsCacheKeyDto.builder()
            .orgId(ORG_ID)
            .costItinerary("DRAFT")
            .itineraryStatus(ItineraryStatusEnum.DRAFT)
            .isActive(false)
            .build();
    return List.of(costItineraryAndFactorsCacheKeyDto1, costItineraryAndFactorsCacheKeyDto2);
  }

  public List<CostFactorCacheKeyDto> getCostFactorCacheKeysDtoList() {
    CostFactorCacheKeyDto costFactorCacheKeyDto1 =
        CostFactorCacheKeyDto.builder().orgId(ORG_ID).costFactor(COST_FACTOR).build();
    CostFactorCacheKeyDto costFactorCacheKeyDto2 =
        CostFactorCacheKeyDto.builder().orgId(ORG_ID).costFactor(COST_FACTOR_2).build();
    return List.of(costFactorCacheKeyDto1, costFactorCacheKeyDto2);
  }

  public List<CostValueCacheKeyDto> getCostValueCacheKeysDtoList() {
    CostValueCacheKeyDto costValueCacheKeyDto1 =
        CostValueCacheKeyDto.builder()
            .orgId(ORG_ID)
            .costItinerary(COST_ITINERARY)
            .costFactorCombinationKey(COST_FACTOR_COMBINATION_KEY)
            .build();
    CostValueCacheKeyDto costValueCacheKeyDto2 =
        CostValueCacheKeyDto.builder()
            .orgId(ORG_ID)
            .costItinerary(COST_ITINERARY_UPSLIKE)
            .costFactorCombinationKey(COST_FACTOR_COMBINATION_KEY)
            .build();
    return List.of(costValueCacheKeyDto1, costValueCacheKeyDto2);
  }

  public List<CostFactorBucketTypeEntity> getCostFactorBucketTypeEntityList() {
    List<CostFactorBucketTypeEntity> costFactorBucketTypeEntities = new ArrayList<>();
    costFactorBucketTypeEntities.add(
        CostFactorBucketTypeEntity.builder().orgId(ORG_ID).costFactor(COST_FACTOR).build());
    costFactorBucketTypeEntities.add(
        CostFactorBucketTypeEntity.builder().orgId(ORG_ID).costFactor(COST_FACTOR).build());
    costFactorBucketTypeEntities.add(
        CostFactorBucketTypeEntity.builder().orgId(ORG_ID).costFactor(COST_FACTOR).build());
    return costFactorBucketTypeEntities;
  }

  public List<CostItineraryAndFactorsEntity> getCostItineraryAndFactorsEntityList() {
    List<CostItineraryAndFactorsEntity> costItineraryAndFactorsEntities = new ArrayList<>();
    costItineraryAndFactorsEntities.add(
        CostItineraryAndFactorsEntity.builder()
            .orgId(ORG_ID)
            .costItinerary(COST_ITINERARY)
            .itineraryStatus(ItineraryStatusEnum.CREATED)
            .isActive(true)
            .build());
    costItineraryAndFactorsEntities.add(
        CostItineraryAndFactorsEntity.builder()
            .orgId(ORG_ID)
            .costItinerary(COST_ITINERARY)
            .itineraryStatus(ItineraryStatusEnum.DRAFT)
            .isActive(false)
            .build());
    return costItineraryAndFactorsEntities;
  }

  public List<CostValueEntity> getCostValueEntityList() {
    List<CostValueEntity> costValueEntities = new ArrayList<>();
    costValueEntities.add(
        CostValueEntity.builder()
            .orgId(ORG_ID)
            .costItinerary(COST_ITINERARY)
            .costFactorCombinationKey(COST_FACTOR_COMBINATION_KEY)
            .customAttributes(CUSTOM_ATTRIBUTES)
            .build());
    costValueEntities.add(
        CostValueEntity.builder()
            .orgId(ORG_ID)
            .costItinerary(COST_ITINERARY)
            .costFactorCombinationKey(COST_FACTOR_COMBINATION_KEY)
            .customAttributes(CUSTOM_ATTRIBUTES)
            .build());
    return costValueEntities;
  }

  public List<CostFactorEntity> getCostFactorEntityList() {
    List<CostFactorEntity> costFactorEntities = new ArrayList<>();
    costFactorEntities.add(
        CostFactorEntity.builder().orgId(ORG_ID).costFactor(COST_FACTOR).build());
    costFactorEntities.add(
        CostFactorEntity.builder().orgId(ORG_ID).costFactor(COST_FACTOR_2).build());
    return costFactorEntities;
  }

  public CostDefinitionRequest getGridRequest() {
    return CostDefinitionRequest.builder()
        .costType(SHIPPING_COST)
        .selector(
            SelectorCostFactorInfoDto.builder()
                .selectorCf("carrierServiceId")
                .selectorCfValue("UPS-GROUND")
                .build())
        .filters(
            List.of(
                FilterCostFactorInfoDto.builder()
                    .costFactor("surge")
                    .costFactorValue("NON-HOLIDAY")
                    .build()))
        .row("billWeight")
        .column("zone")
        .build();
  }

  public CostDefinitionRequest getGridRequestWithoutSelector() {
    return CostDefinitionRequest.builder()
        .costType(NODE_PROCESSING_COST)
        .filters(
            List.of(
                FilterCostFactorInfoDto.builder()
                    .costFactor("surge")
                    .costFactorValue("NON-HOLIDAY")
                    .build()))
        .row("billWeight")
        .column("zone")
        .build();
  }

  public CostTypeValidationResponse getCostTypeValidationResponseWithoutSelector() {
    return CostTypeValidationResponse.builder()
        .currency("USD")
        .costType(SHIPPING_COST)
        .displayName(SHIPPING_COST)
        .costItinerary("DEFAULT-ITN")
        .row(
            CostFactorDescriptionDto.builder()
                .costFactor("billWeight")
                .values(List.of("0<<=5", "5<<=10", "10<<=20", "30+"))
                .build())
        .column(
            CostFactorDescriptionDto.builder()
                .costFactor("zone")
                .values(List.of("zone1", "zone2", "zone3"))
                .build())
        .costFactors(
            List.of(
                CostFactorDescriptionDto.builder()
                    .costFactor("surge")
                    .values(List.of("NON-HOLIDAY", "HOLIDAY"))
                    .build(),
                CostFactorDescriptionDto.builder()
                    .costFactor("EDD")
                    .values(List.of("BeforeEDD", "AfterEDD"))
                    .build()))
        .build();
  }

  public OptimizationAndCostTypesMappingEntity getOptimizationAndCostTypesMappingEntity() {
    return OptimizationAndCostTypesMappingEntity.builder()
        .orgId(ORG_ID)
        .id(ID)
        .costTypes(COST_TYPE_SHIPPING_COST)
        .optimizationStrategy(OPT_STRATEGY)
        .description(DESCRIPTION)
        .javaClassName(JAVA_CLASS_NAME)
        .build();
  }

  public OptimizationAndCostTypesMappingEntity
      getOptimizationAndCostTypesMappingEntityWithProfit() {
    return OptimizationAndCostTypesMappingEntity.builder()
        .orgId(ORG_ID)
        .id(ID)
        .costTypes(COST_TYPE_SHIPPING_COST + "," + COST_TYPE_SHIPPING_REVENUE)
        .optimizationStrategy(OPT_STRATEGY_PROFIT)
        .description(DESCRIPTION)
        .javaClassName(JAVA_CLASS_NAME)
        .build();
  }

  public CreateOptimizationAndCostTypesMappingRequest
      getCreateOptimizationAndCostTypesMappingRequest() {
    return CreateOptimizationAndCostTypesMappingRequest.builder()
        .orgId(ORG_ID)
        .costTypes(COST_TYPE_SHIPPING_COST)
        .optimizationStrategy(OPT_STRATEGY)
        .description(DESCRIPTION)
        .javaClassName(JAVA_CLASS_NAME)
        .build();
  }

  public CreateOptimizationAndCostTypesMappingRequest
      getCreateOptimizationAndCostTypesMappingRequestWithProfit() {
    return CreateOptimizationAndCostTypesMappingRequest.builder()
        .orgId(ORG_ID)
        .costTypes(COST_TYPE_SHIPPING_COST + "," + COST_TYPE_SHIPPING_REVENUE)
        .optimizationStrategy(OPT_STRATEGY_PROFIT)
        .description(DESCRIPTION)
        .javaClassName(JAVA_CLASS_NAME)
        .build();
  }

  public UpdateOptimizationAndCostTypesMappingRequest
      getUpdateOptimizationAndCostTypesMappingRequest() {
    return UpdateOptimizationAndCostTypesMappingRequest.builder()
        .costTypes(COST_TYPE_SHIPPING_COST)
        .description(DESCRIPTION)
        .javaClassName(JAVA_CLASS_NAME)
        .build();
  }

  public OptimizationAndCostTypesMappingResponse getOptimizationAndCostTypesMappingResponse() {
    return OptimizationAndCostTypesMappingResponse.builder()
        .id(ID)
        .orgId(ORG_ID)
        .costTypes(COST_TYPE_SHIPPING_COST)
        .optimizationStrategy(OPT_STRATEGY)
        .description(DESCRIPTION)
        .javaClassName(JAVA_CLASS_NAME)
        .build();
  }

  public NamedOptimizationStrategyDomainDto getNamedOptimizationStrategyDomainDto() {
    return NamedOptimizationStrategyDomainDto.builder()
        .id(1L)
        .orgId(ORG_ID)
        .groupId("12")
        .optimizationStrategyName("OSN")
        .optimizationStrategyDetails("COST")
        .build();
  }

  public NamedOptimizationStrategyDomainDto getNamedOptimizationStrategyDomainDtoForProfit() {
    return NamedOptimizationStrategyDomainDto.builder()
        .id(1L + 1)
        .orgId(ORG_ID)
        .groupId("13")
        .optimizationStrategyName("OSN")
        .optimizationStrategyDetails("PROFIT")
        .build();
  }
}
