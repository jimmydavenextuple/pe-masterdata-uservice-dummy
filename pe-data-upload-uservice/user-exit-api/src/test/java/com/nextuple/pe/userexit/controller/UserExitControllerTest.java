package com.nextuple.pe.userexit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.userexit.domain.dto.UserExitConfigDataDto;
import com.nextuple.common.userexit.domain.dto.UserExitMetaDataDto;
import com.nextuple.pe.userexit.domain.inbound.CreateConfigDataRequest;
import com.nextuple.pe.userexit.domain.inbound.CreateMetaDataRequest;
import com.nextuple.pe.userexit.domain.inbound.UpdateConfigDataRequest;
import com.nextuple.pe.userexit.domain.inbound.UpdateMetaDataRequest;
import com.nextuple.pe.userexit.domain.outbound.ConfigDataResponse;
import com.nextuple.pe.userexit.domain.outbound.MetaDataResponse;
import com.nextuple.pe.userexit.service.UserExitDataService;
import com.nextuple.pe.userexit.util.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class UserExitControllerTest {
  @InjectMocks UserExitController controller;
  @InjectMocks TestUtil testUtil;
  @Mock UserExitDataService userExitDataService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Fetch meta data")
  void fetchMetaData() throws CommonServiceException {
    when(userExitDataService.fetchMetaData(any(), any(), any()))
        .thenReturn(testUtil.getUserExitMetaDataDto());

    ResponseEntity<BaseResponse<UserExitMetaDataDto>> response =
        controller.fetchMetaData("PE", "sourcing", "getSourcingRules");
    verify(userExitDataService, times(1)).fetchMetaData(any(), any(), any());
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertNotNull(response);
  }

  @Test
  @DisplayName("Fetch meta data - Exception case")
  void fetchMetaDataExceptionTest() throws CommonServiceException {
    when(userExitDataService.fetchMetaData(any(), any(), any()))
        .thenThrow(new RuntimeException("Error occurred"));

    Assertions.assertThrows(
        RuntimeException.class,
        () -> {
          controller.fetchMetaData("PE", "sourcing", "getSourcingRules");
        });
    verify(userExitDataService, times(1)).fetchMetaData(any(), any(), any());
  }

  @Test
  void fetchConfigData() throws CommonServiceException {
    when(userExitDataService.fetchConfigData(any(), any(), any(), any()))
        .thenReturn(testUtil.getUserExitConfigDataDto());

    ResponseEntity<BaseResponse<UserExitConfigDataDto>> response =
        controller.fetchConfigData("Nextuple", "PE", "sourcing", "getSourcingRules");
    Assertions.assertNotNull(response);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    verify(userExitDataService, times(1)).fetchConfigData(any(), any(), any(), any());
  }

  @Test
  void fetchConfigDataExceptionTest() throws CommonServiceException {
    when(userExitDataService.fetchConfigData(any(), any(), any(), any()))
        .thenThrow(new RuntimeException("Error occurred"));

    Assertions.assertThrows(
        RuntimeException.class,
        () -> {
          controller.fetchConfigData("Nextuple", "PE", "sourcing", "getSourcingRules");
        });
    verify(userExitDataService, times(1)).fetchConfigData(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Insert meta data")
  void insertMetaDataTest() throws CommonServiceException {
    CreateMetaDataRequest request = new CreateMetaDataRequest();
    MetaDataResponse userExitResponse = new MetaDataResponse();
    when(userExitDataService.addMetaData(request)).thenReturn(userExitResponse);
    ResponseEntity<BaseResponse<MetaDataResponse>> response = controller.insertMetaData(request);

    Assertions.assertEquals(
        "User Exit metadata inserted successfully", response.getBody().getMessage());
    Assertions.assertEquals(userExitResponse, response.getBody().getPayload());
    Assertions.assertEquals(200, response.getStatusCodeValue());
    verify(userExitDataService, times(1)).addMetaData(request);
  }

  @Test
  @DisplayName("Insert meta data - Exception scenario")
  void insertMetaDataExceptionTest() throws CommonServiceException {
    when(userExitDataService.addMetaData(any(CreateMetaDataRequest.class)))
        .thenThrow(new RuntimeException("Error in adding meta data"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () -> {
              controller.insertMetaData(testUtil.createMetaDataRequest());
            });
    Assertions.assertEquals("Error in adding meta data", ex.getMessage());
    verify(userExitDataService, times(1)).addMetaData(any(CreateMetaDataRequest.class));
  }

  @Test
  @DisplayName("Insert config data")
  void insertConfigDataTest() throws CommonServiceException {
    CreateConfigDataRequest request = new CreateConfigDataRequest();
    ConfigDataResponse userExitResponse = new ConfigDataResponse();
    when(userExitDataService.addConfigData(request)).thenReturn(userExitResponse);
    ResponseEntity<BaseResponse<ConfigDataResponse>> response =
        controller.insertConfigData(request);

    Assertions.assertEquals(
        "User Exit configdata inserted successfully", response.getBody().getMessage());
    Assertions.assertEquals(userExitResponse, response.getBody().getPayload());

    Assertions.assertEquals(200, response.getStatusCodeValue());
    verify(userExitDataService, times(1)).addConfigData(request);
  }

  @Test
  @DisplayName("Insert config data - Exception scenario")
  void insertConfigDataExceptionTest() throws CommonServiceException {
    when(userExitDataService.addConfigData(any(CreateConfigDataRequest.class)))
        .thenThrow(new RuntimeException("Error in adding config data"));

    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () -> {
              controller.insertConfigData(testUtil.createConfigDataRequest());
            });
    Assertions.assertEquals("Error in adding config data", ex.getMessage());
    verify(userExitDataService, times(1)).addConfigData(any(CreateConfigDataRequest.class));
  }

  @Test
  @DisplayName("Update config data")
  void updateConfigDataTest() throws CommonServiceException {
    String userExitName = TestUtil.USER_EXIT_NAME;
    String appName = TestUtil.APP_NAME;
    String orgId = TestUtil.ORG_ID;
    String serviceName = TestUtil.SERVICE_NAME;
    UpdateConfigDataRequest request = new UpdateConfigDataRequest();
    ConfigDataResponse configDataResponse = new ConfigDataResponse();
    when(userExitDataService.updateConfigData(userExitName, appName, orgId, serviceName, request))
        .thenReturn(configDataResponse);

    ResponseEntity<BaseResponse<ConfigDataResponse>> response =
        controller.updateConfigData(userExitName, appName, orgId, serviceName, request);

    Assertions.assertEquals(
        "User Exit config data updated successfully", response.getBody().getMessage());
    Assertions.assertEquals(configDataResponse, response.getBody().getPayload());
    Assertions.assertEquals(200, response.getStatusCodeValue());
    verify(userExitDataService, times(1))
        .updateConfigData(userExitName, appName, orgId, serviceName, request);
  }

  @Test
  @DisplayName("Update config data - Exception scenario")
  void updateConfigDataExceptionTest() throws CommonServiceException {
    String userExitName = "userExitName";
    String appName = "appName";
    String orgId = "orgId";
    String serviceName = "serviceName";
    UpdateConfigDataRequest request = new UpdateConfigDataRequest();
    when(userExitDataService.updateConfigData(userExitName, appName, orgId, serviceName, request))
        .thenThrow(new CommonServiceException(HttpStatus.INTERNAL_SERVER_ERROR, 0x1776, null));

    Assertions.assertThrows(
        CommonServiceException.class,
        () -> {
          controller.updateConfigData(userExitName, appName, orgId, serviceName, request);
        },
        "Test exception");
    verify(userExitDataService, times(1))
        .updateConfigData(userExitName, appName, orgId, serviceName, request);
  }

  @Test
  @DisplayName("Update meta data")
  void updateMetaDataTest() throws CommonServiceException {
    String name = "name";
    String appName = "appName";
    String serviceName = "serviceName";
    UpdateMetaDataRequest request = new UpdateMetaDataRequest();
    MetaDataResponse metaDataResponse = new MetaDataResponse();
    when(userExitDataService.updateMetaData(name, appName, serviceName, request))
        .thenReturn(metaDataResponse);
    ResponseEntity<BaseResponse<MetaDataResponse>> response =
        controller.updateConfigData(name, appName, serviceName, request);

    Assertions.assertEquals(
        "User Exit meta data updated successfully", response.getBody().getMessage());
    Assertions.assertEquals(metaDataResponse, response.getBody().getPayload());
    Assertions.assertEquals(200, response.getStatusCodeValue());
    verify(userExitDataService, times(1)).updateMetaData(name, appName, serviceName, request);
  }

  @Test
  @DisplayName("Update meta data - Exception scenario")
  void updateMetaDataExceptionTest() throws CommonServiceException {
    String name = "name";
    String appName = "appName";
    String serviceName = "serviceName";
    UpdateMetaDataRequest request = new UpdateMetaDataRequest();
    when(userExitDataService.updateMetaData(name, appName, serviceName, request))
        .thenThrow(new CommonServiceException(HttpStatus.INTERNAL_SERVER_ERROR, 0x1776, null));

    Assertions.assertThrows(
        CommonServiceException.class,
        () -> {
          controller.updateConfigData(name, appName, serviceName, request);
        },
        "Test exception");
    verify(userExitDataService, times(1)).updateMetaData(name, appName, serviceName, request);
  }
}
