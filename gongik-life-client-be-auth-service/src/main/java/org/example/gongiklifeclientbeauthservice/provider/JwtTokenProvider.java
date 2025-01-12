package org.example.gongiklifeclientbeauthservice.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbeauthservice.dto.TokenDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtTokenProvider {

  @Value("${jwt.secret}")
  private String secretKey;

  @Value("${jwt.access-token-validity}")
  private long accessTokenValidityInMilliseconds;

  @Value("${jwt.refresh-token-validity}")
  private long refreshTokenValidityInMilliseconds;

  private Key key;

  @PostConstruct
  protected void init() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    this.key = Keys.hmacShaKeyFor(keyBytes);
  }

  public TokenDto generateAccessToken(String id) {
    return generateToken(id, accessTokenValidityInMilliseconds, true);

  }

  public String generateRefreshToken(String id) {
    return generateToken(id, refreshTokenValidityInMilliseconds, false).getRefreshToken();
  }

  private TokenDto generateToken(String id, long validityInMilliseconds, boolean isAccessToken) {
    Date now = new Date();
    Date validity = new Date(now.getTime() + validityInMilliseconds);

    String token = Jwts.builder()
        .subject(id)
        .issuedAt(now)
        .expiration(validity)
        .signWith(key)   // 알고리즘 명시적 지정
        .compact();

    if (isAccessToken) {
      return TokenDto.builder()
          .accessToken(token)
          .accessTokenExpiresAt(validity.toString())
          .build();
    } else {
      return TokenDto.builder()
          .refreshToken(token)
          .build();
    }
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser()
          .verifyWith((SecretKey) key)
          .build()
          .parseSignedClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public String getEmailFromToken(String token) {
    Claims claims = Jwts.parser()
        .verifyWith((SecretKey) key)
        .build()
        .parseSignedClaims(token)
        .getPayload();

    return claims.getSubject();
  }

  public String validateAccessTokenAndGetId(String accessToken) {
    try {
      Claims claims = Jwts.parser()
          .verifyWith((SecretKey) key)
          .build()
          .parseSignedClaims(accessToken)
          .getPayload();
      return claims.getSubject();
    } catch (Exception e) {
      log.info("Invalid Access Token ", e);
      throw e;
    }
  }

  public String validateRefreshTokenAndGetId(String refreshToken) {
    try {
      Claims claims = Jwts.parser()
          .verifyWith((SecretKey) key)
          .build()
          .parseSignedClaims(refreshToken)
          .getPayload();
      return claims.getSubject();
    } catch (Exception e) {
      log.info("Invalid Refresh Token ", e);
      throw e;
    }
  }

  public Long getAccessTokenExpiresIn() {
    return accessTokenValidityInMilliseconds;
  }
}
