package com.nextuple.common.userexit;

import java.util.Map;

/**
 * Interface for class-based User Exit implementations.
 *
 * @param <T> The type of the input data.
 * @param <G> The type of the output data.
 */
public interface IClassBasedExit<T, G> {
  /**
   * Fetches the response for the provided input data and custom attributes.
   *
   * @param inputData The input data used for processing the User Exit.
   * @param customAttributeMap A map containing custom attributes for processing.
   * @return The processed response of type {@code G}.
   *     <p>This method is designed to handle the execution logic of a User Exit, where the input
   *     data and custom attributes are processed to produce an output. The implementation is
   *     expected to define how the response is generated.
   */
  G fetchResponse(T inputData, Map<String, Object> customAttributeMap);
}
