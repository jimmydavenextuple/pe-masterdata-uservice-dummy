/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.master.data.integration.inbound;

import com.nextuple.master.data.integration.constants.BatchApiConstants;
import com.nextuple.master.data.integration.enums.ActionEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BatchRequest<T> {

  @NotNull(message = "action can't null")
  private ActionEnum action;

  private Date producedTimestamp;
  private Date receivedTimestamp;

  @NotNull(message = "record no can't be null")
  @Min(value = 1)
  @Schema(
      description = BatchApiConstants.RECORD_NUMBER,
      example = BatchApiConstants.RECORD_NUMBER_EXAMPLE)
  private Integer recordNo;

  @NotNull(message = "payload can't be null")
  @Schema(description = BatchApiConstants.PAYLOAD, example = BatchApiConstants.PAYLOAD_EXAMPLE)
  private T payload;
}
