package com.hbc.fsa.node.service;

import com.hbc.fsa.node.pojo.NodeFSASyncRequest;

public interface NodeFSASyncService {
  void sendNodeFSAMapping(NodeFSASyncRequest request);
}
