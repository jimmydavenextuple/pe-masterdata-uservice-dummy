package com.nextuple.jobs.consumers.domain.mapper;

import com.nextuple.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.nextuple.jobs.consumers.common.TestUtil;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierRequest;
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
        nodeCarrierRequestMapper.convertToNodeCarrierRequest((ProcessingLeadTimesRaw) null);
    Assertions.assertNull(nullNodeCarrierRequest);
  }
}
