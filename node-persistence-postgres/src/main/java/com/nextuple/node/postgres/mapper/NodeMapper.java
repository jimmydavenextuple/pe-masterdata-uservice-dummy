package com.nextuple.node.postgres.mapper;

import com.nextuple.node.dto.key.NodeEntityKey;
import com.nextuple.node.postgres.dto.NodeEntity;
import com.nextuple.node.postgres.dto.NodeKey;
import org.mapstruct.Mapper;

@Mapper
public interface NodeMapper {
  com.nextuple.node.dto.NodeEntity postgresEntityToNodeEntity(NodeEntity nodeEntity);

  NodeEntity nodeEntityToPostgresEntity(com.nextuple.node.dto.NodeEntity nodeEntity);

  NodeKey entityKeyToNodeKey(NodeEntityKey nodeEntityKey);

  NodeEntityKey nodeKeytoEntityKey(NodeKey nodeKey);
}
