package com.hbc.jobs.framework.common.clients;

import com.hbc.common.response.BaseResponse;
import com.hbc.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import javax.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "pe-config-jobs-dashboard",
    url = "${spring.application.dependencies.data-upload:http://pe-config-data-upload:8080/}")
public interface FileMetaDataClient {

  @GetMapping("/file-metadata/{id}")
  BaseResponse<FileMetaDataResponse> findFileMetadataById(@NotBlank @PathVariable("id") Long id);
}
