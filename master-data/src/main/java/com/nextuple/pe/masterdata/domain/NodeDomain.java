package com.nextuple.pe.masterdata.domain;

import com.nextuple.pe.masterdata.domain.entity.NodeEntity;
import com.nextuple.pe.masterdata.domain.repository.NodeRepository;
import com.nextuple.pe.masterdata.exception.NodeDomainException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
