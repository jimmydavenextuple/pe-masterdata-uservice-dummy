package com.hbc.jobs.dashboard.service;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.jobs.dashboard.domain.entity.FileMetaDataEntity;
import com.hbc.jobs.dashboard.domain.inbound.FileMetaDataCreationRequest;
import com.hbc.jobs.dashboard.domain.inbound.FileMetaDataUpdationRequest;
import com.hbc.jobs.dashboard.domain.mapper.FileMetaDataMapper;
import com.hbc.jobs.dashboard.exception.FileMetaDataException;
import com.hbc.jobs.dashboard.repository.FileMetaDataRepository;
import com.hbc.jobs.framework.common.domain.outbound.FileMetaDataResponse;
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
