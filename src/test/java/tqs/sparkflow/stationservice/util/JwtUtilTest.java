package tqs.sparkflow.stationservice.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String secret = "test-secret-key-for-tests-that-should-be-at-least-32-characters-long";
    
    private String validToken;
    private String expiredToken;
    private String invalidToken;
    private String malformedToken;
    
    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Use reflection to set the secret field
        try {
            var field = JwtUtil.class.getDeclaredField("secret");
            field.setAccessible(true);
            field.set(jwtUtil, secret);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set secret field", e);
        }
        
        // Create test tokens
        createTestTokens();
    }
    
    private void createTestTokens() {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        
        // Valid token
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", "test@example.com");
        claims.put("isOperator", false);
        
        validToken = Jwts.builder()
            .setClaims(claims)
            .setSubject("testuser")
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
            
        // Expired token
        expiredToken = Jwts.builder()
            .setClaims(claims)
            .setSubject("testuser")
            .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 120)) // 2 hours ago
            .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60)) // 1 hour ago
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
            
        // Invalid token (wrong signature)
        SecretKey wrongKey = Keys.hmacShaKeyFor("wrong-secret-key-for-testing-purposes-32-chars".getBytes(StandardCharsets.UTF_8));
        invalidToken = Jwts.builder()
            .setClaims(claims)
            .setSubject("testuser")
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
            .signWith(wrongKey, SignatureAlgorithm.HS256)
            .compact();
            
        // Malformed token
        malformedToken = "invalid.jwt.token";
    }

    @Test
    @DisplayName("Should extract username from valid token")
    void shouldExtractUsernameFromValidToken() {
        String username = jwtUtil.extractUsername(validToken);
        assertThat(username).isEqualTo("testuser");
    }
    
    @Test
    @DisplayName("Should extract expiration date from valid token")
    void shouldExtractExpirationFromValidToken() {
        Date expiration = jwtUtil.extractExpiration(validToken);
        assertThat(expiration).isAfter(new Date());
    }
    
    @Test
    @DisplayName("Should extract email from valid token")
    void shouldExtractEmailFromValidToken() {
        String email = jwtUtil.extractEmail(validToken);
        assertThat(email).isEqualTo("test@example.com");
    }
    
    @Test
    @DisplayName("Should extract isOperator from valid token")
    void shouldExtractIsOperatorFromValidToken() {
        Boolean isOperator = jwtUtil.extractIsOperator(validToken);
        assertThat(isOperator).isFalse();
    }
    
    @Test
    @DisplayName("Should validate token successfully for correct username")
    void shouldValidateTokenSuccessfully() {
        Boolean isValid = jwtUtil.validateToken(validToken, "testuser");
        assertThat(isValid).isTrue();
    }
    
    @Test
    @DisplayName("Should fail validation for wrong username")
    void shouldFailValidationForWrongUsername() {
        Boolean isValid = jwtUtil.validateToken(validToken, "wronguser");
        assertThat(isValid).isFalse();
    }
    
    @Test
    @DisplayName("Should fail validation for expired token")
    void shouldFailValidationForExpiredToken() {
        Boolean isValid = jwtUtil.validateToken(expiredToken, "testuser");
        assertThat(isValid).isFalse();
    }
    
    @Test
    @DisplayName("Should fail validation for invalid signature")
    void shouldFailValidationForInvalidSignature() {
        Boolean isValid = jwtUtil.validateToken(invalidToken, "testuser");
        assertThat(isValid).isFalse();
    }
    
    @Test
    @DisplayName("Should fail validation for malformed token")
    void shouldFailValidationForMalformedToken() {
        Boolean isValid = jwtUtil.validateToken(malformedToken, "testuser");
        assertThat(isValid).isFalse();
    }
    
    @Test
    @DisplayName("Should handle null token gracefully")
    void shouldHandleNullTokenGracefully() {
        Boolean isValid = jwtUtil.validateToken(null, "testuser");
        assertThat(isValid).isFalse();
    }
    
    @Test
    @DisplayName("Should handle empty token gracefully")
    void shouldHandleEmptyTokenGracefully() {
        Boolean isValid = jwtUtil.validateToken("", "testuser");
        assertThat(isValid).isFalse();
    }
    
    @Test
    @DisplayName("Should throw exception when extracting username from expired token")
    void shouldThrowExceptionWhenExtractingUsernameFromExpiredToken() {
        assertThatThrownBy(() -> jwtUtil.extractUsername(expiredToken))
            .isInstanceOf(ExpiredJwtException.class);
    }
    
    @Test
    @DisplayName("Should throw exception when extracting username from invalid token")
    void shouldThrowExceptionWhenExtractingUsernameFromInvalidToken() {
        assertThatThrownBy(() -> jwtUtil.extractUsername(invalidToken))
            .isInstanceOf(SignatureException.class);
    }
    
    @Test
    @DisplayName("Should throw exception when extracting username from malformed token")
    void shouldThrowExceptionWhenExtractingUsernameFromMalformedToken() {
        assertThatThrownBy(() -> jwtUtil.extractUsername(malformedToken))
            .isInstanceOf(MalformedJwtException.class);
    }
    
    @Test
    @DisplayName("Should extract custom claims using extractClaim method")
    void shouldExtractCustomClaimsUsingExtractClaimMethod() {
        String email = jwtUtil.extractClaim(validToken, claims -> claims.get("email", String.class));
        Boolean isOperator = jwtUtil.extractClaim(validToken, claims -> claims.get("isOperator", Boolean.class));
        
        assertThat(email).isEqualTo("test@example.com");
        assertThat(isOperator).isFalse();
    }
    
    @Test
    @DisplayName("Should create token with operator privileges")
    void shouldCreateTokenWithOperatorPrivileges() {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", "admin@example.com");
        claims.put("isOperator", true);
        
        String operatorToken = Jwts.builder()
            .setClaims(claims)
            .setSubject("admin")
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
            
        String username = jwtUtil.extractUsername(operatorToken);
        String email = jwtUtil.extractEmail(operatorToken);
        Boolean isOperator = jwtUtil.extractIsOperator(operatorToken);
        Boolean isValid = jwtUtil.validateToken(operatorToken, "admin");
        
        assertThat(username).isEqualTo("admin");
        assertThat(email).isEqualTo("admin@example.com");
        assertThat(isOperator).isTrue();
        assertThat(isValid).isTrue();
    }
    
    @Test
    @DisplayName("Should handle token with missing claims gracefully")
    void shouldHandleTokenWithMissingClaimsGracefully() {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        
        // Token without email and isOperator claims
        String tokenWithoutClaims = Jwts.builder()
            .setSubject("testuser")
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
            
        String username = jwtUtil.extractUsername(tokenWithoutClaims);
        String email = jwtUtil.extractEmail(tokenWithoutClaims);
        Boolean isOperator = jwtUtil.extractIsOperator(tokenWithoutClaims);
        
        assertThat(username).isEqualTo("testuser");
        assertThat(email).isNull();
        assertThat(isOperator).isNull();
    }
}