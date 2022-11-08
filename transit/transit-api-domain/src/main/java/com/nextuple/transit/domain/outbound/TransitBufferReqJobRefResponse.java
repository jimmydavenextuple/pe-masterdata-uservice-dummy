package com.nextuple.transit.domain.outbound;

import com.nextuple.transit.domain.enums.TransitBufferReqJobRefEnum;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TransitBufferReqJobRefResponse implements Serializable {

  private Long id;

  private String extReferenceId;

  private Long transitBufferReqId;

  private TransitBufferReqJobRefEnum action;
}
