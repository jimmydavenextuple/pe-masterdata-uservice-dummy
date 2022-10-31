package com.hbc.jobs.framework.common.service;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.jobs.framework.common.domain.outbound.PreSignedUrlResponse;

public interface PreSignedUrlInterface {
  PreSignedUrlResponse getPreSignedURL(String fileName, String moduleName)
      throws CommonServiceException;

  PreSignedUrlResponse downloadFileURLById(long fileMetaId) throws CommonServiceException;
}
