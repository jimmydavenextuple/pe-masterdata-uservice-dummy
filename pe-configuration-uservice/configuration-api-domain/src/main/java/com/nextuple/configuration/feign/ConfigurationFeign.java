/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.configuration.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.configuration.inbound.TenantConfigdataRequest;
import com.nextuple.configuration.inbound.TenantConfigdataUpdateRequest;
import com.nextuple.configuration.outbound.TenantConfigdataResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "pe-configuration-uservice",
    url = "${spring.application.dependencies.configuration:http://pe-configuration-uservice:8080/}")
public interface ConfigurationFeign {

  @GetMapping("/tenant-configuration/orgId/{orgId}/configKey/{configKey}")
  BaseResponse<TenantConfigdataResponse> getTenantConfigdataByOrgIdAndConfigKey(
      @NotBlank @PathVariable String orgId, @NotBlank @PathVariable String configKey);

  @PostMapping("/tenant-configuration")
  BaseResponse<TenantConfigdataResponse> addTenantConfigdata(
      @RequestBody TenantConfigdataRequest tenantConfigdataRequest);

  @PutMapping(value = "/tenant-configuration/orgId/{orgId}/configKey/{configKey}")
  BaseResponse<TenantConfigdataResponse> updateTenantConfigdata(
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
      @NotBlank(message = "configKey can't be empty") @PathVariable String configKey,
      @RequestBody TenantConfigdataUpdateRequest tenantConfigdataUpdateRequest);

  @DeleteMapping(value = "/tenant-configuration/orgId/{orgId}/configKey/{configKey}")
  BaseResponse<TenantConfigdataResponse> deleteTenantConfigdata(
      @NotBlank @PathVariable String orgId, @NotBlank @PathVariable String configKey);
}
