package com.nextuple.promise.sourcing.rule.domain;

import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.domain.entity.PromiseSourcingRule;
import com.nextuple.promise.sourcing.rule.domain.repository.PromiseSourcingRuleRepository;
import com.nextuple.promise.sourcing.rule.exception.common.ApplicationLayer;
import com.nextuple.promise.sourcing.rule.exception.common.ExceptionCodeMapping;
import com.nextuple.promise.sourcing.rule.exception.common.PromiseEngineException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromiseSourcingRuleDomain {
  private static final Logger logger = LoggerFactory.getLogger(PromiseSourcingRuleDomain.class);
  private final PromiseSourcingRuleRepository promiseSourcingRuleRepository;

  /**
   * Fetch Promise Sourcing Rule
   *
   * @param baseRequest Fetch Promise Sourcing Rule Request
   * @return List of Promise Sourcing Rules
   * @throws PromiseEngineException
   */
  public List<PromiseSourcingRule> fetchSourcingRule(FetchPromiseSourcingRuleRequest baseRequest)
      throws PromiseEngineException {
    logger.info("-- inside fetchSourcingRule domain --");
    try {
      return promiseSourcingRuleRepository
          .findByServiceOptionInAndOrgIdAndAllocationRuleIdAndDestinationGeoZone(
              baseRequest.getServiceOptions(),
              baseRequest.getOrgId(),
              baseRequest.getAllocationRuleId(),
              baseRequest.getDestinationGeoZone());
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find Promise Sourcing Rule!");
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Unable to find Promise Sourcing Rule!");
    }
  }

  /**
   * Save Promise sourcing rule
   *
   * @param promiseSourcingRule Entity to be saved
   * @return Saved Promise Sourcing Rule
   * @throws PromiseEngineException
   */
  public PromiseSourcingRule savePromiseSourcingRule(PromiseSourcingRule promiseSourcingRule)
      throws PromiseEngineException {
    logger.info("-- inside savePromiseSourcingRule domain --");
    try {
      return promiseSourcingRuleRepository.save(promiseSourcingRule);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to save Promise sourcing rule!");
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_SAVE_FAILED,
          "Unable to save Promise sourcing rule!");
    }
  }

  /**
   * Get Promise Sourcing Rule
   *
   * @param orgId organisation ID using which service will look for Promise Sourcing Rule
   * @param serviceOption Service Option using which service will look for Promise Sourcing Rule
   * @param destinationGeoZone Destination Geo Zone using which service will look for Promise
   *     Sourcing Rule
   * @param allocationRuleId Allocation Rule ID using which service will look for Promise Sourcing
   *     Rule
   * @return Fetched Promise Sourcing Rule
   * @throws PromiseEngineException
   */
  public PromiseSourcingRule getPromiseSourcingRule(
      String orgId,
      String serviceOption,
      String destinationGeoZone,
      String allocationRuleId,
      int priority)
      throws PromiseEngineException {
    logger.info("-- inside getPromiseSourcingRule domain --");
    try {
      return promiseSourcingRuleRepository
          .findByOrgIdAndServiceOptionAndDestinationGeoZoneAndAllocationRuleIdAndPriority(
              orgId, serviceOption, destinationGeoZone, allocationRuleId, priority);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Promise Sourcing Rule not found!");
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Promise Sourcing Rule not found!");
    }
  }

  /**
   * Get Promise Sourcing Rules by orgId
   *
   * @param orgId Organisation ID
   * @return Fetched Promise Sourcing Rule
   * @throws PromiseEngineException
   */
  public List<PromiseSourcingRule> getPromiseSourcingRulesByOrgId(String orgId)
      throws PromiseEngineException {
    logger.info("-- inside getPromiseSourcingRulesByOrgId domain --");
    try {
      return promiseSourcingRuleRepository.findByOrgId(orgId);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Promise Sourcing Rules not found!");
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Promise Sourcing Rules not found!");
    }
  }

  /**
   * Get Promise Sourcing Rules by priority
   *
   * @param priority Priority
   * @return Fetched Promise Sourcing Rule
   * @throws PromiseEngineException
   */
  public List<PromiseSourcingRule> getPromiseSourcingRulesByPriority(int priority)
      throws PromiseEngineException {
    logger.info("-- inside getPromiseSourcingRulesByPriority domain --");
    try {
      return promiseSourcingRuleRepository.findByPriority(priority);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Promise Sourcing Rules not found!");
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_FIND_FAILED,
          "Promise Sourcing Rules not found!");
    }
  }

  /**
   * Delete Promise Sourcing Rule
   *
   * @param promiseSourcingRule Promise Sourcing Rule Entity to be deleted
   * @return Deleted Promise Sourcing Rule
   * @throws PromiseEngineException
   */
  public PromiseSourcingRule deletePromiseSourcingRule(PromiseSourcingRule promiseSourcingRule)
      throws PromiseEngineException {
    logger.info("-- inside deletePromiseSourcingRule domain --");
    try {
      promiseSourcingRuleRepository.delete(promiseSourcingRule);
      return promiseSourcingRule;
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to delete Promise Sourcing Rule!");
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.DAO_DELETE_FAILED,
          "Unable to delete Promise Sourcing Rule!");
    }
  }
}
