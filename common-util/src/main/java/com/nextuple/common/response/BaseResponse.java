package com.nextuple.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> implements Serializable {
  private static final long serialVersionUID = -6697518836477283253L;
  private boolean success;
  private String requestId;
  private Date timestamp;
  private String message;
  private T payload; // NOSONAR

  public static BaseResponseBuilder builder() {
    return new BaseResponseBuilder();
  }
}
