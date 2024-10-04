/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.repository;

import com.nextuple.node.carrier.domain.entity.NodeCarrierSelectionEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NodeCarrierSelectionRepository
    extends JpaRepository<NodeCarrierSelectionEntity, String> {

  @Query(
      value =
          "SELECT * FROM node_carrier_selection t WHERE t.org_id = ?1 AND t.service_option = ?2 AND ( t.destination_geozone = ?3 OR t.destination_geozone = ?4)",
      nativeQuery = true)
  List<NodeCarrierSelectionEntity> findByOrgIdAndServiceOptionAndDestinationGeoZone(
      String orgId, String serviceOption, String destinationGeozone1, String destinationGeozone2);

  Optional<NodeCarrierSelectionEntity>
      findByOrgIdAndServiceOptionAndSourceGeozoneAndDestinationGeozone(
          String orgId, String serviceOption, String sourceGeozone, String destinationGeozone);
}
