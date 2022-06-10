package com.nextuple.node.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.domain.NodeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
    name = "node-uservice",
    url = "${spring.application.dependencies.node:http://node-uservice:8080/node}")
public interface NodeServiceClient {

  @GetMapping("/nodes/{nodeNo}")
  BaseResponse<NodeResponse> getNodeById(@PathVariable("orgId") String orgId, @PathVariable("nodeId") String nodeId);

}
