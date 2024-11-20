/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.service.impl;

import static com.nextuple.common.constants.CommonConstants.CARRIER_SERVICE_ID;
import static com.nextuple.common.constants.CommonConstants.DESTINATION_GEOZONE;
import static com.nextuple.common.constants.CommonConstants.ORG_ID;
import static com.nextuple.common.constants.CommonConstants.SOURCE_GEOZONE;

import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.postal.code.timezone.api.domain.feign.PostalCodeFeign;
import com.nextuple.postal.code.timezone.api.domain.outbound.PostalCodeResponse;
import com.nextuple.transit.domain.inbound.ZoneRequest;
import com.nextuple.transit.domain.inbound.ZoneUpdateRequest;
import com.nextuple.transit.domain.mapper.ZoneMapper;
import com.nextuple.transit.domain.outbound.ZoneResponse;
import com.nextuple.transit.persistence.domain.ZoneDomainDto;
import com.nextuple.transit.persistence.service.ZonePersistenceService;
import com.nextuple.transit.service.ZoneService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
@Service
public class ZoneServiceImpl implements ZoneService {

  private static final Logger logger = LoggerFactory.getLogger(ZoneServiceImpl.class);
  private static final String ZONE_EXCEPTION_MESSAGE = "Zone data not found";
  private static final String INVALID_CARRIER_DATA_EXCEPTION_MESSAGE =
      "Zone data cannot be created with given carrierServiceId and orgId";
  private static final String GEOZONE = "geoZone";
  private static final String INVALID_GEOZONE_ERROR_MESSAGE =
      "Zone data cannot be created with given geo zone";

  private final ZonePersistenceService zonePersistenceService;

  private final CarrierFeign carrierFeign;

  private final PostalCodeFeign postalCodeFeign;

  public static final ZoneMapper INSTANCE = Mappers.getMapper(ZoneMapper.class);

  @Override
  public ZoneResponse addZoneData(ZoneRequest zoneRequest)
      throws PromiseEngineException, CommonServiceException {
    validateCarrierAndGeoZoneDetails(zoneRequest);
    var zoneDomainDto = INSTANCE.convertToZoneEntity(zoneRequest);
    return INSTANCE.convertToZoneResponse(zonePersistenceService.saveZone(zoneDomainDto));
  }

  @Override
  public ZoneResponse updateZone(
      String orgId,
      String sourceGeozone,
      String destinationGeozone,
      String carrierServiceId,
      ZoneUpdateRequest zoneUpdateRequest)
      throws PromiseEngineException, CommonServiceException {
    ZoneDomainDto zoneDomainDto =
        fetchAndValidateZoneDetails(orgId, sourceGeozone, destinationGeozone, carrierServiceId);
    zoneDomainDto.setZone(zoneUpdateRequest.getZone());
    return INSTANCE.convertToZoneResponse(zonePersistenceService.saveZone(zoneDomainDto));
  }

  @Override
  public ZoneResponse getZoneDetails(
      String orgId, String sourceGeozone, String destinationGeozone, String carrierServiceId)
      throws PromiseEngineException, CommonServiceException {
    ZoneDomainDto zoneDomainDto =
        fetchAndValidateZoneDetails(orgId, sourceGeozone, destinationGeozone, carrierServiceId);
    return INSTANCE.convertToZoneResponse(zoneDomainDto);
  }

  @Override
  public ZoneResponse deleteZoneDetails(
      String orgId, String sourceGeozone, String destinationGeozone, String carrierServiceId)
      throws CommonServiceException, PromiseEngineException {
    ZoneDomainDto zoneDomainDto =
        fetchAndValidateZoneDetails(orgId, sourceGeozone, destinationGeozone, carrierServiceId);
    return INSTANCE.convertToZoneResponse(zonePersistenceService.deleteZoneDetails(zoneDomainDto));
  }

  @Override
  public List<ZoneResponse> getZoneDetailsList(String orgId, String destinationGeozone)
      throws PromiseEngineException, CommonServiceException {
    List<ZoneDomainDto> zoneEntities = fetchAndValidateZoneList(orgId, destinationGeozone);
    return INSTANCE.convertToZoneResponseList(zoneEntities);
  }

  private List<ZoneDomainDto> fetchAndValidateZoneList(String orgId, String destinationGeozone)
      throws PromiseEngineException, CommonServiceException {
    List<ZoneDomainDto> zoneDomainDtos =
        zonePersistenceService.fetchZoneByOrgIdAndDestGeozone(orgId, destinationGeozone);

    if (zoneDomainDtos.isEmpty()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          DESTINATION_GEOZONE, FieldError.builder().rejectedValue(destinationGeozone).build());
      throw new CommonServiceException(
          ZONE_EXCEPTION_MESSAGE, HttpStatus.BAD_REQUEST, 0x1772, errorMap);
    }

    return zoneDomainDtos;
  }

  private void validateCarrierAndGeoZoneDetails(ZoneRequest zoneRequest)
      throws CommonServiceException {
    // validate carrier and geo zones
    validateCarrierDetails(zoneRequest.getOrgId(), zoneRequest.getCarrierServiceId());
    validateSourceAndDestinationGeozone(zoneRequest.getOrgId(), zoneRequest.getSourceGeozone());
    validateSourceAndDestinationGeozone(
        zoneRequest.getOrgId(), zoneRequest.getDestinationGeozone());
  }

  private ZoneDomainDto fetchAndValidateZoneDetails(
      String orgId, String sourceGeozone, String destinationGeozone, String carrierServiceId)
      throws PromiseEngineException, CommonServiceException {
    Optional<ZoneDomainDto> zoneDomainDto =
        zonePersistenceService.fetchZoneDetails(
            orgId, sourceGeozone, destinationGeozone, carrierServiceId);

    if (zoneDomainDto.isEmpty()) {
      logger.error(ZONE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(SOURCE_GEOZONE, FieldError.builder().rejectedValue(sourceGeozone).build());
      errorMap.put(
          DESTINATION_GEOZONE, FieldError.builder().rejectedValue(destinationGeozone).build());
      errorMap.put(
          CARRIER_SERVICE_ID, FieldError.builder().rejectedValue(carrierServiceId).build());
      throw new CommonServiceException(
          ZONE_EXCEPTION_MESSAGE, HttpStatus.BAD_REQUEST, 0x1772, null);
    }

    return zoneDomainDto.get();
  }

  private void validateSourceAndDestinationGeozone(String orgId, String geozone)
      throws CommonServiceException {
    try {
      BaseResponse<List<PostalCodeResponse>> postalCodeResponseList =
          postalCodeFeign.getByPostalCodePrefix(orgId, geozone);
      if (CollectionUtils.isEmpty(postalCodeResponseList.getPayload())) {
        commonServiceExceptionInvalidGeoZone(orgId, geozone);
      }
    } catch (Exception e) {
      logger.error(
          "Error while fetching postal code timezone details for orgId:{} , geoZone:{}",
          orgId,
          geozone);
      commonServiceExceptionInvalidGeoZone(orgId, geozone);
    }
  }

  private void commonServiceExceptionInvalidGeoZone(String orgId, String geoZone)
      throws CommonServiceException {
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
    errorMap.put(GEOZONE, FieldError.builder().rejectedValue(geoZone).build());
    throw new CommonServiceException(
        INVALID_GEOZONE_ERROR_MESSAGE, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
  }

  private void validateCarrierDetails(String orgId, String carrierServiceId)
      throws CommonServiceException {
    try {
      var carrierServiceResponse =
          carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(carrierServiceId, orgId);
      if (CollectionUtils.isEmpty(carrierServiceResponse.getPayload())) {
        commonServiceExceptionCarrierService(orgId, carrierServiceId);
      }
    } catch (Exception e) {
      logger.error(
          "Error while fetching carrier details for orgId:{} , carrierServiceId:{}",
          orgId,
          carrierServiceId);
      commonServiceExceptionCarrierService(orgId, carrierServiceId);
    }
  }

  private void commonServiceExceptionCarrierService(String orgId, String carrierServiceId)
      throws CommonServiceException {
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
    errorMap.put(CARRIER_SERVICE_ID, FieldError.builder().rejectedValue(carrierServiceId).build());
    throw new CommonServiceException(
        INVALID_CARRIER_DATA_EXCEPTION_MESSAGE, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
  }
}
