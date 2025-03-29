/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.domain.outbound;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
{
  "totalCount": 2,
  "successCount": 2,
  "failureCount": 0,
  "results": [
    {
      "index": 0,
      "success": true,
      "message": "Created successfully",
      "payload": {
        "orgId": "NEXTUPLE",
        "transferScheduleId": "TS001",
        "dropoffNodeId": "DN001",
        "startTime": "09:00",
        "endTime": "17:00"
      },
      "errorCode": null
    },
    {
    }
  ]
}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferScheduleBatchResponse implements Serializable {

  @Schema(description = "Total number of records in the batch", example = "2")
  private Integer totalCount;

  @Schema(description = "Number of successful records in the batch", example = "2")
  private Integer successCount;

  @Schema(description = "Number of failed records in the batch", example = "0")
  private Integer failureCount;

  @Schema(description = "List of results for each record in the batch")
  private List<TransferScheduleConsumerResult> results;
}
