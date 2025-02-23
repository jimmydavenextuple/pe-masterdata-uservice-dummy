/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.dashboard.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.nextuple.jobs.dashboard.common.FileMetadataTestUtil;
import com.nextuple.jobs.dashboard.exception.FileMetaDataException;
import com.nextuple.jobs.dashboard.repository.FileMetaDataRepository;
import com.nextuple.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class FileMetaDataServiceTest {

  @InjectMocks private FileMetaDataService fileMetadataService;

  @InjectMocks private FileMetadataTestUtil fileMetadataTestUtil;
  @Mock private FileMetaDataRepository metaDataRepository;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    //    MockMvcBuilders.standaloneSetup(fileMetadataService).build();
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
