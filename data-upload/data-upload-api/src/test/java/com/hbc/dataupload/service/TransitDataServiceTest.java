package com.hbc.dataupload.service;

import static org.mockito.Mockito.*;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.util.TestUtil;
import com.hbc.transit.domain.feign.TransitFeign;
import com.hbc.transit.domain.inbound.DistinctGeozonesResponse;
import feign.FeignException;
import feign.Request;
import feign.Request.HttpMethod;
import java.util.Collections;
import java.util.HashMap;
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
    when(transitFeign.getDistinctSourceAndDestinationGeozones(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID))
        .thenThrow(
            new FeignException.BadRequest(
                "Error when fetching distinct geozones list",
                Request.create(HttpMethod.GET, "", new HashMap<>(), null, null, null),
                "Error when fetching distinct geozones list".getBytes()));

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitDataService.getDistinctGeozonesList(
                    TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));

    Assertions.assertNotNull(exception);
  }
}
