/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.postal.code.timezone.api.domain.feign.PostalCodeFeign;
import com.nextuple.postal.code.timezone.api.domain.outbound.PostalCodeResponse;
import com.nextuple.transit.TestUtil;
import com.nextuple.transit.domain.inbound.ZoneRequest;
import com.nextuple.transit.domain.inbound.ZoneUpdateRequest;
import com.nextuple.transit.domain.outbound.ZoneResponse;
import com.nextuple.transit.persistence.domain.ZoneDomainDto;
import com.nextuple.transit.persistence.service.ZonePersistenceService;
import com.nextuple.transit.service.TransitService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ZoneServiceImplTest {

  @InjectMocks ZoneServiceImpl zoneService;
  @Mock ZonePersistenceService zonePersistenceService;
  @Mock TransitService transitService;
  @Mock CarrierFeign carrierFeign;
  @Mock PostalCodeFeign postalCodeFeign;
  @InjectMocks TestUtil testUtil;

  @Test
  void addZoneDataTest() throws PromiseEngineException, CommonServiceException {
    ZoneRequest zoneRequest = testUtil.getZoneRequest();

    when(postalCodeFeign.getByPostalCodePrefix(any(), any()))
        .thenReturn(testUtil.getPostalCodeResponse());
    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(testUtil.getCarrierServiceUpdateResponse());
    when(zonePersistenceService.saveZone(any(ZoneDomainDto.class)))
        .thenReturn(testUtil.getZoneDomainDto());

    ZoneResponse response = zoneService.addZoneData(zoneRequest);

    assertNotNull(response);
    assertEquals(zoneRequest.getCarrierServiceId(), response.getCarrierServiceId());
    assertEquals(zoneRequest.getZone(), response.getZone());
    assertEquals(zoneRequest.getCustomAttributes(), response.getCustomAttributes());
    verify(zonePersistenceService, times(1)).saveZone(any(ZoneDomainDto.class));
  }

  @Test
  void addZoneDataInvalidCarrierDetailsTest()
      throws PromiseEngineException, CommonServiceException {
    ZoneRequest zoneRequest = testUtil.getZoneRequest();
    BaseResponse<List<CarrierServiceResponse>> response = new BaseResponse<>();
    response.setPayload(new ArrayList<>());

    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(response);

    Exception exception =
        assertThrows(CommonServiceException.class, () -> zoneService.addZoneData(zoneRequest));

    assertNotNull(exception);
    assertEquals(
        "Zone data cannot be created with given carrierServiceId and orgId",
        exception.getMessage());
  }

  @Test
  void addZoneDataInvalidGeozonesDetailsTest()
      throws PromiseEngineException, CommonServiceException {
    ZoneRequest zoneRequest = testUtil.getZoneRequest();

    BaseResponse<List<PostalCodeResponse>> response = new BaseResponse<>();
    response.setPayload(null);
    response.setSuccess(true);

    when(postalCodeFeign.getByPostalCodePrefix(any(), any())).thenReturn(response);
    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(testUtil.getCarrierServiceUpdateResponse());

    Exception exception =
        assertThrows(CommonServiceException.class, () -> zoneService.addZoneData(zoneRequest));

    assertNotNull(exception);
    assertEquals("Zone data cannot be created with given geo zone", exception.getMessage());
  }

  @Test
  void updateZoneTest() throws PromiseEngineException, CommonServiceException {
    ZoneUpdateRequest zoneUpdateRequest = testUtil.getZoneUpdationRequest();

    when(zonePersistenceService.fetchZoneDetails(any(), any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getZoneDomainDto()));

    when(zonePersistenceService.saveZone(any(ZoneDomainDto.class)))
        .thenReturn(testUtil.getZoneDomainDto());

    ZoneResponse response =
        zoneService.updateZone(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID,
            zoneUpdateRequest);

    assertNotNull(response);
    assertEquals(zoneUpdateRequest.getZone(), response.getZone());
    verify(zonePersistenceService, times(1)).fetchZoneDetails(any(), any(), any(), any());
    verify(zonePersistenceService, times(1)).saveZone(any(ZoneDomainDto.class));
  }

  @Test
  void updateZoneForInvalidZoneDetailsTest() throws PromiseEngineException, CommonServiceException {
    ZoneUpdateRequest zoneUpdateRequest = testUtil.getZoneUpdationRequest();

    when(zonePersistenceService.fetchZoneDetails(any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                zoneService.updateZone(
                    TestUtil.ORG_ID,
                    TestUtil.SOURCE_GEOZONE,
                    TestUtil.DESTINATION_GEOZONE,
                    TestUtil.CARRIER_SERVICE_ID,
                    zoneUpdateRequest));

    assertNotNull(exception);
    assertEquals("Zone data not found", exception.getMessage());
    verify(zonePersistenceService, times(1)).fetchZoneDetails(any(), any(), any(), any());
  }

  @Test
  void getZoneDetailsTest() throws PromiseEngineException, CommonServiceException {
    ZoneDomainDto zoneDomainDto = testUtil.getZoneDomainDto();

    when(zonePersistenceService.fetchZoneDetails(any(), any(), any(), any()))
        .thenReturn(Optional.of(zoneDomainDto));

    ZoneResponse response =
        zoneService.getZoneDetails(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID);

    assertNotNull(response);
    assertEquals(zoneDomainDto.getZone(), response.getZone());
    assertEquals(zoneDomainDto.getCarrierServiceId(), response.getCarrierServiceId());
    assertEquals(zoneDomainDto.getCustomAttributes(), response.getCustomAttributes());
    verify(zonePersistenceService, times(1)).fetchZoneDetails(any(), any(), any(), any());
  }

  @Test
  void getZoneDetailsForInvalidZoneDetailsTest()
      throws PromiseEngineException, CommonServiceException {
    when(zonePersistenceService.fetchZoneDetails(any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                zoneService.getZoneDetails(
                    TestUtil.ORG_ID,
                    TestUtil.SOURCE_GEOZONE,
                    TestUtil.DESTINATION_GEOZONE,
                    TestUtil.CARRIER_SERVICE_ID));

    assertNotNull(exception);
    assertEquals("Zone data not found", exception.getMessage());
    verify(zonePersistenceService, times(1)).fetchZoneDetails(any(), any(), any(), any());
  }

  @Test
  void deleteZoneDetailsTest() throws PromiseEngineException, CommonServiceException {
    ZoneDomainDto zoneDomainDto = testUtil.getZoneDomainDto();

    when(zonePersistenceService.fetchZoneDetails(any(), any(), any(), any()))
        .thenReturn(Optional.of(zoneDomainDto));

    when(zonePersistenceService.deleteZoneDetails(any(ZoneDomainDto.class)))
        .thenReturn(zoneDomainDto);

    ZoneResponse response =
        zoneService.deleteZoneDetails(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID);

    assertNotNull(response);
    assertEquals(zoneDomainDto.getZone(), response.getZone());
    assertEquals(zoneDomainDto.getCarrierServiceId(), response.getCarrierServiceId());
    assertEquals(zoneDomainDto.getCustomAttributes(), response.getCustomAttributes());
    verify(zonePersistenceService, times(1)).deleteZoneDetails(any(ZoneDomainDto.class));
  }

  @Test
  void deleteZoneDetailsForInvalidZOneDetailsTest()
      throws PromiseEngineException, CommonServiceException {
    when(zonePersistenceService.fetchZoneDetails(any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                zoneService.deleteZoneDetails(
                    TestUtil.ORG_ID,
                    TestUtil.SOURCE_GEOZONE,
                    TestUtil.DESTINATION_GEOZONE,
                    TestUtil.CARRIER_SERVICE_ID));

    assertNotNull(exception);
    assertEquals("Zone data not found", exception.getMessage());
    verify(zonePersistenceService, times(1)).fetchZoneDetails(any(), any(), any(), any());
  }

  @Test
  void getZoneDetailsListTest() throws PromiseEngineException, CommonServiceException {
    List<ZoneDomainDto> zoneEntityList = testUtil.getZoneDomainDtoList();

    when(zonePersistenceService.fetchZoneByOrgIdAndDestGeozone(any(), any()))
        .thenReturn(zoneEntityList);

    List<ZoneResponse> response =
        zoneService.getZoneDetailsList(TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE);

    assertNotNull(response);
    assertEquals(zoneEntityList.size(), response.size());
    assertEquals(
        zoneEntityList.get(0).getCarrierServiceId(), response.get(0).getCarrierServiceId());
    assertEquals(
        zoneEntityList.get(0).getCustomAttributes(), response.get(0).getCustomAttributes());
    verify(zonePersistenceService, times(1)).fetchZoneByOrgIdAndDestGeozone(any(), any());
  }

  @Test
  void getZoneDetailsEmptyListTest() throws PromiseEngineException, CommonServiceException {
    when(zonePersistenceService.fetchZoneByOrgIdAndDestGeozone(any(), any()))
        .thenReturn(new ArrayList<>());

    Exception exception =
        assertThrows(
            CommonServiceException.class,
            () -> zoneService.getZoneDetailsList(TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE));

    assertNotNull(exception);
    assertEquals("Zone data not found", exception.getMessage());
    verify(zonePersistenceService, times(1)).fetchZoneByOrgIdAndDestGeozone(any(), any());
  }
}
