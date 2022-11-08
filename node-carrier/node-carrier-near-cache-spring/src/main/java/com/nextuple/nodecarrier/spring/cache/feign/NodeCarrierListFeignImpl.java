package com.nextuple.nodecarrier.spring.cache.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "pe-config-node-carrier",
    url = "${spring.application.dependencies.node-carrier:http://pe-config-node-carrier:8080/}")
public interface NodeCarrierListFeignImpl
    extends GenericFeignService<String, BaseResponse<List<NodeCarrierResponse>>> {

  @GetMapping("node/carrier/get")
  BaseResponse<List<NodeCarrierResponse>> get(String request);

  @GetMapping("node/carrier/{nodeId}/{orgId}/{serviceOption}")
  BaseResponse<List<NodeCarrierResponse>> getNodeCarrierList(
      @PathVariable String nodeId, @PathVariable String orgId, @PathVariable String serviceOption);
}
