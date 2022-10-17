package com.hbc.dataupload.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.base.PagePayload;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.common.outbound.TransitBufferDetailsResponse;
import com.hbc.dataupload.domain.pojo.TransitTimeBufferPageProperties;
import com.hbc.dataupload.service.TransitBufferService;
import com.hbc.dataupload.util.TestUtil;
import com.hbc.jobs.framework.common.domain.pojo.DefaultPageProperties;
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
class TransitBufferControllerTest {
  @Mock private TransitTimeBufferPageProperties transitTimeBufferPageProperties;
  @Mock private DefaultPageProperties defaultPageProperties;
  @Mock private TransitBufferService transitBufferService;
  @InjectMocks private TransitBufferController transitBufferController;
  @InjectMocks private TestUtil testUtil;

  @Test
  void getTransitTimeBufferDetails() {
    when(transitBufferService.getTransitTimeBufferDetails(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getTransitBufferDetailsResponsePagePayload(1));
    when(transitTimeBufferPageProperties.getSortOrder()).thenReturn("DESC");
    when(transitTimeBufferPageProperties.getSortBy()).thenReturn("carrierServiceId");
    when(defaultPageProperties.getPageSize()).thenReturn(15);
    when(defaultPageProperties.getPageNo()).thenReturn(1);
    ResponseEntity<BaseResponse<PagePayload<TransitBufferDetailsResponse>>> response =
        transitBufferController.getTransitTimeBufferDetails(
            TestUtil.ORG_ID,
            testUtil.getPageParams(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response);
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getPayload());
    Assertions.assertFalse(CollectionUtils.isEmpty(response.getBody().getPayload().getData()));
    verify(transitBufferService, times(1))
        .getTransitTimeBufferDetails(any(), any(), any(), any(), any());
  }
}
