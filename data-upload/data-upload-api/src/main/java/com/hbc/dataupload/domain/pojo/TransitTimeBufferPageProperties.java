package com.hbc.dataupload.domain.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "transit-time-buffer-page-properties")
public class TransitTimeBufferPageProperties {

  private String sortBy;
  private String sortOrder;
}
