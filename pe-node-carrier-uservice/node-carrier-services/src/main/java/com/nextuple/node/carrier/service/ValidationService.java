/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.service;

import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.node.carrier.config.NodeCarrierTenantBasedDBConfig;
import com.nextuple.node.carrier.domain.inbound.NodeCarriersRequest;
import com.nextuple.node.carrier.domain.inbound.NodeCarriersUpdateRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionRequest;
import com.nextuple.node.carrier.domain.inbound.NodeServiceOptionUpdateRequest;
import com.nextuple.node.carrier.exception.InvalidDataException;
import com.nextuple.node.domain.feign.NodeFeign;
import com.nextuple.node.domain.outbound.NodeResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@RequiredArgsConstructor
@Service
public class ValidationService {

  private static final Logger logger = LoggerFactory.getLogger(ValidationService.class);

  private final NodeCarrierTenantBasedDBConfig nodeCarrierTenantBasedDBConfig;
  private static final String ORG_ID = "orgId";
  private static final String NODE_ID = "nodeId";
  private static final String SERVICE_OPTION = "serviceOption";

  private final NodeFeign nodeFeign;

  private final CarrierFeign carrierFeign;

  private final Pattern validationOfLastPickupTimeRegex =
      Pattern.compile("([01]?\\d|2[0-3]):[0-5]\\d");

  private static final String INVALID_CARRIER_DATA_EXCEPTION_MESSAGE =
      "Node carrier data cannot be created with given carrierServiceId and orgId";

  private void validateServiceOption(String orgId, String nodeId, String serviceOption)
      throws CommonServiceException {
    if (!nodeCarrierTenantBasedDBConfig.getServiceOptions(orgId).contains(serviceOption)) {
      throwCommonServiceException("Invalid serviceOption", orgId, nodeId, serviceOption);
    }
  }

  private NodeResponse validateNodeDetails(String orgId, String nodeId, String serviceOption)
      throws CommonServiceException {
    BaseResponse<NodeResponse> response = null;

    try {
      response = nodeFeign.getNodeDetails(nodeId, orgId);
      if (Objects.isNull(response) || Objects.isNull(response.getPayload())) {
        throwCommonServiceException(
            "Invalid nodeId and orgId combination", orgId, nodeId, serviceOption);
      }
    } catch (Exception e) {
      throwCommonServiceException(
          "Invalid nodeId and orgId combination", orgId, nodeId, serviceOption);
    }
    return response.getPayload();
  }

  private void validateProcessingLeadTime(Double processingLeadTime) throws InvalidDataException {
    if (ObjectUtils.isEmpty(processingLeadTime) || processingLeadTime < 0) {
      logger.error("Processing lead time can not be negative or empty");
      throw new InvalidDataException(
          "Processing lead time can not be negative or empty", null, processingLeadTime);
    }
  }

  private void validateLastPickupTime(String lastPickupTime) throws InvalidDataException {
    if (!validationOfLastPickupTimeRegex.matcher(lastPickupTime).matches()) {
      throw new InvalidDataException("LastPickupTime is invalid", lastPickupTime, null);
    }
  }

  private Boolean validateCarrierDetails(String orgId, String carrierServiceId) {
    try {
      var carrierServiceResponse =
          carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(carrierServiceId, orgId);
      if (Objects.nonNull(carrierServiceResponse)
          && Objects.nonNull(carrierServiceResponse.getPayload())
          && !carrierServiceResponse.getPayload().isEmpty()) {
        return true;
      }
    } catch (Exception e) {
      logger.error(
          "Error while fetching carrier details for orgId: {} , carrierServiceId: {}",
          orgId,
          carrierServiceId);
    }
    return false;
  }

  private void validateCarrierServiceId(
      String orgId, String nodeId, String carrierServiceId, String serviceOption)
      throws CommonServiceException {
    if (!ObjectUtils.isEmpty(carrierServiceId)
        && Boolean.FALSE.equals(validateCarrierDetails(orgId, carrierServiceId))) {
      throwCommonServiceException(
          INVALID_CARRIER_DATA_EXCEPTION_MESSAGE, orgId, nodeId, serviceOption);
    }
  }

  private void throwCommonServiceException(
      String errorMessage, String orgId, String nodeId, String serviceOption)
      throws CommonServiceException {
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
    errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
    errorMap.put(SERVICE_OPTION, FieldError.builder().rejectedValue(serviceOption).build());
    throw new CommonServiceException(errorMessage, HttpStatus.BAD_REQUEST, 0x1772, errorMap);
  }

  public void validate(NodeCarriersRequest nodeCarriersRequest)
      throws CommonServiceException, InvalidDataException {
    validateNodeDetails(
        nodeCarriersRequest.getOrgId(),
        nodeCarriersRequest.getNodeId(),
        nodeCarriersRequest.getServiceOption());
    validateCarrierServiceId(
        nodeCarriersRequest.getOrgId(),
        nodeCarriersRequest.getNodeId(),
        nodeCarriersRequest.getCarrierServiceId(),
        nodeCarriersRequest.getServiceOption());
    validateServiceOption(
        nodeCarriersRequest.getOrgId(),
        nodeCarriersRequest.getNodeId(),
        nodeCarriersRequest.getServiceOption());
    validateLastPickupTime(nodeCarriersRequest.getLastPickupTime());
  }

  public void validate(String orgId, String nodeId, String serviceOption)
      throws CommonServiceException {
    validateServiceOption(orgId, nodeId, serviceOption);
  }

  public void validate(NodeCarriersUpdateRequest nodeCarriersUpdateRequest)
      throws InvalidDataException {
    validateLastPickupTime(nodeCarriersUpdateRequest.getLastPickupTime());
  }

  public void validate(NodeServiceOptionRequest nodeServiceOptionRequest)
      throws CommonServiceException, InvalidDataException {
    validateNodeDetails(
        nodeServiceOptionRequest.getOrgId(),
        nodeServiceOptionRequest.getNodeId(),
        nodeServiceOptionRequest.getServiceOption());
    validateServiceOption(
        nodeServiceOptionRequest.getOrgId(),
        nodeServiceOptionRequest.getNodeId(),
        nodeServiceOptionRequest.getServiceOption());
    validateProcessingLeadTime(nodeServiceOptionRequest.getProcessingTime());
  }

  public void validate(NodeServiceOptionUpdateRequest nodeServiceOptionUpdateRequestRequest)
      throws InvalidDataException {
    validateProcessingLeadTime(nodeServiceOptionUpdateRequestRequest.getProcessingTime());
  }
}
