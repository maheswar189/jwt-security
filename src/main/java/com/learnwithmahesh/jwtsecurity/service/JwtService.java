package com.learnwithmahesh.jwtsecurity.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.learnwithmahesh.jwtsecurity.dto.TokenPair;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Service
public class JwtService {

	private static Logger log = LoggerFactory.getLogger(JwtService.class);

	@Value("${app.jwt.secret}")
	private String jwtSecret;
	@Value("${app.jwt.expiration}")
	private long jwtExpirationMs;
	@Value("${app.jwt.refresh-expiration}")
	private long refreshExpirationMs;

	private static final String TOKEN_PREFIX = "Bearer ";

	// Generate access token
	public String generateAccesToken(Authentication authentication) {
		return generateToken(authentication, jwtExpirationMs, new HashMap<>());
	}

	// Generate refresh token
	public String generateRefreshToken(Authentication authentication) {

		Map<String, String> claims = new HashMap<>();
		claims.put("tokenType", "refresh");

		return generateToken(authentication, refreshExpirationMs, claims);
	}

	// Validate token
	public boolean isValidToken(String token, UserDetails userDetails) {
		// Extract the username from the token
		final String username = extractUsernameFromToken(token);
		if (!username.equals(userDetails.getUsername())) {
			return false;
		}
		try {
			Jwts.parser().verifyWith(getSigninKey()).build().parseSignedClaims(token);
			return true;
		} catch (SignatureException ex) {
			log.error("Invalid JWT Signature: {}::", ex.getMessage());
		} catch (MalformedJwtException ex) {
			log.error("Invalid JWT Signature: {}::", ex.getMessage());
		} catch (ExpiredJwtException ex) {
			log.error(" JWT token is expired: {}::", ex.getMessage());
		} catch (UnsupportedJwtException ex) {
			log.error("JWT token is unsupported: {}::", ex.getMessage());
		} catch (IllegalArgumentException ex) {
			log.error("JWT claims string is empty : {}::", ex.getMessage());
		}
		return false;
	}

	public  String extractUsernameFromToken(String token) {

		return Jwts.parser().verifyWith(getSigninKey()).build().parseSignedClaims(token).getPayload().getSubject();
	}

	// Validate if the token is refresh token
	public boolean isRefreshToken(String token) {
		Claims claims = Jwts.parser().verifyWith(getSigninKey()).build().parseSignedClaims(token).getPayload();
		return "refresh".equals(claims.get("tokenType"));
	}

	private SecretKey getSigninKey() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	private String generateToken(Authentication authentication, long expirationTime, Map<String, String> claims) {
		UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

		Date now = new Date(); // Time of token creation
		Date expiration = new Date(now.getTime() + expirationTime);

		return Jwts.builder().header().add("typ","JWT").and().subject(userPrincipal.getUsername()).claims(claims).issuedAt(now).expiration(expiration)
				.signWith(getSigninKey()).compact();
	}

	public TokenPair generateTokenPair(Authentication authentication) {

		String accessToken =generateAccesToken( authentication);
		String refreshToken =generateRefreshToken(authentication);
		return new TokenPair(accessToken,refreshToken);
		
	}

}
