package com.nextuple.jobs.framework.common.domain.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "page-properties")
public class DefaultPageProperties {

  private Integer pageNo;
  private Integer pageSize;
  private String sortBy;
  private String sortOrder;
}
