package com.nextuple.pe;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.pe.configs.ITenantConfig;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import org.junit.jupiter.api.Assertions;
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

  @Test
  void publishGenericExceptionEventWithLineIdTest() {
    publishEventService.publishGenericExceptionEventWithLineId(123, "Error occurred", "lineId");
    verify(applicationEventPublisher, times(1)).publishEvent(any(ExceptionEvent.class));
  }

  @Test
  void checkEventTimestampConversion() {
    SimpleDateFormat formatter =
        new SimpleDateFormat("MMM dd, yyyy, hh:mm:ss.SSS a", Locale.ENGLISH);
    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    calendar.set(2025, Calendar.JANUARY, 17, 2, 0, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    String twoAmTimeStamp = formatter.format(calendar.getTime());
    Assertions.assertEquals("JAN 17, 2025, 02:00:00.000 AM", twoAmTimeStamp.toUpperCase());

    calendar.set(Calendar.HOUR_OF_DAY, 17);
    String fivePmTimeStamp = formatter.format(calendar.getTime());
    Assertions.assertEquals("JAN 17, 2025, 05:00:00.000 PM", fivePmTimeStamp.toUpperCase());
  }
}
