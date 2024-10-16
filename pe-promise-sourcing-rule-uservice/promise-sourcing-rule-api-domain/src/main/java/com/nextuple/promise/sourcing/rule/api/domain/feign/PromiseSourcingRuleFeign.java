/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.api.domain.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.dto.PromiseSourcingRuleDto;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.CreatePromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.UpdatePromiseSourcingRuleRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-promise-sourcing-rule-uservice",
    url =
        "${spring.application.dependencies.promise-sourcing-rule:http://pe-promise-sourcing-rule-uservice:8080/}")
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
