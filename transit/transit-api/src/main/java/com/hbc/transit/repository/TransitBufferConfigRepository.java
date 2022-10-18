package com.hbc.transit.repository;

import com.hbc.transit.domain.entity.TransitBufferConfigRequestEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransitBufferConfigRepository
    extends JpaRepository<TransitBufferConfigRequestEntity, String> {
  Optional<TransitBufferConfigRequestEntity> findById(long id);

  @Query(
      value =
          "SELECT * FROM transit_buffer_config_request t WHERE t.org_id = ?1 AND t.carrier_service_id = ?2  AND t.status NOT IN ?3 ",
      nativeQuery = true)
  List<TransitBufferConfigRequestEntity> findByOrgIdAndCarrierServiceId(
      String orgId, String carrierServiceId, List<String> status);
}
