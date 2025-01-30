package com.example.demo.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entity.OTPToken;
import com.example.demo.entity.User;
import com.example.demo.repository.OTPTokenRepository;
import com.example.demo.repository.UserRepository;

@Service
public class ForgotPasswordServiceImplementation implements ForgotPasswordService{
	
	private final UserRepository userRepository;
	private final JavaMailSender mailSender;
	private final OTPTokenRepository tokenRepository;
	
	public ForgotPasswordServiceImplementation(UserRepository userRepository, JavaMailSender mailSender, OTPTokenRepository tokenRepository) {
		this.userRepository = userRepository;
		this.mailSender = mailSender;
		this.tokenRepository = tokenRepository;
	}

	@Override
	public Optional<?> findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public void sendOtp(User user) {
		String otp = String.format("%06d",  new Random().nextInt(999999));
		
		//Generate OTP
		OTPToken token = new OTPToken();
		token.setUser(user);
		token.setOtp(otp);
		token.setCreated_at(LocalDateTime.now());
		tokenRepository.save(token);
		
		// Send otp to mail
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.getEmail());
		message.setSubject("Your OTP for Password reset");
		message.setText("Use this otp to verify " + otp);
		mailSender.send(message);
		
	}

	@Override
	public Map<String, Object> verifyOtp(String otp) {
		OTPToken token = tokenRepository.findByOtp(otp);
		
		if (token == null) {
			throw new RuntimeException("Invalid OTP");
		} if (ChronoUnit.MINUTES.between(token.getCreated_at(), LocalDateTime.now())> 1) {
			tokenRepository.delete(token);
			throw new RuntimeException("OTP has expired");
		}
		tokenRepository.delete(token);
		Map<String, Object> response = new HashMap<>();
		response.put("userId", token.getUser().getId());
		response.put("message", "OTP verified successfully");
		return response;
	}
	
	@Override
	public User setNewPassword(Integer userId,String newPassword) {
		
		final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		Optional<?> user = userRepository.findById(userId);
		
		if (user.isPresent()) {
			String hashPassword = passwordEncoder.encode(newPassword);
			
			User updatedUser = (User) user.get();
			updatedUser.setPassword(hashPassword);
			
			// Save the password in the database
			userRepository.save(updatedUser);
			
			return updatedUser;

			
		} else {
			throw new RuntimeException("User not found");
		}
		
		
		
	}

}
