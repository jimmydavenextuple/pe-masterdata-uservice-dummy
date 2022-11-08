package com.nextuple.fsa.node.utils;

import com.nextuple.common.base.PagePayload;
import com.nextuple.fsa.node.pojo.FSACoverage;
import com.nextuple.fsa.node.pojo.Node;
import com.nextuple.fsa.node.pojo.NodeFSAMessage;
import com.nextuple.fsa.node.pojo.NodeFSASyncRequest;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.domain.dto.NodeCacheKeyDto;
import com.nextuple.node.domain.outbound.NodeResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestUtils {

  public static final String NODE = "node-";
  public static final String ORG_ID = "BAY";
  public static final String POSTAL_CODE = "M1R T2P";
  public static final String SERVICE_OPTION_1 = "SDND";
  public static final String SERVICE_OPTION_2 = "STANDARD";
  public static final String NODE_ID = "node-1";
  public static final String CARRIER_SERVICE_ID = "ALL-SDND";
  public static final String FSA_MESSAGE_NAME = "NodeFSAMappingEvent_v1";
  public static final String EARLIEST_CUTOFF = "17:00";
  public static final String LAST_PICKUP_TIME = "18:00";
  public static final Double PROCESSING_TIME = 1.0d;
  public static final List<String> FSA_LIST = List.of("M1R", "M4R", "T2P");

  public List<NodeCacheKeyDto> getNodeCacheKeyDtos(int count) {
    List<NodeCacheKeyDto> dtos = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      NodeCacheKeyDto dto = new NodeCacheKeyDto(NODE + i, ORG_ID);
      dtos.add(dto);
    }
    return dtos;
  }

  public NodeResponse getNodeResponse() {
    NodeResponse nodeResponse = new NodeResponse();
    nodeResponse.setOrgId(ORG_ID);
    nodeResponse.setNodeId(NODE_ID);
    nodeResponse.setPostalCode(POSTAL_CODE);
    Map<String, Boolean> serviceOptionEligibility = new HashMap<>();
    serviceOptionEligibility.put(SERVICE_OPTION_1.toLowerCase() + "Eligible", Boolean.TRUE);
    serviceOptionEligibility.put(SERVICE_OPTION_2.toLowerCase() + "Eligible", Boolean.FALSE);

    nodeResponse.setServiceOptionEligibilities(serviceOptionEligibility);
    return nodeResponse;
  }

  public NodeFSAMessage getNodeFSAMessage() {
    NodeFSAMessage message = new NodeFSAMessage();
    message.setEventName(FSA_MESSAGE_NAME);
    message.setOrgId(ORG_ID);
    message.setNodeId(NODE_ID);
    message.setType("store");
    Map<String, FSACoverage> fsaCoverageMap = new HashMap<>();
    FSACoverage coverage = new FSACoverage();
    coverage.setFsas(FSA_LIST);
    coverage.setEarliestCutoff(EARLIEST_CUTOFF);
    fsaCoverageMap.put(SERVICE_OPTION_1, coverage);
    message.setFsaCoverage(fsaCoverageMap);
    return message;
  }

  public List<NodeCarrierResponse> getNodeCarrierResponses(int i) {
    List<NodeCarrierResponse> nodeCarrierResponses = new ArrayList<>();
    for (int j = 0; j < i; j++) {
      NodeCarrierResponse response = new NodeCarrierResponse();
      response.setNodeId(NODE + j);
      response.setOrgId(ORG_ID);
      response.setCarrierServiceId(CARRIER_SERVICE_ID);
      response.setServiceOption(SERVICE_OPTION_1);
      response.setLastPickupTime(LAST_PICKUP_TIME);
      response.setProcessingTime(PROCESSING_TIME);
      nodeCarrierResponses.add(response);
    }
    return nodeCarrierResponses;
  }

  public PagePayload<NodeResponse> getNodeDetailsPageResponse() {

    PagePayload pagePayload = new PagePayload();
    PagePayload.Pagination pagination = new PagePayload.Pagination();
    pagination.setTotalPages(1);
    pagination.setCurrentPage(1);
    pagePayload.setPagination(pagination);

    pagePayload.setData(List.of(getNodeResponse()));
    return pagePayload;
  }

  public NodeFSASyncRequest getNodeFSASyncRequest() {
    NodeFSASyncRequest request = new NodeFSASyncRequest();
    List<Node> nodes = List.of(new Node(ORG_ID, NODE_ID), new Node(ORG_ID, NODE_ID));
    request.setNodes(nodes);
    return request;
  }
}
