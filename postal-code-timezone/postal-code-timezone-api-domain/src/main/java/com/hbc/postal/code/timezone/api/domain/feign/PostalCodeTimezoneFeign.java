package com.hbc.postal.code.timezone.api.domain.feign;

import com.hbc.common.response.BaseResponse;
import com.hbc.postal.code.timezone.api.domain.dto.MarketRegionDto;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.hbc.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.hbc.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-config-postal-code-timezone",
    url =
        "${spring.application.dependencies.postal-code-timezone:http://pe-config-postal-code-timezone:8080/}")
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
  BaseResponse<List<MarketRegionDto>> getMarketRegionsForOrgId(@PathVariable String orgId);

  @GetMapping("/postalCodeTimezone/market-region/org/{orgId}")
  BaseResponse<List<PostalCodeTimezoneDto>> getPostalCodeTimeZoneForOrgIdAndCountry(
      @NotBlank @RequestParam String orgId, @NotBlank @RequestParam String country);
}
