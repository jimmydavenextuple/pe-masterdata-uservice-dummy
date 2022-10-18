package com.hbc.jobs.dashboard.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.hbc.jobs.dashboard.common.FileMetadataTestUtil;
import com.hbc.jobs.dashboard.domain.outbound.FileMetaDataResponse;
import com.hbc.jobs.dashboard.exception.FileMetaDataException;
import com.hbc.jobs.dashboard.repository.FileMetaDataRepository;
import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class FileMetaDataServiceTest {

  @InjectMocks private FileMetaDataService fileMetadataService;

  @InjectMocks private FileMetadataTestUtil fileMetadataTestUtil;
  @Mock private FileMetaDataRepository metaDataRepository;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    MockMvcBuilders.standaloneSetup(fileMetadataService).build();
  }

  @Test
  void createRecord() throws FileMetaDataException {
    when(metaDataRepository.save(any())).thenReturn(fileMetadataTestUtil.getFileMetadata());

    FileMetaDataResponse fileMetadata =
        fileMetadataService.createFileMetadata(fileMetadataTestUtil.getFileMetadataRequest());

    Assertions.assertNotNull(fileMetadata);
    verify(metaDataRepository, times(1)).save(any());
  }

  @Test
  void CreateRecordError() {
    when(metaDataRepository.save(any())).thenThrow(RuntimeException.class);

    FileMetaDataException exception =
        assertThrows(
            FileMetaDataException.class,
            () ->
                fileMetadataService.createFileMetadata(
                    fileMetadataTestUtil.getFileMetadataRequest()));

    Assertions.assertEquals("Failed to create file metadata detail", exception.getMessage());
    verify(metaDataRepository, times(1)).save(any());
  }

  @Test
  void getRecord() throws FileMetaDataException {
    when(metaDataRepository.findById(any()))
        .thenReturn(Optional.ofNullable(fileMetadataTestUtil.getFileMetadata()));

    FileMetaDataResponse fileMetadata =
        fileMetadataService.getFileMetadataById(fileMetadataTestUtil.Id);

    Assertions.assertEquals(fileMetadata.getId(), fileMetadataTestUtil.getFileMetadata().getId());
    Assertions.assertNotNull(fileMetadata);
    verify(metaDataRepository, times(1)).findById(any());
  }

  @Test
  void GetRecordNotExists() {
    when(metaDataRepository.findById(any())).thenReturn(Optional.empty());

    FileMetaDataException exception =
        assertThrows(
            FileMetaDataException.class,
            () -> fileMetadataService.getFileMetadataById(fileMetadataTestUtil.Id));

    Assertions.assertEquals("Failed to find record: ID does not exist", exception.getMessage());
    verify(metaDataRepository, times(1)).findById(any());
  }

  @Test
  void UpdateRecord() throws Exception {
    when(metaDataRepository.findById(any()))
        .thenReturn(Optional.ofNullable(fileMetadataTestUtil.getFileMetadata()));

    FileMetaDataResponse response =
        fileMetadataService.updateFileMetadata(
            fileMetadataTestUtil.Id, fileMetadataTestUtil.getFileMetadataUpdationRequest());

    Assertions.assertEquals(response.getId(), fileMetadataTestUtil.getFileMetadata().getId());
    Assertions.assertNotNull(response);
    verify(metaDataRepository, times(1)).findById(any());
    verify(metaDataRepository, times(1)).save(any());
  }

  @Test
  void UpdateRecordNotExists() throws Exception {
    when(metaDataRepository.findById(any())).thenReturn(Optional.empty());

    FileMetaDataException exception =
        assertThrows(
            FileMetaDataException.class,
            () ->
                fileMetadataService.updateFileMetadata(
                    fileMetadataTestUtil.Id,
                    fileMetadataTestUtil.getFileMetadataUpdationRequest()));

    Assertions.assertEquals("Failed to update record: record not found", exception.getMessage());
    verify(metaDataRepository, times(0)).save(any());
    verify(metaDataRepository, times(1)).findById(any());
  }

  @Test
  void UpdateRecordSaveError() {
    when(metaDataRepository.findById(any()))
        .thenReturn(Optional.ofNullable(fileMetadataTestUtil.getFileMetadata()));

    when(metaDataRepository.save(any())).thenThrow(new RuntimeException());

    FileMetaDataException exception =
        assertThrows(
            FileMetaDataException.class,
            () ->
                fileMetadataService.updateFileMetadata(
                    fileMetadataTestUtil.Id,
                    fileMetadataTestUtil.getFileMetadataUpdationRequest()));

    Assertions.assertEquals("Failed to update file metadata detail", exception.getMessage());
    verify(metaDataRepository, times(1)).save(any());
    verify(metaDataRepository, times(1)).findById(any());
  }

  @Test
  void DeleteRecord() throws Exception {
    when(metaDataRepository.findById(any()))
        .thenReturn(Optional.ofNullable(fileMetadataTestUtil.getFileMetadata()));

    FileMetaDataResponse response =
        fileMetadataService.deleteFileMetadataById(fileMetadataTestUtil.Id);

    Assertions.assertEquals(response.getId(), fileMetadataTestUtil.getFileMetadata().getId());
    Assertions.assertNotNull(response);
    verify(metaDataRepository, times(1)).findById(any());
    verify(metaDataRepository, times(1)).deleteById(any());
  }

  @Test
  void DeleteRecordNotExists() {
    when(metaDataRepository.findById(any())).thenReturn(Optional.empty());

    FileMetaDataException exception =
        assertThrows(
            FileMetaDataException.class,
            () -> fileMetadataService.deleteFileMetadataById(fileMetadataTestUtil.Id));

    Assertions.assertEquals(
        "Failed to delete record: record not found for given id", exception.getMessage());
    verify(metaDataRepository, times(1)).findById(any());
    verify(metaDataRepository, times(0)).deleteById(any());
  }

  @Test
  void DeleteRecordError() {
    when(metaDataRepository.findById(any()))
        .thenReturn(Optional.ofNullable(fileMetadataTestUtil.getFileMetadata()));

    doThrow(new RuntimeException()).when(metaDataRepository).deleteById(any());

    FileMetaDataException exception =
        assertThrows(
            FileMetaDataException.class,
            () -> fileMetadataService.deleteFileMetadataById(fileMetadataTestUtil.Id));

    Assertions.assertEquals("Failed to delete file metadata detail", exception.getMessage());
    verify(metaDataRepository, times(1)).findById(any());
    verify(metaDataRepository, times(1)).deleteById(any());
  }
}
