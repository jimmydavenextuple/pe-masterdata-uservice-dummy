package com.hbc.jobs.dashboard.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hbc.common.response.BaseResponse;
import com.hbc.jobs.dashboard.common.FileMetadataTestUtil;
import com.hbc.jobs.dashboard.common.TestUtil;
import com.hbc.jobs.dashboard.domain.entity.FileMetaDataEntity;
import com.hbc.jobs.dashboard.domain.outbound.FileMetaDataResponse;
import com.hbc.jobs.dashboard.exception.FileMetaDataException;
import com.hbc.jobs.dashboard.service.FileMetaDataService;
import com.hbc.jobs.framework.common.domain.pojo.DefaultPageProperties;
import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class FileMetaDataControllerTest {

  @InjectMocks private FileMetaDataController fileMetadataController;

  @InjectMocks private FileMetadataTestUtil fileMetadataTestUtil;

  @Mock private FileMetaDataService fileMetadataService;

  @Mock private DefaultPageProperties defaultPageProperties;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    MockMvcBuilders.standaloneSetup(fileMetadataController).build();
  }

  @Test
  void CreateNewFileMetadataRecord() throws Exception {
    FileMetaDataEntity fileMetadataEntity = fileMetadataTestUtil.getFileMetadata();

    when(fileMetadataService.createFileMetadata(any()))
        .thenReturn(fileMetadataTestUtil.getFileMetadataResponse());

    ResponseEntity<BaseResponse<FileMetaDataResponse>> responseEntity =
        fileMetadataController.createFileMetadata(fileMetadataTestUtil.getFileMetadataRequest());

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertNotNull(responseEntity.getBody());
    Assertions.assertNotNull(responseEntity.getBody().getPayload());
    Assertions.assertEquals(responseEntity.getBody().getPayload().getId(), fileMetadataTestUtil.Id);
    verify(fileMetadataService, times(1)).createFileMetadata(any());
  }

  @Test
  void GetFileMetadataById() throws Exception {
    when(fileMetadataService.getFileMetadataById(any()))
        .thenReturn(fileMetadataTestUtil.getFileMetadataResponse());

    ResponseEntity<BaseResponse<FileMetaDataResponse>> response =
        fileMetadataController.findFileMetadataById(fileMetadataTestUtil.Id);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        fileMetadataTestUtil.Id, Objects.requireNonNull(response.getBody()).getPayload().getId());
    verify(fileMetadataService, times(1)).getFileMetadataById(any());
  }

  @Test
  void UpdateFileMetadataById() throws Exception {
    when(fileMetadataService.updateFileMetadata(any(), any()))
        .thenReturn(fileMetadataTestUtil.getFileMetadataResponse());

    ResponseEntity<BaseResponse<FileMetaDataResponse>> response =
        fileMetadataController.updateFileMetadataById(
            fileMetadataTestUtil.Id, fileMetadataTestUtil.getFileMetadataUpdationRequest());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        fileMetadataTestUtil.Id, Objects.requireNonNull(response.getBody()).getPayload().getId());
    verify(fileMetadataService, times(1)).updateFileMetadata(any(), any());
  }

  @Test
  void DeleteFileMetadataById() throws Exception {

    when(fileMetadataService.deleteFileMetadataById(any()))
        .thenReturn(fileMetadataTestUtil.getFileMetadataResponse());

    ResponseEntity<BaseResponse<FileMetaDataResponse>> response =
        fileMetadataController.deleteFileMetadataById(fileMetadataTestUtil.Id);

    Assertions.assertNotNull(response);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        fileMetadataTestUtil.Id, Objects.requireNonNull(response.getBody()).getPayload().getId());
    verify(fileMetadataService, times(1)).deleteFileMetadataById(any());
  }

  @Test
  void DeleteRecordNotExists() throws Exception {
    when(fileMetadataService.deleteFileMetadataById(any()))
        .thenThrow(
            new FileMetaDataException(
                "Failed to delete record: record not found", fileMetadataTestUtil.Id));

    FileMetaDataException exception =
        assertThrows(
            FileMetaDataException.class,
            () -> fileMetadataController.deleteFileMetadataById(fileMetadataTestUtil.Id));

    Assertions.assertEquals("Failed to delete record: record not found", exception.getMessage());
    verify(fileMetadataService, times(1)).deleteFileMetadataById(any());
  }

  @Test
  void PostRecordAlreadyExists() throws Exception {
    when(fileMetadataService.createFileMetadata(any()))
        .thenThrow(
            new FileMetaDataException(
                "Failed to create record: ID already exists", fileMetadataTestUtil.Id));

    FileMetaDataException exception =
        assertThrows(
            FileMetaDataException.class,
            () ->
                fileMetadataController.createFileMetadata(
                    fileMetadataTestUtil.getFileMetadataRequest()));

    Assertions.assertEquals("Failed to create record: ID already exists", exception.getMessage());
    verify(fileMetadataService, times(1)).createFileMetadata(any());
  }

  @Test
  void UpdateRecordNotExists() throws Exception {
    when(fileMetadataService.updateFileMetadata(any(), any()))
        .thenThrow(
            new FileMetaDataException(
                "Failed to update record: record not found", fileMetadataTestUtil.Id));

    FileMetaDataException exception =
        assertThrows(
            FileMetaDataException.class,
            () ->
                fileMetadataController.updateFileMetadataById(
                    fileMetadataTestUtil.Id,
                    fileMetadataTestUtil.getFileMetadataUpdationRequest()));

    Assertions.assertEquals("Failed to update record: record not found", exception.getMessage());
    verify(fileMetadataService, times(1)).updateFileMetadata(any(), any());
  }
}
