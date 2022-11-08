package com.nextuple.jobs.dashboard.common;

import com.nextuple.jobs.dashboard.domain.entity.FileMetaDataEntity;
import com.nextuple.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.nextuple.jobs.framework.common.inbound.FileMetaDataCreationRequest;
import com.nextuple.jobs.framework.common.inbound.FileMetaDataUpdationRequest;

public class FileMetadataTestUtil {
  public static final String NAME = "TestMetaDataFile";
  public static final String PATH = "http://path";
  public static final String SIZE = "1024";
  public static final String TYPE = "METADATA";
  public static final String STORAGE = "MEMORY";
  public static final String DESC = "File meta data description";
  public static final String EXTERNAL_REFERENCE = "1";
  public static final Long PARENT_FILE_ID = 2L;
  public final Long Id = 1L;

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
        .parentFileId(PARENT_FILE_ID)
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
        .parentFileId(PARENT_FILE_ID)
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
        .parentFileId(PARENT_FILE_ID)
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
        .parentFileId(PARENT_FILE_ID)
        .build();
  }
}
