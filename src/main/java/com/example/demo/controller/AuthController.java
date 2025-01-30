package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.LoginRequest;
import com.example.demo.entity.User;
import com.example.demo.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin(origins="http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;
	
	public AuthController(AuthService authService) {
		this.authService = authService;
	}
	
	@PostMapping("/login")
	@CrossOrigin(origins="http://localhost:5173")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
		try {
			// Authenticate user and get the role
			User user = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
			
			// Generate JWT Token
			String token = authService.generateToken(user);
			
			// Set token as HttpOnly Cookie
			Cookie cookie = new Cookie("authToken", token);
			cookie.setHttpOnly(true);
			cookie.setSecure(false);
			cookie.setPath("/");
			cookie.setMaxAge(3600);
			response.addCookie(cookie);
			
			
			// Return user role in response body
			Map<String, String> responseBody = new HashMap<>();
			responseBody.put("message", "Login successful");
			responseBody.put("role", user.getRole().name());
			responseBody.put("username", user.getUsername());
			
			return ResponseEntity.ok(responseBody);
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error",e.getMessage()));
		}
		
		
	}
	
	@PostMapping("/logout")
	public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
		try {
			User user=(User) request.getAttribute("authenticatedUser");
			System.out.println("User before logout: " + user);
            authService.logout(user);
            Cookie cookie = new Cookie("authToken", null);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "Logout successful");
            return ResponseEntity.ok(responseBody);
		} catch (RuntimeException e) {
			// Error response
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("message", "Logout failed");
			return ResponseEntity.status(500).body(errorResponse);
		}
	}
}
