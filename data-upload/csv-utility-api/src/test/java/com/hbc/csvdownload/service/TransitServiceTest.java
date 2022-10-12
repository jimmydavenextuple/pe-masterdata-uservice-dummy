package com.hbc.csvdownload.service;

import static org.mockito.Mockito.*;

import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.common.TestUtil;
import com.hbc.csvdownload.exception.TransitServiceException;
import com.hbc.transit.domain.feign.TransitFeign;
import com.hbc.transit.domain.outbound.TransitResponse;
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
class TransitServiceTest {

  @Mock private TransitFeign transitFeign;
  @InjectMocks private TransitService transitService;
  @InjectMocks private TestUtil testUtil;

  @Test
  void getTransitDetails() throws TransitServiceException {
    when(transitFeign.getTransitTimeDetailsForDestinationGeoZonesList(any(), any(), any()))
        .thenReturn(
            BaseResponse.builder().payload(List.of(testUtil.getTransitResponse(1.5F))).build());

    List<TransitResponse> transitResponses =
        transitService.getTransitDetails(
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
                transitService.getTransitDetails(
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
                transitService.getTransitDetails(
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    List.of(TestUtil.DESTINATION_FSA)));
    Assertions.assertNotNull(exception);
    verify(transitFeign, times(1))
        .getTransitTimeDetailsForDestinationGeoZonesList(any(), any(), any());
  }

  @Test
  void getTransitDetailsForCarrierServiceIdTest() throws TransitServiceException {
    when(transitFeign.getTransitDetailsForCarrierServiceId(anyString(), anyString()))
        .thenReturn(
            BaseResponse.builder().payload(List.of(testUtil.getTransitResponse(1.5F))).build());
    List<TransitResponse> transitResponses =
        transitService.getTransitDetailsForCarrierServiceId(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);
    Assertions.assertFalse(CollectionUtils.isEmpty(transitResponses));
    verify(transitFeign, times(1)).getTransitDetailsForCarrierServiceId(any(), any());
  }

  @Test
  void getTransitDetailsForCarrierServiceIdTestNullResponse() {
    when(transitFeign.getTransitDetailsForCarrierServiceId(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(null).build());
    Assertions.assertThrows(
        TransitServiceException.class,
        () ->
            transitService.getTransitDetailsForCarrierServiceId(
                TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));
    verify(transitFeign, times(1)).getTransitDetailsForCarrierServiceId(any(), any());
  }

  @Test
  void getTransitDetailsForCarrierServiceIdTestEmptyResponse() {
    when(transitFeign.getTransitDetailsForCarrierServiceId(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(Collections.emptyList()).build());
    Assertions.assertThrows(
        TransitServiceException.class,
        () ->
            transitService.getTransitDetailsForCarrierServiceId(
                TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));
    verify(transitFeign, times(1)).getTransitDetailsForCarrierServiceId(any(), any());
  }
}
