/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.weightage.configuration.api.domain.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.weightage.configuration.api.domain.dto.WeightageCacheKeyDto;
import com.nextuple.weightage.configuration.api.domain.dto.WeightageConfigurationDto;
import com.nextuple.weightage.configuration.api.domain.inbound.CreateWeightageConfigurationRequest;
import com.nextuple.weightage.configuration.api.domain.inbound.UpdateWeightageConfigurationRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-weightage-configuration-uservice",
    url =
        "${spring.application.dependencies.weightage-configuration:http://pe-weightage-configuration-uservice:8080/}")
public interface WeightageConfigurationFeign {

  @PostMapping("/weightage/create")
  BaseResponse<WeightageConfigurationDto> createWeightageConfiguration(
      @Valid @RequestBody CreateWeightageConfigurationRequest baseRequest);

  @GetMapping("/weightage")
  BaseResponse<WeightageConfigurationDto> getWeightageConfiguration(
      @NotBlank @RequestParam String orgId,
      @NotBlank @RequestParam String type,
      @NotBlank @RequestParam String key);

  @PutMapping("/weightage")
  BaseResponse<WeightageConfigurationDto> updateWeightageConfiguration(
      @NotBlank @RequestParam String orgId,
      @NotBlank @RequestParam String type,
      @NotBlank @RequestParam String key,
      @Valid @RequestBody UpdateWeightageConfigurationRequest baseRequest);

  @DeleteMapping("/weightage")
  BaseResponse<WeightageConfigurationDto> deleteWeightageConfiguration(
      @NotBlank @RequestParam String orgId,
      @NotBlank @RequestParam String type,
      @NotBlank @RequestParam String key);

  @GetMapping("/weightage/get-all-cache-keys")
  BaseResponse<List<WeightageCacheKeyDto>> getWeightageCacheKeys(
      @NotBlank @RequestParam Integer limit);
}
