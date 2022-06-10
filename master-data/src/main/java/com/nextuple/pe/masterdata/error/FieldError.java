package com.nextuple.pe.masterdata.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldError implements Serializable {
  private static final long serialVersionUID = 6006518972445723246L;
  private Object rejectedValue; // NOSONAR
  private Object actualValue; // NOSONAR
  private String errorMessage;
}
