package com.hbc.jobs.framework.common.clients;

import com.hbc.common.base.PagePayload;
import com.hbc.common.response.BaseResponse;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-config-jobs-consumer",
    url = "${spring.application.dependencies.job.framework:http://pe-config-data-upload:8080/}")
public interface JobsConsumerClient {
  @PostMapping("/jobs")
  BaseResponse<JobDto> createJob(@Valid @RequestBody JobDto jobDto);

  @GetMapping("/org/{orgId}/jobs/{jobId}")
  BaseResponse<JobDto> getJob(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @NotEmpty @NotNull @PathVariable("jobId") String jobId);

  @GetMapping("/org/{orgId}/jobs")
  BaseResponse<PagePayload<JobDto>> getJobsByFilter(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @RequestParam(name = "jobType", required = false) String jobType,
      @RequestParam(name = "days", required = false) Integer days,
      @RequestParam(name = "sortField", required = false) String sortField,
      @RequestParam(name = "sortOrder", required = false) String sortOrder,
      @RequestParam(name = "pageNo", required = false) int pageNo,
      @RequestParam(name = "pageSize", required = false) int pageSize);

  @GetMapping("/org/{orgId}/jobs/{jobId}/results")
  BaseResponse<List<RecordStatusDto>> getJobRecordsByFilter(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @NotEmpty @NotNull @PathVariable("jobId") String jobId,
      @RequestParam(name = "status", required = false) String status);
}
