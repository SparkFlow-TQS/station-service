package tqs.sparkflow.stationservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
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
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  private JwtUtil jwtUtil;

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
    if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
      jwtToken = requestTokenHeader.substring(7).trim();
      if (!jwtToken.isEmpty()) {
        try {
          username = jwtUtil.extractUsername(jwtToken);
        } catch (Exception e) {
          logger.warn("Unable to get JWT Token or JWT Token has expired");
        }
      }
    }

    // Once we get the token validate it.
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      try {
        // Validate token
        if (jwtUtil.validateToken(jwtToken, username)) {
          // Extract user information from token
          Boolean isOperator = jwtUtil.extractIsOperator(jwtToken);
          String email = jwtUtil.extractEmail(jwtToken);
          
          // Create authorities based on operator status
          SimpleGrantedAuthority authority = isOperator ? 
              new SimpleGrantedAuthority("ROLE_ADMIN") : 
              new SimpleGrantedAuthority("ROLE_USER");
          
          UsernamePasswordAuthenticationToken authToken = 
              new UsernamePasswordAuthenticationToken(
                  username, null, Collections.singletonList(authority));
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
          // Set authentication in security context
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      } catch (Exception e) {
        logger.warn("Unable to validate JWT token for username: " + username);
      }
    }
    chain.doFilter(request, response);
  }
}