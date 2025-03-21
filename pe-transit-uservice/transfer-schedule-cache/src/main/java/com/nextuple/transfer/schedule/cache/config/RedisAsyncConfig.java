/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transfer.schedule.cache.config;

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
public class RedisAsyncConfig {

  @Value("${taskexecutor.common.core.pool.size:2}")
  private int corePoolSize;

  @Value("${taskexecutor.common.max.pool.size:10}")
  private int maxPoolSize;

  @Value("${taskexecutor.common.queue.capacity:50}")
  private int queueCapacity;

  @Bean(name = "transferScheduleTaskExecutor")
  public ThreadPoolTaskExecutor transferScheduleTaskExecutor() {
    var threadPoolTaskExecutor = setThreadPoolTaskExecutor(new ThreadPoolTaskExecutor());
    threadPoolTaskExecutor.setThreadNamePrefix("RedisThreadPoolTask-");
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
