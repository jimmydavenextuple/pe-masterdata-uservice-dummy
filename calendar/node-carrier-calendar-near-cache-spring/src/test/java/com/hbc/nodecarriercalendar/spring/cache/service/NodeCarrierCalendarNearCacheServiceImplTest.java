package com.hbc.nodecarriercalendar.spring.cache.service;

import com.hbc.core.cache.domain.CacheValue;
import com.hbc.core.cache.service.GenericFeignCacheService;
import com.hbc.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheKey;
import com.hbc.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheValue;
import com.hbc.node.carrier.calendar.cache.spring.service.NodeCarrierCalendarSpringNearCacheService;
import com.hbc.nodecarriercalendar.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NodeCarrierCalendarNearCacheServiceImplTest {
    @InjectMocks
    private NodeCarrierCalendarSpringNearCacheService nodeCarrierCalendarSpringNearCacheService;

    @InjectMocks private TestUtil testUtil;

    @Mock
    private GenericFeignCacheService<NodeCarrierCalendarCacheKey, NodeCarrierCalendarCacheValue> feignCacheService;

    @Test
    void getInValidTest() {
        NodeCarrierCalendarCacheKey cacheKey = testUtil.getNodeCarrierCalendarCacheKey();

        when(feignCacheService.get(any())).thenReturn(null);
        assertNull(nodeCarrierCalendarSpringNearCacheService.get(cacheKey));
        verify(feignCacheService, times(1)).get(cacheKey);
    }

    @Test
    void deleteTest() {
        NodeCarrierCalendarCacheKey cacheKey = testUtil.getNodeCarrierCalendarCacheKey();

        nodeCarrierCalendarSpringNearCacheService.delete(cacheKey);
        CacheValue cacheValue = nodeCarrierCalendarSpringNearCacheService.get(cacheKey);
        assertNull(cacheValue);
    }

    @Test
    void deleteAllTest() {
        NodeCarrierCalendarCacheKey cacheKey = testUtil.getNodeCarrierCalendarCacheKey();

        nodeCarrierCalendarSpringNearCacheService.deleteAll();
        CacheValue cacheValue = nodeCarrierCalendarSpringNearCacheService.get(cacheKey);
        assertNull(cacheValue);
    }
}
