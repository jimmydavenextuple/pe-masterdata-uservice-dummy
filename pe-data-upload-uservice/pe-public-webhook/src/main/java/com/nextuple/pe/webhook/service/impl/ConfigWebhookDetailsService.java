package com.nextuple.pe.webhook.service.impl;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.pe.webhook.config.WebhookConfiguration;
import com.nextuple.pe.webhook.domain.dtos.WebhookConfigDetails;
import com.nextuple.pe.webhook.service.WebhookDetailsService;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "pe-public-webhook", name = "enabled", havingValue = "true")
public class ConfigWebhookDetailsService implements WebhookDetailsService {
  @Autowired private WebhookConfiguration webhookConfiguration;

  @Override
  public Optional<WebhookConfigDetails> getWebhookConfigDetails(String tenantId, String webhookName)
      throws CommonServiceException {
    Optional<WebhookConfigDetails> webhookConfigDetails = Optional.empty();
    if (Objects.nonNull(webhookConfiguration)
        && StringUtils.hasLength(tenantId)
        && StringUtils.hasLength(webhookName)) {
      webhookConfigDetails =
          webhookConfiguration.getWebhook().stream()
              .filter(
                  w -> tenantId.equals(w.getTenantId()) && webhookName.equals(w.getWebhookName()))
              .findFirst();
    }
    if (webhookConfigDetails.isEmpty()) {
      log.debug(
          "Webhook details not found for tenantId {} and webhookName {}", tenantId, webhookName);
      throw new CommonServiceException(
          "Webhook details not found for tenantId " + tenantId + " and webhookName " + webhookName,
          HttpStatus.BAD_REQUEST,
          0x1772,
          null);
    }
    return webhookConfigDetails;
  }
}
