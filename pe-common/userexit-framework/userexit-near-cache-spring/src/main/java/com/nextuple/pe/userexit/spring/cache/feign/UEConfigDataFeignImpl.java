/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.userexit.spring.cache.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.userexit.domain.dto.UserExitConfigDataDto;
import com.nextuple.core.cache.service.GenericFeignService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "userexit-uservice",
    url = "${spring.application.dependencies.userexit:http://pe-data-upload-uservice:8080/}")
public interface UEConfigDataFeignImpl
    extends GenericFeignService<String, BaseResponse<UserExitConfigDataDto>> {

  @GetMapping("/user-exit/config/get")
  BaseResponse<UserExitConfigDataDto> get(String request);

  @GetMapping("user-exit/config/{orgId}/{appName}/{serviceName}/{userExitName}")
  BaseResponse<UserExitConfigDataDto> fetchConfigData(
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String appName,
      @NotBlank @PathVariable String serviceName,
      @NotBlank @PathVariable String userExitName);
}
