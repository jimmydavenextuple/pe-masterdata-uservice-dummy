package com.hbc.jobs.consumers.service;

import com.hbc.csvdownload.common.pojo.TransitDataUpload;
import com.hbc.jobs.consumers.domain.mapper.TransitDataUploadMapper;
import com.hbc.jobs.consumers.exception.TransitMapperException;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.RecordInputDto;
import com.hbc.transit.domain.feign.TransitFeign;
import java.util.Map;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TransitMapper implements FeignClientMapper {

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
      if (jobTypeEnum == JobTypeEnum.UPLOAD_TRANSIT_TIMES) {
        return TransitDataUpload.class;
      }
      log.error("Unable to map an object!");
      throw new TransitMapperException(
          "Error while mapping an object to the expected object", jobTypeEnum);
    } catch (Exception e) {
      log.error("Error while mapping to DTO");
      throw new TransitMapperException(
          "Exception while mapping an object to expected object", e, jobTypeEnum);
    }
  }

  @Override
  public ResponseEntity<?> callApi(Object request, RecordInputDto inputs)
      throws TransitMapperException {
    if (jobTypeEnum == JobTypeEnum.UPLOAD_TRANSIT_TIMES) {
      var transitDataUpload = (TransitDataUpload) request;
      if (DELETE_D.equalsIgnoreCase(transitDataUpload.getActionType())) {
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
    }
    log.error("Failed to make a call based on job type");
    throw new TransitMapperException("Please provide the valid job type", jobTypeEnum);
  }
}
