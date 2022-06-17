package com.nextuple.carrier.calendar.cache.service;

import com.nextuple.carrier.calendar.cache.domain.CarrierServiceCalendarCacheKey;
import com.nextuple.carrier.calendar.cache.domain.CarrierServiceCalendarCacheValue;
import com.nextuple.core.cache.service.GenericNearCacheService;

public interface CarrierServiceCalendarNearCacheService
    extends GenericNearCacheService<
        CarrierServiceCalendarCacheKey, CarrierServiceCalendarCacheValue> {}
