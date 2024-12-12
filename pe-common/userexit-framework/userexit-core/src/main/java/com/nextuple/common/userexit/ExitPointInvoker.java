package com.nextuple.common.userexit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jayway.jsonpath.DocumentContext;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.userexit.domain.UserExitData;
import com.nextuple.common.userexit.domain.dto.ErrorWrapper;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

public interface ExitPointInvoker<T, G> {

  UserExitData fetchUEData(String orgId, String userExitName, String appName, String serviceName)
      throws CommonServiceException;

  ErrorWrapper<G> invoke(
      UserExitData userExitData,
      T inputData,
      DocumentContext documentContext,
      TypeReference<T> inputClazz,
      TypeReference<G> outputClazz)
      throws URISyntaxException,
          IOException,
          InterruptedException,
          ClassNotFoundException,
          InvocationTargetException,
          NoSuchMethodException,
          InstantiationException,
          IllegalAccessException;
}
