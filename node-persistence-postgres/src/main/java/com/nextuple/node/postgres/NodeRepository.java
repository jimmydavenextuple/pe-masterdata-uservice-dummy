package com.nextuple.node.postgres;

import com.nextuple.node.postgres.dto.NodeEntity;
import com.nextuple.node.postgres.dto.NodeKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeRepository
    extends CrudRepository<NodeEntity, NodeKey> {

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

