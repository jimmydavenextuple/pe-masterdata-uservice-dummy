package com.nextuple.nodde.spring.cache.feign;

import com.nextuple.controltower.common.base.BaseResponse;
import com.nextuple.controltower.common.base.PagePayload;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.core.node.domain.NodeDto;
import com.nextuple.core.node.outbound.NodeValidationResponse;
import com.nextuple.node.cache.domain.NodeValidationRequest;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "node-uservice",
    url = "${spring.application.dependencies.node:http://node-uservice:8080/}")
public interface NodeFeignImpl
    extends GenericFeignService<NodeValidationRequest, BaseResponse<NodeValidationResponse>> {
  @GetMapping("/nodes")
  BaseResponse<PagePayload<NodeDto>> processGetAllNodes(
      @RequestParam("pageNo") int pageNo,
      @RequestParam("pageSize") int pageSize,
      @RequestParam("clusterList") List<String> clusterList);

  @GetMapping("/nodes/{nodeNo}")
  BaseResponse<NodeDto> handleGetNodeByNodeNo(@PathVariable("nodeNo") String nodeNo);

  @GetMapping("/nodes/{nodeNo}")
  BaseResponse<NodeDto> get(@PathVariable("nodeNo") String nodeNo);

  @PostMapping("/nodes/validate-node")
  BaseResponse<NodeValidationResponse> get(
      @RequestBody NodeValidationRequest nodeValidationRequest);
}
