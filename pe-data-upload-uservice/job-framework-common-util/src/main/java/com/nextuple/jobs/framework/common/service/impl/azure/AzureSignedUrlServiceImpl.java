/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.framework.common.service.impl.azure;

import static com.nextuple.jobs.framework.common.utils.JobsFrameworkUtil.validateFileName;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.UserDelegationKey;
import com.azure.storage.blob.sas.BlobContainerSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.PreSignedUrlResponse;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.jobs.framework.common.clients.FileMetaDataClient;
import com.nextuple.jobs.framework.common.config.StorageConfigProperties;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.jobs.framework.common.service.PreSignedUrlInterface;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "dataupload.type", havingValue = "BLOB")
public class AzureSignedUrlServiceImpl implements PreSignedUrlInterface {

  @Autowired private StorageConfigProperties storageConfigProperties;

  @Autowired private BlobServiceClient blobServiceClient;

  @Value("${dataupload.base-path}")
  private String endpoint;

  @Autowired private FileMetaDataClient fileMetaDataClient;

  private static final Logger logger = LoggerFactory.getLogger(AzureSignedUrlServiceImpl.class);

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
            storageConfigProperties.getBucketName(),
            ModuleEnum.UI.getModuleValue(),
            moduleName,
            DateTime.now().toString("yyyy-MM-dd"));
    var file = "%s-%s".formatted(new Date().getTime(), fileName);
    var signedUrl = generatePreSignedUrl(bucketPath, file);
    return PreSignedUrlResponse.builder()
        .signedURL(signedUrl)
        .filePath(String.format("%s/%s", bucketPath, file))
        .storageType(storageConfigProperties.getType())
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

    var signedUrl = generatePreSignedUrl(filePathArray[0], filePathArray[1]);
    var url = signedUrl != null ? signedUrl : "";
    return PreSignedUrlResponse.builder()
        .signedURL(url)
        .filePath(fileMetaDataResponse.getPath())
        .storageType(storageConfigProperties.getType())
        .build();
  }

  public String generatePreSignedUrl(String bucketPath, String fileName) {
    BlobContainerClient blobContainerClient =
        blobServiceClient.getBlobContainerClient(storageConfigProperties.getBucketName());
    OffsetDateTime offsetDateTime =
        OffsetDateTime.now().plusMinutes(storageConfigProperties.getSignedUrlExpiryMinutes());
    UserDelegationKey userDelegationKey =
        blobServiceClient.getUserDelegationKey(OffsetDateTime.now(), offsetDateTime);
    BlobContainerSasPermission blobContainerSasPermission =
        new BlobContainerSasPermission()
            .setReadPermission(true)
            .setListPermission(true)
            .setWritePermission(true)
            .setCreatePermission(true)
            .setAddPermission(true);
    BlobServiceSasSignatureValues serviceSasSignatureValues =
        new BlobServiceSasSignatureValues(offsetDateTime, blobContainerSasPermission);
    String sasRequestParams =
        blobContainerClient.generateUserDelegationSas(serviceSasSignatureValues, userDelegationKey);
    return "%s/%s/%s?%s".formatted(endpoint, bucketPath, fileName, sasRequestParams);
  }

  public boolean validateModuleName(String moduleName) {
    return Arrays.stream(ModuleEnum.values()).anyMatch(t -> t.getModuleValue().equals(moduleName));
  }
}
