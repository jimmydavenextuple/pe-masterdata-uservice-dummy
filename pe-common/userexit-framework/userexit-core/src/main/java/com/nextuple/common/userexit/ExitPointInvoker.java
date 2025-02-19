package com.nextuple.common.userexit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jayway.jsonpath.DocumentContext;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.userexit.domain.UserExitData;
import com.nextuple.common.userexit.domain.dto.ErrorWrapper;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

/**
 * Interface for invoking User Exit points, which are customizable hooks that can be triggered
 * during the execution of a specific operation.
 *
 * <p>This interface defines the contract for classes that are responsible for executing user exit
 * points, allowing different implementations for various execution strategies.
 *
 * @param <T> The type of input data required for invoking the user exit.
 * @param <G> The type of output data returned by the user exit.
 */
public interface ExitPointInvoker<T, G> {

  /**
   * Fetches the User Exit data for the specified organization, user exit name, application name,
   * and service name.
   *
   * @param orgId The unique identifier of the organization.
   * @param userExitName The name of the User Exit to be fetched.
   * @param appName The name of the application associated with the User Exit.
   * @param serviceName The name of the service associated with the User Exit.
   * @return A {@link UserExitData} object containing metadata and configuration details for the
   *     specified User Exit.
   * @throws CommonServiceException If there is an error while fetching the User Exit data.
   *     <p>This method is responsible for retrieving the metadata and configuration details for a
   *     given User Exit. It may log debugging information about the input parameters and handle
   *     exceptions if the data cannot be fetched.
   * @see UserExitData
   * @see CommonServiceException
   */
  UserExitData fetchUEData(String orgId, String userExitName, String appName, String serviceName)
      throws CommonServiceException;

  /**
   * Invokes a User Exit implementation with the provided input data and configuration details.
   *
   * @param userExitData The metadata and configuration data associated with the User Exit being
   *     invoked.
   * @param inputData The input data to be passed to the User Exit implementation.
   * @param documentContext The JSON document context used for reading attribute values via JSON
   *     paths.
   * @param inputClazz The type reference for the input data object.
   * @param outputClazz The type reference for the output data object.
   * @return An {@link ErrorWrapper} containing the output data of type {@code G} or error details
   *     in case of failure.
   * @throws URISyntaxException If there is an issue with URI syntax during the User Exit
   *     invocation.
   * @throws IOException If an I/O error occurs during execution.
   * @throws InterruptedException If the invocation is interrupted.
   * @throws ClassNotFoundException If a required class is not found during reflection or
   *     deserialization.
   * @throws InvocationTargetException If an exception is thrown by the invoked method.
   * @throws NoSuchMethodException If a required method is not found during reflection.
   * @throws InstantiationException If an instance of a class cannot be created.
   * @throws IllegalAccessException If access to a method or field is not allowed during reflection.
   * @see ErrorWrapper
   */
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
