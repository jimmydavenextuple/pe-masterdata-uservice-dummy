package com.hbc.carrier.domain.feign;

import com.hbc.carrier.domain.dto.CarrierCacheKeyDto;
import com.hbc.carrier.domain.inbound.CarrierServiceRequest;
import com.hbc.carrier.domain.inbound.CarrierServiceUpdateRequest;
import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import com.hbc.common.base.PagePayload;
import com.hbc.common.response.BaseResponse;
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
}
