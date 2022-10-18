package com.hbc.jobs.dashboard.service;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.jobs.dashboard.domain.outbound.FileResponse;
import java.io.File;

public interface FileService {
  void uploadFile(String bucketName, String filePath, File file);

  FileResponse getFile(String bucketName, String filePath) throws CommonServiceException;
}
