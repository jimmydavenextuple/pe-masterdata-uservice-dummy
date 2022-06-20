package com.nextuple.postal.code.timezone.utils;

import static com.nextuple.postal.code.timezone.utils.PostalCodeTimezoneConstants.CITY;
import static com.nextuple.postal.code.timezone.utils.PostalCodeTimezoneConstants.COUNTRY;
import static com.nextuple.postal.code.timezone.utils.PostalCodeTimezoneConstants.LATITUDE;
import static com.nextuple.postal.code.timezone.utils.PostalCodeTimezoneConstants.LONGITUDE;
import static com.nextuple.postal.code.timezone.utils.PostalCodeTimezoneConstants.ORG_ID;
import static com.nextuple.postal.code.timezone.utils.PostalCodeTimezoneConstants.POSTAL_CODE_PREFIX;
import static com.nextuple.postal.code.timezone.utils.PostalCodeTimezoneConstants.STATE;
import static com.nextuple.postal.code.timezone.utils.PostalCodeTimezoneConstants.TIME_ZONE;

import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.domain.entity.PostalCodeTimezoneEntity;
import com.nextuple.postal.code.timezone.domain.mapper.PostalCodeTimezoneMapper;
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
}
