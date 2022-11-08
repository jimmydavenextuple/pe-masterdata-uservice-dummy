package com.nextuple.dataupload.domain.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "node-carrier-service-page-properties")
public class NodeCarrierServicePageProperties {

  private String sortBy;
  private String sortOrder;
}
