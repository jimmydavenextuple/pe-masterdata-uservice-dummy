package com.hbc.fsa.node.pojo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FSACoverage {
  private List<String> fsas;
  private String earliestCutoff;
}
