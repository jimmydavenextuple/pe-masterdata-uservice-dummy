package com.hbc.dataupload.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.base.PagePayload;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.common.outbound.NodeCarrierServiceAndServiceOptionResponse;
import com.hbc.dataupload.domain.pojo.NodeCarrierServicePageProperties;
import com.hbc.dataupload.service.NodeCarrierServiceAndServiceOptionService;
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
class NodeCarrierServiceAndServiceOptionControllerTest {

  @Mock private NodeCarrierServicePageProperties pageProperties;
  @Mock private DefaultPageProperties defaultPageProperties;
  @Mock private NodeCarrierServiceAndServiceOptionService nodeCarrierServiceAndServiceOptionService;

  @InjectMocks
  private NodeCarrierServiceAndServiceOptionController nodeCarrierServiceAndServiceOptionController;

  @InjectMocks private TestUtil testUtil;

  @Test
  void getListOfNodeCarrierServiceAndServiceOptionDetails() {
    when(nodeCarrierServiceAndServiceOptionService
            .getListOfNodeCarrierServiceAndServiceOptionDetails(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeCarrierServiceAndServiceOptionResponse(1));
    when(pageProperties.getSortOrder()).thenReturn("ASC");
    when(pageProperties.getSortBy()).thenReturn("nodeId");
    when(defaultPageProperties.getPageSize()).thenReturn(15);
    when(defaultPageProperties.getPageNo()).thenReturn(1);

    ResponseEntity<BaseResponse<PagePayload<NodeCarrierServiceAndServiceOptionResponse>>> response =
        nodeCarrierServiceAndServiceOptionController
            .getListOfNodeCarrierServiceAndServiceOptionDetails(
                TestUtil.ORG_ID,
                testUtil.getPageParams(
                    Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response);
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getPayload());
    Assertions.assertFalse(CollectionUtils.isEmpty(response.getBody().getPayload().getData()));
    verify(nodeCarrierServiceAndServiceOptionService, times(1))
        .getListOfNodeCarrierServiceAndServiceOptionDetails(any(), any(), any(), any(), any());
  }
}
