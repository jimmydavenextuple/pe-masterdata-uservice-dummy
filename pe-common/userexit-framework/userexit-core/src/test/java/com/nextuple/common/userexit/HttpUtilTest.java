package com.nextuple.common.userexit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.userexit.domain.UserExitData;
import com.nextuple.common.util.TestUtil;
import io.micrometer.core.instrument.MockClock;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class HttpUtilTest {
  @InjectMocks HttpUtil<String, String> httpUtil;
  @InjectMocks TestUtil testUtil;
  @Mock UserExitUtil userExitUtil;
  @Mock ObjectMapper objectMapper;
  private static final MockClock clock = new MockClock();
  private static final SimpleMeterRegistry meterRegistry =
      new SimpleMeterRegistry(SimpleConfig.DEFAULT, clock);

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(httpUtil, "readTimeout", 10);
    ReflectionTestUtils.setField(httpUtil, "meterRegistry", meterRegistry);
  }

  @Test
  @DisplayName("Make Post call - Happy Path")
  void makePostCallTest() throws IOException, InterruptedException, URISyntaxException {
    UserExitData userExitData = testUtil.getUserExitData();
    HttpClient httpClientMock = mock(HttpClient.class);
    HttpResponse<Object> httpResponse = mock(HttpResponse.class);
    Map<String, String> exampleMap = Map.of("output", "Modified String");
    BaseResponse<Object> baseResponse =
        BaseResponse.builder()
            .success(true)
            .message("Success Response")
            .payload(exampleMap.toString())
            .build();
    Gson gson = new Gson();
    when(httpResponse.body()).thenReturn(gson.toJson(baseResponse));
    when(userExitUtil.getHttpClient()).thenReturn(httpClientMock);
    when(httpClientMock.send(any(), any())).thenReturn(httpResponse);
    TypeReference inputTypeReference = new TypeReference<String>() {};

    when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(baseResponse);
    String response = httpUtil.makePOSTCall(userExitData, "Input String", null, inputTypeReference);
    Assertions.assertNotNull(response);
    verify(userExitUtil, times(1)).getHttpClient();
  }
}
