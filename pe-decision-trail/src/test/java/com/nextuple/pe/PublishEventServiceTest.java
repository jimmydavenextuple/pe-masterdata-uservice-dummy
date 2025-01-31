package com.nextuple.pe;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.pe.configs.ITenantConfig;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;

class PublishEventServiceTest {
  @InjectMocks PublishEventService publishEventService;
  @Mock ApplicationEventPublisher applicationEventPublisher;
  @Mock ITenantConfig iTenantConfig;

  public static final String PAGE_NAME = "pageName";

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    when(iTenantConfig.getAllowedPagesListForPublishingEvent())
        .thenReturn(Set.of("checkout", "cart"));
    when(iTenantConfig.getPublishEnabledMap()).thenReturn(Map.of("ItemDetailsEvent", Boolean.TRUE));
  }

  @Test
  void setCommonFieldsAndPublishEventTest() {
    MDC.put(PAGE_NAME, "Checkout");
    publishEventService.setCommonFieldsAndPublishEvent(new BaseEvent());
    verify(applicationEventPublisher, times(1)).publishEvent(any(BaseEvent.class));

    MDC.put(PAGE_NAME, "PDP");
    publishEventService.setCommonFieldsAndPublishEvent(new BaseEvent());
    verify(applicationEventPublisher, times(1)).publishEvent(any(BaseEvent.class));

    MDC.put(PAGE_NAME, null);
    publishEventService.setCommonFieldsAndPublishEvent(new BaseEvent());
    verify(applicationEventPublisher, times(2)).publishEvent(any(BaseEvent.class));
  }

  @Test
  void publishDisabledTest() {
    when(iTenantConfig.getPublishEnabledMap()).thenReturn(Map.of("BaseEvent", Boolean.FALSE));
    publishEventService.publishEvent(new BaseEvent());
    verify(applicationEventPublisher, times(0)).publishEvent(any());
  }

  @Test
  void setCommonFieldsTestLogDisabled() {
    MDC.put(PAGE_NAME, null);
    when(iTenantConfig.getLogLevelMap()).thenReturn(Map.of("BaseEvent", "DEBUG"));
    when(iTenantConfig.getPublishEnabledMap()).thenReturn(Map.of("BaseEvent", Boolean.FALSE));
    publishEventService.setCommonFieldsAndPublishEvent(new BaseEvent());
    verify(applicationEventPublisher, times(0)).publishEvent(any(BaseEvent.class));
  }

  @Test
  void setCommonFieldsTestLogEnabled() {
    MDC.put(PAGE_NAME, null);
    BaseEvent e = new BaseEvent();
    when(iTenantConfig.getLogLevelMap()).thenReturn(Map.of("BaseEvent", "ERROR"));
    when(iTenantConfig.getPublishEnabledMap()).thenReturn(Map.of("BaseEvent", Boolean.TRUE));
    publishEventService.setCommonFieldsAndPublishEvent(new BaseEvent());
    verify(applicationEventPublisher, times(1)).publishEvent(any(BaseEvent.class));
  }

  @Test
  void publishGenericExceptionEventTest() {
    publishEventService.publishGenericExceptionEvent(123, "Error occurred");
    verify(applicationEventPublisher, times(1)).publishEvent(any(ExceptionEvent.class));
  }
}
