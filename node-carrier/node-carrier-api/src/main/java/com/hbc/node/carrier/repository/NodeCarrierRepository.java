package com.hbc.node.carrier.repository;

import com.hbc.node.carrier.domain.entity.NodeCarrierEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NodeCarrierRepository extends JpaRepository<NodeCarrierEntity, String> {
  Optional<NodeCarrierEntity> findByNodeIdAndOrgIdAndCarrierServiceIdAndServiceOption(
      String nodeId, String orgId, String carrierServiceId, String serviceOption);

  List<NodeCarrierEntity> findByNodeIdAndOrgIdAndServiceOption(
      String nodeId, String orgId, String serviceOption);

  @Query(
      value =
          "SELECT * FROM node_carrier t WHERE t.node_id = ?1 AND t.org_id = ?2 AND ( t.carrier_service_id = ?3 OR t.carrier_service_id = ?4 OR t.carrier_service_id = 'ALL')",
      nativeQuery = true)
  List<NodeCarrierEntity> findByCarrierServiceIdsWithServiceOption(
      String nodeId, String orgId, String carrierServiceId1, String carrierServiceId2);

  List<NodeCarrierEntity> findByNodeIdAndOrgIdAndCarrierServiceId(
      String nodeId, String orgId, String carrierServiceId);

  @Query(value = "SELECT * FROM node_carrier LIMIT ?1", nativeQuery = true)
  List<NodeCarrierEntity> findAllNodeCarriersByLimit(Integer limit);

  List<NodeCarrierEntity> findByNodeIdAndOrgId(String nodeId, String orgId);
}
