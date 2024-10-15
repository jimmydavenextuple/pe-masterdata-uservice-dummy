/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone;

import com.nextuple.common.pojo.PageParams;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionDto;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodePrefixDto;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.api.domain.inbound.CustomRegionRequest;
import com.nextuple.postal.code.timezone.api.domain.inbound.PostalCodeRequest;
import com.nextuple.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.api.domain.outbound.CustomRegionResponse;
import com.nextuple.postal.code.timezone.api.domain.outbound.PostalCodeResponse;
import com.nextuple.postal.code.timezone.api.domain.projection.MarketRegionProjection;
import com.nextuple.postal.code.timezone.domain.mapper.CustomRegionMapper;
import com.nextuple.postal.code.timezone.domain.mapper.PostalCodeMapper;
import com.nextuple.postal.code.timezone.domain.mapper.PostalCodeTimezoneMapper;
import com.nextuple.postal.code.timezone.persistence.domain.CustomRegionDomainDto;
import com.nextuple.postal.code.timezone.persistence.domain.PostalCodeDomainDto;
import com.nextuple.postal.code.timezone.persistence.domain.PostalCodeTimezoneDomainDto;
import java.util.*;
import java.util.function.Function;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class TestUtil {
  public static final String ORG_ID = "ABC";
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
        .build();
  }

  public CustomRegionRequest getCreateCustomRegionRequest() {
    return CustomRegionRequest.builder()
        .orgId(ORG_ID)
        .customRegionName(CUSTOM_REGION_NAME)
        .codes(PARTIAL_CODES)
        .customRegionDescription(CUSTOM_REGION_DESC)
        .id(ID_2)
        .build();
  }

  public CustomRegionRequest getCreateCustomRegionRequest4() {
    return CustomRegionRequest.builder()
        .orgId(ORG_ID)
        .customRegionName(CUSTOM_REGION_NAME)
        .codes(PARTIAL_CODES)
        .customRegionDescription(CUSTOM_REGION_DESC)
        .id(ID)
        .build();
  }

  public CustomRegionRequest getCreateCustomRegionRequest2() {
    return CustomRegionRequest.builder()
        .orgId(ORG_ID)
        .customRegionName(CUSTOM_REGION_NAME)
        .codes(PARTIAL_CODES2)
        .customRegionDescription(CUSTOM_REGION_DESC)
        .id(ID_2)
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
        .build();
  }

  public CustomRegionRequest getCreateCustomRegionEmptyCodesRequest() {
    return CustomRegionRequest.builder()
        .orgId(ORG_ID)
        .customRegionName(CUSTOM_REGION_NAME)
        .codes(EMPTY_CODES)
        .customRegionDescription(CUSTOM_REGION_DESC)
        .id(ID)
        .build();
  }

  public CustomRegionResponse getCustomRegionResponse() {
    return CustomRegionResponse.builder()
        .orgId(ORG_ID)
        .customRegionName(CUSTOM_REGION_NAME)
        .codes(PARTIAL_CODES)
        .customRegionDescription(CUSTOM_REGION_DESC)
        .id(ID)
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
        .build();
  }

  public List<CustomRegionDomainDto> getCustomRegionEntityList() {
    return Arrays.asList(getCustomRegionEntity(), getCustomRegionEntity2());
  }
}
