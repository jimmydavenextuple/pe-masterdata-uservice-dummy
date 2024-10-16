/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.sourcing.cost.config.domain.repository;

import com.nextuple.sourcing.cost.config.domain.entity.SelectorAndCostItineraryMappingEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SelectorAndCostItineraryMappingRepository
    extends JpaRepository<SelectorAndCostItineraryMappingEntity, String> {
  Optional<SelectorAndCostItineraryMappingEntity> findByIdAndOrgId(long id, String orgId);

  Optional<SelectorAndCostItineraryMappingEntity> findById(long id);

  List<SelectorAndCostItineraryMappingEntity> findByOrgId(String orgId);

  List<SelectorAndCostItineraryMappingEntity> findByOrgIdAndCostType(String orgId, String costType);

  List<SelectorAndCostItineraryMappingEntity> findByOrgIdAndSelectorCf(
      String orgId, String selectorCf);

  List<SelectorAndCostItineraryMappingEntity> findByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(
      String orgId, String costType, String selectorCf, String selectorCfValue);

  List<SelectorAndCostItineraryMappingEntity> findByOrgIdAndCostTypeAndSelectorCf(
      String orgId, String costType, String selectorCf);

  Optional<SelectorAndCostItineraryMappingEntity> findFirstByOrgIdAndCostType(
      String orgId, String costType);

  Optional<SelectorAndCostItineraryMappingEntity>
      findFirstByOrgIdAndCostTypeAndSelectorCfAndSelectorCfValue(
          String orgId, String costType, String selectorCf, String selectorCfValue);

  List<SelectorAndCostItineraryMappingEntity> findByOrgIdAndSelectorCfValueAndCostType(
      String orgId, String selectorCfValue, String costType);

  List<SelectorAndCostItineraryMappingEntity> findByOrgIdAndSelectorCfAndCostType(
      String orgId, String selectorCf, String costType);

  @Query(value = "SELECT * FROM selector_and_cost_itinerary_mapping LIMIT ?1", nativeQuery = true)
  List<SelectorAndCostItineraryMappingEntity> findAllSelectorAndCostItineraryMappingEntities(
      Integer limit);

  List<SelectorAndCostItineraryMappingEntity>
      findBySelectorCfIsNotNullAndSelectorCfValueIsNullAndCostItinerary(String costItinerary);

  List<SelectorAndCostItineraryMappingEntity>
      findBySelectorCfIsNullAndSelectorCfValueIsNullAndCostItinerary(String costItinerary);
}
