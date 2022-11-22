package com.hbc.transit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.carrier.domain.feign.CarrierFeign;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.jobs.framework.common.clients.FileMetaDataClient;
import com.hbc.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.hbc.jobs.framework.common.domain.outbound.PreSignedUrlResponse;
import com.hbc.jobs.framework.common.service.FileService;
import com.hbc.jobs.framework.common.service.PreSignedUrlInterface;
import com.hbc.postal.code.timezone.api.domain.feign.PostalCodeTimezoneFeign;
import com.hbc.transit.TestUtil;
import com.hbc.transit.domain.entity.TransitBufferConfigRequestEntity;
import com.hbc.transit.domain.entity.TransitBufferEntity;
import com.hbc.transit.domain.entity.TransitEntity;
import com.hbc.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import com.hbc.transit.domain.outbound.TransitBufferResponse;
import com.hbc.transit.repository.TransitBufferConfigRepository;
import com.hbc.transit.repository.TransitBufferRepository;
import com.hbc.transit.repository.TransitRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransitBufferServiceTest {

  @InjectMocks private TransitBufferService transitBufferService;

  @Mock private TransitBufferRepository transitBufferRepository;
  @Mock private TransitBufferConfigRepository transitBufferConfigRepository;
  @Mock private FileMetaDataClient fileMetaDataClient;
  @Mock private PreSignedUrlInterface preSignedUrlInterface;
  @Mock private FileService fileService;

  @InjectMocks private TestUtil testUtil;
  @Mock private TransitRepository transitRepository;
  @Mock private CarrierFeign carrierFeign;
  @Mock private PostalCodeTimezoneFeign postalCodeTimezoneFeign;

  @Test
  void saveTransitBufferTest() throws CommonServiceException {
    TransitBufferEntity transitBufferEntity = testUtil.getTransitBufferEntity(TestUtil.ORG_ID);
    when(transitBufferRepository
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Optional<TransitEntity> transitEntity =
        Optional.of(testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS));
    when(transitRepository.findByOrgIdAndSourceGeozoneAndDestinationGeozoneAndCarrierServiceId(
            any(), any(), any(), any()))
        .thenReturn(transitEntity);

    when(transitBufferRepository.save(any())).thenReturn(transitBufferEntity);
    TransitBufferResponse response =
        transitBufferService.saveTransitBuffer(testUtil.getTransitBufferRequest());

    assertEquals(transitBufferEntity.getOrgId(), response.getOrgId());
    assertEquals(transitBufferEntity.getCarrierServiceId(), response.getCarrierServiceId());
    assertEquals(transitBufferEntity.getSourceGeozone(), response.getSourceGeozone());
    assertEquals(transitBufferEntity.getDestinationGeozone(), response.getDestinationGeozone());
    verify(transitBufferRepository, times(1)).save(any());
    verify(transitRepository, times(1))
        .findByOrgIdAndSourceGeozoneAndDestinationGeozoneAndCarrierServiceId(
            any(), any(), any(), any());
  }

  @Test
  void saveTransitBufferTestException() {
    TransitBufferEntity transitBufferEntity = testUtil.getTransitBufferEntity(TestUtil.ORG_ID);
    when(transitBufferRepository
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.empty());
    when(transitBufferRepository.save(any())).thenThrow(new RuntimeException("Error while saving"));

    Optional<TransitEntity> transitEntity =
        Optional.of(testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS));
    when(transitRepository.findByOrgIdAndSourceGeozoneAndDestinationGeozoneAndCarrierServiceId(
            any(), any(), any(), any()))
        .thenReturn(transitEntity);

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> transitBufferService.saveTransitBuffer(testUtil.getTransitBufferRequest()));

    assertEquals("Unable to create transit buffer", ex.getMessage());
    verify(transitBufferRepository, times(1)).save(any());
  }

  @Test
  void saveTransitBufferEmptyTransitEntityTest() {
    when(transitRepository.findByOrgIdAndSourceGeozoneAndDestinationGeozoneAndCarrierServiceId(
            any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> transitBufferService.saveTransitBuffer(testUtil.getTransitBufferRequest()));

    assertEquals("Transit details not found", ex.getMessage());
  }

  @Test
  void saveExistingTransitBufferExceptionTest() {
    TransitBufferEntity transitBufferEntity = testUtil.getTransitBufferEntity(TestUtil.ORG_ID);
    when(transitBufferRepository
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.of(transitBufferEntity));

    Optional<TransitEntity> transitEntity =
        Optional.of(testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS));
    when(transitRepository.findByOrgIdAndSourceGeozoneAndDestinationGeozoneAndCarrierServiceId(
            any(), any(), any(), any()))
        .thenReturn(transitEntity);
    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> transitBufferService.saveTransitBuffer(testUtil.getTransitBufferRequest()));

    assertEquals("Transit Buffer details already present", ex.getMessage());
    verify(transitBufferRepository, times(1))
        .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
            any(), any(), any(), any());
    verify(transitBufferRepository, times(0)).save(any());
  }

  @Test
  void updateTransitBufferTest() throws CommonServiceException {
    TransitBufferEntity transitBufferEntity = testUtil.getTransitBufferEntity(TestUtil.ORG_ID);
    when(transitBufferRepository
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.of(transitBufferEntity));
    when(transitBufferRepository.save(any())).thenReturn(transitBufferEntity);

    Optional<TransitEntity> transitEntity =
        Optional.of(testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS));
    when(transitRepository.findByOrgIdAndSourceGeozoneAndDestinationGeozoneAndCarrierServiceId(
            any(), any(), any(), any()))
        .thenReturn(transitEntity);

    TransitBufferResponse response =
        transitBufferService.updateTransitBuffer(testUtil.getTransitBufferRequest());

    assertEquals(transitBufferEntity.getOrgId(), response.getOrgId());
    assertEquals(transitBufferEntity.getCarrierServiceId(), response.getCarrierServiceId());
    assertEquals(transitBufferEntity.getSourceGeozone(), response.getSourceGeozone());
    assertEquals(transitBufferEntity.getDestinationGeozone(), response.getDestinationGeozone());
    verify(transitBufferRepository, times(1))
        .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
            any(), any(), any(), any());
    verify(transitBufferRepository, times(1)).save(any());
  }

  @Test
  void updateTransitBufferExceptionTest() {
    when(transitBufferRepository
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Optional<TransitEntity> transitEntity =
        Optional.of(testUtil.getTransitEntity(TestUtil.TRANSIT_DAYS));
    when(transitRepository.findByOrgIdAndSourceGeozoneAndDestinationGeozoneAndCarrierServiceId(
            any(), any(), any(), any()))
        .thenReturn(transitEntity);

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> transitBufferService.updateTransitBuffer(testUtil.getTransitBufferRequest()));

    assertEquals("Transit buffer details not found", ex.getMessage());
    verify(transitBufferRepository, times(1))
        .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
            any(), any(), any(), any());
    verify(transitBufferRepository, times(0)).save(any());
  }

  @Test
  void getTransitBuffersByOrgIdAndDestinationGeozoneTest() throws CommonServiceException {
    List<TransitBufferEntity> transitBufferEntities =
        List.of(testUtil.getTransitBufferEntity(TestUtil.ORG_ID));
    when(transitBufferRepository.findByOrgIdAndDestinationGeozone(any(), any()))
        .thenReturn(transitBufferEntities);

    List<TransitBufferResponse> responses =
        transitBufferService.getTransitBuffersByOrgIdAndDestinationGeozone(
            TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE);

    assertEquals(transitBufferEntities.size(), responses.size());
    assertEquals(transitBufferEntities.get(0).getOrgId(), responses.get(0).getOrgId());
    assertEquals(
        transitBufferEntities.get(0).getCarrierServiceId(), responses.get(0).getCarrierServiceId());
    assertEquals(
        transitBufferEntities.get(0).getSourceGeozone(), responses.get(0).getSourceGeozone());
    assertEquals(
        transitBufferEntities.get(0).getDestinationGeozone(),
        responses.get(0).getDestinationGeozone());
    verify(transitBufferRepository, times(1)).findByOrgIdAndDestinationGeozone(any(), any());
  }

  @Test
  void getTransitBuffersByOrgIdAndDestinationGeozoneExceptionTest() {
    when(transitBufferRepository.findByOrgIdAndDestinationGeozone(any(), any()))
        .thenThrow(new RuntimeException("Error while fetching"));

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferService.getTransitBuffersByOrgIdAndDestinationGeozone(
                    TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE));

    assertEquals("Unable to fetch transit buffer details", ex.getMessage());
    verify(transitBufferRepository, times(1)).findByOrgIdAndDestinationGeozone(any(), any());
  }

  @Test
  void deleteTransitBufferDetailsTest() throws CommonServiceException {
    TransitBufferEntity transitBufferEntity = testUtil.getTransitBufferEntity(TestUtil.ORG_ID);
    when(transitBufferRepository
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.of(transitBufferEntity));
    doNothing().when(transitBufferRepository).delete(any());

    TransitBufferResponse response =
        transitBufferService.deleteTransitBufferDetails(
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE);

    assertEquals(transitBufferEntity.getOrgId(), response.getOrgId());
    assertEquals(transitBufferEntity.getCarrierServiceId(), response.getCarrierServiceId());
    assertEquals(transitBufferEntity.getSourceGeozone(), response.getSourceGeozone());
    assertEquals(transitBufferEntity.getDestinationGeozone(), response.getDestinationGeozone());
    verify(transitBufferRepository, times(1))
        .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
            any(), any(), any(), any());
    verify(transitBufferRepository, times(1)).delete(any());
  }

  @Test
  void deleteTransitBufferDetailsExceptionTest() {
    when(transitBufferRepository
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferService.deleteTransitBufferDetails(
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SOURCE_GEOZONE,
                    TestUtil.DESTINATION_GEOZONE));

    assertEquals("Transit buffer details not found", ex.getMessage());
    verify(transitBufferRepository, times(1))
        .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
            any(), any(), any(), any());
    verify(transitBufferRepository, times(0)).delete(any());
  }

  @Test
  void getTransitBufferDetailsTest() throws CommonServiceException, IOException {
    List<TransitBufferEntity> transitBufferEntityList =
        List.of(testUtil.getTransitBufferEntity(TestUtil.ORG_ID));
    TransitBufferConfigRequestEntity transitBufferConfigRequestEntity =
        testUtil.getTransitBufferConfigRequestEntity(TransitBufferConfigRequestStatusEnum.CREATED);
    BaseResponse<FileMetaDataResponse> fileMetaDataResponse = testUtil.getFileMetaDataResponse();
    PreSignedUrlResponse preSignedUrlResponse = testUtil.getPreSignedUrl();

    when(transitBufferRepository.findByTransitBufferConfigRequestId(anyLong()))
        .thenReturn(transitBufferEntityList);
    when(transitBufferConfigRepository.findById(anyLong()))
        .thenReturn(Optional.of(transitBufferConfigRequestEntity));
    when(fileMetaDataClient.findFileMetadataById(anyLong())).thenReturn(fileMetaDataResponse);
    when(fileMetaDataClient.createFileMetadata(any())).thenReturn(fileMetaDataResponse);
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse());
    doNothing().when(fileService).uploadFile(any(), any(), any());
    when(preSignedUrlInterface.downloadFileURLById(anyLong())).thenReturn(preSignedUrlResponse);

    PreSignedUrlResponse response = transitBufferService.getTransitBufferDetails(1L, "user1");

    assertNotNull(response);
    assertEquals(preSignedUrlResponse, response);

    verify(transitBufferRepository, times(1)).findByTransitBufferConfigRequestId(anyLong());
    verify(transitBufferConfigRepository, times(1)).findById(anyLong());
    verify(fileMetaDataClient, times(1)).findFileMetadataById(anyLong());
    verify(fileMetaDataClient, times(1)).createFileMetadata(any());
    verify(fileService, times(1)).getFile(anyString(), anyString());
    verify(fileService, times(1)).uploadFile(any(), any(), any());
    verify(preSignedUrlInterface, times(1)).downloadFileURLById(anyLong());
  }

  @Test
  void getTransitBufferDetailsFileAlreadyExistTest() throws CommonServiceException, IOException {
    List<TransitBufferEntity> transitBufferEntityList =
        List.of(testUtil.getTransitBufferEntity(TestUtil.ORG_ID));
    TransitBufferConfigRequestEntity transitBufferConfigRequestEntity =
        testUtil.getTransitBufferConfigRequestEntity1(TransitBufferConfigRequestStatusEnum.CREATED);
    BaseResponse<FileMetaDataResponse> fileMetaDataResponse = testUtil.getFileMetaDataResponse();
    PreSignedUrlResponse preSignedUrlResponse = testUtil.getPreSignedUrl();

    when(transitBufferRepository.findByTransitBufferConfigRequestId(anyLong()))
        .thenReturn(transitBufferEntityList);
    when(transitBufferConfigRepository.findById(anyLong()))
        .thenReturn(Optional.of(transitBufferConfigRequestEntity));
    when(fileMetaDataClient.findFileMetadataById(anyLong())).thenReturn(fileMetaDataResponse);
    when(preSignedUrlInterface.downloadFileURLById(anyLong())).thenReturn(preSignedUrlResponse);

    PreSignedUrlResponse response = transitBufferService.getTransitBufferDetails(1L, "user1");

    assertNotNull(response);
    assertEquals(preSignedUrlResponse, response);

    verify(transitBufferRepository, times(1)).findByTransitBufferConfigRequestId(anyLong());
    verify(transitBufferConfigRepository, times(1)).findById(anyLong());
    verify(fileMetaDataClient, times(1)).findFileMetadataById(anyLong());
    verify(preSignedUrlInterface, times(1)).downloadFileURLById(anyLong());
  }

  @Test
  void getTransitBufferDetailsFileExceptionTest() {
    List<TransitBufferEntity> transitBufferEntityList =
        List.of(testUtil.getTransitBufferEntity(TestUtil.ORG_ID));

    when(transitBufferRepository.findByTransitBufferConfigRequestId(anyLong()))
        .thenReturn(transitBufferEntityList);
    when(transitBufferConfigRepository.findById(anyLong())).thenReturn(Optional.empty());

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> transitBufferService.getTransitBufferDetails(1L, "user1"));

    assertEquals("Transit Buffer Config Request not found", ex.getMessage());

    verify(transitBufferRepository, times(1)).findByTransitBufferConfigRequestId(anyLong());
    verify(transitBufferConfigRepository, times(1)).findById(anyLong());
  }

  @Test
  void getTransitBufferDetailsWithEmptyDataTest() throws CommonServiceException, IOException {
    List<TransitBufferEntity> transitBufferEntityList = new ArrayList<>();
    TransitBufferConfigRequestEntity transitBufferConfigRequestEntity =
        testUtil.getTransitBufferConfigRequestEntity(TransitBufferConfigRequestStatusEnum.CREATED);
    BaseResponse<FileMetaDataResponse> fileMetaDataResponse = testUtil.getFileMetaDataResponse();
    PreSignedUrlResponse preSignedUrlResponse = testUtil.getPreSignedUrl();

    when(transitBufferRepository.findByTransitBufferConfigRequestId(anyLong()))
        .thenReturn(transitBufferEntityList);
    when(transitBufferConfigRepository.findById(anyLong()))
        .thenReturn(Optional.of(transitBufferConfigRequestEntity));
    when(fileMetaDataClient.findFileMetadataById(anyLong())).thenReturn(fileMetaDataResponse);
    when(fileMetaDataClient.createFileMetadata(any())).thenReturn(fileMetaDataResponse);
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse());
    doNothing().when(fileService).uploadFile(any(), any(), any());
    when(preSignedUrlInterface.downloadFileURLById(anyLong())).thenReturn(preSignedUrlResponse);

    PreSignedUrlResponse response = transitBufferService.getTransitBufferDetails(1L, "user1");

    assertNotNull(response);
    assertEquals(preSignedUrlResponse, response);

    verify(transitBufferRepository, times(1)).findByTransitBufferConfigRequestId(anyLong());
    verify(transitBufferConfigRepository, times(1)).findById(anyLong());
    verify(fileMetaDataClient, times(1)).findFileMetadataById(anyLong());
    verify(fileMetaDataClient, times(1)).createFileMetadata(any());
    verify(fileService, times(1)).getFile(anyString(), anyString());
    verify(fileService, times(1)).uploadFile(any(), any(), any());
    verify(preSignedUrlInterface, times(1)).downloadFileURLById(anyLong());
  }
}
