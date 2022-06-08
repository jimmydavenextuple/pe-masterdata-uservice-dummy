package com.nextuple.node.postgres.service;

import com.nextuple.common.persistence.postgres.service.AbstractPostgreSQLServiceImpl;
import com.nextuple.node.dto.key.NodeEntityKey;
import com.nextuple.node.dto.service.NodePersistenceService;
import com.nextuple.node.postgres.dto.NodeEntity;
import com.nextuple.node.postgres.dto.NodeKey;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

@Slf4j
public class NodePersistenceServicePostgreSQLImpl
    extends AbstractPostgreSQLServiceImpl<NodeKey,
    NodeEntity, NodeEntityKey, com.nextuple.node.dto.NodeEntity>
implements NodePersistenceService {

  @Override
  protected Logger log() {
    return log;
  }
}
