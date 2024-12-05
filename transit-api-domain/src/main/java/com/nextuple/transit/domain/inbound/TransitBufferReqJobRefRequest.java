/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.domain.inbound;

import com.nextuple.transit.domain.enums.TransitBufferReqJobRefEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TransitBufferReqJobRefRequest implements Serializable {

  @Schema(description = "Unique identifier of the external reference or job.", example = "ref1232")
  @Length(max = 50)
  private String extReferenceId;

  @Schema(description = "Unique identifier of the transit buffer request.", example = "1231231231")
  private Long transitBufferReqId;

  @Schema(
      description = "Action to be performed on the uploaded transit data file.",
      allowableValues = {"CREATE", "UPDATE", "DELETE"})
  private TransitBufferReqJobRefEnum action;
}
