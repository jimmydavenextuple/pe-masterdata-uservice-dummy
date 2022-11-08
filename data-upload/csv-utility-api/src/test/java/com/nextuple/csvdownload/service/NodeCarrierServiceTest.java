package com.nextuple.csvdownload.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.csvdownload.common.TestUtil;
import com.nextuple.node.carrier.domain.feign.NodeCarrierFeign;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeCarrierServiceTest {

  @InjectMocks private NodeCarrierService nodeCarrierService;

  @Mock private NodeCarrierFeign nodeCarrierFeign;

  @InjectMocks private TestUtil testUtil;

  @Test
  void getNodeCarrierResponseTest() {
    when(nodeCarrierFeign.getNodeCarrierListWithLastPickUpTimeDetails(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrier());

    List<NodeCarrierResponse> response =
        nodeCarrierService.getNodeCarrierResponse(TestUtil.NODE_ID, TestUtil.ORG_ID);

    assertEquals(2, response.size());
    assertEquals(TestUtil.ORG_ID, response.get(0).getOrgId());
    assertEquals(TestUtil.NODE_ID, response.get(0).getNodeId());
    verify(nodeCarrierFeign, times(1)).getNodeCarrierListWithLastPickUpTimeDetails(any(), any());
  }
}
