package com.hbc.node.carrier.domain.feign;

import com.hbc.common.response.BaseResponse;
import com.hbc.node.carrier.domain.inbound.NodeCarrierBufferRequest;
import com.hbc.node.carrier.domain.inbound.NodeCarrierRequest;
import com.hbc.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import java.util.List;
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
    name = "pe-config-node-carrier",
    url = "${spring.application.dependencies.node-carrier:http://pe-config-node-carrier:8080/}")
public interface NodeCarrierFeign {

  @PostMapping("/node/carrier")
  BaseResponse<NodeCarrierResponse> createNodeCarrier(
      @Valid @RequestBody NodeCarrierRequest nodeCarrierRequest);

  @PutMapping("/node/carrier/{nodeId}/{orgId}/{carrierServiceId}/{serviceOption}")
  BaseResponse<NodeCarrierResponse> updateNodeCarrier(
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String carrierServiceId,
      @NotBlank @PathVariable String serviceOption,
      @Valid @RequestBody NodeCarrierUpdateRequest nodeCarrierUpdateRequest);

  @GetMapping("/{nodeId}/{orgId}/{carrierServiceId}/{serviceOption}")
  BaseResponse<NodeCarrierResponse> getNodeCarrier(
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String carrierServiceId,
      @NotBlank @PathVariable String serviceOption);

  @DeleteMapping("/node/carrier/{nodeId}/{orgId}/{carrierServiceId}/{serviceOption}")
  BaseResponse<NodeCarrierResponse> deleteNodeCarrier(
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String carrierServiceId,
      @NotBlank @PathVariable String serviceOption);

  @GetMapping("/node/carrier/{nodeId}/{orgId}")
  BaseResponse<List<NodeCarrierResponse>> getNodeCarrierList(
      @NotBlank @PathVariable String nodeId, @NotBlank @PathVariable String orgId);

  @PutMapping("/node/carrier/buffer")
  BaseResponse<NodeCarrierResponse> updateBuffer(
      @Valid @RequestBody NodeCarrierBufferRequest nodeCarrierBufferRequest);

  @PostMapping("/node/carrier/processing-lead-time")
  BaseResponse<NodeCarrierResponse> updateProcessingLeadTime(
      @Valid @RequestBody NodeCarrierRequest nodeCarrierRequest);
}
