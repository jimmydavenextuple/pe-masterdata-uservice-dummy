package com.nextuple.common.interceptor;

import com.nextuple.common.constants.CommonConstants;
import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.common.context.LogContext;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.util.ObjectUtils;

public class KafkaProducerContextInterceptor implements ProducerInterceptor { // NOSONAR

  private static final Logger logger =
      LoggerFactory.getLogger(KafkaProducerContextInterceptor.class);

  @Override
  public ProducerRecord onSend(ProducerRecord record) {
    if (!ObjectUtils.isEmpty(CurrentThreadContext.getLogContext().getCorrelationId())) {
      record
          .headers()
          .add(
              new RecordHeader(
                  LogContext.CORRELATION_ID,
                  CurrentThreadContext.getLogContext().getCorrelationId().getBytes()));
    }

    if (!ObjectUtils.isEmpty(CurrentThreadContext.getLogContext().getTenantId())) {
      record
          .headers()
          .add(
              new RecordHeader(
                  CommonConstants.HEADER_TENANT_ID,
                  CurrentThreadContext.getLogContext().getTenantId().getBytes()));
    }

    if (!ObjectUtils.isEmpty(CurrentThreadContext.getLogContext().getUsername())) {
      record
          .headers()
          .add(
              new RecordHeader(
                  CommonConstants.HEADER_USER,
                  CurrentThreadContext.getLogContext().getUsername().getBytes()));
    }

    if (!ObjectUtils.isEmpty(CurrentThreadContext.getLogContext().getUserRole())) {
      record
          .headers()
          .add(
              new RecordHeader(
                  CommonConstants.HEADER_ROLE,
                  CurrentThreadContext.getLogContext().getUserRole().getBytes()));
    }

    if (!ObjectUtils.isEmpty(CurrentThreadContext.getLogContext().getUserLocale())) {
      record
          .headers()
          .add(
              new RecordHeader(
                  CommonConstants.HEADER_USER_LOCALE,
                  CurrentThreadContext.getLogContext().getUserLocale().getBytes()));
    }
    return record;
  }

  @Override
  public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
    if (exception != null) {
      logger.error(exception, "Exception thrown on acknowledgement");
    } else {
      try {
        logger.debug(
            "Ack sent, topic:{}, offset:{}, partition:{}",
            metadata.topic(),
            metadata.offset(),
            metadata.partition());
      } catch (Exception e) {
        logger.error(e, "Unable to log acknowledgement");
      }
    }
  }

  @Override
  public void close() {
    logger.info("Common kafka producer context interceptor closed");
  }

  @Override
  public void configure(Map<String, ?> configs) {
    logger.info("Common kafka producer context interceptor configured");
  }
}
