/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.domain.repository;

import com.nextuple.sourcing.cost.config.domain.entity.CostItineraryAndFactorsEntity;
import com.nextuple.sourcing.cost.config.enums.ItineraryStatusEnum;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CostItineraryAndFactorsRepository
    extends JpaRepository<CostItineraryAndFactorsEntity, String> {
  Optional<CostItineraryAndFactorsEntity> findByIdAndOrgId(long id, String orgId);

  @Query(
      value =
          "SELECT * FROM public.cost_itinerary_and_factors \n"
              + "WHERE org_id=:orgId AND cost_factors LIKE %:costFactor%",
      nativeQuery = true)
  List<CostItineraryAndFactorsEntity> findCostFactorInItineraries(
      @Param("costFactor") String costFactor, @Param("orgId") String orgId);

  Optional<CostItineraryAndFactorsEntity> findByOrgIdAndCostItinerary(
      String orgId, String costItinerary);

  Optional<CostItineraryAndFactorsEntity> findByOrgIdAndCostItineraryAndItineraryStatus(
      String orgId, String costItinerary, ItineraryStatusEnum itineraryStatus);

  @Query(
      value =
          "SELECT * FROM cost_itinerary_and_factors WHERE org_Id=:orgId AND (cost_factors=:costFactor OR cost_factors LIKE CONCAT(:costFactor, ',%') OR cost_factors LIKE CONCAT('%,', :costFactor) OR cost_factors LIKE CONCAT('%,', :costFactor, ',%'))",
      nativeQuery = true)
  List<CostItineraryAndFactorsEntity> findCostItinerariesByCostFactor(
      @Param("orgId") String orgId, @Param("costFactor") String costFactor);

  Optional<CostItineraryAndFactorsEntity> findByOrgIdAndCostItineraryAndItineraryStatusAndIsActive(
      String orgId, String costItinerary, ItineraryStatusEnum itineraryStatus, Boolean isActive);

  @Query(value = "SELECT * FROM cost_itinerary_and_factors LIMIT ?1", nativeQuery = true)
  List<CostItineraryAndFactorsEntity> findAllCostItineraryAndFactorsEntities(Integer limit);
}
