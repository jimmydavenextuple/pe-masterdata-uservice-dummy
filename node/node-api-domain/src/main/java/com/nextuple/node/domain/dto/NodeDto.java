package com.nextuple.node.domain.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NodeDto implements Serializable {
  private static final long serialVersionUID = 1240891589171888066L;

  private String nodeId;
  private String orgId;
  private String province;
  private String postalCode;
  private String nodeType;
  private String street;
  private String latitude;
  private String longitude;
  private String timezone;
  private String city;
  private Boolean isActive;
}
