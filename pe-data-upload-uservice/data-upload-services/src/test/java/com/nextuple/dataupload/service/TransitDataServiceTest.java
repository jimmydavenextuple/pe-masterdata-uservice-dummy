/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.transit.domain.feign.TransitFeign;
import com.nextuple.transit.domain.inbound.DistinctGeozonesResponse;
import feign.FeignException;
import feign.Request;
import feign.Request.HttpMethod;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;

@ExtendWith(MockitoExtension.class)
class TransitDataServiceTest {

  @Mock TransitFeign transitFeign;
  @InjectMocks TransitDataService transitDataService;
  @InjectMocks TestUtil testUtil;

  @Test
  void getDistinctGeozonesList() throws CommonServiceException {
    when(transitFeign.getDistinctSourceAndDestinationGeozones(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID))
        .thenReturn(BaseResponse.builder().payload(testUtil.geozonesResponse()).build());

    DistinctGeozonesResponse geozonesResponse =
        transitDataService.getDistinctGeozonesList(TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);

    Assertions.assertNotNull(geozonesResponse);
    Assertions.assertFalse(CollectionUtils.isEmpty(geozonesResponse.getDestinationGeozones()));
    Assertions.assertFalse(CollectionUtils.isEmpty(geozonesResponse.getSourceGeozones()));
  }

  @Test
  void getDistinctGeozonesListEmptyGeoZonesList() {
    DistinctGeozonesResponse distinctGeozonesResponse1 = testUtil.geozonesResponse();
    distinctGeozonesResponse1.setSourceGeozones(Collections.emptyList());
    when(transitFeign.getDistinctSourceAndDestinationGeozones(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID))
        .thenReturn(BaseResponse.builder().payload(distinctGeozonesResponse1).build());

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitDataService.getDistinctGeozonesList(
                    TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));

    DistinctGeozonesResponse distinctGeozonesResponse2 = testUtil.geozonesResponse();
    distinctGeozonesResponse2.setDestinationGeozones(Collections.emptyList());
    when(transitFeign.getDistinctSourceAndDestinationGeozones(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID))
        .thenReturn(BaseResponse.builder().payload(distinctGeozonesResponse2).build());

    Exception exception1 =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitDataService.getDistinctGeozonesList(
                    TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));

    Assertions.assertNotNull(exception);
    Assertions.assertNotNull(exception1);
  }

  @Test
  void getDistinctGeozonesListException() {
    Map<String, Collection<String>> headers = new HashMap<>();

    when(transitFeign.getDistinctSourceAndDestinationGeozones(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID))
        .thenThrow(
            new FeignException.BadRequest(
                "Error when fetching distinct geozones list",
                Request.create(HttpMethod.GET, "", new HashMap<>(), null, null, null),
                "Error when fetching distinct geozones list".getBytes(),
                headers));

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitDataService.getDistinctGeozonesList(
                    TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));

    Assertions.assertNotNull(exception);
  }
}
