/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.framework.common.domain.pojo;

import com.nextuple.jobs.framework.common.domain.enums.JobStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class JobDto implements Serializable {

  private static final long serialVersionUID = 2806054788561367739L;

  @Schema(
      description = "Unique identifier of the job.",
      example = "db94622e-68bb-4375-9c37-27a4c5108c55")
  private String jobId;

  @Schema(description = "Specifies th name of the file.", example = "postal-code-timezone.csv")
  private String fileName;

  @Schema(description = "Specifies the file to be processed.")
  private byte[] file;

  @Schema(description = "Specifies the total number of records.", example = "1")
  private Integer totalRecords;

  @Schema(description = "Specifies the type of the job.", example = "UPLOAD_TRANSIT_TIMES")
  private JobTypeEnum jobType;

  @Schema(description = "Specifies the number of records processed.", example = "1")
  private Integer processedRecords;

  @Schema(description = "Specifies the number of records remaining to be processed.", example = "5")
  private Integer remainingRecords;

  @Schema(description = "Specifies the number of failure count.", example = "1")
  private Integer failureCount;

  @Schema(description = "Specifies the number of success count.", example = "2")
  private Integer successCount;

  @Schema(description = "Specifies the status of the job.", example = "SUBMITTED")
  private JobStatusEnum status;

  @Schema(description = "Unique identifier of the user.", example = "xyz")
  private String userId;

  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  private String orgId;

  @Schema(
      description = "Specifies the timestamp when the processing starts.",
      example = "2024-03-07T11:09:18.376+00:00")
  private Date processingStartedAt;

  @Schema(description = "Specifies the metadata of job.")
  private Metadata metadata;

  @Schema(description = "Specifies the list of steps/logs for processing.")
  private List<AuditLog> auditLog;

  @Schema(description = "Specifies the error message.")
  private String errorMessage;

  @Schema(description = "Specifies the unique identifier of the file metadata.", example = "9L")
  private Long fileMetaDataId;
}
