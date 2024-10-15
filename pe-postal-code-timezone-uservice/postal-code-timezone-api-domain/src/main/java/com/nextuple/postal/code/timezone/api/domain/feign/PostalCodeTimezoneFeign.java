/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.api.domain.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.postal.code.timezone.api.domain.dto.MarketRegionInfo;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
    name = "pe-postal-code-timezone-uservice",
    url =
        "${spring.application.dependencies.postal-code-timezone:http://pe-postal-code-timezone-uservice:8080/}")
public interface PostalCodeTimezoneFeign {
  @PostMapping("/postalCodeTimezone")
  BaseResponse<PostalCodeTimezoneDto> createPostalCodeTimezone(
      @Valid @RequestBody CreatePostalCodeTimezoneRequest baseRequest);

  @GetMapping("/postalCodeTimezone")
  BaseResponse<PostalCodeTimezoneDto> getPostalCodeTimezone(
      @NotBlank @RequestParam String orgId, @NotBlank @RequestParam String postalCodePrefix);

  @PutMapping("/postalCodeTimezone")
  BaseResponse<PostalCodeTimezoneDto> updatePostalCodeTimezone(
      @NotBlank @RequestParam String orgId,
      @NotBlank @RequestParam String postalCodePrefix,
      @Valid @RequestBody UpdatePostalCodeTimezoneRequest baseRequest);

  @DeleteMapping("/postalCodeTimezone")
  BaseResponse<PostalCodeTimezoneDto> deletePostalCodeTimezone(
      @NotBlank @RequestParam String orgId, @NotBlank @RequestParam String postalCodePrefix);

  @GetMapping("/postalCodeTimezone/org/{orgId}")
  BaseResponse<List<String>> getPostalCodePrefixForOrgIdAndState(
      @PathVariable String orgId, @RequestParam String state);

  @GetMapping("/postalCodeTimezone/market-regions/org/{orgId}")
  BaseResponse<List<MarketRegionInfo>> getMarketRegionsForOrgId(@PathVariable String orgId);

  @GetMapping("/postalCodeTimezone/market-region/org/{orgId}")
  BaseResponse<List<PostalCodeTimezoneDto>> getPostalCodeTimeZoneForOrgIdAndCountry(
      @NotBlank @RequestParam String orgId, @NotBlank @RequestParam String country);
}
