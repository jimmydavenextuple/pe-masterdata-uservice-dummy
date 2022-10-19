package com.hbc.jobs.dashboard.common;

import com.hbc.jobs.dashboard.domain.entity.FileMetaDataEntity;
import com.hbc.jobs.dashboard.domain.inbound.FileMetaDataCreationRequest;
import com.hbc.jobs.dashboard.domain.inbound.FileMetaDataUpdationRequest;
import com.hbc.jobs.dashboard.domain.outbound.FileMetaDataResponse;

public class FileMetadataTestUtil {
  public static final String NAME = "TestMetaDataFile";
  public static final String PATH = "http://path";
  public static final String SIZE = "1024";
  public static final String TYPE = "METADATA";
  public static final String STORAGE = "MEMORY";
  public static final String DESC = "File meta data description";
  public static final String EXTERNAL_REFERENCE = "1";
  public static final String PARENT_FIELD = "ParentField";
  public final Long Id = Long.valueOf(1);

  public FileMetaDataEntity getFileMetadata() {
    return FileMetaDataEntity.builder()
        .id(Id)
        .type(TYPE)
        .name(NAME)
        .storageType(STORAGE)
        .size(SIZE)
        .description(DESC)
        .extReferenceId(EXTERNAL_REFERENCE)
        .path(PATH)
        .parentFileId(PARENT_FIELD)
        .build();
  }

  public FileMetaDataCreationRequest getFileMetadataRequest() {
    return FileMetaDataCreationRequest.builder()
        .type(TYPE)
        .storageType(STORAGE)
        .size(SIZE)
        .name(NAME)
        .description(DESC)
        .extReferenceId(EXTERNAL_REFERENCE)
        .path(PATH)
        .parentFileId(PARENT_FIELD)
        .build();
  }

  public FileMetaDataUpdationRequest getFileMetadataUpdationRequest() {
    return FileMetaDataUpdationRequest.builder()
        .type(TYPE)
        .storageType(STORAGE)
        .size(SIZE)
        .name(NAME)
        .description(DESC)
        .extReferenceId(EXTERNAL_REFERENCE)
        .parentFileId(PARENT_FIELD)
        .path(PATH)
        .build();
  }

  public FileMetaDataResponse getFileMetadataResponse() {
    return FileMetaDataResponse.builder()
        .id(Id)
        .type(TYPE)
        .storageType(STORAGE)
        .size(SIZE)
        .name(NAME)
        .description(DESC)
        .extReferenceId(EXTERNAL_REFERENCE)
        .path(PATH)
        .parentFileId(PARENT_FIELD)
        .build();
  }
}
