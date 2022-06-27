package com.hbc.carrier.repository;

import com.hbc.carrier.domain.entity.CarrierServiceEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarrierServiceRepository extends JpaRepository<CarrierServiceEntity, String> {
  Optional<CarrierServiceEntity> findCarrierServiceByCarrierIdAndCarrierServiceIdAndOrgId(
      String carrierId, String carrierServiceId, String orgId);
}
