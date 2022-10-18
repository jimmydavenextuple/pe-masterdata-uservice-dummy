package com.hbc.node.carrier.domain.feign;

import com.hbc.common.response.BaseResponse;
import com.hbc.node.carrier.domain.dto.NodeCarrierListCacheKeyDto;
import com.hbc.node.carrier.domain.inbound.NodeCarrierBufferRequest;
import com.hbc.node.carrier.domain.inbound.NodeCarrierRequest;
import com.hbc.node.carrier.domain.inbound.NodeCarrierSelectionRequest;
import com.hbc.node.carrier.domain.inbound.NodeCarrierUpdateRequest;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.node.carrier.domain.outbound.NodeCarrierSelectionResponse;
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

  @GetMapping("/node/carrier/node-carrier-selection/{orgId}/{serviceOption}/{destinationGeozone}")
  BaseResponse<List<NodeCarrierSelectionResponse>> getNodeCarrierSelectionDetails(
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String serviceOption,
      @NotBlank @PathVariable String destinationGeozone);

  @PostMapping("/node/carrier/node-carrier-selection")
  BaseResponse<NodeCarrierSelectionResponse> addNodeCarrierSelectionPriority(
      @Valid @RequestBody NodeCarrierSelectionRequest nodeCarrierSelectionRequest);

  @DeleteMapping("/node/carrier/{nodeId}/{orgId}/{serviceOption}")
  BaseResponse<NodeCarrierResponse> deleteNodeCarrierByOrgIdNodeIdAndServiceOption(
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String orgId,
      @RequestParam String carrierServiceId,
      @NotBlank @PathVariable String serviceOption);

  @GetMapping("/node/carrier/{nodeId}/{orgId}/{serviceOption}")
  BaseResponse<List<NodeCarrierResponse>> getNodeCarrierListForServiceOption(
      @NotBlank @PathVariable String nodeId,
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String serviceOption);

  @GetMapping("/node/carrier/get-all-cache-keys")
  BaseResponse<List<NodeCarrierListCacheKeyDto>> getNodeCarrierListCacheKeys(
      @NotNull @RequestParam Integer limit);

  @DeleteMapping("/node/carrier/node-carrier-selection")
  BaseResponse<NodeCarrierSelectionResponse> deleteNodeCarrierSelectionDetails(
      @Valid @RequestBody NodeCarrierSelectionRequest nodeCarrierSelectionRequest);

  @GetMapping("/node/carrier/{orgId}/{nodeId}/carrier-service")
  BaseResponse<List<String>> getUniqueNodeCarrierServiceList(
      @NotBlank @PathVariable String orgId, @NotBlank @PathVariable String nodeId);

  @GetMapping("/node/carrier/v1/{nodeId}/{orgId}")
  BaseResponse<List<NodeCarrierResponse>> getNodeCarrierListWithLastPickUpTimeDetails(
      @NotBlank @PathVariable String nodeId, @NotBlank @PathVariable String orgId);
}
