package com.hbc.node.service;

import static com.hbc.common.constants.CommonConstants.DEFAULT_SORT_ORDER;
import static com.hbc.common.constants.CommonConstants.DESC_SORT_ORDER;
import static com.hbc.common.constants.CommonConstants.NODE_DEFAULT_SORT_BY;

import com.hbc.calendar.domain.feign.CalendarFeign;
import com.hbc.calendar.domain.outbound.NodeCalendarResponse;
import com.hbc.common.base.PagePayload;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.error.FieldError;
import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.node.domain.NodeDomain;
import com.hbc.node.domain.dto.NodeCacheKeyDto;
import com.hbc.node.domain.dto.NodeDto;
import com.hbc.node.domain.dto.NodeListDto;
import com.hbc.node.domain.dto.NodeWorkingCalendarDto;
import com.hbc.node.domain.dto.PickupTimeDto;
import com.hbc.node.domain.entity.NodeEntity;
import com.hbc.node.domain.feign.NodeFeign;
import com.hbc.node.domain.inbound.NodeRequest;
import com.hbc.node.domain.inbound.NodeUpdationRequest;
import com.hbc.node.domain.mapper.NodeMapper;
import com.hbc.node.domain.outbound.NodeResponse;
import com.hbc.node.exception.NodeDomainException;
import com.hbc.postgres.config.ReaderDS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NodeService {

  private static final Logger logger = LoggerFactory.getLogger(NodeService.class);
  private static final String ORG_ID = "orgId";
  private static final String NODE_ID = "nodeId";
  private static final String SORT_ORDER = "sortOrder";

  private final NodeDomain nodeDomain;

  private final CalendarFeign calendarFeign;
  private final NodeCarrierFeign nodeCarrierFeign;


  public static final NodeMapper INSTANCE = Mappers.getMapper(NodeMapper.class);

  private static final String NODE_EXCEPTION_MESSAGE = "Node not found with given details";

  public NodeResponse createNode(NodeRequest nodeRequest) throws NodeDomainException {

    var nodeEntity = INSTANCE.nodeRequestToNodeEntity(nodeRequest);

    return INSTANCE.toNodeResponse(nodeDomain.saveNodeEntity(nodeEntity));
  }

  public NodeResponse updateNodeDetails(
      String nodeId, String orgId, NodeUpdationRequest nodeUpdationRequest)
      throws NodeDomainException, CommonServiceException {

    Optional<NodeEntity> existingNodeEntity = nodeDomain.findNodeByNodeIdAndOrgId(nodeId, orgId);

    if (existingNodeEntity.isEmpty()) {
      logger.error(NODE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
      throw new CommonServiceException(
          NODE_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    logger.info("Response before updation of node :{}", existingNodeEntity.get());
    INSTANCE.updateNodeEntity(nodeUpdationRequest, existingNodeEntity.get());
    return INSTANCE.toNodeResponse(nodeDomain.saveNodeEntity(existingNodeEntity.get()));
  }

  @ReaderDS
  public NodeResponse getNodeDetails(String nodeId, String orgId)
      throws NodeDomainException, CommonServiceException {

    Optional<NodeEntity> nodeEntity = nodeDomain.findNodeByNodeIdAndOrgId(nodeId, orgId);

    if (nodeEntity.isEmpty()) {
      logger.error(NODE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
      throw new CommonServiceException(
          NODE_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return INSTANCE.toNodeResponse(nodeEntity.get());
  }

  public NodeResponse deleteNode(String nodeId, String orgId)
      throws NodeDomainException, CommonServiceException {

    Optional<NodeEntity> nodeEntity = nodeDomain.findNodeByNodeIdAndOrgId(nodeId, orgId);

    if (nodeEntity.isEmpty()) {
      logger.error(NODE_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
      throw new CommonServiceException(
          NODE_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    logger.info("Response before deletion of node :{}", INSTANCE.toNodeResponse(nodeEntity.get()));
    var nodeResponse = INSTANCE.toNodeResponse(nodeEntity.get());
    nodeDomain.deleteNode(nodeEntity.get());
    return nodeResponse;
  }

  @ReaderDS
  public Page<NodeDto> getNodeListByOrgId(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder)
      throws NodeDomainException, CommonServiceException {
    if (sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)
        || sortOrder.equalsIgnoreCase(DESC_SORT_ORDER)) {
      return nodeDomain.getNodeByOrgId(orgId, pageNo, pageSize, sortBy, sortOrder);
    } else {
      logger.error("Invalid sort order");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(SORT_ORDER, FieldError.builder().rejectedValue(sortOrder).build());
      throw new CommonServiceException(
          "Invalid sort order, consider giving either ASC or DESC",
          HttpStatus.BAD_REQUEST,
          0x1771,
          errorMap);
    }
  }

  public List<NodeCacheKeyDto> getAllNodeCacheKeys(Integer limit) throws NodeDomainException {
    var nodeEntities = nodeDomain.getAllNodeEntities(limit);

    return INSTANCE.toNodeCacheKeyResponseList(nodeEntities);
  }

  @ReaderDS
  public PagePayload<NodeListDto> getNodesList(
          String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder) throws NodeDomainException, CommonServiceException {
    PagePayload<NodeListDto> nodeListDtoPagePayload = new PagePayload<>();
    List<NodeListDto> responseList = new ArrayList<>();

    Page<NodeDto> nodeDtoPage =
            getNodeListByOrgId(orgId, pageNo, pageSize, sortBy, sortOrder);

    List<NodeDto> nodeResponseList = nodeDtoPage.getContent();
    for (NodeDto nodeServiceResponse : nodeResponseList) {
      List<NodeCalendarResponse> nodeCalendarResponseList = new ArrayList<>();
      try {
        nodeCalendarResponseList =
                calendarFeign
                        .handleGetNodeCalendar(
                                nodeServiceResponse.getOrgId(), nodeServiceResponse.getNodeId())
                        .getPayload();
      } catch (RuntimeException e) {
        logger.error("Empty Node Calendar Response List");
      }

      List<NodeCarrierResponse> nodeCarrierResponse =
              nodeCarrierFeign.getNodeCarrierListWithLastPickUpTimeDetails(nodeServiceResponse.getNodeId(), nodeServiceResponse.getOrgId()).getPayload();

      responseList.add(
              setNodeListDto(
                      nodeServiceResponse, nodeCalendarResponseList, nodeCarrierResponse));
    }

    var pagination = new PagePayload.Pagination();
    pagination.setTotalRecords((int) nodeDtoPage.getTotalElements());
    pagination.setTotalPages(nodeDtoPage.getTotalPages());
    pagination.setCurrentPage(pageNo);
    pagination.setSortOrder(sortOrder);
    pagination.setSortBy(sortBy);
    nodeListDtoPagePayload.setPagination(pagination);
    nodeListDtoPagePayload.setData(responseList);
    return nodeListDtoPagePayload;
  }

  private NodeListDto setNodeListDto(NodeDto nodeResponse, List<NodeCalendarResponse> nodeCalendarResponseList, List<NodeCarrierResponse> nodeCarrierResponse) {
    NodeListDto nodeListDto;
    nodeListDto = INSTANCE.toNodeListDto(nodeResponse);
    nodeListDto.setNodeWorkingCalendar(setNodeCalendar(nodeCalendarResponseList));
    nodeListDto.setServiceOptions(getServiceOptions(nodeCarrierResponse));
    nodeListDto.setCarrierServices(getCarrierServiceIds(nodeCarrierResponse));
    nodeListDto.setPickupTime(getPickupTimeDetails(nodeCarrierResponse));
    return nodeListDto;
  }

  private List<PickupTimeDto> getPickupTimeDetails(List<NodeCarrierResponse> nodeCarrierResponseList) {

    List<PickupTimeDto> pickupTimeDtoList = new ArrayList<>();
    for (NodeCarrierResponse nodeCarrierResponse : nodeCarrierResponseList)
    {
      PickupTimeDto pickupTimeDto = new PickupTimeDto();
      pickupTimeDto.setNodeId(nodeCarrierResponse.getNodeId());
      pickupTimeDto.setCarrierServiceId(nodeCarrierResponse.getCarrierServiceId());
      pickupTimeDto.setPickupTime(nodeCarrierResponse.getLastPickupTime());
      pickupTimeDtoList.add(pickupTimeDto);
    }
    return pickupTimeDtoList.stream().distinct().collect(Collectors.toList());
  }

  private List<String> getCarrierServiceIds(List<NodeCarrierResponse> nodeCarrierResponse) {

    List<String> carrierServiceIds = new ArrayList<>();
    nodeCarrierResponse.forEach(nodeCarrier -> carrierServiceIds.add(nodeCarrier.getCarrierServiceId()));
    return carrierServiceIds.stream().distinct().collect(Collectors.toList());
  }

  private List<NodeWorkingCalendarDto> setNodeCalendar(List<NodeCalendarResponse> nodeCalendarResponseList) {

    List<NodeWorkingCalendarDto> nodeWorkingCalendarDtoList = new ArrayList<>();
    for (NodeCalendarResponse nodeCalendarResponse : nodeCalendarResponseList)
    {
      NodeWorkingCalendarDto nodeWorkingCalendarDto = new NodeWorkingCalendarDto();
      nodeWorkingCalendarDto.setCalendarId(nodeCalendarResponse.getCalendarId());
      nodeWorkingCalendarDto.setEffectiveDate(nodeCalendarResponse.getEffectiveDate());
      nodeWorkingCalendarDtoList.add(nodeWorkingCalendarDto);
    }
    return nodeWorkingCalendarDtoList.stream().distinct().collect(Collectors.toList());
  }

  private List<String> getServiceOptions(List<NodeCarrierResponse> nodeCarrierResponse) {
    List<String> serviceOptions = new ArrayList<>();
    nodeCarrierResponse.forEach(nodeCarrier -> serviceOptions.add(nodeCarrier.getServiceOption()));
    return serviceOptions.stream().distinct().collect(Collectors.toList());
  }
}
