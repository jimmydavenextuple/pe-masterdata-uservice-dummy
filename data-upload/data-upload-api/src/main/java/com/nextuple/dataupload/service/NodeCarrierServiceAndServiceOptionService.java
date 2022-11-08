package com.nextuple.dataupload.service;

import com.nextuple.common.base.PagePayload;
import com.nextuple.dataupload.common.outbound.NodeCarrierServiceAndServiceOptionResponse;
import com.nextuple.dataupload.common.pojo.ActiveCombination;
import com.nextuple.node.carrier.domain.feign.NodeCarrierFeign;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.node.domain.feign.NodeFeign;
import com.nextuple.postgres.config.ReaderDS;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodeCarrierServiceAndServiceOptionService {

  private final NodeFeign nodeFeign;
  private final NodeCarrierFeign nodeCarrierFeign;

  @ReaderDS
  public PagePayload<NodeCarrierServiceAndServiceOptionResponse>
      getListOfNodeCarrierServiceAndServiceOptionDetails(
          String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder) {

    PagePayload<NodeDto> nodeResponse =
        nodeFeign.getNodeList(orgId, pageNo, pageSize, sortBy, sortOrder).getPayload();

    List<NodeCarrierServiceAndServiceOptionResponse> responseList =
        nodeResponse.getData().stream()
            .map(this::createNodeCarrierServiceAndServiceOptionResponse)
            .collect(Collectors.toList());

    PagePayload<NodeCarrierServiceAndServiceOptionResponse> payload = new PagePayload<>();
    payload.setData(responseList);
    payload.setPagination(nodeResponse.getPagination());
    return payload;
  }

  private NodeCarrierServiceAndServiceOptionResponse
      createNodeCarrierServiceAndServiceOptionResponse(NodeDto nodeDto) {
    List<NodeCarrierResponse> nodeCarrierResponseList =
        nodeCarrierFeign
            .getNodeCarrierListWithLastPickUpTimeDetails(nodeDto.getNodeId(), nodeDto.getOrgId())
            .getPayload();

    Set<String> carrierServiceIdsSet =
        nodeCarrierResponseList.stream()
            .map(NodeCarrierResponse::getCarrierServiceId)
            .collect(Collectors.toSet());

    Set<String> serviceOptionsSet =
        nodeCarrierResponseList.stream()
            .map(NodeCarrierResponse::getServiceOption)
            .collect(Collectors.toSet());

    List<ActiveCombination> activeCombinations =
        nodeCarrierResponseList.stream()
            .map(
                nodeCarrierResponse ->
                    ActiveCombination.builder()
                        .nodeId(nodeCarrierResponse.getNodeId())
                        .carrierServiceId(nodeCarrierResponse.getCarrierServiceId())
                        .serviceOption(nodeCarrierResponse.getServiceOption())
                        .isActive(true)
                        .build())
            .collect(Collectors.toList());

    return NodeCarrierServiceAndServiceOptionResponse.builder()
        .nodeId(nodeDto.getNodeId())
        .orgId(nodeDto.getOrgId())
        .street(nodeDto.getStreet())
        .city(nodeDto.getCity())
        .province(nodeDto.getProvince())
        .postalCode(nodeDto.getPostalCode())
        .carrierServices(new ArrayList<>(carrierServiceIdsSet))
        .serviceOptions(new ArrayList<>(serviceOptionsSet))
        .activeCombination(activeCombinations)
        .build();
  }
}
