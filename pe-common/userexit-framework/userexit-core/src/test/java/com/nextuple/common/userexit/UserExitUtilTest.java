package com.nextuple.common.userexit;

import java.net.http.HttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class UserExitUtilTest {
  @InjectMocks UserExitUtil userExitUtil;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(userExitUtil, "connectTimeout", 10);
  }

  @Test
  void getHttpClientTest() {
    HttpClient httpClient = userExitUtil.getHttpClient();
    Assertions.assertNotNull(httpClient);
  }
}
