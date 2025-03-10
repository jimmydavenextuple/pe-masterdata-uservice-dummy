package com.nextuple.transit.consumer.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.master.data.integration.enums.ActionEnum;
import com.nextuple.master.data.integration.enums.TaskInformation;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.service.BatchService;
import com.nextuple.transit.consumer.dto.TransferScheduleDto;
import com.nextuple.transit.consumer.mapper.TransferScheduleBatchMapper;
import com.nextuple.transit.domain.feign.TransferScheduleFeign;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferScheduleBatchServiceImpl extends BatchService<TransferScheduleDto> {
    private TransferScheduleFeign transferScheduleFeign;
    private final TypeReference<BatchRequest<TransferScheduleDto>> transferScheduleTypeReference =
            new TypeReference<>() {};

    private static final Logger logger = LoggerFactory.getLogger(TransferScheduleBatchServiceImpl.class);

    //create and implement a mapper
    public static final TransferScheduleBatchMapper INSTANCE = Mappers.getMapper(TransferScheduleBatchMapper.class);

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
        return transferScheduleFeign.createTransferSchedule(INSTANCE.toTransferScheduleCreateRequest(payload)).getMessage();
    }

    @Override
    public String updateRecordImpl(TransferScheduleDto payload) throws CommonServiceException {
        handleInvalidAction(ActionEnum.UPDATE);
        return "";
    }

    @Override
    public String deleteRecordImpl(TransferScheduleDto payload) throws CommonServiceException {
        return transferScheduleFeign.deleteTransferSchedule(INSTANCE.toTransferScheduleRequest(payload)).getMessage();
    }

    @Override
    public void checkForOutdatedRecord(BatchRequest<TransferScheduleDto> transferScheduleDtoBatchRequest) throws CommonServiceException {
//        TransferScheduleDto transferScheduleDto = transferScheduleDtoBatchRequest.getPayload();
//        String orgId = transferScheduleDto.getOrgId();
//        transferScheduleDto.get
//        if (Objects.nonNull(nodeId) && Objects.nonNull(orgId)) {
//            try {
//                Optional<NodeDomainDto> nodeDomainDto =
//                        nodePersistenceService.findNodeByNodeIdAndOrgId(nodeId, orgId);
//                if (nodeDomainDto.isPresent()
//                        && (nodeDomainDto
//                        .get()
//                        .getLastModifiedDate()
//                        .after(nodeBatchRequest.getReceivedTimestamp()))) {
//                    Map<String, FieldError> errorMap = new HashMap<>();
//                    errorMap.put(
//                            "receivedTimestamp",
//                            FieldError.builder().rejectedValue(nodeBatchRequest.getReceivedTimestamp()).build());
//                    errorMap.put(
//                            "lastUpdatedTimestamp",
//                            FieldError.builder()
//                                    .rejectedValue(nodeDomainDto.get().getLastModifiedDate())
//                                    .build());
//                    throw new CommonServiceException(
//                            "Can't process the record as it's outdated",
//                            HttpStatus.BAD_REQUEST,
//                            0x1771,
//                            errorMap);
//                }
//            } catch (NodeDomainException e) {
//                log.debug(
//                        "Cannot check for outdated record as the given node does not exist for the given details orgId:{} nodeId:{}",
//                        orgId,
//                        nodeId);
//            }
//        }
    }
}
