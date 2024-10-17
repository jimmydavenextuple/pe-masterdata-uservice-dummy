/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.framework.common.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.PreSignedUrlResponse;
import com.nextuple.jobs.framework.common.clients.FileMetaDataClient;
import com.nextuple.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.nextuple.jobs.framework.common.domain.pojo.StorageConfigProperties;
import com.nextuple.jobs.framework.common.service.impl.aws.S3SignedUrlServiceImpl;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class S3SignedUrlServiceImplTest {

  @InjectMocks private S3SignedUrlServiceImpl s3SignedUrlServiceImpl;

  @Mock private AmazonS3 amazonS3;

  @Mock private FileMetaDataClient fileMetaDataClient;

  @Mock StorageConfigProperties storageConfig;

  @BeforeEach
  public void init() {
    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(storageConfig, "signedUrlExpiryMinutes", 30);
    ReflectionTestUtils.setField(storageConfig, "containerName", "dataupload");
    ReflectionTestUtils.setField(storageConfig, "storageType", "S3");
  }

  @Test
  void getPreSignedUrlTest() throws MalformedURLException, CommonServiceException {
    when(amazonS3.generatePresignedUrl(any()))
        .thenReturn(new URL("https", "s3.amazonaws.com", 8080, "/test.csv"));
    PreSignedUrlResponse response = s3SignedUrlServiceImpl.getPreSignedURL("test.csv", "transit");
    Assertions.assertEquals("https://s3.amazonaws.com:8080/test.csv", response.getSignedURL());
    verify(amazonS3, times(1)).generatePresignedUrl(any());
  }

  @Test
  void getPreSignedUrlModuleNameInvalidTest() throws MalformedURLException, CommonServiceException {
    when(amazonS3.generatePresignedUrl(any()))
        .thenReturn(new URL("https", "s3.amazonaws.com", 8080, "/test.csv"));
    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> s3SignedUrlServiceImpl.getPreSignedURL("test.csv", "unknown"));
    Assertions.assertEquals("module name is not valid", ex.getMessage());
    verify(amazonS3, times(0)).generatePresignedUrl(any());
  }

  @DisplayName("Exception scenario: Invalid File Upload")
  @Test
  void getPreSignedUrlInvalidFileNameExceptionTest() {
    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> s3SignedUrlServiceImpl.getPreSignedURL("test.txt", "unknown"));
    Assertions.assertEquals("Invalid file uploaded, upload a csv file", ex.getMessage());
    verify(amazonS3, times(0)).generatePresignedUrl(any());
  }

  @Test
  void downloadFileUrlTest() throws MalformedURLException, CommonServiceException {
    when(fileMetaDataClient.findFileMetadataById(anyLong())).thenReturn(getFileMetaDataResponse());
    when(amazonS3.generatePresignedUrl(any(), any(), any()))
        .thenReturn(new URL("https", "s3.amazonaws.com", 8080, "/test.csv"));
    PreSignedUrlResponse response = s3SignedUrlServiceImpl.downloadFileURLById(123L);
    Assertions.assertEquals("https://s3.amazonaws.com:8080/test.csv", response.getSignedURL());
  }

  @Test
  void downloadFileUrlNullSignedUrlTest() throws CommonServiceException {
    when(fileMetaDataClient.findFileMetadataById(anyLong())).thenReturn(getFileMetaDataResponse());
    when(amazonS3.generatePresignedUrl(any(), any(), any())).thenReturn(null);
    PreSignedUrlResponse response = s3SignedUrlServiceImpl.downloadFileURLById(1L);
    Assertions.assertEquals("", response.getSignedURL());
  }

  @Test
  void downloadFileUrlInvalidIdTest() throws MalformedURLException, CommonServiceException {
    Date expiration = DateTime.now().plusMinutes(10).toDate();
    when(amazonS3.generatePresignedUrl("any", "any", expiration))
        .thenReturn(new URL("https", "s3.amazonaws.com", 8080, "/test.csv"));

    when(fileMetaDataClient.findFileMetadataById(anyLong())).thenReturn(nullFileResponse());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class, () -> s3SignedUrlServiceImpl.downloadFileURLById(123L));
    Assertions.assertEquals("File meta data not found.", ex.getMessage());
  }

  public BaseResponse<FileMetaDataResponse> getFileMetaDataResponse() {
    FileMetaDataResponse fileMetaDataResponse =
        FileMetaDataResponse.builder()
            .id(1L)
            .storageType("S3")
            .path("bucket/path")
            .name("ANY")
            .size("56")
            .build();
    BaseResponse<FileMetaDataResponse> fileMetaDataResponseBaseResponse = new BaseResponse<>();
    fileMetaDataResponseBaseResponse.setPayload(fileMetaDataResponse);
    fileMetaDataResponseBaseResponse.setSuccess(Boolean.TRUE);
    return fileMetaDataResponseBaseResponse;
  }

  public BaseResponse<FileMetaDataResponse> nullFileResponse() {
    BaseResponse<FileMetaDataResponse> fileMetaDataResponseBaseResponse = new BaseResponse<>();
    fileMetaDataResponseBaseResponse.setPayload(null);
    fileMetaDataResponseBaseResponse.setSuccess(Boolean.TRUE);
    return fileMetaDataResponseBaseResponse;
  }
}
