package com.nextuple.carrier.spring.cache.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.domain.carrier.CarrierServiceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "pe-config-master-data",
    url = "${spring.application.dependencies.carrier:http://pe-config-master-data:8080/}")
public interface CarrierFeignImpl
    extends GenericFeignService<String, BaseResponse<CarrierServiceResponse>> {

  @GetMapping("/carrier/get")
  BaseResponse<CarrierServiceResponse> get(String request);

  @GetMapping("/carrierService/{carrierId}/{serviceId}/{orgId}")
  public BaseResponse<CarrierServiceResponse> getCarrier(
      @PathVariable String carrierId, @PathVariable String serviceId, @PathVariable String orgId);
}
