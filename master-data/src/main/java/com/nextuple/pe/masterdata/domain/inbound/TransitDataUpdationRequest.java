package com.nextuple.pe.masterdata.domain.inbound;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransitDataUpdationRequest implements Serializable {

  private Float transitDays;
}
