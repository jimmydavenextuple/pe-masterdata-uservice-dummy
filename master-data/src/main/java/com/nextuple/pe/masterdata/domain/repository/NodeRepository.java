package com.nextuple.pe.masterdata.domain.repository;

import com.nextuple.pe.masterdata.domain.entity.NodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NodeRepository extends JpaRepository<NodeEntity, String> {

  Optional<NodeEntity> findByNodeIdAndOrgId(String nodeId, String orgId);
}
