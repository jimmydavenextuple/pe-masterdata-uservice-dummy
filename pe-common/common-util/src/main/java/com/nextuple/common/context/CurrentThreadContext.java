package com.nextuple.common.context;

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
  private static final ThreadLocal<RequestContext> requestContextThreadLocal = // NOSONAR
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
    var logContext = logContextThreadLocal.get();
    if (logContext == null) {
      logContext = LogContext.builder().build();
      logContextThreadLocal.set(logContext);
    }
    return logContext;
  }

  /**
   * Get {@link RequestContext} from current thread - it initializes new request context if thread
   * doesn't have it
   *
   * @return {@link RequestContext}
   */
  public static final RequestContext getRequestContext() {
    var requestContext = requestContextThreadLocal.get();
    if (requestContext == null) {
      requestContext = RequestContext.builder().build();
      requestContextThreadLocal.set(requestContext);
    }
    return requestContext;
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
   * Set new request context
   *
   * @param requestContext {@link RequestContext} to be set
   */
  public static final void setRequestContext(RequestContext requestContext) {
    requestContextThreadLocal.set(requestContext);
  }

  /**
   * Clean already set log context
   *
   * <p>It sets log context to null
   */
  public static final void cleanLogContext() {
    logContextThreadLocal.remove();
  }

  /**
   * Clean already set request context
   *
   * <p>It sets request context to null
   */
  public static final void cleanRequestContext() {
    requestContextThreadLocal.remove();
  }
}
