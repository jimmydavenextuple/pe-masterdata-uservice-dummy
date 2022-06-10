package com.nextuple.node.controller;

import com.nextuple.node.domain.NodeRequest;
import com.nextuple.node.domain.NodeResponse;
import com.nextuple.node.mapper.NodeRequestResponseEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/node")
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
