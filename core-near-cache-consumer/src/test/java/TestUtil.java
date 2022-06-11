import com.nextuple.core.consumer.LocalCacheUpdateEvent;
import com.nextuple.core.consumer.LocalCacheUpdateMessage;
import java.util.Map;

public class TestUtil {

  public LocalCacheUpdateEvent getLocalCacheUpdateEvent(
      Map<String, String> message, String entityName) {
    LocalCacheUpdateEvent localCacheUpdateEvent = new LocalCacheUpdateEvent();
    LocalCacheUpdateMessage localCacheUpdateMessage = new LocalCacheUpdateMessage();
    localCacheUpdateMessage.setMessage(message);
    localCacheUpdateMessage.setEntityName(entityName);
    localCacheUpdateEvent.setLocalCacheUpdateMessage(localCacheUpdateMessage);

    return localCacheUpdateEvent;
  }
}
