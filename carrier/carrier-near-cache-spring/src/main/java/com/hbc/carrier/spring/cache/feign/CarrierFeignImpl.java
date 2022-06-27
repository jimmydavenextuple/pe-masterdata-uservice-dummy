package com.hbc.carrier.spring.cache.feign;

import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.service.GenericFeignService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "pe-config-carrier",
    url = "${spring.application.dependencies.carrier:http://pe-config-carrier:8080/}")
public interface CarrierFeignImpl
    extends GenericFeignService<String, BaseResponse<CarrierServiceResponse>> {

  @GetMapping("/carrier/get")
  BaseResponse<CarrierServiceResponse> get(String request);

  @GetMapping("/carrier-service/{carrierId}/{carrierServiceId}/{orgId}")
  public BaseResponse<CarrierServiceResponse> getCarrier(
      @PathVariable String carrierId,
      @PathVariable String carrierServiceId,
      @PathVariable String orgId);
}
