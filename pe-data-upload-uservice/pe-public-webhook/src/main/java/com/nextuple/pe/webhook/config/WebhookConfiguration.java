package com.nextuple.pe.webhook.config;

import com.nextuple.pe.webhook.domain.dtos.WebhookConfigDetails;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pe-public-webhook")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WebhookConfiguration {
  private List<WebhookConfigDetails> webhook;
}
