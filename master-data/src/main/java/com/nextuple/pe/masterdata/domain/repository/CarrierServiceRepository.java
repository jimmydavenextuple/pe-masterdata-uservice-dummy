package com.nextuple.pe.masterdata.domain.repository;

import com.nextuple.pe.masterdata.domain.entity.CarrierServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarrierServiceRepository extends JpaRepository<CarrierServiceEntity, String> {
  Optional<CarrierServiceEntity> findCarrierServiceByCarrierIdAndServiceIdAndOrgId(
      String carrierId, String serviceId, String orgId);
}
