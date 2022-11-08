package com.nextuple.csvdownload.domain.mapper;

import com.nextuple.csvdownload.common.TestUtil;
import com.nextuple.csvdownload.domain.pojo.TransitDataErrorLogsPojo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransitDataRequestMapperTest {
  @InjectMocks private TestUtil testUtil;
  TransitDataRequestMapper mapper = Mappers.getMapper(TransitDataRequestMapper.class);

  @Test
  void convertToTransitDataErrorLogsPojo() {

    TransitDataErrorLogsPojo transitDataErrorLogsPojo =
        mapper.convertToTransitDataErrorLogsPojo(testUtil.getAddTransitDataRequest());
    TransitDataErrorLogsPojo nullTransitDataErrorLogsPojo =
        mapper.convertToTransitDataErrorLogsPojo(null);
    Assertions.assertNull(nullTransitDataErrorLogsPojo);
    Assertions.assertNotNull(transitDataErrorLogsPojo);
    Assertions.assertEquals(TestUtil.ORG_ID, transitDataErrorLogsPojo.getOrgId());
  }
}
