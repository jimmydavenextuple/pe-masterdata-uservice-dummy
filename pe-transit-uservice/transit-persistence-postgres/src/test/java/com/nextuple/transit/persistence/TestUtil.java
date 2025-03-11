/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.nextuple.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import com.nextuple.transit.domain.enums.TransitBufferReqJobRefEnum;
import com.nextuple.transit.domain.inbound.FetchTransferScheduleRequest;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import com.nextuple.transit.domain.pojo.ProjectedTransitEntity;
import com.nextuple.transit.persistence.domain.TransferScheduleDomainDto;
import com.nextuple.transit.persistence.domain.TransferScheduleDomainRequest;
import com.nextuple.transit.persistence.domain.TransitBufferConfigRequestDomainDto;
import com.nextuple.transit.persistence.domain.TransitBufferDomainDto;
import com.nextuple.transit.persistence.domain.TransitBufferReqJobRefDomainDto;
import com.nextuple.transit.persistence.domain.TransitBufferV2DomainDto;
import com.nextuple.transit.persistence.domain.TransitDomainDto;
import com.nextuple.transit.persistence.domain.ZoneDomainDto;
import com.nextuple.transit.persistence.entity.TransferScheduleEntity;
import com.nextuple.transit.persistence.entity.TransitBufferConfigRequestEntity;
import com.nextuple.transit.persistence.entity.TransitBufferEntity;
import com.nextuple.transit.persistence.entity.TransitBufferReqJobRefEntity;
import com.nextuple.transit.persistence.entity.TransitBufferV2Entity;
import com.nextuple.transit.persistence.entity.TransitEntity;
import com.nextuple.transit.persistence.entity.ZoneEntity;
import java.util.ArrayList;
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
  public static final String DESTINATION_NODE = "Node2";
  public static final JsonNode CUSTOM_ATTRIBUTES =
      JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2");

  public static final JsonNode CUSTOM_ATTRIBUTES =
      JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2");

  public ZoneDomainDto getZoneDomainDto() {
    return ZoneDomainDto.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .zone(ZONE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public ZoneEntity getZoneEntity() {
    return ZoneEntity.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .zone(ZONE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public TransitBufferConfigRequestEntity getTransitBufferConfigRequestEntity(
      TransitBufferConfigRequestStatusEnum status) {
    Date bufferStartDate = new Date(1000);
    Date bufferEndDate = new Date(1000);
    return TransitBufferConfigRequestEntity.builder()
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

  public TransitBufferEntity getTransitBufferEntity() {
    return TransitBufferEntity.builder()
        .orgId(TestUtil.ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .bufferDays(BUFFER_DAYS)
        .bufferStartDate(new Date(1000))
        .bufferEndDate(new Date(1000))
        .transitBufferConfigRequestId(TRANS_BUFFER_CONFIG_REQUEST_ID)
        .build();
  }

  public TransitBufferDomainDto getTransitBufferDomainDto() {
    return TransitBufferDomainDto.builder()
        .orgId(TestUtil.ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .bufferDays(BUFFER_DAYS)
        .bufferStartDate(new Date(1000))
        .bufferEndDate(new Date(1000))
        .transitBufferConfigRequestId(TRANS_BUFFER_CONFIG_REQUEST_ID)
        .build();
  }

  public TransitBufferReqJobRefEntity getTransitBufferReqJobRefEntity() {
    return TransitBufferReqJobRefEntity.builder()
        .id(Id)
        .transitBufferReqId(TransitBufferReqId)
        .action(TransitBufferReqJobRefEnum.CREATE)
        .extReferenceId(EXTERNAL_REFERENCE)
        .build();
  }

  public TransitBufferReqJobRefDomainDto getTransitBufferReqJobRefDomainDto() {
    return TransitBufferReqJobRefDomainDto.builder()
        .id(Id)
        .transitBufferReqId(TransitBufferReqId)
        .action(TransitBufferReqJobRefEnum.CREATE)
        .extReferenceId(EXTERNAL_REFERENCE)
        .build();
  }

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

  public TransitEntity getTransitEntity(Float transitDays) {
    Date bufferStartDate = new Date(1000);
    Date bufferEndDate = new Date(1000);
    return TransitEntity.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .transitDays(transitDays)
        .bufferDays(BUFFER_DAYS)
        .bufferStartDate(bufferStartDate)
        .bufferEndDate(bufferEndDate)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public TransitEntity getTransitEntities(String carrierServiceId) {
    return TransitEntity.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_GEOZONE)
        .destinationGeozone(DESTINATION_GEOZONE)
        .carrierServiceId(carrierServiceId)
        .transitDays(TRANSIT_DAYS)
        .customAttributes(CUSTOM_ATTRIBUTES)
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

      @Override
      public JsonNode getCustomAttributes() {
        return CUSTOM_ATTRIBUTES;
      }
    };
  }

  public List<TransitBufferV2Entity> getTransitBufferV2Entities(Integer no) {
    List<TransitBufferV2Entity> list = new ArrayList<>();
    while (no-- > 0) {
      list.add(
          TransitBufferV2Entity.builder()
              .id((long) no)
              .orgId(ORG_ID)
              .destinationGeozone(DESTINATION_GEOZONE)
              .sourceGeozone(SOURCE_GEOZONE)
              .carrierServiceId(CARRIER_SERVICE_ID)
              .bufferDays(BUFFER_DAYS)
              .bufferStartDate(LocalDate.now().plusDays(no).toDate())
              .bufferEndDate(LocalDate.now().plusDays(no + 2).toDate())
              .customAttributes(CUSTOM_ATTRIBUTES)
              .build());
    }
    return list;
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
              .customAttributes(CUSTOM_ATTRIBUTES)
              .build());
    }
    return list;
  }

  public TransitBufferV2Entity getTransitBufferV2Entity(Integer no) {
    return TransitBufferV2Entity.builder()
        .id((long) no)
        .orgId(ORG_ID)
        .destinationGeozone(DESTINATION_GEOZONE)
        .sourceGeozone(SOURCE_GEOZONE)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .bufferDays(BUFFER_DAYS)
        .bufferStartDate(LocalDate.now().plusDays(no).toDate())
        .bufferEndDate(LocalDate.now().plusDays(no + 2).toDate())
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public TransferScheduleDomainDto getTransferScheduleDomainDto() {
    return TransferScheduleDomainDto.builder()
        .orgId(ORG_ID)
        .sourceNodeId(SOURCE_NODE)
        .dropoffNodeId(DESTINATION_NODE)
        .startTime(new Date())
        .endTime(new Date())
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public TransferScheduleEntity getTransferScheduleEntity() {
    return TransferScheduleEntity.builder()
        .id(1L)
        .sourceNodeId(SOURCE_NODE)
        .dropoffNodeId(DESTINATION_NODE)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public List<TransferScheduleEntity> getTransferScheduleEntityList() {
    return List.of(getTransferScheduleEntity());
  }

  public FetchTransferScheduleRequest getFetchTransferScheduleRequest(
      List<String> sourceNodeIds, List<String> dropoffNodeIds) {
    FetchTransferScheduleRequest request = new FetchTransferScheduleRequest();
    request.setSourceNodeIds(sourceNodeIds);
    request.setDropoffNodeIds(dropoffNodeIds);
    DateTime startDateTime = new DateTime(2024, 8, 1, 0, 0);
    DateTime endDateTime = new DateTime(2024, 8, 30, 23, 59);
    request.setStartDate(startDateTime.toLocalDate());
    request.setEndDate(endDateTime.toLocalDate());

    return request;
  }

  public TransferScheduleResponse getTransferScheduleResponse() {
    return TransferScheduleResponse.builder()
        .orgId(ORG_ID)
        .sourceNodeId(SOURCE_NODE)
        .dropoffNodeId(DESTINATION_NODE)
        .startTime(new Date())
        .endTime(new Date())
        .build();
  }

  public TransferScheduleDomainRequest getTransferScheduleDomainRequest() {

    return TransferScheduleDomainRequest.builder()
        .orgId(ORG_ID)
        .dropoffNodeId(DESTINATION_NODE)
        .rule("DC:KITCHEN")
        .ruleName("Rule1")
        .startTimeLowerBound(new Date())
        .endTimeLowerBound(new Date())
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }
}
