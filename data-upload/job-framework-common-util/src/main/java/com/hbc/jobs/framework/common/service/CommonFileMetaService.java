package com.hbc.jobs.framework.common.service;

import com.hbc.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.hbc.jobs.framework.common.inbound.FileMetaDataCreationRequest;

public interface CommonFileMetaService {
  FileMetaDataResponse create(FileMetaDataCreationRequest fileMetadataRequest);
}
