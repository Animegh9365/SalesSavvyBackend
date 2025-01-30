package com.example.demo.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.OTPRequest;
import com.example.demo.dto.PasswordResetDTO;
import com.example.demo.entity.User;
import com.example.demo.service.ForgotPasswordService;

@RestController
@CrossOrigin(origins="http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/auth")
public class ForgotPasswordController {
	
	private final ForgotPasswordService passwordService;
	
	public ForgotPasswordController(ForgotPasswordService passwordService) {
		this.passwordService = passwordService;
	}
	
	@PostMapping("/forgotPassword")

	public ResponseEntity<?> forgotPassword(@RequestBody LoginRequest loginRequest) {
		Optional<?> optionalUser = passwordService.findUserByEmail(loginRequest.getEmail());
		
		if (optionalUser.isPresent()) {
			User user = (User) optionalUser.get();
			passwordService.sendOtp(user);
			return ResponseEntity.ok(Map.of("message","OTP has been sent to your email"));
		}
		return ResponseEntity.status(404).body(Map.of("message","Email not registered"));
	}
	
	@PostMapping("/verifyOtp")
	public ResponseEntity<?> verifyOtp(@RequestBody OTPRequest otpRequest) {
		
		try {
			Map<String,Object> response= passwordService.verifyOtp(otpRequest.getOtp());
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(400).body(Map.of("message",e.getMessage()));
		}
		
	}
	
	@PostMapping("/setNewPassword")
	public ResponseEntity<?> newPassword(@RequestBody PasswordResetDTO request) {
		try {
			Integer userId =  Integer.valueOf(request.getUserId());

			passwordService.setNewPassword(userId, request.getPassword());
			return ResponseEntity.ok(Map.of("message","Password updated successfully"));
		} catch (Exception e) {
			return ResponseEntity.status(400).body(Map.of("message",e.getMessage()));
		}
	}

	
}
