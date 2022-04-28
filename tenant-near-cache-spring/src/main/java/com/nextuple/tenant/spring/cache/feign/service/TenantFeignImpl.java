package com.nextuple.tenant.spring.cache.feign.service;

import com.nextuple.controltower.common.base.BaseResponse;
import com.nextuple.core.cache.service.GenericFeignService;
import com.nextuple.core.tenant.domain.TenantDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "tenant-uservice",
    url = "${spring.application.dependencies.tenant:http://tenant-uservice:8080/}")
public interface TenantFeignImpl extends GenericFeignService<String, BaseResponse<TenantDto>> {
  @GetMapping("/tenant/{id}")
  BaseResponse<TenantDto> get(@PathVariable(name = "id") String id);

  @GetMapping("/tenant/{id}")
  BaseResponse<TenantDto> getTenantById(@PathVariable(name = "id") String id);
}
