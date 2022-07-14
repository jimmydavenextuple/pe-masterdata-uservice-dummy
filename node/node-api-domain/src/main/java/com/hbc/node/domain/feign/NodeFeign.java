package com.hbc.node.domain.feign;

import com.hbc.common.response.BaseResponse;
import com.hbc.node.domain.inbound.NodeRequest;
import com.hbc.node.domain.inbound.NodeUpdationRequest;
import com.hbc.node.domain.outbound.NodeResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "pe-config-node",
    url = "${spring.application.dependencies.node:http://pe-config-node:8080/}")
public interface NodeFeign {

  @PostMapping("/node")
  BaseResponse<NodeResponse> createNode(@Valid @RequestBody NodeRequest nodeRequest);

  @PutMapping("/node/{nodeId}/{orgId}")
  BaseResponse<NodeResponse> updateNodeDetails(
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String orgId,
      @Valid @RequestBody NodeUpdationRequest nodeUpdationRequest);

  @GetMapping("/node/{nodeId}/{orgId}")
  BaseResponse<NodeResponse> getNodeDetails(
      @NotBlank @PathVariable String nodeId, @NotBlank @PathVariable String orgId);

  @DeleteMapping("/node/{nodeId}/{orgId}")
  BaseResponse<NodeResponse> deleteNode(
      @NotBlank @PathVariable String nodeId, @NotBlank @PathVariable String orgId);
}
