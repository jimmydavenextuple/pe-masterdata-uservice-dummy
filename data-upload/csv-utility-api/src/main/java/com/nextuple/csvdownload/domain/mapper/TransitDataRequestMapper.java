package com.nextuple.csvdownload.domain.mapper;

import com.nextuple.csvdownload.domain.pojo.DownloadErrorTransitData;
import com.nextuple.csvdownload.domain.pojo.TransitDataErrorLogsPojo;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TransitDataRequestMapper {

  TransitDataErrorLogsPojo convertToTransitDataErrorLogsPojo(
      DownloadErrorTransitData downloadErrorTransitData);
}
