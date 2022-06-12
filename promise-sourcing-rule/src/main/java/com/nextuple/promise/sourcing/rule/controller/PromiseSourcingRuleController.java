package com.nextuple.promise.sourcing.rule.controller;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.domain.dto.PromiseSourcingRuleDto;
import com.nextuple.promise.sourcing.rule.domain.inbound.CreatePromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.domain.inbound.UpdatePromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.domain.outbound.FetchPromiseSourcingRuleResponse;
import com.nextuple.promise.sourcing.rule.exception.common.PromiseEngineException;
import com.nextuple.promise.sourcing.rule.service.PromiseSourcingRuleService;
import java.util.List;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    logger.info("Processing fetch Promise Sourcing Rule request");
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Promise Sourcing Rule successfully fetched!")
              .payload(promiseSourcingRuleService.fetchSourcingRule(baseRequest))
              .build());
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Failed to process fetch Promise Sourcing Rule request!");
      throw e;
    }
  }

  @PostMapping
  public ResponseEntity<BaseResponse<PromiseSourcingRuleDto>> createPromiseSourcingRule(
      @Valid @RequestBody CreatePromiseSourcingRuleRequest baseRequest)
      throws PromiseEngineException {
    logger.info("Processing create Promise Sourcing Rule request");
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Promise Sourcing Rule successfully created!")
              .payload(promiseSourcingRuleService.createPromiseSourcingRule(baseRequest))
              .build());
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Failed to process create Promise Sourcing Rule request!");
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
    logger.info("Processing get Promise Sourcing Rule by sourcingRuleId request");
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Promise Sourcing Rule successfully fetched!")
              .payload(
                  promiseSourcingRuleService.getPromiseSourcingRule(
                      orgId, serviceOption, destinationGeoZone, allocationRuleId, priority))
              .build());
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Failed to process get Promise Sourcing Rule request!");
      throw e;
    }
  }

  @GetMapping("/{orgId}@oid")
  public ResponseEntity<BaseResponse<List<PromiseSourcingRuleDto>>> getPromiseSourcingRulesByOrgId(
      @NotBlank @PathVariable String orgId) throws PromiseEngineException {
    logger.info("Processing get Promise Sourcing Rules by orgId request");
    try {
      List<PromiseSourcingRuleDto> promiseSourcingRuleDtoList =
          promiseSourcingRuleService.getPromiseSourcingRulesByOrgId(orgId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Promise Sourcing Rules successfully fetched!")
              .payload(promiseSourcingRuleDtoList)
              .build());
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Failed to process get Promise Sourcing Rules request!");
      throw e;
    }
  }

  @GetMapping("/{priority}@pty")
  public ResponseEntity<BaseResponse<List<PromiseSourcingRuleDto>>>
      getPromiseSourcingRulesByPriority(@NotBlank @PathVariable int priority)
          throws PromiseEngineException {
    logger.info("Processing get Promise Sourcing Rules by priority request");
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Promise Sourcing Rules successfully fetched!")
              .payload(promiseSourcingRuleService.getPromiseSourcingRulesByPriority(priority))
              .build());
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Failed to process get Promise Sourcing Rules request!");
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
    logger.info("Processing update Promise Sourcing Rule request");
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Promise Sourcing Rule successfully updated!")
              .payload(
                  promiseSourcingRuleService.updatePromiseSourcingRule(
                      orgId,
                      serviceOption,
                      destinationGeoZone,
                      allocationRuleId,
                      priority,
                      baseRequest))
              .build());
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Failed to process update Promise Sourcing Rule request!");
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
    logger.info("Processing delete Promise Sourcing Rule request by sourcingRuleId");
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Promise Sourcing Rule successfully deleted!")
              .payload(
                  promiseSourcingRuleService.deletePromiseSourcingRule(
                      orgId, serviceOption, destinationGeoZone, allocationRuleId, priority))
              .build());
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Failed to process delete Promise Sourcing Rule request!");
      throw e;
    }
  }
}
