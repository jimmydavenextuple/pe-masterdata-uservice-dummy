/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.framework.common.service.impl.azure;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.domain.pojo.StorageConfigProperties;
import com.nextuple.jobs.framework.common.service.FileService;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@ConditionalOnProperty(value = "dataupload.type", havingValue = "BLOB")
public class AzureFileServiceImpl implements FileService {

  @Autowired private StorageConfigProperties storageConfigProperties;

  @Autowired private BlobServiceClient blobServiceClient;
  private static final Logger logger = LoggerFactory.getLogger(AzureFileServiceImpl.class);

  @Override
  public void uploadFile(String bucketName, String filePath, File file) throws IOException {
    try (InputStream targetStream = FileUtils.openInputStream(file)) {
      BlobContainerClient blobContainerClient =
          blobServiceClient.getBlobContainerClient(bucketName);
      BlockBlobClient blockBlobClient =
          blobContainerClient.getBlobClient(filePath).getBlockBlobClient();
      blockBlobClient.upload(new BufferedInputStream(targetStream), file.length());
    } catch (Exception e) {
      logger.error("Error in uploading file ");
      throw e;
    }
  }

  @Override
  public FileResponse getFile(String bucketName, String filePath) throws CommonServiceException {
    try {
      BlobContainerClient blobContainerClient =
          blobServiceClient.getBlobContainerClient(storageConfigProperties.getContainerName());
      BlobClient blobClient = blobContainerClient.getBlobClient(filePath);
      if (ObjectUtils.isEmpty(blobClient)) {
        logger.error(
            "File not found in Azure for bucketName = {} and filePath = {}", bucketName, filePath);
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

      return getFileResponse(bucketName, filePath, blobClient);
    } catch (Exception e) {
      logger.error("Error in downloading file ");
      throw e;
    }
  }

  private static FileResponse getFileResponse(
      String bucketName, String filePath, BlobClient blobClient) {
    var lastModifiedDate =
        new Date(blobClient.getProperties().getLastModified().toInstant().toEpochMilli());
    return FileResponse.builder()
        .bucketName(bucketName)
        .fileName(blobClient.getBlobName())
        .contentLength(blobClient.getProperties().getBlobSize())
        .contentType(blobClient.getProperties().getContentType())
        .lastModifiedDate(lastModifiedDate)
        .filePath(filePath)
        .inputStream(blobClient.openInputStream())
        .build();
  }
}
