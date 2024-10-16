/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.persistence.repository;

import com.nextuple.node.persistence.entity.NodeEntity;
import com.nextuple.node.persistence.entity.key.NodeKey;
import com.nextuple.postgres.repository.CommonJpaRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeRepository extends CommonJpaRepository<NodeEntity, NodeKey> {

  Page<NodeEntity> findNodeByOrgId(String orgId, Pageable pageable);

  @Query(value = "SELECT * FROM node LIMIT ?1", nativeQuery = true)
  List<NodeEntity> findAllNodeEntities(Integer limit);

  @Query(value = "SELECT DISTINCT node_type FROM node WHERE org_id = ?1", nativeQuery = true)
  List<String> findDistinctNodeTypesByOrgId(String orgId);

  Page<NodeEntity> findByNodeIdInAndNodeTypeAndOrgId(
      List<String> nodeIdsList, String nodeType, String orgId, Pageable pageable);

  Page<NodeEntity> findByNodeTypeAndOrgId(String nodeType, String orgId, Pageable pageable);

  Page<NodeEntity> findByNodeIdInAndOrgId(
      List<String> nodeIdsList, String orgId, Pageable pageable);
}
