package com.hbc.transit.spring.cache.feign;

import com.hbc.common.response.BaseResponse;
import com.hbc.transit.domain.outbound.TransitResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "pe-config-transit",
        url = "${spring.application.dependencies.transit:http://pe-config-transit:8080/}")
public interface TransitDataFeignImpl {

    @GetMapping("/transit/{orgId}/{destinationGeozone}")
    BaseResponse<List<TransitResponse>> get(@PathVariable String orgId, @PathVariable String destinationGeozone);
}