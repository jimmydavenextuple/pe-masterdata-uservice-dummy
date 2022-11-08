package com.nextuple.common.configuration.cache.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CommonConfigDetails implements Serializable {

  private static final long serialVersionUID = 6866270566874921944L;

  private String orgId;

  private String type;

  private String key;

  private String value;
}
