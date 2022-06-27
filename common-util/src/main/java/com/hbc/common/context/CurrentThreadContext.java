package com.hbc.common.context;

/**
 * Access thread local contexts - {@link CurrentThreadContext} manages two different thread local
 * variables 1) Log Context - should be used to form log context only 2) Request Context - should be
 * used to form request context only
 *
 * <p>It uses {@link InheritableThreadLocal} to maintain thread local context.
 */
public class CurrentThreadContext {
  private static final ThreadLocal<LogContext> logContextThreadLocal = // NOSONAR
      new InheritableThreadLocal<>();

  private CurrentThreadContext() {
    // Everyone should be using static methods only
  }

  /**
   * Get {@link LogContext} from current thread - it initializes new log context if thread doesn't
   * have it
   *
   * @return {@link LogContext}
   */
  public static final LogContext getLogContext() {
    LogContext logContext = logContextThreadLocal.get();
    if (logContext == null) {
      logContext = LogContext.builder().build();
      logContextThreadLocal.set(logContext);
    }
    return logContext;
  }

  /**
   * Set new log context
   *
   * @param logContext {@link LogContext} to be set
   */
  public static final void setLogContext(LogContext logContext) {
    logContextThreadLocal.set(logContext);
  }

  /**
   * Clean already set log context
   *
   * <p>It sets log context to null
   */
  public static final void cleanLogContext() {
    logContextThreadLocal.remove();
  }
}
