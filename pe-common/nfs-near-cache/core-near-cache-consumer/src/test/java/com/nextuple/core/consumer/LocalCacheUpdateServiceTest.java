package com.nextuple.core.consumer;

import com.nextuple.core.cache.service.GenericNearCacheService;
import com.nextuple.core.event.LocalCacheUpdateEvent;
import com.nextuple.core.event.LocalCacheUpdateMessage;
import com.nextuple.core.registry.NearCacheRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class LocalCacheUpdateServiceTest {

    @InjectMocks private LocalCacheUpdateService localCacheUpdateService;
    @InjectMocks TestUtil testUtil;
    @Mock NearCacheRegistry nearCacheRegistry;
    @Mock Environment env;
    @Mock GenericNearCacheService genericNearCacheService;
    private static final String TRANSFER_SCHEDULE_CACHE_KEY_CLASS =
            "com.nextuple.transit.cache.domain.TransferScheduleCacheKey";


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(localCacheUpdateService, "nearCacheServices", List.of(genericNearCacheService));
        ReflectionTestUtils.setField(localCacheUpdateService, "nearCacheRegistry", nearCacheRegistry);
        ReflectionTestUtils.setField(localCacheUpdateService, "env", env);

    }

    @Test
    void handleLocalCacheUpdateFullDrop() {
        String entityName = "Node";
        when(genericNearCacheService.getEntityName()).thenReturn("Node");
        when(nearCacheRegistry.getRegistry(any())).thenReturn(Map.of("NodeEntity", "full"));
        LocalCacheUpdateEvent localCacheUpdateEvent = testUtil.getLocalCacheUpdateEvent(entityName);
        Assertions.assertDoesNotThrow(() -> localCacheUpdateService.handleLocalCacheUpdate(localCacheUpdateEvent));
        verify(genericNearCacheService, times(1)).deleteAll();
    }


    @Test
    @DisplayName("Partial drop cache updates based on environment properties")
    void handlePartialDropCacheUpdate() throws Exception {
        Map<String, Object> message = new HashMap<>();
        message.put("id", 123L);
        message.put("name", "test");

        LocalCacheUpdateEvent event = createEvent("TestEntity", message);
        when(genericNearCacheService.getEntityName()).thenReturn("TestEntity");
        when(nearCacheRegistry.getRegistry("TestEntity"))
                .thenReturn(Map.of("com.nextuple.core.consumer.TestEntity", "partial"));
        when(env.getProperty("nearcache.entity.TestEntity.attributes"))
                .thenReturn("test");

        localCacheUpdateService.handleLocalCacheUpdate(event);

        verify(genericNearCacheService).delete(any());
    }

    @Test
    @DisplayName("No matching cache service does not perform any operations")
    void noMatchingCacheServicePerformsNoOperations() throws Exception {
        when(genericNearCacheService.getEntityName()).thenReturn("DifferentEntity");
        LocalCacheUpdateEvent event = createEvent("TestEntity", Map.of());

        localCacheUpdateService.handleLocalCacheUpdate(event);

        verifyNoInteractions(nearCacheRegistry);
    }

    @Test
    @DisplayName("Empty parameters list handles cache update without errors")
    void emptyParametersListHandlesCacheUpdate() throws Exception {
        LocalCacheUpdateEvent event = createEvent("TestEntity", Map.of());
        when(nearCacheRegistry.getRegistry("TestEntity"))
                .thenReturn(Map.of("com.nextuple.core.consumer.TestEntity", "partial"));
        when(env.getProperty("nearcache.entity.TestEntity.attributes"))
                .thenReturn("");
        when(genericNearCacheService.getEntityName()).thenReturn("TestEntity");

        localCacheUpdateService.handleLocalCacheUpdate(event);

        verify(genericNearCacheService).delete(any());
    }

    private LocalCacheUpdateEvent createEvent(String entityName, Map<String, Object> message) {
        LocalCacheUpdateMessage msg = new LocalCacheUpdateMessage();
        msg.setEntityName(entityName);
        msg.setMessage(message);
        return new LocalCacheUpdateEvent(msg);
    }

}