/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.service;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.IS_FRIDAY_WORKING;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.IS_MONDAY_WORKING;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.IS_SATURDAY_WORKING;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.IS_SUNDAY_WORKING;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.IS_THURSDAY_WORKING;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.IS_TUESDAY_WORKING;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.IS_WEDNESDAY_WORKING;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nextuple.calendar.domain.feign.CalendarFeign;
import com.nextuple.calendar.domain.outbound.CalendarResponse;
import com.nextuple.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.util.BooleanUtil;
import com.nextuple.csvdownload.exception.CsvDataValidationException;
import com.nextuple.jobs.consumers.domain.mapper.NodeCalendarRequestMapper;
import com.nextuple.jobs.consumers.exception.InvalidActionTypeException;
import com.nextuple.jobs.consumers.exception.InvalidJobTypeException;
import com.nextuple.jobs.consumers.exception.NodeCarrierMapperException;
import com.nextuple.jobs.consumers.exception.TransitMapperException;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.CalendarDataUpload;
import com.nextuple.jobs.framework.common.domain.pojo.CarrierCalendarUpload;
import com.nextuple.jobs.framework.common.domain.pojo.NodeCalendarUpload;
import com.nextuple.jobs.framework.common.domain.pojo.PickUpCalendarUpload;
import com.nextuple.jobs.framework.common.domain.pojo.RecordInputDto;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalendarMapper implements FeignClientMapper {

  private static final String INVALID_ACTION_TYPE_ERROR_MESSAGE =
      "Please provide the valid action: ";
  private final CalendarFeign calendarFeign;

  private final Logger logger = LoggerFactory.getLogger(CalendarMapper.class);

  private JobTypeEnum jobType;

  public static final NodeCalendarRequestMapper INSTANCE =
      Mappers.getMapper(NodeCalendarRequestMapper.class);

  @Override
  public ModuleEnum getModule() {
    return ModuleEnum.CALENDAR;
  }

  @Override
  public void setJobType(JobTypeEnum jobType) {
    this.jobType = jobType;
  }

  @Override
  public Class mapTODto() throws InvalidJobTypeException { // NOSONAR
    switch (jobType) {
      case UPLOAD_NODE_CALENDER:
        return NodeCalendarUpload.class;
      case UPLOAD_CARRIER_SERVICE_CALENDER:
        return CarrierCalendarUpload.class;
      case UPLOAD_CALENDER:
        return CalendarDataUpload.class;
      case UPLOAD_PICKUP_CALENDER:
        return PickUpCalendarUpload.class;
      default:
        {
          logger.error("Invalid Job type: {}", jobType);
          throw new InvalidJobTypeException("Invalid Job type", jobType.name());
        }
    }
  }

  @Override
  public ResponseEntity<?> callApi(Object request, RecordInputDto inputs)
      throws TransitMapperException,
          NodeCarrierMapperException,
          InvalidActionTypeException,
          CommonServiceException,
          InvalidJobTypeException,
          JsonProcessingException {
    switch (jobType) {
      case UPLOAD_NODE_CALENDER:
        return invokeUploadNodeCalendarApis((NodeCalendarUpload) request);
      case UPLOAD_CARRIER_SERVICE_CALENDER:
        return invokeCarrierCalendarUploadApis((CarrierCalendarUpload) request);
      case UPLOAD_CALENDER:
        return invokeCalendarDataUploadApis((CalendarDataUpload) request);
      case UPLOAD_PICKUP_CALENDER:
        return invokePickupCalendarUploadApis((PickUpCalendarUpload) request);
      default:
        {
          logger.error("Invalid job type: {}", jobType);
          throw new InvalidJobTypeException("Invalid job type", jobType.name());
        }
    }
  }

  private ResponseEntity<?> invokePickupCalendarUploadApis(PickUpCalendarUpload request) {
    if (CREATE.equals(request.getAction())) {
      return ResponseEntity.ok(
          calendarFeign.createNodeCarrierServiceCalendar(
              INSTANCE.convertToNodeCarrierServiceCalendarRequest(request)));
    }
    logger.error("Invalid action provided: {}", request.getAction());
    throw new CsvDataValidationException(INVALID_ACTION_TYPE_ERROR_MESSAGE + request.getAction());
  }

  private static void validateBooleanDays(CalendarDataUpload request)
      throws CommonServiceException {
    BooleanUtil.checkValidBooleanValue(request.getIsMondayWorking(), IS_MONDAY_WORKING);
    BooleanUtil.checkValidBooleanValue(request.getIsTuesdayWorking(), IS_TUESDAY_WORKING);
    BooleanUtil.checkValidBooleanValue(request.getIsWednesdayWorking(), IS_WEDNESDAY_WORKING);
    BooleanUtil.checkValidBooleanValue(request.getIsThursdayWorking(), IS_THURSDAY_WORKING);
    BooleanUtil.checkValidBooleanValue(request.getIsFridayWorking(), IS_FRIDAY_WORKING);
    BooleanUtil.checkValidBooleanValue(request.getIsSaturdayWorking(), IS_SATURDAY_WORKING);
    BooleanUtil.checkValidBooleanValue(request.getIsSundayWorking(), IS_SUNDAY_WORKING);
  }

  private ResponseEntity<BaseResponse<CalendarResponse>> invokeCalendarDataUploadApis(
      CalendarDataUpload request) throws CommonServiceException, JsonProcessingException {
    validateBooleanDays(request);
    if (CREATE.equals(request.getAction())) {
      return ResponseEntity.ok(
          calendarFeign.createCalendar(INSTANCE.convertToCalendarRequest(request)));
    }
    logger.error("Invalid action: {}", request.getAction());
    throw new CsvDataValidationException(INVALID_ACTION_TYPE_ERROR_MESSAGE + request.getAction());
  }

  private ResponseEntity<BaseResponse<CarrierServiceCalendarResponse>>
      invokeCarrierCalendarUploadApis(CarrierCalendarUpload request) {
    String action = request.getAction();
    if (CREATE.equals(action)) {
      return ResponseEntity.ok(
          calendarFeign.createCarrierServiceCalendar(
              INSTANCE.convertToCarrierServiceCalendarRequest(request)));
    }
    logger.error("Invalid action type: {}", request.getAction());
    throw new CsvDataValidationException(INVALID_ACTION_TYPE_ERROR_MESSAGE + request.getAction());
  }

  private ResponseEntity<BaseResponse<NodeCalendarResponse>> invokeUploadNodeCalendarApis(
      NodeCalendarUpload request) {

    String action = request.getAction();

    if (CREATE.equals(action)) {
      return ResponseEntity.ok(
          calendarFeign.createNodeCalendar(INSTANCE.convertToNodeCalendarRequest(request)));
    }
    logger.error("Invalid action type: {}", request.getAction());
    throw new CsvDataValidationException(INVALID_ACTION_TYPE_ERROR_MESSAGE + request.getAction());
  }

  @Override
  public Map<String, String> getColumnNameMapping(String[] headerColumns) {
    return new HashMap<>();
  }

  @Override
  public Object getDTOFromCustomMapper(String stringRecord) {
    return null;
  }
}
