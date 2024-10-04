/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionBufferDeleteRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionBufferRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionBufferUpdateRequest;
import com.nextuple.node.carrier.domain.outbound.NodeServiceOptionBufferResponse;
import java.time.LocalDate;
import java.util.List;

public interface NodeServiceOptionBufferService {
  NodeServiceOptionBufferResponse createNodeServiceOptionBuffer(
      NodeServiceOptionBufferRequest nodeServiceOptionBufferRequest) throws CommonServiceException;

  NodeServiceOptionBufferResponse fetchNodeServiceOptionBuffer(String orgId, Long id)
      throws CommonServiceException;

  List<NodeServiceOptionBufferResponse> fetchApplicableNodeServiceOptionBuffers(
      String orgId, String nodeId, String serviceOption, LocalDate requestDate, Integer horizonDays)
      throws CommonServiceException;

  NodeServiceOptionBufferResponse updateNodeServiceOptionBuffer(
      String orgId, Long id, NodeServiceOptionBufferUpdateRequest updateRequest)
      throws CommonServiceException;

  NodeServiceOptionBufferResponse deleteNodeServiceOptionBuffer(
      NodeServiceOptionBufferDeleteRequest nodeServiceOptionBufferDeleteRequest)
      throws CommonServiceException;

  NodeServiceOptionBufferResponse deleteNodeServiceOptionBufferByOrgIdAndId(String orgId, Long id)
      throws CommonServiceException;

  List<NodeServiceOptionBufferResponse> getBuffersByOrgIdAndNodeIdAndServiceOption(
      String orgId, String nodeId, String serviceOption) throws CommonServiceException;
}
