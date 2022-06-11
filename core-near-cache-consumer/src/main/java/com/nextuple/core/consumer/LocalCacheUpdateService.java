package com.nextuple.core.consumer;

import com.nextuple.core.cache.domain.CacheKey;
import com.nextuple.core.cache.service.GenericNearCacheService;
import com.nextuple.core.registry.NearCacheRegistry;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class LocalCacheUpdateService {

  private final List<GenericNearCacheService> nearCacheServices; // NOSONAR

  @Autowired NearCacheRegistry nearCacheRegistry;

  @Autowired Environment env;

  public void handleLocalCacheUpdate(LocalCacheUpdateEvent localCacheUpdateEvent)
      throws IllegalAccessException, ClassNotFoundException, NoSuchFieldException,
          InvocationTargetException, NoSuchMethodException, InstantiationException {
    String entity = localCacheUpdateEvent.getLocalCacheUpdateMessage().getEntityName();
    Map<String, String> message = localCacheUpdateEvent.getLocalCacheUpdateMessage().getMessage();
    for (GenericNearCacheService genericNearCacheService : nearCacheServices) { // NOSONAR
      if (genericNearCacheService.getEntityName().equals(entity)) {
        String className = nearCacheRegistry.getRegistry(entity);

        Class<?> c = Class.forName(className);
        Constructor<?> cons = c.getConstructor();
        CacheKey cacheKey = (CacheKey) cons.newInstance();

        String path = "near-cache.entity." + entity + ".attributes";
        String params = env.getProperty(path);

        List<String> paramsList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(params)) {
          paramsList = Arrays.asList(params.split("\\s*,\\s*"));
        }
        for (String param : paramsList) {
          Field field = c.getDeclaredField(param);
          field.setAccessible(true);
          field.set(cacheKey, message.get(param));
        }
        genericNearCacheService.delete(cacheKey); // NOSONAR
      }
    }
  }
}
