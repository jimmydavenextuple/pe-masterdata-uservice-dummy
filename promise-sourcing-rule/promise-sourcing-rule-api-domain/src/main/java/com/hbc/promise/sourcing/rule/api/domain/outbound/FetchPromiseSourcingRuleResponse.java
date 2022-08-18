package com.hbc.promise.sourcing.rule.api.domain.outbound;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hbc.promise.sourcing.rule.api.domain.pojo.ServiceOptionInfo;
import java.io.Serializable;
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
public class FetchPromiseSourcingRuleResponse implements Serializable {
  private static final long serialVersionUID = -5720876261751291895L;
  private List<ServiceOptionInfo> sdnd;
  private List<ServiceOptionInfo> express;
  private List<ServiceOptionInfo> standard;
  private List<ServiceOptionInfo> nextday;
}
