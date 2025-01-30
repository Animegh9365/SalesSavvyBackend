package com.example.demo.service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entity.JWTToken;
import com.example.demo.entity.User;
import com.example.demo.repository.JWTTokenRepository;
import com.example.demo.repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class AuthServiceImplementation implements AuthService{
	
	// Securely generated signing key
	private Key SIGNING_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
	
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordencoder;
	private final JWTTokenRepository jwtTokenRepository;
	
	
	public AuthServiceImplementation(UserRepository userRepository, JWTTokenRepository jwtTokenRepository, @Value("${jwt.secret}") String jwtSecret) {
		this.userRepository = userRepository;
		this.passwordencoder = new BCryptPasswordEncoder();
		this.jwtTokenRepository = jwtTokenRepository;
		
		// Ensure the key length is at least 64bytes
		if (jwtSecret.getBytes(StandardCharsets.UTF_8).length <64) {
			throw new IllegalArgumentException("JWT_SECRET in application.properties must be at least 64 bytes long for HS512.");
		}
		this.SIGNING_KEY = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
		
	}

	@Override
	public User authenticate(String username, String password) {

		User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Invalid username or password"));
		System.out.println(user.getUsername());
		if (!passwordencoder.matches(password, user.getPassword())) {
			System.out.println(user.getPassword());
			throw new RuntimeException("Invalid username or password");
		}
			
		return user;
	}
	
	@Override
	public String generateToken(User user) {
		String token;
		LocalDateTime now = LocalDateTime.now();
		JWTToken existingToken = jwtTokenRepository.findByUserId(user.getId());

		if (existingToken != null && now.isBefore(existingToken.getExpiresAt())) {
			token = existingToken.getToken();
		} else {
			token = generateNewToken(user);
			if (existingToken != null) {
				jwtTokenRepository.delete(existingToken);
			}
			saveToken(user, token);
		}
		return token;
	}

	private String generateNewToken(User user) {

		
		return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS512)
                .compact();
	}

	public void saveToken(User user, String token) {
		JWTToken jwtToken = new JWTToken(user,token,LocalDateTime.now().plusHours(1));
		jwtTokenRepository.save(jwtToken);
	}
	
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
			.setSigningKey(SIGNING_KEY)
			.build()
			.parseClaimsJws(token);
			
			// Check if the token exists in the database and is not expired
			Optional<JWTToken> jwtToken = jwtTokenRepository.findByToken(token);
			return jwtToken.isPresent() && jwtToken.get().getExpiresAt().isAfter(LocalDateTime.now());
		} catch (Exception e) {
			return false;
		}
	}
	
	public String extractUsername(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(SIGNING_KEY)
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}

	@Override
	public void logout(User user) {

		int id = user.getId();
		
		// Retrieve the JWT Token associated with the user
		JWTToken token = jwtTokenRepository.findByUserId(id);
		
		// If a token exists, delete it from the repository
		if (token != null) {
			jwtTokenRepository.deleteByUserId(id);
		}
	}
}
