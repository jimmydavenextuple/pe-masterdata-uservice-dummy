package com.nextuple.node.mapper;

import com.nextuple.common.mapper.GenericRequestResponseMapper;
import com.nextuple.node.domain.NodeRequest;
import com.nextuple.node.domain.NodeResponse;
import com.nextuple.node.dto.NodeEntity;
import com.nextuple.node.dto.key.NodeEntityKey;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NodeRequestResponseEntityMapper extends GenericRequestResponseMapper<NodeRequest, NodeResponse, NodeEntityKey, NodeEntity> {

}
