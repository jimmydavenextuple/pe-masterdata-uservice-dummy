package com.hbc.node.repository;

import com.hbc.node.domain.entity.NodeEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeRepository extends JpaRepository<NodeEntity, String> {

  Optional<NodeEntity> findByNodeIdAndOrgId(String nodeId, String orgId);

  Page<NodeEntity> findNodeByOrgId(String orgId, Pageable pageable);
}
