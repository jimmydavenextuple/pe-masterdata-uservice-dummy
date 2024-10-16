package com.nextuple.promise.sourcing.rule.api.domain.outbound;

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
public class AttributeValueResponse implements Serializable {
  private static final long serialVersionUID = 1069234021589759124L;

  private String attributeName;
  private List<String> values;
}
