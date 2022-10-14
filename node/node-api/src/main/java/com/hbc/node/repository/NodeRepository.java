package com.hbc.node.repository;

import com.hbc.node.domain.entity.NodeEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeRepository extends JpaRepository<NodeEntity, String> {

  Optional<NodeEntity> findByNodeIdAndOrgId(String nodeId, String orgId);

  Page<NodeEntity> findNodeByOrgId(String orgId, Pageable pageable);

  @Query(value = "SELECT * FROM node LIMIT ?1", nativeQuery = true)
  List<NodeEntity> findAllNodeEntities(Integer limit);
}
