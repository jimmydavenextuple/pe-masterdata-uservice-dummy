package com.hbc.node.domain;

import static com.hbc.common.constants.CommonConstants.DEFAULT_SORT_ORDER;

import com.hbc.node.domain.dto.NodeDto;
import com.hbc.node.domain.entity.NodeEntity;
import com.hbc.node.domain.mapper.NodeMapper;
import com.hbc.node.domain.outbound.NodeResponse;
import com.hbc.node.exception.NodeDomainException;
import com.hbc.node.repository.NodeRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodeDomain {

  private static final Logger logger = LoggerFactory.getLogger(NodeDomain.class);
  private final NodeRepository nodeRepository;

  public static final NodeMapper INSTANCE = Mappers.getMapper(NodeMapper.class);

  public NodeEntity saveNodeEntity(NodeEntity nodeEntity) throws NodeDomainException {
    try {
      return nodeRepository.save(nodeEntity);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to save node");
      throw new NodeDomainException(
          "Error while saving the node", nodeEntity.getNodeId(), nodeEntity.getOrgId());
    }
  }

  public Optional<NodeEntity> findNodeByNodeIdAndOrgId(String nodeId, String orgId)
      throws NodeDomainException {
    try {
      return nodeRepository.findByNodeIdAndOrgId(nodeId, orgId);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find node");
      throw new NodeDomainException("Error while finding node", nodeId, orgId);
    }
  }

  public void deleteNode(NodeEntity nodeEntity) throws NodeDomainException {
    try {
      nodeRepository.delete(nodeEntity);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to delete node");
      throw new NodeDomainException(
          "Error while deleting node", nodeEntity.getNodeId(), nodeEntity.getOrgId());
    }
  }

  public Page<NodeDto> getNodeByOrgId(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder)
      throws NodeDomainException {
    try {
      Pageable pageable = null;
      if (sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)) {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).ascending());
      } else {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).descending());
      }
      return nodeRepository.findNodeByOrgId(orgId, pageable).map(INSTANCE::toNodeDto);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find node list");
      throw new NodeDomainException("Error while finding node list", null, orgId);
    }
  }

  public Page<NodeResponse> getAllNodesPaginated(
      Integer pageNo, Integer pageSize, String sortBy, String sortOrder)
      throws NodeDomainException {
    try {
      Pageable pageable = null;
      if (sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)) {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).ascending());
      } else {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).descending());
      }
      return nodeRepository.findAll(pageable).map(INSTANCE::toNodeResponse);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find node list");
      throw new NodeDomainException("Error while finding node list", null, null);
    }
  }

  public List<NodeEntity> getAllNodeEntities(Integer limit) throws NodeDomainException {
    try {
      return nodeRepository.findAllNodeEntities(limit);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to fetch the node entities");
      throw new NodeDomainException("Error while fetching all node records", null, null);
    }
  }
}
