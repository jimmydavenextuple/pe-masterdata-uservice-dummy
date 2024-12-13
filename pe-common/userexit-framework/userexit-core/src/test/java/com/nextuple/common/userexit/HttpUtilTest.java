package com.nextuple.common.userexit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gson.Gson;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.userexit.domain.UserExitData;
import com.nextuple.common.userexit.domain.dto.ErrorDetails;
import com.nextuple.common.userexit.domain.dto.ErrorWrapper;
import com.nextuple.common.util.TestUtil;
import io.micrometer.core.instrument.MockClock;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
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
  @InjectMocks HttpUtil<String, List<String>> httpUtilStringList;
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
  void makePostCallTest()
      throws IOException, InterruptedException, URISyntaxException, CommonServiceException {
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
    ErrorWrapper<String> response =
        httpUtil.makePOSTCall(userExitData, "Input String", null, inputTypeReference);
    Assertions.assertNotNull(response);
    verify(userExitUtil, times(1)).getHttpClient();
  }

  @Test
  @DisplayName("Make Post call - Happy Path with error propagation true")
  void makePostCallErrorPropagationTest()
      throws IOException, InterruptedException, URISyntaxException, CommonServiceException {
    UserExitData userExitData = testUtil.getUserExitData();
    userExitData.getUserExitConfigData().setPropagateError(true);
    HttpClient httpClientMock = mock(HttpClient.class);
    HttpResponse<Object> httpResponse = mock(HttpResponse.class);
    TypeFactory typeFactory = mock(TypeFactory.class);
    JavaType javaTypeMock = mock(JavaType.class);
    List<ErrorDetails> errorDetailsList = new ArrayList<>();
    ErrorDetails errorDetails =
        ErrorDetails.builder().errorCode(123).errorMessage("Error Occurred").build();
    errorDetailsList.add(errorDetails);
    ErrorWrapper<Object> errorWrapper =
        ErrorWrapper.builder().data("Modified String").errorDetailsList(errorDetailsList).build();
    BaseResponse<Object> baseResponse =
        BaseResponse.builder()
            .success(true)
            .message("Success Response")
            .payload(errorWrapper)
            .build();
    Gson gson = new Gson();
    when(httpResponse.body()).thenReturn(gson.toJson(baseResponse));
    when(userExitUtil.getHttpClient()).thenReturn(httpClientMock);
    when(httpClientMock.send(any(), any())).thenReturn(httpResponse);
    TypeReference inputTypeReference = new TypeReference<String>() {};

    when(objectMapper.getTypeFactory()).thenReturn(typeFactory);
    when(objectMapper.getTypeFactory().constructType(inputTypeReference.getType()))
        .thenReturn(javaTypeMock);
    when(objectMapper.getTypeFactory().constructParametricType(ErrorWrapper.class, javaTypeMock))
        .thenReturn(javaTypeMock);
    when(objectMapper.getTypeFactory().constructParametricType(BaseResponse.class, javaTypeMock))
        .thenReturn(javaTypeMock);
    when(objectMapper.readValue(anyString(), any(JavaType.class))).thenReturn(baseResponse);
    when(objectMapper.writeValueAsString(any()))
        .thenReturn(
            "BaseResponse(success=true, requestId=null, timestamp=1733831893602, message=null, payload={output=Modified String})");
    ErrorWrapper<String> response =
        httpUtil.makePOSTCall(userExitData, "Input String", null, inputTypeReference);
    Assertions.assertNotNull(response);
    Assertions.assertNotNull(response.getErrorDetailsList());
    Assertions.assertEquals(123, response.getErrorDetailsList().get(0).getErrorCode());
    verify(userExitUtil, times(1)).getHttpClient();
  }

  @Test
  @DisplayName("Make Post call - Happy Path with error propagation true and parameterized type")
  void makePostCallErrorPropagationWithParameterizedTypeTest()
      throws IOException, InterruptedException, URISyntaxException, CommonServiceException {
    ReflectionTestUtils.setField(httpUtilStringList, "readTimeout", 10);
    ReflectionTestUtils.setField(httpUtilStringList, "meterRegistry", meterRegistry);
    UserExitData userExitData = testUtil.getUserExitData();
    userExitData.getUserExitConfigData().setPropagateError(true);
    HttpClient httpClientMock = mock(HttpClient.class);
    HttpResponse<Object> httpResponse = mock(HttpResponse.class);
    TypeFactory typeFactory = mock(TypeFactory.class);
    JavaType javaTypeMock = mock(JavaType.class);
    List<ErrorDetails> errorDetailsList = new ArrayList<>();
    ErrorDetails errorDetails =
        ErrorDetails.builder().errorCode(123).errorMessage("Error Occurred").build();
    errorDetailsList.add(errorDetails);
    List<String> stringList = new ArrayList<>();
    stringList.add("Modified String1");
    stringList.add("Modified String2");
    ErrorWrapper<Object> errorWrapper =
        ErrorWrapper.builder().data(stringList).errorDetailsList(errorDetailsList).build();
    BaseResponse<Object> baseResponse =
        BaseResponse.builder()
            .success(true)
            .message("Success Response")
            .payload(errorWrapper)
            .build();
    Gson gson = new Gson();
    when(httpResponse.body()).thenReturn(gson.toJson(baseResponse));
    when(userExitUtil.getHttpClient()).thenReturn(httpClientMock);
    when(httpClientMock.send(any(), any())).thenReturn(httpResponse);
    TypeReference inputTypeReference = new TypeReference<List<String>>() {};
    when(objectMapper.getTypeFactory()).thenReturn(typeFactory);
    when(objectMapper
            .getTypeFactory()
            .constructType(
                ((ParameterizedType) inputTypeReference.getType()).getActualTypeArguments()[0]))
        .thenReturn(javaTypeMock);
    when(objectMapper.getTypeFactory().constructParametricType(ErrorWrapper.class, javaTypeMock))
        .thenReturn(javaTypeMock);
    when(objectMapper.getTypeFactory().constructParametricType(BaseResponse.class, javaTypeMock))
        .thenReturn(javaTypeMock);
    when(objectMapper.readValue(anyString(), any(JavaType.class))).thenReturn(baseResponse);
    when(objectMapper.writeValueAsString(any()))
        .thenReturn(
            "BaseResponse(success=true, requestId=null, timestamp=1733848785784, message=Success Response, payload=ErrorWrapper(data=[Modified String1, Modified String2], errorDetailsList=[ErrorDetails(errorMessage=Error Occurred, errorCode=123, refId=null)]))");
    ErrorWrapper<List<String>> response =
        httpUtilStringList.makePOSTCall(userExitData, "Input String", null, inputTypeReference);
    Assertions.assertNotNull(response);
    Assertions.assertNotNull(response.getErrorDetailsList());
    Assertions.assertEquals(123, response.getErrorDetailsList().get(0).getErrorCode());
    verify(userExitUtil, times(1)).getHttpClient();
  }

  @Test
  @DisplayName("Make Post call - failure due to 400 HTTP status code")
  void makePostCallFailureTest() throws IOException, InterruptedException {
    UserExitData userExitData = testUtil.getUserExitData();
    HttpClient httpClientMock = mock(HttpClient.class);
    HttpResponse<Object> httpResponse = mock(HttpResponse.class);
    Map<String, String> exampleMap = Map.of("output", "Modified String");
    BaseResponse<Object> baseResponse =
        BaseResponse.builder()
            .success(false)
            .message("Failure Response")
            .payload(exampleMap.toString())
            .build();
    Gson gson = new Gson();
    when(httpResponse.body()).thenReturn(gson.toJson(baseResponse));
    when(httpResponse.statusCode()).thenReturn(400);
    when(userExitUtil.getHttpClient()).thenReturn(httpClientMock);
    when(httpClientMock.send(any(), any())).thenReturn(httpResponse);
    TypeReference inputTypeReference = new TypeReference<String>() {};
    JsonNode jsonNode = mock(JsonNode.class);
    when(jsonNode.asText()).thenReturn("Failure Response");
    when(jsonNode.isNull()).thenReturn(false);
    when(jsonNode.get(anyString())).thenReturn(jsonNode);
    when(objectMapper.readTree(anyString())).thenReturn(jsonNode);
    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> httpUtil.makePOSTCall(userExitData, "Input String", null, inputTypeReference));
    Assertions.assertEquals(
        "Unable to process request as ExampleExit failed with error: Failure Response",
        exception.getMessage());
    verify(userExitUtil, times(1)).getHttpClient();
  }

  @Test
  @DisplayName("Make Post call - failure due to 400 HTTP status code with error parsing failure")
  void makePostCallFailureNoMessageTest() throws IOException, InterruptedException {
    UserExitData userExitData = testUtil.getUserExitData();
    HttpClient httpClientMock = mock(HttpClient.class);
    HttpResponse<Object> httpResponse = mock(HttpResponse.class);
    Map<String, String> exampleMap = Map.of("output", "Modified String");
    BaseResponse<Object> baseResponse =
        BaseResponse.builder().success(false).message("Failure Response").build();
    Gson gson = new Gson();
    when(httpResponse.body()).thenReturn(gson.toJson(baseResponse));
    when(httpResponse.statusCode()).thenReturn(400);
    when(userExitUtil.getHttpClient()).thenReturn(httpClientMock);
    when(httpClientMock.send(any(), any())).thenReturn(httpResponse);
    TypeReference inputTypeReference = new TypeReference<String>() {};
    JsonNode jsonNode = mock(JsonNode.class);
    when(jsonNode.asText()).thenThrow(new RuntimeException());
    when(jsonNode.isNull()).thenReturn(false);
    when(jsonNode.get(anyString())).thenReturn(jsonNode);
    when(objectMapper.readTree(anyString())).thenReturn(jsonNode);
    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> httpUtil.makePOSTCall(userExitData, "Input String", null, inputTypeReference));
    Assertions.assertEquals(
        "Unable to process request as ExampleExit failed", exception.getMessage());
    verify(userExitUtil, times(1)).getHttpClient();
  }

  @Test
  @DisplayName("Make Post call - failure due to 400 HTTP status code with no error message")
  void makePostCallFailureNoErrorMessageTest()
      throws IOException, InterruptedException, URISyntaxException, CommonServiceException {
    UserExitData userExitData = testUtil.getUserExitData();
    HttpClient httpClientMock = mock(HttpClient.class);
    HttpResponse<Object> httpResponse = mock(HttpResponse.class);
    Map<String, String> exampleMap = Map.of("output", "Modified String");
    BaseResponse<Object> baseResponse =
        BaseResponse.builder().success(false).message("Failure Response").build();
    Gson gson = new Gson();
    when(httpResponse.body()).thenReturn(gson.toJson(baseResponse));
    when(httpResponse.statusCode()).thenReturn(400);
    when(userExitUtil.getHttpClient()).thenReturn(httpClientMock);
    when(httpClientMock.send(any(), any())).thenReturn(httpResponse);
    TypeReference inputTypeReference = new TypeReference<String>() {};
    JsonNode jsonNode = mock(JsonNode.class);
    when(jsonNode.isNull()).thenReturn(true);
    when(jsonNode.get(anyString())).thenReturn(jsonNode);
    when(objectMapper.readTree(anyString())).thenReturn(jsonNode);
    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> httpUtil.makePOSTCall(userExitData, "Input String", null, inputTypeReference));
    Assertions.assertEquals(
        "Unable to process request as ExampleExit failed", exception.getMessage());
    verify(userExitUtil, times(1)).getHttpClient();
  }

  @Test
  @DisplayName("Make Post call - failure due to 400 HTTP status code with messageNode null")
  void makePostCallFailureMessageNodeNullTest()
      throws IOException, InterruptedException, URISyntaxException, CommonServiceException {
    UserExitData userExitData = testUtil.getUserExitData();
    HttpClient httpClientMock = mock(HttpClient.class);
    HttpResponse<Object> httpResponse = mock(HttpResponse.class);
    Map<String, String> exampleMap = Map.of("output", "Modified String");
    BaseResponse<Object> baseResponse =
        BaseResponse.builder().success(false).message("Failure Response").build();
    Gson gson = new Gson();
    when(httpResponse.body()).thenReturn(gson.toJson(baseResponse));
    when(httpResponse.statusCode()).thenReturn(400);
    when(userExitUtil.getHttpClient()).thenReturn(httpClientMock);
    when(httpClientMock.send(any(), any())).thenReturn(httpResponse);
    TypeReference inputTypeReference = new TypeReference<String>() {};
    JsonNode jsonNode = mock(JsonNode.class);
    when(jsonNode.isNull()).thenReturn(true);
    when(jsonNode.get(anyString())).thenReturn(null);
    when(objectMapper.readTree(anyString())).thenReturn(jsonNode);
    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> httpUtil.makePOSTCall(userExitData, "Input String", null, inputTypeReference));
    Assertions.assertEquals(
        "Unable to process request as ExampleExit failed", exception.getMessage());
    verify(userExitUtil, times(1)).getHttpClient();
  }
}
