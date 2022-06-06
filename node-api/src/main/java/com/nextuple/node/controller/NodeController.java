package com.nextuple.node.controller;

import com.nextuple.node.NodeRequestResponseEntityMapper;
import com.nextuple.node.domain.NodeRequest;
import com.nextuple.node.domain.NodeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("/node")
public class NodeController {

    @Autowired
    NodeHandler handler;

    @Autowired
    NodeRequestResponseEntityMapper mapper;


    @GetMapping
    NodeResponse get(@RequestParam(name = "orgId") String orgId,
                         @RequestParam(name = "nodeId") String nodeId) {
        return mapper.entityToResponse(handler.get(mapper.requestToEntityKey(NodeRequest.builder().nodeId(nodeId).orgId(orgId).build())));
    }
}
