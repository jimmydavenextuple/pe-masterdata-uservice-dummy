package com.hbc.postal.code.timezone;

import static com.hbc.postal.code.timezone.utils.PostalCodeTimezoneConstants.CITY;
import static com.hbc.postal.code.timezone.utils.PostalCodeTimezoneConstants.COUNTRY;
import static com.hbc.postal.code.timezone.utils.PostalCodeTimezoneConstants.LATITUDE;
import static com.hbc.postal.code.timezone.utils.PostalCodeTimezoneConstants.LONGITUDE;
import static com.hbc.postal.code.timezone.utils.PostalCodeTimezoneConstants.ORG_ID;
import static com.hbc.postal.code.timezone.utils.PostalCodeTimezoneConstants.POSTAL_CODE_PREFIX;
import static com.hbc.postal.code.timezone.utils.PostalCodeTimezoneConstants.POSTAL_CODE_PREFIX_2;
import static com.hbc.postal.code.timezone.utils.PostalCodeTimezoneConstants.STATE;
import static com.hbc.postal.code.timezone.utils.PostalCodeTimezoneConstants.TIME_ZONE;

import com.hbc.postal.code.timezone.api.domain.dto.PostalCodePrefixDto;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.hbc.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.hbc.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
import com.hbc.postal.code.timezone.domain.entity.PostalCodeTimezoneEntity;
import com.hbc.postal.code.timezone.domain.mapper.PostalCodeTimezoneMapper;
import java.util.List;
import org.mapstruct.factory.Mappers;

public class TestUtil {

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
