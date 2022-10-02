package com.hbc.jobs.framework.common.clients;

import com.hbc.common.base.PagePayload;
import com.hbc.common.response.BaseResponse;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.outbound.JobResponse;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-config-jobs-dashboard",
    url = "${spring.application.dependencies.data-upload:http://pe-config-data-upload:8080/}")
public interface JobsDashboardClient {

  @GetMapping("/org/{orgId}/jobs/{jobId}")
  BaseResponse<JobDto> getJob(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @NotEmpty @NotNull @PathVariable("jobId") String jobId);

  @PostMapping("/org/{orgId}/jobs")
  BaseResponse<JobResponse> processJobJsonOffline(
      @NotNull @Valid @RequestParam("jobType") JobTypeEnum jobType,
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @RequestBody String request);

  @PutMapping(
      value = "/org/{orgId}/jobs/{jobId}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  BaseResponse<JobResponse> processJobsJsonOffline(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @NotNull @Valid @RequestParam("jobType") JobTypeEnum jobType,
      @PathVariable("jobId") String jobId);

  @GetMapping("/org/{orgId}/jobs/filters")
  BaseResponse<PagePayload<JobResponse>> getJobsByFilter(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @RequestParam(name = "jobType", required = false) String jobType,
      @RequestParam(name = "days", required = false) Integer days,
      @RequestParam(name = "sortField", required = false) String sortField,
      @RequestParam(name = "sortOrder", required = false) String sortOrder,
      @RequestParam(name = "pageNo", required = false) int pageNo,
      @RequestParam(name = "pageSize", required = false) int pageSize);

  @GetMapping("/org/{orgId}/jobs-dashboard/{jobId}/results")
  BaseResponse<List<RecordStatusDto>> getJobRecords(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @NotEmpty @NotNull @PathVariable("jobId") String jobId,
      @RequestParam(name = "status", required = false) String status);

  @PostMapping("/org/{orgId}/jobs")
  BaseResponse<JobResponse> processJobOffline(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @RequestParam("jobType") @NotNull @Valid JobTypeEnum jobType,
      @RequestBody byte[] csvFile,
      @RequestParam("fileName") String fileName);
}
