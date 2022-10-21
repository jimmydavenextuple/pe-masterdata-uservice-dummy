package com.hbc.node;

import com.hbc.common.pojo.PageParams;
import com.hbc.node.domain.dto.NodeCacheKeyDto;
import com.hbc.node.domain.dto.NodeDto;
import com.hbc.node.domain.entity.NodeEntity;
import com.hbc.node.domain.inbound.NodeRequest;
import com.hbc.node.domain.inbound.NodeUpdationRequest;
import com.hbc.node.domain.outbound.NodeResponse;
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
  public static final String PROVINCE = "province-1";
  public static final String POSTAL_CODE = "33666";
  public static final String COUNTRY = "IN";
  public static final String LATITUDE = "43.769912";
  public static final String LONGITUDE = "-79.296678";
  public static final String TIME_ZONE = "America/Toronto";
  public static final String SORT_BY = "nodeId";
  public static final String SORT_ORDER_DESC = "DESC";
  public static final String DEFAULT_SORT_ORDER = "ASC";
  public static Boolean SHIP_TO_TIME = Boolean.TRUE;
  public static Boolean BOPIS_ELIGIBLE = Boolean.TRUE;
  public static Boolean SDND_ELIGIBLE = Boolean.TRUE;
  public static Boolean EXPRESS_ELIGIBLE = Boolean.TRUE;
  public static String NODE_TYPE = "MFC";
  public static Boolean IS_ACTIVE = Boolean.TRUE;

  public NodeEntity getNodeEntity() {
    return NodeEntity.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(BOPIS_ELIGIBLE)
        .city(CITY)
        .country(COUNTRY)
        .nodeType(NODE_TYPE)
        .isActive(IS_ACTIVE)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .postalCode(POSTAL_CODE)
        .province(PROVINCE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .serviceOptionEligibilities(getServiceOptionEligibilities())
        .build();
  }

  public NodeRequest getNodeRequest() {
    return NodeRequest.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(BOPIS_ELIGIBLE)
        .city(CITY)
        .nodeType("Store")
        .country(COUNTRY)
        .nodeType(NODE_TYPE)
        .isActive(IS_ACTIVE)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .postalCode(POSTAL_CODE)
        .province(PROVINCE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .serviceOptionEligibilities(getServiceOptionEligibilities())
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
        .isActive(IS_ACTIVE)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .postalCode(POSTAL_CODE)
        .province(PROVINCE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .serviceOptionEligibilities(getServiceOptionEligibilities())
        .build();
  }

  public NodeUpdationRequest getNodeUpdationRequest() {
    return NodeUpdationRequest.builder()
        .isActive(Boolean.FALSE)
        .city("city-2")
        .country(COUNTRY)
        .nodeType("Store")
        .timezone(TIME_ZONE)
        .latitude("3526.5262")
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
        .isActive(Boolean.FALSE)
        .latitude("3526.5262")
        .longitude(LONGITUDE)
        .postalCode(POSTAL_CODE)
        .province(PROVINCE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .serviceOptionEligibilities(getServiceOptionEligibilities())
        .build();
  }

  public NodeEntity getUpdatedNodeEntity() {
    return NodeEntity.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(BOPIS_ELIGIBLE)
        .city("city-2")
        .country(COUNTRY)
        .nodeType(NODE_TYPE)
        .isActive(Boolean.FALSE)
        .latitude("3526.5262")
        .longitude(LONGITUDE)
        .postalCode(POSTAL_CODE)
        .province(PROVINCE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .serviceOptionEligibilities(getServiceOptionEligibilities())
        .build();
  }

  public NodeEntity getNodeEntity2() {
    return NodeEntity.builder()
        .nodeId(NODE_ID_2)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(BOPIS_ELIGIBLE)
        .city(CITY)
        .country(COUNTRY)
        .nodeType(NODE_TYPE)
        .isActive(IS_ACTIVE)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .postalCode(POSTAL_CODE)
        .province(PROVINCE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .serviceOptionEligibilities(getServiceOptionEligibilities())
        .build();
  }

  public List<NodeEntity> getNodeEntityList() {
    return Arrays.asList(getNodeEntity(), getNodeEntity2());
  }

  public List<NodeDto> getNodeDtoList() {
    return Arrays.asList(getNodeDto(NODE_ID), getNodeDto(NODE_ID_2));
  }

  private NodeDto getNodeDto(String nodeId) {
    return NodeDto.builder()
        .nodeId(nodeId)
        .orgId(ORG_ID)
        .street(STREET)
        .city(CITY)
        .nodeType(NODE_TYPE)
        .province(PROVINCE)
        .build();
  }

  public Page<NodeResponse> getNodeResponsePage(
      int totalPages, List<NodeResponse> nodeResponses, int totalElements) {
    return new Page<NodeResponse>() {
      @Override
      public int getTotalPages() {
        return totalPages;
      }

      @Override
      public long getTotalElements() {
        return totalElements;
      }

      @Override
      public <U> Page<U> map(Function<? super NodeResponse, ? extends U> converter) {
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
      public List<NodeResponse> getContent() {
        return nodeResponses;
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
      public Iterator<NodeResponse> iterator() {
        return null;
      }
    };
  }

  public Page<NodeDto> getNodeDtoPage(
      int totalPages, List<NodeDto> nodeDtoList, int totalElements) {
    return new Page<NodeDto>() {
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
}
