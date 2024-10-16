/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.spring.cache.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.sourcing.cost.config.outbound.CostValueResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-promise-sourcing-rule-uservice",
    url =
        "${spring.application.dependencies.promise-sourcing-rule:http://pe-promise-sourcing-rule-uservice:8080/}")
public interface CostValueFeignImpl
    extends GenericFeignService<String, BaseResponse<CostValueResponse>> {

  @GetMapping("/cost-config/cost-value/{orgId}")
  BaseResponse<CostValueResponse> get(@PathVariable String orgId);

  @GetMapping("/cost-config/cost-value/get-cost/{orgId}/{costItinerary}")
  BaseResponse<CostValueResponse> getCostValueForCostFactorCombinationKey(
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String costItinerary,
      @RequestParam(required = false, defaultValue = "") String costFactorCombinationKey);
}
