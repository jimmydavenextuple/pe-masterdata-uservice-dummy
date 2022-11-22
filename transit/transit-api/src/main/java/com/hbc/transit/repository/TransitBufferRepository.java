package com.hbc.transit.repository;

import com.hbc.transit.domain.entity.TransitBufferEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransitBufferRepository extends JpaRepository<TransitBufferEntity, Long> {
  List<TransitBufferEntity> findByOrgIdAndDestinationGeozone(
      String orgId, String destinationGeozone);

  Optional<TransitBufferEntity> findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
      String orgId, String carrierServiceId, String sourceGeozone, String destinationGeozone);

  List<TransitBufferEntity> findByTransitBufferConfigRequestId(Long transitBufferConfigRequestId);
}
