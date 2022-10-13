package com.hbc.jobs.dashboard.service;

import com.hbc.common.exception.CommonServiceException;

public interface PreSignedUrlInterface {
  String getPreSignedURL(String fileName, String moduleName) throws CommonServiceException;
}
