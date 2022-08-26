package com.hbc.jobs.consumers.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;

import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeignClientMapperFactoryTest {

  @Mock private TransitMapper transitMapper;

  @Mock private NodeCarrierMapper nodeCarrierMapper;

  @InjectMocks private FeignClientMapperFactory feignClientMapperFactory;

  @Test
  void getMapper() {
    doNothing().when(transitMapper).setJobTypeEnum(any());
    doNothing().when(nodeCarrierMapper).setJobTypeEnum(any());

    TransitMapper transitMapper1 =
        (TransitMapper) feignClientMapperFactory.getMapper(JobTypeEnum.UPLOAD_TRANSIT_TIMES);
    NodeCarrierMapper nodeCarrierMapper1 =
        (NodeCarrierMapper)
            feignClientMapperFactory.getMapper(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);

    Assertions.assertNotNull(nodeCarrierMapper1);
    Assertions.assertNotNull(transitMapper1);
    Assertions.assertEquals(TransitMapper.class, transitMapper1.getClass());
    Assertions.assertEquals(NodeCarrierMapper.class, nodeCarrierMapper1.getClass());
  }
}
