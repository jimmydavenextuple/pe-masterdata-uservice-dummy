package com.hbc.jobs.consumers.domain.mapper;

import com.hbc.calendar.domain.inbound.NodeCalendarRequest;
import com.hbc.jobs.framework.common.domain.pojo.NodeCalendarUpload;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NodeCalendarRequestMapper {

  NodeCalendarRequest convertToNodeCalendarRequest(NodeCalendarUpload nodeCalendarUpload);
}
