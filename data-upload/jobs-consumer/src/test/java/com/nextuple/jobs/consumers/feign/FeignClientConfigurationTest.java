package com.nextuple.jobs.consumers.feign;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.Encoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class FeignClientConfigurationTest {

  @InjectMocks AuthTokenApiFeignClientConfiguration authTokenApiFeignClientConfiguration;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(authTokenApiFeignClientConfiguration, "username", "user");
    ReflectionTestUtils.setField(authTokenApiFeignClientConfiguration, "password", "password");
  }

  @Test
  void basicAuthRequestInterceptorTest() {
    BasicAuthRequestInterceptor basicAuthRequestInterceptor =
        authTokenApiFeignClientConfiguration.basicAuthRequestInterceptor();

    assertNotNull(basicAuthRequestInterceptor);
  }

  @Test
  void encoder() {
    Encoder encoder = authTokenApiFeignClientConfiguration.encoder();
    Assertions.assertNotNull(encoder);
  }
}
