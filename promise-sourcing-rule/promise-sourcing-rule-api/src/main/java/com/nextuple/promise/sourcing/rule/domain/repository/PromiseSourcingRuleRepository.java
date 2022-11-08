package com.nextuple.promise.sourcing.rule.domain.repository;

import com.nextuple.promise.sourcing.rule.domain.entity.PromiseSourcingRule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromiseSourcingRuleRepository extends JpaRepository<PromiseSourcingRule, String> {

  List<PromiseSourcingRule> findByOrgId(String orgId);

  List<PromiseSourcingRule> findByPriority(int priority);

  List<PromiseSourcingRule> findByServiceOptionInAndOrgIdAndAllocationRuleIdAndDestinationGeoZone(
      List<String> serviceOptions,
      String orgId,
      String allocationRuleId,
      String destinationGeoZone);

  List<PromiseSourcingRule> findByServiceOptionAndOrgIdAndAllocationRuleIdAndDestinationGeoZone(
      String serviceOption, String orgId, String allocationRuleId, String destinationGeoZone);

  PromiseSourcingRule
      findByOrgIdAndServiceOptionAndDestinationGeoZoneAndAllocationRuleIdAndPriority(
          String orgId,
          String serviceOption,
          String destinationGeoZone,
          String allocationRuleId,
          int priority);
}
