package com.hbc.transit.repository;

import com.hbc.transit.domain.entity.TransitEntity;
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

  @Query(
      value =
          "SELECT * FROM transit_data t WHERE t.org_id = ?1 AND t.destination_geozone = ?2  AND t.source_geozone IN ?3 ",
      nativeQuery = true)
  List<TransitEntity> findByOrgIdAndDestinationGeozoneAndSourceGeoZones(
      String orgId, String destinationGeozone, List<String> sourceGeozones);

  @Query(
      value =
          "SELECT COUNT(*) FROM transit_data t WHERE t.org_id = ?1 AND t.carrier_service_id = ?2",
      nativeQuery = true)
  Integer findTransitCountByOrgIdAndCarrierServiceId(String orgId, String carrierServiceId);

  @Query(
          value =
                  "SELECT * FROM transit_data t WHERE t.org_id = ?1 AND t.destination_geozone = ?2 ",
          nativeQuery = true)
  List<TransitEntity> findByOrgIdAndDestinationGeozone(
          String orgId, String destinationGeozone);

}
