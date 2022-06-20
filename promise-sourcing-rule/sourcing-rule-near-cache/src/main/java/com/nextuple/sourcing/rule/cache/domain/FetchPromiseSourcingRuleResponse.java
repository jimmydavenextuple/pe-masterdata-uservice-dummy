package com.nextuple.sourcing.rule.cache.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FetchPromiseSourcingRuleResponse {

  private List<ServiceOptionInfo> sdnd;
  private List<ServiceOptionInfo> express;
  private List<ServiceOptionInfo> standard;
}
