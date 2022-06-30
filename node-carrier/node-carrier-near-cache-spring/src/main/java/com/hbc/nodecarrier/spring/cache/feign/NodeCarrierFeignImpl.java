package com.hbc.nodecarrier.spring.cache.feign;

import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.service.GenericFeignService;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "pe-config-node-carrier",
    url = "${spring.application.dependencies.node-carrier:http://pe-config-node-carrier:8080/}")
public interface NodeCarrierFeignImpl
    extends GenericFeignService<String, BaseResponse<NodeCarrierResponse>> {

  @GetMapping("node/carrier/get")
  BaseResponse<NodeCarrierResponse> get(String request);

  @GetMapping("node/carrier/{nodeId}/{orgId}/{carrierServiceId}/{serviceOption}")
  BaseResponse<NodeCarrierResponse> getNodeCarrier(
      @PathVariable String nodeId,
      @PathVariable String orgId,
      @PathVariable String carrierServiceId,
      @PathVariable String serviceOption);
}
