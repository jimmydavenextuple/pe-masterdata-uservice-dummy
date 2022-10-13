package com.hbc.jobs.dashboard.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.jobs.dashboard.service.impl.aws.S3SignedUrlServiceImpl;
import java.net.MalformedURLException;
import java.net.URL;
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

  @BeforeEach
  public void init() {
    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(s3SignedUrlServiceImpl, "signedUrlExpiryMinutes", 30);
  }

  @Test
  void getPreSignedUrlTest() throws MalformedURLException, CommonServiceException {
    when(amazonS3.generatePresignedUrl(any()))
        .thenReturn(new URL("https", "s3.amazonaws.com", 8080, "/test.csv"));
    String response = s3SignedUrlServiceImpl.getPreSignedURL("test.csv", "transit");
    Assertions.assertEquals("https://s3.amazonaws.com:8080/test.csv", response);
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
}
