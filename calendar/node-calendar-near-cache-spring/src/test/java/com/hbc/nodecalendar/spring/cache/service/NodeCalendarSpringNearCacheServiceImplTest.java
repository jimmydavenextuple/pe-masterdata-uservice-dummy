package com.hbc.nodecalendar.spring.cache.service;


import com.hbc.core.cache.domain.CacheValue;
import com.hbc.core.cache.service.GenericFeignCacheService;
import com.hbc.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.hbc.node.calendar.cache.domain.NodeCalendarCacheKey;
import com.hbc.node.calendar.cache.domain.NodeCalendarCacheValue;
import com.hbc.node.calendar.cache.spring.service.NodeCalendarSpringNearCacheService;
import com.hbc.nodecalendar.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NodeCalendarSpringNearCacheServiceImplTest {
    @InjectMocks
    private NodeCalendarSpringNearCacheService nodeCalendarSpringNearCacheService;

    @InjectMocks private TestUtil testUtil;

    @Mock private AbstractGenericFeignClientServiceImpl abstractGenericFeignClientService;
    @Mock
    private GenericFeignCacheService<NodeCalendarCacheKey, NodeCalendarCacheValue> feignCacheService;

    @Test
    void getValidTest() {
        NodeCalendarCacheKey cacheKey = testUtil.getNodeCalendarCacheKey();
        NodeCalendarCacheValue cacheValue = testUtil.getNodeCalendarCacheValue();

        when(feignCacheService.get(any())).thenReturn(cacheValue);
        when(abstractGenericFeignClientService.get(any())).thenReturn(cacheValue);

        // First Invocation
        CacheValue cacheValue1 = nodeCalendarSpringNearCacheService.get(cacheKey);
        assertEquals(cacheValue, cacheValue1);

        // Second Invocation
        CacheValue cacheValue2 = abstractGenericFeignClientService.get(cacheKey);
        assertEquals(cacheValue, cacheValue2);

        // Third Invocation
        CacheValue cacheValue3 = abstractGenericFeignClientService.get(cacheKey);
        assertEquals(cacheValue, cacheValue3);
        verify(feignCacheService, times(1)).get(cacheKey);
    }
    @Test
    void getInValidTest() {
        NodeCalendarCacheKey cacheKey = testUtil.getNodeCalendarCacheKey();

        when(feignCacheService.get(any())).thenReturn(null);
        assertNull(nodeCalendarSpringNearCacheService.get(cacheKey));
        verify(feignCacheService, times(1)).get(cacheKey);
    }

    @Test
    void deleteTest() {
        NodeCalendarCacheKey cacheKey = testUtil.getNodeCalendarCacheKey();

        nodeCalendarSpringNearCacheService.delete(cacheKey);
        CacheValue cacheValue = nodeCalendarSpringNearCacheService.get(cacheKey);
        assertNull(cacheValue);
    }

    @Test
    void deleteAllTest() {
        NodeCalendarCacheKey cacheKey = testUtil.getNodeCalendarCacheKey();

        nodeCalendarSpringNearCacheService.deleteAll();
        CacheValue cacheValue = nodeCalendarSpringNearCacheService.get(cacheKey);
        assertNull(cacheValue);
    }
}
