package com.nextuple.jobs.framework.common.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import java.io.File;

public interface FileService {
  void uploadFile(String bucketName, String filePath, File file);

  FileResponse getFile(String bucketName, String filePath) throws CommonServiceException;
}
