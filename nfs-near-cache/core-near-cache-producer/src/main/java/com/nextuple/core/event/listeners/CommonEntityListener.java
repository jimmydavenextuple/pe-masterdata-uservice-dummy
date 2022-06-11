package com.nextuple.core.event.listeners;

import com.nextuple.core.event.LocalCacheUpdateMessage;
import com.nextuple.core.exception.LocalCacheUpdateEventException;
import com.nextuple.core.mapper.NearCacheEntityNameMapper;
import com.nextuple.core.producer.EntityEventProducer;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class CommonEntityListener {

  @Autowired EntityEventProducer entityEventProducer;

  @PostPersist
  @PostUpdate
  @PostRemove
  public void afterUpdating(Object entity)
      throws IllegalAccessException, LocalCacheUpdateEventException {
    log.info("Calling afterUpdating method on updating the record!");

    Map<String, String> message = new HashMap<>();

    // Getting primary key fields and values
    List<Field> allFields = getAllFields(entity.getClass());
    for (Field field : allFields) {
      if (field.isAnnotationPresent(Id.class)) {
        field.setAccessible(true);
        message.put(field.getName(), (String) field.get(entity));
      }
    }
    // Entity Name Mapping
    String entityName =
        NearCacheEntityNameMapper.getEntityMapping().get(entity.getClass().getSimpleName());
    // Publishing to kafka
    LocalCacheUpdateMessage localCacheUpdateMessage = new LocalCacheUpdateMessage();
    localCacheUpdateMessage.setMessage(message);
    localCacheUpdateMessage.setEntityName(entityName);

    log.debug("Publishing LocalUpdateCacheEvent to Kafka with {}", localCacheUpdateMessage);
    entityEventProducer.publishEntityEvent(localCacheUpdateMessage);
  }

  private List<Field> getAllFields(Class<?> aClass) {
    List<Field> fieldsList = Arrays.stream(aClass.getDeclaredFields()).collect(Collectors.toList());
    if (aClass.getSuperclass() != null) {
      fieldsList.addAll(getAllFields(aClass.getSuperclass()));
    }
    return fieldsList;
  }
}
