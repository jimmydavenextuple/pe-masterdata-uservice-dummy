package com.nextuple.jobs.consumers.domain.mapper;

import com.nextuple.jobs.framework.common.domain.pojo.PostalCodeTimezoneUpload;
import com.nextuple.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
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
