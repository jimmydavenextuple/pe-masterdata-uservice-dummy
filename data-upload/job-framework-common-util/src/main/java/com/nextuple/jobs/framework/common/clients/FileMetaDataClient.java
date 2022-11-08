package com.nextuple.jobs.framework.common.clients;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.nextuple.jobs.framework.common.inbound.FileMetaDataCreationRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "pe-config-jobs-dashboard",
    url = "${spring.application.dependencies.data-upload:http://pe-config-data-upload:8080/}")
public interface FileMetaDataClient {

  @PostMapping("/file-metadata")
  BaseResponse<FileMetaDataResponse> createFileMetadata(
      @Valid @RequestBody FileMetaDataCreationRequest fileMetadataCreationRequest);

  @GetMapping("/file-metadata/{id}")
  BaseResponse<FileMetaDataResponse> findFileMetadataById(@NotBlank @PathVariable("id") Long id);
}
