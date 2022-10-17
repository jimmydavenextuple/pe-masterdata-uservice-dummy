package com.hbc.dataupload.service;

import static org.mockito.Mockito.*;

import com.hbc.carrier.domain.feign.CarrierFeign;
import com.hbc.common.base.PagePayload;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.common.outbound.TransitBufferDetailsResponse;
import com.hbc.dataupload.util.TestUtil;
import com.hbc.transit.domain.feign.TransitBufferConfigRequestFeign;
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
class TransitBufferServiceTest {

  @Mock private CarrierFeign carrierFeign;
  @Mock private TransitBufferConfigRequestFeign transitBufferConfigRequestFeign;
  @InjectMocks private TransitBufferService transitBufferService;
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
        transitBufferService.getTransitTimeBufferDetails(
            TestUtil.ORG_ID, 1, 15, "carrierServiceId", "DESC");

    Assertions.assertNotNull(payload);
    Assertions.assertFalse(CollectionUtils.isEmpty(payload.getData()));
    verify(carrierFeign, times(1))
        .getCarrierServiceListWithPagination(any(), any(), any(), any(), any());
    verify(transitBufferConfigRequestFeign, times(2)).getTransitBufferConfigRequests(any(), any());
  }
}
