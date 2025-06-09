package tqs.sparkflow.stationservice.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import tqs.sparkflow.stationservice.util.JwtUtil;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private FilterChain filterChain;
    
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil);
        // Clear security context before each test
        SecurityContextHolder.clearContext();
    }
    
    @Test
    @DisplayName("Should authenticate user with valid Bearer token")
    void shouldAuthenticateUserWithValidBearerToken() throws ServletException, IOException {
        // Given
        String token = "valid.jwt.token";
        String bearerToken = "Bearer " + token;
        String username = "testuser";
        String email = "test@example.com";
        Boolean isOperator = false;
        
        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(jwtUtil.validateToken(token, username)).thenReturn(true);
        when(jwtUtil.extractIsOperator(token)).thenReturn(isOperator);
        when(jwtUtil.extractEmail(token)).thenReturn(email);
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo(username);
        assertThat(auth.getAuthorities()).hasSize(1);
        assertThat(auth.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
        assertThat(auth.isAuthenticated()).isTrue();
        
        verify(filterChain).doFilter(request, response);
    }
    
    @Test
    @DisplayName("Should authenticate admin with valid Bearer token and operator status")
    void shouldAuthenticateAdminWithValidBearerTokenAndOperatorStatus() throws ServletException, IOException {
        // Given
        String token = "valid.jwt.token";
        String bearerToken = "Bearer " + token;
        String username = "admin";
        String email = "admin@example.com";
        Boolean isOperator = true;
        
        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(jwtUtil.validateToken(token, username)).thenReturn(true);
        when(jwtUtil.extractIsOperator(token)).thenReturn(isOperator);
        when(jwtUtil.extractEmail(token)).thenReturn(email);
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo(username);
        assertThat(auth.getAuthorities()).hasSize(1);
        assertThat(auth.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_OPERATOR");
        assertThat(auth.isAuthenticated()).isTrue();
        
        verify(filterChain).doFilter(request, response);
    }
    
    @Test
    @DisplayName("Should not authenticate with invalid token")
    void shouldNotAuthenticateWithInvalidToken() throws ServletException, IOException {
        // Given
        String token = "invalid.jwt.token";
        String bearerToken = "Bearer " + token;
        String username = "testuser";
        
        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(jwtUtil.validateToken(token, username)).thenReturn(false);
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
        
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extractIsOperator(anyString());
        verify(jwtUtil, never()).extractEmail(anyString());
    }
    
    @Test
    @DisplayName("Should not authenticate when Authorization header is missing")
    void shouldNotAuthenticateWhenAuthorizationHeaderIsMissing() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
        
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extractUsername(anyString());
    }
    
    @Test
    @DisplayName("Should not authenticate when Authorization header does not start with Bearer")
    void shouldNotAuthenticateWhenAuthorizationHeaderDoesNotStartWithBearer() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Basic dGVzdDp0ZXN0");
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
        
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extractUsername(anyString());
    }
    
    @Test
    @DisplayName("Should not authenticate when Bearer token is empty")
    void shouldNotAuthenticateWhenBearerTokenIsEmpty() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer ");
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
        
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extractUsername(anyString());
    }
    
    @Test
    @DisplayName("Should not authenticate when Bearer token is only whitespace")
    void shouldNotAuthenticateWhenBearerTokenIsOnlyWhitespace() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer    ");
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
        
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extractUsername(anyString());
    }
    
    @Test
    @DisplayName("Should handle ExpiredJwtException gracefully")
    void shouldHandleExpiredJwtExceptionGracefully() throws ServletException, IOException {
        // Given
        String token = "expired.jwt.token";
        String bearerToken = "Bearer " + token;
        
        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtUtil.extractUsername(token)).thenThrow(new ExpiredJwtException(null, null, "Token expired"));
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
        
        verify(filterChain).doFilter(request, response);
    }
    
    @Test
    @DisplayName("Should handle SignatureException gracefully")
    void shouldHandleSignatureExceptionGracefully() throws ServletException, IOException {
        // Given
        String token = "invalid.signature.token";
        String bearerToken = "Bearer " + token;
        
        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtUtil.extractUsername(token)).thenThrow(new SignatureException("Invalid signature"));
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
        
        verify(filterChain).doFilter(request, response);
    }
    
    @Test
    @DisplayName("Should handle MalformedJwtException gracefully")
    void shouldHandleMalformedJwtExceptionGracefully() throws ServletException, IOException {
        // Given
        String token = "malformed.token";
        String bearerToken = "Bearer " + token;
        
        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtUtil.extractUsername(token)).thenThrow(new MalformedJwtException("Malformed token"));
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
        
        verify(filterChain).doFilter(request, response);
    }
    
    @Test
    @DisplayName("Should handle validation exception gracefully")
    void shouldHandleValidationExceptionGracefully() throws ServletException, IOException {
        // Given
        String token = "valid.jwt.token";
        String bearerToken = "Bearer " + token;
        String username = "testuser";
        
        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(jwtUtil.validateToken(token, username)).thenThrow(new RuntimeException("Validation error"));
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
        
        verify(filterChain).doFilter(request, response);
    }
    
    @Test
    @DisplayName("Should not overwrite existing authentication")
    void shouldNotOverwriteExistingAuthentication() throws ServletException, IOException {
        // Given
        String token = "valid.jwt.token";
        String bearerToken = "Bearer " + token;
        String username = "testuser";
        
        // Set existing authentication
        org.springframework.security.authentication.UsernamePasswordAuthenticationToken existingAuth = 
            new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                "existinguser", null, java.util.Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(existingAuth);
        
        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth.getName()).isEqualTo("existinguser");
        
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).validateToken(anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should handle null username from token gracefully")
    void shouldHandleNullUsernameFromTokenGracefully() throws ServletException, IOException {
        // Given
        String token = "token.without.username";
        String bearerToken = "Bearer " + token;
        
        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtUtil.extractUsername(token)).thenReturn(null);
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
        
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).validateToken(anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should handle empty username from token gracefully")
    void shouldHandleEmptyUsernameFromTokenGracefully() throws ServletException, IOException {
        // Given
        String token = "token.with.empty.username";
        String bearerToken = "Bearer " + token;
        
        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtUtil.extractUsername(token)).thenReturn("");
        
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
        
        verify(filterChain).doFilter(request, response);
        // Empty username should still cause validation to be skipped
        verify(jwtUtil, never()).validateToken(anyString(), anyString());
    }
}