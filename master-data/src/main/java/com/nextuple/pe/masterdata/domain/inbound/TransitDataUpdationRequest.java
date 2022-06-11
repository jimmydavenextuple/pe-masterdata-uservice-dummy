package com.nextuple.pe.masterdata.domain.inbound;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransitDataUpdationRequest implements Serializable {

  private Float transitDays;
}
