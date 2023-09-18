package com.thoughtworks.problem3application.infrastructure.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenCreator {

	@Value("${jwt.secret}")
	private String jwtSecret;

	public String generateToken(String input) {
		return Jwts.builder()
				.setSubject(input)
				.setIssuedAt(new Date())
				.signWith(SignatureAlgorithm.HS256, jwtSecret)
				.compact();
	}
}
