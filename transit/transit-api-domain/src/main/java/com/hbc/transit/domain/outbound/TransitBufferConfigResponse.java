package com.hbc.transit.domain.outbound;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hbc.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransitBufferConfigResponse implements Serializable {

  private static final long serialVersionUID = 1291152938195817113L;

  private Long id;

  private String orgId;

  private String carrierServiceId;

  private Double bufferDays;

  private Date startDate;

  private Date endDate;

  private TransitBufferConfigRequestStatusEnum status;

  @JsonInclude(Include.NON_NULL)
  private Long parentRequestId;
}
