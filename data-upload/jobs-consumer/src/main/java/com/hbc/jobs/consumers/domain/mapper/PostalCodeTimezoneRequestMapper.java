package com.hbc.jobs.consumers.domain.mapper;

import com.hbc.jobs.framework.common.domain.pojo.PostalCodeTimezoneUpload;
import com.hbc.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.hbc.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PostalCodeTimezoneRequestMapper {

  CreatePostalCodeTimezoneRequest convertToCreatePostalCodeTimezoneRequest(
      PostalCodeTimezoneUpload postalCodeTimezoneUpload);

  UpdatePostalCodeTimezoneRequest convertToUpdatePostalCodeTimezoneRequest(
      PostalCodeTimezoneUpload postalCodeTimezoneUpload);
}
