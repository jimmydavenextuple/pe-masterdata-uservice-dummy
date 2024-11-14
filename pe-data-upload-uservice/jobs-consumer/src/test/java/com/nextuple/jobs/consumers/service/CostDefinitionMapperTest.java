/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.exception.CsvDataValidationException;
import com.nextuple.jobs.consumers.common.TestUtil;
import com.nextuple.jobs.consumers.exception.InvalidJobTypeException;
import com.nextuple.jobs.consumers.exception.NodeCarrierMapperException;
import com.nextuple.jobs.consumers.exception.TransitMapperException;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.CostValueDataUpload;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.sourcing.cost.config.feign.CostValueFeign;
import com.nextuple.sourcing.cost.config.outbound.CostDefinitionResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CostDefinitionMapperTest {
  @Mock private CostValueFeign costValueFeign;
  @InjectMocks private CostDefinitionMapper costDefinitionMapper;
  @InjectMocks private TestUtil testUtil;

  @Test
  void callApiTest() {
    Object object = testUtil.getCostValueUploadRequest("DELETE");
    when(costValueFeign.deleteCostValueCostFactorCombinationKey(
            anyString(), anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getCostValueUploadResponse()).build());

    ResponseEntity<BaseResponse<CostDefinitionResponse>> response =
        (ResponseEntity<BaseResponse<CostDefinitionResponse>>)
            costDefinitionMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void callApiTestWithNA() {
    Object object = testUtil.getCostValueUploadRequest("NA");

    ResponseEntity<BaseResponse<?>> response =
        (ResponseEntity<BaseResponse<?>>) costDefinitionMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        "We are skipping this upload because of cost value NA", response.getBody().getMessage());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void callApiInvalidTest() {
    Object object = testUtil.getCostValueUploadRequest("INVALID");

    Exception ex =
        assertThrows(
            CsvDataValidationException.class, () -> costDefinitionMapper.callApi(object, null));

    Assertions.assertEquals("Invalid action or cost value: INVALID", ex.getMessage());
    Assertions.assertNotNull(ex);
  }

  @Test
  void callApiTestValidNumber() {
    Object object = testUtil.getCostValueUploadRequest("20.0");
    when(costValueFeign.createCostValue(anyString(), any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getCostValueUploadResponse()).build());

    ResponseEntity<BaseResponse<CostDefinitionResponse>> response =
        (ResponseEntity<BaseResponse<CostDefinitionResponse>>)
            costDefinitionMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void getModule() {
    ModuleEnum moduleEnum = costDefinitionMapper.getModule();
    Assertions.assertEquals(ModuleEnum.COST_DEFINITION, moduleEnum);
  }

  @Test
  void setJobType() {
    Assertions.assertDoesNotThrow(
        () -> costDefinitionMapper.setJobType(JobTypeEnum.UPLOAD_COST_DEFINITION));
  }

  @Test
  void getDTOFromCustomMapper() {
    Assertions.assertNull(costDefinitionMapper.getDTOFromCustomMapper("request"));
  }

  @Test
  void getColumnNameMapping() {
    Assertions.assertNotNull(costDefinitionMapper.getColumnNameMapping(new String[] {"request"}));
  }

  @Test
  void mapTODto()
      throws InvalidJobTypeException, TransitMapperException, NodeCarrierMapperException {
    Class costValueDataUpload = costDefinitionMapper.mapTODto();
    Assertions.assertEquals(CostValueDataUpload.class, costValueDataUpload);
    Assertions.assertNotNull(costValueDataUpload);
  }
}
