package com.example.demo.service;

import com.example.demo.entity.User;

public interface AuthService {

	User authenticate(String username, String password);
	String generateToken(User user);
	public void saveToken(User user, String token);
	public boolean validateToken(String token);
	public String extractUsername(String token);
	public void logout(User user);
	
}
