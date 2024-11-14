/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.persistence.service.impl;

import com.nextuple.postal.code.timezone.api.domain.projection.CustomRegionProjection;
import com.nextuple.postal.code.timezone.api.domain.projection.MarketRegionProjection;
import com.nextuple.postal.code.timezone.persistence.domain.CustomRegionDomainDto;
import com.nextuple.postal.code.timezone.persistence.domain.PostalCodeDomainDto;
import com.nextuple.postal.code.timezone.persistence.domain.PostalCodeTimezoneDomainDto;
import com.nextuple.postal.code.timezone.persistence.entity.CustomRegionEntity;
import com.nextuple.postal.code.timezone.persistence.entity.PostalCodeEntity;
import com.nextuple.postal.code.timezone.persistence.entity.PostalCodeTimezoneEntity;
import com.nextuple.postal.code.timezone.persistence.mapper.CustomRegionEntityMapper;
import com.nextuple.postal.code.timezone.persistence.mapper.PostalCodeEntityMapper;
import com.nextuple.postal.code.timezone.persistence.mapper.PostalCodeTimezoneEntityMapper;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class TestUtil {
  public static final String ORG_ID = "ABC";
  public static final List<String> PARTIAL_CODES = Arrays.asList("T2P", "T3P");
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
  public static final String SORT_BY = "id";
  public static final String UPLOAD_DATE = "2024-10-10";
  private static final PostalCodeTimezoneEntityMapper POSTAL_CODE_TIMEZONE_ENTITY_MAPPER =
      Mappers.getMapper(PostalCodeTimezoneEntityMapper.class);

  private static final CustomRegionEntityMapper CUSTOM_REGION_ENTITY_MAPPER =
      Mappers.getMapper(CustomRegionEntityMapper.class);

  private static final PostalCodeEntityMapper POSTAL_CODE_ENTITY_MAPPER =
      Mappers.getMapper(PostalCodeEntityMapper.class);

  public CustomRegionEntity getCustomRegionEntity() {
    return CustomRegionEntity.builder()
        .orgId(ORG_ID)
        .customRegionName(CUSTOM_REGION_NAME)
        .codes(PARTIAL_CODES)
        .customRegionDescription(CUSTOM_REGION_DESC)
        .id(ID_2)
        .build();
  }

  public CustomRegionDomainDto getCustomRegionDomainDto() {
    return CUSTOM_REGION_ENTITY_MAPPER.toDomain(getCustomRegionEntity());
  }

  public CustomRegionEntity getCustomRegionEntity2() {
    return CustomRegionEntity.builder()
        .orgId(ORG_ID)
        .customRegionName(CUSTOM_REGION_NAME)
        .codes(PARTIAL_CODES)
        .customRegionDescription(CUSTOM_REGION_DESC)
        .id(ID)
        .build();
  }

  public PostalCodeTimezoneEntity getPostalCodeTimezoneEntity() {
    return PostalCodeTimezoneEntity.builder()
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

  public List<PostalCodeTimezoneEntity> getPostalCodeTimezoneEntityList() {
    return List.of(getPostalCodeTimezoneEntity());
  }

  public PostalCodeTimezoneDomainDto getPostalCodeTimezoneDomainDto() {

    return POSTAL_CODE_TIMEZONE_ENTITY_MAPPER.toDomain(getPostalCodeTimezoneEntity());
  }

  public List<PostalCodeTimezoneDomainDto> getPostalCodeTimezoneDomainDtoList() {

    return POSTAL_CODE_TIMEZONE_ENTITY_MAPPER.toDomain(getPostalCodeTimezoneEntityList());
  }

  public PostalCodeEntity getPostalCodeEntity() {
    return PostalCodeEntity.builder()
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

  public List<PostalCodeEntity> getPostalCodeEntityList() {
    return List.of(getPostalCodeEntity());
  }

  public PostalCodeDomainDto getPostalCodeDomainDto() {
    return POSTAL_CODE_ENTITY_MAPPER.toDomain(getPostalCodeEntity());
  }

  public List<PostalCodeDomainDto> getPostalCodeDomainDtoList() {
    return POSTAL_CODE_ENTITY_MAPPER.toDomain(getPostalCodeEntityList());
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

  public List<CustomRegionEntity> getCustomRegionEntityList() {
    return Arrays.asList(getCustomRegionEntity(), getCustomRegionEntity2());
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
}
