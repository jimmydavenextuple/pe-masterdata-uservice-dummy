package com.hbc.fsa.node.mapper;

import com.hbc.fsa.node.pojo.Node;
import com.hbc.fsa.node.pojo.NodeDetails;
import com.hbc.fsa.node.utils.TestUtils;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class NodeMapperTest {

  @InjectMocks TestUtils testUtils;

  private static NodeMapper mapper = Mappers.getMapper(NodeMapper.class);

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void convertToNodeTest() {

    List<Node> nodes = mapper.convertToNode(testUtils.getNodeCacheKeyDtos(2));
    Assertions.assertNotNull(nodes);
    Assertions.assertEquals(2, nodes.size());
    Assertions.assertEquals(TestUtils.ORG_ID, nodes.get(0).getOrgId());
    Assertions.assertEquals(TestUtils.NODE + 0, nodes.get(0).getNodeId());
  }

  @Test
  void convertToNodeDetailsTest() {
    NodeDetails nodeDetails = mapper.convertToNodeDetails(testUtils.getNodeResponse());
    Assertions.assertEquals(TestUtils.NODE_ID, nodeDetails.getNodeId());
    Assertions.assertEquals(TestUtils.ORG_ID, nodeDetails.getOrgId());
    Assertions.assertEquals(2, nodeDetails.getServiceOptionEligibilities().keySet().size());
    Assertions.assertTrue(
        nodeDetails
            .getServiceOptionEligibilities()
            .get(TestUtils.SERVICE_OPTION_1.toLowerCase() + "Eligible"));
  }
}
