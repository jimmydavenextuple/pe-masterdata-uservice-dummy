package com.nextuple.carrier.repository;

import com.nextuple.carrier.domain.entity.CarrierServiceEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CarrierServiceRepository extends JpaRepository<CarrierServiceEntity, String> {
  Optional<CarrierServiceEntity> findCarrierServiceByCarrierIdAndCarrierServiceIdAndOrgId(
      String carrierId, String carrierServiceId, String orgId);

  Optional<List<CarrierServiceEntity>> findCarrierServiceByCarrierServiceIdAndOrgId(
      String carrierServiceId, String orgId);

  Page<CarrierServiceEntity> findCarrierServicesByOrgId(String orgId, Pageable pageable);

  List<CarrierServiceEntity> findCarrierServicesByOrgId(String orgId);

  @Query(value = "SELECT * FROM carrier_service LIMIT ?1", nativeQuery = true)
  List<CarrierServiceEntity> findAllCarriersByLimit(Integer limit);
}
