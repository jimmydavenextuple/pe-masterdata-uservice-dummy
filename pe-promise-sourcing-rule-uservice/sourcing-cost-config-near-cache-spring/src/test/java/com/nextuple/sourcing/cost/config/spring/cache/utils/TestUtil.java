/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.spring.cache.utils;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.cache.domain.CostAttributeDetailsCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.CostAttributeDetailsCacheValue;
import com.nextuple.sourcing.cost.config.cache.domain.CostAttributeMappingCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.CostAttributeMappingCacheValue;
import com.nextuple.sourcing.cost.config.cache.domain.CostFactorBucketTypeCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.CostFactorBucketTypeCacheValue;
import com.nextuple.sourcing.cost.config.cache.domain.CostFactorCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.CostFactorCacheValue;
import com.nextuple.sourcing.cost.config.cache.domain.CostFactorContiguousBucketCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.CostFactorContiguousBucketCacheValue;
import com.nextuple.sourcing.cost.config.cache.domain.CostFactorDiscreteBucketCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.CostFactorDiscreteBucketCacheValue;
import com.nextuple.sourcing.cost.config.cache.domain.CostItineraryAndFactorsCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.CostItineraryAndFactorsCacheValue;
import com.nextuple.sourcing.cost.config.cache.domain.CostValueCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.CostValueCacheValue;
import com.nextuple.sourcing.cost.config.cache.domain.OptimizationAndCostTypesMappingKey;
import com.nextuple.sourcing.cost.config.cache.domain.OptimizationAndCostTypesMappingValue;
import com.nextuple.sourcing.cost.config.cache.domain.PreferenceSelectorCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.PreferenceSelectorCacheValue;
import com.nextuple.sourcing.cost.config.cache.domain.SelectorAndCostItineraryCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.SelectorAndCostItineraryCacheValue;
import com.nextuple.sourcing.cost.config.cache.domain.TenantCostTypeCacheKey;
import com.nextuple.sourcing.cost.config.cache.domain.TenantCostTypeCacheValue;
import com.nextuple.sourcing.cost.config.dto.CostAttributeDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorBucketTypeDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorContiguousBucketDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorDiscreteBucketDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorDto;
import com.nextuple.sourcing.cost.config.dto.CostItineraryAndFactorsDto;
import com.nextuple.sourcing.cost.config.dto.PreferenceSelectorDto;
import com.nextuple.sourcing.cost.config.enums.BucketTypeEnum;
import com.nextuple.sourcing.cost.config.enums.DataTypeEnum;
import com.nextuple.sourcing.cost.config.enums.ItineraryStatusEnum;
import com.nextuple.sourcing.cost.config.enums.LevelAppliedEnum;
import com.nextuple.sourcing.cost.config.enums.LookupContextEnum;
import com.nextuple.sourcing.cost.config.outbound.CostAttributeMappingResponse;
import com.nextuple.sourcing.cost.config.outbound.CostValueResponse;
import com.nextuple.sourcing.cost.config.outbound.OptimizationAndCostTypesMappingResponse;
import com.nextuple.sourcing.cost.config.outbound.SelectorAndCostItineraryMappingResponse;
import com.nextuple.sourcing.cost.config.outbound.TenantCostTypeResponse;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TestUtil {

  public static final String COST_TYPE_LIST = "SHIPPING_COST,NODE_PROCESSING_COST";
  public static final String ORG_ID = "org123";
  public static final Long ID = 1L;
  public static final String CARRIER_SERVICE_ID = "carrierServiceId";
  public static final String UPS_GROUND = "UPS-GROUND";
  public static final String SHIPPING_COST_UPS_LIKE = "SHIPPING_COST_UPS_LIKE";
  public static final String SHIPPING_COST = "SHIPPING_COST";

  public static final String COST_FACTOR = "BillWeightDoordash";
  public static final DataTypeEnum DATA_TYPE = DataTypeEnum.NUMBER;
  public static final String FORMULA = "(l*b*h)/1000";
  public static final String DISPLAY_NAME = "Bill Weight Custom Doordash";
  public static final String VALUES = "S,M,L,XL";
  public static final String DEFAULT_VALUE = "S";
  public static final LevelAppliedEnum LEVEL_APPLIED = LevelAppliedEnum.SHIPMENT;

  public static final String SELECTOR_CF = "carrierServiceId";
  public static final String COST_TYPE = "SHIPPING_COST";
  public static final String COST_TYPE_DISPLAY_NAME = "Shipping cost";
  public static final String CANONICAL_NAME = "itemLength";
  public static final String ATTRIBUTE_NAME = "length";
  public static final String CANONICAL_DISPLAY_NAME = "Item Length";

  public static final String COST_FACTOR_COMBINATION_KEY = "UPS_GROUND|NON_HOLIDAYS|Z1|XXL";
  public static final String PREV_SLAB_VALUE = "UPS_GROUND|NON_HOLIDAYS|Z1|XL";
  public static final double COST_VALUE_WITH_PREV_SLB = 0.63;
  public static final String COST_ITINERARY = "SHIPPING_COST_UPSLIKE";
  public static final String ATTRIBUTE_DESCRIPTION = "Length of the item";
  public static final String ATTRIBUTE_PATH = "/shipments/1/items/1/length";
  public static final String NOTATION = "S";
  public static final String OPT_STRATEGY = "COST";
  public static final String COST_TYPES = "BUYING_COST,SHIPPING_COST";
  public static final String DESCRIPTION = "Description";

  public BaseResponse<PreferenceSelectorDto> getPreferenceSelectorDto() {
    BaseResponse<PreferenceSelectorDto> baseResponse = new BaseResponse<>();
    PreferenceSelectorDto preferenceSelectorResponse =
        PreferenceSelectorDto.builder()
            .id(ID)
            .orgId(ORG_ID)
            .selectorCf(SELECTOR_CF)
            .costType(COST_TYPE)
            .build();
    baseResponse.setMessage("Node details fetched successfully");
    baseResponse.setPayload(preferenceSelectorResponse);
    return baseResponse;
  }

  public PreferenceSelectorCacheValue getPreferenceSelectorCacheValue() {
    return PreferenceSelectorCacheValue.builder()
        .id(ID)
        .orgId(ORG_ID)
        .orgId(ORG_ID)
        .selectorCf(SELECTOR_CF)
        .costType(COST_TYPE)
        .build();
  }

  public PreferenceSelectorCacheKey getPreferenceSelectorCacheKey() {
    return PreferenceSelectorCacheKey.builder().orgId(ORG_ID).costType(COST_TYPE).build();
  }

  public BaseResponse<CostItineraryAndFactorsDto> getCostItineraryAndFactorsResponse() {

    BaseResponse<CostItineraryAndFactorsDto> baseResponse = new BaseResponse<>();
    CostItineraryAndFactorsDto tostItineraryAndFactorsResponse =
        CostItineraryAndFactorsDto.builder()
            .id(ID)
            .orgId(ORG_ID)
            .costItinerary(COST_ITINERARY)
            .costFactors(COST_FACTOR)
            .itineraryStatus(ItineraryStatusEnum.CREATED)
            .isActive(Boolean.TRUE)
            .build();
    baseResponse.setMessage("Node details fetched successfully");
    baseResponse.setPayload(tostItineraryAndFactorsResponse);
    return baseResponse;
  }

  public CostItineraryAndFactorsCacheValue getCostItineraryAndFactorsCacheValue() {
    return CostItineraryAndFactorsCacheValue.builder()
        .id(ID)
        .orgId(ORG_ID)
        .costItinerary(COST_ITINERARY)
        .costFactors(COST_FACTOR)
        .itineraryStatus(ItineraryStatusEnum.CREATED)
        .isActive(Boolean.TRUE)
        .build();
  }

  public CostItineraryAndFactorsCacheKey getCostItineraryAndFactorsCacheKey() {
    return CostItineraryAndFactorsCacheKey.builder()
        .orgId(ORG_ID)
        .costItinerary(COST_ITINERARY)
        .itineraryStatus(ItineraryStatusEnum.CREATED)
        .isActive(Boolean.TRUE)
        .build();
  }

  public BaseResponse<CostFactorDto> getCostFactorDto() {
    BaseResponse<CostFactorDto> baseResponse = new BaseResponse<>();
    CostFactorDto CostFactorResponse =
        CostFactorDto.builder()
            .id(ID)
            .orgId(ORG_ID)
            .costFactor(COST_FACTOR)
            .values(VALUES)
            .defaultValue(DEFAULT_VALUE)
            .dataType(DATA_TYPE)
            .displayName(DISPLAY_NAME)
            .formula(FORMULA)
            .levelApplied(LEVEL_APPLIED)
            .build();
    baseResponse.setMessage("Cost factor fetched successfully");
    baseResponse.setPayload(CostFactorResponse);
    return baseResponse;
  }

  public CostFactorCacheValue getCostFactorCacheValue() {
    return CostFactorCacheValue.builder()
        .id(ID)
        .orgId(ORG_ID)
        .costFactor(COST_FACTOR)
        .values(VALUES)
        .defaultValue(DEFAULT_VALUE)
        .dataType(DATA_TYPE)
        .displayName(DISPLAY_NAME)
        .formula(FORMULA)
        .levelApplied(LEVEL_APPLIED)
        .build();
  }

  public CostFactorCacheKey getCostFactorCacheKey() {
    return CostFactorCacheKey.builder().orgId(ORG_ID).costFactor(COST_FACTOR).build();
  }

  public BaseResponse<List<SelectorAndCostItineraryMappingResponse>>
      getSelectorAndCostItineraryMappingResponse() {
    BaseResponse<List<SelectorAndCostItineraryMappingResponse>> baseResponse = new BaseResponse<>();
    SelectorAndCostItineraryMappingResponse selectorAndCostItineraryResponse =
        SelectorAndCostItineraryMappingResponse.builder()
            .id(ID)
            .orgId(ORG_ID)
            .selectorCf(CARRIER_SERVICE_ID)
            .selectorCfValue(UPS_GROUND)
            .costItinerary(SHIPPING_COST_UPS_LIKE)
            .costType(SHIPPING_COST)
            .build();
    baseResponse.setMessage("Node details fetched successfully");
    baseResponse.setPayload(List.of(selectorAndCostItineraryResponse));
    return baseResponse;
  }

  public SelectorAndCostItineraryCacheValue getSelectorAndCostItineraryCacheValue() {
    return SelectorAndCostItineraryCacheValue.builder()
        .selectorAndCostItineraryMappingResponses(
            List.of(
                SelectorAndCostItineraryMappingResponse.builder()
                    .id(ID)
                    .orgId(ORG_ID)
                    .selectorCf(CARRIER_SERVICE_ID)
                    .selectorCfValue(UPS_GROUND)
                    .costItinerary(SHIPPING_COST_UPS_LIKE)
                    .costType(SHIPPING_COST)
                    .build()))
        .build();
  }

  public SelectorAndCostItineraryCacheKey getSelectorAndCostItineraryCacheKey() {
    return SelectorAndCostItineraryCacheKey.builder()
        .orgId(ORG_ID)
        .costType(COST_TYPE)
        .selectorCf(SELECTOR_CF)
        .build();
  }

  public BaseResponse<List<TenantCostTypeResponse>> getTenantCostTypeResponse() {

    BaseResponse<List<TenantCostTypeResponse>> baseResponse = new BaseResponse<>();
    TenantCostTypeResponse tenantCostTypeResponse =
        TenantCostTypeResponse.builder()
            .id(ID)
            .orgId(ORG_ID)
            .costType(COST_TYPE)
            .displayName(COST_TYPE_DISPLAY_NAME)
            .build();
    baseResponse.setMessage("Node details fetched successfully");
    baseResponse.setPayload(List.of(tenantCostTypeResponse));
    return baseResponse;
  }

  public TenantCostTypeCacheValue getTenantCostTypeCacheValue() {
    return TenantCostTypeCacheValue.builder()
        .tenantCostTypeResponses(
            List.of(
                TenantCostTypeResponse.builder()
                    .id(ID)
                    .orgId(ORG_ID)
                    .costType(COST_TYPE)
                    .displayName(COST_TYPE_DISPLAY_NAME)
                    .build()))
        .build();
  }

  public TenantCostTypeCacheKey getTenantCostTypeCacheKey() {
    return TenantCostTypeCacheKey.builder().orgId(ORG_ID).build();
  }

  public CostAttributeMappingCacheKey getCostAttributeMappingCacheKey() {
    return CostAttributeMappingCacheKey.builder()
        .orgId(ORG_ID)
        .canonicalName(CANONICAL_NAME)
        .build();
  }

  private CostAttributeMappingResponse getCostAttributeMappingResponse() {
    return CostAttributeMappingResponse.builder()
        .id(ID)
        .orgId(ORG_ID)
        .canonicalName(CANONICAL_NAME)
        .displayName(CANONICAL_DISPLAY_NAME)
        .attributeName(ATTRIBUTE_NAME)
        .build();
  }

  public BaseResponse<CostAttributeMappingResponse> getBaseResponseOfCostAttributeMapping() {
    return BaseResponse.builder().payload(getCostAttributeMappingResponse()).build();
  }

  public CostAttributeMappingCacheValue getCostAttributeMappingCacheValue() {
    return CostAttributeMappingCacheValue.builder()
        .costAttributeMappingResponse(getCostAttributeMappingResponse())
        .build();
  }

  public BaseResponse<CostValueResponse> getCostValueResponse() {
    BaseResponse<CostValueResponse> baseResponse = new BaseResponse<>();

    CostValueResponse costValueResponse =
        CostValueResponse.builder()
            .id(ID)
            .orgId(ORG_ID)
            .costItinerary(COST_ITINERARY)
            .costValue(COST_VALUE_WITH_PREV_SLB)
            .costFactorCombinationKey(COST_FACTOR_COMBINATION_KEY)
            .prevSlabValue(PREV_SLAB_VALUE)
            .build();
    baseResponse.setMessage("Cost value fetched successfully");
    baseResponse.setPayload(costValueResponse);
    return baseResponse;
  }

  public CostValueCacheValue getCostValueCacheValue() {
    return CostValueCacheValue.builder()
        .id(ID)
        .orgId(ORG_ID)
        .costItinerary(COST_ITINERARY)
        .costValue(COST_VALUE_WITH_PREV_SLB)
        .costFactorCombinationKey(COST_FACTOR_COMBINATION_KEY)
        .prevSlabValue(PREV_SLAB_VALUE)
        .build();
  }

  public CostValueCacheKey getCostValueCacheKey() {
    return CostValueCacheKey.builder()
        .orgId(ORG_ID)
        .costItinerary(COST_ITINERARY)
        .costFactorCombinationKey(COST_FACTOR_COMBINATION_KEY)
        .build();
  }

  public BaseResponse<CostAttributeDto> getCostAttributeDetailsResponse() {
    BaseResponse<CostAttributeDto> baseResponse = new BaseResponse<>();

    CostAttributeDto costAttributeDto =
        CostAttributeDto.builder()
            .id(ID)
            .attributeName(ATTRIBUTE_NAME)
            .attributeDescription(ATTRIBUTE_DESCRIPTION)
            .path(ATTRIBUTE_PATH)
            .lookupContext(LookupContextEnum.SOLUTION)
            .build();
    baseResponse.setMessage("Cost attribute details fetched successfully");
    baseResponse.setPayload(costAttributeDto);
    return baseResponse;
  }

  public CostAttributeDetailsCacheKey getCostAttributeDetailsCacheKey() {
    return CostAttributeDetailsCacheKey.builder().attributeName(ATTRIBUTE_NAME).build();
  }

  public CostAttributeDetailsCacheValue getCostAttributeDetailsCacheValue() {
    return CostAttributeDetailsCacheValue.builder()
        .id(ID)
        .attributeName(ATTRIBUTE_NAME)
        .attributeDescription(ATTRIBUTE_DESCRIPTION)
        .path(ATTRIBUTE_PATH)
        .lookupContext(LookupContextEnum.SOLUTION)
        .build();
  }

  public CostFactorBucketTypeCacheKey getCostFactorBucketTypeCacheKey() {
    return CostFactorBucketTypeCacheKey.builder().orgId(ORG_ID).costFactor(COST_FACTOR).build();
  }

  public CostFactorBucketTypeCacheValue getCostFactorBucketTypeCacheValue() {
    return CostFactorBucketTypeCacheValue.builder()
        .orgId(ORG_ID)
        .costFactor(COST_FACTOR)
        .bucketType(BucketTypeEnum.CONTIGUOUS)
        .id(ID)
        .build();
  }

  public BaseResponse<CostFactorBucketTypeDto> getCostFactorBucketTypeDtoBaseResponse() {
    BaseResponse<CostFactorBucketTypeDto> baseResponse = new BaseResponse<>();

    CostFactorBucketTypeDto costFactorBucketTypeDto =
        CostFactorBucketTypeDto.builder()
            .id(ID)
            .orgId(ORG_ID)
            .bucketType(BucketTypeEnum.CONTIGUOUS)
            .costFactor(COST_FACTOR)
            .build();
    baseResponse.setMessage("Cost factor bucket type fetched successfully");
    baseResponse.setPayload(costFactorBucketTypeDto);
    return baseResponse;
  }

  public CostFactorContiguousBucketCacheKey getCostFactorContiguousBucketCacheKey() {
    return CostFactorContiguousBucketCacheKey.builder()
        .orgId(ORG_ID)
        .costFactor(COST_FACTOR)
        .build();
  }

  public CostFactorContiguousBucketCacheValue getCostFactorContiguousBucketCacheValue() {
    return CostFactorContiguousBucketCacheValue.builder()
        .bucketsTreeMap(getContiguousTreeMap())
        .build();
  }

  private TreeMap<Double, CostFactorContiguousBucketDto> getContiguousTreeMap() {
    TreeMap<Double, CostFactorContiguousBucketDto> treeMap = new TreeMap<>();
    treeMap.put(10.0, getCostFactorContiguousBucketDtoBaseResponse().getPayload().get(0));
    return treeMap;
  }

  public BaseResponse<List<CostFactorContiguousBucketDto>>
      getCostFactorContiguousBucketDtoBaseResponse() {
    BaseResponse<List<CostFactorContiguousBucketDto>> baseResponse = new BaseResponse<>();

    CostFactorContiguousBucketDto costFactorContiguousBucketDto =
        CostFactorContiguousBucketDto.builder()
            .id(ID)
            .orgId(ORG_ID)
            .notation(NOTATION)
            .costFactor(COST_FACTOR)
            .fromValue(0.0)
            .toValue(10.0)
            .isFromValueInclusive(true)
            .isToValueInclusive(true)
            .build();
    baseResponse.setMessage("Cost factor contiguous buckets fetched successfully");
    baseResponse.setPayload(List.of(costFactorContiguousBucketDto));
    return baseResponse;
  }

  public CostFactorDiscreteBucketCacheKey getCostFactorDiscreteBucketCacheKey() {
    return CostFactorDiscreteBucketCacheKey.builder().orgId(ORG_ID).costFactor(COST_FACTOR).build();
  }

  public CostFactorDiscreteBucketCacheValue getCostFactorDiscreteBucketCacheValue() {
    return CostFactorDiscreteBucketCacheValue.builder()
        .valueToBucketMapping(getDiscreteBucketMap())
        .build();
  }

  private Map<String, String> getDiscreteBucketMap() {
    return Map.of("KITCHEN", "S", "BEVERAGES", "S");
  }

  public BaseResponse<List<CostFactorDiscreteBucketDto>>
      getCostFactorDiscreteBucketDtoBaseResponse() {
    BaseResponse<List<CostFactorDiscreteBucketDto>> baseResponse = new BaseResponse<>();

    CostFactorDiscreteBucketDto costFactorDiscreteBucketDto =
        CostFactorDiscreteBucketDto.builder()
            .id(ID)
            .orgId(ORG_ID)
            .notation(NOTATION)
            .costFactor(COST_FACTOR)
            .valueList("KITCHEN,BEVERAGES")
            .build();
    baseResponse.setMessage("Cost factor discrete buckets fetched successfully");
    baseResponse.setPayload(List.of(costFactorDiscreteBucketDto));
    return baseResponse;
  }

  public OptimizationAndCostTypesMappingValue getOptimizationAndCostTypesMappingValue() {
    return OptimizationAndCostTypesMappingValue.builder()
        .optimizationAndCostTypesMappingResponse(
            OptimizationAndCostTypesMappingResponse.builder()
                .id(ID)
                .orgId(ORG_ID)
                .optimizationStrategy(OPT_STRATEGY)
                .costTypes(COST_TYPES)
                .description(DESCRIPTION)
                .javaClassName(null)
                .build())
        .build();
  }

  public OptimizationAndCostTypesMappingKey getOptimizationAndCostTypesMappingKey() {
    return OptimizationAndCostTypesMappingKey.builder()
        .orgId(ORG_ID)
        .optimizationStrategy(OPT_STRATEGY)
        .build();
  }

  public BaseResponse<OptimizationAndCostTypesMappingResponse>
      getOptimizationAndCostTypesMappingResponse() {
    BaseResponse<OptimizationAndCostTypesMappingResponse> response = new BaseResponse<>();
    OptimizationAndCostTypesMappingResponse optimizationAndCostTypesMappingResponse =
        OptimizationAndCostTypesMappingResponse.builder()
            .id(ID)
            .orgId(ORG_ID)
            .optimizationStrategy(OPT_STRATEGY)
            .costTypes(COST_TYPES)
            .description(DESCRIPTION)
            .javaClassName(null)
            .build();
    response.setPayload(optimizationAndCostTypesMappingResponse);
    return response;
  }
}
