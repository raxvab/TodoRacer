package com.auth.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
// Ensure the correct package path for the User class
import com.auth.demo.model.User; // Update this to the actual package path of the User class

/**
 * Helper class for generating and validating JWT tokens.
 */
@Component
public class JwtHelper {

    // Token validity duration (15 minutes)
    public static final long JWT_TOKEN_VALIDITY = 15 * 60;

    // Secret key for signing the JWT
    private final String secret = "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";

    // In-memory blacklist for invalidated tokens
    private final Set<String> tokenBlacklist = ConcurrentHashMap.newKeySet();

    // Retrieve username from JWT token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // Retrieve expiration date from JWT token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // Retrieve a specific claim from the token
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // For retrieving any information from token, we need the secret key
    private Claims getAllClaimsFromToken(String token) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    // Check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // Generate token for user
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities()); // Example: Add user roles to claims
        return doGenerateToken(claims, userDetails.getUsername());
    }

    // While creating the token -
    // 1. Define claims of the token, like Issuer, Expiration, Subject, and the ID
    // 2. Sign the JWT using the HS512 algorithm and secret key.
    // 3. Compact the JWT to a URL-safe string
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes()); // Generate a secure key using the secret
        return Jwts.builder()
                .setClaims(claims) // Add custom claims to the token
                .setSubject(subject) // Set the subject (e.g., username)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Set the token issuance time
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000)) // Set expiration time
                .signWith(key, SignatureAlgorithm.HS512) // Sign the token with the key and HS512 algorithm
                .compact(); // Compact the token into a URL-safe string
    }

    // Validate token
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return (username != null && userDetails != null
                    && username.equals(userDetails.getUsername())
                    && !isTokenExpired(token)
                    && !isTokenBlacklisted(token));
        } catch (Exception e) {
            return false; // Return false if token is invalid or parsing fails
        }
    }

    public boolean validateToken(String token, User user) {
        String username = getUsernameFromToken(token);
        return username.equals(user.getEmail()) && !isTokenExpired(token);
    }

    // Add token to blacklist
    public void blacklistToken(String token) {
        tokenBlacklist.add(token);
    }

    // Check if a token is blacklisted
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }
}