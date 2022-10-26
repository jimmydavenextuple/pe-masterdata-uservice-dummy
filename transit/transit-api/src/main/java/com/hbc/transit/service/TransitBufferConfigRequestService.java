package com.hbc.transit.service;

import static com.hbc.dataupload.common.constants.CommonDataUploadErrorConstants.NO_RECORDS_FOUND_IN_THE_CSV;
import static com.hbc.dataupload.common.constants.CommonDataUploadErrorConstants.TRANSIT_BUFFER_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.common.constants.CommonDataUploadErrorConstants.TRANSIT_BUFFER_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.BUFFER_DAYS;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.BUFFER_END_DATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.BUFFER_START_DATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CREATED_BY;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CREATE_C;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DELETE_D;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DESTINATION_GEO_ZONE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.SOURCE_GEO_ZONE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.UPDATE_U;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.error.FieldError;
import com.hbc.dataupload.common.utils.v1.DataUploadUtil;
import com.hbc.jobs.framework.common.clients.JobsDashboardClient;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.hbc.jobs.framework.common.domain.outbound.FileResponse;
import com.hbc.jobs.framework.common.domain.outbound.JobResponse;
import com.hbc.jobs.framework.common.inbound.FileMetaDataCreationRequest;
import com.hbc.jobs.framework.common.service.FileService;
import com.hbc.jobs.framework.common.utils.ExceptionUtils;
import com.hbc.transit.domain.entity.TransitBufferConfigRequestEntity;
import com.hbc.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import com.hbc.transit.domain.enums.TransitBufferReqJobRefEnum;
import com.hbc.transit.domain.inbound.TransitBufferConfigRequest;
import com.hbc.transit.domain.inbound.TransitBufferReqJobRefRequest;
import com.hbc.transit.domain.mapper.TransitBufferConfigRequestMapper;
import com.hbc.transit.domain.outbound.TransitBufferConfigResponse;
import com.hbc.transit.exception.TransitBufferReqJobRefDomainException;
import com.hbc.transit.repository.TransitBufferConfigRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import feign.FeignException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@RequiredArgsConstructor
@Service
@Slf4j
public class TransitBufferConfigRequestService {

  private static final Logger logger =
      LoggerFactory.getLogger(TransitBufferConfigRequestService.class);

  private static final String TRANSIT_BUFFER_CONFIG_REQUEST_EXCEPTION_MESSAGE =
      "Transit buffer config request not found with given id";

  public static final TransitBufferConfigRequestMapper INSTANCE =
      Mappers.getMapper(TransitBufferConfigRequestMapper.class);

  private final TransitBufferConfigRepository transitBufferConfigRepository;

  private static final String ORG_ID = "orgId";

  private static final String CARRIER_SERVICE_ID = "carrierServiceId";

  private static final String TRANSIT_BUFFER_CONFIG_REQUEST_ID = "transitBufferConfigRequestId";

  private final FileService fileService;

  private final JobsDashboardClient jobsDashboardClient;

  private final TransitBufferReqJobRefService transitBufferReqJobRefService;

  public TransitBufferConfigResponse processTransitBufferRequest(
      TransitBufferConfigRequest transitBufferConfigRequest)
      throws CommonServiceException, IOException, TransitBufferReqJobRefDomainException,
          CsvException {
    if (!ObjectUtils.isEmpty(transitBufferConfigRequest.getAction())) {
      if (transitBufferConfigRequest.getAction().equals(CREATE_C)) {

        return createTransitBufferDetails(transitBufferConfigRequest);

      } else if (transitBufferConfigRequest.getAction().equals(UPDATE_U)) {
        return updateTransitBufferDetails(transitBufferConfigRequest);
      }
    }
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(
        ORG_ID, FieldError.builder().rejectedValue(transitBufferConfigRequest.getOrgId()).build());
    errorMap.put(
        CARRIER_SERVICE_ID,
        FieldError.builder()
            .rejectedValue(transitBufferConfigRequest.getCarrierServiceId())
            .build());
    throw new CommonServiceException(
        "No action type specified / Invalid action type",
        HttpStatus.BAD_REQUEST,
        0xfffff3,
        errorMap);
  }

  private TransitBufferConfigResponse createTransitBufferDetails(
      TransitBufferConfigRequest transitBufferConfigRequest)
      throws CommonServiceException, IOException, TransitBufferReqJobRefDomainException,
          CsvException {
    String bucketName = transitBufferConfigRequest.getFilePath().split("/", 2)[0];
    String filePath = transitBufferConfigRequest.getFilePath().split("/", 2)[1];

    var fileResponse = fileService.getFile(bucketName, filePath);

    /** validating file type */
    DataUploadUtil.validateFileType(
        fileResponse.getContentType(), TRANSIT_BUFFER_DATA_UPLOAD_INVALID_FILE_TYPE);

    var csvReader = new CSVReader(new InputStreamReader(fileResponse.getInputStream()));
    List<String[]> csvFileContents = csvReader.readAll();

    /** validating empty file */
    DataUploadUtil.validateEmptyCSV(csvFileContents, NO_RECORDS_FOUND_IN_THE_CSV, csvReader);

    /** validating CSV Headers */
    DataUploadUtil.validateCSVHeaders(
        csvFileContents.get(0),
        "transit-buffer",
        TRANSIT_BUFFER_DATA_UPLOAD_INVALID_FILE_HEADERS,
        csvReader);

    var fileMetaDataCreationRequest =
        getFileMetaDataCreationRequest(
            fileResponse, transitBufferConfigRequest, "Details of file uploaded by UI", null);
    var fileMetaDataResponse =
        jobsDashboardClient.createFileMetadata(fileMetaDataCreationRequest).getPayload();

    var fileMetaDataResponseForNewFile =
        readCsvFileAndCreateNewFile(
            fileResponse, transitBufferConfigRequest, fileMetaDataResponse, csvFileContents);

    var newTransitBufferConfigRequestEntity =
        createTransitBufferConfigEntity(transitBufferConfigRequest, fileMetaDataResponse);
    var jobResponse =
        submitJob(transitBufferConfigRequest.getOrgId(), fileMetaDataResponseForNewFile.getId());
    createTransitBufferRequestJobReference(
        jobResponse, newTransitBufferConfigRequestEntity, TransitBufferReqJobRefEnum.CREATE);
    return updateTransitBufferRequestStatus(
        newTransitBufferConfigRequestEntity.getId(),
        TransitBufferConfigRequestStatusEnum.INPROGRESS);
  }

  private TransitBufferConfigResponse updateTransitBufferDetails(
      TransitBufferConfigRequest transitBufferConfigRequest)
      throws CommonServiceException, IOException, TransitBufferReqJobRefDomainException,
          CsvException {

    var transitBufferConfigRequestEntity =
        getTransitBufferRequest(transitBufferConfigRequest.getTransitBufferRequestId());
    var fileMetaDataResponse =
        jobsDashboardClient
            .findFileMetadataById(transitBufferConfigRequestEntity.getFileMetaDataId())
            .getPayload();
    String bucketName = fileMetaDataResponse.getPath().split("/", 2)[0];
    String filePath = fileMetaDataResponse.getPath().split("/", 2)[1];
    var fileResponse = fileService.getFile(bucketName, filePath);
    var csvReader = new CSVReader(new InputStreamReader(fileResponse.getInputStream()));
    List<String[]> csvFileContents = csvReader.readAll();
    var fileMetaDataResponseForNewFile =
        readCsvFileAndCreateNewFile(
            fileResponse, transitBufferConfigRequest, fileMetaDataResponse, csvFileContents);
    csvReader.close();
    transitBufferConfigRequestEntity.setStatus(TransitBufferConfigRequestStatusEnum.INACTIVE);
    var newTransitBufferConfigRequestEntity =
        createTransitBufferConfigEntity(transitBufferConfigRequest, fileMetaDataResponse);
    var jobResponse =
        submitJob(transitBufferConfigRequest.getOrgId(), fileMetaDataResponseForNewFile.getId());
    if (transitBufferConfigRequest.getAction().equals(UPDATE_U))
      createTransitBufferRequestJobReference(
          jobResponse, newTransitBufferConfigRequestEntity, TransitBufferReqJobRefEnum.UPDATE);
    if (transitBufferConfigRequest.getAction().equals(DELETE_D))
      createTransitBufferRequestJobReference(
          jobResponse, newTransitBufferConfigRequestEntity, TransitBufferReqJobRefEnum.DELETE);
    return updateTransitBufferRequestStatus(
        newTransitBufferConfigRequestEntity.getId(),
        TransitBufferConfigRequestStatusEnum.INPROGRESS);
  }

  private TransitBufferConfigRequestEntity createTransitBufferConfigEntity(
      TransitBufferConfigRequest transitBufferConfigRequest,
      FileMetaDataResponse fileMetaDataResponse) {
    var transitBufferConfigRequestEntity =
        INSTANCE.toTransitBufferConfigEntity(transitBufferConfigRequest);
    transitBufferConfigRequestEntity.setFileMetaDataId(fileMetaDataResponse.getId());
    transitBufferConfigRequestEntity.setStatus(TransitBufferConfigRequestStatusEnum.CREATED);

    return transitBufferConfigRepository.save(transitBufferConfigRequestEntity);
  }

  private void createTransitBufferRequestJobReference(
      JobResponse jobResponse,
      TransitBufferConfigRequestEntity savedTransitBufferConfigRequestEntity,
      TransitBufferReqJobRefEnum action)
      throws TransitBufferReqJobRefDomainException {

    var transitBufferReqJobRefRequest = new TransitBufferReqJobRefRequest();
    transitBufferReqJobRefRequest.setExtReferenceId(jobResponse.getJobId());
    transitBufferReqJobRefRequest.setTransitBufferReqId(
        savedTransitBufferConfigRequestEntity.getId());
    transitBufferReqJobRefRequest.setAction(action);

    transitBufferReqJobRefService.createTransitBufferReqJobRef(transitBufferReqJobRefRequest);
  }

  private JobResponse submitJob(String orgId, long id) throws CommonServiceException {

    try {
      return jobsDashboardClient
          .processJobOffline(orgId, JobTypeEnum.TRANSIT_BUFFER_REQUEST, id)
          .getPayload();
    } catch (FeignException e) {
      logger.error("Feign exception while submitting job", e);
      var errorResponse = ExceptionUtils.parseFeignException(e);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      throw new CommonServiceException(
          errorResponse.getMessage(), HttpStatus.BAD_REQUEST, 0xfffff3, errorMap);
    }
  }

  private FileMetaDataCreationRequest getFileMetaDataCreationRequest(
      FileResponse fileResponse,
      TransitBufferConfigRequest transitBufferConfigRequest,
      String description,
      Long parentFileId) {
    return FileMetaDataCreationRequest.builder()
        .name(fileResponse.getFileName())
        .path(String.format("%s/%s", fileResponse.getBucketName(), fileResponse.getFilePath()))
        .size(String.valueOf(fileResponse.getContentLength()))
        .type(fileResponse.getContentType())
        .description(description)
        .parentFileId(parentFileId)
        .storageType(transitBufferConfigRequest.getStorageType())
        .createdBy(transitBufferConfigRequest.getCreatedBy())
        .build();
  }

  private FileMetaDataResponse readCsvFileAndCreateNewFile(
      FileResponse fileResponse,
      TransitBufferConfigRequest transitBufferConfigRequest,
      FileMetaDataResponse fileMetaDataResponse,
      List<String[]> csvFileContents)
      throws IOException {
    List<String> sourceFSAList = new ArrayList<>();
    List<String> destinationFSAList = new ArrayList<>();

    // Remove the header
    csvFileContents.remove(0);

    for (String[] csvRecord : csvFileContents) {
      List<String> csvRow = Arrays.asList(csvRecord);
      if (!CollectionUtils.isEmpty(csvRow)) {
        if (!csvRow.get(0).isEmpty()) {
          sourceFSAList.add(csvRow.get(0));
        }
        if (!csvRow.get(1).isEmpty()) {
          destinationFSAList.add(csvRow.get(1));
        }
      }
    }

    var fileNameWithExtension = fileResponse.getFileName();
    var filename = fileNameWithExtension.substring(0, fileNameWithExtension.lastIndexOf("."));

    String newFileName = filename + new Date().getTime() + ".csv";
    FileAttribute<Set<PosixFilePermission>> attr =
        PosixFilePermissions.asFileAttribute(setFilePermissions());
    Path tempFile = Files.createTempFile(filename + new Date().getTime(), ".csv", attr);
    try (var csvWriter = new CSVWriter(new FileWriter(tempFile.toFile(), true))) {
      var headers =
          new String[] {
            ORG_ID,
            CARRIER_SERVICE_ID,
            SOURCE_GEO_ZONE,
            DESTINATION_GEO_ZONE,
            BUFFER_DAYS,
            BUFFER_START_DATE,
            BUFFER_END_DATE,
            ACTION,
            CREATED_BY
          };
      csvWriter.writeNext(headers);
      writeDataOntoFile(csvWriter, sourceFSAList, destinationFSAList, transitBufferConfigRequest);
      csvWriter.flush();

      String newPath = fileResponse.getFilePath().replace(fileResponse.getFileName(), newFileName);
      fileService.uploadFile(fileResponse.getBucketName(), newPath, tempFile.toFile());
      fileResponse.setFileName(newFileName);
      fileResponse.setFilePath(newPath);
      fileResponse.setContentLength(tempFile.toFile().length());
      if (ObjectUtils.isEmpty(transitBufferConfigRequest.getStorageType()))
        transitBufferConfigRequest.setStorageType(fileMetaDataResponse.getStorageType());

      var fileMetaDataCreationRequest =
          getFileMetaDataCreationRequest(
              fileResponse,
              transitBufferConfigRequest,
              "Details of the messaged file",
              fileMetaDataResponse.getId());
      return jobsDashboardClient.createFileMetadata(fileMetaDataCreationRequest).getPayload();
    } finally {
      tempFile.toFile().delete(); // NOSONAR
    }
  }

  private void writeDataOntoFile(
      CSVWriter csvWriter,
      List<String> sourceFSAList,
      List<String> destinationFSAList,
      TransitBufferConfigRequest transitBufferConfigRequest) {
    if (!CollectionUtils.isEmpty(sourceFSAList) && !CollectionUtils.isEmpty(destinationFSAList)) {
      sourceFSAList.forEach(
          sourceFSA -> {
            var sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            var orgId = transitBufferConfigRequest.getOrgId();
            var carrierServiceId = transitBufferConfigRequest.getCarrierServiceId();
            var bufferDays = transitBufferConfigRequest.getBufferDays();
            var bufferStartDate = sdf.format(transitBufferConfigRequest.getStartDate());
            var bufferEndDate = sdf.format(transitBufferConfigRequest.getEndDate());
            var action = transitBufferConfigRequest.getAction();
            var createdBy = transitBufferConfigRequest.getCreatedBy();

            destinationFSAList.forEach(
                destinationFSA ->
                    csvWriter.writeNext(
                        new String[] {
                          orgId,
                          carrierServiceId,
                          sourceFSA,
                          destinationFSA,
                          String.valueOf(bufferDays),
                          bufferStartDate,
                          bufferEndDate,
                          action,
                          createdBy
                        }));
          });
    }
  }

  public TransitBufferConfigResponse updateTransitBufferRequestStatus(
      Long id, TransitBufferConfigRequestStatusEnum status) throws CommonServiceException {

    Optional<TransitBufferConfigRequestEntity> existingTransitBufferConfigRequestEntity =
        transitBufferConfigRepository.findById(id);

    if (existingTransitBufferConfigRequestEntity.isEmpty()) {
      logger.error(TRANSIT_BUFFER_CONFIG_REQUEST_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          TRANSIT_BUFFER_CONFIG_REQUEST_ID, FieldError.builder().rejectedValue(id).build());
      throw new CommonServiceException(
          TRANSIT_BUFFER_CONFIG_REQUEST_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    existingTransitBufferConfigRequestEntity.get().setStatus(status);
    return INSTANCE.toTransitBufferConfigResponse(
        transitBufferConfigRepository.save(existingTransitBufferConfigRequestEntity.get()));
  }

  public List<TransitBufferConfigResponse> fetchTransitBufferRequests(
      String orgId, String carrierServiceId) throws CommonServiceException {
    try {
      List<String> filteredStatus =
          List.of(
              TransitBufferConfigRequestStatusEnum.INACTIVE.getStatus(),
              TransitBufferConfigRequestStatusEnum.DELETED.getStatus());
      List<TransitBufferConfigRequestEntity> transitBufferConfigRequestEntities =
          transitBufferConfigRepository.findByOrgIdAndCarrierServiceId(
              orgId, carrierServiceId, filteredStatus);

      return INSTANCE.toTransitBufferConfigResponseList(transitBufferConfigRequestEntities);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to fetch transit buffer requests");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          CARRIER_SERVICE_ID, FieldError.builder().rejectedValue(carrierServiceId).build());
      throw new CommonServiceException(
          "Unable to fetch transit buffer requests", HttpStatus.BAD_REQUEST, 0x1772, errorMap);
    }
  }

  public TransitBufferConfigRequestEntity getTransitBufferRequest(Long id)
      throws CommonServiceException {

    Optional<TransitBufferConfigRequestEntity> transitBufferConfigRequestEntity =
        transitBufferConfigRepository.findById(id);

    if (transitBufferConfigRequestEntity.isEmpty()) {
      logger.error(TRANSIT_BUFFER_CONFIG_REQUEST_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          TRANSIT_BUFFER_CONFIG_REQUEST_ID, FieldError.builder().rejectedValue(id).build());
      throw new CommonServiceException(
          TRANSIT_BUFFER_CONFIG_REQUEST_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return transitBufferConfigRequestEntity.get();
  }

  private Set<PosixFilePermission> setFilePermissions() {
    Set<PosixFilePermission> posixFilePermissions = new HashSet<>();
    posixFilePermissions.add(PosixFilePermission.OWNER_READ);
    posixFilePermissions.add(PosixFilePermission.OWNER_WRITE);
    return posixFilePermissions;
  }

  public TransitBufferConfigResponse deleteTransitBufferRequest(
      Long parentRequestId, String createdBy)
      throws CommonServiceException, IOException, TransitBufferReqJobRefDomainException,
          CsvException {

    var transitBufferConfigRequestEntity = getTransitBufferRequest(parentRequestId);
    var transitBufferConfigRequest = new TransitBufferConfigRequest();
    transitBufferConfigRequest.setTransitBufferRequestId(parentRequestId);
    transitBufferConfigRequest.setBufferDays(transitBufferConfigRequestEntity.getBufferDays());
    transitBufferConfigRequest.setAction(DELETE_D);
    transitBufferConfigRequest.setCarrierServiceId(
        transitBufferConfigRequestEntity.getCarrierServiceId());
    transitBufferConfigRequest.setOrgId(transitBufferConfigRequestEntity.getOrgId());
    transitBufferConfigRequest.setEndDate(transitBufferConfigRequestEntity.getEndDate());
    transitBufferConfigRequest.setStartDate(transitBufferConfigRequestEntity.getStartDate());
    transitBufferConfigRequest.setCreatedBy(createdBy);
    return updateTransitBufferDetails(transitBufferConfigRequest);
  }
}
