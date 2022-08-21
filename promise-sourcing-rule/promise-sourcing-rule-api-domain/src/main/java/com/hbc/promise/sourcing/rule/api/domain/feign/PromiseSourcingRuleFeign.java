package com.hbc.promise.sourcing.rule.api.domain.feign;

import com.hbc.common.response.BaseResponse;
import com.hbc.promise.sourcing.rule.api.domain.dto.PromiseSourcingRuleDto;
import com.hbc.promise.sourcing.rule.api.domain.inbound.CreatePromiseSourcingRuleRequest;
import com.hbc.promise.sourcing.rule.api.domain.inbound.UpdatePromiseSourcingRuleRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-config-promise-sourcing-rule",
    url =
        "${spring.application.dependencies.promise-sourcing-rule:http://pe-config-promise-sourcing-rule:8080/}")
public interface PromiseSourcingRuleFeign {

  @PostMapping("/promise-sourcing-rule")
  BaseResponse<PromiseSourcingRuleDto> createPromiseSourcingRule(
      @Valid @RequestBody CreatePromiseSourcingRuleRequest baseRequest);

  @GetMapping("/promise-sourcing-rule")
  BaseResponse<PromiseSourcingRuleDto> getPromiseSourcingRule(
      @NotBlank @RequestParam String orgId,
      @NotBlank @RequestParam String serviceOption,
      @NotBlank @RequestParam String destinationGeoZone,
      @NotBlank @RequestParam String allocationRuleId,
      @NotBlank @RequestParam int priority);

  @PutMapping("/promise-sourcing-rule")
  BaseResponse<PromiseSourcingRuleDto> updatePromiseSourcingRule(
      @NotBlank @RequestParam String orgId,
      @NotBlank @RequestParam String serviceOption,
      @NotBlank @RequestParam String destinationGeoZone,
      @NotBlank @RequestParam String allocationRuleId,
      @NotBlank @RequestParam int priority,
      @Valid @RequestBody UpdatePromiseSourcingRuleRequest baseRequest);

  @DeleteMapping("/promise-sourcing-rule")
  BaseResponse<PromiseSourcingRuleDto> deletePromiseSourcingRule(
      @NotBlank @RequestParam String orgId,
      @NotBlank @RequestParam String serviceOption,
      @NotBlank @RequestParam String destinationGeoZone,
      @NotBlank @RequestParam String allocationRuleId,
      @NotBlank @RequestParam int priority);
}
