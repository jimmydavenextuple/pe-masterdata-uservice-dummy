package com.nextuple.promise.sourcing.rule.api.domain.outbound;

import com.nextuple.promise.sourcing.rule.api.domain.pojo.GenericColumnInfoDto;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericDetailsResponse implements Serializable {
  private static final long serialVersionUID = -6321504908960872244L;

  // todo : make an object Column Info Dto for generic column use
  private List<GenericColumnInfoDto> columns;
  private transient List<Map<String, Object>> rows;
}
