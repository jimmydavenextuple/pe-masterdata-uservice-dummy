package com.nextuple.node.dto.service;

import com.nextuple.common.service.GenericPersistenceService;
import com.nextuple.node.dto.NodeEntity;
import com.nextuple.node.dto.key.NodeEntityKey;

public interface NodePersistenceService
    extends GenericPersistenceService<NodeEntityKey, NodeEntity> {
}
