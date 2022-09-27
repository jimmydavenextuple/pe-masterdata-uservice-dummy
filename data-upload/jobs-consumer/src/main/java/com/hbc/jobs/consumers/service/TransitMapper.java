package com.hbc.jobs.consumers.service;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.csvdownload.common.pojo.TransitDataUpload;
import com.hbc.csvdownload.exception.CsvDataValidationException;
import com.hbc.jobs.consumers.domain.mapper.TransitDataUploadMapper;
import com.hbc.jobs.consumers.exception.TransitMapperException;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.RecordInputDto;
import com.hbc.transit.domain.feign.TransitFeign;
import java.util.Map;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
public class TransitMapper implements FeignClientMapper {

  private final Logger logger = LoggerFactory.getLogger(TransitMapper.class);

  private static final String DELETE_D = "D";
  private final TransitFeign transitFeign;

  public TransitMapper(TransitFeign transitFeign) {
    this.transitFeign = transitFeign;
  }

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
      var transitDataUpload = (TransitDataUpload) request;
      var transitDaysString = transitDataUpload.getTransitDays();
      validateActionTypeAndTransitDays(transitDataUpload, transitDaysString);
      if (DELETE_D.equalsIgnoreCase(transitDaysString)) {
        return ResponseEntity.ok(
            transitFeign.deleteTransitDetails(
                transitDataUpload.getOrgId(),
                transitDataUpload.getSourceGeozone(),
                transitDataUpload.getDestinationGeozone(),
                transitDataUpload.getCarrierServiceId()));
      } else {
        return ResponseEntity.ok(
            transitFeign.addTransitData(INSTANCE.convertToTransitDataRequest(transitDataUpload)));
      }
    } else if (jobTypeEnum == JobTypeEnum.DELETE_TRANSIT_BUFFER) {
      var transitDataUpload = (TransitDataUpload) request;
      return ResponseEntity.ok(
          transitFeign.deleteBufferDays(
              transitDataUpload.getOrgId(),
              transitDataUpload.getCarrierServiceId(),
              transitDataUpload.getSourceGeozone(),
              transitDataUpload.getDestinationGeozone()));
    }
    logger.error("Failed to make a call based on job type");
    throw new TransitMapperException("Please provide the valid job type", jobTypeEnum);
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
