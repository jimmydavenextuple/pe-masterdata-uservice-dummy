package com.nextuple.jobs.framework.common.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.jobs.framework.common.domain.outbound.PreSignedUrlResponse;

public interface PreSignedUrlInterface {
  PreSignedUrlResponse getPreSignedURL(String fileName, String moduleName)
      throws CommonServiceException;

  PreSignedUrlResponse downloadFileURLById(long fileMetaId) throws CommonServiceException;
}
