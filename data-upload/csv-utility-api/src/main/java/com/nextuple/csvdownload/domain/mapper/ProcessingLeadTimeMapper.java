package com.nextuple.csvdownload.domain.mapper;

import com.nextuple.csvdownload.common.pojo.ProcessingLeadTime;
import com.nextuple.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProcessingLeadTimeMapper {

  ProcessingLeadTime convertToNodeCarrierRequest(ProcessingLeadTimesRaw processingLeadTimesRaw);
}
