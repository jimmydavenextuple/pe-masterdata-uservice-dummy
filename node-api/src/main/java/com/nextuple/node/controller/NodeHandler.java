package com.nextuple.node.controller;

import com.nextuple.common.AbstractCommonHandler;
import com.nextuple.node.dto.NodeEntity;
import com.nextuple.node.dto.key.NodeEntityKey;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NodeHandler
    extends AbstractCommonHandler<NodeEntityKey, NodeEntity> {

    @Override
    protected Logger log() {
        return log;
    }

}























//    @Autowired
//    NodePersistenceService nodePersistenceService;

//    @Override
//    public NodeEntity create(NodeEntityKey key, NodeEntity entity) {
//        log().trace("Inserting a new record for {} with value {} ", key, entity);
//        return nodePersistenceService.create(key, entity);
//    }
//
//    @Override
//    public NodeEntity update(NodeEntityKey key, NodeEntity entity) {
//        log().trace("Updating a new record for {} with value {} ", key, entity);
//        return nodePersistenceService.update(key, entity);
//    }
//
//    @Override
//    public NodeEntity get(NodeEntityKey key) {
//        long start = System.currentTimeMillis();
//        NodeEntity entity = nodePersistenceService.get(key);
//        Timer readTimer = registry.timer("core.persistence", OPERATION_TAG_NAME, DB_OPERATION_READ);
//        long end = System.currentTimeMillis();
//        readTimer.record(end-start, TimeUnit.MILLISECONDS);
//        log().trace("Fetching details for {}", key);
//        return entity;
//    }
//
//    @Override
//    public void delete(NodeEntityKey key) {
//        log().trace("Deleting a the record with key {} ", key);
//        nodePersistenceService.delete(key);
//    }

//node persistence should have dep on common persistence
//NodePersistenceService extends gencericpersistenceService<>
//
//define mappers