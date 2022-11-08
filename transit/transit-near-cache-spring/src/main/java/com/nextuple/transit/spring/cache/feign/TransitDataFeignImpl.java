package com.nextuple.transit.spring.cache.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.transit.domain.outbound.TransitResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "pe-config-transit",
    url = "${spring.application.dependencies.transit:http://pe-config-transit:8080/}")
public interface TransitDataFeignImpl
    extends GenericFeignService<String, BaseResponse<List<TransitResponse>>> {
  @GetMapping("/transit/get")
  BaseResponse<List<TransitResponse>> get(String request);

  @GetMapping("/transit/batch-transit/{orgId}/{destinationGeozone}")
  BaseResponse<List<TransitResponse>> getTransitDetailsListForDestinationGeoZone(
      @PathVariable String orgId, @PathVariable String destinationGeozone);
}
