/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.carrier.domain.feign;

import com.nextuple.carrier.domain.dto.CarrierCacheKeyDto;
import com.nextuple.carrier.domain.inbound.CarrierServiceRequest;
import com.nextuple.carrier.domain.inbound.CarrierServiceUpdateRequest;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.base.PagePayload;
import com.nextuple.common.response.BaseResponse;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-carrier-uservice",
    url = "${spring.application.dependencies.carrier:http://pe-carrier-uservice:8080/}")
public interface CarrierFeign {
  @PostMapping("/carrier-service")
  BaseResponse<CarrierServiceResponse> createCarrierService(
      @Valid @RequestBody CarrierServiceRequest carrierServiceRequest);

  @GetMapping("/carrier-service/{carrierId}/{carrierServiceId}/{orgId}")
  BaseResponse<CarrierServiceResponse> getCarrierServiceDetails(
      @NotBlank @PathVariable String carrierId,
      @NotBlank @PathVariable String carrierServiceId,
      @NotBlank @PathVariable String orgId);

  @GetMapping("/carrier-service/{carrierServiceId}/{orgId}")
  BaseResponse<List<CarrierServiceResponse>> getCarrierServiceDetailsByCarrierServiceIdAndOrgId(
      @NotBlank @PathVariable String carrierServiceId, @NotBlank @PathVariable String orgId);

  @PutMapping("/carrier-service/{carrierId}/{carrierServiceId}/{orgId}")
  BaseResponse<CarrierServiceResponse> updateCarrierServiceDetails(
      @NotBlank @PathVariable String carrierId,
      @NotBlank @PathVariable String carrierServiceId,
      @NotBlank @PathVariable String orgId,
      @Valid @RequestBody CarrierServiceUpdateRequest carrierServiceUpdateRequest);

  @DeleteMapping("/carrier-service/{carrierId}/{carrierServiceId}/{orgId}")
  BaseResponse<CarrierServiceResponse> deleteCarrierService(
      @NotBlank @PathVariable String carrierId,
      @NotBlank @PathVariable String carrierServiceId,
      @NotBlank @PathVariable String orgId);

  @GetMapping("/carrier-service/{orgId}")
  BaseResponse<PagePayload<CarrierServiceResponse>> getCarrierServiceListWithPagination(
      @PathVariable String orgId,
      @RequestParam(required = false) Integer pageNo,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) String sortBy,
      @RequestParam(required = false) String sortOrder);

  @GetMapping("/carrier-service/get-all-cache-keys")
  BaseResponse<List<CarrierCacheKeyDto>> getCarrierCacheKeys(@NotNull @RequestParam Integer limit);

  @GetMapping("/carrier-service/orgId/{orgId}")
  BaseResponse<List<CarrierServiceResponse>> getCarrierServiceListByOrgId(
      @PathVariable String orgId);
}
