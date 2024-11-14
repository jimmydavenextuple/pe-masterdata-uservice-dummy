package com.nextuple.pe.webhook.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookConfigDetails {
  private String tenantId;
  private String moduleName;
  private String webhookName;
  private WebhookDetail webhookDetail;
}
