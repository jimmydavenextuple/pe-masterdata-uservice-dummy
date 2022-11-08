package com.nextuple.transit.domain.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.transit.domain.inbound.TransitBufferRequest;
import com.nextuple.transit.domain.outbound.TransitBufferResponse;
import java.util.List;
import javax.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-config-transit",
    url = "${spring.application.dependencies.transit:http://pe-config-transit:8080/}")
public interface TransitBufferFeign {

  @PostMapping("/transit/v1/buffer")
  BaseResponse<TransitBufferResponse> createTransitBuffer(
      @Valid @RequestBody TransitBufferRequest transitBufferRequest);

  @GetMapping("/transit/v1/buffer/org/{orgId}")
  BaseResponse<List<TransitBufferResponse>> getByOrgIdAndDestinationGeozone(
      @PathVariable(name = "orgId") String orgId,
      @RequestParam(name = "destinationGeozone") String destinationGeozone);

  @PutMapping("/transit/v1/buffer")
  BaseResponse<TransitBufferResponse> updateTransitBuffer(
      @Valid @RequestBody TransitBufferRequest transitBufferRequest);

  @DeleteMapping("/transit/v1/buffer")
  BaseResponse<TransitBufferResponse> deleteTransitBufferDetails(
      @Valid @RequestBody TransitBufferRequest transitBufferRequest);
}
