package com.hbc.carrier.domain.feign;

import com.hbc.carrier.domain.inbound.CarrierServiceRequest;
import com.hbc.carrier.domain.inbound.CarrierServiceUpdateRequest;
import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import com.hbc.common.response.BaseResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "pe-config-carrier",
    url = "${spring.application.dependencies.carrier:http://pe-config-carrier:8080/}")
public interface CarrierFeign {
  @PostMapping("/carrier-service")
  BaseResponse<CarrierServiceResponse> createCarrierService(
      @Valid @RequestBody CarrierServiceRequest carrierServiceRequest);

  @GetMapping("/carrier-service/{carrierId}/{carrierServiceId}/{orgId}")
  BaseResponse<CarrierServiceResponse> getCarrierServiceDetails(
      @NotBlank @PathVariable String carrierId,
      @NotBlank @PathVariable String carrierServiceId,
      @NotBlank @PathVariable String orgId);

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
}
