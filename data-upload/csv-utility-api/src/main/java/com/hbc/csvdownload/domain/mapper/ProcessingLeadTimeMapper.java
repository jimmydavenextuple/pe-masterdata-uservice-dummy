package com.hbc.csvdownload.domain.mapper;

import com.hbc.csvdownload.common.pojo.ProcessingLeadTime;
import com.hbc.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProcessingLeadTimeMapper {

  ProcessingLeadTime convertToNodeCarrierRequest(ProcessingLeadTimesRaw processingLeadTimesRaw);
}
