package com.nextuple.pe.masterdata.calendar.error;

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
public class ErrorData implements Serializable {
  private static final long serialVersionUID = 6006518972445723246L;
  private Integer code;
  private String message;
}
