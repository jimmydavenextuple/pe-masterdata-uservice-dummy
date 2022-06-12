package com.nextuple.pe.masterdata.domain.repository;

import com.nextuple.pe.masterdata.domain.entity.NodeCarrierEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NodeCarrierRepository extends JpaRepository<NodeCarrierEntity, String> {
  Optional<NodeCarrierEntity> findByNodeIdAndOrgIdAndCarrierServiceIdAndServiceOption(
      String nodeId, String orgId, String carrierServiceId, String serviceOption);

  List<NodeCarrierEntity> findByNodeIdAndOrgIdAndServiceOption(
      String nodeId, String orgId, String serviceOption);
}
