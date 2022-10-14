package com.hbc.jobs.dashboard.service;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.jobs.framework.common.domain.outbound.PreSignedUrlResponse;

public interface PreSignedUrlInterface {
  PreSignedUrlResponse getPreSignedURL(String fileName, String moduleName)
      throws CommonServiceException;
}
