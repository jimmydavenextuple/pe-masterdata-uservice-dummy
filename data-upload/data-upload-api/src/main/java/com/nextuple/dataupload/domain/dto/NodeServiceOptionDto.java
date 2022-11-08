package com.nextuple.dataupload.domain.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeServiceOptionDto implements Serializable {
  private static final long serialVersionUID = -1026987908750016056L;

  private String nodeId;
  private String orgId;
  private String nodeType;
  private String street;
  private String city;
  private String province;
  private Boolean isActive;
  private List<String> serviceOptions;
  private Map<String, Double> processingTime;
}
