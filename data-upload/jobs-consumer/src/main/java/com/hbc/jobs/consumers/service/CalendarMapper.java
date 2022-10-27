package com.hbc.jobs.consumers.service;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CREATE;

import com.hbc.calendar.domain.feign.CalendarFeign;
import com.hbc.calendar.domain.outbound.CalendarResponse;
import com.hbc.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.hbc.calendar.domain.outbound.NodeCalendarResponse;
import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.exception.CsvDataValidationException;
import com.hbc.jobs.consumers.domain.mapper.NodeCalendarRequestMapper;
import com.hbc.jobs.consumers.exception.InvalidActionTypeException;
import com.hbc.jobs.consumers.exception.InvalidJobTypeException;
import com.hbc.jobs.consumers.exception.NodeCarrierMapperException;
import com.hbc.jobs.consumers.exception.TransitMapperException;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.CalendarDataUpload;
import com.hbc.jobs.framework.common.domain.pojo.CarrierCalendarUpload;
import com.hbc.jobs.framework.common.domain.pojo.NodeCalendarUpload;
import com.hbc.jobs.framework.common.domain.pojo.PickUpCalendarUpload;
import com.hbc.jobs.framework.common.domain.pojo.RecordInputDto;
import com.hbc.jobs.framework.common.enums.ModuleEnum;
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
      throws TransitMapperException, NodeCarrierMapperException, InvalidActionTypeException,
          CommonServiceException, InvalidJobTypeException {
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

  private ResponseEntity<BaseResponse<CalendarResponse>> invokeCalendarDataUploadApis(
      CalendarDataUpload request) {
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
    return null;
  }

  @Override
  public Object getDTOFromCustomMapper(String stringRecord) {
    return null;
  }
}
