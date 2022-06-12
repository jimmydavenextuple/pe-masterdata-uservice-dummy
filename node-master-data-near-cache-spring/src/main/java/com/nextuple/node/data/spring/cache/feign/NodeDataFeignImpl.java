package com.nextuple.node.data.spring.cache.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.domain.node.NodeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "pe-config-master-data",
    url = "${spring.application.dependencies.node:http://pe-config-master-data:8080/}")
public interface NodeDataFeignImpl extends GenericFeignService<String, BaseResponse<NodeResponse>> {

  @GetMapping("/node/{nodeId}")
  BaseResponse<NodeResponse> get(@PathVariable String nodeId);

  @GetMapping("/node/{nodeId}/{orgId}")
  BaseResponse<NodeResponse> getNodeDetails(
      @PathVariable String nodeId, @PathVariable String orgId);
}
