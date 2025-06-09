package tqs.sparkflow.stationservice.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tqs.sparkflow.stationservice.util.JwtUtil;

/**
 * JWT authentication filter for processing JWT tokens in HTTP requests.
 * This filter intercepts incoming requests and validates JWT tokens in the Authorization header.
 */
@Component
@Profile({"!test", "securitytest"})
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String BEARER_PREFIX = "Bearer ";
  private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();
  
  private final JwtUtil jwtUtil;

  public JwtAuthenticationFilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  /**
   * Filters incoming HTTP requests to extract and validate JWT tokens.
   * 
   * @param request the HTTP servlet request
   * @param response the HTTP servlet response
   * @param chain the filter chain
   * @throws ServletException if a servlet-related error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                FilterChain chain) throws ServletException, IOException {
        
    final String requestTokenHeader = request.getHeader("Authorization");
    String jwtToken = extractJwtToken(requestTokenHeader);
    String username = extractUsernameFromToken(jwtToken);

    if (shouldAuthenticate(username)) {
      authenticateUser(jwtToken, username, request);
    }
    
    chain.doFilter(request, response);
  }
  
  private String extractJwtToken(String requestTokenHeader) {
    if (requestTokenHeader != null && requestTokenHeader.startsWith(BEARER_PREFIX)) {
      String token = requestTokenHeader.substring(BEARER_PREFIX_LENGTH).trim();
      return token.isEmpty() ? null : token;
    }
    return null;
  }
  
  private String extractUsernameFromToken(String jwtToken) {
    if (jwtToken == null) {
      return null;
    }
    try {
      return jwtUtil.extractUsername(jwtToken);
    } catch (JwtException | IllegalArgumentException e) {
      logger.warn("Unable to get JWT Token or JWT Token has expired");
      return null;
    }
  }
  
  private boolean shouldAuthenticate(String username) {
    return username != null && 
           !username.trim().isEmpty() && 
           SecurityContextHolder.getContext().getAuthentication() == null;
  }
  
  private void authenticateUser(String jwtToken, String username, HttpServletRequest request) {
    try {
      if (Boolean.TRUE.equals(jwtUtil.validateToken(jwtToken, username))) {
        Boolean isOperator = jwtUtil.extractIsOperator(jwtToken);
        
        SimpleGrantedAuthority authority = Boolean.TRUE.equals(isOperator) ? 
            new SimpleGrantedAuthority("ROLE_OPERATOR") : 
            new SimpleGrantedAuthority("ROLE_USER");
        
        UsernamePasswordAuthenticationToken authToken = 
            new UsernamePasswordAuthenticationToken(
                username, null, Collections.singletonList(authority));
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                  
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    } catch (JwtException | IllegalArgumentException e) {
      logger.warn("Unable to validate JWT token for username: " + username);
    }
  }
}