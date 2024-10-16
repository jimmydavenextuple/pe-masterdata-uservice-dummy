/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.dto.CostValueCacheKeyDto;
import com.nextuple.sourcing.cost.config.inbound.CreateCostValueRequest;
import com.nextuple.sourcing.cost.config.inbound.UpdateCostValueRequest;
import com.nextuple.sourcing.cost.config.outbound.CostValueResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-promise-sourcing-rule-uservice",
    url =
        "${spring.application.dependencies.promise-sourcing-rule:http://pe-promise-sourcing-rule-uservice:8080/}")
public interface CostValueFeign {
  @PostMapping("/cost-config/cost-value/{orgId}")
  BaseResponse<CostValueResponse> createCostValue(
      @NotNull @PathVariable String orgId,
      @Valid @RequestBody CreateCostValueRequest costValueRequest);

  @GetMapping("/cost-config/cost-value/{orgId}/{id}")
  BaseResponse<CostValueResponse> getCostValue(
      @NotNull @PathVariable String orgId, @NotNull @PathVariable Long id);

  @GetMapping("/cost-config/cost-value/get-cost/{orgId}/{costItinerary}")
  BaseResponse<CostValueResponse> getCostValueForCostFactorCombinationKey(
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String costItinerary,
      @RequestParam(required = false, defaultValue = "") String costFactorCombinationKey);

  @PutMapping("/cost-config/cost-value/{orgId}/{id}")
  BaseResponse<CostValueResponse> updateCostValue(
      @NotNull @PathVariable String orgId,
      @NotNull @PathVariable Long id,
      @Valid @RequestBody UpdateCostValueRequest costValueRequest);

  @DeleteMapping("/cost-config/cost-value/{orgId}/{id}")
  BaseResponse<CostValueResponse> deleteCostValue(
      @NotNull @PathVariable String orgId, @NotNull @PathVariable Long id);

  @DeleteMapping("/cost-config/cost-value/{orgId}/{costItinerary}/{costFactorCombinationKey}")
  BaseResponse<CostValueResponse> deleteCostValueCostFactorCombinationKey(
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String costItinerary,
      @NotBlank @PathVariable String costFactorCombinationKey);

  @GetMapping("/cost-config/cost-value/get-all-cache-keys")
  BaseResponse<List<CostValueCacheKeyDto>> getCostValueCacheKeys(
      @NotNull @RequestParam Integer limit);
}
