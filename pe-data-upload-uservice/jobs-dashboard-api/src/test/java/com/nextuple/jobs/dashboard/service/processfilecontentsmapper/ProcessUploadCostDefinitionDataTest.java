/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.dashboard.service.processfilecontentsmapper;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.jobs.dashboard.common.TestUtil;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.sourcing.cost.config.feign.CostConfigDashboardFeign;
import com.nextuple.sourcing.cost.config.outbound.CostTypeValidationResponse;
import com.opencsv.exceptions.CsvException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProcessUploadCostDefinitionDataTest {

  @InjectMocks private ProcessUploadCostDefinitionData processUploadCostDefinitionData;
  @Mock CostConfigDashboardFeign costConfigDashboardFeign;
  @InjectMocks private TestUtil testUtil;

  @Test
  void getJobTypeTest() {
    JobTypeEnum jobTypeEnum = processUploadCostDefinitionData.getJobType();
    Assertions.assertNotNull(jobTypeEnum);
    Assertions.assertEquals(JobTypeEnum.UPLOAD_COST_DEFINITION, jobTypeEnum);
  }

  @Test
  void updateRequestObjectsListGridDataTest()
      throws CommonServiceException, IOException, CsvException {
    InputStream inputStream =
        new ByteArrayInputStream(TestUtil.CSV_CONTENTS_COST_DEFINITION_FOR_GRID.getBytes());

    when(costConfigDashboardFeign.getCostTypesForValidation(anyString(), anyString()))
        .thenReturn(
            BaseResponse.builder().payload(testUtil.getCostTypeValidationResponse()).build());

    List<Object> objectList =
        processUploadCostDefinitionData.updateRequestObjectsList(
            testUtil.getJobDto(JobTypeEnum.UPLOAD_COST_DEFINITION), inputStream);

    Assertions.assertEquals(12, objectList.size());
    verify(costConfigDashboardFeign, times(1)).getCostTypesForValidation(anyString(), anyString());
  }

  @Test
  void updateRequestObjectsListTableDataTest()
      throws CommonServiceException, IOException, CsvException {
    InputStream inputStream =
        new ByteArrayInputStream(TestUtil.CSV_CONTENTS_COST_DEFINITION_FOR_TABLE.getBytes());

    when(costConfigDashboardFeign.getCostTypesForValidation(anyString(), anyString()))
        .thenReturn(
            BaseResponse.builder().payload(testUtil.getCostTypeValidationResponse()).build());

    List<Object> objectList =
        processUploadCostDefinitionData.updateRequestObjectsList(
            testUtil.getJobDto(JobTypeEnum.UPLOAD_COST_DEFINITION), inputStream);

    Assertions.assertEquals(4, objectList.size());
    verify(costConfigDashboardFeign, times(1)).getCostTypesForValidation(anyString(), anyString());
  }

  @Test
  void updateRequestObjectsListStaticTableDataTest()
      throws CommonServiceException, IOException, CsvException {
    InputStream inputStream =
        new ByteArrayInputStream(TestUtil.CSV_CONTENTS_COST_DEFINITION_FOR_STATIC_TABLE.getBytes());

    when(costConfigDashboardFeign.getCostTypesForValidation(anyString(), anyString()))
        .thenReturn(
            BaseResponse.builder().payload(testUtil.getCostTypeValidationResponse()).build());

    List<Object> objectList =
        processUploadCostDefinitionData.updateRequestObjectsList(
            testUtil.getJobDto(JobTypeEnum.UPLOAD_COST_DEFINITION), inputStream);

    Assertions.assertEquals(1, objectList.size());
    verify(costConfigDashboardFeign, times(1)).getCostTypesForValidation(anyString(), anyString());
  }

  @Test
  void updateRequestObjectsListWithoutSelectorCFGridTest()
      throws CommonServiceException, IOException, CsvException {
    InputStream inputStream =
        new ByteArrayInputStream(TestUtil.CSV_CONTENTS_WITHOUT_SELECTOR_CF_FOR_GRID.getBytes());

    when(costConfigDashboardFeign.getCostTypesForValidation(anyString(), anyString()))
        .thenReturn(
            BaseResponse.builder()
                .payload(testUtil.getCostTypeDetailsWithEmptySelectorCfForGrid())
                .build());

    List<Object> objectList =
        processUploadCostDefinitionData.updateRequestObjectsList(
            testUtil.getJobDto(JobTypeEnum.UPLOAD_COST_DEFINITION), inputStream);

    Assertions.assertEquals(4, objectList.size());
    verify(costConfigDashboardFeign, times(1)).getCostTypesForValidation(anyString(), anyString());
  }

  @Test
  void updateRequestObjectsListWithoutSelectorCFValuesGridTest()
      throws CommonServiceException, IOException, CsvException {
    InputStream inputStream =
        new ByteArrayInputStream(
            TestUtil.CSV_CONTENTS_WITHOUT_SELECTOR_CF_VALUE_FOR_GRID.getBytes());

    CostTypeValidationResponse costTypeValidationResponse =
        testUtil.getCostTypeValidationResponse();
    costTypeValidationResponse.getSelectorCfInfo().get(0).setSelectorCfValue("");

    when(costConfigDashboardFeign.getCostTypesForValidation(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(costTypeValidationResponse).build());

    List<Object> objectList =
        processUploadCostDefinitionData.updateRequestObjectsList(
            testUtil.getJobDto(JobTypeEnum.UPLOAD_COST_DEFINITION), inputStream);

    Assertions.assertEquals(12, objectList.size());
    verify(costConfigDashboardFeign, times(1)).getCostTypesForValidation(anyString(), anyString());
  }

  @Test
  void updateRequestObjectsListWithoutSelectorCFTableTest()
      throws CommonServiceException, IOException, CsvException {
    InputStream inputStream =
        new ByteArrayInputStream(TestUtil.CSV_CONTENTS_WITHOUT_SELECTOR_CF_FOR_TABLE.getBytes());

    when(costConfigDashboardFeign.getCostTypesForValidation(anyString(), anyString()))
        .thenReturn(
            BaseResponse.builder()
                .payload(testUtil.getCostTypeDetailsWithEmptySelectorCfForTable())
                .build());

    List<Object> objectList =
        processUploadCostDefinitionData.updateRequestObjectsList(
            testUtil.getJobDto(JobTypeEnum.UPLOAD_COST_DEFINITION), inputStream);

    Assertions.assertEquals(2, objectList.size());
    verify(costConfigDashboardFeign, times(1)).getCostTypesForValidation(anyString(), anyString());
  }

  @Test
  void updateRequestObjectsListWithoutSelectorCFValuesTableTest()
      throws CommonServiceException, IOException, CsvException {
    InputStream inputStream =
        new ByteArrayInputStream(
            TestUtil.CSV_CONTENTS_WITHOUT_SELECTOR_CF_VALUE_FOR_TABLE.getBytes());

    CostTypeValidationResponse costTypeValidationResponse =
        testUtil.getCostTypeValidationResponse();
    costTypeValidationResponse.getSelectorCfInfo().get(0).setSelectorCfValue("");
    when(costConfigDashboardFeign.getCostTypesForValidation(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(costTypeValidationResponse).build());

    List<Object> objectList =
        processUploadCostDefinitionData.updateRequestObjectsList(
            testUtil.getJobDto(JobTypeEnum.UPLOAD_COST_DEFINITION), inputStream);

    Assertions.assertEquals(4, objectList.size());
    verify(costConfigDashboardFeign, times(1)).getCostTypesForValidation(anyString(), anyString());
  }

  @Test
  void updateRequestObjectsListWithoutSelectorCFStaticTableTest()
      throws CommonServiceException, IOException, CsvException {
    InputStream inputStream =
        new ByteArrayInputStream(
            TestUtil.CSV_CONTENTS_WITHOUT_SELECTOR_CF_FOR_STATIC_TABLE.getBytes());

    when(costConfigDashboardFeign.getCostTypesForValidation(anyString(), anyString()))
        .thenReturn(
            BaseResponse.builder()
                .payload(testUtil.getCostTypeDetailsWithEmptySelectorCfForStaticTable())
                .build());

    List<Object> objectList =
        processUploadCostDefinitionData.updateRequestObjectsList(
            testUtil.getJobDto(JobTypeEnum.UPLOAD_COST_DEFINITION), inputStream);

    Assertions.assertEquals(1, objectList.size());
    verify(costConfigDashboardFeign, times(1)).getCostTypesForValidation(anyString(), anyString());
  }

  @Test
  void updateRequestObjectsListWithoutSelectorCFValueStaticTableTest()
      throws CommonServiceException, IOException, CsvException {
    InputStream inputStream =
        new ByteArrayInputStream(
            TestUtil.CSV_CONTENTS_WITHOUT_SELECTOR_CF_VALUE_FOR_STATIC_TABLE.getBytes());
    CostTypeValidationResponse costTypeValidationResponse =
        testUtil.getCostTypeValidationResponse();
    costTypeValidationResponse.getSelectorCfInfo().get(0).setSelectorCfValue("");
    when(costConfigDashboardFeign.getCostTypesForValidation(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(costTypeValidationResponse).build());

    List<Object> objectList =
        processUploadCostDefinitionData.updateRequestObjectsList(
            testUtil.getJobDto(JobTypeEnum.UPLOAD_COST_DEFINITION), inputStream);

    Assertions.assertEquals(1, objectList.size());
    verify(costConfigDashboardFeign, times(1)).getCostTypesForValidation(anyString(), anyString());
  }
}
