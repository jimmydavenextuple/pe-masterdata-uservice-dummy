/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nextuple.common.pojo.AdditionalAttributes;
import com.nextuple.sourcing.cost.config.customannotations.AttributePath;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SampleSourcingSolutionForFormulaValidation extends AdditionalAttributes
    implements Serializable {

  private static final long serialVersionUID = 722826846716560147L;

  @Schema(description = "org id", example = "NEXTUPLE_GR")
  @AttributePath(path = "/orgId")
  private String orgId;

  @Schema(
      description =
          "Service option of the order line. This will override the service option specified at the parent level.",
      example = "SDND / NEXTDAY / EXPRESS")
  @AttributePath(path = "/serviceOption")
  private String serviceOption;

  @Schema(description = "Total number of lines")
  @AttributePath(path = "/totalLines")
  private Integer totalLines;

  @Schema(description = "Total cost")
  @AttributePath(path = "/totalCost")
  private Double totalCost;

  @Schema(description = "List of shipments")
  @AttributePath(path = "/shipments/*")
  private List<Shipment> shipments;
}
