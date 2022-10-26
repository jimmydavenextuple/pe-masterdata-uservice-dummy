package com.hbc.jobs.consumers.service;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CREATE;

import com.hbc.calendar.domain.feign.CalendarFeign;
import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.csvdownload.exception.CsvDataValidationException;
import com.hbc.jobs.consumers.domain.mapper.NodeCalendarRequestMapper;
import com.hbc.jobs.consumers.exception.InvalidActionTypeException;
import com.hbc.jobs.consumers.exception.NodeCarrierMapperException;
import com.hbc.jobs.consumers.exception.TransitMapperException;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.NodeCalendarUpload;
import com.hbc.jobs.framework.common.domain.pojo.RecordInputDto;
import com.hbc.jobs.framework.common.enums.ModuleEnum;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NodeCalendarMapper implements FeignClientMapper {

  private final CalendarFeign calendarFeign;

  private final Logger logger = LoggerFactory.getLogger(NodeCalendarMapper.class);

  @Setter private JobTypeEnum jobType;

  public static final NodeCalendarRequestMapper INSTANCE =
      Mappers.getMapper(NodeCalendarRequestMapper.class);

  @Override
  public ModuleEnum getModule() {
    return ModuleEnum.NODE_CALENDAR;
  }

  @Override
  public void setJobType(JobTypeEnum jobType) {
    this.jobType = jobType;
  }

  @Override
  public Object getDTOFromCustomMapper(String stringRecord) {
    return null;
  }

  @Override
  public Map<String, String> getColumnNameMapping(String[] headerColumns) {
    return null;
  }

  @Override
  public Class mapTODto() {
    return NodeCalendarUpload.class;
  }

  @Override
  public ResponseEntity<?> callApi(Object request, RecordInputDto inputs)
      throws TransitMapperException, NodeCarrierMapperException, InvalidActionTypeException,
          CommonServiceException {
    var nodeCalendarUpload = (NodeCalendarUpload) request;
    String action = nodeCalendarUpload.getAction();

    if (CREATE.equals(action)) {
      return ResponseEntity.ok(
          calendarFeign.createNodeCalendar(
              INSTANCE.convertToNodeCalendarRequest(nodeCalendarUpload)));
    }
    logger.error("Invalid action type: {}", nodeCalendarUpload.getAction());
    throw new CsvDataValidationException(
        "Please provide the valid action: " + nodeCalendarUpload.getAction());
  }
}
