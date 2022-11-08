package com.nextuple.jobs.consumers.feign;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokenRequest implements Serializable {
  private static final long serialVersionUID = 2513791156431898728L;

  private String grant_type; // NOSONAR

  private String scope;
}
