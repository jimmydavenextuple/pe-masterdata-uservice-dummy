package com.nextuple.common.userexit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.userexit.domain.UserExitData;
import com.nextuple.common.userexit.domain.dto.UserExitConfigDataDto;
import com.nextuple.common.userexit.domain.enums.UEImplTypeEnum;
import com.nextuple.common.userexit.domain.enums.UserExitTypeEnum;
import com.nextuple.common.userexit.domain.feign.UEDataFeign;
import com.nextuple.common.util.TestUtil;
import com.nextuple.pe.userexit.cache.service.UEConfigDataNearCacheService;
import io.micrometer.core.instrument.MockClock;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

class ApiUEImplTest {
  @InjectMocks ApiUEImpl<String, Integer> apiUE;
  @InjectMocks TestUtil testUtil;
  @Mock UEDataFeign ueDataFeign;
  @Mock HttpUtil<String, String> httpUtilForPreUE;
  @Mock HttpUtil<Integer, Integer> httpUtilForPostUE;
  @Mock HttpUtil<String, Integer> httpUtil;
  @Mock UEConfigDataNearCacheService ueConfigDataNearCacheService;
  @Mock ApplicationContext applicationContext;
  @Mock ExampleClassBasedExit clasBasedExit;
  private static final MockClock clock = new MockClock();
  private static final SimpleMeterRegistry meterRegistry =
      new SimpleMeterRegistry(SimpleConfig.DEFAULT, clock);

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(apiUE, "httpUtilForPreUE", httpUtilForPreUE);
    ReflectionTestUtils.setField(apiUE, "httpUtilForPostUE", httpUtilForPostUE);
    ReflectionTestUtils.setField(apiUE, "httpUtil", httpUtil);
    ReflectionTestUtils.setField(apiUE, "meterRegistry", meterRegistry);
  }

  @Test
  @DisplayName("Invoke API UE - Mock")
  void invokeMockTest()
      throws URISyntaxException, IOException, InterruptedException, CommonServiceException {
    UserExitData userExitData = testUtil.getUserExitData();
    userExitData.getUserExitMetaData().setType(UserExitTypeEnum.API);
    userExitData.getUserExitConfigData().setUeImplType(UEImplTypeEnum.CLASS_BASED);
    userExitData.getUserExitConfigData().setUrl("com.nextuple.common.userexit.ExampleMock");
    String beanName = "exampleMock";
    when(applicationContext.getBean(beanName, IClassBasedExit.class)).thenReturn(clasBasedExit);
    when(clasBasedExit.fetchResponse(any(), any())).thenReturn(1);
    Integer response = apiUE.invoke("Input", null, userExitData, null, null);
    Assertions.assertNotNull(response);
    Assertions.assertEquals(1, response);
    verify(ueDataFeign, times(0)).fetchConfigData(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Invoke API UE with pre and post")
  void invokePreAndPostTest()
      throws URISyntaxException, IOException, InterruptedException, CommonServiceException {
    UserExitData userExitData = testUtil.getUserExitData();
    userExitData.getUserExitMetaData().setType(UserExitTypeEnum.API);
    userExitData.getUserExitMetaData().setPreUEName("preUE");
    userExitData.getUserExitMetaData().setPostUEName("postUE");

    BaseResponse<UserExitConfigDataDto> userExitConfigData = testUtil.getUEConfigDataBaseResponse();
    when(httpUtilForPreUE.makePOSTCall(any(), any(), any(), any())).thenReturn("InputModified");
    when(httpUtilForPostUE.makePOSTCall(any(), any(), any(), any())).thenReturn(2);
    when(httpUtil.makePOSTCall(any(), any(), any(), any())).thenReturn(1);
    when(ueConfigDataNearCacheService.get(any())).thenReturn(testUtil.getUEConfigDataCacheValue());
    Integer response = apiUE.invoke("Input", null, userExitData, null, null);
    Assertions.assertNotNull(response);
    Assertions.assertEquals(2, response);
    verify(httpUtilForPreUE, times(1)).makePOSTCall(any(), any(), any(), any());
    verify(httpUtilForPostUE, times(1)).makePOSTCall(any(), any(), any(), any());
    verify(httpUtil, times(1)).makePOSTCall(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Invoke API UE with post UE only")
  void invokePostUETest()
      throws URISyntaxException, IOException, InterruptedException, CommonServiceException {
    UserExitData userExitData = testUtil.getUserExitData();
    userExitData.getUserExitMetaData().setType(UserExitTypeEnum.API);
    userExitData.getUserExitMetaData().setPostUEName("postUE");

    BaseResponse<UserExitConfigDataDto> userExitConfigData = testUtil.getUEConfigDataBaseResponse();
    when(httpUtilForPostUE.makePOSTCall(any(), any(), any(), any())).thenReturn(2);
    when(httpUtil.makePOSTCall(any(), any(), any(), any())).thenReturn(1);
    when(ueConfigDataNearCacheService.get(any())).thenReturn(testUtil.getUEConfigDataCacheValue());
    Integer response = apiUE.invoke("Input", null, userExitData, null, null);
    Assertions.assertNotNull(response);
    Assertions.assertEquals(2, response);
    verify(httpUtilForPreUE, times(0)).makePOSTCall(any(), any(), any(), any());
    verify(httpUtilForPostUE, times(1)).makePOSTCall(any(), any(), any(), any());
    verify(httpUtil, times(1)).makePOSTCall(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Invoke API UE without pre and post")
  void invokeWithoutPreAndPostTest()
      throws URISyntaxException, IOException, InterruptedException, CommonServiceException {
    UserExitData userExitData = testUtil.getUserExitData();
    userExitData.getUserExitMetaData().setType(UserExitTypeEnum.API);
    userExitData.getUserExitMetaData().setPreUEName(null);
    userExitData.getUserExitMetaData().setPostUEName(null);

    BaseResponse<UserExitConfigDataDto> userExitConfigData = testUtil.getUEConfigDataBaseResponse();
    when(httpUtil.makePOSTCall(any(), any(), any(), any())).thenReturn(1);
    when(ueConfigDataNearCacheService.get(any())).thenReturn(testUtil.getUEConfigDataCacheValue());

    Integer response = apiUE.invoke("Input", null, userExitData, null, null);
    Assertions.assertNotNull(response);
    Assertions.assertEquals(1, response);
    verify(httpUtilForPreUE, times(0)).makePOSTCall(any(), any(), any(), any());
    verify(httpUtilForPostUE, times(0)).makePOSTCall(any(), any(), any(), any());
    verify(httpUtil, times(1)).makePOSTCall(any(), any(), any(), any());
  }
}
