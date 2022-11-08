package com.nextuple.jobs.dashboard.domain.mapper;

import com.nextuple.jobs.dashboard.domain.entity.FileMetaDataEntity;
import com.nextuple.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.nextuple.jobs.framework.common.inbound.FileMetaDataCreationRequest;
import com.nextuple.jobs.framework.common.inbound.FileMetaDataUpdationRequest;
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
