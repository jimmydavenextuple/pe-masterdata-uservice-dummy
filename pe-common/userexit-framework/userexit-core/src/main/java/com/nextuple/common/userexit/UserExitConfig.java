package com.nextuple.common.userexit;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@ConfigurationProperties(prefix = "userexit")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EnableConfigurationProperties
public class UserExitConfig {
  private Map<String, Map<String, String>> packageMap;
}
