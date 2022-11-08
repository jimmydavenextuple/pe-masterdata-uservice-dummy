package com.nextuple.transit.configuration;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class TransitAsyncConfig {

  @Value("${taskexecutor.common.core.pool.size:2}")
  private int corePoolSize;

  @Value("${taskexecutor.common.max.pool.size:10}")
  private int maxPoolSize;

  @Value("${taskexecutor.common.queue.capacity:50}")
  private int queueCapacity;

  @Bean
  public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
    var threadPoolTaskExecutor = setThreadPoolTaskExecutor(new ThreadPoolTaskExecutor());
    threadPoolTaskExecutor.setThreadNamePrefix("TransitThreadPoolTask-");
    threadPoolTaskExecutor.initialize();
    return threadPoolTaskExecutor;
  }

  private ThreadPoolTaskExecutor setThreadPoolTaskExecutor(
      ThreadPoolTaskExecutor threadPoolTaskExecutor) {
    threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
    threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
    threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
    threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
    threadPoolTaskExecutor.setTaskDecorator(
        runnable -> {
          Map<String, String> contextMap = MDC.getCopyOfContextMap();
          return () -> {
            if (contextMap != null) {
              MDC.setContextMap(contextMap);
            }
            runnable.run();
          };
        });
    return threadPoolTaskExecutor;
  }
}
