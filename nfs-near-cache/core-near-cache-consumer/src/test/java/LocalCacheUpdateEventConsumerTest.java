import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.hbc.core.constants.NearCacheConstants;
import com.hbc.core.consumer.LocalCacheUpdateEventConsumer;
import com.hbc.core.consumer.LocalCacheUpdateService;
import com.hbc.core.event.LocalCacheUpdateEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LocalCacheUpdateEventConsumerTest {

  @InjectMocks private LocalCacheUpdateEventConsumer localCacheUpdateEventConsumer;
  @InjectMocks private TestUtil testUtil;

  @Mock private LocalCacheUpdateService localCacheUpdateService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void localCacheUpdateConsumerTest()
      throws IllegalAccessException, ClassNotFoundException, NoSuchFieldException,
          InvocationTargetException, NoSuchMethodException, InstantiationException {

    Map<String, Object> message = new HashMap<>();
    message.put("nodeId", "Node-1");
    message.put("orgId", "Tenant-1");
    doNothing()
        .when(localCacheUpdateService)
        .handleLocalCacheUpdate(any(LocalCacheUpdateEvent.class));

    localCacheUpdateEventConsumer.onLocalCacheUpdateEvent(
        testUtil.getLocalCacheUpdateEvent(message, NearCacheConstants.NODE_ENTITY_NAME), null);
    verify(localCacheUpdateService, times(1))
        .handleLocalCacheUpdate(any(LocalCacheUpdateEvent.class));
  }

  @Test
  void localCacheUpdateConsumerTestException()
      throws IllegalAccessException, ClassNotFoundException, NoSuchFieldException,
          InvocationTargetException, NoSuchMethodException, InstantiationException {
    Map<String, Object> message = new HashMap<>();
    message.put("nodeId", "Node-1");
    message.put("orgId", "Tenant-1");
    doThrow(new RuntimeException("error"))
        .when(localCacheUpdateService)
        .handleLocalCacheUpdate(any(LocalCacheUpdateEvent.class));

    Exception ex =
        assertThrows(
            Exception.class,
            () ->
                localCacheUpdateEventConsumer.onLocalCacheUpdateEvent(
                    testUtil.getLocalCacheUpdateEvent(message, NearCacheConstants.NODE_ENTITY_NAME),
                    null));
    assertEquals("error", ex.getMessage());
  }
}
