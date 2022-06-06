package com.nextuple.node.feign;

import com.nextuple.controltower.common.base.BaseResponse;
import com.nextuple.controltower.common.base.PagePayload;
import com.nextuple.controltower.common.constants.CommonFulfillmentType;
import com.nextuple.core.node.NodeDetails;
import com.nextuple.core.node.ServiceProvider;
import com.nextuple.core.node.domain.*;
import com.nextuple.core.node.enums.FulfillmentTypeFilterModeEnum;
import com.nextuple.core.node.enums.NodeSortByFieldEnum;
import com.nextuple.core.node.enums.ServicePointSortByFieldEnum;
import com.nextuple.core.node.enums.SortOrderEnum;
import com.nextuple.core.node.inbound.*;
import com.nextuple.core.node.outbound.NodeValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
    name = "node-uservice",
    url = "${spring.application.dependencies.node:http://node-uservice:8080/node}")
public interface NodeServiceClient {

  @GetMapping("/nodes/{nodeNo}")
  BaseResponse<NodeDto> getNodeById(@PathVariable("orgId") String orgId, @PathVariable("nodeId") String nodeId);

}
