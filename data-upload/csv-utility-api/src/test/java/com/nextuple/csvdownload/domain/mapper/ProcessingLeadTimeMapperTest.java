package com.nextuple.csvdownload.domain.mapper;

import com.nextuple.csvdownload.common.TestUtil;
import com.nextuple.csvdownload.common.pojo.ProcessingLeadTime;
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

    ProcessingLeadTime processingLeadTime =
        mapper.convertToNodeCarrierRequest(testUtil.getProcessingLeadTimesRaw());
    ProcessingLeadTime nullProcessingLeadTime = mapper.convertToNodeCarrierRequest(null);
    Assertions.assertNull(nullProcessingLeadTime);
    Assertions.assertNotNull(processingLeadTime);
    Assertions.assertEquals(TestUtil.ORG_ID, processingLeadTime.getOrgId());
  }
}
