package com.nextuple.promise.sourcing.rule.api.domain.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class GenericColumnInfoDto implements Serializable {
  private static final long serialVersionUID = 5889803183936041423L;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Schema(description = "Unique name of the column")
  private String columnName;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Schema(description = "Unique meta name of the column")
  private String columnMeta;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Schema(description = "isSortable flag for column")
  private Boolean isSortable;
}
