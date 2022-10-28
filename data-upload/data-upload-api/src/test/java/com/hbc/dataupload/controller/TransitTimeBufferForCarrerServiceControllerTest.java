package com.hbc.dataupload.controller;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.base.PagePayload;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.common.outbound.TransitBufferDetailsResponse;
import com.hbc.dataupload.domain.pojo.TransitTimeBufferPageProperties;
import com.hbc.dataupload.service.TransitTimeBufferService;
import com.hbc.dataupload.util.TestUtil;
import com.hbc.jobs.framework.common.domain.pojo.DefaultPageProperties;
import com.hbc.transit.domain.outbound.TransitBufferConfigResponse;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

@ExtendWith(MockitoExtension.class)
class TransitTimeBufferForCarrerServiceControllerTest {

  @Mock private TransitTimeBufferService transitTimeBufferService;
  @Mock private TransitTimeBufferPageProperties transitTimeBufferPageProperties;
  @Mock private DefaultPageProperties defaultPageProperties;

  @InjectMocks
  private TransitTimeBufferForCarrerServiceController transitTimeBufferForCarrerServiceController;

  @InjectMocks private TestUtil testUtil;

  @Test
  void getTransitTimeBufferDetails() {
    when(defaultPageProperties.getPageNo()).thenReturn(15);
    when(defaultPageProperties.getPageSize()).thenReturn(15);
    when(transitTimeBufferPageProperties.getSortOrder()).thenReturn("DESC");
    when(transitTimeBufferPageProperties.getSortBy()).thenReturn("carrierServiceId");
    when(transitTimeBufferService.getTransitTimeBufferDetailsForCarrierServices(
            anyString(), anyInt(), anyInt(), anyString(), anyString()))
        .thenReturn(testUtil.getTransitBufferDetailsResponsePagePayload(1));

    ResponseEntity<BaseResponse<PagePayload<TransitBufferDetailsResponse>>> response =
        transitTimeBufferForCarrerServiceController.getTransitTimeBufferDetailsForCarrierServices(
            TestUtil.ORG_ID,
            testUtil.getPageParams(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getPayload());
    Assertions.assertFalse(CollectionUtils.isEmpty(response.getBody().getPayload().getData()));
    verify(transitTimeBufferService, times(1))
        .getTransitTimeBufferDetailsForCarrierServices(
            anyString(), anyInt(), anyInt(), anyString(), anyString());
  }

  @Test
  void getTransitBufferConfigRequests() {

    when(transitTimeBufferService.getTransitBufferConfigRequests(anyString(), anyString()))
        .thenReturn(
            Collections.singletonList(
                testUtil.getTransitBufferConfigResponse(TestUtil.CARRIER_SERVICE_ID)));

    ResponseEntity<BaseResponse<List<TransitBufferConfigResponse>>> response =
        Assertions.assertDoesNotThrow(
            () ->
                transitTimeBufferForCarrerServiceController.getTransitBufferConfigRequests(
                    TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getPayload());
    Assertions.assertEquals(1, response.getBody().getPayload().size());
  }
}
