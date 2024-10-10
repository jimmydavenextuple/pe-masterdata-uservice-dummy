/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.PreSignedUrlResponse;
import com.nextuple.jobs.framework.common.clients.FileMetaDataClient;
import com.nextuple.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.nextuple.jobs.framework.common.service.FileService;
import com.nextuple.jobs.framework.common.service.PreSignedUrlInterface;
import com.nextuple.postal.code.timezone.api.domain.feign.PostalCodeTimezoneFeign;
import com.nextuple.transit.TestUtil;
import com.nextuple.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import com.nextuple.transit.domain.outbound.TransitBufferResponse;
import com.nextuple.transit.persistence.domain.TransitBufferConfigRequestDomainDto;
import com.nextuple.transit.persistence.domain.TransitBufferDomainDto;
import com.nextuple.transit.persistence.domain.TransitBufferV2DomainDto;
import com.nextuple.transit.persistence.domain.TransitDomainDto;
import com.nextuple.transit.persistence.exception.TransitDomainException;
import com.nextuple.transit.persistence.service.impl.TransitBufferConfigRequestPersistenceServiceImpl;
import com.nextuple.transit.persistence.service.impl.TransitBufferPersistenceServiceImpl;
import com.nextuple.transit.persistence.service.impl.TransitBufferV2PersistenceServiceImpl;
import com.nextuple.transit.persistence.service.impl.TransitPersistenceServiceImpl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransitBufferServiceTest {

  @InjectMocks private TransitBufferService transitBufferService;

  @Mock private TransitBufferPersistenceServiceImpl transitBufferPersistenceService;
  @Mock private TransitBufferV2PersistenceServiceImpl transitBufferV2PersistenceService;

  @Mock
  private TransitBufferConfigRequestPersistenceServiceImpl
      transitBufferConfigRequestPersistenceService;

  @Mock private FileMetaDataClient fileMetaDataClient;
  @Mock private PreSignedUrlInterface preSignedUrlInterface;
  @Mock private FileService fileService;

  @InjectMocks private TestUtil testUtil;
  @Mock private TransitPersistenceServiceImpl transitPersistenceService;
  @Mock private CarrierFeign carrierFeign;
  @Mock private PostalCodeTimezoneFeign postalCodeTimezoneFeign;

  @Test
  void saveTransitBufferTest() throws CommonServiceException, TransitDomainException {
    TransitBufferDomainDto transitBufferDomainDto =
        testUtil.getTransitBufferDomainDto(TestUtil.ORG_ID);
    when(transitBufferPersistenceService
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Optional<TransitDomainDto> transitEntity =
        Optional.of(testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS));
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(transitEntity);

    when(transitBufferPersistenceService.saveTransitBuffer(any()))
        .thenReturn(transitBufferDomainDto);
    TransitBufferResponse response =
        transitBufferService.saveTransitBuffer(testUtil.getTransitBufferRequest());

    assertEquals(transitBufferDomainDto.getOrgId(), response.getOrgId());
    assertEquals(transitBufferDomainDto.getCarrierServiceId(), response.getCarrierServiceId());
    assertEquals(transitBufferDomainDto.getSourceGeozone(), response.getSourceGeozone());
    assertEquals(transitBufferDomainDto.getDestinationGeozone(), response.getDestinationGeozone());
    verify(transitBufferPersistenceService, times(1)).saveTransitBuffer(any());
    verify(transitPersistenceService, times(1)).findTransitDetails(any(), any(), any(), any());
  }

  @Test
  void saveTransitBufferTestException() throws CommonServiceException, TransitDomainException {
    TransitBufferDomainDto transitBufferEntity =
        testUtil.getTransitBufferDomainDto(TestUtil.ORG_ID);
    when(transitBufferPersistenceService
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.empty());
    when(transitBufferPersistenceService.saveTransitBuffer(any()))
        .thenThrow(new RuntimeException("Error while saving"));

    Optional<TransitDomainDto> transitEntity =
        Optional.of(testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS));
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(transitEntity);

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> transitBufferService.saveTransitBuffer(testUtil.getTransitBufferRequest()));

    assertEquals("Unable to create transit buffer", ex.getMessage());
    verify(transitBufferPersistenceService, times(1)).saveTransitBuffer(any());
  }

  @Test
  void deleteTransitBufferDetailsTest() throws CommonServiceException {
    TransitBufferDomainDto transitBufferDomainDto =
        testUtil.getTransitBufferDomainDto(TestUtil.ORG_ID);
    when(transitBufferPersistenceService
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.of(transitBufferDomainDto));
    when(transitBufferPersistenceService.deleteTransitBuffer(any()))
        .thenReturn(transitBufferDomainDto);

    TransitBufferResponse response =
        transitBufferService.deleteTransitBufferDetails(
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE);

    assertEquals(transitBufferDomainDto.getOrgId(), response.getOrgId());
    assertEquals(transitBufferDomainDto.getCarrierServiceId(), response.getCarrierServiceId());
    assertEquals(transitBufferDomainDto.getSourceGeozone(), response.getSourceGeozone());
    assertEquals(transitBufferDomainDto.getDestinationGeozone(), response.getDestinationGeozone());
    verify(transitBufferPersistenceService, times(1))
        .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
            any(), any(), any(), any());
    verify(transitBufferPersistenceService, times(1)).deleteTransitBuffer(any());
  }

  @Test
  void deleteTransitBufferDetailsExceptionTest() throws CommonServiceException {
    when(transitBufferPersistenceService
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferService.deleteTransitBufferDetails(
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SOURCE_GEOZONE,
                    TestUtil.DESTINATION_GEOZONE));

    assertEquals("Transit buffer details not found", ex.getMessage());
    verify(transitBufferPersistenceService, times(1))
        .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
            any(), any(), any(), any());
    verify(transitBufferPersistenceService, times(0)).delete(any());
  }

  @Test
  void getTransitBufferDetailsTest() throws CommonServiceException, IOException, IOException {
    List<TransitBufferV2DomainDto> transitBufferEntityList =
        List.of(testUtil.getTransitBufferDomainDtoV2(TestUtil.ORG_ID));
    TransitBufferConfigRequestDomainDto transitBufferConfigRequestEntity =
        testUtil.getTransitBufferConfigRequestDomainDto(
            TransitBufferConfigRequestStatusEnum.CREATED);
    BaseResponse<FileMetaDataResponse> fileMetaDataResponse = testUtil.getFileMetaDataResponse();
    PreSignedUrlResponse preSignedUrlResponse = testUtil.getPreSignedUrl();

    when(transitBufferV2PersistenceService.fetchTransitBufferById(anyLong()))
        .thenReturn(transitBufferEntityList);
    when(transitBufferConfigRequestPersistenceService.findById(anyLong()))
        .thenReturn(Optional.of(transitBufferConfigRequestEntity));
    when(fileMetaDataClient.findFileMetadataById(anyLong())).thenReturn(fileMetaDataResponse);
    when(fileMetaDataClient.createFileMetadata(any())).thenReturn(fileMetaDataResponse);
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse());
    doNothing().when(fileService).uploadFile(any(), any(), any());
    when(preSignedUrlInterface.downloadFileURLById(anyLong())).thenReturn(preSignedUrlResponse);

    PreSignedUrlResponse response = transitBufferService.getTransitBufferDetails(1L, "user1");

    assertNotNull(response);
    assertEquals(preSignedUrlResponse, response);

    verify(transitBufferV2PersistenceService, times(1)).fetchTransitBufferById(anyLong());
    verify(transitBufferConfigRequestPersistenceService, times(1)).findById(anyLong());
    verify(fileMetaDataClient, times(1)).findFileMetadataById(anyLong());
    verify(fileMetaDataClient, times(1)).createFileMetadata(any());
    verify(fileService, times(2)).getFile(anyString(), anyString());
    verify(fileService, times(1)).uploadFile(any(), any(), any());
    verify(preSignedUrlInterface, times(1)).downloadFileURLById(anyLong());
  }

  @Test
  void getTransitBufferDetailsFileAlreadyExistTest() throws CommonServiceException, IOException {
    List<TransitBufferV2DomainDto> transitBufferEntityList =
        List.of(testUtil.getTransitBufferDomainDtoV2(TestUtil.ORG_ID));
    TransitBufferConfigRequestDomainDto transitBufferConfigRequestEntity =
        testUtil.getTransitBufferConfigRequestDomainDto1(
            TransitBufferConfigRequestStatusEnum.CREATED);
    PreSignedUrlResponse preSignedUrlResponse = testUtil.getPreSignedUrl();

    when(transitBufferV2PersistenceService.fetchTransitBufferById(anyLong()))
        .thenReturn(transitBufferEntityList);
    when(transitBufferConfigRequestPersistenceService.findById(anyLong()))
        .thenReturn(Optional.of(transitBufferConfigRequestEntity));
    when(preSignedUrlInterface.downloadFileURLById(anyLong())).thenReturn(preSignedUrlResponse);

    PreSignedUrlResponse response = transitBufferService.getTransitBufferDetails(1L, "user1");

    assertNotNull(response);
    assertEquals(preSignedUrlResponse, response);

    verify(transitBufferV2PersistenceService, times(1)).fetchTransitBufferById(anyLong());
    verify(transitBufferConfigRequestPersistenceService, times(1)).findById(anyLong());
    verify(preSignedUrlInterface, times(1)).downloadFileURLById(anyLong());
  }

  @Test
  void saveTransitBufferEmptyTransitEntityTest() throws TransitDomainException {
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> transitBufferService.saveTransitBuffer(testUtil.getTransitBufferRequest()));

    assertEquals("Transit details not found", ex.getMessage());
  }

  @Test
  void saveExistingTransitBufferExceptionTest()
      throws CommonServiceException, TransitDomainException {
    TransitBufferDomainDto transitBufferDomainDto =
        testUtil.getTransitBufferDomainDto(TestUtil.ORG_ID);
    when(transitBufferPersistenceService
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.of(transitBufferDomainDto));

    Optional<TransitDomainDto> transitEntity =
        Optional.of(testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS));
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(transitEntity);
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> transitBufferService.saveTransitBuffer(testUtil.getTransitBufferRequest()));

    assertEquals("Transit Buffer details already present", ex.getMessage());
    verify(transitBufferPersistenceService, times(1))
        .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
            any(), any(), any(), any());
    verify(transitBufferPersistenceService, times(0)).saveTransitBuffer(any());
  }

  @Test
  void updateTransitBufferTest() throws CommonServiceException, TransitDomainException {
    TransitBufferDomainDto transitBufferDomainDto =
        testUtil.getTransitBufferDomainDto(TestUtil.ORG_ID);
    when(transitBufferPersistenceService
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.of(transitBufferDomainDto));
    when(transitBufferPersistenceService.saveTransitBuffer(any()))
        .thenReturn(transitBufferDomainDto);

    Optional<TransitDomainDto> transitDomainDto =
        Optional.of(testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS));
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(transitDomainDto);

    TransitBufferResponse response =
        transitBufferService.updateTransitBuffer(testUtil.getTransitBufferRequest());

    assertEquals(transitBufferDomainDto.getOrgId(), response.getOrgId());
    assertEquals(transitBufferDomainDto.getCarrierServiceId(), response.getCarrierServiceId());
    assertEquals(transitBufferDomainDto.getSourceGeozone(), response.getSourceGeozone());
    assertEquals(transitBufferDomainDto.getDestinationGeozone(), response.getDestinationGeozone());
    verify(transitBufferPersistenceService, times(1))
        .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
            any(), any(), any(), any());
    verify(transitBufferPersistenceService, times(1)).saveTransitBuffer(any());
  }

  @Test
  void updateTransitBufferExceptionTest() throws CommonServiceException, TransitDomainException {
    when(transitBufferPersistenceService
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Optional<TransitDomainDto> transitEntity =
        Optional.of(testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS));
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(transitEntity);

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> transitBufferService.updateTransitBuffer(testUtil.getTransitBufferRequest()));

    assertEquals("Transit buffer details not found", ex.getMessage());
    verify(transitBufferPersistenceService, times(1))
        .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
            any(), any(), any(), any());
    verify(transitBufferPersistenceService, times(0)).save(any());
  }

  @Test
  void getTransitBuffersByOrgIdAndDestinationGeozoneTest() throws CommonServiceException {
    List<TransitBufferDomainDto> transitBufferDomainDto =
        List.of(testUtil.getTransitBufferDomainDto(TestUtil.ORG_ID));
    when(transitBufferPersistenceService.findByOrgIdAndDestinationGeozone(any(), any()))
        .thenReturn(transitBufferDomainDto);

    List<TransitBufferResponse> responses =
        transitBufferService.getTransitBuffersByOrgIdAndDestinationGeozone(
            TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE);

    assertEquals(transitBufferDomainDto.size(), responses.size());
    assertEquals(transitBufferDomainDto.get(0).getOrgId(), responses.get(0).getOrgId());
    assertEquals(
        transitBufferDomainDto.get(0).getCarrierServiceId(),
        responses.get(0).getCarrierServiceId());
    assertEquals(
        transitBufferDomainDto.get(0).getSourceGeozone(), responses.get(0).getSourceGeozone());
    assertEquals(
        transitBufferDomainDto.get(0).getDestinationGeozone(),
        responses.get(0).getDestinationGeozone());
    verify(transitBufferPersistenceService, times(1))
        .findByOrgIdAndDestinationGeozone(any(), any());
  }

  @Test
  void getTransitBuffersByOrgIdAndDestinationGeozoneExceptionTest() throws CommonServiceException {
    when(transitBufferPersistenceService.findByOrgIdAndDestinationGeozone(any(), any()))
        .thenThrow(new RuntimeException("Error while fetching"));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferService.getTransitBuffersByOrgIdAndDestinationGeozone(
                    TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE));

    assertEquals("Unable to fetch transit buffer details", ex.getMessage());
    verify(transitBufferPersistenceService, times(1))
        .findByOrgIdAndDestinationGeozone(any(), any());
  }

  @Test
  void getTransitBufferDetailsFileExceptionTest() throws CommonServiceException {
    List<TransitBufferV2DomainDto> transitBufferDomainDtoList =
        List.of(testUtil.getTransitBufferDomainDtoV2(TestUtil.ORG_ID));

    when(transitBufferV2PersistenceService.fetchTransitBufferById(anyLong()))
        .thenReturn(transitBufferDomainDtoList);
    when(transitBufferConfigRequestPersistenceService.findById(anyLong()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> transitBufferService.getTransitBufferDetails(1L, "user1"));

    assertEquals("Transit Buffer Config Request not found", ex.getMessage());

    verify(transitBufferV2PersistenceService, times(1)).fetchTransitBufferById(anyLong());
    verify(transitBufferConfigRequestPersistenceService, times(1)).findById(anyLong());
  }

  @Test
  void getTransitBufferDetailsWithEmptyDataTest() throws CommonServiceException, IOException {
    List<TransitBufferV2DomainDto> transitBufferV2DomainDtos = new ArrayList<>();
    TransitBufferConfigRequestDomainDto transitBufferConfigRequestDomainDto =
        testUtil.getTransitBufferConfigRequestDomainDto(
            TransitBufferConfigRequestStatusEnum.CREATED);
    BaseResponse<FileMetaDataResponse> fileMetaDataResponse = testUtil.getFileMetaDataResponse();
    PreSignedUrlResponse preSignedUrlResponse = testUtil.getPreSignedUrl();

    when(transitBufferV2PersistenceService.fetchTransitBufferById(anyLong()))
        .thenReturn(transitBufferV2DomainDtos);
    when(transitBufferConfigRequestPersistenceService.findById(anyLong()))
        .thenReturn(Optional.of(transitBufferConfigRequestDomainDto));
    when(fileMetaDataClient.findFileMetadataById(anyLong())).thenReturn(fileMetaDataResponse);
    when(fileMetaDataClient.createFileMetadata(any())).thenReturn(fileMetaDataResponse);
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse());
    doNothing().when(fileService).uploadFile(any(), any(), any());

    when(preSignedUrlInterface.downloadFileURLById(anyLong())).thenReturn(preSignedUrlResponse);

    PreSignedUrlResponse response = transitBufferService.getTransitBufferDetails(1L, "user1");

    assertNotNull(response);
    assertEquals(preSignedUrlResponse, response);

    verify(transitBufferV2PersistenceService, times(1)).fetchTransitBufferById(anyLong());
    verify(transitBufferConfigRequestPersistenceService, times(1)).findById(anyLong());
    verify(fileMetaDataClient, times(1)).findFileMetadataById(anyLong());
    verify(fileMetaDataClient, times(1)).createFileMetadata(any());
    verify(fileService, times(2)).getFile(anyString(), anyString());
    verify(fileService, times(1)).uploadFile(any(), any(), any());
    verify(preSignedUrlInterface, times(1)).downloadFileURLById(anyLong());
  }

  @Test
  @DisplayName("Blob file already present, no need to upload the file")
  void getTransitBufferDetailsFileAlreadyPresentTest() throws CommonServiceException, IOException {
    List<TransitBufferV2DomainDto> transitBufferEntityList =
        List.of(testUtil.getTransitBufferDomainDtoV2(TestUtil.ORG_ID));
    TransitBufferConfigRequestDomainDto transitBufferConfigRequestDomainDto =
        testUtil.getTransitBufferConfigRequestDomainDto(
            TransitBufferConfigRequestStatusEnum.CREATED);
    BaseResponse<FileMetaDataResponse> fileMetaDataResponse = testUtil.getFileMetaDataResponse();
    PreSignedUrlResponse preSignedUrlResponse = testUtil.getPreSignedUrl();

    when(transitBufferV2PersistenceService.fetchTransitBufferById(anyLong()))
        .thenReturn(transitBufferEntityList);
    when(transitBufferConfigRequestPersistenceService.findById(anyLong()))
        .thenReturn(Optional.of(transitBufferConfigRequestDomainDto));
    when(fileMetaDataClient.findFileMetadataById(anyLong())).thenReturn(fileMetaDataResponse);
    when(fileMetaDataClient.createFileMetadata(any())).thenReturn(fileMetaDataResponse);
    when(fileService.getFile(anyString(), anyString()))
        .thenReturn(testUtil.getDownloadFileResponse());

    when(preSignedUrlInterface.downloadFileURLById(anyLong())).thenReturn(preSignedUrlResponse);

    PreSignedUrlResponse response = transitBufferService.getTransitBufferDetails(1L, "user1");

    assertNotNull(response);
    assertEquals(preSignedUrlResponse, response);

    verify(transitBufferV2PersistenceService, times(1)).fetchTransitBufferById(anyLong());
    verify(transitBufferConfigRequestPersistenceService, times(1)).findById(anyLong());
    verify(fileMetaDataClient, times(1)).findFileMetadataById(anyLong());
    verify(fileMetaDataClient, times(1)).createFileMetadata(any());
    verify(fileService, times(1)).getFile(anyString(), anyString());
    verify(fileService, times(0)).uploadFile(any(), any(), any());
    verify(preSignedUrlInterface, times(1)).downloadFileURLById(anyLong());
  }

  @Test
  @DisplayName("Blob file not present, upload the file and test")
  void getTransitBufferDetailsFileNotPresent() throws CommonServiceException, IOException {
    List<TransitBufferV2DomainDto> transitBufferV2DomainDtos = new ArrayList<>();
    TransitBufferConfigRequestDomainDto transitBufferConfigRequestEntity =
        testUtil.getTransitBufferConfigRequestDomainDto(
            TransitBufferConfigRequestStatusEnum.CREATED);
    BaseResponse<FileMetaDataResponse> fileMetaDataResponse = testUtil.getFileMetaDataResponse();
    PreSignedUrlResponse preSignedUrlResponse = testUtil.getPreSignedUrl();

    when(transitBufferV2PersistenceService.fetchTransitBufferById(anyLong()))
        .thenReturn(transitBufferV2DomainDtos);
    when(transitBufferConfigRequestPersistenceService.findById(anyLong()))
        .thenReturn(Optional.of(transitBufferConfigRequestEntity));
    when(fileMetaDataClient.findFileMetadataById(anyLong())).thenReturn(fileMetaDataResponse);
    when(fileMetaDataClient.createFileMetadata(any())).thenReturn(fileMetaDataResponse);
    when(fileService.getFile(anyString(), anyString()))
        .thenThrow(new RuntimeException())
        .thenReturn(testUtil.getDownloadFileResponse());
    doNothing().when(fileService).uploadFile(any(), any(), any());

    when(preSignedUrlInterface.downloadFileURLById(anyLong())).thenReturn(preSignedUrlResponse);

    PreSignedUrlResponse response = transitBufferService.getTransitBufferDetails(1L, "user1");

    assertNotNull(response);
    assertEquals(preSignedUrlResponse, response);

    verify(transitBufferV2PersistenceService, times(1)).fetchTransitBufferById(anyLong());
    verify(transitBufferConfigRequestPersistenceService, times(1)).findById(anyLong());
    verify(fileMetaDataClient, times(1)).findFileMetadataById(anyLong());
    verify(fileMetaDataClient, times(1)).createFileMetadata(any());
    verify(fileService, times(2)).getFile(anyString(), anyString());
    verify(fileService, times(1)).uploadFile(any(), any(), any());
    verify(preSignedUrlInterface, times(1)).downloadFileURLById(anyLong());
  }
}
