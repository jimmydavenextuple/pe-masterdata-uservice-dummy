package com.nextuple.dataupload.domain.mapper;

import com.nextuple.dataupload.domain.dto.NodeListDto;
import com.nextuple.node.domain.dto.NodeDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NodeMapper {

  NodeListDto toNodeListDto(NodeDto nodeDto);
}
