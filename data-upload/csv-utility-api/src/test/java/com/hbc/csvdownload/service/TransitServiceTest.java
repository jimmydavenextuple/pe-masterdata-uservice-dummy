package com.hbc.csvdownload.service;

import static org.mockito.Mockito.*;

import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.common.TestUtil;
import com.hbc.csvdownload.exception.TransitServiceException;
import com.hbc.transit.domain.dto.TransitTimeEntriesDto;
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
  void gTransitTimeEntriesDto() throws TransitServiceException {
    when(transitFeign.getTransitTimeEntries(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getTransitTimeEntriesDto()).build());
    TransitTimeEntriesDto transitResponses =
        transitService.getTransitTimeEntries(TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);
    Assertions.assertNotNull(transitResponses);
    verify(transitFeign, times(1)).getTransitTimeEntries(any(), any());
  }

  @Test
  void getTransitDetailsForCarrierServiceIdTestException() {
    when(transitFeign.getTransitTimeEntries(anyString(), anyString()))
        .thenThrow(new RuntimeException());
    Assertions.assertThrows(
        TransitServiceException.class,
        () -> transitService.getTransitTimeEntries(TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));
    verify(transitFeign, times(1)).getTransitTimeEntries(any(), any());
  }
}
