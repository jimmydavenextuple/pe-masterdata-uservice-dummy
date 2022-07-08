package com.hbc.core.event.listeners;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.hbc.core.exception.LocalCacheUpdateEventException;
import com.hbc.core.producer.EntityEventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommonEntityListenerTest {

  @InjectMocks CommonEntityListener commonEntityListener;

  @Mock private EntityEventProducer entityEventProducer;

  @Test
  void afterUpdating() throws LocalCacheUpdateEventException, IllegalAccessException {
    doNothing().when(entityEventProducer).publishEntityEvent(any());

    commonEntityListener.afterUpdating(new Object());

    verify(entityEventProducer, times(1)).publishEntityEvent(any());
  }
}
