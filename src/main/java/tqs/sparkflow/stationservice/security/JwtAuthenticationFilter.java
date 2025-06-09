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

    String username = null;
    String jwtToken = null;

    // JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
    if (requestTokenHeader != null && requestTokenHeader.startsWith(BEARER_PREFIX)) {
      jwtToken = requestTokenHeader.substring(BEARER_PREFIX_LENGTH).trim();
      if (!jwtToken.isEmpty()) {
        try {
          username = jwtUtil.extractUsername(jwtToken);
        } catch (JwtException | IllegalArgumentException e) {
          logger.warn("Unable to get JWT Token or JWT Token has expired");
        }
      }
    }

    // Once we get the token validate it.
    if (username != null && !username.trim().isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
      try {
        // Validate token
        if (jwtUtil.validateToken(jwtToken, username)) {
          // Extract user information from token
          Boolean isOperator = jwtUtil.extractIsOperator(jwtToken);
          String email = jwtUtil.extractEmail(jwtToken);
          
          // Create authorities based on operator status
          SimpleGrantedAuthority authority = isOperator ? 
              new SimpleGrantedAuthority("ROLE_OPERATOR") : 
              new SimpleGrantedAuthority("ROLE_USER");
          
          UsernamePasswordAuthenticationToken authToken = 
              new UsernamePasswordAuthenticationToken(
                  username, null, Collections.singletonList(authority));
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
          // Set authentication in security context
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      } catch (JwtException | IllegalArgumentException e) {
        logger.warn("Unable to validate JWT token for username: " + username);
      }
    }
    chain.doFilter(request, response);
  }
}