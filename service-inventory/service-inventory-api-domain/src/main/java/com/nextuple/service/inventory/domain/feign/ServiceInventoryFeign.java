package com.nextuple.service.inventory.domain.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.service.inventory.domain.inbound.ServiceInventoryRequest;
import com.nextuple.service.inventory.domain.outbound.ServiceInventoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "pe-config-service-inventory",
    url =
        "${spring.application.dependencies.service-inventory:http://pe-config-service-inventory:8080/}")
public interface ServiceInventoryFeign {

  @PostMapping("/serviceOption/inventoryType")
  BaseResponse<ServiceInventoryDto> createServiceOptionInventoryType(
      @RequestBody ServiceInventoryRequest serviceToInventoryRequest);

  @GetMapping("/serviceOption/inventoryType/{orgId}/{serviceOption}")
  BaseResponse<ServiceInventoryDto> getServiceOptionToInventoryMapping(
      @PathVariable(name = "orgId") String orgId,
      @PathVariable(name = "serviceOption") String serviceOption);
}
