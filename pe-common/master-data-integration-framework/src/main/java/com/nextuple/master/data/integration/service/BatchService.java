/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.master.data.integration.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.common.enums.ActionEnum;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.ConfigException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.jobs.framework.common.utils.ExceptionUtils;
import com.nextuple.master.data.integration.dto.CommonMasterDataFieldsDto;
import com.nextuple.master.data.integration.dto.ResponseDto;
import com.nextuple.master.data.integration.enums.TaskInformation;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import feign.FeignException;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

public abstract class BatchService<T extends CommonMasterDataFieldsDto> { // NOSONAR

  private final ObjectMapper objectMapper = new ObjectMapper();
  @Autowired ErrorHandlingService errorHandlingService;
  private static final ExecutorService executors = Executors.newVirtualThreadPerTaskExecutor();

  @PostConstruct
  public void init() {
    objectMapper.registerModule(new JodaModule());
  }

  public String handleRetry(BatchRequest<?> batchRequest) {
    try {
      TypeReference<BatchRequest<T>> typeReference = getTypeReference();
      BatchRequest<T> request = objectMapper.convertValue(batchRequest, typeReference);
      return makeFeignCallBasedOnAction(request);
    } catch (CommonServiceException e) {
      throw new ConfigException(e.getMessage());
    }
  }

  public void addTaskForSuccessResponse(BatchRequest<T> batchRequest) {
    errorHandlingService.handleSuccessResponse(
        batchRequest,
        batchRequest.getPayload().getOrgId(),
        getTaskInformation(),
        this.getClass().getName());
  }

  public void addTaskForException(
      int statusCode, String errorMessage, BatchRequest<T> batchRequest) {
    errorHandlingService.handleExceptions(
        statusCode,
        errorMessage,
        batchRequest,
        getTaskInformation(),
        batchRequest.getPayload().getOrgId(),
        this.getClass().getName());
  }

  public abstract TaskInformation getTaskInformation();

  public ResponseDto processRecordBasedOnAction(
      BatchRequest<T> batchRequest, Boolean isRetryRequired) {
    ResponseDto responseDto = new ResponseDto();
    try {
      String responseMessage = makeFeignCallBasedOnAction(batchRequest);
      responseDto.setStatusCode(HttpStatus.OK.value());
      responseDto.setMessage(responseMessage);
      if (Boolean.TRUE.equals(isRetryRequired)) addTaskForSuccessResponse(batchRequest);

    } catch (FeignException feignException) {
      responseDto.setStatusCode(feignException.status());
      var errorResponse = ExceptionUtils.parseFeignException(feignException);
      String errorMessage = errorResponse.getMessage();
      if (errorResponse.getPayload() != null
          && !CollectionUtils.isEmpty(errorResponse.getPayload().getFields())) {
        Map<String, FieldError> errorFieldsMap = errorResponse.getPayload().getFields();
        errorMessage =
            errorFieldsMap.keySet().stream()
                .filter(key -> !ObjectUtils.isEmpty(errorFieldsMap.get(key).getErrorMessage()))
                .map(key -> errorFieldsMap.get(key).getErrorMessage())
                .findFirst()
                .orElse(errorResponse.getMessage());
      }
      responseDto.setMessage(errorMessage);

      if (Boolean.TRUE.equals(isRetryRequired))
        addTaskForException(feignException.status(), responseDto.getMessage(), batchRequest);
    } catch (Exception e) {
      responseDto.setStatusCode(HttpStatus.BAD_REQUEST.value());
      responseDto.setMessage(e.getMessage());
      if (Boolean.TRUE.equals(isRetryRequired))
        addTaskForException(HttpStatus.BAD_REQUEST.value(), e.getMessage(), batchRequest);
    } finally {
      CurrentThreadContext.cleanLogContext();
    }
    return responseDto;
  }

  public String makeFeignCallBasedOnAction(BatchRequest<T> batchRequest)
      throws CommonServiceException {
    TypeReference<BatchRequest<T>> typeReference = getTypeReference();
    BatchRequest<T> request = objectMapper.convertValue(batchRequest, typeReference);
    T payload = request.getPayload();
    setTenantIdInLogContext(payload.getOrgId());
    if (Objects.nonNull(batchRequest.getReceivedTimestamp())) checkForOutdatedRecord(batchRequest);
    var action = batchRequest.getAction();
    switch (action) {
      case CREATE:
        return createRecordImpl(payload);
      case UPDATE:
        return updateRecordImpl(payload);
      case DELETE:
        return deleteRecordImpl(payload);
      default:
        handleInvalidAction(action);
    }
    return "";
  }

  public abstract TypeReference<BatchRequest<T>> getTypeReference();

  public static void handleInvalidAction(ActionEnum action) throws CommonServiceException {
    throw new CommonServiceException(
        "Action not supported : " + action, HttpStatus.BAD_REQUEST, 0x1771, null);
  }

  public abstract String createRecordImpl(T payload) throws CommonServiceException;

  public abstract String updateRecordImpl(T payload) throws CommonServiceException;

  public abstract String deleteRecordImpl(T payload) throws CommonServiceException;

  public abstract void checkForOutdatedRecord(BatchRequest<T> batchRequest)
      throws CommonServiceException;

  public BatchResponse processRecordsWithRetry(List<BatchRequest<T>> batchRequestList) {
    return processBatchRecords(batchRequestList, Boolean.TRUE);
  }

  public BatchResponse processRecordsWithoutRetry(List<BatchRequest<T>> batchRequestList) {
    return processBatchRecords(batchRequestList, Boolean.FALSE);
  }

  public BatchResponse processBatchRecords(
      List<BatchRequest<T>> batchRequestList, Boolean isRetryRequired) {
    batchRequestList = sortRecordsOnBasisOfProducedTimestamp(batchRequestList);
    int successfulRecords = 0;
    BatchResponse batchResponse = new BatchResponse();
    List<ResponseDto> responseDtoList = new ArrayList<>();
    List<Callable<ResponseDto>> tasks =
        batchRequestList.stream()
            .map(
                batchRequest ->
                    (Callable<ResponseDto>)
                        () -> {
                          ResponseDto responseDto =
                              processRecordBasedOnAction(batchRequest, isRetryRequired);
                          if (Objects.nonNull(batchRequest.getRecordNo()))
                            responseDto.setRecordNo(batchRequest.getRecordNo());
                          return responseDto;
                        })
            .toList();

    try {
      List<Future<ResponseDto>> futures = executors.invokeAll(tasks);
      for (Future<ResponseDto> future : futures) {
        ResponseDto responseDto = future.get();
        responseDtoList.add(responseDto);
        if (responseDto.getStatusCode() == HttpStatus.OK.value()) {
          successfulRecords++;
        }
      }
    } catch (InterruptedException | ExecutionException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Error processing batch records", e);
    }

    batchResponse.setTotalRecords(batchRequestList.size());
    batchResponse.setSuccessfulRecords(successfulRecords);
    batchResponse.setFailedRecords(batchRequestList.size() - successfulRecords);
    batchResponse.setResponses(responseDtoList);
    return batchResponse;
  }

  protected List<BatchRequest<T>> sortRecordsOnBasisOfProducedTimestamp(
      List<BatchRequest<T>> batchRequestList) {
    List<BatchRequest<T>> batchRequestListWithNoProducedTimestamp =
        batchRequestList.stream()
            .filter(batchRequest -> Objects.isNull(batchRequest.getProducedTimestamp()))
            .collect(Collectors.toList());
    if (batchRequestListWithNoProducedTimestamp.isEmpty())
      return batchRequestList.stream()
          .sorted(Comparator.comparing(BatchRequest::getProducedTimestamp))
          .collect(Collectors.toList());
    return batchRequestList;
  }

  private void setTenantIdInLogContext(String orgId) {
    String currentTenant = CurrentThreadContext.getLogContext().getTenantId();
    if (Objects.isNull(currentTenant) || !currentTenant.equals(orgId))
      CurrentThreadContext.getLogContext().setTenantId(orgId);
  }
}
