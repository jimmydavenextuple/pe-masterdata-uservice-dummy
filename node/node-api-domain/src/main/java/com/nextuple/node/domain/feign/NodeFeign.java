package com.nextuple.node.domain.feign;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.domain.dto.NodeCacheKeyDto;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.node.domain.inbound.NodeRequest;
import com.nextuple.node.domain.inbound.NodeUpdationRequest;
import com.nextuple.node.domain.outbound.NodeResponse;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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

  @GetMapping("/node/{orgId}")
  BaseResponse<PagePayload<NodeDto>> getNodeList(
      @NotBlank @PathVariable String orgId,
      @RequestParam(required = false) Integer pageNo,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) String sortBy,
      @RequestParam(required = false) String sortOrder);

  @GetMapping("/node/all-nodes")
  BaseResponse<PagePayload<NodeResponse>> getAllNodesList(
      @RequestParam(required = false) Integer pageNo,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) String sortBy,
      @RequestParam(required = false) String sortOrder);

  @GetMapping("/node/get-all-cache-keys")
  BaseResponse<List<NodeCacheKeyDto>> getNodeCacheKeys(@NotNull @RequestParam Integer limit);
}
