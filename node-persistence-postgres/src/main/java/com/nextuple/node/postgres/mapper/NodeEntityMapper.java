package com.nextuple.node.postgres.mapper;

import com.nextuple.common.persistence.postgres.mapper.GenericEntityMapper;
import com.nextuple.node.dto.key.NodeEntityKey;
import com.nextuple.node.postgres.dto.NodeEntity;
import com.nextuple.node.postgres.dto.NodeKey;
import org.mapstruct.factory.Mappers;

public class NodeEntityMapper
    implements GenericEntityMapper<NodeKey, NodeEntity, NodeEntityKey, com.nextuple.node.dto.NodeEntity> {

  NodeMapper nodeMapper = Mappers.getMapper(NodeMapper.class);
  @Override
  public NodeEntity entityToDto(NodeEntityKey key, com.nextuple.node.dto.NodeEntity entity) {
    return nodeMapper.nodeEntityToPostgresEntity(entity);
  }

  @Override
  public com.nextuple.node.dto.NodeEntity dtoToEntity(NodeEntity pgDto) {
    return nodeMapper.postgresEntityToNodeEntity(pgDto);
  }

  @Override
  public NodeKey entityKeyToDtoKey(NodeEntityKey entityKey) {

    return nodeMapper.entityKeyToNodeKey(entityKey);
  }

  @Override
  public NodeEntityKey dtoKeyToEntityKey(NodeKey pgDtoKey) {

    return nodeMapper.nodeKeytoEntityKey(pgDtoKey);
  }
}
