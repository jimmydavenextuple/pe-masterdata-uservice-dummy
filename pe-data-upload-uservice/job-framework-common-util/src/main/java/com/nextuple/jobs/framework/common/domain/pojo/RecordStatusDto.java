/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.framework.common.domain.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nextuple.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecordStatusDto implements Serializable {

  private static final long serialVersionUID = 7235171183817789960L;

  @Schema(
      description = "Unique identifier of the job.",
      example = "db94622e-68bb-4375-9c37-27a4c5108c55")
  private String jobId;

  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  private String orgId;

  @Schema(description = "Specifies the record number.", example = "1")
  private Integer recordNo;

  @Schema(description = "Specifies the status code of the record.", example = "1")
  private Integer statusCode;

  @Schema(description = "Specifies the status of the API.", example = "FAILURE")
  private ApiStatusEnum status;

  @Schema(description = "Specifies the request body code.")
  private String requestBody;

  @Schema(description = "Specifies the response body.")
  private String responseBody;

  @Schema(description = "Specifies the error message.")
  private String errorMessage;

  @Schema(description = "Unique identifier of user.", example = "xyz")
  private String userId;

  @Schema(description = "Specifies the type of the job.", example = "UPLOAD_TRANSIT_TIMES")
  private JobTypeEnum jobType;

  @Schema(description = "Specifies the response time.", example = "<= 9007199254740991")
  private Long responseTime;

  @Schema(description = "Specifies the total number of records in the job.", example = "2")
  private Integer totalRecordsInJob;

  @Schema(description = "Specifies the references of the job.", example = "2")
  private JobReferences jobReferences;

  @Schema(description = "Specifies a unique ID of the API response.", example = "398ABC")
  private String correlationId;

  private String serviceCorrelationId;

  @Schema(
      description = "Specifies a flag to indicate if the request body is present.",
      example = "true")
  private Boolean responseBodyPresent;

  @Schema(description = "Specifies any exception in the error", example = "None")
  private String exception = "None";

  public void setJobReferences(Map<String, Object> references) {
    this.jobReferences = new JobReferences(references);
  }
}
