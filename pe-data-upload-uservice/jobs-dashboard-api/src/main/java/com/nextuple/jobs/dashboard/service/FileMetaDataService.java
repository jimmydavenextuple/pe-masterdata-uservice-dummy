/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.dashboard.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.jobs.dashboard.domain.entity.FileMetaDataEntity;
import com.nextuple.jobs.dashboard.domain.mapper.FileMetaDataMapper;
import com.nextuple.jobs.dashboard.exception.FileMetaDataException;
import com.nextuple.jobs.dashboard.repository.FileMetaDataRepository;
import com.nextuple.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.nextuple.jobs.framework.common.inbound.FileMetaDataCreationRequest;
import com.nextuple.jobs.framework.common.inbound.FileMetaDataUpdationRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileMetaDataService {

  private final FileMetaDataRepository fileMetaDataRepository;

  public static final FileMetaDataMapper INSTANCE = Mappers.getMapper(FileMetaDataMapper.class);

  private static final Logger logger = LoggerFactory.getLogger(FileMetaDataService.class);

  public FileMetaDataResponse createFileMetadata(FileMetaDataCreationRequest fileMetadataRequest)
      throws FileMetaDataException {
    try {
      var fileMetadata = INSTANCE.fileMetadataRequestToEntity(fileMetadataRequest);

      return INSTANCE.toFileMetadataResponse(fileMetaDataRepository.save(fileMetadata));
    } catch (Exception e) {
      logger.error("Failed to create file metadata detail");
      throw new FileMetaDataException("Failed to create file metadata detail", null);
    }
  }

  public FileMetaDataResponse getFileMetadataById(Long id) throws FileMetaDataException {

    Optional<FileMetaDataEntity> fileMetadata = fileMetaDataRepository.findById(id);
    if (fileMetadata.isPresent()) {
      return INSTANCE.toFileMetadataResponse(fileMetadata.get());
    } else {
      throw new FileMetaDataException("Failed to find record: ID does not exist", id);
    }
  }

  public FileMetaDataResponse updateFileMetadata(
      Long id, FileMetaDataUpdationRequest fileMetadataRequest) throws FileMetaDataException {

    var fileMetadataFromRequest = INSTANCE.fileMetadataRequestToEntity(id, fileMetadataRequest);

    Optional<FileMetaDataEntity> fileMetadata = fileMetaDataRepository.findById(id);

    if (fileMetadata.isPresent()) {

      try {
        fileMetaDataRepository.save(fileMetadataFromRequest);
        return INSTANCE.toFileMetadataResponse(fileMetadataFromRequest);

      } catch (Exception e) {
        throw new FileMetaDataException("Failed to update file metadata detail", id);
      }
    } else {
      throw new FileMetaDataException("Failed to update record: record not found", id);
    }
  }

  public FileMetaDataResponse deleteFileMetadataById(Long id) throws FileMetaDataException {
    Optional<FileMetaDataEntity> fileMetadata = fileMetaDataRepository.findById(id);

    if (fileMetadata.isPresent()) {

      try {
        fileMetaDataRepository.deleteById(id);

        return INSTANCE.toFileMetadataResponse(fileMetadata.get());
      } catch (Exception e) {
        throw new FileMetaDataException("Failed to delete file metadata detail", id);
      }
    } else {
      throw new FileMetaDataException("Failed to delete record: record not found for given id", id);
    }
  }
}
