package com.hbc.jobs.dashboard.domain.mapper;

import com.hbc.jobs.dashboard.domain.entity.FileMetaDataEntity;
import com.hbc.jobs.dashboard.domain.inbound.FileMetaDataCreationRequest;
import com.hbc.jobs.dashboard.domain.inbound.FileMetaDataUpdationRequest;
import com.hbc.jobs.dashboard.domain.outbound.FileMetaDataResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FileMetaDataMapper {

  FileMetaDataResponse toFileMetadataResponse(FileMetaDataEntity fileMetadataEntity);

  FileMetaDataEntity fileMetadataRequestToEntity(
      FileMetaDataCreationRequest fileMetadataCreationRequest);

  FileMetaDataEntity fileMetadataRequestToEntity(
      Long id, FileMetaDataUpdationRequest fileMetadataCreationRequest);
}
