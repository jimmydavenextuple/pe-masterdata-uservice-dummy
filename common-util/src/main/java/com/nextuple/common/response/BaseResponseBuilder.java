package com.nextuple.common.response;

import com.nextuple.common.context.CurrentThreadContext;
import java.util.Date;

public class BaseResponseBuilder {
  private BaseResponse baseResponse; // NOSONAR

  public BaseResponseBuilder() {
    this.baseResponse = new BaseResponse(); // NOSONAR
    this.baseResponse.setTimestamp(new Date());
    this.baseResponse.setSuccess(true);
    this.baseResponse.setRequestId(CurrentThreadContext.getLogContext().getCorrelationId());
  }

  public BaseResponseBuilder success(boolean success) {
    this.baseResponse.setSuccess(success);
    return this;
  }

  public BaseResponseBuilder requestId(String requestId) {
    this.baseResponse.setRequestId(requestId);
    return this;
  }

  public BaseResponseBuilder timestamp(Date timestamp) {
    this.baseResponse.setTimestamp(timestamp);
    return this;
  }

  public BaseResponseBuilder message(String message) {
    this.baseResponse.setMessage(message);
    return this;
  }

  public BaseResponseBuilder payload(Object payload) {
    this.baseResponse.setPayload(payload);
    return this;
  }

  public BaseResponse build() { // NOSONAR
    return this.baseResponse;
  }
}
