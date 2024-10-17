/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.framework.common.service.impl.aws;

import static com.nextuple.jobs.framework.common.utils.JobsFrameworkUtil.validateFileName;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.PreSignedUrlResponse;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.jobs.framework.common.clients.FileMetaDataClient;
import com.nextuple.jobs.framework.common.domain.pojo.StorageConfigProperties;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.jobs.framework.common.service.PreSignedUrlInterface;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "dataupload.type", havingValue = "S3")
public class S3SignedUrlServiceImpl implements PreSignedUrlInterface {

  @Autowired private StorageConfigProperties storageConfigProperties;

  @Autowired private AmazonS3 amazonS3;

  @Autowired private FileMetaDataClient fileMetaDataClient;

  private static final Logger logger = LoggerFactory.getLogger(S3SignedUrlServiceImpl.class);

  @Override
  public PreSignedUrlResponse getPreSignedURL(String fileName, String moduleName)
      throws CommonServiceException {
    validateFileName(fileName);
    if (!validateModuleName(moduleName)) {
      throw new CommonServiceException(
          "module name is not valid",
          HttpStatus.BAD_REQUEST,
          0x1778,
          Map.of("moduleName", FieldError.builder().rejectedValue(moduleName).build()));
    }
    var bucketPath =
        String.format(
            "%s/%s/%s/%s",
            storageConfigProperties.getContainerName(),
            ModuleEnum.UI.getModuleValue(),
            moduleName,
            DateTime.now().toString("yyyy-MM-dd"));
    var file = "%s-%s".formatted(new Date().getTime(), fileName);
    return PreSignedUrlResponse.builder()
        .signedURL(generatePreSignedUrl(bucketPath, file))
        .filePath(String.format("%s/%s", bucketPath, file))
        .storageType(storageConfigProperties.getStorageType())
        .build();
  }

  @Override
  public PreSignedUrlResponse downloadFileURLById(long fileMetadataId)
      throws CommonServiceException {
    var fileMetaDataResponse = fileMetaDataClient.findFileMetadataById(fileMetadataId).getPayload();

    if (fileMetaDataResponse == null) {
      logger.error("File meta data not found for fileMetadataId : {}", fileMetadataId);
      throw new CommonServiceException(
          "File meta data not found.", HttpStatus.BAD_REQUEST, 0x1771, null);
    }

    String[] filePathArray = fileMetaDataResponse.getPath().split("/", 2);

    var expiration =
        DateTime.now().plusMinutes(storageConfigProperties.getSignedUrlExpiryMinutes()).toDate();
    var signedUrl = amazonS3.generatePresignedUrl(filePathArray[0], filePathArray[1], expiration);
    var url = signedUrl != null ? signedUrl.toString() : "";
    return PreSignedUrlResponse.builder()
        .signedURL(url)
        .filePath(fileMetaDataResponse.getPath())
        .storageType(storageConfigProperties.getStorageType())
        .build();
  }

  /*
   * This method is used to generate pre signed url for particular object in bucket
   */
  public String generatePreSignedUrl(String bucketName, String key) {
    var generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, key);
    generatePresignedUrlRequest.setMethod(HttpMethod.PUT);
    generatePresignedUrlRequest.setExpiration(
        DateTime.now().plusMinutes(storageConfigProperties.getSignedUrlExpiryMinutes()).toDate());
    generatePresignedUrlRequest.setContentType("text/csv");
    var signedUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
    return signedUrl.toString();
  }

  public boolean validateModuleName(String moduleName) {
    return Arrays.stream(ModuleEnum.values()).anyMatch(t -> t.getModuleValue().equals(moduleName));
  }
}
