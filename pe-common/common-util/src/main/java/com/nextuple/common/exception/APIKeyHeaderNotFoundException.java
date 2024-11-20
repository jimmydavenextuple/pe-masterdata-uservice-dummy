package com.nextuple.common.exception;

/**
 * Exception should be thrown when u-services expects {@code CommonConstants.HEADER_API_KEY} to be
 * passed in each request and cannot process any request without that.
 */
public class APIKeyHeaderNotFoundException extends RuntimeException {}
