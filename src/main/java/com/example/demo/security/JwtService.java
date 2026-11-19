package com.example.demo.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;

import javax.crypto.SecretKey;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private long expiration;

	private SecretKey getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	// Generate JWT Token
	public String generateToken(String email) {

		return Jwts.builder().subject(email).issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + expiration)).signWith(getSigningKey()).compact();
	}

	// Extract Email from Token
	public String extractEmail(String token) {

		return extractClaims(token).getSubject();
	}

	// Validate Token
	public boolean isTokenValid(String token, String email) {

		String tokenEmail = extractEmail(token);

		return tokenEmail.equals(email) && !isTokenExpired(token);
	}

	// Check Token Expiration
	private boolean isTokenExpired(String token) {

		return extractClaims(token).getExpiration().before(new Date());
	}

	// Extract Claims
	private Claims extractClaims(String token) {

		return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
	}
}