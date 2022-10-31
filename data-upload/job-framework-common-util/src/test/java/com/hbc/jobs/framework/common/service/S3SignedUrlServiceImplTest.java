package com.hbc.jobs.framework.common.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.jobs.framework.common.clients.FileMetaDataClient;
import com.hbc.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.hbc.jobs.framework.common.domain.outbound.PreSignedUrlResponse;
import com.hbc.jobs.framework.common.service.impl.aws.S3SignedUrlServiceImpl;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class S3SignedUrlServiceImplTest {

  @InjectMocks private S3SignedUrlServiceImpl s3SignedUrlServiceImpl;

  @Mock private AmazonS3 amazonS3;

  @Mock private FileMetaDataClient fileMetaDataClient;

  @BeforeEach
  public void init() {
    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(s3SignedUrlServiceImpl, "signedUrlExpiryMinutes", 30);
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
