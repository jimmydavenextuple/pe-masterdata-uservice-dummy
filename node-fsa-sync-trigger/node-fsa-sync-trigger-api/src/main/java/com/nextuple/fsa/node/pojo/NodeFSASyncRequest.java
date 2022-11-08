package com.nextuple.fsa.node.pojo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeFSASyncRequest {
  private List<Node> nodes;
}
