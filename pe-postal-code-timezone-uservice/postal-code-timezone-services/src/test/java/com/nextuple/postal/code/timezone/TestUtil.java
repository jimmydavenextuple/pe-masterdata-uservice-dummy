/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionDto;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionInfo;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodePrefixDto;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.api.domain.inbound.CustomRegionRequest;
import com.nextuple.postal.code.timezone.api.domain.inbound.DeleteCustomRegionGeozonesRequest;
import com.nextuple.postal.code.timezone.api.domain.inbound.PostalCodeRequest;
import com.nextuple.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.api.domain.outbound.CustomRegionResponse;
import com.nextuple.postal.code.timezone.api.domain.outbound.PostalCodeResponse;
import com.nextuple.postal.code.timezone.api.domain.projection.CustomRegionProjection;
import com.nextuple.postal.code.timezone.api.domain.projection.MarketRegionProjection;
import com.nextuple.postal.code.timezone.domain.mapper.CustomRegionMapper;
import com.nextuple.postal.code.timezone.domain.mapper.PostalCodeMapper;
import com.nextuple.postal.code.timezone.domain.mapper.PostalCodeTimezoneMapper;
import com.nextuple.postal.code.timezone.persistence.domain.CustomRegionDomainDto;
import com.nextuple.postal.code.timezone.persistence.domain.PostalCodeDomainDto;
import com.nextuple.postal.code.timezone.persistence.domain.PostalCodeTimezoneDomainDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class TestUtil {
  public static final String ORG_ID = "ABC";
  public static final String UPLOAD_DATE = "2024-10-10";
  public static final List<String> PARTIAL_CODES = Arrays.asList("T2P", "T3P");
  public static final List<String> PARTIAL_CODES2 = Arrays.asList("T2P");
  public static final List<String> PARTIAL_CODES3 = Arrays.asList("T2P", "T3P", "T4P");
  public static final String REGION_ID = "CRID1";
  public static final List<String> FULL_CODES = Arrays.asList("T2P101", "T3P102");
  public static final List<String> FULL_CODES2 = Arrays.asList("T2P101");
  public static final List<String> FULL_CODES3 = Arrays.asList("T2P101", "T3P102", "T3P103");
  public static final List<String> EMPTY_CODES = new ArrayList<>();

  public static final String ID = "CRID1";
  public static final String ID_2 = "CRID2";
  public static final List<String> CODES1 = Arrays.asList("T2P", "T3P");
  public static final List<String> CODES2 = Arrays.asList("S2P", "S3P");
  public static final String CUSTOM_REGION_DESC = "ABC";
  public static final String CUSTOM_REGION_NAME = "CR1";
  public static final String ZIP_CODE_PREFIX = "X2H";
  public static final String ZIP_CODE = "X2H2YZ";
  public static final String POSTAL_CODE_PREFIX_2 = "ABC";
  public static final String COUNTRY = "CA";
  public static final String STATE = "ONTARIO";
  public static final String CITY = "TORONTO";
  public static final String LATITUDE = "LATITUDE";
  public static final String LONGITUDE = "LONGITUDE";
  public static final String TIME_ZONE = "America/Whitehorse";
  public static final String STATUS_CODE = "Status code";
  public static final String SORT_BY = "id";
  public static final String SORT_ORDER_DESC = "DESC";
  public static final String DEFAULT_SORT_ORDER = "ASC";
  private static final PostalCodeTimezoneMapper INSTANCE =
      Mappers.getMapper(PostalCodeTimezoneMapper.class);

  private static final CustomRegionMapper CUSTOM_REGION_MAPPER =
      Mappers.getMapper(CustomRegionMapper.class);

  private static final PostalCodeMapper POSTAL_CODE_MAPPER =
      Mappers.getMapper(PostalCodeMapper.class);

  public static final JsonNode CUSTOM_ATTRIBUTES =
      JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2");

  public CreatePostalCodeTimezoneRequest getCreatePostalCodeTimezoneRequest() {
    return CreatePostalCodeTimezoneRequest.builder()
        .orgId(ORG_ID)
        .zipCodePrefix(ZIP_CODE_PREFIX)
        .country(COUNTRY)
        .state(STATE)
        .city(CITY)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .timeZone(TIME_ZONE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public PostalCodeRequest getPostalCodeRequest() {
    return PostalCodeRequest.builder()
        .orgId(ORG_ID)
        .zipCode(ZIP_CODE)
        .zipCodePrefix(ZIP_CODE_PREFIX)
        .country(COUNTRY)
        .state(STATE)
        .city(CITY)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .timeZone(TIME_ZONE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public CustomRegionRequest getCreateCustomRegionRequest() {
    return CustomRegionRequest.builder()
        .orgId(ORG_ID)
        .customRegionName(CUSTOM_REGION_NAME)
        .codes(PARTIAL_CODES)
        .customRegionDescription(CUSTOM_REGION_DESC)
        .id(ID_2)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public CustomRegionRequest getCreateCustomRegionRequest4() {
    return CustomRegionRequest.builder()
        .orgId(ORG_ID)
        .customRegionName(CUSTOM_REGION_NAME)
        .codes(PARTIAL_CODES)
        .customRegionDescription(CUSTOM_REGION_DESC)
        .id(ID)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public CustomRegionRequest getCreateCustomRegionRequest2() {
    return CustomRegionRequest.builder()
        .orgId(ORG_ID)
        .customRegionName(CUSTOM_REGION_NAME)
        .codes(PARTIAL_CODES2)
        .customRegionDescription(CUSTOM_REGION_DESC)
        .id(ID_2)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public CustomRegionRequest getCreateCustomRegionRequest3() {
    return CustomRegionRequest.builder()
        .orgId(ORG_ID)
        .customRegionName(CUSTOM_REGION_NAME)
        .codes(PARTIAL_CODES3)
        .customRegionDescription(CUSTOM_REGION_DESC)
        .id(ID_2)
        .build();
  }

  public CustomRegionRequest getCreateCustomRegionFullRequest() {
    return CustomRegionRequest.builder()
        .orgId(ORG_ID)
        .customRegionName(CUSTOM_REGION_NAME)
        .codes(FULL_CODES)
        .customRegionDescription(CUSTOM_REGION_DESC)
        .id(ID)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public CustomRegionRequest getCreateCustomRegionFullRequest2() {
    return CustomRegionRequest.builder()
        .orgId(ORG_ID)
        .customRegionName(CUSTOM_REGION_NAME)
        .codes(FULL_CODES2)
        .customRegionDescription(CUSTOM_REGION_DESC)
        .id(ID)
        .build();
  }

  public CustomRegionRequest getCreateCustomRegionFullRequest3() {
    return CustomRegionRequest.builder()
        .orgId(ORG_ID)
        .customRegionName(CUSTOM_REGION_NAME)
        .codes(FULL_CODES3)
        .customRegionDescription(CUSTOM_REGION_DESC)
        .id(ID)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public CustomRegionRequest getCreateCustomRegionEmptyCodesRequest() {
    return CustomRegionRequest.builder()
        .orgId(ORG_ID)
        .customRegionName(CUSTOM_REGION_NAME)
        .codes(EMPTY_CODES)
        .customRegionDescription(CUSTOM_REGION_DESC)
        .id(ID)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public CustomRegionResponse getCustomRegionResponse() {
    return CustomRegionResponse.builder()
        .orgId(ORG_ID)
        .customRegionName(CUSTOM_REGION_NAME)
        .codes(PARTIAL_CODES)
        .customRegionDescription(CUSTOM_REGION_DESC)
        .id(ID)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public CustomRegionResponse getCustomRegionResponseCrId2() {
    return CustomRegionResponse.builder()
        .orgId(ORG_ID)
        .customRegionName(CUSTOM_REGION_NAME)
        .codes(List.of("T2P", "T3P", "X2H"))
        .customRegionDescription(CUSTOM_REGION_DESC)
        .id(ID_2)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public CustomRegionDomainDto getCustomRegionEntity() {
    return CUSTOM_REGION_MAPPER.toCustomRegionEntity(getCreateCustomRegionRequest());
  }

  public CustomRegionDomainDto getCustomRegionEntity2() {
    return CUSTOM_REGION_MAPPER.toCustomRegionEntity(getCreateCustomRegionRequest4());
  }

  public CustomRegionDomainDto getCustomRegionFullEntity() {
    return CUSTOM_REGION_MAPPER.toCustomRegionEntity(getCreateCustomRegionFullRequest());
  }

  public PostalCodeTimezoneDto getPostalCodeTimezoneDto() {
    return PostalCodeTimezoneDto.builder()
        .orgId(ORG_ID)
        .zipCodePrefix(ZIP_CODE_PREFIX)
        .country(COUNTRY)
        .state(STATE)
        .city(CITY)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .timeZone(TIME_ZONE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public PostalCodeResponse getPostalCodeResponse() {
    return PostalCodeResponse.builder()
        .orgId(ORG_ID)
        .zipCode(ZIP_CODE)
        .zipCodePrefix(ZIP_CODE_PREFIX)
        .country(COUNTRY)
        .state(STATE)
        .city(CITY)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .timeZone(TIME_ZONE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public PostalCodeTimezoneDomainDto getPostalCodeTimezoneEntity() {
    return INSTANCE.convertToPostalCodeTimezoneEntity(getPostalCodeTimezoneDto());
  }

  public PostalCodeDomainDto getPostalCodeEntity() {
    return POSTAL_CODE_MAPPER.convertToPostalCodeEntity(getPostalCodeRequest());
  }

  public PostalCodeDomainDto getPostalCodeEntityWithRegions() {
    PostalCodeDomainDto postalCodeEntity =
        POSTAL_CODE_MAPPER.convertToPostalCodeEntity(getPostalCodeRequest());
    postalCodeEntity.setCustomRegion(REGION_ID);
    return postalCodeEntity;
  }

  public UpdatePostalCodeTimezoneRequest getUpdatePostalCodeTimezoneRequest() {
    return UpdatePostalCodeTimezoneRequest.builder()
        .country(COUNTRY)
        .state(STATE)
        .city(CITY)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .timeZone(TIME_ZONE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public PostalCodePrefixDto getPostalCodePrefixDto() {
    return PostalCodePrefixDto.builder()
        .state(STATE)
        .zipCodePrefix(List.of(ZIP_CODE_PREFIX, POSTAL_CODE_PREFIX_2))
        .build();
  }

  public List<MarketRegionProjection> getMarketRegion() {
    return List.of(
        new MarketRegionProjection() {
          @Override
          public String getCountry() {

            return "IN";
          }

          @Override
          public long getNoOfStates() {
            return 2;
          }

          @Override
          public long getNoOfCities() {
            return 4;
          }

          @Override
          public long getNoOfZipCodePrefixes() {
            return 1;
          }

          @Override
          public String getUploadDate() {
            return new Date().toString();
          }

          @Override
          public void setUploadDate(String v) {}
        });
  }

  public Page<CustomRegionDto> getCustomRegionDtoPage(
      int totalPages, List<CustomRegionDto> customRegionDtoList, int totalElements) {
    return new Page<CustomRegionDto>() {
      @Override
      public int getTotalPages() {
        return totalPages;
      }

      @Override
      public long getTotalElements() {
        return totalElements;
      }

      @Override
      public <U> Page<U> map(Function<? super CustomRegionDto, ? extends U> converter) {
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
      public List<CustomRegionDto> getContent() {
        return customRegionDtoList;
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
      public Iterator<CustomRegionDto> iterator() {
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

  public List<CustomRegionDto> getCustomRegionDtoList() {
    return Arrays.asList(getCustomRegionDto("region1"), getCustomRegionDto("region2"));
  }

  private CustomRegionDto getCustomRegionDto(String regionId) {
    return CustomRegionDto.builder()
        .id(regionId)
        .orgId(ORG_ID)
        .customRegionName(CUSTOM_REGION_NAME)
        .customRegionDescription(CUSTOM_REGION_DESC)
        .codes(PARTIAL_CODES)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public List<CustomRegionDomainDto> getCustomRegionEntityList() {
    return Arrays.asList(getCustomRegionEntity(), getCustomRegionEntity2());
  }

  public DeleteCustomRegionGeozonesRequest getDeleteCompleteCustomRegionRequest() {
    return DeleteCustomRegionGeozonesRequest.builder().codes(PARTIAL_CODES).build();
  }

  public List<PostalCodeDomainDto> getPostalCodeEntityList1() {
    PostalCodeDomainDto domainZipS1P = getPostalCodeEntity();
    domainZipS1P.setZipCodePrefix("S1P");
    PostalCodeDomainDto domainZipS2P = getPostalCodeEntity();
    domainZipS2P.setZipCodePrefix("S2P");
    return List.of(domainZipS1P, domainZipS2P);
  }

  public List<PostalCodeDomainDto> getPostalCodeEntityList() {
    PostalCodeDomainDto domainZipT2P = getPostalCodeEntity();
    domainZipT2P.setZipCodePrefix("T2P");
    PostalCodeDomainDto domainZipT3P = getPostalCodeEntity();
    domainZipT3P.setZipCodePrefix("T3P");
    return List.of(domainZipT2P, domainZipT3P);
  }

  public List<CustomRegionInfo> getTwoCustomRegionInfoList() {
    CustomRegionInfo customRegionInfo1 =
        CustomRegionInfo.builder()
            .customRegionId(ID)
            .customRegionDescription(CUSTOM_REGION_DESC)
            .customRegionName(CUSTOM_REGION_NAME)
            .zipCodes(PARTIAL_CODES)
            .citiesCount(1)
            .statesCount(1)
            .zipCodePrefixesCount(2)
            .orgId(ORG_ID)
            .uploadDate("2024-10-10")
            .build();

    CustomRegionInfo customRegionInfo2 =
        CustomRegionInfo.builder()
            .customRegionId(ID_2)
            .customRegionDescription(CUSTOM_REGION_DESC)
            .customRegionName(CUSTOM_REGION_NAME)
            .zipCodes(List.of("S1P"))
            .citiesCount(1)
            .statesCount(1)
            .zipCodePrefixesCount(1)
            .orgId(ORG_ID)
            .uploadDate("2024-10-10")
            .build();
    return List.of(customRegionInfo1, customRegionInfo2);
  }

  public CustomRegionProjection getCustomRegionProjection(
      String orgId,
      String customRegionId,
      String uploadDate,
      Integer nosOfStates,
      Integer nosOfCities,
      Integer nosOfZipCodePrefixes) {
    return new CustomRegionProjection() {
      @Override
      public String getOrgId() {
        return orgId;
      }

      @Override
      public String getCustomRegionId() {
        return customRegionId;
      }

      @Override
      public long getStatesCount() {
        return nosOfStates;
      }

      @Override
      public long getCitiesCount() {
        return nosOfCities;
      }

      @Override
      public long getZipCodePrefixesCount() {
        return nosOfZipCodePrefixes;
      }

      @Override
      public String getUploadDate() {
        return uploadDate;
      }
    };
  }

  public Page<CustomRegionProjection> getCustomRegionProjectionPage() {

    return new Page<CustomRegionProjection>() {
      @Override
      public int getTotalPages() {
        return 0;
      }

      @Override
      public long getTotalElements() {
        return 0;
      }

      @Override
      public <U> Page<U> map(Function<? super CustomRegionProjection, ? extends U> converter) {
        return null;
      }

      @Override
      public int getNumber() {
        return 0;
      }

      @Override
      public int getSize() {
        return 5;
      }

      @Override
      public int getNumberOfElements() {
        return 0;
      }

      @Override
      public List<CustomRegionProjection> getContent() {
        return List.of(
            getCustomRegionProjection(ORG_ID, ID, UPLOAD_DATE, 1, 1, 2),
            getCustomRegionProjection(ORG_ID, ID_2, UPLOAD_DATE, 1, 1, 2));
      }

      @Override
      public boolean hasContent() {
        return false;
      }

      @Override
      public Sort getSort() {
        return Sort.by(Sort.Direction.ASC, "customRegionId");
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

      @NotNull
      @Override
      public Iterator<CustomRegionProjection> iterator() {
        return null;
      }
    };
  }

  public List<CustomRegionDomainDto> getCustomRegionEntityList2() {
    return List.of(
        getCustomRegionDomainDto(
            ID, CUSTOM_REGION_NAME, CUSTOM_REGION_DESC, CODES1, ORG_ID, CUSTOM_ATTRIBUTES),
        getCustomRegionDomainDto(
            ID_2, CUSTOM_REGION_NAME, CUSTOM_REGION_DESC, CODES2, ORG_ID, CUSTOM_ATTRIBUTES));
  }

  private CustomRegionDomainDto getCustomRegionDomainDto(
      String id,
      String name,
      String description,
      List<String> codes,
      String orgId,
      JsonNode customAttributes) {
    return CustomRegionDomainDto.builder()
        .customRegionDescription(description)
        .customRegionName(name)
        .id(id)
        .codes(codes)
        .orgId(orgId)
        .customAttributes(customAttributes)
        .build();
  }

  public List<PostalCodeDomainDto> getPostalCodeEntityListCountry(String country) {
    return List.of(PostalCodeDomainDto.builder().country(country).build());
  }
}
