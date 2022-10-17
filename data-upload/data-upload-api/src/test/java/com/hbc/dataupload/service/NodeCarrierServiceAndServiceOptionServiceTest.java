package com.hbc.dataupload.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.base.PagePayload;
import com.hbc.dataupload.common.outbound.NodeCarrierServiceAndServiceOptionResponse;
import com.hbc.dataupload.util.TestUtil;
import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.domain.feign.NodeFeign;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;

@ExtendWith(MockitoExtension.class)
class NodeCarrierServiceAndServiceOptionServiceTest {

  @Mock private NodeFeign nodeFeign;
  @Mock private NodeCarrierFeign nodeCarrierFeign;

  @InjectMocks
  private NodeCarrierServiceAndServiceOptionService nodeCarrierServiceAndServiceOptionService;

  @InjectMocks private TestUtil testUtil;

  @Test
  void getListOfNodeCarrierServiceAndServiceOptionDetails() {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());
    when(nodeCarrierFeign.getNodeCarrierListWithLastPickUpTimeDetails(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierListResponse());

    PagePayload<NodeCarrierServiceAndServiceOptionResponse> payload =
        nodeCarrierServiceAndServiceOptionService
            .getListOfNodeCarrierServiceAndServiceOptionDetails(
                TestUtil.ORG_ID, 1, 15, "nodeId", "ASC");

    Assertions.assertNotNull(payload);
    Assertions.assertFalse(CollectionUtils.isEmpty(payload.getData()));
    verify(nodeCarrierFeign, times(2)).getNodeCarrierListWithLastPickUpTimeDetails(any(), any());
    verify(nodeFeign, times(1)).getNodeList(any(), any(), any(), any(), any());
  }
}
