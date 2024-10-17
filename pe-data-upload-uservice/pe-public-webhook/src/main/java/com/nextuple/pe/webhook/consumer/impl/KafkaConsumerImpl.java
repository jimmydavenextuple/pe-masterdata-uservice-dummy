package com.nextuple.pe.webhook.consumer.impl;

import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.pe.webhook.consumer.KafkaConsumer;
import com.nextuple.pe.webhook.service.ClientWebhookService;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@KafkaListener(
    topics = "${pe-public-webhook.pe-response.topic}",
    groupId = "${pe-public-webhook.pe-response.group-id}",
    batch = "false",
    containerFactory = "StringDeserializerConsumer",
    autoStartup = "${kafka-topic-flags.pe-public-webhook.pe-response.enabled:true}")
public class KafkaConsumerImpl implements KafkaConsumer {
  private static final String TENANT_ID = "tenantId";
  @Autowired private ClientWebhookService clientWebhookService;
  private static final String EVENT_NAME = "eventName";

  @Override
  @KafkaHandler
  public void receiveCoreEngineResponse(
      @Payload String response, @Headers KafkaMessageHeaders headers)
      throws CommonServiceException {
    List<String> responseList = new ArrayList<>();
    responseList.add(response);
    String tenantId = CurrentThreadContext.getLogContext().get(TENANT_ID);
    String eventName = headers.getRawHeaders().get(EVENT_NAME).toString();
    log.info("Received message for event {} , {} ", eventName, responseList);
    try {
      clientWebhookService.publishToClientWebhook(tenantId, eventName, responseList);
    } catch (Exception e) {
      log.error("Error in sending pe response message to client");
      throw e;
    }
  }
}
