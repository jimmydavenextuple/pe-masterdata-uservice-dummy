package com.nextuple.node.postgres;

import com.nextuple.common.persistence.postgres.service.AbstractPostgreSQLServiceImpl;
import com.nextuple.node.dto.key.NodeEntityKey;
import com.nextuple.node.postgres.dto.NodeEntity;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

@Slf4j
public class NodeRepository
    extends AbstractPostgreSQLServiceImpl<NodeEntityKey, NodeEntity, NodeEntityKey, NodeEntity> {

  @Override
  protected Logger log() {
    return log;
  }
}


//generic entity mapper
//    dependent on nfs common persistence common persistence postgres
//    node persistence
//
//node entity with postgres
//node key with postgres
//
//nodePersistenceServiceImpl not  repository<postgres key, postgres entity, Node Key, node ENtity> extends AbstractPostgressSQLServiceImple
//    implement nodePersistence Service
//node persistence
//    Service  extends generic persistence service
//node persistence postgres
//    node persistence impl extends node Postgres
//
//node persistence postgres
//nodeRepository extends crudRepository

