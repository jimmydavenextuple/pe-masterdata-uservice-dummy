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
@ConfigurationProperties(prefix = "promise-coordination.operation-template-mapping")
public class OperationalTemplateConfig {
  private Map<String, Map<String, String>> operationalTemplateConfigMap;
}
