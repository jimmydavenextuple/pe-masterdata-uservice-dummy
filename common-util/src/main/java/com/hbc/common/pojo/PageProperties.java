package com.hbc.common.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "pagination")
public class PageProperties {

  private Integer pageNo;
  private Integer pageSize;
}
