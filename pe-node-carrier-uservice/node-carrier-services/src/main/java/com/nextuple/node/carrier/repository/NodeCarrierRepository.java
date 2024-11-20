/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.repository;

import com.nextuple.node.carrier.domain.entity.NodeCarrierEntity;
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

  @Query(
      value =
          "SELECT DISTINCT carrier_service_id FROM node_carrier WHERE org_id = ?1 AND node_id = ?2",
      nativeQuery = true)
  List<String> findUniqueNodeCarrierServiceListByOrgIdAndNodeId(String orgId, String nodeId);

  List<NodeCarrierEntity> findByNodeIdAndOrgId(String nodeId, String orgId);

  List<NodeCarrierEntity> findByOrgIdAndCarrierServiceId(String orgId, String carrierServiceId);
}
