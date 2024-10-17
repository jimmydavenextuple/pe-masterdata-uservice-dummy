package com.nextuple.pe.webhook.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.pe.webhook.domain.dtos.WebhookConfigDetails;
import com.nextuple.pe.webhook.service.impl.ConfigurationClientWebhookService;
import com.nextuple.pe.webhook.util.TestUtil;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

class ConfigurationClientWebhookServiceTest {

  @Mock WebhookDetailsService webhookDetailsService;
  @InjectMocks ConfigurationClientWebhookService configurationClientWebhookService;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Happy path: 200 OK while reporting to client")
  void publishToClientWebhook1() throws CommonServiceException {

    WebhookConfigDetails webhookConfigDetails = TestUtil.createWebhookConfigDetails();

    when(webhookDetailsService.getWebhookConfigDetails(anyString(), anyString()))
        .thenReturn(Optional.of(webhookConfigDetails));
    WebClient webClient = mock(WebClient.class);
    Mockito.mockStatic(WebClient.class);
    when(WebClient.create()).thenReturn(webClient);
    WebClient.RequestHeadersUriSpec requestHeadersUriSpec =
        mock(WebClient.RequestHeadersUriSpec.class);
    WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
    WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
    WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
    Mono<ResponseEntity<String>> responseMono =
        Mono.just(ResponseEntity.status(HttpStatus.OK).body("OK"));

    when(webClient.method(any())).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(webhookConfigDetails.getWebhookDetail().getEndpoint()))
        .thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.exchangeToMono(any())).thenReturn(responseMono);

    configurationClientWebhookService.publishToClientWebhook(
        "tenantId", "eventName", Collections.emptyList());

    verify(webhookDetailsService, times(1)).getWebhookConfigDetails("tenantId", "eventName");
    verify(webClient, times(1)).method(any());
    verify(requestBodyUriSpec).uri(webhookConfigDetails.getWebhookDetail().getEndpoint());
    verify(requestBodySpec).headers(any());
    verify(requestBodySpec).bodyValue(Collections.emptyList());
    verify(requestHeadersUriSpec).exchangeToMono(any());
  }

  @Test
  @DisplayName("Exception, Webhook details are not found")
  void publishToClientWebhook2() throws CommonServiceException {
    when(webhookDetailsService.getWebhookConfigDetails(anyString(), anyString()))
        .thenReturn(Optional.empty());
    CommonServiceException cse =
        assertThrows(
            CommonServiceException.class,
            () ->
                configurationClientWebhookService.publishToClientWebhook(
                    "tenantId", "eventName", Collections.emptyList()));
    assertEquals("Received error response  while sending to client", cse.getMessage());
  }
}
