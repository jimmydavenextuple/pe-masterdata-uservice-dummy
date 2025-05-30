package com.nextuple.pe.configs;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "promise-coordination.order-operations")
public class OrderOperationConfig {
  private Map<String, String> operationConfigMap;
}
