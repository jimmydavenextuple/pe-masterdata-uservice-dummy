package com.nextuple.transit.consumer.mapper;

import com.nextuple.transit.consumer.dto.TransferScheduleDto;
import com.nextuple.transit.domain.inbound.TransferScheduleRequest;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import com.nextuple.transit.domain.inbound.TransferScheduleCreationRequest;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TransferScheduleBatchMapper {

    TransferScheduleCreationRequest toTransferScheduleCreateRequest(TransferScheduleDto transferScheduleDto);

    TransferScheduleRequest toTransferScheduleRequest(TransferScheduleDto transferScheduleDto);
}
