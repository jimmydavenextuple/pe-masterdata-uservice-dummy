package com.nextuple.csvdownload.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.csvdownload.common.TestUtil;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.node.domain.feign.NodeFeign;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

  @InjectMocks private NodeService nodeService;

  @Mock private NodeFeign nodeFeign;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(nodeService, "pageSize", 2);
  }

  @Test
  void getNodeListTest() {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodePage());

    List<NodeDto> responseList = nodeService.getNodeList(TestUtil.ORG_ID);

    assertEquals(4, responseList.size());
    assertEquals(TestUtil.ORG_ID, responseList.get(0).getOrgId());
    assertEquals(TestUtil.NODE_ID, responseList.get(0).getNodeId());
    verify(nodeFeign, times(2)).getNodeList(any(), any(), any(), any(), any());
  }
}
