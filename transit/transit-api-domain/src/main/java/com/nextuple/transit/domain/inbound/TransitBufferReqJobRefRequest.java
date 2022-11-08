package com.nextuple.transit.domain.inbound;

import com.nextuple.transit.domain.enums.TransitBufferReqJobRefEnum;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TransitBufferReqJobRefRequest implements Serializable {

  @Length(max = 50)
  private String extReferenceId;

  private Long transitBufferReqId;

  private TransitBufferReqJobRefEnum action;
}
