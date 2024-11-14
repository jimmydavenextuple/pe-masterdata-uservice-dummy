package com.nextuple.common.exception;

/**
 * Exception should be thrown when u-services expects header {@code
 * com.nextuple.controltower.common.constants.CommonConstants .HEADER_TENANT_ID} for each request
 * and cannot process any request without it
 */
public class TenantIdHeaderNotFoundException extends RuntimeException {}
