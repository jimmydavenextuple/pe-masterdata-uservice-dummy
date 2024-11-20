/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service.v1;

import com.nextuple.csvdownload.service.v1.impl.CostDefinitionRequestSelectorValidationImpl;
import com.nextuple.csvdownload.service.v1.impl.CostDefinitionRequestValidationImpl;
import com.nextuple.sourcing.cost.config.inbound.CostDefinitionRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class CostDefinitionRequestValidationFactory {
  public ICostDefinitionRequestValidation getCostDefinitionRequestValidationFactory(
      CostDefinitionRequest request) {
    return ObjectUtils.isEmpty(request.getSelector())
        ? new CostDefinitionRequestValidationImpl()
        : new CostDefinitionRequestSelectorValidationImpl();
  }
}
