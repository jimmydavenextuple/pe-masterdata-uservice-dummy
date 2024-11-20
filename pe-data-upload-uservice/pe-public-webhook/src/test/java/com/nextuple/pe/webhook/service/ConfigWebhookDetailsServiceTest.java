package com.nextuple.pe.webhook.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.pe.webhook.config.WebhookConfiguration;
import com.nextuple.pe.webhook.domain.dtos.WebhookConfigDetails;
import com.nextuple.pe.webhook.service.impl.ConfigWebhookDetailsService;
import com.nextuple.pe.webhook.util.TestUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ConfigWebhookDetailsServiceTest {

  @Mock WebhookConfiguration webhookConfiguration;

  @InjectMocks ConfigWebhookDetailsService webhookDetailsService;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Happy path, WebhookConfigDetails are fetched successfully")
  void getWebhookConfigDetailsTest1() throws CommonServiceException {
    when(webhookConfiguration.getWebhook())
        .thenReturn(List.of(TestUtil.createWebhookConfigDetails()));
    Optional<WebhookConfigDetails> webhookConfigDetails =
        webhookDetailsService.getWebhookConfigDetails(TestUtil.ORG_ID, TestUtil.WEBHOOK_NAME);
    assertTrue(webhookConfigDetails.isPresent());
    assertEquals(TestUtil.ORG_ID, webhookConfigDetails.get().getTenantId());
    assertEquals(TestUtil.WEBHOOK_NAME, webhookConfigDetails.get().getWebhookName());
    assertEquals("POST", webhookConfigDetails.get().getWebhookDetail().getMethod());
  }

  @Test
  @DisplayName("Exception, WebhookDetails not found for combination of tenantID and webhookName")
  void getWebhookConfigDetailsTest2() throws CommonServiceException {
    when(webhookConfiguration.getWebhook()).thenReturn(new ArrayList<>());
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                webhookDetailsService.getWebhookConfigDetails(
                    TestUtil.ORG_ID, TestUtil.WEBHOOK_NAME));
    assertTrue(ex.getMessage().contains("Webhook details not found for tenantId"));
  }
}
