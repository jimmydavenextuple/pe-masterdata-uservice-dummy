/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nextuple.sourcing.cost.config.customannotations.AttributePath;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeDetails implements Serializable {

  private static final long serialVersionUID = 9204860398507113171L;

  @Schema(description = "Node id")
  @AttributePath(path = "/nodeId")
  private String nodeId;

  @Schema(description = "Type of node")
  @AttributePath(path = "/nodeType")
  private String nodeType;

  @Schema(description = "Zipcode of node")
  @AttributePath(path = "/nodeZipCode")
  private String zipCode;

  @Schema(description = "Node labour tier details")
  @AttributePath(path = "/nodeLabourTier")
  private String nodeLabourTier;
}
