package com.hbc.postal.code.timezone.domain.mapper;

import com.hbc.postal.code.timezone.api.domain.projection.MarketRegionProjection;
import com.hbc.postal.code.timezone.api.domain.dto.MarketRegionInfo;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.hbc.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.hbc.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
import com.hbc.postal.code.timezone.domain.entity.PostalCodeTimezoneEntity;
import java.util.List;
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

  List<MarketRegionInfo> convertToMarketRegionInfo(List<MarketRegionProjection> marketRegionProjectionList);
}
