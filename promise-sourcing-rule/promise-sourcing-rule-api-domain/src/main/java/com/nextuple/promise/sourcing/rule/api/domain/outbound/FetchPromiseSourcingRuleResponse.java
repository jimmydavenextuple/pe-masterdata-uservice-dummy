package com.nextuple.promise.sourcing.rule.api.domain.outbound;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.ServiceOptionInfo;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
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
  private Map<String, List<ServiceOptionInfo>> serviceOptionSourcingRules;
}
