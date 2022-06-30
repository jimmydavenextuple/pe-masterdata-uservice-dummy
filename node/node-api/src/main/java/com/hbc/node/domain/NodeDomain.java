package com.hbc.node.domain;

import com.hbc.node.domain.entity.NodeEntity;
import com.hbc.node.exception.NodeDomainException;
import com.hbc.node.repository.NodeRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodeDomain {

  private static final Logger logger = LoggerFactory.getLogger(NodeDomain.class);
  private final NodeRepository nodeRepository;

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
}
