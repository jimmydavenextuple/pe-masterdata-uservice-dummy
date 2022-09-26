package com.hbc.jobs.consumers.domain.mapper;

import com.hbc.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.hbc.jobs.consumers.common.TestUtil;
import com.hbc.node.carrier.domain.inbound.NodeCarrierRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeCarrierRequestMapperTest {

  @InjectMocks private NodeCarrierRequestMapperImpl nodeCarrierRequestMapper;
  @InjectMocks private TestUtil testUtil;

  @Test
  void convertToNodeCarrierRequest() {
    ProcessingLeadTimesRaw processingLeadTime = testUtil.getProcessingLeadTime("U");
    NodeCarrierRequest nodeCarrierRequest =
        nodeCarrierRequestMapper.convertToNodeCarrierRequest(processingLeadTime);
    Assertions.assertNotNull(nodeCarrierRequest);
    NodeCarrierRequest nullNodeCarrierRequest =
        nodeCarrierRequestMapper.convertToNodeCarrierRequest(null);
    Assertions.assertNull(nullNodeCarrierRequest);
  }
}
