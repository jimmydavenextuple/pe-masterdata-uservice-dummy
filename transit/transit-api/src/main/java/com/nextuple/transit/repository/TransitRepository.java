package com.nextuple.transit.repository;

import com.nextuple.transit.domain.entity.TransitEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransitRepository extends JpaRepository<TransitEntity, String> {

  @Query(
      value =
          "SELECT * FROM transit_data t WHERE t.org_id = ?1 AND t.source_geozone = ?2 AND t.destination_geozone = ?3 AND ( t.carrier_service_id = ?4 OR t.carrier_service_id = ?5 OR t.carrier_service_id = 'ALL')",
      nativeQuery = true)
  List<TransitEntity> findByCarrierServiceIdsWithServiceOption(
      String orgId,
      String sourceGeozone,
      String destinationGeozone,
      String carrierServiceId1,
      String carrierServiceId2);

  Optional<TransitEntity> findByOrgIdAndSourceGeozoneAndDestinationGeozoneAndCarrierServiceId(
      String orgId, String sourceGeozone, String destinationGeozone, String carrierServiceId);
}
