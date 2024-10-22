package com.nextuple.common.aop;

import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.common.util.CorrelationUtil;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.converter.KafkaMessageHeaders;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class KafkaHandlerAspect {

  @Value("${spring.application.name}")
  private String applicationName;

  @Value("${spring.application.version}")
  private String applicationVersion;

  @Before("@annotation(org.springframework.kafka.annotation.KafkaHandler)")
  public void initializeContext(JoinPoint joinPoint) {
    // Clean previous context
    CurrentThreadContext.cleanLogContext();
    MDC.clear();
    KafkaMessageHeaders kafkaMessageHeaders = null;

    if (joinPoint.getArgs() != null) {
      Optional<KafkaMessageHeaders> kafkaMessageHeadersOptional =
          Arrays.asList(joinPoint.getArgs()).stream()
              .filter(x -> x instanceof KafkaMessageHeaders)
              .map(x -> (KafkaMessageHeaders) x)
              .findFirst();
      if (kafkaMessageHeadersOptional.isPresent()) {
        kafkaMessageHeaders = kafkaMessageHeadersOptional.get();
      }
    }

    if (kafkaMessageHeaders != null) {
      // Set Application name and version
      CurrentThreadContext.getLogContext()
          .initFromKafka(kafkaMessageHeaders)
          .setApplicationName(applicationName)
          .setApplicationVersion(applicationVersion);

      // Process correlation id
      CorrelationUtil.processCorrelationId(
          CorrelationUtil.extractCorrelationId(kafkaMessageHeaders));
    } else {
      // Set Application name and version
      CurrentThreadContext.getLogContext()
          .setKafkaTopic("unknown")
          .setKafkaMessageID("unknown")
          .setApplicationName(applicationName)
          .setApplicationVersion(applicationVersion);

      // Process random correlation id
      CorrelationUtil.processCorrelationId(UUID.randomUUID().toString());
    }
  }

  @After("@annotation(org.springframework.kafka.annotation.KafkaHandler)")
  public void closeContext(JoinPoint joinPoint) {
    // Clean previous context
    CurrentThreadContext.cleanLogContext();
    MDC.clear();
  }
}
