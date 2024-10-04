/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.node.carrier.domain.dto.NodeCarrierListCacheKeyDto;
import com.nextuple.node.carrier.domain.inbound.NodeCarriersRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarriersUpdateRequest;
import com.nextuple.node.carrier.domain.outbound.NodeCarriersResponse;
import com.nextuple.node.carrier.exception.InvalidDataException;
import java.util.List;

public interface NodeCarriersService {

  NodeCarriersResponse createNodeCarrier(NodeCarriersRequest nodeCarriersRequest)
      throws InvalidDataException, CommonServiceException;

  NodeCarriersResponse getNodeCarrierDetails(
      String orgId, String nodeId, String carrierServiceId, String serviceOption)
      throws CommonServiceException;

  NodeCarriersResponse updateNodeCarrier(
      String orgId,
      String nodeId,
      String carrierServiceId,
      String serviceOption,
      NodeCarriersUpdateRequest nodeCarriersUpdateRequest)
      throws CommonServiceException, InvalidDataException;

  NodeCarriersResponse deleteNodeCarrier(
      String orgId, String nodeId, String carrierServiceId, String serviceOption)
      throws CommonServiceException;

  List<NodeCarriersResponse> getNodeCarriersListByOrgIdAndNodeId(String orgId, String nodeId)
      throws CommonServiceException;

  List<String> getListOfCarrierServiceNameByOrgIdAndNodeId(String orgId, String nodeId)
      throws CommonServiceException;

  List<NodeCarriersResponse> getNodeCarriersList(String orgId, String nodeId, String serviceOption)
      throws CommonServiceException;

  List<NodeCarrierListCacheKeyDto> getAllNodeCarriersCacheKeys(Integer limit)
      throws CommonServiceException;

  List<NodeCarriersResponse> getAllNodeCarriersByOrgIdCarrierServiceId(
      String orgId, String carrierServiceId) throws CommonServiceException;
}
