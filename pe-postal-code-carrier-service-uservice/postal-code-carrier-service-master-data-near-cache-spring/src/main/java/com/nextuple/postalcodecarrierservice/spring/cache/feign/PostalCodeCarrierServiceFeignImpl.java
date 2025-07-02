package com.nextuple.postalcodecarrierservice.spring.cache.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.postalcodecarrierservice.domain.outbound.PostalCodeCarrierServiceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "pe-postalcode-carrier-uservice",
    url =
        "${spring.application.dependencies.postalcode-carrier:http://pe-postalcode-carrier-uservice:8080/}")
public interface PostalCodeCarrierServiceFeignImpl
    extends GenericFeignService<String, BaseResponse<PostalCodeCarrierServiceResponse>> {
  @GetMapping("/api/v1/postal-code-carrier-service/{zipcode}/{carrierServiceId}")
  BaseResponse<PostalCodeCarrierServiceResponse> getPostalCodeCarrierService(
      @PathVariable String zipcode, @PathVariable String carrierServiceId);

  @GetMapping("/{orgId}")
  BaseResponse<PostalCodeCarrierServiceResponse> get(@PathVariable String orgId);
}
