package com.nextuple.common.properties;

import java.io.Serializable;
import java.util.HashMap;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Data
@Component
@ConfigurationProperties(prefix = "spring.application")
public class ApplicationProperties {
  private String name;
  private String title;
  private String version;
  private String description;
  private String contactEmail;
  private String contactName;
  private String contactWebsite;
  private Properties properties;

  private String getOrDefault(String propertyValue, String defaultValue) {
    return ObjectUtils.isEmpty(propertyValue) ? defaultValue : propertyValue;
  }

  public String getName() {
    return getOrDefault(this.name, "UNKNOWN");
  }

  public String getTitle() {
    return getOrDefault(this.title, "UNKNOWN");
  }

  public String getVersion() {
    return getOrDefault(this.version, "UNKNOWN");
  }

  public String getContactEmail() {
    return getOrDefault(this.contactEmail, "support@nextuple.com");
  }

  public String getContactName() {
    return getOrDefault(this.contactName, "Nextuple Support Team");
  }

  public String getContactWebsite() {
    return getOrDefault(this.contactWebsite, "https://www.nextuple.com");
  }

  public String getDescription() {
    return getOrDefault(this.description, "");
  }

  public Properties getProperties() {
    return CollectionUtils.isEmpty(this.properties) ? new Properties() : this.properties;
  }

  public static class Properties extends HashMap<String, Object> implements Serializable {
    private static final long serialVersionUID = 4725923469045234532L;
  }
}
