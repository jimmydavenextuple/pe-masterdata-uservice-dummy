package com.hbc.csvdownload.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.common.TestUtil;
import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
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
  void getNodeCarrierListTest() {
    List<NodeCarrierResponse> nodeCarrierResponseList = testUtil.getNodeCarrierResponseList();

    when(nodeCarrierFeign.getAllNodeCarriersByOrgId(any()))
        .thenReturn(BaseResponse.builder().payload(nodeCarrierResponseList).build());

    List<NodeCarrierResponse> response = nodeCarrierService.getNodeCarrierList(TestUtil.ORG_ID);

    assertNotNull(response);
    assertEquals(nodeCarrierResponseList.size(), response.size());
    verify(nodeCarrierFeign, times(1)).getAllNodeCarriersByOrgId(any());
  }
}
