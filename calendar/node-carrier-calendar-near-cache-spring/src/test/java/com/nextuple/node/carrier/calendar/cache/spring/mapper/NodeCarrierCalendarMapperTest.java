package com.nextuple.node.carrier.calendar.cache.spring.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.nextuple.calendar.domain.CalendarDaysStatusInfo;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheValue;
import com.nextuple.node.carrier.calendar.cache.spring.util.TestUtil;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeCarrierCalendarMapperTest {

  @InjectMocks private NodeCarrierCalendarMapper mapper;

  @InjectMocks private TestUtil testUtil;

  @Test
  void requestToCacheKey() {
    assertNull(mapper.requestToCacheKey("request"));
  }

  @Test
  void cacheKeyToRequest() {
    assertNull(mapper.cacheKeyToRequest(testUtil.getNodeCarrierCalendarCacheKey()));
  }

  @Test
  void responseToCacheValue() {
    NodeCarrierCalendarCacheValue cacheValue = testUtil.getNodeCarrierCalendarCacheValue();

    BaseResponse<List<CalendarDaysStatusInfo>> response =
        testUtil.getBaseResponseOfListOfCalendarDaysStatusInfo();

    assertEquals(cacheValue, mapper.responseToCacheValue(response));
  }

  @Test
  void cacheValueToResponse() {
    assertNull(mapper.cacheValueToResponse(testUtil.getNodeCarrierCalendarCacheValue()));
  }
}
