package com.nextuple.nodecarrier.spring.cache.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "promising-sourcing-rule",
    url = "${spring.application.dependencies.node-carrier:http://promising-sourcing-rule:8080/}")
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
