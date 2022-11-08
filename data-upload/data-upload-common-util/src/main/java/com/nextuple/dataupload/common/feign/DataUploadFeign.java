package com.nextuple.dataupload.common.feign;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.common.outbound.NodeCarrierServiceAndServiceOptionResponse;
import com.nextuple.dataupload.common.outbound.ProcessingTimeBufferResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-config-data-upload-api",
    url = "${spring.application.dependencies.data-upload:http://pe-config-data-upload:8080/}")
public interface DataUploadFeign {

  @GetMapping("/ui/node-carrier-service-option/{orgId}")
  BaseResponse<PagePayload<NodeCarrierServiceAndServiceOptionResponse>>
      getListOfNodeCarrierServiceAndServiceOptionDetails(
          @NotEmpty @NotNull @PathVariable("orgId") String orgId,
          @RequestParam(required = false) Integer pageNo,
          @RequestParam(required = false) Integer pageSize,
          @RequestParam(required = false) String sortBy,
          @RequestParam(required = false) String sortOrder);

  @GetMapping("/ui/processing-time-buffer/orgId/{orgId}")
  BaseResponse<PagePayload<ProcessingTimeBufferResponse>> getProcessingTimeBufferDetails(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @RequestParam(required = false) Integer pageNo,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) String sortBy,
      @RequestParam(required = false) String sortOrder);
}
