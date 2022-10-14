package com.hbc.csvdownload.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.base.PagePayload;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.common.TestUtil;
import com.hbc.node.domain.dto.NodeDto;
import com.hbc.node.domain.feign.NodeFeign;
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
  public void init() {
    ReflectionTestUtils.setField(nodeService, "pageSize", 2);
  }

  @Test
  void getNodeListTest() {
    BaseResponse<PagePayload<NodeDto>> pagePayloadBaseResponse =
        testUtil.getNodeListPaginationBaseResponse(1);

    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(pagePayloadBaseResponse);

    List<NodeDto> response = nodeService.getNodeList(TestUtil.ORG_ID);

    assertNotNull(response);
    assertEquals(pagePayloadBaseResponse.getPayload().getData().size(), response.size());
    verify(nodeFeign, times(1)).getNodeList(any(), any(), any(), any(), any());
  }
}
