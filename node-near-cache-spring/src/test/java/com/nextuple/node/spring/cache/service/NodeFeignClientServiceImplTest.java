package com.nextuple.node.spring.cache.service;

import com.nextuple.controltower.common.base.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.node.outbound.NodeValidationResponse;
import com.nextuple.nodde.spring.cache.feign.NodeFeignImpl;
import com.nextuple.nodde.spring.cache.service.NodeFeignClientServiceImpl;
import com.nextuple.node.cache.domain.NodeCacheKey;
import com.nextuple.node.cache.domain.NodeCacheValue;
import com.nextuple.node.cache.domain.NodeValidationRequest;
import com.nextuple.node.spring.cache.util.TestUtil;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeFeignClientServiceImplTest {

  @InjectMocks private NodeFeignClientServiceImpl nodeFeignClientService;

  @InjectMocks private TestUtil testUtil;

  @Mock
  private GenericMapper<
          NodeCacheKey, NodeCacheValue, NodeValidationRequest, BaseResponse<NodeValidationResponse>>
      mapper;

  @Mock private NodeFeignImpl nodeFeign;
}
