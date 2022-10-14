package com.hbc.transit.domain.inbound;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistinctGeozonesResponse {

  private List<String> sourceGeozones;
  private List<String> destinationGeozones;
}
