package com.nextuple.postal.code.timezone.domain.mapper;

import com.nextuple.postal.code.timezone.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.domain.entity.PostalCodeTimezoneEntity;
import com.nextuple.postal.code.timezone.domain.inbound.CreatePostalCodeTimezoneRequest;
import org.mapstruct.Mapper;
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
}
