/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.repository;

import com.nextuple.node.carrier.domain.entity.NodeCarriersEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NodeCarriersRepository extends JpaRepository<NodeCarriersEntity, String> {
  Optional<NodeCarriersEntity> findByOrgIdAndNodeIdAndCarrierServiceIdAndServiceOption(
      String orgId, String nodeId, String carrierServiceId, String serviceOption);

  Optional<NodeCarriersEntity> deleteByOrgIdAndNodeIdAndCarrierServiceIdAndServiceOption(
      String orgId, String nodeId, String carrierServiceId, String serviceOption);

  List<NodeCarriersEntity> findByOrgIdAndNodeId(String orgId, String nodeId);

  List<NodeCarriersEntity> findByOrgIdAndNodeIdAndServiceOption(
      String orgId, String nodeId, String serviceOption);

  Page<NodeCarriersEntity> findAll(Pageable pageable);

  List<NodeCarriersEntity> findByOrgIdAndCarrierServiceId(String orgId, String carrierServiceId);
}
