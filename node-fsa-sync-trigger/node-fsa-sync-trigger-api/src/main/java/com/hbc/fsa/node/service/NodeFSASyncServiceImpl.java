package com.hbc.fsa.node.service;

import com.hbc.common.base.PagePayload;
import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.fsa.node.mapper.NodeMapper;
import com.hbc.fsa.node.pojo.*;
import com.hbc.fsa.node.producer.NodeFSAMessagePublisher;
import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.node.domain.feign.NodeFeign;
import com.hbc.node.domain.outbound.NodeResponse;
import com.hbc.transit.domain.feign.TransitFeign;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NodeFSASyncServiceImpl implements NodeFSASyncService {
  private static final Logger logger = LoggerFactory.getLogger(NodeFSASyncServiceImpl.class);

  private final NodeFeign nodeFeign;
  private final NodeCarrierFeign nodeCarrierFeign;

  private final TransitFeign transitFeign;

  private final NodeFSAMessagePublisher nodeFSAMessagePublisher;

  private static final long DEFAULT_PROCESSING_TIME_IN_MINS = 120;
  private static final int PAGE_SIZE = 20;

  @Value("${fsa-updates.activated}")
  private Boolean fsaUpdatesActive;

  @Value("#{'${fsa-updates.serviceOptions}'.split('\\s*,\\s*')}")
  private Set<String> serviceOptions;

  @Value("${fsa-updates.publish-topic}")
  private String nodeFSATopic;

  private static final NodeMapper mapper = Mappers.getMapper(NodeMapper.class);

  @Override
  public void sendNodeFSAMapping(NodeFSASyncRequest request) {
    try {
      if (Boolean.FALSE.equals(fsaUpdatesActive)) return;
      if (Objects.isNull(request.getNodes()) || request.getNodes().isEmpty()) {
        publishForAllNodes();
      } else {
        List<NodeResponse> nodeResponses = new ArrayList<>();
        List<Node> nodesToProcess = request.getNodes();

        for (Node node : nodesToProcess) {
          NodeResponse response =
              nodeFeign.getNodeDetails(node.getNodeId(), node.getOrgId()).getPayload();
          nodeResponses.add(response);
        }
        publishForNodeList(nodeResponses);
      }
    } catch (Exception e) {
      logger.error("Error while sending nodeFSA mapping", e);
    }
  }

  void publishForNodeList(List<NodeResponse> nodeResponses) {
    for (NodeResponse nodeResponse : nodeResponses) {
      if (!Boolean.TRUE.equals(nodeResponse.getIsActive())) continue;
      var message = createNodeFSAMessage(nodeResponse);
      try {
        for (String serviceOption : serviceOptions) {
          String key = serviceOption.toLowerCase() + "Eligible";
          if (Boolean.FALSE.equals(
              nodeResponse.getServiceOptionEligibilities().getOrDefault(key, false))) continue;

          // these responses have one record with null CSI which gives us processing time
          List<NodeCarrierResponse> carrierResponses =
              nodeCarrierFeign
                  .getNodeCarrierListForServiceOption(
                      nodeResponse.getNodeId(), nodeResponse.getOrgId(), serviceOption)
                  .getPayload();

          String earliestCutoff = findMinCutoff(carrierResponses);
          List<String> carrierServiceIds =
              carrierResponses.stream()
                  .map(NodeCarrierResponse::getCarrierServiceId)
                  .filter(x -> !x.isBlank())
                  .collect(Collectors.toList());

          List<String> dFSAs =
              transitFeign
                  .getDistinctDestinationGeoZones(
                      nodeResponse.getOrgId(),
                      nodeResponse.getPostalCode().substring(0, 3),
                      carrierServiceIds)
                  .getPayload();

          Map<String, FSACoverage> fsaCoverage = message.getFsaCoverage();
          fsaCoverage.put(serviceOption, new FSACoverage(dFSAs, earliestCutoff));

          Map<String, Boolean> nodeEligiblityFlags = message.getNodeEligibilityFlags();
          nodeEligiblityFlags.put(serviceOption, Boolean.TRUE);
          message.setNodeEligibilityFlags(nodeEligiblityFlags);
        }
      } catch (Exception e) {
        logger.error(e, "Error while publishing node fsa mapping for node: {}", nodeResponse);
      }
      if (!message.getNodeEligibilityFlags().isEmpty())
        nodeFSAMessagePublisher.publish(message, nodeFSATopic);
    }
  }

  private String findMinCutoff(List<NodeCarrierResponse> carrierResponses) {
    Optional<NodeCarrierResponse> nodeCarrierOptional =
        carrierResponses.stream().filter(x -> x.getCarrierServiceId().isEmpty()).findFirst();
    long processingTime = DEFAULT_PROCESSING_TIME_IN_MINS;
    if (nodeCarrierOptional.isPresent()
        && Objects.nonNull(nodeCarrierOptional.get().getProcessingTime())) {
      processingTime = nodeCarrierOptional.get().getProcessingTime().longValue() * 60;
    }
    String minPickUpTime =
        carrierResponses.stream()
            .filter(x -> !x.getCarrierServiceId().isEmpty())
            .map(NodeCarrierResponse::getLastPickupTime)
            .min(String::compareTo)
            .orElse("");
    var pickUpTime = LocalTime.parse(minPickUpTime, DateTimeFormatter.ofPattern("HH:mm"));
    pickUpTime = pickUpTime.minus(processingTime, ChronoUnit.MINUTES);

    return pickUpTime.toString();
  }

  void publishForAllNodes() {
    var pageNo = 1;
    PagePayload<NodeResponse> nodeResponsePage =
        nodeFeign.getAllNodesList(pageNo, PAGE_SIZE, "nodeId", "desc").getPayload();

    while (nodeResponsePage.getPagination().getCurrentPage()
        <= nodeResponsePage.getPagination().getTotalPages()) {
      pageNo = pageNo + 1;
      List<NodeResponse> nodeResponses = nodeResponsePage.getData();
      publishForNodeList(nodeResponses);
      if (pageNo > nodeResponsePage.getPagination().getTotalPages()) break;
      nodeResponsePage =
          nodeFeign.getAllNodesList(pageNo, PAGE_SIZE, "nodeId", "desc").getPayload();
    }
  }

  private NodeFSAMessage createNodeFSAMessage(NodeResponse nodeResponse) {
    var message = new NodeFSAMessage();
    var systemTime = Instant.now().toString();
    message.setEventTime(systemTime);
    message.setEventName("NodeFSAMappingEvent_v1");
    message.setEventTrigger("ONDEMAND");
    message.setOrgId(nodeResponse.getOrgId());
    message.setNodeId(nodeResponse.getNodeId());
    message.setType(nodeResponse.getNodeType());

    var address = new Address();
    address.setStreet(nodeResponse.getStreet());
    address.setCity(nodeResponse.getCity());
    address.setProvince(nodeResponse.getProvince());
    address.setPostalCode(nodeResponse.getPostalCode());
    address.setCountry(nodeResponse.getCountry());
    address.setLongitude(nodeResponse.getLongitude());
    address.setLatitude(nodeResponse.getLatitude());
    message.setAddress(address);

    message.setFsaCoverage(new HashMap<>());
    message.setNodeEligibilityFlags(new HashMap<>());
    return message;
  }
}
