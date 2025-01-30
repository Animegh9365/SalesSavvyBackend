package com.example.demo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;

@RestController
@CrossOrigin(origins="http://localhost:5173")
@RequestMapping("/api/users")
public class RegisterController {

	final UserService userService;
	
	public RegisterController(UserService userService) {
		this.userService = userService;
	}
	
	
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody User user) {
		try {
			User registeredUser = userService.registerUser(user);
			return ResponseEntity.ok(Map.of("message", "User registered successfully","user", registeredUser));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(Map.of("error",e.getMessage()));
		}
	
		
	}
}
