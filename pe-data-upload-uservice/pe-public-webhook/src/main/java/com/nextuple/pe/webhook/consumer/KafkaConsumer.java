package com.nextuple.pe.webhook.consumer;

import com.nextuple.common.exception.CommonServiceException;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;

public interface KafkaConsumer {
  public void receiveCoreEngineResponse(String response, KafkaMessageHeaders headers)
      throws CommonServiceException;
}
