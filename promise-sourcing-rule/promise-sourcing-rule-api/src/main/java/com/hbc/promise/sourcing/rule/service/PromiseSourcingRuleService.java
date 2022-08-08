package com.hbc.promise.sourcing.rule.service;

import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.EXPRESS;
import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.MHF;
import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.NEXTDAY;
import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.SDND;
import static com.hbc.promise.sourcing.rule.utils.PromiseSourcingRuleConstants.STANDARD;

import com.hbc.common.enums.ApplicationLayer;
import com.hbc.common.enums.ExceptionCodeMapping;
import com.hbc.common.exception.PromiseEngineException;
import com.hbc.promise.sourcing.rule.api.domain.dto.PromiseSourcingRuleDto;
import com.hbc.promise.sourcing.rule.api.domain.inbound.CreatePromiseSourcingRuleRequest;
import com.hbc.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.hbc.promise.sourcing.rule.api.domain.inbound.UpdatePromiseSourcingRuleRequest;
import com.hbc.promise.sourcing.rule.api.domain.outbound.FetchPromiseSourcingRuleResponse;
import com.hbc.promise.sourcing.rule.api.domain.pojo.ServiceOptionInfo;
import com.hbc.promise.sourcing.rule.domain.PromiseSourcingRuleDomain;
import com.hbc.promise.sourcing.rule.domain.entity.PromiseSourcingRule;
import com.hbc.promise.sourcing.rule.domain.mapper.PromiseSourcingRuleMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class PromiseSourcingRuleService {

  private static final Logger logger = LoggerFactory.getLogger(PromiseSourcingRuleService.class);
  private static final PromiseSourcingRuleMapper INSTANCE =
      Mappers.getMapper(PromiseSourcingRuleMapper.class);
  private final PromiseSourcingRuleDomain promiseSourcingRuleDomain;

  /**
   * Convert PromiseSourcingRule entity to PromiseSourcingRuleDto with all required processing
   *
   * @param promiseSourcingRule PromiseSourcingRule entity
   * @return PromiseSourcingRuleDto dto
   */
  private PromiseSourcingRuleDto preparePromiseSourcingRuleDto(
      PromiseSourcingRule promiseSourcingRule) {
    return INSTANCE.convertToPromiseSourcingRuleDto(promiseSourcingRule);
  }

  /**
   * Get Service Option Info
   *
   * @param promiseSourcingRule Promise Sourcing Rule
   * @return ServiceOptionInfo object
   */
  private ServiceOptionInfo getServiceOptionInfo(PromiseSourcingRule promiseSourcingRule) {
    var serviceOptionInfo = new ServiceOptionInfo();
    serviceOptionInfo.setSourceNodes(promiseSourcingRule.getSourceNodes());
    serviceOptionInfo.setPriority(promiseSourcingRule.getPriority());
    return serviceOptionInfo;
  }

  /**
   * Fetch Sourcing rule
   *
   * @param baseRequest Fetch Promise Sourcing Rule Request
   * @return FetchPromiseSourcingRuleResponse object
   * @throws PromiseEngineException
   */
  public FetchPromiseSourcingRuleResponse fetchSourcingRule(
      FetchPromiseSourcingRuleRequest baseRequest) throws PromiseEngineException {
    logger.debug("-- inside fetchSourcingRule service --");
    List<PromiseSourcingRule> promiseSourcingRuleList =
        promiseSourcingRuleDomain.fetchSourcingRule(baseRequest);
    if (promiseSourcingRuleList.isEmpty()) {
      throw new PromiseEngineException(
          ApplicationLayer.SERVICE_LAYER,
          ExceptionCodeMapping.SERVICE_FIND_FAILED,
          "Promise Sourcing Rules not found!");
    }

    var fetchPromiseSourcingRuleResponse = new FetchPromiseSourcingRuleResponse();
    List<ServiceOptionInfo> serviceOptionsForSdnd = new ArrayList<>();
    List<ServiceOptionInfo> serviceOptionsForStandard = new ArrayList<>();
    List<ServiceOptionInfo> serviceOptionsForExpress = new ArrayList<>();
    List<ServiceOptionInfo> serviceOptionsForNextday = new ArrayList<>();
    List<ServiceOptionInfo> serviceOptionsForMhf = new ArrayList<>();

    promiseSourcingRuleList.forEach(
        promiseSourcingRule -> {
          var serviceOptionInfo = getServiceOptionInfo(promiseSourcingRule);
          switch (promiseSourcingRule.getServiceOption()) {
            case SDND:
              serviceOptionsForSdnd.add(serviceOptionInfo);
              break;
            case STANDARD:
              serviceOptionsForStandard.add(serviceOptionInfo);
              break;
            case EXPRESS:
              serviceOptionsForExpress.add(serviceOptionInfo);
              break;
            case NEXTDAY:
              serviceOptionsForNextday.add(serviceOptionInfo);
              break;
            case MHF:
              serviceOptionsForMhf.add(serviceOptionInfo);
              break;
            default:
              logger.error("Invalid service option");
          }
        });
    fetchPromiseSourcingRuleResponse.setSdnd(serviceOptionsForSdnd);
    fetchPromiseSourcingRuleResponse.setStandard(serviceOptionsForStandard);
    fetchPromiseSourcingRuleResponse.setExpress(serviceOptionsForExpress);
    fetchPromiseSourcingRuleResponse.setNextday(serviceOptionsForNextday);
    fetchPromiseSourcingRuleResponse.setMhf(serviceOptionsForMhf);
    return fetchPromiseSourcingRuleResponse;
  }

  /**
   * Creates new PromiseSourcingRule entity
   *
   * @param baseRequest for Creating Promise Sourcing Rule
   * @return Created Promise Sourcing Rule dto
   * @throws PromiseEngineException
   */
  public PromiseSourcingRuleDto createPromiseSourcingRule(
      CreatePromiseSourcingRuleRequest baseRequest) throws PromiseEngineException {
    logger.debug("-- inside createPromiseSourcingRule service --");
    if (!StringUtils.hasLength(baseRequest.getAllocationRuleId())) {
      baseRequest.setAllocationRuleId("DEFAULT");
    }

    String destinationGeoZone = baseRequest.getDestinationGeoZone();
    String orgId = baseRequest.getOrgId();
    String serviceOption = baseRequest.getServiceOption();
    String allocationRuleId = baseRequest.getAllocationRuleId();
    Set<String> sourceNodes = baseRequest.getSourceNodes();

    validateSourcingRules(destinationGeoZone, orgId, serviceOption, allocationRuleId, sourceNodes);

    var promiseSourcingRule =
        INSTANCE.convertFromCreatePromiseSourcingRuleRequestToEntity(baseRequest);
    return preparePromiseSourcingRuleDto(
        promiseSourcingRuleDomain.savePromiseSourcingRule(promiseSourcingRule));
  }

  /**
   * Validate if for given parameters , rules exists or not
   *
   * @param destinationGeoZone Destination GeoZone
   * @param orgId Org Id
   * @param serviceOption Service option
   * @param allocationRuleId Allocation Rule Id
   * @param sourceNodes Source Nodes
   * @throws PromiseEngineException
   */
  private void validateSourcingRules(
      String destinationGeoZone,
      String orgId,
      String serviceOption,
      String allocationRuleId,
      Set<String> sourceNodes)
      throws PromiseEngineException {
    List<PromiseSourcingRule> promiseSourcingRules =
        promiseSourcingRuleDomain.fetchSourcingRule(
            serviceOption, orgId, allocationRuleId, destinationGeoZone);
    Set<String> definedSourceNodes =
        promiseSourcingRules.stream()
            .parallel()
            .map(PromiseSourcingRule::getSourceNodes)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());

    Set<String> nodesWithAlreadyExistingRules =
        definedSourceNodes.stream().filter(sourceNodes::contains).collect(Collectors.toSet());

    if (!CollectionUtils.isEmpty(nodesWithAlreadyExistingRules)) {
      throw new PromiseEngineException(
          ApplicationLayer.DAO_LAYER,
          ExceptionCodeMapping.SERVICE_INVALID_INPUT,
          "The nodes " + nodesWithAlreadyExistingRules + " are already part of different rules! ");
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
   * @param priority Priority using which service will look for Promise Sourcing Rule
   * @return Fetched Promise Sourcing Rule dto
   * @throws PromiseEngineException
   */
  public PromiseSourcingRuleDto getPromiseSourcingRule(
      String orgId,
      String serviceOption,
      String destinationGeoZone,
      String allocationRuleId,
      int priority)
      throws PromiseEngineException {
    logger.debug("-- inside getPromiseSourcingRule service --");
    Optional<PromiseSourcingRule> promiseSourcingRule =
        Optional.ofNullable(
            promiseSourcingRuleDomain.getPromiseSourcingRule(
                orgId, serviceOption, destinationGeoZone, allocationRuleId, priority));

    if (promiseSourcingRule.isEmpty()) {
      logger.error("-- Promise Sourcing Rule not found --");
      throw new PromiseEngineException(
          ApplicationLayer.SERVICE_LAYER,
          ExceptionCodeMapping.SERVICE_FIND_FAILED,
          "Promise Sourcing Rule not found!");
    }

    return preparePromiseSourcingRuleDto(promiseSourcingRule.get());
  }

  /**
   * Get Promise Sourcing Rules by orgId
   *
   * @param orgId using which service will look for Promise Sourcing Rule
   * @return Fetched Promise Sourcing Rules dto
   * @throws PromiseEngineException
   */
  public List<PromiseSourcingRuleDto> getPromiseSourcingRulesByOrgId(String orgId)
      throws PromiseEngineException {
    logger.debug("-- inside getPromiseSourcingRulesByOrgId service --");
    List<PromiseSourcingRule> promiseSourcingRules =
        promiseSourcingRuleDomain.getPromiseSourcingRulesByOrgId(orgId);

    return promiseSourcingRules.stream()
        .map(this::preparePromiseSourcingRuleDto)
        .collect(Collectors.toList());
  }

  /**
   * Get Promise Sourcing Rules by orgId
   *
   * @param priority using which service will look for Promise Sourcing Rule
   * @return Fetched Promise Sourcing Rules dto
   * @throws PromiseEngineException
   */
  public List<PromiseSourcingRuleDto> getPromiseSourcingRulesByPriority(int priority)
      throws PromiseEngineException {
    logger.debug("-- inside getPromiseSourcingRulesByPriority service --");
    List<PromiseSourcingRule> promiseSourcingRules =
        promiseSourcingRuleDomain.getPromiseSourcingRulesByPriority(priority);

    return promiseSourcingRules.stream()
        .map(this::preparePromiseSourcingRuleDto)
        .collect(Collectors.toList());
  }

  /**
   * Update Promise Sourcing Rule
   *
   * @param orgId organisation ID using which service will look for Promise Sourcing Rule
   * @param serviceOption Service Option using which service will look for Promise Sourcing Rule
   * @param destinationGeoZone Destination Geo Zone using which service will look for Promise
   *     Sourcing Rule
   * @param allocationRuleId Allocation Rule ID using which service will look for Promise Sourcing
   *     Rule
   * @param priority Priority using which service will look for Promise Sourcing Rule
   * @param baseRequest for Updating Promise Sourcing Rule
   * @return Updated Promise Sourcing Rule dto
   * @throws PromiseEngineException
   */
  public PromiseSourcingRuleDto updatePromiseSourcingRule(
      String orgId,
      String serviceOption,
      String destinationGeoZone,
      String allocationRuleId,
      int priority,
      UpdatePromiseSourcingRuleRequest baseRequest)
      throws PromiseEngineException {
    logger.debug("-- inside updatePromiseSourcingRule service --");
    var promiseSourcingRuleFromDB =
        INSTANCE.convertToPromiseSourcingRuleEntity(
            getPromiseSourcingRule(
                orgId, serviceOption, destinationGeoZone, allocationRuleId, priority));
    logger.info(
        "Response before updation of promise sourcing rule :{}",
        INSTANCE.convertToPromiseSourcingRuleDto(promiseSourcingRuleFromDB));
    INSTANCE.insertValuesFromUpdatePromiseSourcingRuleRequestToEntity(
        baseRequest, promiseSourcingRuleFromDB);

    return preparePromiseSourcingRuleDto(
        promiseSourcingRuleDomain.savePromiseSourcingRule(promiseSourcingRuleFromDB));
  }

  /**
   * Delete Promise source rule
   *
   * @param orgId organisation ID using which service will look for Promise Sourcing Rule
   * @param serviceOption Service Option using which service will look for Promise Sourcing Rule
   * @param destinationGeoZone Destination Geo Zone using which service will look for Promise
   *     Sourcing Rule
   * @param allocationRuleId Allocation Rule ID using which service will look for Promise Sourcing
   *     Rule
   * @param priority Priority using which service will look for Promise Sourcing Rule
   * @return Deleted Promise Sourcing Rule dto
   * @throws PromiseEngineException
   */
  public PromiseSourcingRuleDto deletePromiseSourcingRule(
      String orgId,
      String serviceOption,
      String destinationGeoZone,
      String allocationRuleId,
      int priority)
      throws PromiseEngineException {
    logger.debug("-- inside deletePromiseSourcingRule service --");
    var promiseSourcingRuleFromDB =
        INSTANCE.convertToPromiseSourcingRuleEntity(
            getPromiseSourcingRule(
                orgId, serviceOption, destinationGeoZone, allocationRuleId, priority));
    logger.info(
        "Response before deletion of promise sourcing rule :{}",
        INSTANCE.convertToPromiseSourcingRuleDto(promiseSourcingRuleFromDB));
    return preparePromiseSourcingRuleDto(
        promiseSourcingRuleDomain.deletePromiseSourcingRule(promiseSourcingRuleFromDB));
  }
}
