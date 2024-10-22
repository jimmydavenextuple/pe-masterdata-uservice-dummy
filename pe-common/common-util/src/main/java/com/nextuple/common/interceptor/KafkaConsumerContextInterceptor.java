package com.nextuple.common.interceptor;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;

public class KafkaConsumerContextInterceptor implements ConsumerInterceptor { // NOSONAR
  private static final Logger logger =
      LoggerFactory.getLogger(KafkaConsumerContextInterceptor.class);

  @Override
  public ConsumerRecords onConsume(ConsumerRecords records) {
    if (!records.isEmpty()) {
      logger.debug("Received {} records", records.count());
    }
    return records;
  }

  @Override
  public void close() {
    logger.info("Kafka consumer context interceptor closed");
  }

  @Override
  public void onCommit(Map offsets) {
    try {
      logger.debug("Committing offsets : {}", offsets);
    } catch (Exception e) {
      logger.error(e, "Unable to log commit offsets");
    }
  }

  @Override
  public void configure(Map<String, ?> configs) {
    String saslKey = "sasl.jaas.config";
    Map<String, Object> filteredConfigs = null;
    if (Objects.nonNull(configs)) {
      filteredConfigs = new HashMap<>(configs);
      filteredConfigs.remove(saslKey);
    }
    logger.info(
        "Kafka consumer context interceptor configured with following configs : {}",
        filteredConfigs);
  }
}
