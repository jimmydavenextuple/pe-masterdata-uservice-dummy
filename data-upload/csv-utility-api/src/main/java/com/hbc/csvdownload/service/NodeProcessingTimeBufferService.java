package com.hbc.csvdownload.service;

import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.node.domain.feign.NodeFeign;
import com.hbc.node.domain.outbound.NodeResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodeProcessingTimeBufferService {

  private static final String ACTIVE = "Active";
  private static final String INACTIVE = "Inactive";
  private final NodeCarrierFeign nodeCarrierFeign;
  private final NodeFeign nodeFeign;

  public String getProcessingTimeBuffersByOgId(String orgId) {
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

    nodeCarrierResponseList.forEach(
        nodeCarrier -> {
          if (nodeCarrierResponseMap.containsKey(nodeCarrier.getNodeId())) {
            nodeCarrierResponseMap.get(nodeCarrier.getNodeId()).add(nodeCarrier);
          } else {
            List<NodeCarrierResponse> nodeCarrierResponseList1 = new ArrayList<>();
            nodeCarrierResponseList1.add(nodeCarrier);
            nodeCarrierResponseMap.put(nodeCarrier.getNodeId(), nodeCarrierResponseList1);
          }
        });

    return nodeCarrierResponseMap;
  }

  private String constructCSVContents(NodeResponse node, NodeCarrierResponse nodeCarrierResponse) {
    String serviceOption = checkForNullValues(nodeCarrierResponse.getServiceOption());
    String bufferHours = checkForNullValues(nodeCarrierResponse.getBufferHours());
    String bufferStartDate =
        checkForNullValues(convertToStringUTC(nodeCarrierResponse.getBufferStartDate()));
    String bufferEndDate =
        checkForNullValues(convertToStringUTC(nodeCarrierResponse.getBufferEndDate()));
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
    return (value == null) ? "NA" : value.toString();
  }

  private String convertToStringUTC(Date value) {
    if (value != null) {
      return value.toInstant().toString();
    }
    return null;
  }

  private String computeStatus(Double bufferHours, Date bufferStartDate, Date bufferEndDate) {
    if (bufferHours != null && bufferStartDate != null && bufferEndDate != null) {
      var currentDate = new Date();
      return currentDate.compareTo(bufferEndDate) <= 0 ? ACTIVE : INACTIVE;
    }
    return null;
  }
}
