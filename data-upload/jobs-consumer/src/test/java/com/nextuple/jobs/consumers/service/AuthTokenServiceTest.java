package com.nextuple.jobs.consumers.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nextuple.jobs.consumers.common.TestUtil;
import com.nextuple.jobs.consumers.feign.AuthTokenAPI;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthTokenServiceTest {

  @InjectMocks AuthTokenService authTokenService;

  @InjectMocks TestUtil testUtil;

  @Mock AuthTokenAPI authTokenAPI;

  @BeforeEach
  public void init() {
    ReflectionTestUtils.setField(authTokenService, "grantType", "client_credentials");
    ReflectionTestUtils.setField(authTokenService, "scope", "sfcc-resources/edd");
  }

  @Test
  void getAuthTokenTest() {
    when(authTokenAPI.getAuthToken(any())).thenReturn(testUtil.getAuthTokenResponse());
    String response = authTokenService.getAuthToken("dummy", "1996-09-05T00:00:00");
    Assertions.assertNotNull(response);
  }

  @Test
  void getAuthTokenTest2() {
    when(authTokenAPI.getAuthToken(any())).thenReturn(testUtil.getAuthTokenResponse());
    String response = authTokenService.getAuthToken("dummy", "");
    Assertions.assertNotNull(response);
  }

  @Test
  void getAuthTokenTest3() {
    var dateTime = LocalDateTime.now();
    dateTime = dateTime.plusDays(1);
    String response = authTokenService.getAuthToken("dummy", dateTime.toString());
    Assertions.assertNotNull(response);
  }
}
