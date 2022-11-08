package com.nextuple.jobs.consumers.service;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CREATE;

import com.nextuple.calendar.domain.feign.CalendarFeign;
import com.nextuple.calendar.domain.outbound.CalendarResponse;
import com.nextuple.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
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
