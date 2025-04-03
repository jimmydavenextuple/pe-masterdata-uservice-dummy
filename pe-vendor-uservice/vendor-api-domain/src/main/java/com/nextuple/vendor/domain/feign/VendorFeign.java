package com.nextuple.vendor.domain.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.vendor.domain.inbound.VendorRequest;
import com.nextuple.vendor.domain.inbound.VendorUpdationRequest;
import com.nextuple.vendor.domain.outbound.VendorResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "pe-vendor-uservice",
    url = "${spring.application.dependencies.vendor:http://pe-vendor-uservice:8080/}")
public interface VendorFeign {

  @PostMapping("/vendor")
  BaseResponse<VendorResponse> createVendor(@Valid @RequestBody VendorRequest vendorRequest);

  @PutMapping("/vendor/{vendorId}/{orgId}")
  BaseResponse<VendorResponse> updateVendorDetails(
      @NotBlank @PathVariable String vendorId,
      @NotBlank @PathVariable String orgId,
      @Valid @RequestBody VendorUpdationRequest vendorUpdationRequest);

  @GetMapping("/node/{nodeId}/{orgId}")
  BaseResponse<VendorResponse> getVendorDetails(
      @NotBlank @PathVariable String nodeId, @NotBlank @PathVariable String orgId);

  @DeleteMapping("/node/{nodeId}/{orgId}")
  BaseResponse<VendorResponse> deleteVendor(
      @NotBlank @PathVariable String nodeId, @NotBlank @PathVariable String orgId);
}
