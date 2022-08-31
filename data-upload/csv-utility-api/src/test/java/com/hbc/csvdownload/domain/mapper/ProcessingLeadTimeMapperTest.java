package com.hbc.csvdownload.domain.mapper;

import com.hbc.csvdownload.common.TestUtil;
import com.hbc.node.carrier.domain.inbound.NodeCarrierRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProcessingLeadTimeMapperTest {

  @InjectMocks private TestUtil testUtil;
  ProcessingLeadTimeMapper mapper = Mappers.getMapper(ProcessingLeadTimeMapper.class);

  @Test
  void convertToNodeCarrierRequest() {

    NodeCarrierRequest nodeCarrierRequest =
        mapper.convertToNodeCarrierRequest(testUtil.getProcessingLeadTimesRaw());
    NodeCarrierRequest nullNodeCarrierRequest = mapper.convertToNodeCarrierRequest(null);
    Assertions.assertNull(nullNodeCarrierRequest);
    Assertions.assertNotNull(nodeCarrierRequest);
    Assertions.assertEquals(TestUtil.ORG_ID, nodeCarrierRequest.getOrgId());
  }
}
