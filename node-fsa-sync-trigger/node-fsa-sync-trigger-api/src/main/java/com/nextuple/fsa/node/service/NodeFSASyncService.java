package com.nextuple.fsa.node.service;

import com.nextuple.fsa.node.pojo.NodeFSASyncRequest;

public interface NodeFSASyncService {
  void sendNodeFSAMapping(NodeFSASyncRequest request);
}
