package com.hbc.jobs.framework.common.service;

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
import com.hbc.common.exception.CommonServiceException;
import com.hbc.jobs.framework.common.domain.outbound.FileResponse;
import com.hbc.jobs.framework.common.service.impl.aws.S3FileServiceImpl;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
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
    Assert.assertThrows(
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
    Assert.assertThrows(
        Exception.class, () -> s3FileServiceImpl.getFile("bucket4", "date/xyz4.csv"));

    verify(amazonS3, times(1)).getObject(anyString(), anyString());
  }
}
