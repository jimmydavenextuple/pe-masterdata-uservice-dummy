package com.hbc.promise.sourcing.rule.controller;

import com.hbc.common.exception.PromiseEngineException;
import com.hbc.common.response.BaseResponse;
import com.hbc.promise.sourcing.rule.api.domain.dto.PromiseSourcingRuleDto;
import com.hbc.promise.sourcing.rule.api.domain.inbound.CreatePromiseSourcingRuleRequest;
import com.hbc.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.hbc.promise.sourcing.rule.api.domain.inbound.UpdatePromiseSourcingRuleRequest;
import com.hbc.promise.sourcing.rule.api.domain.outbound.FetchPromiseSourcingRuleResponse;
import com.hbc.promise.sourcing.rule.service.PromiseSourcingRuleService;
import java.util.List;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/promiseSourcingRule")
@RequiredArgsConstructor
public class PromiseSourcingRuleController {

  private static final Logger logger = LoggerFactory.getLogger(PromiseSourcingRuleController.class);
  private final PromiseSourcingRuleService promiseSourcingRuleService;

  @PostMapping("/fetchRules")
  public ResponseEntity<BaseResponse<FetchPromiseSourcingRuleResponse>> fetchSourcingRule(
      @Valid @RequestBody FetchPromiseSourcingRuleRequest baseRequest)
      throws PromiseEngineException {
    logger.debug("Processing fetch Promise Sourcing Rule request");
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Promise Sourcing Rule successfully fetched!")
              .payload(promiseSourcingRuleService.fetchSourcingRule(baseRequest))
              .build());
    } catch (Exception e) {
      logger.error("Failed to process fetch Promise Sourcing Rule request!");
      throw e;
    }
  }

  @PostMapping
  public ResponseEntity<BaseResponse<PromiseSourcingRuleDto>> createPromiseSourcingRule(
      @Valid @RequestBody CreatePromiseSourcingRuleRequest baseRequest)
      throws PromiseEngineException {
    logger.debug("Processing create Promise Sourcing Rule request");
    try {
      PromiseSourcingRuleDto promiseSourcingRuleDto =
          promiseSourcingRuleService.createPromiseSourcingRule(baseRequest);
      logger.info("Response after creation of postal-code timezone :{}", promiseSourcingRuleDto);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Promise Sourcing Rule successfully created!")
              .payload(promiseSourcingRuleDto)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process create Promise Sourcing Rule request!");
      throw e;
    }
  }

  @GetMapping
  public ResponseEntity<BaseResponse<PromiseSourcingRuleDto>> getPromiseSourcingRule(
      @NotBlank @RequestParam String orgId,
      @NotBlank @RequestParam String serviceOption,
      @NotBlank @RequestParam String destinationGeoZone,
      @NotBlank @RequestParam String allocationRuleId,
      @NotBlank @RequestParam int priority)
      throws PromiseEngineException {
    logger.debug("Processing get Promise Sourcing Rule request");
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Promise Sourcing Rule successfully fetched!")
              .payload(
                  promiseSourcingRuleService.getPromiseSourcingRule(
                      orgId, serviceOption, destinationGeoZone, allocationRuleId, priority))
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get Promise Sourcing Rule request!");
      throw e;
    }
  }

  @GetMapping("/{orgId}@oid")
  public ResponseEntity<BaseResponse<List<PromiseSourcingRuleDto>>> getPromiseSourcingRulesByOrgId(
      @NotBlank @PathVariable String orgId) throws PromiseEngineException {
    logger.debug("Processing get Promise Sourcing Rules by orgId request");
    try {
      List<PromiseSourcingRuleDto> promiseSourcingRuleDtoList =
          promiseSourcingRuleService.getPromiseSourcingRulesByOrgId(orgId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Promise Sourcing Rules successfully fetched!")
              .payload(promiseSourcingRuleDtoList)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get Promise Sourcing Rules request!");
      throw e;
    }
  }

  @GetMapping("/{priority}@pty")
  public ResponseEntity<BaseResponse<List<PromiseSourcingRuleDto>>>
      getPromiseSourcingRulesByPriority(@NotBlank @PathVariable int priority)
          throws PromiseEngineException {
    logger.debug("Processing get Promise Sourcing Rules by priority request");
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Promise Sourcing Rules successfully fetched!")
              .payload(promiseSourcingRuleService.getPromiseSourcingRulesByPriority(priority))
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get Promise Sourcing Rules request!");
      throw e;
    }
  }

  @PutMapping
  public ResponseEntity<BaseResponse<PromiseSourcingRuleDto>> updatePromiseSourcingRule(
      @NotBlank @RequestParam String orgId,
      @NotBlank @RequestParam String serviceOption,
      @NotBlank @RequestParam String destinationGeoZone,
      @NotBlank @RequestParam String allocationRuleId,
      @NotBlank @RequestParam int priority,
      @Valid @RequestBody UpdatePromiseSourcingRuleRequest baseRequest)
      throws PromiseEngineException {
    logger.debug("Processing update Promise Sourcing Rule request");
    try {
      PromiseSourcingRuleDto promiseSourcingRuleDto =
          promiseSourcingRuleService.updatePromiseSourcingRule(
              orgId, serviceOption, destinationGeoZone, allocationRuleId, priority, baseRequest);
      logger.info("Response after updation of postal-code timezone :{}", promiseSourcingRuleDto);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Promise Sourcing Rule successfully updated!")
              .payload(promiseSourcingRuleDto)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process update Promise Sourcing Rule request!");
      throw e;
    }
  }

  @Transactional
  @DeleteMapping
  public ResponseEntity<BaseResponse<PromiseSourcingRuleDto>> deletePromiseSourcingRule(
      @NotBlank @RequestParam String orgId,
      @NotBlank @RequestParam String serviceOption,
      @NotBlank @RequestParam String destinationGeoZone,
      @NotBlank @RequestParam String allocationRuleId,
      @NotBlank @RequestParam int priority)
      throws PromiseEngineException {
    logger.debug("Processing delete Promise Sourcing Rule request by sourcingRuleId");
    try {
      PromiseSourcingRuleDto promiseSourcingRuleDto =
          promiseSourcingRuleService.deletePromiseSourcingRule(
              orgId, serviceOption, destinationGeoZone, allocationRuleId, priority);
      logger.info("Response after deletion of postal-code timezone :{}", promiseSourcingRuleDto);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Promise Sourcing Rule successfully deleted!")
              .payload(promiseSourcingRuleDto)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process delete Promise Sourcing Rule request!");
      throw e;
    }
  }
}
