package com.nextuple.pe.webhook.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.pe.webhook.domain.dtos.WebhookConfigDetails;
import java.util.Optional;

public interface WebhookDetailsService {

  Optional<WebhookConfigDetails> getWebhookConfigDetails(String tenantId, String webhookName)
      throws CommonServiceException;
}
