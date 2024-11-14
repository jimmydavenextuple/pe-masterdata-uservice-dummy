package com.nextuple.common.filter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterConfig;

@ExtendWith(MockitoExtension.class)
class AuthFilterTest {
  @InjectMocks private AuthFilter authFilter;

  @Mock private HttpServletRequest httpServletRequest;

  @Mock private HttpServletResponse httpServletResponse;

  @Mock private FilterChain filterChain;

  @Mock private AuthProperties authProperties;
  static final long EXPIRATIONTIME = 864_000_000; // 10 days

  @Test
  void doFilterTestWhenFilterIsEnabled() throws ServletException, IOException, AuthFilterException {
    String token = createToken();
    Map<String, List<String>> claims = new HashMap<>();
    claims.put("scope", List.of("sfcc-resources/edd", "management-app/admin"));

    when(authProperties.isFilterEnabled()).thenReturn(true);
    when(httpServletRequest.getRequestURI()).thenReturn("/node");
    when(authProperties.getClaims()).thenReturn(claims);
    when(authProperties.getIssuer())
        .thenReturn("https://cognito-idp.us-east-1.amazonaws.com/us-east-1_SRg9eldJN");
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
  void doFilterTestWhenFilterWhenRequestIsOptions() throws ServletException, IOException {
    when(authProperties.isFilterEnabled()).thenReturn(true);
    when(httpServletRequest.getRequestURI()).thenReturn("/node");
    when(httpServletRequest.getMethod()).thenReturn("options");

    authFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
    verify(filterChain, times(1)).doFilter(any(), any());
  }

  @Test
  void doFilterAuthFilterExceptionForInvalidClaimsTest() throws ServletException, IOException {
    String token = createTokenWithInvalidClaims();
    Map<String, List<String>> claims = new HashMap<>();
    claims.put("scope", List.of("sfcc-resources/edd", "management-app/admin"));

    when(authProperties.isFilterEnabled()).thenReturn(true);
    when(httpServletRequest.getRequestURI()).thenReturn("/node");
    when(authProperties.getClaims()).thenReturn(claims);
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
    String token = createTokenWithInvalidIssuer();
    Map<String, List<String>> claims = new HashMap<>();
    claims.put("scope", List.of("sfcc-resources/edd", "management-app/admin"));

    when(authProperties.isFilterEnabled()).thenReturn(true);
    when(httpServletRequest.getRequestURI()).thenReturn("/node");
    when(authProperties.getClaims()).thenReturn(claims);
    when(authProperties.getIssuer())
        .thenReturn("https://cognito-idp.us-east-1.amazonaws.com/us-east-1_SRg9eldJN");
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

  @Test
  void doFilterAuthFilterNullExceptionTest() throws ServletException, IOException {
    String token =
        "Bearer eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiSmFuZSBEb2UiLCJlbWFpbCI6ImphbmVAZXhhbXBsZS5jb20iLCJyb2xlIjpbIkFETUlOIiwiR1VFU1QiLCJWRU5ET1IiXSwiaXNzIjoiaHR0cHM6Ly9wZS1kZXYtaXNzdWVyLnMzLmFtYXpvbmF3cy5jb20vIiwic3ViIjoiamFuZSJ9.9xXF0-mG_YTHT9UBOJxlDcWk67NJEDZAHNhFgemZ-VU";
    Map<String, List<String>> claims = new HashMap<>();
    claims.put("scope", List.of("sfcc-resources/edd", "management-app/admin"));

    when(authProperties.isFilterEnabled()).thenReturn(true);
    when(httpServletRequest.getRequestURI()).thenReturn("/node");
    when(authProperties.getClaims()).thenReturn(claims);
    when(httpServletRequest.getHeader("Authorization")).thenReturn(token);

    Exception exception =
        assertThrows(
            AuthFilterException.class,
            () -> authFilter.doFilter(httpServletRequest, httpServletResponse, filterChain));

    assertEquals("Required claims not found", exception.getMessage());
    verify(filterChain, times(0)).doFilter(any(), any());
  }

  @Test
  void doFilterTestForManagementAppScope() throws ServletException, IOException {
    String token = createToken();
    Map<String, List<String>> claims = new HashMap<>();
    claims.put("scope", List.of("sfcc-resources/edd", "management-app/admin"));

    when(authProperties.isFilterEnabled()).thenReturn(true);
    when(httpServletRequest.getRequestURI()).thenReturn("/ui/carrier-transit");
    when(authProperties.getClaims()).thenReturn(claims);
    when(authProperties.getIssuer())
        .thenReturn("https://cognito-idp.us-east-1.amazonaws.com/us-east-1_SRg9eldJN");
    when(httpServletRequest.getHeader("Authorization")).thenReturn(token);

    authFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
    verify(filterChain, times(1)).doFilter(any(), any());
  }

  @Test
  void initTest() {
    MockFilterConfig mockFilterConfig = new MockFilterConfig();

    assertDoesNotThrow(() -> authFilter.init(mockFilterConfig));
  }

  @Test
  void destroyTest() {
    assertDoesNotThrow(() -> authFilter.destroy());
  }

  private String createToken() {
    String jwtToken =
        Jwts.builder()
            .claim("scope", "sfcc-resources/edd")
            .claim("email", "guest@example.com")
            .setSubject("guest")
            .setId(UUID.randomUUID().toString())
            .setIssuer("https://cognito-idp.us-east-1.amazonaws.com/us-east-1_SRg9eldJN")
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
            .compact();
    return jwtToken;
  }

  public static String createTokenWithInvalidClaims() {
    String jwtToken =
        Jwts.builder()
            .claim("scope", List.of("sfcc-resources/edd", "management-app/admin"))
            .claim("email", "guest@example.com")
            .setSubject("guest")
            .setId(UUID.randomUUID().toString())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
            .compact();
    return jwtToken;
  }

  public static String createTokenWithInvalidIssuer() {
    String jwtToken =
        Jwts.builder()
            .claim("scope", "sfcc-resources/edd")
            .claim("email", "guest@example.com")
            .setSubject("guest")
            .setId(UUID.randomUUID().toString())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
            .compact();
    return jwtToken;
  }
}
