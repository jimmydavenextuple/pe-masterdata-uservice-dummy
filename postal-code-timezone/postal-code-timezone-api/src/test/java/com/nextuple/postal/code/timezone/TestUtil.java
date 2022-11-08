package com.nextuple.postal.code.timezone;

import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodePrefixDto;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.api.domain.projection.MarketRegionProjection;
import com.nextuple.postal.code.timezone.domain.entity.PostalCodeTimezoneEntity;
import com.nextuple.postal.code.timezone.domain.mapper.PostalCodeTimezoneMapper;
import java.util.Date;
import java.util.List;
import org.mapstruct.factory.Mappers;

public class TestUtil {
  public static final String ORG_ID = "ABC";
  public static final String POSTAL_CODE_PREFIX = "XYZ";
  public static final String POSTAL_CODE_PREFIX_2 = "ABC";
  public static final String COUNTRY = "CA";
  public static final String STATE = "ONTARIO";
  public static final String CITY = "TORONTO";
  public static final String LATITUDE = "LATITUDE";
  public static final String LONGITUDE = "LONGITUDE";
  public static final String TIME_ZONE = "America/Whitehorse";
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
          public long getNoOfPostalCodePrefixes() {
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
}
