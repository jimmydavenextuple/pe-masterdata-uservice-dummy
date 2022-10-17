package com.hbc.transit.domain.feign;

import com.hbc.common.response.BaseResponse;
import com.hbc.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import com.hbc.transit.domain.inbound.TransitBufferConfigRequest;
import com.hbc.transit.domain.outbound.TransitBufferConfigResponse;
import java.util.List;
import javax.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-config-transit",
    url = "${spring.application.dependencies.transit:http://pe-config-transit:8080/}")
public interface TransitBufferConfigRequestFeign {

  @PostMapping("/transit/buffer-config-request")
  BaseResponse<TransitBufferConfigResponse> createTransitBufferConfigRequest(
      @Valid @RequestBody TransitBufferConfigRequest transitBufferConfigRequest);

  @PutMapping("/transit/buffer-config-request/update-status/{id}")
  BaseResponse<TransitBufferConfigResponse> updateTransitBufferConfigRequestStatus(
      @PathVariable(name = "id") Long id,
      @RequestParam(name = "status") TransitBufferConfigRequestStatusEnum status);

  @GetMapping("/transit/buffer-config-request/{orgId}")
  BaseResponse<List<TransitBufferConfigResponse>> getTransitBufferConfigRequests(
      @PathVariable(name = "orgId") String orgId,
      @RequestParam(name = "carrierServiceId") String carrierServiceId);
}
