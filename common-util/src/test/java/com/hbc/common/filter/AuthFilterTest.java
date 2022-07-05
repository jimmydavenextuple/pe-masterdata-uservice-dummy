package com.hbc.common.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthFilterTest {
  @InjectMocks private AuthFilter authFilter;

  @Mock private HttpServletRequest httpServletRequest;

  @Mock private HttpServletResponse httpServletResponse;

  @Mock private FilterChain filterChain;

  @Mock private AuthProperties authProperties;

  @Test
  void doFilterTestWhenFilterIsEnabled() throws ServletException, IOException {
    String token =
        "Bearer eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiSmFuZSBEb2UiLCJlbWFpbCI6ImphbmVAZXhhbXBsZS5jb20iLCJyb2xlIjpbIkFETUlOIiwiR1VFU1QiLCJWRU5ET1IiXSwiaXNzIjoiaHR0cHM6Ly9wZS1kZXYtaXNzdWVyLnMzLmFtYXpvbmF3cy5jb20vIiwic3ViIjoiamFuZSJ9.9xXF0-mG_YTHT9UBOJxlDcWk67NJEDZAHNhFgemZ-VU";

    when(authProperties.isFilterEnabled()).thenReturn(true);
    when(httpServletRequest.getRequestURI()).thenReturn("/node");
    when(authProperties.getRoles()).thenReturn(Arrays.asList("GUEST1", "GUEST2", "ADMIN"));
    when(authProperties.getIssuer()).thenReturn("https://pe-dev-issuer.s3.amazonaws.com/");
    when(httpServletRequest.getHeader("Authorization")).thenReturn(token);

    authFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
    verify(filterChain, times(1)).doFilter(any(), any());
  }

  @Test
  void doFilterTestWhenFilterIsEnabledAndSingleRoleIsPassed() throws ServletException, IOException {
    String token =
        "Bearer eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiSmFuZSBEb2UiLCJlbWFpbCI6ImphbmVAZXhhbXBsZS5jb20iLCJyb2xlIjoiQURNSU4iLCJpc3MiOiJodHRwczovL3BlLWRldi1pc3N1ZXIuczMuYW1hem9uYXdzLmNvbS8iLCJzdWIiOiJqYW5lIn0.VAYlinDMOgpDp_UgHZyxCImvycfHV0cj1H6Imhlg6QY";

    when(authProperties.isFilterEnabled()).thenReturn(true);
    when(httpServletRequest.getRequestURI()).thenReturn("/node");
    when(authProperties.getRoles()).thenReturn(Arrays.asList("GUEST1", "GUEST2", "ADMIN"));
    when(authProperties.getIssuer()).thenReturn("https://pe-dev-issuer.s3.amazonaws.com/");
    when(httpServletRequest.getHeader("Authorization")).thenReturn(token);

    authFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
    verify(filterChain, times(1)).doFilter(any(), any());
  }

  @Test
  void doFilterTestWhenFilterIsNotEnabled() throws ServletException, IOException {
    when(authProperties.isFilterEnabled()).thenReturn(false);

    authFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
    verify(filterChain, times(1)).doFilter(any(), any());
  }

  @Test
  void doFilterTestWhenFilterWhenURIStartsWithActuator() throws ServletException, IOException {
    when(authProperties.isFilterEnabled()).thenReturn(true);
    when(httpServletRequest.getRequestURI()).thenReturn("/actuator");

    authFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
    verify(filterChain, times(1)).doFilter(any(), any());
  }

  @Test
  void doFilterAuthFilterExceptionForInvalidRolesTest() throws ServletException, IOException {
    String token =
        "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiSmFuZSBEb2UiLCJlbWFpbCI6ImphbmVAZXhhbXBsZS5jb20iLCJyb2xlIjpbIklOVkFMSURfUk9MRTEiLCJJTlZBTElEX1JPTEUyIl0sImlzcyI6Imh0dHBzOi8vcGUtZGV2LWlzc3Vlci5zMy5hbWF6b25hd3MuY29tLyIsInN1YiI6ImphbmUifQ.oL-t-7tkSFsMYCUzkzM7x3y2o8En712-tE9w4__YrAU";

    when(authProperties.isFilterEnabled()).thenReturn(true);
    when(httpServletRequest.getRequestURI()).thenReturn("/node");
    when(authProperties.getRoles()).thenReturn(Arrays.asList("GUEST1", "GUEST2", "ADMIN"));
    when(authProperties.getIssuer()).thenReturn("https://pe-dev-issuer.s3.amazonaws.com/");
    when(httpServletRequest.getHeader("Authorization")).thenReturn(token);

    Exception exception =
        assertThrows(
            AuthFilterException.class,
            () -> authFilter.doFilter(httpServletRequest, httpServletResponse, filterChain));

    assertEquals("Verification failed", exception.getMessage());
    verify(filterChain, times(0)).doFilter(any(), any());
  }

  @Test
  void doFilterAuthFilterExceptionForInvalidIssuerTest() throws ServletException, IOException {
    String token =
        "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiSmFuZSBEb2UiLCJlbWFpbCI6ImphbmVAZXhhbXBsZS5jb20iLCJyb2xlIjpbIkFETUlOIiwiR1VFU1QiLCJWRU5ET1IiXSwiaXNzIjoiaW52YWxpZF9pc3N1ZXIiLCJzdWIiOiJqYW5lIn0.oUKXicOhJbsrc57FjjCbiOBGZ_VVQlp4PkfjBCelDFg";

    when(authProperties.isFilterEnabled()).thenReturn(true);
    when(httpServletRequest.getRequestURI()).thenReturn("/node");
    when(authProperties.getRoles()).thenReturn(Arrays.asList("GUEST1", "GUEST2", "ADMIN"));
    when(authProperties.getIssuer()).thenReturn("https://pe-dev-issuer.s3.amazonaws.com/");
    when(httpServletRequest.getHeader("Authorization")).thenReturn(token);

    Exception exception =
        assertThrows(
            AuthFilterException.class,
            () -> authFilter.doFilter(httpServletRequest, httpServletResponse, filterChain));

    assertEquals("Verification failed", exception.getMessage());
    verify(filterChain, times(0)).doFilter(any(), any());
  }

  @Test
  void doFilterAuthFilterExceptionForEmptyHeaderValueTest() throws ServletException, IOException {

    when(authProperties.isFilterEnabled()).thenReturn(true);
    when(httpServletRequest.getRequestURI()).thenReturn("/node");
    when(httpServletRequest.getHeader("Authorization")).thenReturn("");

    Exception exception =
        assertThrows(
            AuthFilterException.class,
            () -> authFilter.doFilter(httpServletRequest, httpServletResponse, filterChain));

    assertEquals("Authorization header value is empty or null", exception.getMessage());
    verify(filterChain, times(0)).doFilter(any(), any());
  }

  @Test
  void doFilterExceptionTest() throws ServletException, IOException {
    String token = "abc-123";

    when(authProperties.isFilterEnabled()).thenReturn(true);
    when(httpServletRequest.getRequestURI()).thenReturn("/node");
    when(httpServletRequest.getHeader("Authorization"))
        .thenThrow(new RuntimeException("Error while authenticating the request"));

    Exception exception =
        assertThrows(
            RuntimeException.class,
            () -> authFilter.doFilter(httpServletRequest, httpServletResponse, filterChain));

    assertEquals("Error while authenticating the request", exception.getMessage());
    verify(filterChain, times(0)).doFilter(any(), any());
  }
}
