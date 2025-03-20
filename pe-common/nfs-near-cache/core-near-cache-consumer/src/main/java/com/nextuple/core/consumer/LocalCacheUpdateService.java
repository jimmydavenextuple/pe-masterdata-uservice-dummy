package com.nextuple.core.consumer;

import com.nextuple.core.cache.domain.CacheKey;
import com.nextuple.core.cache.service.GenericNearCacheService;
import com.nextuple.core.event.LocalCacheUpdateEvent;
import com.nextuple.core.registry.NearCacheRegistry;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class LocalCacheUpdateService {

  private static final Logger logger = LoggerFactory.getLogger(LocalCacheUpdateService.class);
  private final List<GenericNearCacheService> nearCacheServices; // NOSONAR
  private static final String TRANSFER_SCHEDULE_CACHE_KEY_CLASS =
      "com.nextuple.transit.cache.domain.TransferScheduleCacheKey";

  @Autowired NearCacheRegistry nearCacheRegistry;

  @Autowired Environment env;

  @Value("${transfer-schedule.horizon-days}")
  private int horizonDays;

  @Value("${transfer-schedule.past-days}")
  private int pastDays;

  public void handleLocalCacheUpdate(LocalCacheUpdateEvent localCacheUpdateEvent)
      throws IllegalAccessException,
          ClassNotFoundException,
          NoSuchFieldException,
          InvocationTargetException,
          NoSuchMethodException,
          InstantiationException {
    String entity = localCacheUpdateEvent.getLocalCacheUpdateMessage().getEntityName();
    Map<String, Object> message = localCacheUpdateEvent.getLocalCacheUpdateMessage().getMessage();
    for (GenericNearCacheService genericNearCacheService : nearCacheServices) { // NOSONAR
      if (genericNearCacheService.getEntityName().equals(entity)) {
        Map<String, String> registryDetails = nearCacheRegistry.getRegistry(entity);
        String className = (String) registryDetails.keySet().toArray()[0];

        logger.debug("Class name :{}", className);
        String dropType = registryDetails.get(className);

        if (dropType.equals("full")) {
          genericNearCacheService.deleteAll();
        } else {
          Class<?> c = Class.forName(className);
          Constructor<?> cons = c.getConstructor();
          var cacheKey = (CacheKey) cons.newInstance();

          if (className.equals(TRANSFER_SCHEDULE_CACHE_KEY_CLASS)) {
            handleTransferSchedulesCacheUpdation(
                genericNearCacheService, message, className, cacheKey);
          } else {
            handleCacheUpdationForPartialDrop(
                genericNearCacheService, entity, c, message, cacheKey);
          }
        }
      }
    }
  }

  private void handleCacheUpdationForPartialDrop(
      GenericNearCacheService genericNearCacheService, // NOSONAR
      String entity,
      Class<?> c,
      Map<String, Object> message,
      CacheKey cacheKey)
      throws NoSuchFieldException, IllegalAccessException {
    String path = "nearcache.entity." + entity + ".attributes";
    String params = env.getProperty(path);

    logger.debug("Params list :{}", params);

    List<String> paramsList = new ArrayList<>();
    if (!ObjectUtils.isEmpty(params)) {
      paramsList = Arrays.asList(params.split("\\s*,\\s*")); // NOSONAR
    }
    for (String param : paramsList) {
      var field = c.getDeclaredField(param);
      castToRequiredType(message, param, field);
      field.setAccessible(true); // NOSONAR
      field.set(cacheKey, message.get(param)); // NOSONAR
    }
    logger.debug("Cache key :{}", cacheKey);

    genericNearCacheService.delete(cacheKey); // NOSONAR
  }

  private void handleTransferSchedulesCacheUpdation(
      GenericNearCacheService genericNearCacheService, // NOSONAR
      Map<String, Object> message,
      String className,
      CacheKey cacheKey)
      throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
    Class<?> c = Class.forName(className);

    Map<String, String> paramsToMessageFieldMap =
        Map.of(
            "orgId",
            "orgId",
            "rule",
            "rule",
            "ruleName",
            "ruleName",
            "dropoffNode",
            "dropoffNodeId");

    for (Entry<String, String> paramEntry : paramsToMessageFieldMap.entrySet()) {
      var field = c.getDeclaredField(paramEntry.getKey());
      castToRequiredType(message, paramEntry.getValue(), field);
      field.setAccessible(true); // NOSONAR
      field.set(cacheKey, message.get(paramEntry.getValue())); // NOSONAR
    }

    // set date bucket here
    DateTime startDateTime =
        new DateTime(message.get("startTime")).toDateTime(DateTimeZone.UTC); // Mar 19
    DateTime endDateTime =
        new DateTime(message.get("endTime")).toDateTime(DateTimeZone.UTC); // Mar 24

    Set<Date> dateBucket = new HashSet<>();
    for (int i = 0; i < horizonDays; i++) {
      dateBucket.add(startDateTime.minusDays(i).withTime(0, 0, 0, 0).toDate());
    }
    for (int i = 0; i < pastDays; i++) {
      dateBucket.add(endDateTime.plusDays(i).withTime(0, 0, 0, 0).toDate());
    }
    var field = c.getDeclaredField("dateBucket");
    field.setAccessible(true); // NOSONAR
    for (Date date : dateBucket) {
      field.set(cacheKey, date); // NOSONAR
      genericNearCacheService.delete(cacheKey); // NOSONAR
    }
  }

  private static void castToRequiredType(Map<String, Object> message, String param, Field field) {
    if (field.getType().isAssignableFrom(Long.class)) {
      message.put(param, Long.valueOf(String.valueOf(message.get(param))));
    }
  }
}
