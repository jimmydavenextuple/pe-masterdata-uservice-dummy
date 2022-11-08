package com.nextuple.transit.domain.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.transit.domain.inbound.TransitBufferReqJobRefRequest;
import com.nextuple.transit.domain.outbound.TransitBufferReqJobRefResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "pe-config-transit",
    url = "${spring.application.dependencies.transit:http://pe-config-transit:8080/}")
public interface TransitBufferReqJobRefFeign {
  @PostMapping("/transit/transit-buffer-req-jobs-reference")
  BaseResponse<TransitBufferReqJobRefResponse> addTransitBufferReqJobRefData(
      @RequestBody TransitBufferReqJobRefRequest transitBufferReqJobRefRequest);

  @GetMapping("/transit/transit-buffer-req-jobs-reference/{extReferenceId}")
  BaseResponse<TransitBufferReqJobRefResponse> getTransitBufferReqJobRefByExtRefId(
      @PathVariable(name = "extReferenceId") String extReferenceId);
}
