/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.service;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DESTINATION_GEO_ZONE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SOURCE_GEO_ZONE;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.PreSignedUrlResponse;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.jobs.framework.common.clients.FileMetaDataClient;
import com.nextuple.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.inbound.FileMetaDataCreationRequest;
import com.nextuple.jobs.framework.common.service.FileService;
import com.nextuple.jobs.framework.common.service.PreSignedUrlInterface;
import com.nextuple.transit.domain.inbound.TransitBufferRequest;
import com.nextuple.transit.domain.mapper.TransitBufferMapper;
import com.nextuple.transit.domain.outbound.TransitBufferResponse;
import com.nextuple.transit.domain.pojo.TransitDetailsValidationDto;
import com.nextuple.transit.persistence.domain.TransitBufferConfigRequestDomainDto;
import com.nextuple.transit.persistence.domain.TransitBufferDomainDto;
import com.nextuple.transit.persistence.domain.TransitBufferV2DomainDto;
import com.nextuple.transit.persistence.domain.TransitDomainDto;
import com.nextuple.transit.persistence.exception.TransitDomainException;
import com.nextuple.transit.persistence.service.TransitBufferConfigRequestPersistenceService;
import com.nextuple.transit.persistence.service.TransitBufferPersistenceService;
import com.nextuple.transit.persistence.service.TransitBufferV2PersistenceService;
import com.nextuple.transit.persistence.service.TransitPersistenceService;
import com.nextuple.transit.utils.TransitUtils;
import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TransitBufferService {
  private final TransitBufferPersistenceService transitBufferPersistenceService;
  private final TransitBufferV2PersistenceService transitBufferV2PersistenceService;

  private final FileService fileService;

  private final TransitBufferConfigRequestPersistenceService
      transitBufferConfigRequestPersistenceService;
  private final FileMetaDataClient fileMetaDataClient;
  private final PreSignedUrlInterface preSignedUrlInterface;

  public static final TransitBufferMapper INSTANCE = Mappers.getMapper(TransitBufferMapper.class);

  private static final Logger logger = LoggerFactory.getLogger(TransitBufferService.class);

  private static final String ORG_ID = "orgId";
  private static final String CARRIER_SERVICE_ID = "carrierServiceId";
  private static final String SOURCE_GEOZONE = "sourceGeozone";
  private static final String DESTINATION_GEOZONE = "destinationGeozone";
  private static final String TRANSIT_BUFFER_NOT_FOUND = "Transit buffer details not found";
  private final TransitPersistenceService transitPersistenceService;
  private static final String TRANSIT_NOT_FOUND = "Transit details not found";
  private static final String TRANSIT_BUFFER_CONFIG_REQUEST_ID = "transitBufferConfigRequestId";

  public TransitBufferResponse saveTransitBuffer(TransitBufferRequest transitBufferRequest)
      throws CommonServiceException, TransitDomainException {
    getTransitDaysAndValidateTransitDetails(transitBufferRequest);
    Optional<TransitBufferDomainDto> existingTransitBufferEntity =
        transitBufferPersistenceService
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                transitBufferRequest.getOrgId(),
                transitBufferRequest.getCarrierServiceId(),
                transitBufferRequest.getSourceGeozone(),
                transitBufferRequest.getDestinationGeozone());
    if (existingTransitBufferEntity.isPresent()) {
      Map<String, FieldError> errorMap =
          getErrorMap(
              transitBufferRequest.getOrgId(),
              transitBufferRequest.getCarrierServiceId(),
              transitBufferRequest.getSourceGeozone(),
              transitBufferRequest.getDestinationGeozone());
      throw new CommonServiceException(
          "Transit Buffer details already present", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
    try {
      var transitBufferEntity =
          transitBufferPersistenceService.saveTransitBuffer(
              INSTANCE.toTransitBufferDomainDto(transitBufferRequest));
      return INSTANCE.toTransitBufferResponse(transitBufferEntity);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to create transit buffer");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          ORG_ID, FieldError.builder().rejectedValue(transitBufferRequest.getOrgId()).build());
      errorMap.put(
          CARRIER_SERVICE_ID,
          FieldError.builder().rejectedValue(transitBufferRequest.getCarrierServiceId()).build());
      throw new CommonServiceException(
          "Unable to create transit buffer", HttpStatus.BAD_REQUEST, 0x1772, errorMap);
    }
  }

  public List<TransitBufferResponse> getTransitBuffersByOrgIdAndDestinationGeozone(
      String orgId, String destinationGeozone) throws CommonServiceException {
    try {
      List<TransitBufferDomainDto> transitBufferEntities =
          transitBufferPersistenceService.findByOrgIdAndDestinationGeozone(
              orgId, destinationGeozone);

      return INSTANCE.toTransitBufferResponseList(transitBufferEntities);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Error in fetching transit buffer details");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          DESTINATION_GEOZONE, FieldError.builder().rejectedValue(destinationGeozone).build());
      throw new CommonServiceException(
          "Unable to fetch transit buffer details", HttpStatus.BAD_REQUEST, 0x1772, errorMap);
    }
  }

  public TransitBufferResponse updateTransitBuffer(TransitBufferRequest transitBufferRequest)
      throws CommonServiceException, TransitDomainException {
    getTransitDaysAndValidateTransitDetails(transitBufferRequest);
    Optional<TransitBufferDomainDto> existingTransitBufferEntity =
        transitBufferPersistenceService
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                transitBufferRequest.getOrgId(),
                transitBufferRequest.getCarrierServiceId(),
                transitBufferRequest.getSourceGeozone(),
                transitBufferRequest.getDestinationGeozone());
    if (existingTransitBufferEntity.isPresent()) {
      existingTransitBufferEntity.get().setBufferDays(transitBufferRequest.getBufferDays());
      existingTransitBufferEntity
          .get()
          .setBufferStartDate(transitBufferRequest.getBufferStartDate());
      existingTransitBufferEntity.get().setBufferEndDate(transitBufferRequest.getBufferEndDate());
      existingTransitBufferEntity.get().setUpdatedBy(transitBufferRequest.getUpdatedBy());
      existingTransitBufferEntity.get().setLastModifiedDate(new Date());
      existingTransitBufferEntity
          .get()
          .setTransitBufferConfigRequestId(transitBufferRequest.getTransitBufferConfigRequestId());
      return INSTANCE.toTransitBufferResponse(
          transitBufferPersistenceService.saveTransitBuffer(existingTransitBufferEntity.get()));
    } else {
      Map<String, FieldError> errorMap =
          getErrorMap(
              transitBufferRequest.getOrgId(),
              transitBufferRequest.getCarrierServiceId(),
              transitBufferRequest.getSourceGeozone(),
              transitBufferRequest.getDestinationGeozone());
      throw new CommonServiceException(
          TRANSIT_BUFFER_NOT_FOUND, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
  }

  public TransitBufferResponse deleteTransitBufferDetails(
      String orgId, String carrierServiceId, String sourceGeozone, String destinationGeozone)
      throws CommonServiceException {
    Optional<TransitBufferDomainDto> transitBufferEntity =
        transitBufferPersistenceService
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                orgId, carrierServiceId, sourceGeozone, destinationGeozone);

    if (transitBufferEntity.isPresent()) {
      var transitBufferResponse = INSTANCE.toTransitBufferResponse(transitBufferEntity.get());
      transitBufferPersistenceService.deleteTransitBuffer(transitBufferEntity.get());
      return transitBufferResponse;
    } else {
      Map<String, FieldError> errorMap =
          getErrorMap(orgId, carrierServiceId, sourceGeozone, destinationGeozone);
      throw new CommonServiceException(
          TRANSIT_BUFFER_NOT_FOUND, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
  }

  private Map<String, FieldError> getErrorMap(
      String orgId, String carrierServiceId, String sourceGeozone, String destinationGeozone) {
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
    errorMap.put(CARRIER_SERVICE_ID, FieldError.builder().rejectedValue(carrierServiceId).build());
    errorMap.put(SOURCE_GEOZONE, FieldError.builder().rejectedValue(sourceGeozone).build());
    errorMap.put(
        DESTINATION_GEOZONE, FieldError.builder().rejectedValue(destinationGeozone).build());
    return errorMap;
  }

  private void getTransitDaysAndValidateTransitDetails(TransitBufferRequest transitBufferRequest)
      throws CommonServiceException, TransitDomainException {
    var transitDays =
        getTransitDaysFromExistingTransitEntity(
            transitBufferRequest.getOrgId(),
            transitBufferRequest.getSourceGeozone(),
            transitBufferRequest.getDestinationGeozone(),
            transitBufferRequest.getCarrierServiceId());
    TransitUtils.validateTransitDetails(
        TransitDetailsValidationDto.builder()
            .transitDays(transitDays)
            .bufferDays(transitBufferRequest.getBufferDays())
            .orgId(transitBufferRequest.getOrgId())
            .sourceGeozone(transitBufferRequest.getSourceGeozone())
            .destinationGeozone(transitBufferRequest.getDestinationGeozone())
            .carrierServiceId(transitBufferRequest.getCarrierServiceId())
            .build());
  }

  private Float getTransitDaysFromExistingTransitEntity(
      String orgId, String sourceGeozone, String destinationGeozone, String carrierServiceId)
      throws CommonServiceException, TransitDomainException {
    Float transitDays;
    Optional<TransitDomainDto> existingTransitEntity =
        transitPersistenceService.findTransitDetails(
            orgId, sourceGeozone, destinationGeozone, carrierServiceId);
    if (existingTransitEntity.isPresent()) {
      transitDays = existingTransitEntity.get().getTransitDays();
    } else {
      Map<String, FieldError> errorMap =
          getErrorMap(orgId, carrierServiceId, sourceGeozone, destinationGeozone);
      throw new CommonServiceException(TRANSIT_NOT_FOUND, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
    return transitDays;
  }

  public PreSignedUrlResponse getTransitBufferDetails(
      Long transitBufferConfigRequestId, String createdBy)
      throws IOException, CommonServiceException {
    // Get the transit buffer list for a given transitBufferConfigRequestId
    List<TransitBufferV2DomainDto> transitBufferV2DomainDtos =
        transitBufferV2PersistenceService.fetchTransitBufferById(transitBufferConfigRequestId);

    // Get fileMetaData from transitBufferConfigRequest table
    Optional<TransitBufferConfigRequestDomainDto> transitBufferConfigRequestDomainDto =
        transitBufferConfigRequestPersistenceService.findById(transitBufferConfigRequestId);

    validateTransitBufferConfigRequestEntity(transitBufferConfigRequestDomainDto);

    // Check if the file already exists in downloads folder
    if (transitBufferConfigRequestDomainDto.get().getDownloadFileMetaDataId() != null) {
      return preSignedUrlInterface.downloadFileURLById(
          transitBufferConfigRequestDomainDto.get().getDownloadFileMetaDataId());
    }

    return createAndUploadTransitCSVFile(
        transitBufferV2DomainDtos, transitBufferConfigRequestDomainDto, createdBy);
  }

  private PreSignedUrlResponse createAndUploadTransitCSVFile(
      List<TransitBufferV2DomainDto> transitBufferV2DomainDtos,
      Optional<TransitBufferConfigRequestDomainDto> transitBufferConfigRequestEntity,
      String createdBy)
      throws IOException, CommonServiceException {
    String filePath;
    String newFilePath;
    var bucketName = "";
    var newFilePathWithFileName = "";

    validateTransitBufferConfigRequestEntity(transitBufferConfigRequestEntity);
    var fileMetaDataResponse =
        getFileMetaDataById(transitBufferConfigRequestEntity.get().getFileMetaDataId());

    if (StringUtils.hasLength(fileMetaDataResponse.getPath())) {
      bucketName = fileMetaDataResponse.getPath().split("/", 2)[0];
      filePath = fileMetaDataResponse.getPath().split("/", 2)[1];
      newFilePath = filePath.substring(0, filePath.lastIndexOf('/')).concat("/downloads/");
      newFilePathWithFileName = newFilePath.concat(fileMetaDataResponse.getName());
    }

    FileAttribute<Set<PosixFilePermission>> attr =
        PosixFilePermissions.asFileAttribute(setFilePermissions());
    Path tempFile =
        Files.createTempFile("download-transit-buffers" + new Date().getTime(), ".csv", attr);

    try (var csvWriter = new CSVWriter(new FileWriter(tempFile.toFile(), true))) {
      var headers =
          new String[] {
            SOURCE_GEO_ZONE, DESTINATION_GEO_ZONE,
          };
      csvWriter.writeNext(headers);
      writeDataOntoFile(csvWriter, transitBufferV2DomainDtos);
      csvWriter.flush();

      var fileInfo = getFileDetails(bucketName, newFilePathWithFileName);
      if (!newFilePathWithFileName.equals(fileInfo.getFilePath())) {
        // upload file to S3
        fileService.uploadFile(bucketName, newFilePathWithFileName, tempFile.toFile());
        fileInfo = fileService.getFile(bucketName, newFilePathWithFileName);
      }

      // Create fileMetaData
      var fileMetaDataInfo = createFileMetaData(fileInfo, fileMetaDataResponse, createdBy);

      // Store the downloaded fileMetaDataId in the transit buffer config requestId
      transitBufferConfigRequestEntity.get().setDownloadFileMetaDataId(fileMetaDataInfo.getId());

      transitBufferConfigRequestPersistenceService.saveTransitBufferConfigRequest(
          transitBufferConfigRequestEntity.get());

      // Generate pre signed url
      return preSignedUrlInterface.downloadFileURLById(fileMetaDataInfo.getId());
    } finally {
      tempFile.toFile().delete(); // NOSONAR
    }
  }

  private FileResponse getFileDetails(String bucketName, String newFilePathWithFileName) {
    try {
      return fileService.getFile(bucketName, newFilePathWithFileName);
    } catch (Exception e) {
      return FileResponse.builder().build();
    }
  }

  private FileMetaDataResponse getFileMetaDataById(Long fileMetaDataId) {
    return fileMetaDataClient.findFileMetadataById(fileMetaDataId).getPayload();
  }

  private FileMetaDataResponse createFileMetaData(
      FileResponse fileInfo, FileMetaDataResponse fileMetaDataResponse, String createdBy) {
    return fileMetaDataClient
        .createFileMetadata(
            getFileMetaDataCreationRequest(
                fileInfo, fileMetaDataResponse, "Downloaded transit buffer file", createdBy))
        .getPayload();
  }

  private void validateTransitBufferConfigRequestEntity(
      Optional<TransitBufferConfigRequestDomainDto> transitBufferConfigRequestEntity)
      throws CommonServiceException {
    if (transitBufferConfigRequestEntity.isEmpty()) {
      throw new CommonServiceException(
          "Transit Buffer Config Request not found", HttpStatus.NOT_FOUND, 0x2773, null);
    }
  }

  private void writeDataOntoFile(
      CSVWriter csvWriter, List<TransitBufferV2DomainDto> transitBufferV2DomainDtos) {
    if (!CollectionUtils.isEmpty(transitBufferV2DomainDtos)) {
      transitBufferV2DomainDtos.forEach(
          transitBufferEntity ->
              csvWriter.writeNext(
                  new String[] {
                    transitBufferEntity.getSourceGeozone(),
                    transitBufferEntity.getDestinationGeozone(),
                  }));
    }
  }

  private Set<PosixFilePermission> setFilePermissions() {
    Set<PosixFilePermission> posixFilePermissions = new HashSet<>();
    posixFilePermissions.add(PosixFilePermission.OWNER_READ);
    posixFilePermissions.add(PosixFilePermission.OWNER_WRITE);
    return posixFilePermissions;
  }

  private FileMetaDataCreationRequest getFileMetaDataCreationRequest(
      FileResponse fileResponse,
      FileMetaDataResponse fileMetaDataResponse,
      String description,
      String createdBy) {
    return FileMetaDataCreationRequest.builder()
        .name(fileResponse.getFileName())
        .path("%s/%s".formatted(fileResponse.getBucketName(), fileResponse.getFilePath()))
        .size(String.valueOf(fileResponse.getContentLength()))
        .type(fileResponse.getContentType())
        .description(description)
        .storageType(fileMetaDataResponse.getType())
        .createdBy(createdBy)
        .build();
  }
}
