package com.nextuple.promise.sourcing.rule.api.domain.outbound;

import com.nextuple.promise.sourcing.rule.api.domain.pojo.AttributeInfo;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SourcingAttributeDefinitionUIResponse implements Serializable {
  public static final long serialVersionUID = -2076637197346262216L;

  private String sourcingAttributesDefinitionId;
  private String orgId;
  private List<AttributeInfo> requiredAttributes;
  private List<AttributeInfo> optionalAttributes;
}
