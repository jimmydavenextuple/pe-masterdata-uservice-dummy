package com.nextuple.core.consumer;

import com.nextuple.core.cache.domain.CacheKey;
import com.nextuple.core.cache.service.GenericNearCacheService;
import com.nextuple.core.event.LocalCacheUpdateEvent;
import com.nextuple.core.registry.NearCacheRegistry;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class LocalCacheUpdateService {

  private static final Logger logger = LoggerFactory.getLogger(LocalCacheUpdateService.class);
  private final List<GenericNearCacheService> nearCacheServices; // NOSONAR

  @Autowired NearCacheRegistry nearCacheRegistry;

  @Autowired Environment env;

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
      }
    }
  }

  private static void castToRequiredType(Map<String, Object> message, String param, Field field) {
    if (field.getType().isAssignableFrom(Long.class)) {
      message.put(param, Long.valueOf(String.valueOf(message.get(param))));
    }
  }
}
