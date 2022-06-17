package com.nextuple.carrier.repository;

import com.nextuple.carrier.domain.entity.CarrierServiceEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarrierServiceRepository extends JpaRepository<CarrierServiceEntity, String> {
  Optional<CarrierServiceEntity> findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
      String carrierId, String serviceId, String orgId);
}
