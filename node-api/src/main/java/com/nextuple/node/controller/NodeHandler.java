package com.nextuple.node.controller;

import com.nextuple.common.AbstractCommonHandler;
import com.nextuple.node.dto.NodeEntity;
import com.nextuple.node.dto.key.NodeEntityKey;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

@Slf4j
public class NodeHandler extends AbstractCommonHandler<NodeEntityKey, NodeEntity> {
    @Override
    protected Logger log() {
        return log;
    }
}
