package com.hbc.node.data.spring.cache.feign;

import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.service.GenericFeignService;
import com.hbc.node.domain.outbound.NodeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "pe-config-node",
    url = "${spring.application.dependencies.node:http://pe-config-node:8080/}")
public interface NodeDataFeignImpl extends GenericFeignService<String, BaseResponse<NodeResponse>> {

  @GetMapping("/node/{nodeId}")
  BaseResponse<NodeResponse> get(@PathVariable String nodeId);

  @GetMapping("/node/{nodeId}/{orgId}")
  BaseResponse<NodeResponse> getNodeDetails(
      @PathVariable String nodeId, @PathVariable String orgId);
}
