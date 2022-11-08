package com.nextuple.transit.configuration;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TransitAsyncConfigTest {

  @InjectMocks TransitAsyncConfig asyncConfig;

  @Test
  void getThreadPoolTaskExecutorTest() {
    ReflectionTestUtils.setField(asyncConfig, "corePoolSize", 2);
    ReflectionTestUtils.setField(asyncConfig, "maxPoolSize", 10);
    ReflectionTestUtils.setField(asyncConfig, "queueCapacity", 50);
    MockedStatic<MDC> mdcMockedStatic = mockStatic(MDC.class);
    Map<String, String> mdcContextMap = new HashMap<>();
    mdcContextMap.put("message", "This is test message");
    when(MDC.getCopyOfContextMap()).thenReturn(mdcContextMap);

    ThreadPoolTaskExecutor threadPoolTaskExecutor = asyncConfig.threadPoolTaskExecutor();
    threadPoolTaskExecutor.execute(() -> System.out.println("Testing new task"));
    mdcMockedStatic.close();
    Assertions.assertNotNull(threadPoolTaskExecutor);
  }

  @Test
  void getTaskExecutorNoMDCContextMapTest() {
    ReflectionTestUtils.setField(asyncConfig, "corePoolSize", 2);
    ReflectionTestUtils.setField(asyncConfig, "maxPoolSize", 10);
    ReflectionTestUtils.setField(asyncConfig, "queueCapacity", 50);

    ThreadPoolTaskExecutor threadPoolTaskExecutor = asyncConfig.threadPoolTaskExecutor();
    threadPoolTaskExecutor.execute(() -> System.out.println("Testing new task"));
    Assertions.assertNotNull(threadPoolTaskExecutor);
  }
}
