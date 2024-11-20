/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.api.domain.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.NamedOptimizationStrategyRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.NamedOptimizationStrategyUpdationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NamedOptimizationStrategyResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "pe-promise-sourcing-rule-uservice",
    url =
        "${spring.application.dependencies.promise-sourcing-rule:http://pe-promise-sourcing-rule-uservice:8080/}")
public interface NamedOptimizationStrategyFeign {

  @PostMapping("/named-optimization-strategy")
  BaseResponse<NamedOptimizationStrategyResponse> addOptimizationStrategy(
      @Valid @RequestBody NamedOptimizationStrategyRequest namedOptimizationStrategyRequest);

  @GetMapping("/named-optimization-strategy/orgId/{orgId}/id/{id}")
  BaseResponse<NamedOptimizationStrategyResponse> getOptimizationStrategyByOrgIdAndId(
      @NotBlank @PathVariable String orgId, @NotNull @PathVariable Long id);

  @GetMapping("/named-optimization-strategy/orgId/{orgId}/groupId/{groupId}")
  BaseResponse<NamedOptimizationStrategyResponse> getOptimizationStrategyByOrgIdAndGroupId(
      @NotBlank @PathVariable String orgId, @NotBlank @PathVariable String groupId);

  @PutMapping("/named-optimization-strategy/orgId/{orgId}/groupId/{groupId}")
  BaseResponse<NamedOptimizationStrategyResponse> updateOptimizationStrategy(
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String groupId,
      @Valid @RequestBody
          NamedOptimizationStrategyUpdationRequest namedOptimizationStrategyUpdationRequest);

  @DeleteMapping("/named-optimization-strategy/orgId/{orgId}/groupId/{groupId}")
  BaseResponse<NamedOptimizationStrategyResponse> deleteOptimizationStrategy(
      @NotBlank @PathVariable String orgId, @NotBlank @PathVariable String groupId);
}
