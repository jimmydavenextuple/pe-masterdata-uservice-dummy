/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.controller;

import static com.nextuple.csvdownload.common.constants.CSVCommonConstants.CART_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.ROW_NUMBER;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.csvdownload.common.inbound.GenericUploadRequest;
import com.nextuple.csvdownload.service.EddComputationService;
import com.opencsv.exceptions.CsvException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class EddComputationUtilityControllerTest {

  @Mock private EddComputationService eddComputationService;

  @InjectMocks private EddComputationUtilityController eddComputationUtilityController;

  @Test
  void eddComputationDataTest() throws CommonServiceException, IOException, CsvException {
    HttpServletResponse response = mock(HttpServletResponse.class);

    doNothing().when(response).setStatus(HttpStatus.OK.value());
    ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);

    when(response.getOutputStream()).thenReturn(servletOutputStream);
    when(eddComputationService.uploadEddCompuationData(GenericUploadRequest.builder().build()))
        .thenReturn(File.createTempFile("BAY", "BAY"));

    eddComputationUtilityController.eddComputationData(
        GenericUploadRequest.builder().build(), response);
    verify(response, times(1)).getOutputStream();
  }

  @Test
  void eddComputationDataExceptionTest() throws CommonServiceException, IOException, CsvException {
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(eddComputationService.uploadEddCompuationData(GenericUploadRequest.builder().build()))
        .thenThrow(IOException.class);
    Assertions.assertThrows(
        IOException.class,
        () ->
            eddComputationUtilityController.eddComputationData(
                GenericUploadRequest.builder().build(), response));
  }

  @DisplayName("Edd Computation request: failure path")
  @Test
  void eddComputationDataCartIDExceptionTest()
      throws CommonServiceException, IOException, CsvException {
    HttpServletResponse response = mock(HttpServletResponse.class);
    Map<String, FieldError> errorMap = new HashMap<>();
    int row = 3;
    errorMap.put(ROW_NUMBER, FieldError.builder().rejectedValue(row).build());
    errorMap.put(CART_ID, FieldError.builder().rejectedValue("ABC").build());
    when(eddComputationService.uploadEddCompuationData(GenericUploadRequest.builder().build()))
        .thenThrow(
            new CommonServiceException(
                "Cart id is blank at row: " + row, HttpStatus.BAD_REQUEST, 0x1773, errorMap));
    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              eddComputationUtilityController.eddComputationData(
                  GenericUploadRequest.builder().build(), response);
            });

    Assertions.assertEquals("Cart id is blank at row: 3", exception.getMessage());
  }
}
