package tqs.sparkflow.stationservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * JWT utility class for token validation and extraction.
 * This service validates tokens issued by the user-service.
 */
@Component
@Profile({"!test", "securitytest"})
public class JwtUtil {

  @Value("${jwt.secret}")
  private String secret;

  /**
   * Extracts username from token.
   *
   * @param token the JWT token
   * @return the username
   */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Extracts expiration date from token.
   *
   * @param token the JWT token
   * @return the expiration date
   */
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * Extracts a specific claim from token.
   *
   * @param token the JWT token
   * @param claimsResolver function to resolve the claim
   * @param <T> the type of the claim
   * @return the claim value
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Extracts all claims from token.
   *
   * @param token the JWT token
   * @return all claims
   */
  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  /**
   * Checks if token is expired.
   *
   * @param token the JWT token
   * @return true if token is expired
   */
  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  /**
   * Validates a token against a username.
   *
   * @param token the JWT token
   * @param username the username to validate against
   * @return true if token is valid
   */
  public Boolean validateToken(String token, String username) {
    try {
      final String extractedUsername = extractUsername(token);
      return (extractedUsername.equals(username) && !isTokenExpired(token));
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Extracts email from token.
   *
   * @param token the JWT token
   * @return the email
   */
  public String extractEmail(String token) {
    return extractClaim(token, claims -> claims.get("email", String.class));
  }

  /**
   * Extracts operator status from token.
   *
   * @param token the JWT token
   * @return the operator status
   */
  public Boolean extractIsOperator(String token) {
    return extractClaim(token, claims -> claims.get("isOperator", Boolean.class));
  }

  /**
   * Gets the signing key for token validation.
   *
   * @return the signing key
   */
  private SecretKey getSigningKey() {
    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}