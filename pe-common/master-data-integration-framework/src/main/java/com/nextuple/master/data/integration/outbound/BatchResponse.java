/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.master.data.integration.outbound;

import com.nextuple.master.data.integration.constants.BatchApiConstants;
import com.nextuple.master.data.integration.dto.ResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BatchResponse {

  @Schema(
      description = BatchApiConstants.TOTAL_RECORDS,
      example = BatchApiConstants.TOTAL_RECORDS_EXAMPLE)
  private int totalRecords;

  @Schema(
      description = BatchApiConstants.SUCCESSFUL_RECORDS,
      example = BatchApiConstants.SUCCESSFUL_RECORDS_EXAMPLE)
  private int successfulRecords;

  @Schema(
      description = BatchApiConstants.FAILED_RECORDS,
      example = BatchApiConstants.FAILED_RECORDS_EXAMPLE)
  private int failedRecords;

  @Schema(description = BatchApiConstants.RESPONSES)
  private List<ResponseDto> responses;
}
