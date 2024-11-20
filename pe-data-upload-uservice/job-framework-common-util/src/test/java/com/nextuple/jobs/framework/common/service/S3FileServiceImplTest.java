/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.framework.common.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.service.impl.aws.S3FileServiceImpl;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class S3FileServiceImplTest {

  @InjectMocks private S3FileServiceImpl s3FileServiceImpl;

  @Mock private AmazonS3 amazonS3;

  @BeforeEach
  public void init() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void uploadFileTest() {
    when(amazonS3.putObject(any())).thenReturn(new PutObjectResult());
    s3FileServiceImpl.uploadFile("bucket1", "date/xyz1.csv", mock(File.class));

    verify(amazonS3, times(1)).putObject(any());
  }

  @Test
  void uploadFileExceptionTest() {
    when(amazonS3.putObject(any())).thenThrow(new RuntimeException("error"));
    Assertions.assertThrows(
        Exception.class,
        () -> s3FileServiceImpl.uploadFile("bucket2", "date/xyz2.csv", mock(File.class)));

    verify(amazonS3, times(1)).putObject(any());
  }

  @Test
  void downloadFileTest() throws CommonServiceException, IOException {
    S3Object s3Object = new S3Object();
    s3Object.setObjectContent(mock(S3ObjectInputStream.class));
    s3Object.setBucketName("bucket3");
    s3Object.setKey("date/xyz3.csv");
    when(amazonS3.getObject(anyString(), anyString())).thenReturn(s3Object);
    FileResponse response = s3FileServiceImpl.getFile("bucket3", "date/xyz3.csv");

    Assertions.assertEquals("xyz3.csv", response.getFileName());
    verify(amazonS3, times(1)).getObject(anyString(), anyString());
  }

  @Test
  void downloadFileExceptionTest() {
    when(amazonS3.getObject(anyString(), anyString())).thenReturn(null);
    Assertions.assertThrows(
        Exception.class, () -> s3FileServiceImpl.getFile("bucket4", "date/xyz4.csv"));

    verify(amazonS3, times(1)).getObject(anyString(), anyString());
  }
}
