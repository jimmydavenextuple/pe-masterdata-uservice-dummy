package com.hbc.jobs.dashboard.domain.mapper;

import com.amazonaws.services.s3.model.S3Object;
import com.hbc.jobs.dashboard.domain.outbound.FileResponse;
import com.hbc.jobs.dashboard.domain.outbound.FileResponse.FileResponseBuilder;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FileMapper {

  @Mapping(target = "filePath", source = "key")
  @Mapping(target = "contentType", source = "objectMetadata.contentType")
  @Mapping(target = "contentLength", source = "objectMetadata.contentLength")
  @Mapping(target = "lastModifiedDate", source = "objectMetadata.lastModified")
  @Mapping(target = "inputStream", ignore = true)
  FileResponse mapToFileDownloadResponse(S3Object s3Object);

  @AfterMapping
  default void afterMappingToFileDownloadResponse(
      @MappingTarget FileResponseBuilder fileDownloadResponse, S3Object s3Object) {
    fileDownloadResponse.inputStream(s3Object.getObjectContent());
    String[] fileArray = s3Object.getKey().split("/", 0);
    fileDownloadResponse.fileName(fileArray[fileArray.length - 1]);
  }
}
