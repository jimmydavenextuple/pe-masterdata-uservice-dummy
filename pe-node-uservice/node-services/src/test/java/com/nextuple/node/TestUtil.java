/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.configuration.outbound.TenantConfigdataResponse;
import com.nextuple.node.domain.dto.NodeCacheKeyDto;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.node.domain.inbound.NodeBaseRequest;
import com.nextuple.node.domain.inbound.NodeRequest;
import com.nextuple.node.domain.outbound.NodeResponse;
import com.nextuple.node.persistence.domain.NodeDomainDto;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class TestUtil {

  public static final String NODE_ID = "node-1";
  public static final String NODE_ID_2 = "node-2";
  public static final String ORG_ID = "org-1";
  public static final String STREET = "street-1";
  public static final String CITY = "city-1";
  public static final String STATE = "state-1";
  public static final String ZIP_CODE = "33666";
  public static final String COUNTRY = "IN";
  public static final String LATITUDE = "43.769912";
  public static final String LONGITUDE = "-79.296678";
  public static final String TIME_ZONE = "America/Toronto";
  public static final String SORT_BY = "nodeId";
  public static final String SORT_ORDER_DESC = "DESC";
  public static final String DEFAULT_SORT_ORDER = "ASC";
  public static final Boolean IS_ACTIVE_FALSE = Boolean.FALSE;
  public static final String UPDATED_LATITUDE = "3526.5262";
  public static Boolean SHIP_TO_TIME = Boolean.TRUE;
  public static Boolean BOPIS_ELIGIBLE = Boolean.TRUE;
  public static String NODE_TYPE = "MFC";
  public static Boolean IS_ACTIVE = Boolean.TRUE;
  public static final String CONFIG_KEY = "node-types";
  public static final String CONFIG_VALUE = "MFC, Store, FC";
  public static final String NODE_LABOUR_TIER = "tier1";

  public NodeRequest getNodeRequest() {
    return NodeRequest.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(BOPIS_ELIGIBLE)
        .city(CITY)
        .nodeType("Store")
        .nodeLabourTier(NODE_LABOUR_TIER)
        .country(COUNTRY)
        .nodeType(NODE_TYPE)
        .isActive(IS_ACTIVE)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .zipCode(ZIP_CODE)
        .state(STATE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .serviceOptionEligibilities(getServiceOptionEligibilities())
        .customAttributes(
            JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2"))
        .build();
  }

  public NodeResponse getNodeResponse() {
    return NodeResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(BOPIS_ELIGIBLE)
        .city(CITY)
        .country(COUNTRY)
        .nodeType(NODE_TYPE)
        .nodeLabourTier(NODE_LABOUR_TIER)
        .isActive(IS_ACTIVE)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .zipCode(ZIP_CODE)
        .state(STATE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .serviceOptionEligibilities(getServiceOptionEligibilities())
        .customAttributes(
            JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2"))
        .build();
  }

  public NodeBaseRequest getNodeUpdationRequest() {
    return NodeBaseRequest.builder()
        .isActive(Boolean.FALSE)
        .city("city-2")
        .country(COUNTRY)
        .nodeType("Store")
        .nodeLabourTier(NODE_LABOUR_TIER)
        .timezone(TIME_ZONE)
        .latitude("3526.5262")
        .customAttributes(
            JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2"))
        .build();
  }

  public NodeResponse getUpdatedNodeResponse() {
    return NodeResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(BOPIS_ELIGIBLE)
        .city("city-2")
        .country(COUNTRY)
        .nodeType(NODE_TYPE)
        .nodeLabourTier(NODE_LABOUR_TIER)
        .isActive(Boolean.FALSE)
        .latitude("3526.5262")
        .longitude(LONGITUDE)
        .zipCode(ZIP_CODE)
        .state(STATE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .serviceOptionEligibilities(getServiceOptionEligibilities())
        .customAttributes(
            JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2"))
        .build();
  }

  public NodeDomainDto getUpdatedNodeDomainDto() {
    return NodeDomainDto.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .city("city-2")
        .country(COUNTRY)
        .shipToHome(SHIP_TO_TIME)
        .bopisEligible(BOPIS_ELIGIBLE)
        .nodeType(NODE_TYPE)
        .nodeLabourTier(NODE_LABOUR_TIER)
        .isActive(IS_ACTIVE_FALSE)
        .latitude(UPDATED_LATITUDE)
        .longitude(LONGITUDE)
        .zipCode(ZIP_CODE)
        .state(STATE)
        .timezone(TIME_ZONE)
        .serviceOptionEligibilities(getServiceOptionEligibilities())
        .customAttributes(
            JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2"))
        .build();
  }

  public List<NodeDto> getNodeDtoList() {
    return Arrays.asList(getNodeDto(NODE_ID), getNodeDto(NODE_ID_2));
  }

  public List<NodeDto> getNodeDtoListV1() {
    return Arrays.asList(
        getNodeDto(NODE_ID), getNodeDto(NODE_ID_2), getNodeDto(NODE_ID_2), getNodeDto(NODE_ID_2));
  }

  private NodeDto getNodeDto(String nodeId) {
    return NodeDto.builder()
        .nodeId(nodeId)
        .orgId(ORG_ID)
        .street(STREET)
        .city(CITY)
        .nodeType(NODE_TYPE)
        .nodeLabourTier(NODE_LABOUR_TIER)
        .customAttributes(
            JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2"))
        .state(STATE)
        .build();
  }

  public NodeDomainDto getNodeDomainDto(String nodeId) {
    return NodeDomainDto.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(BOPIS_ELIGIBLE)
        .city(CITY)
        .country(COUNTRY)
        .nodeType(NODE_TYPE)
        .nodeLabourTier(NODE_LABOUR_TIER)
        .isActive(IS_ACTIVE)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .zipCode(ZIP_CODE)
        .state(STATE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .serviceOptionEligibilities(getServiceOptionEligibilities())
        .customAttributes(
            JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2"))
        .startWorkingTime("08:00")
        .lastWorkingTime("16:00")
        .build();
  }

  public List<NodeDomainDto> getNodeDomainDtoListV1() {
    return Arrays.asList(
        getNodeDomainDto(NODE_ID),
        getNodeDomainDto(NODE_ID_2),
        getNodeDomainDto(NODE_ID_2),
        getNodeDomainDto(NODE_ID_2));
  }

  public List<NodeDomainDto> getNodeDomainDtoList() {
    return Arrays.asList(getNodeDomainDto(NODE_ID), getNodeDomainDto(NODE_ID_2));
  }

  public Page<NodeDto> getNodeDtoPage(
      int totalPages, List<NodeDto> nodeDtoList, int totalElements) {
    return new Page<>() {
      @Override
      public int getTotalPages() {
        return totalPages;
      }

      @Override
      public long getTotalElements() {
        return totalElements;
      }

      @Override
      public <U> Page<U> map(Function<? super NodeDto, ? extends U> converter) {
        return null;
      }

      @Override
      public int getNumber() {
        return 0;
      }

      @Override
      public int getSize() {
        return 0;
      }

      @Override
      public int getNumberOfElements() {
        return 0;
      }

      @Override
      public List<NodeDto> getContent() {
        return nodeDtoList;
      }

      @Override
      public boolean hasContent() {
        return false;
      }

      @Override
      public Sort getSort() {
        return null;
      }

      @Override
      public boolean isFirst() {
        return false;
      }

      @Override
      public boolean isLast() {
        return false;
      }

      @Override
      public boolean hasNext() {
        return false;
      }

      @Override
      public boolean hasPrevious() {
        return false;
      }

      @Override
      public Pageable nextPageable() {
        return null;
      }

      @Override
      public Pageable previousPageable() {
        return null;
      }

      @Override
      public Iterator<NodeDto> iterator() {
        return null;
      }
    };
  }

  public PageParams getPageParams(
      Optional<Integer> pageNo,
      Optional<Integer> pageSize,
      Optional<String> sortBy,
      Optional<String> sortOrder) {
    PageParams pageParams = new PageParams();
    pageParams.setPageNo(pageNo);
    pageParams.setPageSize(pageSize);
    pageParams.setSortBy(sortBy);
    pageParams.setSortOrder(sortOrder);
    return pageParams;
  }

  public PageParams getPageParamsWithoutPageSize(
      Optional<Integer> pageNo, Optional<String> sortBy, Optional<String> sortOrder) {
    PageParams pageParams = new PageParams();
    pageParams.setPageNo(pageNo);
    pageParams.setSortBy(sortBy);
    pageParams.setSortOrder(sortOrder);
    return pageParams;
  }

  public Map<String, Boolean> getServiceOptionEligibilities() {
    return Map.of(
        "sdndEligible", Boolean.TRUE,
        "expressEligible", Boolean.TRUE,
        "nextdayEligible", Boolean.TRUE);
  }

  public List<NodeCacheKeyDto> getNodeCacheKeysDtoList() {
    NodeCacheKeyDto nodeCacheKeyDto1 =
        NodeCacheKeyDto.builder().nodeId(NODE_ID).orgId(ORG_ID).build();

    NodeCacheKeyDto nodeCacheKeyDto2 =
        NodeCacheKeyDto.builder().nodeId(NODE_ID_2).orgId(ORG_ID).build();

    return List.of(nodeCacheKeyDto1, nodeCacheKeyDto2);
  }

  public BaseResponse<TenantConfigdataResponse> getTenantConfigdataBaseResponse() {
    TenantConfigdataResponse tenantConfigdataResponse =
        TenantConfigdataResponse.builder().configKey(CONFIG_KEY).configValue(CONFIG_VALUE).build();
    return BaseResponse.builder().payload(tenantConfigdataResponse).build();
  }
}
