package com.hbc.carriercalendar.spring.cache.service;

import com.hbc.carrier.calendar.cache.domain.CarrierServiceCalendarCacheKey;
import com.hbc.carrier.calendar.cache.domain.CarrierServiceCalendarCacheValue;
import com.hbc.carrier.calendar.cache.spring.service.CarrierServiceCalendarSpringNearCacheService;
import com.hbc.carriercalendar.spring.cache.util.TestUtil;
import com.hbc.core.cache.domain.CacheValue;
import com.hbc.core.cache.service.GenericFeignCacheService;
import com.hbc.core.spring.service.AbstractGenericFeignClientServiceImpl;
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
public class CarrierCalendarSpringNearCacheServiceImplTest {

    @InjectMocks
    private CarrierServiceCalendarSpringNearCacheService carrierServiceCalendarSpringNearCacheService;

    @InjectMocks private TestUtil testUtil;

    @Mock private AbstractGenericFeignClientServiceImpl abstractGenericFeignClientService;
    @Mock
    private GenericFeignCacheService<CarrierServiceCalendarCacheKey, CarrierServiceCalendarCacheValue> feignCacheService;

    @Test
    void getValidTest() {
        CarrierServiceCalendarCacheKey cacheKey = testUtil.getCarrierServiceCalendarCacheKey();
        CarrierServiceCalendarCacheValue cacheValue = testUtil.getCarrierServiceCalendarCacheValue();

        when(feignCacheService.get(any())).thenReturn(cacheValue);
        when(abstractGenericFeignClientService.get(any())).thenReturn(cacheValue);

        // First Invocation
        CacheValue cacheValue1 = carrierServiceCalendarSpringNearCacheService.get(cacheKey);
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
        CarrierServiceCalendarCacheKey cacheKey = testUtil.getCarrierServiceCalendarCacheKey();

        when(feignCacheService.get(any())).thenReturn(null);
        assertNull(carrierServiceCalendarSpringNearCacheService.get(cacheKey));
        verify(feignCacheService, times(1)).get(cacheKey);
    }

    @Test
    void deleteTest() {
        CarrierServiceCalendarCacheKey cacheKey = testUtil.getCarrierServiceCalendarCacheKey();

        carrierServiceCalendarSpringNearCacheService.delete(cacheKey);
        CacheValue cacheValue = carrierServiceCalendarSpringNearCacheService.get(cacheKey);
        assertNull(cacheValue);
    }

    @Test
    void deleteAllTest() {
        CarrierServiceCalendarCacheKey cacheKey = testUtil.getCarrierServiceCalendarCacheKey();

        carrierServiceCalendarSpringNearCacheService.deleteAll();
        CacheValue cacheValue = carrierServiceCalendarSpringNearCacheService.get(cacheKey);
        assertNull(cacheValue);
    }

}
