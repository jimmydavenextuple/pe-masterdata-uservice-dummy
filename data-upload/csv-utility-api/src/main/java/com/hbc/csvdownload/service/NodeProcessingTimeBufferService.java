package com.hbc.csvdownload.service;

import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.node.domain.feign.NodeFeign;
import com.hbc.node.domain.outbound.NodeResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodeProcessingTimeBufferService {

  private final NodeCarrierFeign nodeCarrierFeign;
  private final NodeFeign nodeFeign;

  public String getProcessingTimeBuffersForOgId(String orgId) {
    List<NodeResponse> nodeResponseList = nodeFeign.getAllNodesByOrgId(orgId).getPayload();
    List<NodeCarrierResponse> nodeCarrierResponseList =
        nodeCarrierFeign.getAllNodeCarriersByOrgId(orgId).getPayload();

    Map<String, List<NodeCarrierResponse>> nodeCarrierResponseMap =
        constructMap(nodeCarrierResponseList);
    List<String> csvContents = new ArrayList<>();

    nodeResponseList.forEach(
        node -> {
          List<NodeCarrierResponse> nodeCarrierResponses =
              nodeCarrierResponseMap.get(node.getNodeId());
          if (nodeCarrierResponses != null) {
            nodeCarrierResponses.forEach(
                nodeCarrierResponse ->
                    csvContents.add(constructCSVContents(node, nodeCarrierResponse)));
          } else csvContents.add(constructCSVContents(node, new NodeCarrierResponse()));
        });

    return String.join("\n", csvContents);
  }

  private Map<String, List<NodeCarrierResponse>> constructMap(
      List<NodeCarrierResponse> nodeCarrierResponseList) {
    Map<String, List<NodeCarrierResponse>> nodeCarrierResponseMap = new HashMap<>();
    Set<String> visitedNodes = new HashSet<>();

    nodeCarrierResponseList.forEach(
        nodeCarrierResponse -> {
          if (!visitedNodes.contains(nodeCarrierResponse.getNodeId())) {
            List<NodeCarrierResponse> nodeCarrierResponseList1 = new ArrayList<>();
            nodeCarrierResponseList.forEach(
                nodeCarrierResponse1 -> {
                  if (nodeCarrierResponse1.getNodeId().equals(nodeCarrierResponse.getNodeId())) {
                    nodeCarrierResponseList1.add(nodeCarrierResponse1);
                  }
                });
            nodeCarrierResponseMap.put(nodeCarrierResponse.getNodeId(), nodeCarrierResponseList1);
            visitedNodes.add(nodeCarrierResponse.getNodeId());
          }
        });

    return nodeCarrierResponseMap;
  }

  private String constructCSVContents(NodeResponse node, NodeCarrierResponse nodeCarrierResponse) {
    String serviceOption = checkForNullValues(nodeCarrierResponse.getServiceOption());
    String bufferHours = checkForNullValues(nodeCarrierResponse.getBufferHours());
    String bufferStartDate = checkForNullValues(nodeCarrierResponse.getBufferStartDate());
    String bufferEndDate = checkForNullValues(nodeCarrierResponse.getBufferEndDate());
    String status =
        checkForNullValues(
            computeStatus(
                nodeCarrierResponse.getBufferHours(),
                nodeCarrierResponse.getBufferStartDate(),
                nodeCarrierResponse.getBufferEndDate()));

    return node.getNodeId()
        + ","
        + node.getOrgId()
        + ","
        + node.getNodeType()
        + ","
        + node.getStreet()
        + ","
        + node.getCity()
        + ","
        + node.getProvince()
        + ","
        + node.getPostalCode()
        + ","
        + serviceOption
        + ","
        + bufferHours
        + ","
        + bufferStartDate
        + ","
        + bufferEndDate
        + ","
        + status;
  }

  private String checkForNullValues(Object value) {
    if (value instanceof Date) {
      // Convert Date to String UTC format
      return ((Date) value).toInstant().toString();
    }
    return (value == null) ? "NA" : value.toString();
  }

  private String computeStatus(Double bufferHours, Date bufferStartDate, Date bufferEndDate) {
    var currentDate = new Date();
    if (bufferHours != null && bufferStartDate != null && bufferEndDate != null) {
      if (currentDate.compareTo(bufferEndDate) <= 0) return "Active";
      else return "Inactive";
    }
    return null;
  }
}
