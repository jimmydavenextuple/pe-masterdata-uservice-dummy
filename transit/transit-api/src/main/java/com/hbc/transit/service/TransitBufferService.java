package com.hbc.transit.service;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DESTINATION_GEO_ZONE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SOURCE_GEO_ZONE;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.error.FieldError;
import com.hbc.jobs.framework.common.clients.FileMetaDataClient;
import com.hbc.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.hbc.jobs.framework.common.domain.outbound.FileResponse;
import com.hbc.jobs.framework.common.domain.outbound.PreSignedUrlResponse;
import com.hbc.jobs.framework.common.inbound.FileMetaDataCreationRequest;
import com.hbc.jobs.framework.common.service.FileService;
import com.hbc.jobs.framework.common.service.PreSignedUrlInterface;
import com.hbc.transit.domain.entity.TransitBufferConfigRequestEntity;
import com.hbc.transit.domain.entity.TransitBufferEntity;
import com.hbc.transit.domain.entity.TransitEntity;
import com.hbc.transit.domain.inbound.TransitBufferRequest;
import com.hbc.transit.domain.mapper.TransitBufferMapper;
import com.hbc.transit.domain.outbound.TransitBufferResponse;
import com.hbc.transit.repository.TransitBufferConfigRepository;
import com.hbc.transit.repository.TransitBufferRepository;
import com.hbc.transit.repository.TransitRepository;
import com.hbc.transit.utils.TransitUtils;
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
  private final TransitBufferRepository transitBufferRepository;

  private final FileService fileService;

  private final TransitBufferConfigRepository transitBufferConfigRepository;
  private final FileMetaDataClient fileMetaDataClient;
  private final PreSignedUrlInterface preSignedUrlInterface;

  public static final TransitBufferMapper INSTANCE = Mappers.getMapper(TransitBufferMapper.class);

  private static final Logger logger = LoggerFactory.getLogger(TransitBufferService.class);

  private static final String ORG_ID = "orgId";
  private static final String CARRIER_SERVICE_ID = "carrierServiceId";
  private static final String SOURCE_GEOZONE = "sourceGeozone";
  private static final String DESTINATION_GEOZONE = "destinationGeozone";
  private static final String TRANSIT_BUFFER_NOT_FOUND = "Transit buffer details not found";
  private final TransitRepository transitRepository;
  private static final String TRANSIT_NOT_FOUND = "Transit details not found";
  private static final String TRANSIT_BUFFER_CONFIG_REQUEST_ID = "transitBufferConfigRequestId";

  public TransitBufferResponse saveTransitBuffer(TransitBufferRequest transitBufferRequest)
      throws CommonServiceException {
    getTransitDaysAndValidateTransitDetails(transitBufferRequest);
    Optional<TransitBufferEntity> existingTransitBufferEntity =
        transitBufferRepository.findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
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
          transitBufferRepository.save(INSTANCE.toTransitBufferEntity(transitBufferRequest));
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
      List<TransitBufferEntity> transitBufferEntities =
          transitBufferRepository.findByOrgIdAndDestinationGeozone(orgId, destinationGeozone);

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
      throws CommonServiceException {
    getTransitDaysAndValidateTransitDetails(transitBufferRequest);
    Optional<TransitBufferEntity> existingTransitBufferEntity =
        transitBufferRepository.findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
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
          transitBufferRepository.save(existingTransitBufferEntity.get()));
    } else {
      Map<String, FieldError> errorMap =
          getErrorMap(
              transitBufferRequest.getOrgId(),
              transitBufferRequest.getCarrierServiceId(),
              transitBufferRequest.getSourceGeozone(),
              transitBufferRequest.getDestinationGeozone());
      throw new CommonServiceException(
          TRANSIT_BUFFER_NOT_FOUND, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
  }

  public TransitBufferResponse deleteTransitBufferDetails(
      String orgId, String carrierServiceId, String sourceGeozone, String destinationGeozone)
      throws CommonServiceException {
    Optional<TransitBufferEntity> transitBufferEntity =
        transitBufferRepository.findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
            orgId, carrierServiceId, sourceGeozone, destinationGeozone);

    if (transitBufferEntity.isPresent()) {
      var transitBufferResponse = INSTANCE.toTransitBufferResponse(transitBufferEntity.get());
      transitBufferRepository.delete(transitBufferEntity.get());
      return transitBufferResponse;
    } else {
      Map<String, FieldError> errorMap =
          getErrorMap(orgId, carrierServiceId, sourceGeozone, destinationGeozone);
      throw new CommonServiceException(
          TRANSIT_BUFFER_NOT_FOUND, HttpStatus.NOT_FOUND, 0x1771, errorMap);
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
      throws CommonServiceException {
    var transitDays =
        getTransitDaysFromExistingTransitEntity(
            transitBufferRequest.getOrgId(),
            transitBufferRequest.getSourceGeozone(),
            transitBufferRequest.getDestinationGeozone(),
            transitBufferRequest.getCarrierServiceId());
    TransitUtils.validateTransitDetails(
        transitDays,
        transitBufferRequest.getBufferDays(),
        transitBufferRequest.getOrgId(),
        transitBufferRequest.getSourceGeozone(),
        transitBufferRequest.getDestinationGeozone(),
        transitBufferRequest.getCarrierServiceId());
  }

  private Float getTransitDaysFromExistingTransitEntity(
      String orgId, String sourceGeozone, String destinationGeozone, String carrierServiceId)
      throws CommonServiceException {
    Float transitDays;
    Optional<TransitEntity> existingTransitEntity =
        transitRepository.findByOrgIdAndSourceGeozoneAndDestinationGeozoneAndCarrierServiceId(
            orgId, sourceGeozone, destinationGeozone, carrierServiceId);
    if (existingTransitEntity.isPresent()) {
      transitDays = existingTransitEntity.get().getTransitDays();
    } else {
      Map<String, FieldError> errorMap =
          getErrorMap(orgId, carrierServiceId, sourceGeozone, destinationGeozone);
      throw new CommonServiceException(TRANSIT_NOT_FOUND, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return transitDays;
  }

  public PreSignedUrlResponse getTransitBufferDetails(
      Long transitBufferConfigRequestId, String createdBy)
      throws IOException, CommonServiceException {
    // Get the transit buffer list for a given transitBufferConfigRequestId
    List<TransitBufferEntity> transitBufferEntities =
        transitBufferRepository.findByTransitBufferConfigRequestId(transitBufferConfigRequestId);

    // Get fileMetaData from transitBufferConfigRequest table
    Optional<TransitBufferConfigRequestEntity> transitBufferConfigRequestEntity =
        transitBufferConfigRepository.findById(transitBufferConfigRequestId);

    if (!transitBufferConfigRequestEntity.isPresent()) {
      throw new CommonServiceException(
          "Transit Buffer Config Request not found", HttpStatus.NOT_FOUND, 0x2773, null);
    }
    var fileMetaDataResponse =
        fileMetaDataClient
            .findFileMetadataById(transitBufferConfigRequestEntity.get().getFileMetaDataId())
            .getPayload();
    String filePath;
    String newFilePath;
    var bucketName = "";
    var newFilePathWithFileName = "";
    if (StringUtils.hasLength(fileMetaDataResponse.getPath())) {
      bucketName = fileMetaDataResponse.getPath().split("/", 2)[0];
      filePath = fileMetaDataResponse.getPath().split("/", 2)[1];
      newFilePath = filePath.substring(0, filePath.lastIndexOf("/")).concat("/downloads/");
      newFilePathWithFileName = newFilePath.concat(fileMetaDataResponse.getName());
    }

    // Check if the file already exists in downloads folder
    if (transitBufferConfigRequestEntity.get().getDownloadFileMetaDataId() != null) {
      return preSignedUrlInterface.downloadFileURLById(
          transitBufferConfigRequestEntity.get().getDownloadFileMetaDataId());
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
      writeDataOntoFile(csvWriter, transitBufferEntities);
      csvWriter.flush();

      // upload file to S3
      fileService.uploadFile(bucketName, newFilePathWithFileName, tempFile.toFile());

      var fileInfo = fileService.getFile(bucketName, newFilePathWithFileName);

      // Create fileMetaData
      var fileMetaDataInfo =
          fileMetaDataClient
              .createFileMetadata(
                  getFileMetaDataCreationRequest(
                      fileInfo, fileMetaDataResponse, "Downloaded transit buffer file", createdBy))
              .getPayload();

      // Store the downloaded fileMetaDataId in the transit buffer config requestId
      transitBufferConfigRequestEntity.get().setDownloadFileMetaDataId(fileMetaDataInfo.getId());

      transitBufferConfigRepository.save(transitBufferConfigRequestEntity.get());

      // Generate pre signed url
      return preSignedUrlInterface.downloadFileURLById(fileMetaDataInfo.getId());
    } finally {
      tempFile.toFile().delete(); // NOSONAR
    }
  }

  private void writeDataOntoFile(
      CSVWriter csvWriter, List<TransitBufferEntity> transitBufferEntities) {
    if (!CollectionUtils.isEmpty(transitBufferEntities)) {
      transitBufferEntities.forEach(
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
        .path(String.format("%s/%s", fileResponse.getBucketName(), fileResponse.getFilePath()))
        .size(String.valueOf(fileResponse.getContentLength()))
        .type(fileResponse.getContentType())
        .description(description)
        .storageType(fileMetaDataResponse.getStorageType())
        .createdBy(createdBy)
        .build();
  }
}
