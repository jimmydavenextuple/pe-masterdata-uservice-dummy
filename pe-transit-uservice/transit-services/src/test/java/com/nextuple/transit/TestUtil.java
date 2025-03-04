/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit;

import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.PreSignedUrlResponse;
import com.nextuple.jobs.framework.common.domain.enums.JobStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponse;
import com.nextuple.jobs.framework.common.domain.pojo.JobDetailsDto;
import com.nextuple.node.domain.outbound.NodeResponse;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.api.domain.outbound.PostalCodeResponse;
import com.nextuple.promise.sourcing.rule.api.domain.enums.RulesConfigurationModuleNameEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.RulesConfigurationResponse;
import com.nextuple.transit.domain.dto.TransitBufferDetailsDto;
import com.nextuple.transit.domain.dto.TransitTimeEntriesDto;
import com.nextuple.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import com.nextuple.transit.domain.enums.TransitBufferReqJobRefEnum;
import com.nextuple.transit.domain.inbound.DistinctGeozonesResponse;
import com.nextuple.transit.domain.inbound.FetchTransferScheduleRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleCreationRequest;
import com.nextuple.transit.domain.inbound.TransferScheduleRequest;
import com.nextuple.transit.domain.inbound.TransitBufferConfigRequest;
import com.nextuple.transit.domain.inbound.TransitBufferCreationRequest;
import com.nextuple.transit.domain.inbound.TransitBufferDeletionRequest;
import com.nextuple.transit.domain.inbound.TransitBufferReqJobRefRequest;
import com.nextuple.transit.domain.inbound.TransitBufferRequest;
import com.nextuple.transit.domain.inbound.TransitBufferV2UpdationRequest;
import com.nextuple.transit.domain.inbound.TransitDataCreationRequest;
import com.nextuple.transit.domain.inbound.TransitDataUpdationRequest;
import com.nextuple.transit.domain.inbound.ZoneRequest;
import com.nextuple.transit.domain.inbound.ZoneUpdateRequest;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import com.nextuple.transit.domain.outbound.TransitBufferConfigResponse;
import com.nextuple.transit.domain.outbound.TransitBufferDetailsResponse;
import com.nextuple.transit.domain.outbound.TransitBufferReqJobRefResponse;
import com.nextuple.transit.domain.outbound.TransitBufferResponse;
import com.nextuple.transit.domain.outbound.TransitBufferV2Response;
import com.nextuple.transit.domain.outbound.TransitResponse;
import com.nextuple.transit.domain.outbound.ZoneResponse;
import com.nextuple.transit.domain.pojo.ProjectedTransitEntity;
import com.nextuple.transit.persistence.domain.TransferScheduleDomainDto;
import com.nextuple.transit.persistence.domain.TransitBufferConfigRequestDomainDto;
import com.nextuple.transit.persistence.domain.TransitBufferDomainDto;
import com.nextuple.transit.persistence.domain.TransitBufferReqJobRefDomainDto;
import com.nextuple.transit.persistence.domain.TransitBufferV2DomainDto;
import com.nextuple.transit.persistence.domain.TransitDomainDto;
import com.nextuple.transit.persistence.domain.ZoneDomainDto;
import com.nextuple.transit.persistence.entity.ZoneEntity;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class TestUtil {

  public static final String ORG_ID = "org-1";

  public static final Long TRANS_BUFFER_REQ_JOB_REF_ID = 1L;

  public static final String TRANS_BUFFER_REQ_JOB_REF_EXT_REF_ID = "1";

  public static final Long TransitBufferReqId = 2L;
  public static final Long Id = 1L;
  public static final String EXTERNAL_REFERENCE = "1";

  public static String SOURCE_GEOZONE = "source-geozone-1";
  public static String DESTINATION_GEOZONE = "destination-geozone-1";
  public static String CARRIER_SERVICE_ID = "carrier-service-id-1";
  public static String ZIP_CODE_PREFIX = "AAA";
  public static String ZIP_CODE = "AAABB1";

  public static final String CARRIER_NAME = "carrier-name-1";
  public static final String SERVICE_NAME = "service-name-1";
  public static final String SERVICE_OPTIONS = "service-options-1";
  public static final String CARRIER_ID = "carrier-1";
  public static Float TRANSIT_DAYS = 10F;

  public static Double BUFFER_DAYS = 3.0;

  public static final String SERVICE_OPTION = "serviceOption-1";
  public static final Long ID = 1L;
  public static final String JOB_ID = "1";
  public static final Long FILE_META_DATA_ID = 3L;
  public static final String CREATED_BY = "created-by";
  public static final String STORAGE_TYPE = "S3";
  public static final String FILE_PATH_WITH_BUCKET_NAME =
      "promise-s3-lambda-dev/ui/transit-buffer/2022-10-18/fsa_upload..csv";
  public static final String ACTION = "CREATE";
  public static final String BUCKET_NAME = "promise-s3-lambda-dev";
  public static final String FILE_PATH = "ui/transit-buffer/2022-10-18/fsa_upload..csv";
  public static final String DOWNLOAD_FILE_PATH =
      "ui/transit-buffer/2022-10-18/downloads/fsa_upload.csv";
  public static final String FILE_NAME = "fsa_upload.csv";
  public static final Long TRANS_BUFFER_CONFIG_REQUEST_ID = 1L;
  public static final String ZONE = "Zone1";
  public static final String SOURCE_NODE = "Node1";
  public static final String DROPOFF_NODE = "Node2";

  public TransitDomainDto getTransitDomainDto(Float transitDays) {
    Date bufferStartDate = new Date(1000);
    Date bufferEndDate = new Date(1000);
    return TransitDomainDto.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .transitDays(transitDays)
        .bufferDays(BUFFER_DAYS)
        .bufferStartDate(bufferStartDate)
        .bufferEndDate(bufferEndDate)
        .build();
  }

  public TransitDomainDto getTransitDomainDto2(Float transitDays) {
    return TransitDomainDto.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .transitDays(transitDays)
        .bufferDays(BUFFER_DAYS)
        .build();
  }

  public TransitDomainDto getTransitDomainDto3(Double bufferDays) {
    return TransitDomainDto.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .transitDays(TRANSIT_DAYS)
        .bufferDays(bufferDays)
        .build();
  }

  public TransitDomainDto getTransitDomainEntities(String carrierServiceId) {
    return TransitDomainDto.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .carrierServiceId(carrierServiceId)
        .transitDays(TRANSIT_DAYS)
        .build();
  }

  public TransitDomainDto getTransitDomainDtos(String carrierServiceId) {
    return TransitDomainDto.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .carrierServiceId(carrierServiceId)
        .transitDays(TRANSIT_DAYS)
        .build();
  }

  public ProjectedTransitEntity getProjectedTransitEntity() {
    return new ProjectedTransitEntity() {
      @Override
      public String getSourceGeozone() {
        return SOURCE_GEOZONE;
      }

      @Override
      public String getDestinationGeozone() {
        return DESTINATION_GEOZONE;
      }

      @Override
      public Float getTransitDays() {
        return TRANSIT_DAYS;
      }
    };
  }

  public TransitResponse getTransitResponse(Float transitDays) {
    Date bufferStartDate = new Date(1000);
    Date bufferEndDate = new Date(1000);
    return TransitResponse.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .transitDays(transitDays)
        .bufferDays(3.0)
        .bufferStartDate(bufferStartDate)
        .bufferEndDate(bufferEndDate)
        .build();
  }

  public TransitResponse getTransitResponse2(Double bufferDays) {
    Date bufferStartDate = new Date(1000);
    Date bufferEndDate = new Date(1000);
    return TransitResponse.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .transitDays(10f)
        .bufferDays(bufferDays)
        .build();
  }

  public TransitResponse getTransitResponse2(Float transitDays) {
    return TransitResponse.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .transitDays(transitDays)
        .bufferDays(3.0)
        .build();
  }

  public TransitDataCreationRequest getTransitDataCreationRequest(Float transitDays) {
    Date bufferStartDate = new Date(1000);
    Date bufferEndDate = new Date(1000);
    return TransitDataCreationRequest.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .transitDays(transitDays)
        .bufferDays(BUFFER_DAYS)
        .bufferStartDate(bufferStartDate)
        .bufferEndDate(bufferEndDate)
        .build();
  }

  public TransitDataCreationRequest getTransitDataCreationRequestWithMentionedDates(
      Float transitDays, Date bufferStartDate, Date bufferEndDate) {
    return TransitDataCreationRequest.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .transitDays(transitDays)
        .bufferDays(BUFFER_DAYS)
        .bufferStartDate(bufferStartDate)
        .bufferEndDate(bufferEndDate)
        .build();
  }

  public TransitDataUpdationRequest getTransitDataUpdationRequest(Float transitDays) {
    return TransitDataUpdationRequest.builder().transitDays(transitDays).build();
  }

  public TransitBufferCreationRequest getTransitBufferCreationRequest(Double bufferDays) {
    return TransitBufferCreationRequest.builder().bufferDays(bufferDays).build();
  }

  public TransitTimeEntriesDto getTransitTimeEntriesDto(String orgId, String carrierServiceId) {
    return TransitTimeEntriesDto.builder()
        .orgId(orgId)
        .carrierServiceId(carrierServiceId)
        .totalRecords(2)
        .build();
  }

  public BaseResponse<List<CarrierServiceResponse>> getCarrierServiceUpdateResponse() {
    var carrierResponse =
        List.of(
            CarrierServiceResponse.builder()
                .orgId(ORG_ID)
                .carrierId(CARRIER_ID)
                .carrierServiceId(CARRIER_SERVICE_ID)
                .carrierName(CARRIER_NAME)
                .serviceName(SERVICE_NAME)
                .serviceOptions(SERVICE_OPTIONS)
                .build());
    return BaseResponse.builder().payload(carrierResponse).build();
  }

  public BaseResponse<List<PostalCodeResponse>> getPostalCodeResponse() {
    PostalCodeResponse postalCodeResponse =
        PostalCodeResponse.builder()
            .zipCodePrefix(ZIP_CODE_PREFIX)
            .zipCode(ZIP_CODE)
            .orgId(ORG_ID)
            .city("DEL")
            .country("CA")
            .timeZone("EST")
            .build();
    return BaseResponse.builder().payload(List.of(postalCodeResponse)).build();
  }

  public BaseResponse<PostalCodeTimezoneDto> getBaseResponseOfPostalCodeTimezoneDto() {
    BaseResponse<PostalCodeTimezoneDto> response = new BaseResponse<>();
    response.setPayload(getPostalCodeTimezoneDto());
    response.setSuccess(true);
    return response;
  }

  public PostalCodeTimezoneDto getPostalCodeTimezoneDto() {
    return PostalCodeTimezoneDto.builder().orgId(ORG_ID).zipCodePrefix(SOURCE_GEOZONE).build();
  }

  public TransitBufferReqJobRefDomainDto getTransitBufferReqJobRefDomainDto() {
    return TransitBufferReqJobRefDomainDto.builder()
        .id(Id)
        .transitBufferReqId(TransitBufferReqId)
        .action(TransitBufferReqJobRefEnum.CREATE)
        .extReferenceId(EXTERNAL_REFERENCE)
        .build();
  }

  public TransitBufferReqJobRefRequest getTransBufferReqJobRefRequest() {
    return TransitBufferReqJobRefRequest.builder()
        .transitBufferReqId(TRANS_BUFFER_REQ_JOB_REF_ID)
        .extReferenceId(TRANS_BUFFER_REQ_JOB_REF_EXT_REF_ID)
        .action(TransitBufferReqJobRefEnum.CREATE)
        .build();
  }

  public TransitBufferReqJobRefResponse getTransitBufferReqJobRefResponse() {
    return TransitBufferReqJobRefResponse.builder()
        .id(Long.valueOf(1))
        .transitBufferReqId(TRANS_BUFFER_REQ_JOB_REF_ID)
        .extReferenceId(TRANS_BUFFER_REQ_JOB_REF_EXT_REF_ID)
        .action(TransitBufferReqJobRefEnum.CREATE)
        .build();
  }

  public TransitBufferReqJobRefResponse getTransBufferReqJobRefResponse1(
      TransitBufferReqJobRefEnum transitBufferReqJobRefEnum) {
    return TransitBufferReqJobRefResponse.builder()
        .id(Long.valueOf(1))
        .transitBufferReqId(TRANS_BUFFER_REQ_JOB_REF_ID)
        .extReferenceId(TRANS_BUFFER_REQ_JOB_REF_EXT_REF_ID)
        .action(transitBufferReqJobRefEnum)
        .build();
  }

  public DistinctGeozonesResponse geozonesResponse() {
    DistinctGeozonesResponse geozonesResponse = new DistinctGeozonesResponse();
    geozonesResponse.setSourceGeozones(List.of(SOURCE_GEOZONE));
    geozonesResponse.setDestinationGeozones(List.of(DESTINATION_GEOZONE));
    return geozonesResponse;
  }

  public TransitBufferConfigRequest getTransitBufferConfigRequest(String action) {
    Date bufferStartDate = new Date(1000);
    Date bufferEndDate = new Date(1000);
    return TransitBufferConfigRequest.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .bufferDays(BUFFER_DAYS)
        .startDate(bufferStartDate)
        .endDate(bufferEndDate)
        .action(action)
        .filePath(FILE_PATH_WITH_BUCKET_NAME)
        .storageType(STORAGE_TYPE)
        .build();
  }

  public TransitBufferConfigResponse getTransitBufferConfigResponse(
      TransitBufferConfigRequestStatusEnum status) {
    Date bufferStartDate = new Date(1000);
    Date bufferEndDate = new Date(1000);
    return TransitBufferConfigResponse.builder()
        .id(ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .bufferDays(BUFFER_DAYS)
        .startDate(bufferStartDate)
        .endDate(bufferEndDate)
        .status(status)
        .fileMetaDataId(FILE_META_DATA_ID)
        .build();
  }

  public TransitBufferConfigRequestDomainDto getTransitBufferConfigRequestDomainDto2(
      TransitBufferConfigRequestStatusEnum status) {
    Date bufferStartDate = new Date(1000);
    Date bufferEndDate = new Date(1000);
    return TransitBufferConfigRequestDomainDto.builder()
        .id(ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .bufferDays(BUFFER_DAYS)
        .startDate(bufferStartDate)
        .endDate(bufferEndDate)
        .status(status)
        .fileMetaDataId(FILE_META_DATA_ID)
        .build();
  }

  public TransitBufferConfigRequestDomainDto getTransitBufferConfigRequestDomainDto(
      TransitBufferConfigRequestStatusEnum status) {
    Date bufferStartDate = new Date(1000);
    Date bufferEndDate = new Date(1000);
    return TransitBufferConfigRequestDomainDto.builder()
        .id(ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .bufferDays(BUFFER_DAYS)
        .startDate(bufferStartDate)
        .endDate(bufferEndDate)
        .status(status)
        .fileMetaDataId(FILE_META_DATA_ID)
        .build();
  }

  public JobDetailsDto getJobDetailsDto(JobStatusEnum jobStatusEnum) {
    JobDetailsDto jobDetailsDto = new JobDetailsDto();
    jobDetailsDto.setJobId(JOB_ID);
    jobDetailsDto.setOrgId(ORG_ID);
    jobDetailsDto.setStatus(jobStatusEnum);
    jobDetailsDto.setJobType(JobTypeEnum.TRANSIT_BUFFER_REQUEST);
    return jobDetailsDto;
  }

  public TransitBufferDomainDto getTransitBufferDomainDto(String orgId) {
    return TransitBufferDomainDto.builder()
        .orgId(orgId)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .bufferDays(BUFFER_DAYS)
        .bufferStartDate(new Date(1000))
        .bufferEndDate(new Date(1000))
        .transitBufferConfigRequestId(TRANS_BUFFER_CONFIG_REQUEST_ID)
        .build();
  }

  public TransitBufferV2DomainDto getTransitBufferDomainDtoV2(String orgId) {
    return TransitBufferV2DomainDto.builder()
        .orgId(orgId)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .bufferDays(BUFFER_DAYS)
        .bufferStartDate(new Date(new Date().getTime() + 5 * 24 * 60 * 60 * 1000L))
        .bufferEndDate(new Date(new Date().getTime() + 10 * 24 * 60 * 60 * 1000L))
        .transitBufferConfigRequestId(TRANS_BUFFER_CONFIG_REQUEST_ID)
        .build();
  }

  public TransitBufferRequest getTransitBufferRequest() {
    return TransitBufferRequest.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .bufferDays(BUFFER_DAYS)
        .bufferStartDate(new Date(1000))
        .bufferEndDate(new Date(1000))
        .build();
  }

  public TransitBufferDeletionRequest getTransitBufferDeletionRequest() {
    return TransitBufferDeletionRequest.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .bufferStartDate(new Date(1000))
        .bufferEndDate(new Date(1000))
        .build();
  }

  public TransitBufferRequest getTransitBufferRequestV2() {
    return TransitBufferRequest.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .bufferDays(BUFFER_DAYS)
        .bufferStartDate(new Date(new Date().getTime() + 5 * 24 * 60 * 60 * 1000L))
        .bufferEndDate(new Date(new Date().getTime() + 10 * 24 * 60 * 60 * 1000L))
        .build();
  }

  public TransitBufferV2UpdationRequest getTransitBufferUpdateRequest() {
    return TransitBufferV2UpdationRequest.builder()
        .bufferDays(BUFFER_DAYS)
        .bufferStartDate(new Date(new Date().getTime() + 5 * 24 * 60 * 60 * 1000L))
        .bufferEndDate(new Date(new Date().getTime() + 10 * 24 * 60 * 60 * 1000L))
        .build();
  }

  public TransitBufferResponse getTransitBufferResponse() {
    return TransitBufferResponse.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .bufferDays(BUFFER_DAYS)
        .bufferStartDate(new Date(1000))
        .bufferEndDate(new Date(1000))
        .createdBy(CREATED_BY)
        .build();
  }

  public FileResponse getFileResponse() {
    String csvFileContent = "sourceGeozone,destinationGeozone\n" + "H1B,R1B\n" + "H2B,R2B\n";
    return FileResponse.builder()
        .filePath(FILE_PATH)
        .fileName(FILE_NAME)
        .bucketName(BUCKET_NAME)
        .contentLength(58L)
        .contentType("text/csv")
        .inputStream(new ByteArrayInputStream(csvFileContent.getBytes()))
        .build();
  }

  public FileResponse getDownloadFileResponse() {
    String csvFileContent = "sourceGeozone,destinationGeozone\n" + "H1B,R1B\n" + "H2B,R2B\n";
    return FileResponse.builder()
        .filePath(DOWNLOAD_FILE_PATH)
        .fileName(FILE_NAME)
        .bucketName(BUCKET_NAME)
        .contentLength(58L)
        .contentType("text/csv")
        .inputStream(new ByteArrayInputStream(csvFileContent.getBytes()))
        .build();
  }

  public BaseResponse<FileMetaDataResponse> getFileMetaDataResponse() {
    FileMetaDataResponse fileMetaDataResponse =
        FileMetaDataResponse.builder()
            .id(1L)
            .storageType(STORAGE_TYPE)
            .path(FILE_PATH_WITH_BUCKET_NAME)
            .name(FILE_NAME)
            .size("56")
            .build();
    BaseResponse<FileMetaDataResponse> fileMetaDataResponseBaseResponse = new BaseResponse<>();
    fileMetaDataResponseBaseResponse.setPayload(fileMetaDataResponse);
    fileMetaDataResponseBaseResponse.setSuccess(Boolean.TRUE);
    return fileMetaDataResponseBaseResponse;
  }

  public BaseResponse<JobResponse> getJobResponse() {
    JobResponse jobResponse = new JobResponse();
    jobResponse.setJobId("1");
    jobResponse.setOrgId(ORG_ID);
    BaseResponse<JobResponse> jobResponseBaseResponse = new BaseResponse<>();
    jobResponseBaseResponse.setPayload(jobResponse);
    jobResponseBaseResponse.setSuccess(Boolean.TRUE);
    return jobResponseBaseResponse;
  }

  public FileResponse getFileResponseWithEmptyCSVFile() {
    String csvFileContent = "";
    return FileResponse.builder()
        .filePath(FILE_PATH)
        .fileName(FILE_NAME)
        .bucketName(BUCKET_NAME)
        .contentLength(58L)
        .contentType("text/csv")
        .inputStream(new ByteArrayInputStream(csvFileContent.getBytes()))
        .build();
  }

  public FileResponse getFileResponseWithInvalidHeaders() {
    String csvFileContent = "invalidHeader,destinationGeozone\n" + "H1B,R1B\n" + "H2B,R2B\n";
    return FileResponse.builder()
        .filePath(FILE_PATH)
        .fileName(FILE_NAME)
        .bucketName(BUCKET_NAME)
        .contentLength(58L)
        .contentType("text/csv")
        .inputStream(new ByteArrayInputStream(csvFileContent.getBytes()))
        .build();
  }

  public FileResponse getFileResponse2() {
    String csvFileContent = "sourceGeozone,destinationGeozone\n" + "H1B,\n" + "H2B,R2B\n";
    return FileResponse.builder()
        .filePath(FILE_PATH)
        .fileName(FILE_NAME)
        .bucketName(BUCKET_NAME)
        .contentLength(58L)
        .contentType("text/csv")
        .inputStream(new ByteArrayInputStream(csvFileContent.getBytes()))
        .build();
  }

  public FileResponse getFileResponse3() {
    String csvFileContent = "sourceGeozone,destinationGeozone\n" + "H1B,R1B\n" + "H2B,\n";
    return FileResponse.builder()
        .filePath(FILE_PATH)
        .fileName(FILE_NAME)
        .bucketName(BUCKET_NAME)
        .contentLength(58L)
        .contentType("text/csv")
        .inputStream(new ByteArrayInputStream(csvFileContent.getBytes()))
        .build();
  }

  public FileResponse getFileResponseWithPartialEmptyData(String csvFileContent) {
    return FileResponse.builder()
        .filePath(FILE_PATH)
        .fileName(FILE_NAME)
        .bucketName(BUCKET_NAME)
        .contentLength(58L)
        .contentType("text/csv")
        .inputStream(new ByteArrayInputStream(csvFileContent.getBytes()))
        .build();
  }

  public PreSignedUrlResponse getPreSignedUrl() {
    return PreSignedUrlResponse.builder()
        .signedURL("signedUrl")
        .filePath("filePath")
        .storageType("S3")
        .build();
  }

  public TransitBufferConfigRequestDomainDto getTransitBufferConfigRequestDomainDto1(
      TransitBufferConfigRequestStatusEnum status) {
    Date bufferStartDate = new Date(1000);
    Date bufferEndDate = new Date(1000);
    return TransitBufferConfigRequestDomainDto.builder()
        .id(ID)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .bufferDays(BUFFER_DAYS)
        .startDate(bufferStartDate)
        .endDate(bufferEndDate)
        .status(status)
        .fileMetaDataId(FILE_META_DATA_ID)
        .downloadFileMetaDataId(2L)
        .build();
  }

  public ZoneResponse getZoneResponse() {
    return ZoneResponse.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .zone(ZONE)
        .build();
  }

  public ZoneRequest getZoneRequest() {
    return ZoneRequest.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .zone(ZONE)
        .build();
  }

  public ZoneUpdateRequest getZoneUpdationRequest() {
    return ZoneUpdateRequest.builder().zone(ZONE).build();
  }

  public ZoneEntity getZoneEntity() {
    return ZoneEntity.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .zone(ZONE)
        .build();
  }

  public ZoneDomainDto getZoneDomainDto() {
    return ZoneDomainDto.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .zone(ZONE)
        .build();
  }

  public List<ZoneEntity> getZoneEntityList() {
    ZoneEntity zoneEntity1 = getZoneEntity();

    ZoneEntity zoneEntity2 =
        ZoneEntity.builder()
            .orgId(ORG_ID)
            .carrierServiceId("carrier-service-2")
            .sourceGeozone("T2P")
            .destinationGeozone(DESTINATION_GEOZONE)
            .zone("Zone2")
            .build();

    return List.of(zoneEntity1, zoneEntity2);
  }

  public List<ZoneDomainDto> getZoneDomainDtoList() {
    ZoneDomainDto zoneDomainDto1 = getZoneDomainDto();

    ZoneDomainDto zoneDomainDto2 =
        ZoneDomainDto.builder()
            .orgId(ORG_ID)
            .carrierServiceId("carrier-service-2")
            .sourceGeozone("T2P")
            .destinationGeozone(DESTINATION_GEOZONE)
            .zone("Zone2")
            .build();

    return List.of(zoneDomainDto1, zoneDomainDto2);
  }

  public List<TransitBufferV2DomainDto> getTransitBufferV2DomainDtos(Integer no) {
    List<TransitBufferV2DomainDto> list = new ArrayList<>();
    while (no-- > 0) {
      list.add(
          TransitBufferV2DomainDto.builder()
              .id((long) no)
              .orgId(ORG_ID)
              .destinationGeozone(DESTINATION_GEOZONE)
              .sourceGeozone(SOURCE_GEOZONE)
              .carrierServiceId(CARRIER_SERVICE_ID)
              .bufferDays(BUFFER_DAYS)
              .bufferStartDate(LocalDate.now().plusDays(no).toDate())
              .bufferEndDate(LocalDate.now().plusDays(no + 2).toDate())
              .build());
    }
    return list;
  }

  public TransitBufferV2DomainDto getTransitBufferV2DomainDto(Integer no) {
    return TransitBufferV2DomainDto.builder()
        .id((long) no)
        .orgId(ORG_ID)
        .destinationGeozone(DESTINATION_GEOZONE)
        .sourceGeozone(SOURCE_GEOZONE)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .bufferDays(BUFFER_DAYS)
        .bufferStartDate(LocalDate.now().plusDays(no).toDate())
        .bufferEndDate(LocalDate.now().plusDays(no + 2).toDate())
        .build();
  }

  public TransitBufferDetailsResponse getTransitBufferDetailsResponse() {
    return TransitBufferDetailsResponse.builder()
        .carrierServiceId(CARRIER_SERVICE_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .orgId(ORG_ID)
        .destinationGeozone(DESTINATION_GEOZONE)
        .transitBuffers(
            List.of(
                TransitBufferDetailsDto.builder()
                    .bufferDays(1D)
                    .bufferStartDate(new Date())
                    .bufferEndDate(new Date())
                    .build()))
        .build();
  }

  public TransitBufferV2Response getTransitBufferV2Response(Long id) {
    return TransitBufferV2Response.builder()
        .id(id)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .bufferDays(BUFFER_DAYS)
        .bufferStartDate(new Date(1000))
        .bufferEndDate(new Date(1000))
        .build();
  }

  public TransferScheduleCreationRequest getTransferScheduleCreationRequest() {
    return TransferScheduleCreationRequest.builder()
        .orgId(ORG_ID)
        .sourceNodeId(SOURCE_NODE)
        .dropoffNodeId(DROPOFF_NODE)
        .startTime(new DateTime())
        .endTime(new DateTime())
        .build();
  }

  public TransferScheduleResponse getTransferScheduleResponse() {
    return TransferScheduleResponse.builder()
        .orgId(ORG_ID)
        .sourceNodeId(SOURCE_NODE)
        .dropoffNodeId(DROPOFF_NODE)
        .build();
  }

  public TransferScheduleRequest getTransferScheduleRequest() {
    return TransferScheduleRequest.builder()
        .orgId(ORG_ID)
        .sourceNodeId(SOURCE_NODE)
        .dropoffNodeId(DROPOFF_NODE)
        .startTime(new DateTime())
        .build();
  }

  public FetchTransferScheduleRequest getFetchTransferScheduleRequest() {
    return FetchTransferScheduleRequest.builder()
        .sourceNodeIds(Collections.singletonList(SOURCE_NODE))
        .dropoffNodeIds(Collections.singletonList(DROPOFF_NODE))
        .build();
  }

  public NodeResponse getNodeDetail(String nodeId) {
    return NodeResponse.builder().orgId(ORG_ID).nodeId(nodeId).build();
  }

  public TransferScheduleDomainDto getTransferScheduleEntity() {
    return TransferScheduleDomainDto.builder()
        .id(1L)
        .sourceNodeId(SOURCE_NODE)
        .dropoffNodeId(DROPOFF_NODE)
        .startTime(new Date())
        .endTime(new Date())
        .build();
  }

  public RulesConfigurationResponse getRuleConfiguration() {
    return RulesConfigurationResponse.builder()
        .orgId(ORG_ID)
        .rule("DC:KITCHEN")
        .ruleName("Rule1")
        .scope(SourcingAttributesDefinitionScopeEnum.TRANSFER_SCHEDULE_RULE)
        .moduleName(RulesConfigurationModuleNameEnum.TRANSFER_SCHEDULE)
        .attributeDefinitionId(123L)
        .build();
  }
}
