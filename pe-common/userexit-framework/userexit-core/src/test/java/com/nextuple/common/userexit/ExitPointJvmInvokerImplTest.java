package com.nextuple.common.userexit;

import static com.nextuple.common.constants.CommonConstants.SERVER_UNAVAILABLE_ERROR_MESSAGE;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jayway.jsonpath.DocumentContext;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.HardExecutionFailureException;
import com.nextuple.common.exception.ServiceUnavailableException;
import com.nextuple.common.userexit.domain.UserExitData;
import com.nextuple.common.userexit.domain.dto.UserExitConfigDataDto;
import com.nextuple.common.userexit.domain.dto.UserExitMetaDataDto;
import com.nextuple.common.userexit.domain.enums.ExecutionFailureEnum;
import com.nextuple.common.userexit.domain.enums.UEImplTypeEnum;
import com.nextuple.common.util.TestUtil;
import com.nextuple.pe.userexit.cache.service.UEConfigDataNearCacheService;
import com.nextuple.pe.userexit.cache.service.UEMetaDataNearCacheService;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

class ExitPointJvmInvokerImplTest {
  @InjectMocks ExitPointJvmInvokerImpl<String, String> exitPointJvmInvoker;
  @InjectMocks TestUtil testUtil;
  @Mock UserExitFactory<String, String> userExitFactory;
  @Mock RegularUEImpl<String, String> regularUE;
  @Mock ApiUEImpl<String, String> apiUE;
  @Mock UEMetaDataNearCacheService ueMetaDataNearCacheService;
  @Mock UEConfigDataNearCacheService ueConfigDataNearCacheService;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Fetch UE Data")
  void fetchUEData() throws CommonServiceException {
    when(ueMetaDataNearCacheService.get(any())).thenReturn(testUtil.getUEMetaDataCacheValue());
    when(ueConfigDataNearCacheService.get(any())).thenReturn(testUtil.getUEConfigDataCacheValue());

    UserExitData userExitData =
        exitPointJvmInvoker.fetchUEData("NEXTUPLE", "ExampleExit", "PE", "SourcingService");
    Assertions.assertNotNull(userExitData);
    Assertions.assertNotNull(userExitData.getUserExitMetaData());
    Assertions.assertNotNull(userExitData.getUserExitConfigData());
    Assertions.assertEquals("NEXTUPLE", userExitData.getUserExitConfigData().getOrgId());
    Assertions.assertEquals("PE", userExitData.getUserExitMetaData().getAppName());
  }

  @Test
  @DisplayName("Fetch UE Data - Meta Data throws exception")
  void fetchUEDataMetaDataNotFound() throws CommonServiceException {
    when(ueMetaDataNearCacheService.get(any())).thenReturn(null);
    when(ueConfigDataNearCacheService.get(any())).thenReturn(testUtil.getUEConfigDataCacheValue());

    CommonServiceException commonServiceException =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              exitPointJvmInvoker.fetchUEData("NEXTUPLE", "ExampleExit", "PE", "SourcingService");
            });
    Assertions.assertNotNull(commonServiceException.getFieldInfo());
    Assertions.assertEquals(
        "NEXTUPLE", commonServiceException.getFieldInfo().get("orgId").getRejectedValue());
  }

  @Test
  @DisplayName("Invoke Regular UE - Happy path")
  void invokeRegularTest()
      throws URISyntaxException,
          IOException,
          InterruptedException,
          ClassNotFoundException,
          InvocationTargetException,
          NoSuchMethodException,
          InstantiationException,
          IllegalAccessException,
          CommonServiceException {
    DocumentContext documentContext = mock(DocumentContext.class);
    UserExitData userExitData = testUtil.getUserExitData();
    UserExitConfigDataDto userExitConfigData = userExitData.getUserExitConfigData();
    userExitConfigData.setAttributeJsonPath("itemId:$.orderLine.itemId");
    when(documentContext.read("$.orderLine.itemId")).thenReturn("Item1");
    when(userExitFactory.getUE(any())).thenReturn(regularUE);
    when(regularUE.invoke(any(), any(), any(), any(), any())).thenReturn("UserExitResult");
    String result =
        exitPointJvmInvoker.invoke(userExitData, "nextuple", documentContext, null, null);
    Assertions.assertNotNull(result);
    Assertions.assertEquals("UserExitResult", result);
    verify(userExitFactory, times(1)).getUE(any());
    verify(regularUE, times(1)).invoke(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Invoke API UE - Happy path")
  void invokeAPITest()
      throws URISyntaxException,
          IOException,
          InterruptedException,
          ClassNotFoundException,
          InvocationTargetException,
          NoSuchMethodException,
          InstantiationException,
          IllegalAccessException,
          CommonServiceException {
    DocumentContext documentContext = mock(DocumentContext.class);
    UserExitData userExitData = testUtil.getUserExitData();
    UserExitConfigDataDto userExitConfigData = userExitData.getUserExitConfigData();
    userExitConfigData.setAttributeJsonPath("itemId:$.orderLine.itemId");
    when(documentContext.read("$.orderLine.itemId")).thenReturn("Item1");
    when(userExitFactory.getUE(any())).thenReturn(apiUE);
    when(apiUE.invoke(any(), any(), any(), any(), any())).thenReturn("UserExitResult");
    String result =
        exitPointJvmInvoker.invoke(userExitData, "nextuple", documentContext, null, null);
    Assertions.assertNotNull(result);
    Assertions.assertEquals("UserExitResult", result);
    verify(userExitFactory, times(1)).getUE(any());
    verify(apiUE, times(1)).invoke(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Invoke Regular UE - Exception scenario")
  void invokeExceptionTest()
      throws URISyntaxException, IOException, InterruptedException, CommonServiceException {
    DocumentContext documentContext = mock(DocumentContext.class);
    UserExitData userExitData = testUtil.getUserExitData();
    UserExitConfigDataDto userExitConfigData = userExitData.getUserExitConfigData();
    userExitConfigData.setAttributeJsonPath("itemId:$.orderLine.itemId");
    when(documentContext.read("$.orderLine.itemId")).thenReturn("Item1");
    when(userExitFactory.getUE(any())).thenThrow(new RuntimeException("Error occurred"));
    RuntimeException runtimeException =
        Assertions.assertThrows(
            RuntimeException.class,
            () -> {
              exitPointJvmInvoker.invoke(userExitData, "nextuple", documentContext, null, null);
            });
    Assertions.assertEquals("Error occurred", runtimeException.getMessage());
    verify(userExitFactory, times(1)).getUE(any());
    verify(regularUE, times(0)).invoke(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Invoke Regular UE - IOException scenario with soft execution failure type")
  void invokeIOExceptionSoftFailureTest()
      throws URISyntaxException, IOException, InterruptedException, CommonServiceException {
    DocumentContext documentContext = mock(DocumentContext.class);
    UserExitData userExitData = testUtil.getUserExitData();
    UserExitConfigDataDto userExitConfigData = userExitData.getUserExitConfigData();
    userExitConfigData.setAttributeJsonPath("itemId:$.orderLine.itemId");
    when(documentContext.read("$.orderLine.itemId")).thenReturn("Item1");
    when(userExitFactory.getUE(any())).thenReturn(regularUE);
    when(regularUE.invoke(any(), any(), any(), any(), any()))
        .thenThrow(new SocketException("Error occurred"));
    SocketException exception =
        Assertions.assertThrows(
            SocketException.class,
            () -> {
              exitPointJvmInvoker.invoke(userExitData, "nextuple", documentContext, null, null);
            });
    Assertions.assertEquals("Error occurred", exception.getMessage());
    verify(userExitFactory, times(1)).getUE(any());
    verify(regularUE, times(1)).invoke(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Invoke Regular UE - IOException scenario with hard execution failure type")
  void invokeIOExceptionHardFailureTest()
      throws URISyntaxException, IOException, InterruptedException, CommonServiceException {
    DocumentContext documentContext = mock(DocumentContext.class);
    UserExitData userExitData = testUtil.getUserExitData();
    UserExitMetaDataDto userExitMetaData = userExitData.getUserExitMetaData();
    userExitMetaData.setExecutionFailureType(ExecutionFailureEnum.HARD);
    userExitData.setUserExitMetaData(userExitMetaData);
    UserExitConfigDataDto userExitConfigData = userExitData.getUserExitConfigData();
    userExitConfigData.setAttributeJsonPath("itemId:$.orderLine.itemId");
    userExitConfigData.setUeImplType(UEImplTypeEnum.REST);
    when(documentContext.read("$.orderLine.itemId")).thenReturn("Item1");
    when(userExitFactory.getUE(any())).thenReturn(regularUE);
    when(regularUE.invoke(any(), any(), any(), any(), any()))
        .thenThrow(new SocketException("Error occurred"));
    ServiceUnavailableException exception =
        Assertions.assertThrows(
            ServiceUnavailableException.class,
            () -> {
              exitPointJvmInvoker.invoke(userExitData, "nextuple", documentContext, null, null);
            });
    Assertions.assertEquals(SERVER_UNAVAILABLE_ERROR_MESSAGE, exception.getMessage());
    verify(userExitFactory, times(1)).getUE(any());
    verify(regularUE, times(1)).invoke(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName(
      "Invoke Regular UE - CommonServiceException scenario with hard execution failure type with REST")
  void invokeCommonServiceExceptionHardFailureTest()
      throws URISyntaxException, IOException, InterruptedException, CommonServiceException {
    DocumentContext documentContext = mock(DocumentContext.class);
    UserExitData userExitData = testUtil.getUserExitData();
    UserExitMetaDataDto userExitMetaData = userExitData.getUserExitMetaData();
    userExitMetaData.setExecutionFailureType(ExecutionFailureEnum.HARD);
    userExitData.setUserExitMetaData(userExitMetaData);
    UserExitConfigDataDto userExitConfigData = userExitData.getUserExitConfigData();
    userExitConfigData.setAttributeJsonPath("itemId:$.orderLine.itemId");
    userExitConfigData.setUeImplType(UEImplTypeEnum.REST);
    when(documentContext.read("$.orderLine.itemId")).thenReturn("Item1");
    when(userExitFactory.getUE(any())).thenReturn(regularUE);
    when(regularUE.invoke(any(), any(), any(), any(), any()))
        .thenThrow(
            new CommonServiceException("Error occurred", HttpStatus.resolve(404), 1, Map.of()));
    HardExecutionFailureException exception =
        Assertions.assertThrows(
            HardExecutionFailureException.class,
            () -> {
              exitPointJvmInvoker.invoke(userExitData, "nextuple", documentContext, null, null);
            });
    Assertions.assertEquals("Error occurred", exception.getMessage());
    verify(userExitFactory, times(1)).getUE(any());
    verify(regularUE, times(1)).invoke(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName(
      "Invoke Regular UE - CommonServiceException scenario with hard execution failure type with Class based")
  void invokeCommonServiceExceptionHardFailureClassTest()
      throws URISyntaxException, IOException, InterruptedException, CommonServiceException {
    DocumentContext documentContext = mock(DocumentContext.class);
    UserExitData userExitData = testUtil.getUserExitData();
    UserExitMetaDataDto userExitMetaData = userExitData.getUserExitMetaData();
    userExitMetaData.setExecutionFailureType(ExecutionFailureEnum.HARD);
    userExitData.setUserExitMetaData(userExitMetaData);
    UserExitConfigDataDto userExitConfigData = userExitData.getUserExitConfigData();
    userExitConfigData.setAttributeJsonPath("itemId:$.orderLine.itemId");
    userExitConfigData.setUeImplType(UEImplTypeEnum.CLASS_BASED);
    when(documentContext.read("$.orderLine.itemId")).thenReturn("Item1");
    when(userExitFactory.getUE(any())).thenReturn(apiUE);
    when(apiUE.invoke(any(), any(), any(), any(), any()))
        .thenThrow(
            new CommonServiceException("Error occurred", HttpStatus.resolve(404), 1, Map.of()));
    Assertions.assertThrows(
        IOException.class,
        () -> {
          exitPointJvmInvoker.invoke(userExitData, "nextuple", documentContext, null, null);
        });
    verify(userExitFactory, times(1)).getUE(any());
    verify(apiUE, times(1)).invoke(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName(
      "Invoke Regular UE - CommonServiceException scenario with soft execution failure type")
  void invokeCommonServiceExceptionSoftFailureTest()
      throws URISyntaxException, IOException, InterruptedException, CommonServiceException {
    DocumentContext documentContext = mock(DocumentContext.class);
    UserExitData userExitData = testUtil.getUserExitData();
    UserExitConfigDataDto userExitConfigData = userExitData.getUserExitConfigData();
    userExitConfigData.setAttributeJsonPath("itemId:$.orderLine.itemId");
    when(documentContext.read("$.orderLine.itemId")).thenReturn("Item1");
    when(userExitFactory.getUE(any())).thenReturn(regularUE);
    when(regularUE.invoke(any(), any(), any(), any(), any()))
        .thenThrow(
            new CommonServiceException("Error occurred", HttpStatus.resolve(404), 1, Map.of()));
    Assertions.assertThrows(
        IOException.class,
        () -> {
          exitPointJvmInvoker.invoke(userExitData, "nextuple", documentContext, null, null);
        });
    verify(userExitFactory, times(1)).getUE(any());
    verify(regularUE, times(1)).invoke(any(), any(), any(), any(), any());
  }
}
