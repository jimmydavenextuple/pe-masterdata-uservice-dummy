package com.hbc.node.carrier.repository;

import com.hbc.node.carrier.domain.entity.NodeCarrierSelectionEntity;
import java.util.List;
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
}
