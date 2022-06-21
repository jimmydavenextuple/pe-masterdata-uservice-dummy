package com.nextuple.postal.code.timezone.domain.mapper;

import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.domain.entity.PostalCodeTimezoneEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PostalCodeTimezoneMapper {
  PostalCodeTimezoneEntity convertToPostalCodeTimezoneEntity(
      PostalCodeTimezoneDto postalCodeTimezoneDto);

  PostalCodeTimezoneDto convertToPostalCodeTimezoneDto(
      PostalCodeTimezoneEntity postalCodeTimezoneEntity);

  PostalCodeTimezoneEntity convertFromCreatePostalCodeTimezoneRequestToEntity(
      CreatePostalCodeTimezoneRequest createPostalCodeTimezoneRequest);

  void insertValuesFromUpdatePostalCodeTimezoneRequestToEntity(
      UpdatePostalCodeTimezoneRequest updatePromiseSourcingRuleRequest,
      @MappingTarget PostalCodeTimezoneEntity postalCodeTimezoneEntity);
}
