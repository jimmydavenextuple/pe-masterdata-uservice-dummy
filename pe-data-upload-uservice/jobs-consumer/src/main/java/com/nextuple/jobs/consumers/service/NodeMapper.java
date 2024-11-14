/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.service;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.BOPIS_ELIGIBLE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DELETE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.IS_ACTIVE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SHIP_TO_HOME;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.UPDATE;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.util.BooleanUtil;
import com.nextuple.csvdownload.exception.CsvDataValidationException;
import com.nextuple.dataupload.common.config.TenantDatabaseConfig;
import com.nextuple.jobs.consumers.domain.mapper.NodeRequestMapper;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.NodeDataUpload;
import com.nextuple.jobs.framework.common.domain.pojo.RecordInputDto;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.node.domain.feign.NodeFeign;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class NodeMapper implements FeignClientMapper {

  @Autowired private TenantDatabaseConfig tenantDatabaseConfig;

  private final NodeFeign nodeFeign;

  private final Logger logger = LoggerFactory.getLogger(NodeMapper.class);

  @Setter private JobTypeEnum jobType;

  public static final NodeRequestMapper INSTANCE = Mappers.getMapper(NodeRequestMapper.class);

  @Override
  public ModuleEnum getModule() {
    return ModuleEnum.NODES;
  }

  @Override
  public Map<String, String> getColumnNameMapping(String[] headerColumns) {
    return new HashMap<>();
  }

  @Override
  public Class mapTODto() {
    return NodeDataUpload.class;
  }

  private static void validateBooleans(NodeDataUpload request, String[] serviceOptions)
      throws CommonServiceException {
    for (String serviceOption : serviceOptions) {
      BooleanUtil.checkValidBooleanValue(
          request.getServiceOptionEligibilities().get(serviceOption), serviceOption);
    }

    BooleanUtil.checkValidBooleanValue(request.getShipToHome(), SHIP_TO_HOME);
    BooleanUtil.checkValidBooleanValue(request.getBopisEligible(), BOPIS_ELIGIBLE);
    BooleanUtil.checkValidBooleanValue(request.getIsActive(), IS_ACTIVE);
  }

  @Override
  public ResponseEntity<?> callApi(Object request, RecordInputDto inputs)
      throws CommonServiceException {
    var nodeDataUpload = (NodeDataUpload) request;
    if (!StringUtils.hasLength(nodeDataUpload.getOrgId())) {
      log.error("OrgId not found in the request");
      throw new CommonServiceException(
          "OrgId not found in the request", HttpStatus.BAD_REQUEST, 6513, null);
    }
    String[] serviceOptions =
        tenantDatabaseConfig.getCurrentTenantServiceOptions(nodeDataUpload.getOrgId());
    String action = nodeDataUpload.getAction();
    switch (action) {
      case CREATE:
        validateBooleans(nodeDataUpload, serviceOptions);
        return ResponseEntity.ok(
            nodeFeign.createNode(INSTANCE.convertToNodeRequest(nodeDataUpload)));
      case UPDATE:
        validateBooleans(nodeDataUpload, serviceOptions);
        return ResponseEntity.ok(
            nodeFeign.updateNodeDetails(
                nodeDataUpload.getNodeId(),
                nodeDataUpload.getOrgId(),
                INSTANCE.convertToNodeUpdationRequest(nodeDataUpload)));

      case DELETE:
        return ResponseEntity.ok(
            nodeFeign.deleteNode(nodeDataUpload.getNodeId(), nodeDataUpload.getOrgId()));
      default:
        {
          logger.error("Invalid action type: {}", nodeDataUpload.getAction());
          throw new CsvDataValidationException(
              "Please provide the valid action: " + nodeDataUpload.getAction());
        }
    }
  }

  @Override
  public void setJobType(JobTypeEnum jobType) {
    this.jobType = jobType;
  }

  @Override
  public Object getDTOFromCustomMapper(String stringRecord) {
    return null;
  }
}
