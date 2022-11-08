package com.nextuple.node.service;

import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;
import static com.nextuple.common.constants.CommonConstants.DESC_SORT_ORDER;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.node.domain.NodeDomain;
import com.nextuple.node.domain.dto.NodeCacheKeyDto;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.node.domain.entity.NodeEntity;
import com.nextuple.node.domain.inbound.NodeRequest;
import com.nextuple.node.domain.inbound.NodeUpdationRequest;
import com.nextuple.node.domain.mapper.NodeMapper;
import com.nextuple.node.domain.outbound.NodeResponse;
import com.nextuple.node.exception.NodeDomainException;
import com.nextuple.postgres.config.ReaderDS;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTimeZone;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NodeService {

  private static final Logger logger = LoggerFactory.getLogger(NodeService.class);
  private static final String ORG_ID = "orgId";
  private static final String NODE_ID = "nodeId";
  private static final String NODE_TYPE = "nodeType";
  private static final String SORT_ORDER = "sortOrder";

  private final NodeDomain nodeDomain;

  public static final NodeMapper INSTANCE = Mappers.getMapper(NodeMapper.class);

  private static final String NODE_EXCEPTION_MESSAGE = "Node not found with given details";
  private static final String NODE_TYPE_EXCEPTION_MESSAGE = "Invalid Node Type Found";
  private static final String TIMEZONE_EXCEPTION_MESSAGE = "Invalid Timezone Found";
  private static final String COUNTRY_EXCEPTION_MESSAGE = "Invalid Country Found";

  @Value("#{'${node.type}'.split('\\s*,\\s*')}")
  public Set<String> nodeTypes;

  public NodeResponse createNode(NodeRequest nodeRequest)
      throws NodeDomainException, CommonServiceException {
    validateNodeTypeTimezoneAndCountry(
        nodeRequest.getNodeType(), nodeRequest.getTimezone(), nodeRequest.getCountry());
    var nodeEntity = INSTANCE.nodeRequestToNodeEntity(nodeRequest);

    return INSTANCE.toNodeResponse(nodeDomain.saveNodeEntity(nodeEntity));
  }

  public NodeResponse updateNodeDetails(
      String nodeId, String orgId, NodeUpdationRequest nodeUpdationRequest)
      throws NodeDomainException, CommonServiceException {
    validateNodeTypeTimezoneAndCountry(
        nodeUpdationRequest.getNodeType(),
        nodeUpdationRequest.getTimezone(),
        nodeUpdationRequest.getCountry());
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

  @ReaderDS
  public Page<NodeResponse> getAllNodes(
      Integer pageNo, Integer pageSize, String sortBy, String sortOrder)
      throws NodeDomainException {
    return nodeDomain.getAllNodesPaginated(pageNo, pageSize, sortBy, sortOrder);
  }

  public List<NodeCacheKeyDto> getAllNodeCacheKeys(Integer limit) throws NodeDomainException {
    var nodeEntities = nodeDomain.getAllNodeEntities(limit);

    return INSTANCE.toNodeCacheKeyResponseList(nodeEntities);
  }

  private void validateNodeTypeTimezoneAndCountry(String nodeType, String timezone, String country)
      throws CommonServiceException {
    if (!nodeTypes.contains(nodeType.toUpperCase()))
      throwCommonServiceException(NODE_TYPE, nodeType, NODE_TYPE_EXCEPTION_MESSAGE);
    if (!Set.of(Locale.getISOCountries()).contains(country))
      throwCommonServiceException("country", country, COUNTRY_EXCEPTION_MESSAGE);
    if (!DateTimeZone.getAvailableIDs().contains(timezone))
      throwCommonServiceException("timezone", timezone, TIMEZONE_EXCEPTION_MESSAGE);
  }

  private void throwCommonServiceException(String field, String fieldValue, String errorMessage)
      throws CommonServiceException {
    logger.error(errorMessage);
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(field, FieldError.builder().rejectedValue(fieldValue).build());
    throw new CommonServiceException(errorMessage, HttpStatus.NOT_FOUND, 0x1771, errorMap);
  }
}
