package com.nextuple.pe.userexit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.userexit.domain.dto.UserExitConfigDataDto;
import com.nextuple.common.userexit.domain.dto.UserExitMetaDataDto;
import com.nextuple.common.userexit.domain.entity.UserExitConfigData;
import com.nextuple.common.userexit.domain.entity.UserExitMetaData;
import com.nextuple.common.userexit.domain.enums.ExecutionFailureEnum;
import com.nextuple.common.userexit.domain.enums.UEImplTypeEnum;
import com.nextuple.common.userexit.domain.enums.UserExitTypeEnum;
import com.nextuple.pe.userexit.domain.UserExitDomain;
import com.nextuple.pe.userexit.domain.inbound.CreateConfigDataRequest;
import com.nextuple.pe.userexit.domain.inbound.CreateMetaDataRequest;
import com.nextuple.pe.userexit.domain.inbound.UpdateConfigDataRequest;
import com.nextuple.pe.userexit.domain.inbound.UpdateMetaDataRequest;
import com.nextuple.pe.userexit.domain.outbound.ConfigDataResponse;
import com.nextuple.pe.userexit.domain.outbound.MetaDataResponse;
import com.nextuple.pe.userexit.util.TestUtil;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

class UserExitDataServiceTest {
  @InjectMocks UserExitDataService userExitDataService;
  @InjectMocks TestUtil testUtil;
  @Mock UserExitDomain userExitDomain;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Fetch meta data - service test - happy path")
  void fetchMetaDataTest() throws CommonServiceException {
    when(userExitDomain.findUEMetaDataByNameAppNameAndServiceName(any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getUserExitMetaData()));
    UserExitMetaDataDto response =
        userExitDataService.fetchMetaData("getSourcingRules", "PE", "pe-sourcing");
    Assertions.assertNotNull(response);
    Assertions.assertEquals("PE", response.getAppName());
    Assertions.assertEquals("GetSourcing", response.getName());
    verify(userExitDomain, times(1)).findUEMetaDataByNameAppNameAndServiceName(any(), any(), any());
  }

  @Test
  @DisplayName("Fetch meta data - not found")
  void fetchMetaDataNotFoundTest() throws CommonServiceException {
    when(userExitDomain.findUEMetaDataByNameAppNameAndServiceName(any(), any(), any()))
        .thenReturn(Optional.empty());
    CommonServiceException e =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              userExitDataService.fetchMetaData("getSourcingRules", "PE", "pe-sourcing");
            });
    Assertions.assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    verify(userExitDomain, times(1)).findUEMetaDataByNameAppNameAndServiceName(any(), any(), any());
  }

  @Test
  @DisplayName("Fetch config data - service test - happy path")
  void fetchConfigDataTest() throws CommonServiceException {
    when(userExitDomain.findUEConfigDataByNameAppNameOrgIdAndServiceName(
            any(), any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getUserExitConfigData()));
    UserExitConfigDataDto response =
        userExitDataService.fetchConfigData("getSourcingRules", "PE", "NEXTUPLE", "pe-sourcing");
    Assertions.assertNotNull(response);
    Assertions.assertEquals("PE", response.getAppName());
    Assertions.assertEquals("GetSourcing", response.getUserExitName());
    verify(userExitDomain, times(1))
        .findUEConfigDataByNameAppNameOrgIdAndServiceName(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Fetch config data - not found")
  void fetchConfigDataNotFoundTest() throws CommonServiceException {
    when(userExitDomain.findUEConfigDataByNameAppNameOrgIdAndServiceName(
            any(), any(), any(), any()))
        .thenReturn(Optional.empty());
    CommonServiceException e =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              userExitDataService.fetchConfigData(
                  "getSourcingRules", "PE", "NEXTUPLE", "pe-sourcing");
            });
    Assertions.assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    verify(userExitDomain, times(1))
        .findUEConfigDataByNameAppNameOrgIdAndServiceName(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Add meta data -service test")
  void addMetaDataTest() throws CommonServiceException {
    CreateMetaDataRequest createMetaDataRequest = testUtil.createMetaDataRequest();
    when(userExitDomain.addUEMetaData(any(UserExitMetaData.class)))
        .thenReturn(testUtil.getUserExitMetaData());

    MetaDataResponse metaDataResponse = userExitDataService.addMetaData(createMetaDataRequest);

    assertEquals(testUtil.getUserExitMetaData().getId(), metaDataResponse.getId());
    verify(userExitDomain, times(1)).addUEMetaData(any(UserExitMetaData.class));
  }

  @Test
  @DisplayName("Add config data -service test")
  void addConfigDataTest() throws CommonServiceException {
    CreateConfigDataRequest createConfigDataRequest = testUtil.createConfigDataRequest();
    when(userExitDomain.addUEConfigData(any(UserExitConfigData.class)))
        .thenReturn(testUtil.getUserExitConfigData());

    ConfigDataResponse configDataResponse =
        userExitDataService.addConfigData(createConfigDataRequest);
    assertEquals(testUtil.getUserExitConfigData().getId(), configDataResponse.getId());

    verify(userExitDomain, times(1)).addUEConfigData(any(UserExitConfigData.class));
  }

  @Test
  @DisplayName("Update meta data -service test")
  void updateMetaDataTest() throws CommonServiceException {
    String description = TestUtil.DESCRIPTION;
    ExecutionFailureEnum executionFailureType = ExecutionFailureEnum.SOFT;
    UserExitTypeEnum type = UserExitTypeEnum.REGULAR;
    String preUEName = TestUtil.PRE_UE_NAME;
    String postUEName = TestUtil.POST_UE_NAME;
    MetaDataResponse metaDataResponse = testUtil.getUpdatedMetaDataResponse();
    UserExitMetaData updatedUserExitMetaData = testUtil.getUserExitMetaData();
    when(userExitDomain.findUEMetaDataByNameAppNameAndServiceName(
            anyString(), anyString(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getUserExitMetaData()));

    when(userExitDomain.addUEMetaData(any(UserExitMetaData.class)))
        .thenReturn(updatedUserExitMetaData);
    MetaDataResponse response =
        userExitDataService.updateMetaData(
            "GetSourcing",
            "PE",
            "Sourcing",
            testUtil.getUpdateMetaDataRequest(
                description, executionFailureType, type, preUEName, postUEName));

    assertEquals(metaDataResponse.getName(), response.getName());
    assertEquals(metaDataResponse.getAppName(), response.getAppName());
    assertEquals(metaDataResponse.getServiceName(), response.getServiceName());

    verify(userExitDomain, times(1))
        .findUEMetaDataByNameAppNameAndServiceName(anyString(), anyString(), anyString());
    verify(userExitDomain, times(1)).addUEMetaData(any(UserExitMetaData.class));
  }

  @Test
  @DisplayName("Add config data -service test")
  void updateConfigDataTest() throws CommonServiceException {
    String url = TestUtil.URL;
    UEImplTypeEnum ueImplType = TestUtil.UE_IMPL_TYPE;
    String attributeJsonPath = TestUtil.ATTRIBUTE_JSON_PATH;
    ConfigDataResponse configDataResponse = testUtil.getUpdatedConfigDataResponse();
    UserExitConfigData updatedUserExitConfigData = testUtil.getUserExitConfigData();

    when(userExitDomain.findUEConfigDataByNameAppNameOrgIdAndServiceName(
            anyString(), anyString(), anyString(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getUserExitConfigData()));
    when(userExitDomain.addUEConfigData(any(UserExitConfigData.class)))
        .thenReturn(updatedUserExitConfigData);

    ConfigDataResponse response =
        userExitDataService.updateConfigData(
            "GetSourcing",
            "PE",
            "SIGNET",
            "Sourcing",
            testUtil.getUpdateConfigDataRequest(url, ueImplType, attributeJsonPath));

    assertEquals(configDataResponse.getUserExitName(), response.getUserExitName());
    assertEquals(configDataResponse.getAppName(), response.getAppName());
    assertEquals(configDataResponse.getServiceName(), response.getServiceName());

    verify(userExitDomain, times(1))
        .findUEConfigDataByNameAppNameOrgIdAndServiceName(
            anyString(), anyString(), anyString(), anyString());
    verify(userExitDomain, times(1)).addUEConfigData(any(UserExitConfigData.class));
  }

  @Test
  @DisplayName("Update config data -Exception scenario")
  void updateConfigDataExceptionTest() throws CommonServiceException {
    String userExitName = TestUtil.USER_EXIT_NAME;
    String appName = TestUtil.APP_NAME;
    String orgId = TestUtil.ORG_ID;
    String serviceName = TestUtil.SERVICE_NAME;
    UpdateConfigDataRequest updateConfigDataRequest = new UpdateConfigDataRequest();

    when(userExitDomain.findUEConfigDataByNameAppNameOrgIdAndServiceName(
            userExitName, appName, orgId, serviceName))
        .thenThrow(
            new CommonServiceException("Mocked exception", HttpStatus.BAD_REQUEST, 0x1234, null));
    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                userExitDataService.updateConfigData(
                    userExitName, appName, orgId, serviceName, updateConfigDataRequest));

    assertEquals("Mocked exception", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    assertEquals(0x1234, exception.getErrorCode());
  }

  @Test
  @DisplayName("Update meta data - Exception scenario")
  void updateMetaDataExceptionTest() throws CommonServiceException {
    String name = TestUtil.NAME;
    String appName = TestUtil.APP_NAME;
    String serviceName = TestUtil.SERVICE_NAME;
    UpdateMetaDataRequest updateMetaDataRequest = new UpdateMetaDataRequest();

    when(userExitDomain.findUEMetaDataByNameAppNameAndServiceName(name, appName, serviceName))
        .thenThrow(
            new CommonServiceException("Mocked exception", HttpStatus.BAD_REQUEST, 0x1234, null));
    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                userExitDataService.updateMetaData(
                    name, appName, serviceName, updateMetaDataRequest));

    assertEquals("Mocked exception", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    assertEquals(0x1234, exception.getErrorCode());
  }

  @Test
  @DisplayName("Update meta data -not found")
  void updateMetaDataNotFoundTest() throws CommonServiceException {
    String name = TestUtil.NAME;
    String appName = TestUtil.APP_NAME;
    String serviceName = TestUtil.SERVICE_NAME;
    UpdateMetaDataRequest updateMetaDataRequest = new UpdateMetaDataRequest();

    Mockito.when(
            userExitDomain.findUEMetaDataByNameAppNameAndServiceName(name, appName, serviceName))
        .thenReturn(Optional.empty());

    Assertions.assertThrows(
        CommonServiceException.class,
        () -> {
          userExitDataService.updateMetaData(name, appName, serviceName, updateMetaDataRequest);
        });
  }

  @Test
  @DisplayName("Update config data -not found")
  void updateConfigDataNotFoundTest() throws CommonServiceException {
    String userExitName = TestUtil.USER_EXIT_NAME;
    String appName = TestUtil.APP_NAME;
    String orgId = TestUtil.ORG_ID;
    String serviceName = TestUtil.SERVICE_NAME;
    UpdateConfigDataRequest updateConfigDataRequest = new UpdateConfigDataRequest();

    Mockito.when(
            userExitDomain.findUEConfigDataByNameAppNameOrgIdAndServiceName(
                userExitName, appName, orgId, serviceName))
        .thenReturn(Optional.empty());

    Assertions.assertThrows(
        CommonServiceException.class,
        () -> {
          userExitDataService.updateConfigData(
              userExitName, appName, orgId, serviceName, updateConfigDataRequest);
        });
  }
}
