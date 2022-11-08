package com.nextuple.jobs.consumers.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.util.PaginationUtil;
import com.nextuple.jobs.consumers.exception.JobDomainException;
import com.nextuple.jobs.consumers.exception.JobException;
import com.nextuple.jobs.consumers.service.JobConsumerService;
import com.nextuple.jobs.consumers.util.UriBuilder;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponse;
import com.nextuple.jobs.framework.common.domain.pojo.DefaultPageProperties;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.nextuple.jobs.framework.common.domain.pojo.JobFilters;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class JobsConsumerController {
  private final JobConsumerService jobConsumerService;
  private static final String PAGINATION_URL_JOB_RECORDS =
      "/data-upload/v1/org/%s/jobs/%s/results?pageNo=%d&pageSize=%d";
  private final DefaultPageProperties defaultPageProperties;

  /**
   * @param orgId
   * @param jobId
   * @param status
   * @return
   * @throws JobException
   */
  @GetMapping(
      path = "/org/{orgId}/jobs/{jobId}/results",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<List<RecordStatusDto>>> getJobRecordsByFilter(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @NotEmpty @NotNull @PathVariable("jobId") String jobId,
      @RequestParam(required = false) Optional<String> status)
      throws JobException {
    log.debug("--Inside getJobRecordsByFilter controller--");

    List<RecordStatusDto> pageResp = jobConsumerService.getJobResults(orgId, jobId, status);

    return ResponseEntity.ok()
        .body(
            BaseResponse.builder()
                .payload(pageResp)
                .message("Retrieval of the job information is successful")
                .build());
  }

  /**
   * @param jobDto
   * @return
   * @throws JobException
   */
  @PostMapping(
      value = "/jobs",
      produces = APPLICATION_JSON_VALUE,
      consumes = APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<JobResponse>> createJob(@Valid @RequestBody JobDto jobDto)
      throws JobException {
    log.debug("-- Inside createJob controller --");

    JobResponse job = jobConsumerService.createJob(jobDto);
    return ResponseEntity.ok(
        BaseResponse.builder().message("Job successfully created").payload(job).build());
  }

  /**
   * @param orgId
   * @param jobId
   * @return
   * @throws JobException
   */
  @GetMapping(path = "/org/{orgId}/jobs/{jobId}")
  public ResponseEntity<BaseResponse<JobDto>> getJob(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @NotEmpty @NotNull @PathVariable String jobId)
      throws JobException {
    log.debug("-- Inside getJob controller --");

    JobDto job = jobConsumerService.getJob(jobId, orgId);

    log.debug("Job successfully retrieved : {}", jobId);

    return ResponseEntity.ok(
        BaseResponse.builder().message("Retrieval of the job is successful").payload(job).build());
  }

  /**
   * @param jobResponse
   * @return
   * @throws JobDomainException
   */
  @PutMapping(path = "/jobs/update")
  public ResponseEntity<BaseResponse<JobResponse>> updateJob(
      @Valid @RequestBody JobResponse jobResponse) throws JobDomainException {
    log.debug("-- Inside update job controller --");

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Retrieval of the job is successful")
            .payload(jobConsumerService.saveJob(jobResponse))
            .build());
  }

  /**
   * @param orgId
   * @param jobFilters
   * @return
   * @throws JobException
   */
  @GetMapping(value = "/org/{orgId}/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<PagePayload<JobResponse>>> getJobsByFilter(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId, JobFilters jobFilters)
      throws JobException {
    log.debug("--Inside getJobsByFilter()--");

    int requiredPageNo = jobFilters.getPageNo().orElse(defaultPageProperties.getPageNo());
    int requiredPageSize = jobFilters.getPageSize().orElse(defaultPageProperties.getPageSize());
    String requiredSortByField = jobFilters.getSortBy().orElse(defaultPageProperties.getSortBy());
    String requiredSortOrder =
        jobFilters.getSortOrder().orElse(defaultPageProperties.getSortOrder());

    if (requiredPageNo < 1) {
      throw new JobException("PageNo can not be less than one", null, requiredPageNo);
    }

    Page<JobResponse> pageResp =
        jobConsumerService.getJobs(
            orgId,
            jobFilters.getJobType(),
            jobFilters.getDays(),
            requiredSortByField,
            requiredSortOrder,
            requiredPageNo,
            requiredPageSize);

    var pagination = new PagePayload.Pagination();
    pagination.setTotalRecords((int) pageResp.getTotalElements());
    pagination.setTotalPages(pageResp.getTotalPages());
    pagination.setCurrentPage(requiredPageNo);
    pagination.setSortBy(requiredSortByField);
    pagination.setSortOrder(requiredSortOrder);

    PagePayload<JobResponse> pagePayload = new PagePayload<>();
    pagePayload.setData(pageResp.getContent());
    pagePayload.setPagination(pagination);
    pagePayload.setAggregation(Collections.emptyList());

    String nextUri =
        UriBuilder.buildUriForPagination(
            jobFilters.getJobType(),
            jobFilters.getDays(),
            requiredSortByField,
            requiredSortOrder,
            requiredPageNo,
            requiredPageSize,
            pageResp.getTotalPages(),
            "next");
    String previousUri =
        UriBuilder.buildUriForPagination(
            jobFilters.getJobType(),
            jobFilters.getDays(),
            requiredSortByField,
            requiredSortOrder,
            requiredPageNo,
            requiredPageSize,
            pageResp.getTotalPages(),
            "previous");
    pagination.setNext(nextUri);
    pagination.setPrevious(previousUri);

    return ResponseEntity.ok()
        .body(
            BaseResponse.builder()
                .payload(pagePayload)
                .message("Retrieval of the jobs by params is successful")
                .build());
  }

  @GetMapping(
      path = "/v1/org/{orgId}/jobs/{jobId}/results",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<PagePayload<RecordStatusDto>>> getJobRecordsByFilters(
      @NotEmpty @NotNull @PathVariable("orgId") String orgId,
      @NotEmpty @NotNull @PathVariable("jobId") String jobId,
      @RequestParam(required = false) Optional<String> status,
      @RequestParam(required = false) Optional<Integer> pageNo,
      @RequestParam(required = false) Optional<Integer> pageSize)
      throws JobException {
    log.debug("--Inside getJobRecordsByFilter controller--");

    int currentPage = pageNo.orElse(defaultPageProperties.getPageNo());

    if (currentPage < 1) {
      throw new JobException("PageNo can not be less than one", null, currentPage);
    }

    Page<RecordStatusDto> pageResp =
        jobConsumerService.getJobResults(
            orgId,
            jobId,
            status,
            pageNo.orElse(defaultPageProperties.getPageNo()),
            pageSize.orElse(defaultPageProperties.getPageSize()));

    var pagination = new PagePayload.Pagination();
    pagination.setTotalRecords((int) pageResp.getTotalElements());
    pagination.setTotalPages(pageResp.getTotalPages());
    pagination.setCurrentPage(currentPage);

    PagePayload<RecordStatusDto> pagePayload = new PagePayload<>();
    pagePayload.setData(pageResp.getContent());
    pagePayload.setPagination(pagination);
    pagePayload.setAggregation(Collections.emptyList());

    String nextUri =
        PaginationUtil.buildUriForPagination(
            currentPage,
            pagePayload.getPagination().getTotalPages(),
            "next",
            String.format(
                PAGINATION_URL_JOB_RECORDS,
                orgId,
                jobId,
                currentPage + 1,
                pageSize.orElse(defaultPageProperties.getPageSize())));

    String previousUri =
        PaginationUtil.buildUriForPagination(
            currentPage,
            pagePayload.getPagination().getTotalPages(),
            "previous",
            String.format(
                PAGINATION_URL_JOB_RECORDS,
                orgId,
                jobId,
                currentPage - 1,
                pageSize.orElse(defaultPageProperties.getPageSize())));

    pagination.setNext(nextUri);
    pagination.setPrevious(previousUri);

    return ResponseEntity.ok()
        .body(
            BaseResponse.builder()
                .payload(pagePayload)
                .message("Retrieval of the job information is successful")
                .build());
  }
}
