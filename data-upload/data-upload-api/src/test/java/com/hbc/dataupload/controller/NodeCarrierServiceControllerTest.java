package com.hbc.dataupload.controller;

import static org.mockito.Mockito.*;

import com.hbc.common.base.PagePayload;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.domain.dto.NodeCarrierServiceResponse;
import com.hbc.dataupload.domain.pojo.NodeCarrierServicePageProperties;
import com.hbc.dataupload.service.NodeCarrierServiceDetailsService;
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
class NodeCarrierServiceControllerTest {

  @Mock private NodeCarrierServiceDetailsService nodeCarrierServiceDetailsService;
  @Mock private NodeCarrierServicePageProperties pageProperties;
  @Mock private DefaultPageProperties defaultPageProperties;
  @InjectMocks private TestUtil testUtil;
  @InjectMocks private NodeCarrierServiceController nodeCarrierServiceController;

  @Test
  void getNodeServiceOptionDetails() {
    when(pageProperties.getSortBy()).thenReturn("nodeId");
    when(pageProperties.getSortOrder()).thenReturn("ASC");
    when(defaultPageProperties.getPageSize()).thenReturn(15);
    when(defaultPageProperties.getPageNo()).thenReturn(1);
    when(nodeCarrierServiceDetailsService.getNodeCarrierServiceDetails(
            any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeCarrierServicePagePayload(1));

    ResponseEntity<BaseResponse<PagePayload<NodeCarrierServiceResponse>>> response =
        nodeCarrierServiceController.getNodeServiceOptionDetails(
            TestUtil.ORG_ID,
            testUtil.getPageParams(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getPayload());
    Assertions.assertFalse(CollectionUtils.isEmpty(response.getBody().getPayload().getData()));
    verify(nodeCarrierServiceDetailsService, times(1))
        .getNodeCarrierServiceDetails(any(), any(), any(), any(), any());
  }
}
