package com.nextuple.postalcodecarrierservice.spring.cache.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.postalcodecarrierservice.domain.outbound.PostalCodeCarrierServiceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "pe-postal-code-carrier-service-uservice",
    url =
        "${spring.application.dependencies.postalcode-carrier:http://pe-postal-code-carrier-service-uservice:8080/}")
public interface PostalCodeCarrierServiceFeignImpl
    extends GenericFeignService<String, BaseResponse<PostalCodeCarrierServiceResponse>> {
  @GetMapping("/api/v1/postal-code-carrier-service/{zipcode}/{carrierServiceId}")
  PostalCodeCarrierServiceResponse getPostalCodeCarrierService(
      @PathVariable String zipcode, @PathVariable String carrierServiceId);

  @GetMapping("/{orgId}")
  BaseResponse<PostalCodeCarrierServiceResponse> get(@PathVariable String orgId);
}
