package com.nextuple.jobs.framework.common.service;

import com.nextuple.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.nextuple.jobs.framework.common.inbound.FileMetaDataCreationRequest;

public interface CommonFileMetaService {
  FileMetaDataResponse create(FileMetaDataCreationRequest fileMetadataRequest);
}
