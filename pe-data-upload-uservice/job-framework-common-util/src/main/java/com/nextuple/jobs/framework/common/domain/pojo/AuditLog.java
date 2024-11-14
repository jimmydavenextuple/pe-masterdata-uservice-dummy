/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.framework.common.domain.pojo;

import com.nextuple.jobs.framework.common.domain.enums.JobStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class AuditLog implements Serializable {
  private static final long serialVersionUID = 8075572212366047610L;

  @Schema(description = "Specifies the status of the job.", example = "SUBMITTED")
  private JobStatusEnum status;

  @Schema(
      description = "Specifies the timestamp of the processing job.",
      example = "2024-03-07T11:09:18.376+00:00")
  private Date timeStamp;
}
