package com.hbc.dataupload.domain.mapper;

import com.hbc.dataupload.domain.dto.NodeListDto;
import com.hbc.node.domain.dto.NodeDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;


@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NodeMapper {

  NodeListDto toNodeListDto(NodeDto nodeDto);
}
