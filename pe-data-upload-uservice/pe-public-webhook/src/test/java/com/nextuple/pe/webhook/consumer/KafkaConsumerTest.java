package com.nextuple.pe.webhook.consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.pe.webhook.consumer.impl.KafkaConsumerImpl;
import com.nextuple.pe.webhook.service.ClientWebhookService;
import com.nextuple.pe.webhook.util.TestUtil;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;

class KafkaConsumerTest {
  @Mock KafkaMessageHeaders headers;
  @Mock ClientWebhookService clientWebhookService;

  @InjectMocks KafkaConsumerImpl kafkaConsumer;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Happy Path, received response and successfully sent to client")
  void receiveCoreEngineResponseTest1() throws CommonServiceException {
    when(headers.getRawHeaders()).thenReturn(Map.of("eventName", TestUtil.WEBHOOK_NAME));
    CurrentThreadContext.getLogContext().setTenantId(TestUtil.ORG_ID);
    doNothing().when(clientWebhookService).publishToClientWebhook(any(), any(), any());
    kafkaConsumer.receiveCoreEngineResponse("PE-Response-1", headers);
    verify(headers, times(1)).getRawHeaders();
    verify(clientWebhookService, times(1)).publishToClientWebhook(any(), any(), any());
  }

  @Test
  @DisplayName("Exception, error in sending response message to client")
  void receiveCoreEngineResponseTest2() throws CommonServiceException {
    String errorMessage = "500 ISE";
    when(headers.getRawHeaders()).thenReturn(Map.of("eventName", TestUtil.WEBHOOK_NAME));
    CurrentThreadContext.getLogContext().setTenantId(TestUtil.ORG_ID);
    doThrow(new CommonServiceException(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR, 0xff1, null))
        .when(clientWebhookService)
        .publishToClientWebhook(any(), any(), any());
    Exception e =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> kafkaConsumer.receiveCoreEngineResponse("PE-Response-1", headers));
    verify(headers, times(1)).getRawHeaders();
    Assertions.assertEquals(errorMessage, e.getMessage());
  }
}
