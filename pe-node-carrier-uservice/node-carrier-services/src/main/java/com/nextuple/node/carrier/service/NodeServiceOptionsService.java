/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionUpdateRequest;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionResponse;
import com.nextuple.node.carrier.exception.InvalidDataException;
import java.util.List;

public interface NodeServiceOptionsService {

  NodeServiceOptionResponse createNodeServiceOption(
      NodeServiceOptionRequest nodeServiceOptionRequest)
      throws CommonServiceException, InvalidDataException;

  NodeServiceOptionResponse updateNodeServiceOption(
      String orgId,
      String nodeId,
      String serviceOption,
      NodeServiceOptionUpdateRequest nodeServiceOptionUpdateRequest)
      throws CommonServiceException, InvalidDataException;

  NodeServiceOptionResponse getNodeServiceOption(String orgId, String nodeId, String serviceOption)
      throws CommonServiceException;

  NodeServiceOptionResponse deleteNodeServiceOption(
      String orgId, String nodeId, String serviceOption)
      throws CommonServiceException, InvalidDataException;

  List<NodeServiceOptionResponse> getNodeServiceOptionList(String orgId, String nodeId)
      throws CommonServiceException;
}
