/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.framework.common.service.impl.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.mapper.FileMapper;
import com.nextuple.jobs.framework.common.service.FileService;
import java.io.File;
import java.util.Map;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@ConditionalOnProperty(value = "dataupload.type", havingValue = "S3")
public class S3FileServiceImpl implements FileService {

  @Autowired private AmazonS3 amazonS3;

  private static final Logger logger = LoggerFactory.getLogger(S3FileServiceImpl.class);
  public static final FileMapper INSTANCE = Mappers.getMapper(FileMapper.class);

  @Override
  public void uploadFile(String bucketName, String filePath, File file) {
    try {
      amazonS3.putObject(new PutObjectRequest(bucketName, filePath, file));

    } catch (Exception e) {
      logger.error("Error in uploading file ");
      throw e;
    }
  }

  @Override
  public FileResponse getFile(String bucketName, String filePath) throws CommonServiceException {
    try {
      var s3object = amazonS3.getObject(bucketName, filePath);
      if (ObjectUtils.isEmpty(s3object)) {
        logger.error(
            "File not found in S3 for bucketName = {} and filePath = {}", bucketName, filePath);
        throw new CommonServiceException(
            "File not found !",
            HttpStatus.BAD_REQUEST,
            0x1779,
            Map.of(
                "bucketName",
                FieldError.builder().rejectedValue(bucketName).build(),
                "filePath",
                FieldError.builder().rejectedValue(filePath).build()));
      }
      return INSTANCE.mapToFileDownloadResponse(s3object);
    } catch (Exception e) {
      logger.error("Error in downloading file ");
      throw e;
    }
  }
}
