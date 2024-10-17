/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service;

import static org.mockito.Mockito.*;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.common.TestUtil;
import com.nextuple.csvdownload.exception.TransitServiceException;
import com.nextuple.transit.domain.dto.TransitTimeEntriesDto;
import com.nextuple.transit.domain.feign.TransitFeign;
import com.nextuple.transit.domain.outbound.TransitResponse;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;

@ExtendWith(MockitoExtension.class)
class TransitResponseServiceTest {

  @Mock private TransitFeign transitFeign;
  @InjectMocks private TransitResponseService transitResponseService;
  @InjectMocks private TestUtil testUtil;

  @Test
  void getTransitDetails() throws TransitServiceException {
    when(transitFeign.getTransitTimeDetailsForDestinationGeoZonesList(any(), any(), any()))
        .thenReturn(
            BaseResponse.builder().payload(List.of(testUtil.getTransitResponse(1.5F))).build());

    List<TransitResponse> transitResponses =
        transitResponseService.getTransitDetails(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, List.of(TestUtil.DESTINATION_FSA));
    Assertions.assertFalse(CollectionUtils.isEmpty(transitResponses));
    verify(transitFeign, times(1))
        .getTransitTimeDetailsForDestinationGeoZonesList(any(), any(), any());
  }

  @Test
  void getTransitDetailsEmptyPayload() {
    when(transitFeign.getTransitTimeDetailsForDestinationGeoZonesList(any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(Collections.emptyList()).build());

    Exception exception =
        Assertions.assertThrows(
            TransitServiceException.class,
            () ->
                transitResponseService.getTransitDetails(
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    List.of(TestUtil.DESTINATION_FSA)));
    Assertions.assertNotNull(exception);
    verify(transitFeign, times(1))
        .getTransitTimeDetailsForDestinationGeoZonesList(any(), any(), any());
  }

  @Test
  void getTransitDetailsNullResponse() {
    when(transitFeign.getTransitTimeDetailsForDestinationGeoZonesList(any(), any(), any()))
        .thenReturn(null);

    Exception exception =
        Assertions.assertThrows(
            TransitServiceException.class,
            () ->
                transitResponseService.getTransitDetails(
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    List.of(TestUtil.DESTINATION_FSA)));
    Assertions.assertNotNull(exception);
    verify(transitFeign, times(1))
        .getTransitTimeDetailsForDestinationGeoZonesList(any(), any(), any());
  }

  @Test
  void gTransitTimeEntriesDto() throws TransitServiceException {
    when(transitFeign.getTransitTimeEntries(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getTransitTimeEntriesDto()).build());
    TransitTimeEntriesDto transitResponses =
        transitResponseService.getTransitTimeEntries(TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);
    Assertions.assertNotNull(transitResponses);
    verify(transitFeign, times(1)).getTransitTimeEntries(any(), any());
  }

  @Test
  void getTransitDetailsForCarrierServiceIdTestException() {
    when(transitFeign.getTransitTimeEntries(anyString(), anyString()))
        .thenThrow(new RuntimeException());
    Assertions.assertThrows(
        TransitServiceException.class,
        () ->
            transitResponseService.getTransitTimeEntries(
                TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));
    verify(transitFeign, times(1)).getTransitTimeEntries(any(), any());
  }
}
