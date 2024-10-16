/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.exception;

import lombok.Data;

@Data
public class NodeCarrierDomainException extends Exception {

  private final String nodeId;
  private final String orgId;
  private final String carrierServiceId;
  private final String serviceOption;

  public NodeCarrierDomainException(
      String message, String nodeId, String orgId, String carrierServiceId, String serviceOption) {
    super(message);
    this.nodeId = nodeId;
    this.orgId = orgId;
    this.carrierServiceId = carrierServiceId;
    this.serviceOption = serviceOption;
  }
}
