package com.nextuple.fsa.node.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.fsa.node.pojo.Node;
import com.nextuple.fsa.node.pojo.NodeFSASyncRequest;
import com.nextuple.fsa.node.producer.NodeFSAMessagePublisher;
import com.nextuple.fsa.node.utils.TestUtils;
import com.nextuple.node.carrier.domain.feign.NodeCarrierFeign;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.domain.feign.NodeFeign;
import com.nextuple.transit.domain.feign.TransitFeign;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class NodeFSASyncServiceImplTest {

  @InjectMocks NodeFSASyncServiceImpl nodeFSASyncService;
  @InjectMocks TestUtils testUtils;
  @Mock NodeFeign nodeFeign;
  @Mock NodeCarrierFeign nodeCarrierFeign;
  @Mock TransitFeign transitFeign;
  @Mock NodeFSAMessagePublisher publisher;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(nodeFSASyncService, "fsaUpdatesActive", Boolean.TRUE);
    ReflectionTestUtils.setField(nodeFSASyncService, "serviceOptions", Set.of("SDND"));
    ReflectionTestUtils.setField(nodeFSASyncService, "nodeFSATopic", "exampleTopic");
  }

  @Test
  @DisplayName("FSA updates inactive")
  void sendNodeFSAMappingTest1() {
    ReflectionTestUtils.setField(nodeFSASyncService, "fsaUpdatesActive", Boolean.FALSE);
    nodeFSASyncService.sendNodeFSAMapping(new NodeFSASyncRequest());
    verify(publisher, times(0)).publish(any(), any());
  }

  @Test
  @DisplayName("FSA updates for all nodes")
  void sendNodeFSAMappingTest2() {
    PagePayload pagePayload = testUtils.getNodeDetailsPageResponse();
    pagePayload.getPagination().setTotalPages(pagePayload.getPagination().getTotalPages() + 1);
    PagePayload pagePayload2 = testUtils.getNodeDetailsPageResponse();
    pagePayload2.getPagination().setCurrentPage(pagePayload2.getPagination().getTotalPages() + 1);
    when(nodeFeign.getAllNodesList(any(), any(), any(), any()))
        .thenReturn(
            BaseResponse.builder().payload(pagePayload).build(),
            BaseResponse.builder().payload(pagePayload2).build());
    List<NodeCarrierResponse> carrierResponses = testUtils.getNodeCarrierResponses(3);
    carrierResponses.get(2).setCarrierServiceId("");
    when(nodeCarrierFeign.getNodeCarrierListForServiceOption(any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(carrierResponses).build());
    when(transitFeign.getDistinctDestinationGeoZones(any(), any(), anyList()))
        .thenReturn(BaseResponse.builder().payload(TestUtils.FSA_LIST).build());

    nodeFSASyncService.sendNodeFSAMapping(new NodeFSASyncRequest());
    verify(publisher, times(1)).publish(any(), any());
    verify(nodeFeign, times(2)).getAllNodesList(any(), any(), any(), any());
    verify(nodeCarrierFeign, times(1)).getNodeCarrierListForServiceOption(any(), any(), any());
    verify(transitFeign, times(1)).getDistinctDestinationGeoZones(any(), any(), anyList());
    verify(publisher, times(1)).publish(any(), any());
  }

  @Test
  @DisplayName("FSA updates for list of nodes")
  void sendNodeFSAMappingTest3() {
    when(nodeFeign.getNodeDetails(any(), any()))
        .thenReturn(BaseResponse.builder().payload(testUtils.getNodeResponse()).build());
    List<NodeCarrierResponse> carrierResponses = testUtils.getNodeCarrierResponses(3);
    carrierResponses.get(2).setCarrierServiceId("");
    when(nodeCarrierFeign.getNodeCarrierListForServiceOption(any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(carrierResponses).build());
    when(transitFeign.getDistinctDestinationGeoZones(any(), any(), anyList()))
        .thenReturn(BaseResponse.builder().payload(TestUtils.FSA_LIST).build());

    nodeFSASyncService.sendNodeFSAMapping(
        new NodeFSASyncRequest(List.of(new Node("BAY", "Node-1"))));
    verify(nodeFeign, times(1)).getNodeDetails(any(), any());
    verify(nodeCarrierFeign, times(1)).getNodeCarrierListForServiceOption(any(), any(), any());
    verify(transitFeign, times(1)).getDistinctDestinationGeoZones(any(), any(), anyList());
    verify(publisher, times(1)).publish(any(), any());
  }
}
