package com.nextuple.node.data.spring.cache.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.node.data.cache.domain.NodeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "node-uservice",
    url = "${spring.application.dependencies.node:http://localhost:8080/}")
public interface NodeDataFeignImpl extends GenericFeignService<String, BaseResponse<NodeResponse>> {

  @GetMapping("/node/{nodeId}")
  BaseResponse<NodeResponse> get(@PathVariable String nodeId);

  @GetMapping("/node/{nodeId}/{orgId}")
  BaseResponse<NodeResponse> getNodeDetails(
      @PathVariable String nodeId, @PathVariable String orgId);
}
