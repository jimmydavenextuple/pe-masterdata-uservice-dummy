package com.hbc.jobs.consumers.service;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.response.BaseResponse;
import com.hbc.common.util.DateUtil;
import com.hbc.csvdownload.common.pojo.TransitDataUpload;
import com.hbc.csvdownload.exception.CsvDataValidationException;
import com.hbc.jobs.consumers.domain.mapper.TransitDataUploadMapper;
import com.hbc.jobs.consumers.exception.TransitMapperException;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.RecordInputDto;
import com.hbc.jobs.framework.common.domain.pojo.TransitBufferUpload;
import com.hbc.transit.domain.feign.TransitBufferFeign;
import com.hbc.transit.domain.feign.TransitFeign;
import com.hbc.transit.domain.outbound.TransitBufferResponse;
import com.hbc.transit.domain.outbound.TransitResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransitMapper implements FeignClientMapper {

  private final Logger logger = LoggerFactory.getLogger(TransitMapper.class);

  private static final String DELETE_D = "D";
  private static final String CREATE_C = "C";
  private static final String UPDATE_U = "U";
  private final TransitFeign transitFeign;
  private final TransitBufferFeign transitBufferFeign;

  @Setter private JobTypeEnum jobTypeEnum;

  public static final TransitDataUploadMapper INSTANCE =
      Mappers.getMapper(TransitDataUploadMapper.class);

  @Override
  public Object getDTOFromCustomMapper(String stringRecord) {
    return null;
  }

  @Override
  public Map<String, String> getColumnNameMapping(String[] headerColumns) {
    return getDefaultColumnNameMapping(headerColumns);
  }

  @Override
  public Class mapTODto() throws TransitMapperException {
    try {
      if (jobTypeEnum == JobTypeEnum.UPLOAD_TRANSIT_TIMES
          || jobTypeEnum == JobTypeEnum.DELETE_TRANSIT_BUFFER) {
        return TransitDataUpload.class;
      } else if (jobTypeEnum == JobTypeEnum.TRANSIT_BUFFER_REQUEST) {
        return TransitBufferUpload.class;
      }
      logger.error("Unable to map an object!");
      throw new TransitMapperException(
          "Error while mapping an object to the expected object", jobTypeEnum);
    } catch (Exception e) {
      logger.error("Error while mapping to DTO");
      throw new TransitMapperException(
          "Exception while mapping an object to expected object", e, jobTypeEnum);
    }
  }

  @Override
  public ResponseEntity<?> callApi(Object request, RecordInputDto inputs)
      throws TransitMapperException {
    if (jobTypeEnum == JobTypeEnum.UPLOAD_TRANSIT_TIMES) {
      return invokeApiForTransitTimeUpload((TransitDataUpload) request);
    } else if (jobTypeEnum == JobTypeEnum.DELETE_TRANSIT_BUFFER) {
      return invokeApiForTransitBufferDelete((TransitDataUpload) request);
    } else if (jobTypeEnum == JobTypeEnum.TRANSIT_BUFFER_REQUEST) {
      return invokeApiForTransitBufferRequestUpload((TransitBufferUpload) request);
    }
    logger.error("Failed to make a call based on job type");
    throw new TransitMapperException("Please provide the valid job type", jobTypeEnum);
  }

  private ResponseEntity<BaseResponse<TransitBufferResponse>>
      invokeApiForTransitBufferRequestUpload(TransitBufferUpload transitBufferUpload) {

    var transitBufferRequest = INSTANCE.convertToTransitBufferRequest(transitBufferUpload);
    transitBufferRequest.setBufferStartDate(
        DateUtil.getDateUTC(transitBufferUpload.getBufferStartDate()));
    transitBufferRequest.setBufferEndDate(
        DateUtil.getDateUTC(transitBufferUpload.getBufferEndDate()));

    if (CREATE_C.equalsIgnoreCase(transitBufferUpload.getAction())) {
      return ResponseEntity.ok(transitBufferFeign.createTransitBuffer(transitBufferRequest));
    } else if (UPDATE_U.equalsIgnoreCase(transitBufferUpload.getAction())) {
      return ResponseEntity.ok(transitBufferFeign.updateTransitBuffer(transitBufferRequest));
    } else if (DELETE_D.equalsIgnoreCase(transitBufferUpload.getAction())) {
      return ResponseEntity.ok(transitBufferFeign.deleteTransitBufferDetails(transitBufferRequest));
    }
    logger.error("Invalid action type: {}", transitBufferUpload.getAction());
    throw new CsvDataValidationException(
        "Please provide the valid action" + transitBufferUpload.getAction());
  }

  private ResponseEntity<BaseResponse<TransitResponse>> invokeApiForTransitBufferDelete(
      TransitDataUpload request) {
    return ResponseEntity.ok(
        transitFeign.updateTransitBufferDays(
            request.getOrgId(),
            request.getCarrierServiceId(),
            request.getSourceGeozone(),
            request.getDestinationGeozone()));
  }

  private ResponseEntity<BaseResponse<TransitResponse>> invokeApiForTransitTimeUpload(
      TransitDataUpload request) {
    var transitDaysString = request.getTransitDays();
    validateActionTypeAndTransitDays(request, transitDaysString);
    if (DELETE_D.equalsIgnoreCase(transitDaysString)) {
      return ResponseEntity.ok(
          transitFeign.deleteTransitDetails(
              request.getOrgId(),
              request.getSourceGeozone(),
              request.getDestinationGeozone(),
              request.getCarrierServiceId()));
    } else {
      return ResponseEntity.ok(
          transitFeign.addTransitData(INSTANCE.convertToTransitDataRequest(request)));
    }
  }

  private void validateActionTypeAndTransitDays(
      TransitDataUpload transitDataUpload, String transitDaysString) {
    if (!ObjectUtils.isEmpty(transitDaysString)
        && !NumberUtils.isCreatable(transitDaysString)
        && !DELETE_D.equalsIgnoreCase(transitDaysString)) {
      logger.error(
          "Invalid action or transit days: {} for destinationFsa: {} and sourceFsa: {}",
          transitDaysString,
          transitDataUpload.getDestinationGeozone(),
          transitDataUpload.getSourceGeozone());
      throw new CsvDataValidationException("Invalid action or transit days: " + transitDaysString);
    }

    if (!NumberUtils.isCreatable(transitDaysString)) {
      logger.error("");
    }
  }
}
