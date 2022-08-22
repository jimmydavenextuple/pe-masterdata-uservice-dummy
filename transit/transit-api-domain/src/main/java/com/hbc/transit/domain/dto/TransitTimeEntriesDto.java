package com.hbc.transit.domain.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransitTimeEntriesDto implements Serializable {
  private static final long serialVersionUID = 2165167475567959198L;

  private String orgId;
  private String carrierServiceId;
  private Integer totalRecords;
}
