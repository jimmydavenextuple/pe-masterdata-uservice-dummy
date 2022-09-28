package com.hbc.common.filter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

  @Test
  void doFilterTestWhenFilterIsEnabled() throws ServletException, IOException {
    String token =
        "Bearer eyJraWQiOiJnZkNGQWtKcWRXcFduOVY4eXFSMG5XSUd6K3NCbm1KZGdybEJTc2pPR1MwPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiIxMDJ0cmJnaDNkdmdjdTd2Z2ludWxkam5vdCIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoic2ZjYy1yZXNvdXJjZXNcL2VkZCIsImF1dGhfdGltZSI6MTY1Nzc5NzcxNSwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLnVzLWVhc3QtMS5hbWF6b25hd3MuY29tXC91cy1lYXN0LTFfU1JnOWVsZEpOIiwiZXhwIjoxNjU3ODAxMzE1LCJpYXQiOjE2NTc3OTc3MTUsInZlcnNpb24iOjIsImp0aSI6ImZjOWM4MmFmLWNmZDMtNGY0OS04NjZkLTUyMDZjMTE1MDFiZSIsImNsaWVudF9pZCI6IjEwMnRyYmdoM2R2Z2N1N3ZnaW51bGRqbm90In0.U2qsCTe79UO6yllpyN7B5wf_8Veinio0Lxjn5-zLhLcbMubvNiYJ-rjKVtW3ftGRYu3dUeU9ODn0ly3Ht_5uHltq6CIdOLBqUnNuzCVmw2aH2qz_YRIPrl25Iaw2f13JJEbD9IyugHQdNoD7AcI3pewvjX0Qo7mDG9032lVvWMvkWoz8iY-CDiCSh6ze_ABOYnAgq70DfjIopUbQa7HY2Uh7cJi7jbJb-ORfUakpzINCF0HVpBrb9p4d0dYfLD713kGIn0ym7jLmC6sOSGodH-pPivB-AZHciE1HXY8OnJE7PH_SunknDm6bH2jaSVAPq_ETlYSv7KgEJcWsGqchzQ";
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
    String token =
        "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjJGOUFGODk3LUI2OTEtNEQzMC05M0E5LUE1OEZFMDQ2MERGMiJ9.eyJzY29wZSI6InNmY2MtcmVzb3VyY2VzL2VzdCIsImlhdCI6MTY1ODEyOTY3NCwiZXhwIjoxNjU5NDI1Njc0LCJhdWQiOiJwcm9taXNlLWVuZ2luZSIsImlzcyI6Imh0dHBzOi8vY29nbml0by1pZHAudXMtZWFzdC0xLmFtYXpvbmF3cy5jb20vdXMtZWFzdC0xX1NSZzllbGRKTiJ9.L7KdFcmzo0beUM5bLQu8BJ7hzPsM5pp0rkumugnQn5zUYOZnTvzWOUZ2n2FUvyJUgSBGltjZkUjEZsyM31HjkjgCgr3q2kV58JMKjmzNKHz0V2f0e07xg_ipHT9vTTDcyPouu2dIlq6C0eGR2OpSWpbbO8Dgi3sycDQzAlmG35y3C36FZ9eNmWSSgSDEwVND9n1DbjbSz0sgfGRcJihaKMNvPJ2hAiIvOk31pM3WKDiN0cQPYcCFF98UAtBoTo5WoJ3CoDGE6wslDw_hcsqsLuxLSJ1dCFLxsjvqDJq7GVxqjUB-sQXr4d6H5KHDaqOBOXQADYZgVZohDB3BTqbkXj2PJIWU7llCyWcrwKFIWTRY3XZTsQbRuzfzomOmAYDPlrMrACDEYdFsjRHCd17spuDuZA6Af6P2HF3WP6OatkLZbdIXIDCY_bucCJ8VO_-vmnSvigF_Sb_NJuCvW5WhL7LiRjXHcD1IdEAK66BW-BvG3K0KeALZAH89TWIGx8QUizkVCwWlGBk7CH6G8HnfLwEQQx_MuVqCz7kVIGZvvNJUoGEfTntiMnCwpTtAC2HP-RakDSrL1W7jeNJhKaJzWeGQgoS54cZ2x1b0N6TlOKBw1B_ODu2AOGrESD_N9vtPPUOyrb86JSrI6uG9R9mHt44ij1F9BbUxfEledLOxtNY";
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
    String token =
        "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IjJGOUFGODk3LUI2OTEtNEQzMC05M0E5LUE1OEZFMDQ2MERGMiJ9.eyJzY29wZSI6InNmY2MtcmVzb3VyY2VzL2VkZCIsImlhdCI6MTY1ODEyOTczMywiZXhwIjoxNjU5NDI1NzMzLCJhdWQiOiJwcm9taXNlLWVuZ2luZSIsImlzcyI6Imh0dHBzOi8vcGUtZGV2LWlzc3Vlci5zMy5hbWF6b25hd3MuY29tLyJ9.ErF3e9BBdPqBOlKSYDGn09LZEdSnpDFEWA0ye55N1TWTPAsLL4KG_7kf2IPiWxCzcoLdlLh1TLFJ28Vy46NV8EVkXGuiyeFGbI8_Dan0k5jF1GRpZlAYDPhqfPkpaGaHDEgmCse7s-LPyz4WdnTuI_tvby32BG9vNejrdkGCf1xh4kmpXaSll4xkIwCvuY8FU9gGcmW-In_xSVc5A2mOdg7X5A4AlTLVrPJp0fVPJbAzVybWwDAxmscOT5VCJR_3LCPMReE_-yq9KFlXKNTL6MZVyHD1FJEgAJc_rZ7CWGJWd8V_A2iOHN3f4_OZmwxtpgJPAhTGm1rz0WGBJzUipNeLOI0dSpMdgf1iBE5nN-cCKCTHLsKEFF_jdoWA6cox3aACypkd0knm_ghHhJeuYqflyIi5Ku9RoKlmwNZ_Ey7XJMXFW11i8Iqsv6Oa-y6kWXkPNCsGJVfOA0es38putpYnTmUaFQPwttQeyM6HOIJ2RFMKwogTjott7JupTQ95Q5q68_BJvsGN4VGSMs207ZqqmZAf3V5J97KO1yP4OnqCRHNqMO0gtK2dqrkn9SeOwJ776CahCZUqO--ojW9AHVbqnsyhOsrxStrmtyWHUSMaf2b1BV2C0lns33yxXZUzki6IbykrSfY6N3o2bAEyaxXo2SBRwbni3AfvM5f7mZo";
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
    String token =
        "Bearer eyJraWQiOiJnZkNGQWtKcWRXcFduOVY4eXFSMG5XSUd6K3NCbm1KZGdybEJTc2pPR1MwPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJkNGY1ZGJjOC0zY2ZlLTQxNWEtYTEwNS1lMjEzYWI5ZWM3NTkiLCJ0b2tlbl91c2UiOiJhY2Nlc3MiLCJzY29wZSI6Im1hbmFnZW1lbnQtYXBwXC9hZG1pbiIsImF1dGhfdGltZSI6MTY2MjAzOTY1OCwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLnVzLWVhc3QtMS5hbWF6b25hd3MuY29tXC91cy1lYXN0LTFfU1JnOWVsZEpOIiwiZXhwIjoxNjYyMDQzMjU4LCJpYXQiOjE2NjIwMzk2NTgsInZlcnNpb24iOjIsImp0aSI6Ijk2NWM0MzM0LThkZmUtNDRjMy04YmZkLTYxOGIzOWQ0ZmNkMCIsImNsaWVudF9pZCI6IjVsbzB2NzVhdjJmMGJkNGxwOXYyaTYza3FmIiwidXNlcm5hbWUiOiJhenVyZS1hZF9nb3V0aGFtLnNla2hhci12ZW5kb3JAdGhlYmF5LmNvbSJ9.QBxmv9O2PFuowhWcqHuhLTWitGTdqgFpoM5oMXH5RJtr5Lot2xRvJ5yjjSwrLcBvlcj8PbEKXYGRPkikYEFDizU8v3GS21WBV6LEx-SoYLN9MeKV9cOqY5vWjo2P68-As3kKWC9mvVHVo_WcVNwIKnFcIUwU5vVwkEFCHaev2EH3Ag2SFsrd5GfrJxUK2TdKhgZrFQXfH1jQQdyd1B_gZPNdDUarX523_QUH0k4xQ8sOQ1odjaCpZ6VC0WNCGdClso5s6Wfi-ZHEu6IHhgjYjgFgZf3SrqThDcz-loefa_9aNk5kZNpqBdvEt_AqhQyYCae0GSgmOh7ik3YNNb81Qg";
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
}
