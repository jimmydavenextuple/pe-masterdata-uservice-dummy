/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static org.mockito.Mockito.*;

import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.base.PagePayload;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.common.outbound.TransitBufferDetailsResponse;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.transit.domain.feign.TransitBufferConfigRequestFeign;
import com.nextuple.transit.domain.outbound.TransitBufferConfigResponse;
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
class TransitTimeBufferServiceTest {

  @Mock private CarrierFeign carrierFeign;
  @Mock private TransitBufferConfigRequestFeign transitBufferConfigRequestFeign;
  @InjectMocks private TransitTimeBufferService transitTimeBufferService;
  @InjectMocks private TestUtil testUtil;

  @Test
  void getTransitTimeBufferDetails() {
    when(carrierFeign.getCarrierServiceListWithPagination(any(), any(), any(), any(), any()))
        .thenReturn(
            BaseResponse.builder()
                .payload(testUtil.getCarrierServiceResponsePagePayload(1))
                .build());

    when(transitBufferConfigRequestFeign.getTransitBufferConfigRequests(any(), any()))
        .thenReturn(
            BaseResponse.builder()
                .payload(
                    List.of(
                        testUtil.getTransitBufferConfigResponse(TestUtil.CARRIER_SERVICE_ID),
                        Collections.emptyList()))
                .build());

    PagePayload<TransitBufferDetailsResponse> payload =
        transitTimeBufferService.getTransitTimeBufferDetailsForCarrierServices(
            TestUtil.ORG_ID, 1, 15, "carrierServiceId", "DESC");

    Assertions.assertNotNull(payload);
    Assertions.assertFalse(CollectionUtils.isEmpty(payload.getData()));
    verify(carrierFeign, times(1))
        .getCarrierServiceListWithPagination(any(), any(), any(), any(), any());
    verify(transitBufferConfigRequestFeign, times(2)).getTransitBufferConfigRequests(any(), any());
  }

  @Test
  void getTransitTimeBufferDetailsEmptyTransitBufferResponse() {
    when(carrierFeign.getCarrierServiceListWithPagination(any(), any(), any(), any(), any()))
        .thenReturn(
            BaseResponse.builder()
                .payload(testUtil.getCarrierServiceResponsePagePayload(1))
                .build());

    when(transitBufferConfigRequestFeign.getTransitBufferConfigRequests(any(), any()))
        .thenReturn(BaseResponse.builder().payload(Collections.emptyList()).build());

    PagePayload<TransitBufferDetailsResponse> payload =
        transitTimeBufferService.getTransitTimeBufferDetailsForCarrierServices(
            TestUtil.ORG_ID, 1, 15, "carrierServiceId", "DESC");

    Assertions.assertNotNull(payload);
    Assertions.assertFalse(CollectionUtils.isEmpty(payload.getData()));
    verify(carrierFeign, times(1))
        .getCarrierServiceListWithPagination(any(), any(), any(), any(), any());
    verify(transitBufferConfigRequestFeign, times(2)).getTransitBufferConfigRequests(any(), any());
  }

  @Test
  void getTransitTimeBufferDetailsNullResponse() {
    PagePayload<CarrierServiceResponse> pagePayload =
        testUtil.getCarrierServiceResponsePagePayload(1);
    pagePayload.setData(null);
    when(carrierFeign.getCarrierServiceListWithPagination(any(), any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(pagePayload).build());

    PagePayload<TransitBufferDetailsResponse> responsePagePayload =
        transitTimeBufferService.getTransitTimeBufferDetailsForCarrierServices(
            TestUtil.ORG_ID, 1, 15, "carrierServiceId", "DESC");

    Assertions.assertNotNull(responsePagePayload);
    Assertions.assertTrue(CollectionUtils.isEmpty(responsePagePayload.getData()));
    verify(carrierFeign, times(1))
        .getCarrierServiceListWithPagination(any(), any(), any(), any(), any());
  }

  @Test
  void getTransitBufferConfigRequestsTest() {
    when(transitBufferConfigRequestFeign.getTransitBufferConfigRequests(anyString(), anyString()))
        .thenReturn(
            testUtil.getBaseResponseTransitBufferConfigResponse(TestUtil.CARRIER_SERVICE_ID));
    List<TransitBufferConfigResponse> responses =
        Assertions.assertDoesNotThrow(
            () ->
                transitTimeBufferService.getTransitBufferConfigRequests(
                    TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));
    Assertions.assertEquals(1, responses.size());
  }
}
