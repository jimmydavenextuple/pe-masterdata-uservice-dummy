package com.nextuple.carrier.spring.cache.feign;

import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
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

  @GetMapping("/carrierService/{carrierId}/{serviceId}/{orgId}")
  public BaseResponse<CarrierServiceResponse> getCarrier(
      @PathVariable String carrierId, @PathVariable String serviceId, @PathVariable String orgId);
}
