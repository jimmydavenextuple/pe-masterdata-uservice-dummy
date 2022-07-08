package com.hbc.weightage.configuration.api.domain.feign;

import com.hbc.common.response.BaseResponse;
import com.hbc.weightage.configuration.api.domain.dto.WeightageConfigurationDto;
import com.hbc.weightage.configuration.api.domain.inbound.CreateWeightageConfigurationRequest;
import com.hbc.weightage.configuration.api.domain.inbound.UpdateWeightageConfigurationRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-config-weightage.configuration",
    url =
        "${spring.application.dependencies.weightage-configuration:http://pe-config-weightage-configuration:8080/}")
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
}
