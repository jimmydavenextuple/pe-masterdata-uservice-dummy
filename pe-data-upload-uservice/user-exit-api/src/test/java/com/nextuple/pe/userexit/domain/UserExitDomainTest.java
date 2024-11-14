package com.nextuple.pe.userexit.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.userexit.domain.entity.UserExitConfigData;
import com.nextuple.common.userexit.domain.entity.UserExitMetaData;
import com.nextuple.pe.userexit.repository.UserExitConfigDataRepository;
import com.nextuple.pe.userexit.repository.UserExitMetaDataRepository;
import com.nextuple.pe.userexit.util.TestUtil;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserExitDomainTest {
  @InjectMocks UserExitDomain userExitDomain;
  @InjectMocks TestUtil testUtil;
  @Mock UserExitMetaDataRepository userExitMetaDataRepository;
  @Mock UserExitConfigDataRepository userExitConfigDataRepository;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Fetch Meta data - Happy path")
  void findUEMetaDataByNameAppNameAndServiceName() throws CommonServiceException {
    when(userExitMetaDataRepository.findByNameAndAppNameAndServiceName(any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getUserExitMetaData()));
    Optional<UserExitMetaData> userExitMetaDataOptional =
        userExitDomain.findUEMetaDataByNameAppNameAndServiceName(
            "getSourcingRules", "PE", "pe-sourcing");
    Assertions.assertNotNull(userExitMetaDataOptional);
    Assertions.assertFalse(userExitMetaDataOptional.isEmpty());
    verify(userExitMetaDataRepository, times(1))
        .findByNameAndAppNameAndServiceName(any(), any(), any());
  }

  @Test
  @DisplayName("Fetch Meta data - Exception Scenario")
  void findUEMetaDataByNameAppNameAndServiceNameExceptionTest() throws CommonServiceException {
    when(userExitMetaDataRepository.findByNameAndAppNameAndServiceName(any(), any(), any()))
        .thenThrow(new RuntimeException("Error occurred"));
    CommonServiceException e =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              userExitDomain.findUEMetaDataByNameAppNameAndServiceName(
                  "getSourcingRules", "PE", "pe-sourcing");
            });
    verify(userExitMetaDataRepository, times(1))
        .findByNameAndAppNameAndServiceName(any(), any(), any());
    Assertions.assertEquals(500, e.getHttpStatus().value());
  }

  @Test
  @DisplayName("Fetch config data - Happy path")
  void findUEConfigDataByNameAppNameOrgIdAndServiceName() throws CommonServiceException {
    when(userExitConfigDataRepository.findByUserExitNameAndAppNameAndOrgIdAndServiceName(
            any(), any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getUserExitConfigData()));
    Optional<UserExitConfigData> userExitMetaDataOptional =
        userExitDomain.findUEConfigDataByNameAppNameOrgIdAndServiceName(
            "getSourcingRules", "PE", "NEXTUPLE", "pe-sourcing");
    Assertions.assertNotNull(userExitMetaDataOptional);
    verify(userExitConfigDataRepository, times(1))
        .findByUserExitNameAndAppNameAndOrgIdAndServiceName(any(), any(), any(), any());
    Assertions.assertFalse(userExitMetaDataOptional.isEmpty());
  }

  @Test
  @DisplayName("Fetch config data - Exception Scenario")
  void findUEConfigDataByNameAppNameOrgIdAndServiceNameExceptionTest()
      throws CommonServiceException {
    when(userExitConfigDataRepository.findByUserExitNameAndAppNameAndOrgIdAndServiceName(
            any(), any(), any(), any()))
        .thenThrow(new RuntimeException("Error occurred"));
    CommonServiceException e =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              userExitDomain.findUEConfigDataByNameAppNameOrgIdAndServiceName(
                  "getSourcingRules", "PE", "NEXTUPLE", "pe-sourcing");
            });
    Assertions.assertEquals(500, e.getHttpStatus().value());
    verify(userExitConfigDataRepository, times(1))
        .findByUserExitNameAndAppNameAndOrgIdAndServiceName(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Add config data - Happy path")
  void addUEConfigDataSuccessTest() throws CommonServiceException {
    UserExitConfigData configData = new UserExitConfigData();
    when(userExitConfigDataRepository.save(configData)).thenReturn(configData);

    UserExitConfigData result = userExitDomain.addUEConfigData(configData);
    Assertions.assertEquals(configData, result);

    verify(userExitConfigDataRepository, times(1)).save(configData);
  }

  @Test
  @DisplayName("Add config data - Exception scenario")
  void addUEConfigDataExceptionTest() {
    UserExitConfigData configData = new UserExitConfigData();
    when(userExitConfigDataRepository.save(configData))
        .thenThrow(new RuntimeException("Test exception"));

    Assertions.assertThrows(
        CommonServiceException.class, () -> userExitDomain.addUEConfigData(configData));

    verify(userExitConfigDataRepository, times(1)).save(configData);
  }

  @Test
  @DisplayName("Add meta data - Happy path")
  void addUEMetaDataSuccessTest() throws CommonServiceException {
    UserExitMetaData metaData = new UserExitMetaData();
    when(userExitMetaDataRepository.save(metaData)).thenReturn(metaData);

    UserExitMetaData result = userExitDomain.addUEMetaData(metaData);
    Assertions.assertEquals(metaData, result);

    verify(userExitMetaDataRepository, times(1)).save(metaData);
  }

  @Test
  @DisplayName("Add meta data - Exception scenario")
  void addUEMetaDataExceptionTest() {
    UserExitMetaData metaData = new UserExitMetaData();
    when(userExitMetaDataRepository.save(metaData))
        .thenThrow(new RuntimeException("Test exception"));

    Assertions.assertThrows(
        CommonServiceException.class, () -> userExitDomain.addUEMetaData(metaData));

    verify(userExitMetaDataRepository, times(1)).save(metaData);
  }
}
