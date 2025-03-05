/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.carrier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.nextuple.carrier.domain.dto.CarrierCacheKeyDto;
import com.nextuple.carrier.domain.inbound.CarrierServiceRequest;
import com.nextuple.carrier.domain.inbound.CarrierServiceUpdateRequest;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.carrier.persistence.domain.CarrierServiceDomainDto;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.configuration.outbound.TenantConfigdataResponse;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.carrier.domain.outbound.NodeCarriersResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class TestUtil {
  public static final String NODE_ID = "node-1";
  public static final String ORG_ID = "org-1";
  public static final String CARRIER_ID = "carrier-1";
  public static final String CARRIER_SERVICE_ID = "carrier-service-1";
  public static final String CARRIER_SERVICE_ID_2 = "carrier-service-2";
  public static final String CARRIER_NAME = "carrier-name-1";
  public static final String SERVICE_NAME = "service-name-1";
  public static final String SERVICE_OPTIONS = "service-options-1";
  public static final String SERVICE_OPTIONS_2 = "service-options-2";
  private static final JsonNode CUSTOM_ATTRIBUTES =
      JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2");
  public static final String SORT_BY = "carrierId";
  public static final String SORT_ORDER_DESC = "desc";
  public static final String SORT_ORDER_ASC = "ASC";
  private static final String CARRIER_ID_2 = "carrier-2";
  public static final String CONFIG_KEY = "service-options";
  public static final String CONFIG_VALUE = "SDND, EXPRESS, STANDARD";

  public CarrierServiceRequest getCarrierServiceRequest() {
    return CarrierServiceRequest.builder()
        .orgId(ORG_ID)
        .carrierId(CARRIER_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public CarrierServiceResponse getCarrierServiceResponse() {
    return CarrierServiceResponse.builder()
        .orgId(ORG_ID)
        .carrierId(CARRIER_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public BaseResponse<List<NodeCarrierResponse>> getEmptyResponseOfNodeCarrier() {
    ArrayList<NodeCarriersResponse> response = new ArrayList<>();

    return BaseResponse.builder()
        .message("Build empty Node-Carrier details")
        .success(true)
        .payload(response)
        .build();
  }

  public BaseResponse<List<NodeCarrierResponse>> getBaseResponseOfNodeCarrier() {
    return BaseResponse.builder()
        .message("Node-Carrier details fetched successfully")
        .success(true)
        .payload(getNodeCarrierResponse())
        .build();
  }

  public List<NodeCarrierResponse> getNodeCarrierResponse() {
    NodeCarrierResponse nodeCarriersResponse1 =
        NodeCarrierResponse.builder()
            .nodeId(NODE_ID)
            .orgId(ORG_ID)
            .carrierServiceId(CARRIER_SERVICE_ID)
            .serviceOption(SERVICE_OPTIONS)
            .lastPickupTime("5:00")
            .build();

    NodeCarrierResponse nodeCarriersResponse2 =
        NodeCarrierResponse.builder()
            .nodeId(NODE_ID)
            .orgId(ORG_ID)
            .carrierServiceId(CARRIER_SERVICE_ID_2)
            .serviceOption(SERVICE_OPTIONS_2)
            .lastPickupTime("11:00")
            .build();

    return Arrays.asList(nodeCarriersResponse1, nodeCarriersResponse2);
  }

  public CarrierServiceUpdateRequest getCarrierServiceUpdateRequest() {
    return CarrierServiceUpdateRequest.builder()
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .build();
  }

  public CarrierServiceResponse getCarrierServiceUpdateResponse() {
    return CarrierServiceResponse.builder()
        .orgId(ORG_ID)
        .carrierId(CARRIER_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public CarrierServiceDomainDto getCarrierServiceDomainDto() {
    return CarrierServiceDomainDto.builder()
        .orgId(ORG_ID)
        .carrierId(CARRIER_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public CarrierServiceResponse getCarrierServiceResponse2() {
    return CarrierServiceResponse.builder()
        .orgId(ORG_ID)
        .carrierId(CARRIER_ID_2)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public List<CarrierServiceResponse> getCarrierServiceResponseList() {
    return List.of(getCarrierServiceResponse(), getCarrierServiceResponse2());
  }

  public List<CarrierServiceDomainDto> getCarrierServiceDomainDtoList() {
    return List.of(getCarrierServiceDomainDto());
  }

  public CarrierServiceDomainDto getCarrierServiceEntity2() {
    return CarrierServiceDomainDto.builder()
        .orgId(ORG_ID)
        .carrierId(CARRIER_ID_2)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public List<CarrierServiceDomainDto> getCarrierServiceEntityList() {
    return List.of(getCarrierServiceDomainDto(), getCarrierServiceEntity2());
  }

  public Page<CarrierServiceResponse> createPageCarrierServiceResponse(
      int totalPage, List<CarrierServiceResponse> carrierServiceResponses, int totalElements) {
    return new Page<CarrierServiceResponse>() {
      @Override
      public int getTotalPages() {
        return totalPage;
      }

      @Override
      public long getTotalElements() {
        return totalElements;
      }

      @Override
      public <U> Page<U> map(Function<? super CarrierServiceResponse, ? extends U> converter) {
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
      public List<CarrierServiceResponse> getContent() {
        return carrierServiceResponses;
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
      public Iterator<CarrierServiceResponse> iterator() {
        return null;
      }
    };
  }

  public Page<CarrierServiceDomainDto> createPageCarrierServiceDomainDto(
      int totalPage, List<CarrierServiceDomainDto> carrierServiceDomainDto, int totalElements) {
    return new Page<CarrierServiceDomainDto>() {
      @Override
      public int getTotalPages() {
        return totalPage;
      }

      @Override
      public long getTotalElements() {
        return totalElements;
      }

      @Override
      public <U> Page<U> map(Function<? super CarrierServiceDomainDto, ? extends U> converter) {
        List<U> content =
            carrierServiceDomainDto.stream().map(converter).collect(Collectors.toList());
        return new PageImpl<>(
            content, PageRequest.of(0, carrierServiceDomainDto.size()), totalElements);
      }

      @Override
      public int getNumber() {
        return 0;
      }

      @Override
      public int getSize() {
        return carrierServiceDomainDto.size();
      }

      @Override
      public int getNumberOfElements() {
        return carrierServiceDomainDto.size();
      }

      @Override
      public List<CarrierServiceDomainDto> getContent() {
        return carrierServiceDomainDto;
      }

      @Override
      public boolean hasContent() {
        return !carrierServiceDomainDto.isEmpty();
      }

      @Override
      public Sort getSort() {
        return null;
      }

      @Override
      public boolean isFirst() {
        return true;
      }

      @Override
      public boolean isLast() {
        return true;
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
      public Iterator<CarrierServiceDomainDto> iterator() {
        return carrierServiceDomainDto.iterator();
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

  public List<CarrierCacheKeyDto> getCarrierCacheKeyDtoList() {
    CarrierCacheKeyDto carrierCacheKeyDto1 =
        CarrierCacheKeyDto.builder()
            .orgId(ORG_ID)
            .carrierId(CARRIER_ID)
            .carrierServiceId(CARRIER_SERVICE_ID)
            .build();

    CarrierCacheKeyDto carrierCacheKeyDto2 =
        CarrierCacheKeyDto.builder()
            .orgId(ORG_ID)
            .carrierId(CARRIER_ID_2)
            .carrierServiceId(CARRIER_SERVICE_ID)
            .build();

    return List.of(carrierCacheKeyDto1, carrierCacheKeyDto2);
  }

  public BaseResponse<TenantConfigdataResponse> getTenantConfigdataBaseResponse() {
    TenantConfigdataResponse tenantConfigdataResponse =
        TenantConfigdataResponse.builder().configKey(CONFIG_KEY).configValue(CONFIG_VALUE).build();
    return BaseResponse.builder().payload(tenantConfigdataResponse).build();
  }
}
