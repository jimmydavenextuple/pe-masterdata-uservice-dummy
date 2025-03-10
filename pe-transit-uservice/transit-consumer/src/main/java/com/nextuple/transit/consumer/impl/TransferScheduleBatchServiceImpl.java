package com.nextuple.transit.consumer.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.master.data.integration.enums.ActionEnum;
import com.nextuple.master.data.integration.enums.TaskInformation;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.service.BatchService;
import com.nextuple.transit.consumer.dto.TransferScheduleDto;
import com.nextuple.transit.consumer.mapper.TransferScheduleBatchMapper;
import com.nextuple.transit.domain.feign.TransferScheduleFeign;
import com.nextuple.transit.persistence.entity.TransferScheduleEntity;
import com.nextuple.transit.persistence.repository.TransferScheduleRepository;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferScheduleBatchServiceImpl extends BatchService<TransferScheduleDto> {
  private TransferScheduleFeign transferScheduleFeign;
  private TransferScheduleRepository transferScheduleRepository;

  private final TypeReference<BatchRequest<TransferScheduleDto>> transferScheduleTypeReference =
      new TypeReference<>() {};

  private static final Logger logger =
      LoggerFactory.getLogger(TransferScheduleBatchServiceImpl.class);

  // create and implement a mapper
  public static final TransferScheduleBatchMapper INSTANCE =
      Mappers.getMapper(TransferScheduleBatchMapper.class);

  @Override
  public TaskInformation getTaskInformation() {
    return TaskInformation.TRANSFER_SCHEDULE;
  }

  @Override
  public TypeReference<BatchRequest<TransferScheduleDto>> getTypeReference() {
    return transferScheduleTypeReference;
  }

  @Override
  public String createRecordImpl(TransferScheduleDto payload) throws CommonServiceException {
    return transferScheduleFeign
        .createTransferSchedule(INSTANCE.toTransferScheduleCreateRequest(payload))
        .getMessage();
  }

  @Override
  public String updateRecordImpl(TransferScheduleDto payload) throws CommonServiceException {
    handleInvalidAction(ActionEnum.UPDATE);
    return "";
  }

  @Override
  public String deleteRecordImpl(TransferScheduleDto payload) throws CommonServiceException {
    return transferScheduleFeign
        .deleteTransferSchedule(INSTANCE.toTransferScheduleRequest(payload))
        .getMessage();
  }

  @Override
  public void checkForOutdatedRecord(
      BatchRequest<TransferScheduleDto> transferScheduleDtoBatchRequest)
      throws CommonServiceException {
    TransferScheduleDto transferScheduleDto = transferScheduleDtoBatchRequest.getPayload();
    String orgId = transferScheduleDto.getOrgId();
    String sourceNodeId = transferScheduleDto.getSourceNodeId();
    String dropOffNodeId = transferScheduleDto.getDropoffNodeId();
    Date startTime = transferScheduleDto.getStartTime().toDate();

    if (Objects.nonNull(orgId)
        && Objects.nonNull(sourceNodeId)
        && Objects.nonNull(dropOffNodeId)
        && Objects.nonNull(startTime)) {
      Optional<TransferScheduleEntity> transferScheduleEntity =
          transferScheduleRepository.findBySourceNodeIdAndDropoffNodeIdAndStartTimeAndOrgId(
              sourceNodeId, dropOffNodeId, startTime, orgId);
      if (transferScheduleEntity.isPresent()
          && (transferScheduleEntity
              .get()
              .getLastModifiedDate()
              .after(transferScheduleDtoBatchRequest.getReceivedTimestamp()))) {
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(
            "receivedTimestamp",
            FieldError.builder()
                .rejectedValue(transferScheduleDtoBatchRequest.getReceivedTimestamp())
                .build());
        errorMap.put(
            "lastUpdatedTimestamp",
            FieldError.builder()
                .rejectedValue(transferScheduleEntity.get().getLastModifiedDate())
                .build());
        throw new CommonServiceException(
            "Can't process the record as it's outdated", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
      }
    }
  }
}
