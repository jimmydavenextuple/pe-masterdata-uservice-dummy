/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.spring.cache.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.transit.domain.outbound.TransitBufferDetailsResponse;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-transit-uservice",
    url = "${spring.application.dependencies.transit:http://pe-transit-uservice:8080/}")
public interface TransitBufferV2FeignImpl
    extends GenericFeignService<String, BaseResponse<List<TransitBufferDetailsResponse>>> {
  @GetMapping("/v2/transit/buffer/get")
  BaseResponse<List<TransitBufferDetailsResponse>> get(String request);

  @GetMapping("/v2/transit/buffer/{orgId}/{destinationGeozone}")
  BaseResponse<List<TransitBufferDetailsResponse>> getTransitBuffersByOrgIdAndDestinationGeozone(
      @PathVariable String orgId,
      @PathVariable String destinationGeozone,
      @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate requestDate,
      @Min(value = 0, message = "horizonDays can't be negative") @RequestParam Integer horizonDays);
}
