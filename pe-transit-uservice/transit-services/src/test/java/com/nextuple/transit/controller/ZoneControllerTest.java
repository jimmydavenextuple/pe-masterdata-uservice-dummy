/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.transit.TestUtil;
import com.nextuple.transit.domain.outbound.ZoneResponse;
import com.nextuple.transit.service.ZoneService;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ZoneControllerTest {

  @InjectMocks ZoneController zoneController;
  @Mock ZoneService zoneService;
  @InjectMocks TestUtil testUtil;

  @Test
  void addZoneDataTest() throws PromiseEngineException, CommonServiceException {
    ZoneResponse zoneResponse = testUtil.getZoneResponse();
    when(zoneService.addZoneData(any())).thenReturn(zoneResponse);

    ResponseEntity<BaseResponse<ZoneResponse>> response =
        zoneController.addZoneData(testUtil.getZoneRequest());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(zoneResponse, response.getBody().getPayload());
    verify(zoneService, times(1)).addZoneData(any());
  }

  @Test
  void updateZoneTest() throws PromiseEngineException, CommonServiceException {
    ZoneResponse zoneResponse = testUtil.getZoneResponse();
    when(zoneService.updateZone(any(), any(), any(), any(), any())).thenReturn(zoneResponse);

    ResponseEntity<BaseResponse<ZoneResponse>> response =
        zoneController.updateZone(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID,
            testUtil.getZoneUpdationRequest());

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(zoneResponse, response.getBody().getPayload());
    verify(zoneService, times(1)).updateZone(any(), any(), any(), any(), any());
  }

  @Test
  void getZoneDetailsTest() throws PromiseEngineException, CommonServiceException {
    ZoneResponse zoneResponse = testUtil.getZoneResponse();
    when(zoneService.getZoneDetails(any(), any(), any(), any())).thenReturn(zoneResponse);

    ResponseEntity<BaseResponse<ZoneResponse>> response =
        zoneController.getZoneDetails(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(zoneResponse, response.getBody().getPayload());
    verify(zoneService, times(1)).getZoneDetails(any(), any(), any(), any());
  }

  @Test
  void deleteZoneDetailsTest() throws PromiseEngineException, CommonServiceException {
    ZoneResponse zoneResponse = testUtil.getZoneResponse();
    when(zoneService.deleteZoneDetails(any(), any(), any(), any())).thenReturn(zoneResponse);

    ResponseEntity<BaseResponse<ZoneResponse>> response =
        zoneController.deleteZoneDetails(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(zoneResponse, response.getBody().getPayload());
    verify(zoneService, times(1)).deleteZoneDetails(any(), any(), any(), any());
  }

  @Test
  void getZoneDetailsListTest() throws PromiseEngineException, CommonServiceException {
    ZoneResponse zoneResponse = testUtil.getZoneResponse();
    when(zoneService.getZoneDetailsList(any(), any())).thenReturn(List.of(zoneResponse));

    ResponseEntity<BaseResponse<List<ZoneResponse>>> response =
        zoneController.getZoneDetailsList(TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(zoneResponse, response.getBody().getPayload().get(0));
    verify(zoneService, times(1)).getZoneDetailsList(any(), any());
  }
}
