package com.hbc.common.context;

/**
 * Logger factory provides a way to generate custom {@link Logger} or Slf4j {@link org.slf4j.Logger}
 */
public class LoggerFactory {

  private LoggerFactory() {
    // Use static methods
  }

  /**
   * Generate a logger wrapper {@link Logger} for given name
   *
   * @param name Logger name
   * @return logger {@link Logger}
   */
  public static Logger getLogger(String name) {
    return new Logger(org.slf4j.LoggerFactory.getLogger(name));
  }

  /**
   * Generate a logger wrapper {@link Logger} for given class
   *
   * @param clazz Logger class
   * @return logger {@link Logger}
   */
  public static Logger getLogger(Class<?> clazz) {
    return new Logger(org.slf4j.LoggerFactory.getLogger(clazz));
  }

  /**
   * Generate a SLF4J Logger for given name
   *
   * @param name Logger name
   * @return {@link org.slf4j.Logger}
   */
  public static org.slf4j.Logger getSlf4jLogger(String name) {
    return org.slf4j.LoggerFactory.getLogger(name);
  }

  /**
   * Generate a SLF4J Logger for given class
   *
   * @param clazz Logger name
   * @return {@link org.slf4j.Logger}
   */
  public static org.slf4j.Logger getSlf4jLogger(Class<?> clazz) {
    return org.slf4j.LoggerFactory.getLogger(clazz);
  }
}
