package com.hbc.carrier.calendar.cache.service;

import com.hbc.carrier.calendar.cache.domain.CarrierServiceCalendarCacheKey;
import com.hbc.carrier.calendar.cache.domain.CarrierServiceCalendarCacheValue;
import com.hbc.core.cache.service.GenericNearCacheService;

public interface CarrierServiceCalendarNearCacheService
    extends GenericNearCacheService<
        CarrierServiceCalendarCacheKey, CarrierServiceCalendarCacheValue> {}
