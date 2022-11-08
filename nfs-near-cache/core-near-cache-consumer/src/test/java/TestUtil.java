import com.nextuple.core.event.LocalCacheUpdateEvent;
import com.nextuple.core.event.LocalCacheUpdateMessage;
import java.util.Map;

public class TestUtil {

  public LocalCacheUpdateEvent getLocalCacheUpdateEvent(
      Map<String, Object> message, String entityName) {
    LocalCacheUpdateEvent localCacheUpdateEvent = new LocalCacheUpdateEvent();
    LocalCacheUpdateMessage localCacheUpdateMessage = new LocalCacheUpdateMessage();
    localCacheUpdateMessage.setMessage(message);
    localCacheUpdateMessage.setEntityName(entityName);
    localCacheUpdateEvent.setLocalCacheUpdateMessage(localCacheUpdateMessage);

    return localCacheUpdateEvent;
  }
}
