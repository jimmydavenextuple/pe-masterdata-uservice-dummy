package com.nextuple.node.controller;

import com.nextuple.common.controller.AbstractCommonCRUDController;
import com.nextuple.node.domain.NodeRequest;
import com.nextuple.node.domain.NodeResponse;
import com.nextuple.node.mapper.NodeRequestResponseEntityMapper;
import java.util.function.Function;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
        return mapper.entityToResponse(
            handler.get(
                mapper.requestToEntityKey(NodeRequest.builder().nodeId(nodeId).orgId(orgId).build())));
    }

    @PostMapping
    NodeResponse create(NodeRequest nodeRequest){
        return mapper.entityToResponse(handler.create(
            mapper.requestToEntityKey(nodeRequest),
            mapper.requestToEntity(nodeRequest)
        ));
    }

    @PutMapping
    NodeResponse update(NodeRequest request){
        return mapper.entityToResponse(handler.update(
            mapper.requestToEntityKey(request),
            mapper.requestToEntity(request)
        ));
    }

    @DeleteMapping
    void delete(NodeRequest request){
        handler.delete(mapper.requestToEntityKey(request));
    }
}
