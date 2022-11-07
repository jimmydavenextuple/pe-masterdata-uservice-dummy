package com.hbc.jobs.consumers.service;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.DELETE;
import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.UPDATE;

import com.hbc.carrier.domain.feign.CarrierFeign;
import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.csvdownload.exception.CsvDataValidationException;
import com.hbc.jobs.consumers.domain.mapper.CarrierServiceRequestMapper;
import com.hbc.jobs.consumers.exception.InvalidActionTypeException;
import com.hbc.jobs.consumers.exception.NodeCarrierMapperException;
import com.hbc.jobs.consumers.exception.TransitMapperException;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.CarrierServiceUpload;
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
public class CarrierMapper implements FeignClientMapper {

  @Setter private JobTypeEnum jobType;

  private final CarrierFeign carrierFeign;

  public static final CarrierServiceRequestMapper INSTANCE =
      Mappers.getMapper(CarrierServiceRequestMapper.class);

  private final Logger logger = LoggerFactory.getLogger(CarrierMapper.class);

  @Override
  public ModuleEnum getModule() {
    return ModuleEnum.CARRIER_SERVICE;
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
  public Class mapTODto() throws TransitMapperException, NodeCarrierMapperException {
    return CarrierServiceUpload.class;
  }

  @Override
  public ResponseEntity<?> callApi(Object request, RecordInputDto inputs)
      throws TransitMapperException, NodeCarrierMapperException, InvalidActionTypeException,
          CommonServiceException {
    var carrierServiceUpload = (CarrierServiceUpload) request;
    String action = carrierServiceUpload.getAction();
    switch (action) {
      case CREATE:
        return ResponseEntity.ok(
            carrierFeign.createCarrierService(
                INSTANCE.convertToCarrierServiceRequest(carrierServiceUpload)));
      case UPDATE:
        return ResponseEntity.ok(
            carrierFeign.updateCarrierServiceDetails(
                carrierServiceUpload.getCarrierId(),
                carrierServiceUpload.getCarrierServiceId(),
                carrierServiceUpload.getOrgId(),
                INSTANCE.convertToCarrierServiceUpdateRequest(carrierServiceUpload)));
      case DELETE:
        return ResponseEntity.ok(
            carrierFeign.deleteCarrierService(
                carrierServiceUpload.getCarrierId(),
                carrierServiceUpload.getCarrierServiceId(),
                carrierServiceUpload.getOrgId()));
      default:
        {
          logger.error("Invalid action type: {}", carrierServiceUpload.getAction());
          throw new CsvDataValidationException(
              "Please provide the valid action: " + carrierServiceUpload.getAction());
        }
    }
  }
}
