package com.hbc.node.carrier.domain;

import com.hbc.node.carrier.domain.entity.NodeCarrierEntity;
import com.hbc.node.carrier.exception.NodeCarrierDomainException;
import com.hbc.node.carrier.repository.NodeCarrierRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodeCarrierDomain {

  private static final Logger logger = LoggerFactory.getLogger(NodeCarrierDomain.class);
  private final NodeCarrierRepository nodeCarrierRepository;

  public NodeCarrierEntity saveNodeCarrierEntity(NodeCarrierEntity nodeCarrierEntity)
      throws NodeCarrierDomainException {
    try {
      return nodeCarrierRepository.save(nodeCarrierEntity);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Error while creating node carrier");
      throw new NodeCarrierDomainException(
          "Error while saving the node carrier",
          nodeCarrierEntity.getNodeId(),
          nodeCarrierEntity.getOrgId(),
          nodeCarrierEntity.getCarrierServiceId(),
          nodeCarrierEntity.getServiceOption());
    }
  }

  public Optional<NodeCarrierEntity> findNodeCarrierDetails(
      String nodeId, String orgId, String carrierServiceId, String serviceOption)
      throws NodeCarrierDomainException {
    try {
      return nodeCarrierRepository.findByNodeIdAndOrgIdAndCarrierServiceIdAndServiceOption(
          nodeId, orgId, carrierServiceId, serviceOption);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find a node carrier");
      throw new NodeCarrierDomainException(
          "Error while finding node carrier", nodeId, orgId, carrierServiceId, serviceOption);
    }
  }

  public List<NodeCarrierEntity> filterAndGetNodeCarrierDetails(
      String nodeId, String orgId, String carrierServiceId, String serviceOption)
      throws NodeCarrierDomainException {
    try {
      return nodeCarrierRepository.findByCarrierServiceIdsWithServiceOption(
          nodeId, orgId, carrierServiceId, serviceOption);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find node carrier details");
      throw new NodeCarrierDomainException(
          "Error while finding node carrier", nodeId, orgId, carrierServiceId, serviceOption);
    }
  }

  public void deleteNodeCarrierEntity(NodeCarrierEntity nodeCarrierEntity)
      throws NodeCarrierDomainException {
    try {
      nodeCarrierRepository.delete(nodeCarrierEntity);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to delete node carrier");
      throw new NodeCarrierDomainException(
          "Error while deleting node carrier",
          nodeCarrierEntity.getNodeId(),
          nodeCarrierEntity.getOrgId(),
          nodeCarrierEntity.getCarrierServiceId(),
          nodeCarrierEntity.getServiceOption());
    }
  }

  public List<NodeCarrierEntity> findNodeCarrierByNodeIdAOrgIdAndServiceOption(
      String nodeId, String orgId, String serviceOption) throws NodeCarrierDomainException {
    try {
      return nodeCarrierRepository.findByNodeIdAndOrgIdAndServiceOption(
          nodeId, orgId, serviceOption);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find node carrier list");
      throw new NodeCarrierDomainException(
          "Error while finding node carrier list", nodeId, orgId, null, serviceOption);
    }
  }

  public List<NodeCarrierEntity> findNodeCarrierByNodeIdAndOrgId(String nodeId, String orgId)
      throws NodeCarrierDomainException {
    try {
      return nodeCarrierRepository.findByNodeIdAOrgIdAndBlankCarrierServiceId(nodeId, orgId);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find node carrier list");
      throw new NodeCarrierDomainException(
          "Error while fetching node carrier list for nodeId and orgId", nodeId, orgId, null, null);
    }
  }
}
