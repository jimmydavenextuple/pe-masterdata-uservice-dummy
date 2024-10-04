/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.persistence.util;

import com.nextuple.common.pojo.PageParams;
import com.nextuple.node.persistence.domain.NodeDomainDto;
import com.nextuple.node.persistence.entity.NodeEntity;
import java.util.*;

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
  public static Boolean SHIP_TO_TIME = Boolean.TRUE;
  public static Boolean BOPIS_ELIGIBLE = Boolean.TRUE;
  public static String NODE_TYPE = "MFC";
  public static Boolean IS_ACTIVE = Boolean.TRUE;
  public static final String CONFIG_KEY = "node-types";
  public static final String CONFIG_VALUE = "MFC, Store, FC";
  public static final String NODE_LABOUR_TIER = "tier1";

  public NodeDomainDto getNodeDomainDto() {
    return NodeDomainDto.builder()
        .nodeId(TestUtil.NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .city(CITY)
        .nodeType(NODE_TYPE)
        .nodeLabourTier(NODE_LABOUR_TIER)
        .state(STATE)
        .build();
  }

  public NodeDomainDto getNodeDomainDto(String node) {
    return NodeDomainDto.builder()
        .nodeId(node)
        .orgId(ORG_ID)
        .street(STREET)
        .city(CITY)
        .nodeType(NODE_TYPE)
        .nodeLabourTier(NODE_LABOUR_TIER)
        .state(STATE)
        .build();
  }

  public NodeEntity getNodeEntity() {
    return NodeEntity.builder()
        .nodeId(TestUtil.NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .city(CITY)
        .nodeType(NODE_TYPE)
        .nodeLabourTier(NODE_LABOUR_TIER)
        .state(STATE)
        .build();
  }

  public NodeEntity getNodeEntity(String node) {
    return NodeEntity.builder()
        .nodeId(node)
        .orgId(ORG_ID)
        .street(STREET)
        .city(CITY)
        .nodeType(NODE_TYPE)
        .nodeLabourTier(NODE_LABOUR_TIER)
        .state(STATE)
        .build();
  }

  public List<NodeDomainDto> getNodeDomainDtoListV1() {
    return Arrays.asList(
        getNodeDomainDto(NODE_ID),
        getNodeDomainDto(NODE_ID_2),
        getNodeDomainDto(NODE_ID_2),
        getNodeDomainDto(NODE_ID_2));
  }

  public List<NodeEntity> getNodeEntityListV1() {
    return Arrays.asList(
        getNodeEntity(NODE_ID), getNodeEntity(NODE_ID_2), getNodeEntity(NODE_ID_2));
  }

  public List<NodeDomainDto> getNodeDomainDtoList() {
    return Arrays.asList(getNodeDomainDto(NODE_ID), getNodeDomainDto(NODE_ID_2));
  }

  public List<NodeEntity> getNodeEntityList() {
    return Arrays.asList(getNodeEntity(NODE_ID), getNodeEntity(NODE_ID_2));
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
}
