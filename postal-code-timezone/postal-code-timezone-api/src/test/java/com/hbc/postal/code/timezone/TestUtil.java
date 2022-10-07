package com.hbc.postal.code.timezone;

import com.hbc.postal.code.timezone.api.domain.dto.PostalCodePrefixDto;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.hbc.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.hbc.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
import com.hbc.postal.code.timezone.domain.entity.PostalCodeTimezoneEntity;
import com.hbc.postal.code.timezone.domain.mapper.PostalCodeTimezoneMapper;
import java.util.List;
import org.mapstruct.factory.Mappers;

public class TestUtil {
  public static final String ORG_ID = "ABC";
  public static final String POSTAL_CODE_PREFIX = "XYZ";
  public static final String POSTAL_CODE_PREFIX_2 = "ABC";
  public static final String COUNTRY = "CANADA";
  public static final String STATE = "ONTARIO";
  public static final String CITY = "TORONTO";
  public static final String LATITUDE = "LATITUDE";
  public static final String LONGITUDE = "LONGITUDE";
  public static final String TIME_ZONE = "TIMEZONE";
  public static final String STATUS_CODE = "Status code";
  private static final PostalCodeTimezoneMapper INSTANCE =
      Mappers.getMapper(PostalCodeTimezoneMapper.class);

  public CreatePostalCodeTimezoneRequest getCreatePostalCodeTimezoneRequest() {
    return CreatePostalCodeTimezoneRequest.builder()
        .orgId(ORG_ID)
        .postalCodePrefix(POSTAL_CODE_PREFIX)
        .country(COUNTRY)
        .state(STATE)
        .city(CITY)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .timeZone(TIME_ZONE)
        .build();
  }

  public PostalCodeTimezoneDto getPostalCodeTimezoneDto() {
    return PostalCodeTimezoneDto.builder()
        .orgId(ORG_ID)
        .postalCodePrefix(POSTAL_CODE_PREFIX)
        .country(COUNTRY)
        .state(STATE)
        .city(CITY)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .timeZone(TIME_ZONE)
        .build();
  }

  public PostalCodeTimezoneEntity getPostalCodeTimezoneEntity() {
    return INSTANCE.convertToPostalCodeTimezoneEntity(getPostalCodeTimezoneDto());
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
        .postalCodePrefix(List.of(POSTAL_CODE_PREFIX, POSTAL_CODE_PREFIX_2))
        .build();
  }
}
